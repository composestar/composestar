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

package Composestar.Core.REXREF2;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * Resolve the soft references
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.REXREF, dependsOn = { ModuleNames.COPPER, ModuleNames.COLLECTOR })
public class RexRef implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.REXREF);

	/**
	 * The reference manager
	 */
	protected ReferenceManager refman;

	protected Repository repository;

	protected UnitRegister register;

	public RexRef()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		refman = resources.get(ReferenceManager.RESOURCE_KEY);
		if (refman == null)
		{
			// no reference manager means nothing has to be resolved
			return ModuleReturnValue.Ok;
		}
		repository = resources.repository();
		register = resources.get(UnitRegister.RESOURCE_KEY);
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.REXREF);
		timer.start("Resolving type references");
		for (TypeReference ref : refman.getTypeReferences())
		{
			if (!ref.isResolved() && !ref.isSelfReference())
			{
				resolveType(ref);
			}
		}
		timer.stop();
		timer.start("Resolving filter module references");
		for (FilterModuleReference ref : refman.getFilterModuleReferences())
		{
			if (!ref.isResolved() && !ref.isSelfReference())
			{
				resolveFilterModule(ref);
			}
		}
		timer.stop();
		timer.start("Resolving method references");
		for (MethodReference ref : refman.getMethodReferences())
		{
			if (!ref.isResolved() && !ref.isSelfReference())
			{
				resolveMethodReference(ref);
			}
		}
		timer.stop();
		timer.start("Resolving instance method references");
		for (InstanceMethodReference ref : refman.getInstanceMethodReferences())
		{
			if (!ref.isResolved() && !ref.isSelfReference())
			{
				resolveInstanceMethodReference(ref);
			}
		}
		timer.stop();
		return ModuleReturnValue.Ok;
	}

	protected void resolveInstanceMethodReference(InstanceMethodReference ref)
	{
	// TODO Auto-generated method stub

	}

	protected void resolveMethodReference(MethodReference ref)
	{
	// TODO Auto-generated method stub

	}

	protected void resolveFilterModule(FilterModuleReference ref)
	{
		FilterModule fm = repository.get(ref.getReferenceId(), FilterModule.class);
		ref.setReference(fm);
		if (fm == null)
		{
			// TODO notify
			logger.warn(String.format("Unresolved reference [%s]: %s", ref.getClass().getName(), ref.getReferenceId()));
		}
	}

	protected void resolveType(TypeReference ref)
	{
		Type type = register.getType(ref.getReferenceId());
		ref.setReference(type);
		if (type == null)
		{
			// TODO notify
			logger.warn(String.format("Unresolved reference [%s]: %s", ref.getClass().getName(), ref.getReferenceId()));
		}
	}
}
