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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TASMAN.Xml.TASMANConfig;
import Composestar.Utils.Logging.CPSLogger;

/**
 * TASk MANager
 * 
 * @author Michiel Hendriks
 */
public class Manager
{
	public static final String MODULE_NAME = "TASMAN";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected SequentialTask tasks;

	protected CommonResources resources;

	public Manager(CommonResources inresources)
	{
		resources = inresources;
	}

	protected InputStream getConfigFile() throws ModuleException
	{
		String filename = resources.configuration().getSetting(MODULE_NAME + ".config");
		try
		{
			if (filename != null && filename.length() > 0)
			{
				File file = new File(filename);
				if (file.isAbsolute())
				{
					return new FileInputStream(file);
				}
				file = new File(resources.configuration().getProject().getBase(), filename);
				if (file.exists())
				{
					return new FileInputStream(file);
				}
				file = resources.getPathResolver().getResource(filename);
				if (file != null)
				{
					return new FileInputStream(file);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			throw new ModuleException("No configuration file found with name '" + filename + "'", MODULE_NAME);
		}

		// get internal file
		InputStream is = resources.getPathResolver().getInternalResourceStream("/TASMANConfig.xml");
		if (is != null)
		{
			return is;
		}
		throw new ModuleException("No configuration file found with name '" + filename
				+ "' and failed loading the internal configuration.", MODULE_NAME);
	}

	public void loadConfig() throws ModuleException
	{
		tasks = TASMANConfig.loadConfig(getConfigFile());
	}

	/**
	 * Execute the configured tasks
	 * 
	 * @param resources
	 * @throws ModuleException
	 */
	public void runTasks() throws ModuleException
	{
		if (tasks == null)
		{
			loadConfig();
		}
		tasks.execute(this, resources);
	}

	public synchronized void reportModuleResult(CTCommonModule.ModuleReturnValue result, CTCommonModule module)
			throws ModuleException
	{
		switch (result)
		{
			case Fatal:
				throw new ModuleException("Module execution was fatal"
				// , module.getModuleID()
				);
			case Error:
				break;
			case Ok:
			default:
		}
	}
}
