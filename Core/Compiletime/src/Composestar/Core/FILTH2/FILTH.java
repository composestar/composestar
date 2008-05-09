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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.COPPER2.FilterTypeMapping;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.Constraint;
import Composestar.Core.FILTH2.Model.ConstraintFactory;
import Composestar.Core.FILTH2.Model.FilterModuleAction;
import Composestar.Core.FILTH2.Model.OrderingConstraint;
import Composestar.Core.FILTH2.Model.PhantomAction;
import Composestar.Core.FILTH2.Model.ConstraintFactory.ConstraintCreationException;
import Composestar.Core.FILTH2.Ordering.OrderGenerator;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * Filter Composition &amp; Checking
 * 
 * @author Michiel Hendriks
 */
public class FILTH implements CTCommonModule
{
	public static final String MODULE_NAME = "FILTH";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * The default inner dispatch filter module. Will be appended to all
	 * generated order
	 */
	protected FilterModuleReference defaultInnerDispatch;

	/**
	 * Ordering specification
	 * 
	 * @Deprecated
	 */
	@Deprecated
	protected Map<String, SyntacticOrderingConstraint> orderSpec;

	/**
	 * The constraint specification, created by COPPER
	 */
	protected ConstraintSpecification constraintSpec;

	protected CPSTimer timer;

	public FILTH()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		timer = CPSTimer.getTimer(MODULE_NAME);
		FilterTypeMapping filterTypes = resources.get(FilterTypeMapping.RESOURCE_KEY);
		defaultInnerDispatch = InnerDispatcher.createInnerDispatchReference(resources.repository(), filterTypes);
		orderSpec = resources.get(SyntacticOrderingConstraint.FILTER_ORDERING_SPEC);
		constraintSpec = resources.get(ConstraintSpecification.RESOURCE_KEY);

		boolean allOK = true;
		Iterator<Concern> conIter = resources.repository().getAllInstancesOf(Concern.class);
		while (conIter.hasNext())
		{
			Concern c = conIter.next();
			SIinfo si = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
			if (si != null)
			{
				timer.start(c.getQualifiedName());
				allOK &= generateFilterModuleOrder(c, si);
				timer.stop();
			}
		}
		if (!allOK)
		{
			throw new ModuleException(String.format("One or more concerns did not have a valid filter module order."),
					MODULE_NAME);
		}
	}

	/**
	 * Create filter module orderings for the given concern
	 * 
	 * @param concern
	 * @param si
	 * @return true when a valid filter module order was selected
	 */
	protected boolean generateFilterModuleOrder(Concern concern, SIinfo sinfo)
	{
		Map<String, Action> actions = new HashMap<String, Action>();

		// Add filter modules as actions
		List<FilterModSIinfo> msalts = sinfo.getFilterModSIAlts();
		FilterModSIinfo fmsi = msalts.get(0);
		for (FilterModuleSuperImposition fms : (List<FilterModuleSuperImposition>) fmsi.getAll())
		{
			Action action = new FilterModuleAction(fms);
			actions.put(action.getName(), action);
		}

		// Add constraints
		// addOrderingConstraints(actions);
		if (constraintSpec != null)
		{
			loadConstraintSpecification(actions);
		}

		List<List<FilterModuleSuperImposition>> fmorders = new LinkedList<List<FilterModuleSuperImposition>>();

		timer.start("Creating orders for %s", concern.getQualifiedName());
		Set<List<Action>> orders = OrderGenerator.generate(actions.values());
		timer.stop();
		// convert the ordered action lists to filter module ordering lists and
		// validate the generated list to conform to the constraints
		for (List<Action> order : orders)
		{
			List<FilterModuleSuperImposition> fmorder = new ArrayList<FilterModuleSuperImposition>();
			boolean isValidOrder = true;
			for (Action action : order)
			{
				// check if the order is valid
				for (Constraint constraint : action.getConstraints())
				{
					if (!constraint.isValidOrder(order))
					{
						isValidOrder = false;
						break;
					}
				}
				if (!isValidOrder)
				{
					break;
				}
				if (action instanceof FilterModuleAction)
				{
					fmorder.add(((FilterModuleAction) action).getFMSI());
				}
			}

			if (isValidOrder)
			{
				// add the default dispatch filter
				fmorder.add(new FilterModuleSuperImposition(defaultInnerDispatch));
				fmorders.add(fmorder);
			}
		}

		// add the filter module orders
		concern.addDynObject(FilterModuleOrder.ALL_ORDERS_KEY, fmorders);
		if (fmorders.size() > 0)
		{
			concern.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, new FilterModuleOrder(fmorders.get(0)));

			if (fmorders.size() > 1)
			{
				logger.warn(String.format("Multiple (%d) Filter Module orderings possible for concern %s", fmorders
						.size(), concern.getQualifiedName()), concern);
			}

			if (fmorders.get(0).size() > 2) // because of the default dispatch
			{
				logger.info("Encountered shared join point: " + concern.getQualifiedName(), concern);

				StringBuffer sb = new StringBuffer();
				for (FilterModuleSuperImposition fms : fmorders.get(0))
				{
					if (sb.length() > 0)
					{
						sb.append(" --> ");
					}
					sb.append(fms.getFilterModule().getRef().getOriginalQualifiedName());
				}
				logger.debug("Selecting filter module order: " + sb.toString());
			}
		}
		else
		{
			logger.error(String.format("No valid filter module order for %s", concern.getQualifiedName()), concern);
			return false;
		}
		return true;
	}

	/**
	 * Add the ordering constraints from the "old" specification
	 * 
	 * @param actions
	 * @deprecated
	 */
	@Deprecated
	protected void addOrderingConstraints(Map<String, Action> actions)
	{
		if (orderSpec == null)
		{
			return;
		}
		for (SyntacticOrderingConstraint soc : orderSpec.values())
		{
			Action lhs = actions.get(soc.getLeft());
			if (lhs == null)
			{
				continue;
			}
			for (String right : soc.getRightFilterModulesEx())
			{
				Action rhs = actions.get(right);
				if (rhs == null)
				{
					continue;
				}
				new OrderingConstraint(lhs, rhs);
			}
		}
	}

	/**
	 * Add the constraint specifications to the action
	 * 
	 * @param actions
	 */
	protected void loadConstraintSpecification(Map<String, Action> actions)
	{
		for (ConstraintSpecification.ConstraintDefinition def : constraintSpec.getDefinitions())
		{
			String[] args = def.getArguments();
			Action[] acts = new Action[args.length];
			for (int i = 0; i < args.length; i++)
			{
				acts[i] = actions.get(args[i]);
				if (acts[i] == null)
				{
					acts[i] = new PhantomAction(args[i]);
				}
			}
			try
			{
				ConstraintFactory.createConstraint(def.getType(), acts);
			}
			catch (ConstraintCreationException e)
			{
				logger.error(e.getMessage(), def);
			}
		}
	}
}
