/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.FILTH2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.COPPER3.FilterTypeMapping;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.CpsRepository2.SISpec.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint;
import Composestar.Core.CpsRepository2Impl.SIInfo.ImposedFilterModuleImpl;
import Composestar.Core.CpsRepository2Impl.SISpec.FilterModuleBindingImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.Constraint;
import Composestar.Core.FILTH2.Model.ConstraintFactory;
import Composestar.Core.FILTH2.Model.PhantomAction;
import Composestar.Core.FILTH2.Model.StructuralConstraint;
import Composestar.Core.FILTH2.Model.ConstraintFactory.ConstraintCreationException;
import Composestar.Core.FILTH2.Ordering.OrderGenerator;
import Composestar.Core.FILTH2.Validation.StructuralValidation;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * SANE produces information about where multiple filtermodules are imposed on
 * the same point. It does not however say anything about the order in which the
 * filtermodules should be applied. The possible orderings are constrained by
 * the filter ordering specification.
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.FILTH, dependsOn = { ModuleNames.LOLA })
public class FILTH implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FILTH);

	/**
	 * The default inner dispatch filter module. Will be appended to all
	 * generated order
	 */
	protected FilterModule defaultInnerDispatch;

	protected FilterModuleBinding defaultFMBinding;

	protected CPSTimer timer;

	/**
	 * The maximum number of orders to return. If set to a value below 1 all
	 * orders will be returned, but this can result in a out of memory access.
	 */
	@ModuleSetting(ID = "max", name = "Maximum Orders", isAdvanced = true)
	protected int maxOrders = 4;

	/**
	 * Mapping from action name to action. Used to find existing actions.
	 */
	protected Map<String, Action> actions;

	/**
	 * Mapping from constraint to its definition, used for error reporting on
	 * the constraints.
	 */
	protected Map<Constraint, FilterModuleConstraint> constraints;

	protected Repository repository;

	public FILTH()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		timer = CPSTimer.getTimer(ModuleNames.FILTH);
		repository = resources.repository();
		FilterTypeMapping filterTypes = resources.get(FilterTypeMapping.RESOURCE_KEY);
		defaultInnerDispatch = InnerDispatcher.createInnerDispatcher(repository, filterTypes);
		defaultFMBinding = new FilterModuleBindingImpl();
		defaultFMBinding.setFilterModuleReference(defaultInnerDispatch);

		timer.start("Creating actions");
		actions = new HashMap<String, Action>();
		for (FilterModule fm : repository.getAll(FilterModule.class))
		{
			actions.put(fm.getFullyQualifiedName(), new Action(fm.getFullyQualifiedName()));
		}
		timer.stop();
		timer.start("Loading constraint specification");
		constraints = new HashMap<Constraint, FilterModuleConstraint>();
		loadConstraintSpecification();
		timer.stop();

		boolean allOK = true;
		for (Superimposed si : repository.getAllSet(Superimposed.class))
		{
			if (!(si.getOwner() instanceof Concern))
			{
				continue;
			}
			Concern c = (Concern) si.getOwner();
			timer.start(c.getFullyQualifiedName());
			allOK &= generateFilterModuleOrder(c, si);
			timer.stop();
		}
		if (!allOK)
		{
			StructuralValidation.isValid(constraints);
			// TODO: perform order validation (i.e. Pre(a,b); Pre(b,a);)
			throw new ModuleException(String.format("One or more concerns did not have a valid filter module order."),
					ModuleNames.FILTH);
		}
		return ModuleReturnValue.OK;
	}

	/**
	 * Create filter module orderings for the given concern
	 * 
	 * @param concern
	 * @param si
	 * @return true when a valid filter module order was selected
	 */
	protected boolean generateFilterModuleOrder(Concern concern, Superimposed sinfo)
	{
		Map<Action, ImposedFilterModule> concernActions = new HashMap<Action, ImposedFilterModule>();
		// Add filter modules as actions
		for (ImposedFilterModule fms : sinfo.getFilterModules())
		{
			Action act = actions.get(fms.getFilterModule().getFullyQualifiedName());
			if (act != null)
			{
				concernActions.put(act, fms);
			}
			else
			{
				logger.error(String.format("Unknown filter module %s in the order for %s", fms.getFilterModule()
						.getFullyQualifiedName(), concern.getFullyQualifiedName()));
				return false;
			}
		}

		List<List<ImposedFilterModule>> fmorders = new LinkedList<List<ImposedFilterModule>>();

		timer.start("Creating orders for %s", concern.getFullyQualifiedName());
		Set<List<Action>> orders = OrderGenerator.generate(concernActions.keySet(), maxOrders);
		timer.stop();

		ImposedFilterModule difm = new ImposedFilterModuleImpl(defaultFMBinding);
		sinfo.addFilterModule(difm);
		repository.add(difm);

		// convert the ordered action lists to filter module ordering lists and
		// validate the generated list to conform to the constraints
		timer.start("Convert orders for %s", concern.getFullyQualifiedName());
		for (List<Action> order : orders)
		{
			List<ImposedFilterModule> fmorder = new ArrayList<ImposedFilterModule>();
			boolean isValidOrder = true;
			for (Action action : order)
			{
				// Check if structural constraints are met
				for (Constraint constraint : action.getConstraints(StructuralConstraint.class))
				{
					if (!constraint.isValidOrder(order, null))
					{
						FilterModuleConstraint def = constraints.get(constraint);
						logger.warn(String.format("Constraint %s is not met in this selected order: %s",
								def.toString(), order), def);
						isValidOrder = false;
						break;
					}
				}
				if (!isValidOrder)
				{
					break;
				}
				ImposedFilterModule fms = concernActions.get(action);
				if (fms != null)
				{
					fmorder.add(fms);
				}
				else
				{
					logger.error(String.format("Unknown action %s in the order for %s", action.getName(), concern
							.getFullyQualifiedName()));
					return false;
				}
			}

			if (isValidOrder)
			{
				// add the default dispatch filter
				fmorder.add(difm);
				fmorders.add(fmorder);
				sinfo.addFilterModuleOrder(fmorder);
			}
		}
		timer.stop();

		// add the filter module orders
		if (fmorders.size() > 0)
		{
			if (fmorders.size() > 1)
			{
				logger.warn(String.format("Multiple Filter Module orderings possible for concern %s", concern
						.getFullyQualifiedName()), concern);
			}

			List<ImposedFilterModule> selorder = sinfo.getFilterModuleOrder();
			if (selorder.size() > 2) // because of the default dispatch
			{
				logger.info("Encountered shared join point: " + concern.getFullyQualifiedName(), concern);

				StringBuffer sb = new StringBuffer();
				for (ImposedFilterModule fms : selorder)
				{
					if (sb.length() > 0)
					{
						sb.append(" --> ");
					}
					sb.append(fms.getFilterModule().getFullyQualifiedName());
				}
				logger.debug("Selecting filter module order: " + sb.toString());
			}
		}
		else
		{
			logger
					.error(String.format("No valid filter module order for %s", concern.getFullyQualifiedName()),
							concern);
			return false;
		}
		return true;
	}

	/**
	 * Add the constraint specifications to the action
	 * 
	 * @param actions
	 */
	protected void loadConstraintSpecification()
	{
		for (FilterModuleConstraint fmc : repository.getAll(FilterModuleConstraint.class))
		{
			List<ConstraintValue> args = fmc.getArguments();
			List<Action> acts = new ArrayList<Action>();
			for (ConstraintValue element : args)
			{
				Action act = actions.get(element.getStringValue());
				if (act == null)
				{
					act = new PhantomAction(element.getStringValue());
					actions.put(element.getStringValue(), act);
				}
				acts.add(act);
			}
			try
			{
				constraints.put(ConstraintFactory.createConstraint(fmc.getConstraintType(), acts), fmc);
			}
			catch (ConstraintCreationException e)
			{
				logger.error(e.getMessage(), fmc);
			}
		}
	}
}
