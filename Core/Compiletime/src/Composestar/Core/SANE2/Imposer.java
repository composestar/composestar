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

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
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
import Composestar.Utils.Perf.CPSTimer;

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

	protected CPSTimer timer;

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
		timer = CPSTimer.getTimer(ModuleNames.SANE);
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
					ImposedFilterModule ifm = new ImposedFilterModuleImpl(fmb);
					ifm.setSourceInformation(fmb.getSourceInformation());
					si.addFilterModule(ifm);
					repository.add(ifm);
					cnt++;
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
}
