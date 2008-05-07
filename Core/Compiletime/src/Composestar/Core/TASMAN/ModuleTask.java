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

package Composestar.Core.TASMAN;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Perf.CPSTimer;

/**
 * This task executes a certain Compose* Module
 * 
 * @author Michiel Hendriks
 */
public class ModuleTask extends Task
{
	protected Class<? extends CTCommonModule> moduleClass;

	protected CTCommonModule module;

	/**
	 * @param tasman
	 */
	public ModuleTask()
	{
		super();
	}

	public void setModuleClass(String ftype) throws ClassNotFoundException
	{
		Class<?> mclass = Class.forName(ftype);
		if (CTCommonModule.class.isAssignableFrom(mclass))
		{
			moduleClass = mclass.asSubclass(CTCommonModule.class);
		}
	}

	public void setModuleClass(Class<? extends CTCommonModule> ftype)
	{
		moduleClass = ftype;
	}

	public Class<? extends CTCommonModule> getModuleClass()
	{
		return moduleClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.TASMAN.Task#execute()
	 */
	@Override
	public void execute(Manager manager, CommonResources resources) throws ModuleException
	{
		if (moduleClass == null)
		{
			throw new ModuleException("Task has no module class assigned", Manager.MODULE_NAME);
		}
		if (module == null)
		{
			try
			{
				module = moduleClass.newInstance();
			}
			catch (InstantiationException e)
			{
				throw new ModuleException(e.getMessage(), Manager.MODULE_NAME, e);
			}
			catch (IllegalAccessException e)
			{
				throw new ModuleException(e.getMessage(), Manager.MODULE_NAME, e);
			}
		}
		resources.inject(module);
		CPSTimer timer = CPSTimer.getTimer(Manager.MODULE_NAME, "Executing module %s (thread: %s)", module.getClass(),
				Thread.currentThread().getName());
		try
		{
			// CTCommonModule.ModuleReturnValue result =
			module.run(resources);
			// manager.reportModuleResult(result, module);
		}
		finally
		{
			timer.stop();
			resources.extract(module);
		}
	}
}
