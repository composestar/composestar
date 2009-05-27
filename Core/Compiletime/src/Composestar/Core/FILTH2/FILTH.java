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

import java.util.List;
import java.util.Set;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.COPPER3.FilterTypeMapping;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2Impl.SIInfo.ImposedFilterModuleImpl;
import Composestar.Core.CpsRepository2Impl.SISpec.FilterModuleBindingImpl;
import Composestar.Core.Exception.ModuleException;
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

	protected Set<Constraint> constraints;

	protected Repository repository;

	public FILTH()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.FILTH;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.LOLA };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
	}

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

		constraints = repository.getAllSet(Constraint.class);

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
		timer.start("Creating orders for %s", concern.getFullyQualifiedName());
		Set<List<ImposedFilterModule>> orders =
				OrderGenerator.generate(sinfo.getFilterModules(), constraints, maxOrders);
		timer.stop();

		ImposedFilterModule difm = new ImposedFilterModuleImpl(defaultFMBinding);
		sinfo.addFilterModule(difm);
		repository.add(difm);

		// convert the ordered action lists to filter module ordering lists and
		// validate the generated list to conform to the constraints
		timer.start("Convert orders for %s", concern.getFullyQualifiedName());
		for (List<ImposedFilterModule> order : orders)
		{
			boolean isValidOrder = true;
			if (isValidOrder)
			{
				// add the default dispatch filter
				order.add(difm);
				sinfo.addFilterModuleOrder(order);
			}
		}
		timer.stop();

		// add the filter module orders
		if (orders.size() > 0)
		{
			if (orders.size() > 1)
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
}
