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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FMParams.Parameterized;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.Reference;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.ReferenceUsage;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * Resolve the soft references. This module can be executed multiple times
 * without negative side effects.
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.REXREF, dependsOn = { ModuleNames.COPPER, ModuleNames.COLLECTOR })
public class RexRef implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.REXREF);

	/**
	 * Resource key that stores the number of refrex passes tha have been
	 * executed
	 */
	protected static final String PASSES_KEY = ModuleNames.REXREF + ".passes";

	/**
	 * The reference manager
	 */
	protected ReferenceManager refman;

	/**
	 * Needed to resolve filter module references
	 */
	protected Repository repository;

	/**
	 * Needed for type references
	 */
	protected UnitRegister register;

	/**
	 * Number of times this module has been executed
	 */
	protected int passes;

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
		Integer ipass = resources.get(PASSES_KEY);
		if (ipass == null)
		{
			passes = 1;
		}
		else
		{
			passes = ipass + 1;
		}
		resources.put(PASSES_KEY, passes);
		refman = resources.get(ReferenceManager.RESOURCE_KEY);
		if (refman == null)
		{
			// no reference manager means nothing has to be resolved
			return ModuleReturnValue.Ok;
		}
		int cnt = 0;
		repository = resources.repository();
		register = resources.get(UnitRegister.RESOURCE_KEY);
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.REXREF);
		timer.start("Resolving type references (pass #" + passes + ")");
		for (TypeReference ref : refman.getTypeReferences())
		{
			if (doResolve(ref))
			{
				resolveType(ref);
				++cnt;
			}
		}
		timer.stop();
		logger.debug(String.format("Resolved %d type references", cnt));
		cnt = 0;
		timer.start("Resolving filter module references (pass #" + passes + ")");
		for (FilterModuleReference ref : refman.getFilterModuleReferences())
		{
			if (doResolve(ref))
			{
				resolveFilterModule(ref);
			}
		}
		timer.stop();
		logger.debug(String.format("Resolved %d filter module references", cnt));
		cnt = 0;
		timer.start("Resolving method references (pass #" + passes + ")");
		for (MethodReference ref : refman.getMethodReferences())
		{
			if (doResolve(ref))
			{
				resolveMethodReference(ref);
				++cnt;
			}
		}
		timer.stop();
		logger.debug(String.format("Resolved %d method references", cnt));
		cnt = 0;
		timer.start("Resolving instance method references (pass #" + passes + ")");
		for (InstanceMethodReference ref : refman.getInstanceMethodReferences())
		{
			if (doResolve(ref))
			{
				resolveInstanceMethodReference(ref);
				++cnt;
			}
		}
		timer.stop();
		logger.debug(String.format("Resolved %d instance method references", cnt));
		return ModuleReturnValue.Ok;
	}

	/**
	 * @param ref
	 * @return True when this reference needs to be resolved
	 */
	protected boolean doResolve(Reference<?> ref)
	{
		// self references and parameterized references are usually not in the
		// refman's list, but just in case
		return !ref.isResolved() && !ref.isSelfReference() && !(ref instanceof Parameterized);
	}

	/**
	 * Produce missing reference notices. Checks the usage table to produce
	 * either warnings or errors.
	 * 
	 * @param ref
	 * @param refName
	 * @param moreInfo
	 */
	protected void notifyMissingRef(Reference<?> ref, String refName, String moreInfo)
	{
		String msg;
		if (moreInfo == null || moreInfo.length() == 0)
		{
			msg = String.format("Unresolved %s reference: '%s'", refName, ref.getReferenceId());
		}
		else
		{
			msg = String.format("Unresolved %s reference: '%s'. %s", refName, ref.getReferenceId(), moreInfo);
		}

		Collection<ReferenceUsage> usage = refman.getReferenceUsage(ref);
		for (ReferenceUsage use : usage)
		{
			if (use.getUser() == null)
			{
				continue;
			}
			Level loglev = Level.WARN;
			if (use.isRequired())
			{
				loglev = Level.ERROR;
			}
			logger.log(loglev, msg, use.getUser());
		}
		if (usage.size() == 0)
		{
			logger.warn(msg);
		}
	}

	/**
	 * @param ref
	 * @see #resolveMethodReference(MethodReference)
	 */
	protected void resolveInstanceMethodReference(InstanceMethodReference ref)
	{
		// reuse the resolving from resolveMethodReference
		resolveMethodReference(ref);
	}

	/**
	 * Resolve method references
	 * 
	 * @param ref
	 */
	protected void resolveMethodReference(MethodReference ref)
	{
		Type type = ref.getTypeReference().getReference();
		if (type == null)
		{
			ref.setReference(null);
			notifyMissingRef(ref, "method", String
					.format("Missing type: '%s'", ref.getTypeReference().getReferenceId()));
			return;
		}
		for (MethodInfo mi : (List<MethodInfo>) type.getMethods())
		{
			if (ref.getReferenceId().equals(mi.getName()))
			{
				ref.setReference(mi);
				return;
			}
		}
		ref.setReference(null);
		notifyMissingRef(ref, "method", String.format("In type: '%s'", ref.getTypeReference().getReferenceId()));
		return;
	}

	/**
	 * Resolve filter module references
	 * 
	 * @param ref
	 */
	protected void resolveFilterModule(FilterModuleReference ref)
	{
		FilterModule fm = repository.get(ref.getReferenceId(), FilterModule.class);
		ref.setReference(fm);
		if (fm == null)
		{
			notifyMissingRef(ref, "filter module", null);
		}
	}

	/**
	 * Resolve type references
	 * 
	 * @param ref
	 */
	protected void resolveType(TypeReference ref)
	{
		Type type = register.getType(ref.getReferenceId());
		ref.setReference(type);
		if (type == null)
		{
			notifyMissingRef(ref, "type", null);
		}
	}
}
