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

package Composestar.Core.DEPART;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * Say goodbye to filter module parameters
 * 
 * @author Michiel Hendriks
 */
public class Departure implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DEPART);

	protected Repository repository;

	protected ReferenceManager refman;

	public Departure()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.DEPART;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		/*
		 * This module does not really depend on FILTH, it is just to make sure
		 * it is executed after FILTH, because FILTH requires the original
		 * filter modules.
		 */
		return new String[] { ModuleNames.LOLA, ModuleNames.FILTH };
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
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.DEPART);
		repository = resources.repository();
		refman = resources.get(ReferenceManager.RESOURCE_KEY);
		for (ImposedFilterModule ifm : repository.getAllSet(ImposedFilterModule.class))
		{
			FilterModule fm = ifm.getFilterModule();
			if (fm.hasParameters())
			{
				timer.start("Resolve FMPs for: %s", fm.getFullyQualifiedName());
				fm = resolveFilterModuleParameters(ifm.getImposedBy());
				timer.stop();
				if (fm != null)
				{
					ifm.setFilterModule(fm);
				}
			}
		}
		return ModuleReturnValue.OK;
	}

	protected FilterModule resolveFilterModuleParameters(FilterModuleBinding fmb)
	{
		FilterModule fm = fmb.getFilterModuleReference().getReference();
		logger.info(String.format("Resolving filter module paramaters for %s", fm.getFullyQualifiedName()), fm);
		List<FMParameterValue> values = fmb.getParameterValues();
		Map<String, FMParameterValue> context = new HashMap<String, FMParameterValue>();
		if (values.size() < fm.getParameters().size())
		{
			logger.error(String.format("Filter module '%s' requires %d parameters, only received %d", fm
					.getFullyQualifiedName(), fm.getParameters().size(), values.size()), fmb);
			return null;
		}
		else if (values.size() > fm.getParameters().size())
		{
			logger.warn(String.format("Filter module '%s' only requires %d parameters, received %d", fm
					.getFullyQualifiedName(), fm.getParameters().size(), values.size()), fmb);
		}
		int idx = 0;
		for (FMParameter par : fm.getParameters())
		{
			FMParameterValue val = values.get(idx);
			int valCnt = val.getValues().size();
			if (!par.isParameterList())
			{
				if (valCnt == 0)
				{
					logger.error(String.format("Parameter %s requires a value", par.getFullyQualifiedName()), val);
				}
				else if (valCnt > 1)
				{
					logger.warn(String.format("Only the first value will be used by parameter %s", par
							.getFullyQualifiedName()), val);
				}
			}
			context.put(par.getFullyQualifiedName(), val);
			++idx;
		}

		try
		{
			// TODO how to handle filter module references in the filter module
			// as filter type?

			RepositoryEntity realOwner = fm.getOwner();
			FakeOwner fakeOwner;
			if (realOwner instanceof QualifiedRepositoryEntity)
			{
				fakeOwner = new FakeOwner(((QualifiedRepositoryEntity) realOwner).getFullyQualifiedName());
			}
			else
			{
				fakeOwner = new FakeOwner("<invalid>");
			}
			fm.setOwner(fakeOwner);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ObjectOutputStream oos = new UnreferencedOOS(os);
			oos.writeObject(fm);
			fm.setOwner(realOwner);
			String newName = fm.getName() + "`" + fmb.getSelector().getFullyQualifiedName().hashCode();

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			ParameterResolver pm = new ParameterResolver(is, context, refman, newName);
			fm = (FilterModule) pm.readObject();
			fm.setOwner(realOwner);

			List<RepositoryEntity> newEnt = pm.getRepositoryEntities();
			Iterator<RepositoryEntity> it = newEnt.iterator();
			while (it.hasNext())
			{
				if (it.next().equals(fakeOwner))
				{
					it.remove();
				}
			}
			repository.addAll(newEnt);
			return fm;
		}
		catch (Exception e)
		{
			logger.error(e);
			return null;
		}
	}

}
