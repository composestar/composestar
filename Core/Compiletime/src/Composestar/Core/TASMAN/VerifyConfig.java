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
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.TASMAN.Xml.TASMANConfig;

/**
 * This class verifies a TASMAN configuration file to see if all modules exist
 * and that their dependencies are met.
 * 
 * @author Michiel Hendriks
 */
public class VerifyConfig
{
	private VerifyConfig()
	{}

	public static boolean isValidConfig(File filename)
	{
		try
		{
			return isValidConfig(new FileInputStream(filename));
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
	}

	public static boolean isValidConfig(InputStream is)
	{
		try
		{
			return isValidTaskCollection(TASMANConfig.loadConfig(is, null), new HashSet<String>());
		}
		catch (ConfigurationException e)
		{
			return false;
		}
	}

	protected static boolean isValidTaskCollection(TaskCollection tasks, Set<String> previousModules)
	{
		boolean isValid = true;
		for (Task task : tasks.getTasks())
		{
			isValid = isValid & isValidTask(task, previousModules);
		}
		return isValid;
	}

	protected static boolean isValidTask(Task task, Set<String> previousModules)
	{
		if (task instanceof ParallelTask)
		{
			return isValidParallelTask((ParallelTask) task, previousModules);
		}
		else if (task instanceof TaskCollection)
		{
			return isValidTaskCollection((TaskCollection) task, previousModules);
		}
		else if (task instanceof ModuleTask)
		{
			return isValidModule(((ModuleTask) task).getModuleClass(), previousModules);
		}
		return true;
	}

	protected static boolean isValidParallelTask(ParallelTask tasks, Set<String> previousModules)
	{
		boolean isValid = true;
		Set<String> resultingMods = new HashSet<String>(previousModules);
		for (Task task : tasks.getTasks())
		{
			Set<String> tmpMods = new HashSet<String>(previousModules);
			isValid = isValid & isValidTask(task, tmpMods);
			resultingMods.addAll(tmpMods);
		}
		previousModules.addAll(resultingMods);
		return isValid;
	}

	protected static boolean isValidModule(Class<? extends CTCommonModule> moduleClass, Set<String> previousModules)
	{
		if (moduleClass == null)
		{
			System.err.println("A module could not be loaded.");
			return false;
		}

		CTCommonModule module;
		try
		{
			module = moduleClass.newInstance();
		}
		catch (InstantiationException e)
		{
			System.err.println("A module could not be loaded.");
			return false;
		}
		catch (IllegalAccessException e)
		{
			System.err.println("A module could not be loaded.");
			return false;
		}

		boolean isValid = true;
		String[] deps = module.getDependencies();
		if (deps == null)
		{
			deps = new String[0];
		}
		for (String dep : deps)
		{
			if (CTCommonModule.DEPEND_ALL.equals(dep))
			{
				continue;
			}
			if (CTCommonModule.DEPEND_PREVIOUS.equals(dep))
			{
				continue;
			}
			if (!previousModules.contains(dep))
			{
				// missing module
				System.err.println(String.format("Module %s depends on %s, but has not been executed", module
						.getModuleName(), dep));
				isValid = false;
			}
		}
		previousModules.add(module.getModuleName());
		return isValid;
	}

	public static void main(String[] args)
	{
		boolean isValid = true;
		for (String s : args)
		{
			File f = new File(s);
			if (f.exists())
			{
				if (!isValidConfig(f))
				{
					System.err.println(String.format("Invalid configuration: %s", f.getAbsoluteFile()));
					isValid = false;
				}
			}
		}
		if (!isValid)
		{
			System.exit(-1);
		}
	}
}
