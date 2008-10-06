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

package Composestar.Core.SANE2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2Impl.SIInfo.ImposedFilterModuleImpl;
import Composestar.Core.CpsRepository2Impl.SIInfo.SuperimposedImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Performs the superimposition of the filter modules and resolves the
 * parameters in the filter modules.
 * 
 * @author Michiel Hendriks
 */
@ComposestarModule(ID = ModuleNames.SANE, dependsOn = { ModuleNames.COPPER, ModuleNames.LOLA })
public class Imposer implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SANE);

	protected Repository repository;

	protected ReferenceManager refman;

	public Imposer()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		boolean res = true;
		repository = resources.repository();
		refman = resources.get(ReferenceManager.RESOURCE_KEY);
		for (FilterModuleBinding fmb : repository.getAllSet(FilterModuleBinding.class))
		{
			res &= superImpose(fmb);
		}
		if (!res)
		{
			return ModuleReturnValue.Error;
		}
		return ModuleReturnValue.Ok;
	}

	protected boolean superImpose(FilterModuleBinding fmb)
	{
		FilterModule fm = fmb.getFilterModuleReference().getReference();
		if (fm.hasParameters())
		{
			fm = resolveFilterModuleParameters(fmb);
			if (fm == null)
			{
				return false;
			}
		}
		int cnt = 0;
		for (ProgramElement pr : fmb.getSelector().getSelection())
		{
			if (pr instanceof Type)
			{
				Type tp = (Type) pr;
				Concern concern = repository.get(tp.getFullName(), Concern.class);
				if (concern != null)
				{
					Superimposed si = concern.getSuperimposed();
					if (si == null)
					{
						si = new SuperimposedImpl();
						concern.setSuperimposed(si);
						repository.add(si);
					}
					MethodReference mr = null;
					if (fmb.getCondition() != null)
					{
						mr = fmb.getCondition().getMethodReference();
					}
					ImposedFilterModule ifm = new ImposedFilterModuleImpl(fm, mr);
					ifm.setSourceInformation(fmb.getSourceInformation());
					si.addFilterModule(ifm);
					repository.add(ifm);
				}
				else
				{
					logger.error(String.format("Selected type '%s' does not have a concern representation", tp
							.getFullName()));
				}
			}
		}
		if (cnt == 0)
		{
			logger.warn(String.format("Selector %s did not select a single type", fmb.getSelector()
					.getFullyQualifiedName()), fmb.getSelector());
		}
		return true;
	}

	protected FilterModule resolveFilterModuleParameters(FilterModuleBinding fmb)
	{
		FilterModule fm = fmb.getFilterModuleReference().getReference();
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
			context.put(par.getRawName(), val);
			++idx;
		}

		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
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
			oos.writeObject(fm);
			fm.setOwner(realOwner);

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			ParameterResolver pm = new ParameterResolver(is, context, refman);
			fm = (FilterModule) pm.readObject();
			fm.setOwner(realOwner);

			List<RepositoryEntity> newEnt = pm.getRepositoryEntities();
			newEnt.remove(fakeOwner);
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
