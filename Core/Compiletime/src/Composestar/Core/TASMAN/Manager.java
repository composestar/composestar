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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.ComposestarModule.Importance;
import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
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

	/**
	 * The child tasks to execute in order
	 */
	protected SequentialTask tasks;

	protected CommonResources resources;

	/**
	 * The results of modules that were executed
	 */
	protected Map<String, CTCommonModule.ModuleReturnValue> moduleResults;

	/**
	 * Defines which groups of modules should be executed.
	 */
	protected Map<Importance, Boolean> importance;

	/**
	 * If set to true modules will always be executed. TASMAN sets this to true
	 * when a module was executed that did not have a module ID.
	 */
	protected boolean undeterministic;

	public Manager(CommonResources inresources)
	{
		undeterministic = false;
		resources = inresources;
		moduleResults = new HashMap<String, CTCommonModule.ModuleReturnValue>();
		importance = new HashMap<Importance, Boolean>();
		importance.put(Importance.REQUIRED, true);
		importance.put(Importance.VALIDATION, true);
		importance.put(Importance.ADVISING, true);
	}

	/**
	 * Get the input stream that contains the TASMAN configuration
	 * 
	 * @return
	 * @throws ModuleException
	 */
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

	/**
	 * Load the configuration of TASMAN
	 * 
	 * @throws ModuleException
	 */
	public void loadConfig() throws ModuleException
	{
		URL basePath = null;
		try
		{
			basePath = resources.getPathResolver().getBase().toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			logger.error(e, e);
		}
		tasks = TASMANConfig.loadConfig(getConfigFile(), basePath);
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

	/**
	 * Get the ID of a module. Tries to get the ID from the ComposestarModule
	 * annotation or the ModuleInformation.
	 * 
	 * @param moduleClass
	 * @return
	 */
	public static String getModuleID(Class<? extends CTCommonModule> moduleClass)
	{
		ComposestarModule annot = moduleClass.getAnnotation(ComposestarModule.class);
		if (annot != null)
		{
			return annot.ID();
		}
		else
		{
			logger.warn(String.format("The module class %s does not have a ComposestarModule annotation", moduleClass
					.getName()));
			// no annotation, try a different way
			ModuleInfo mi = ModuleInfoManager.get(moduleClass);
			if (mi != null)
			{
				return mi.getId();
			}
		}
		return null;
	}

	/**
	 * @param result
	 * @param module
	 * @param throwOnFatal
	 * @throws ModuleException
	 */
	public synchronized void reportModuleResult(CTCommonModule.ModuleReturnValue result, CTCommonModule module,
			boolean throwOnFatal) throws ModuleException
	{
		reportModuleResult(result, module.getClass(), throwOnFatal);
	}

	/**
	 * Method to call to report the result of a module.
	 * 
	 * @param result
	 * @param module
	 * @throws ModuleException
	 */
	public synchronized void reportModuleResult(CTCommonModule.ModuleReturnValue result,
			Class<? extends CTCommonModule> module, boolean throwOnFatal) throws ModuleException
	{
		if (module == null)
		{
			return;
		}
		String moduleid = getModuleID(module);
		if (moduleid == null)
		{
			// Panic! No module name
			logger.warn(String.format("Module %s does not have an ID, switching to always execute modules", module
					.getName()));
			undeterministic = true;
			moduleid = module.getName();
		}
		else
		{
			moduleResults.put(moduleid, result);
		}
		if (result == CTCommonModule.ModuleReturnValue.FATAL && throwOnFatal)
		{
			throw new ModuleException("Module execution was fatal", moduleid);
		}
	}

	/**
	 * Will return true when this module can execute. Execution of this module
	 * depends on the execution result of its dependencies.
	 * 
	 * @param module
	 * @return
	 * @throws ModuleException
	 */
	public boolean canExecute(Class<? extends CTCommonModule> module) throws ModuleException
	{
		ComposestarModule annot = module.getAnnotation(ComposestarModule.class);
		if (annot == null)
		{
			// no annotation, can not check it, always execute
			return true;
		}
		if (!importance.get(annot.importance()))
		{
			return false || undeterministic;
		}
		for (String dep : annot.dependsOn())
		{
			if (ComposestarModule.DEPEND_ALL.equals(dep))
			{
				// check if everything executed
				for (CTCommonModule.ModuleReturnValue res : moduleResults.values())
				{
					if (res != CTCommonModule.ModuleReturnValue.OK)
					{
						// one failed
						logger.info(String.format("Module %s depends on module %s which did not return Ok", annot.ID(),
								dep));
						return false || undeterministic;
					}
				}
			}
			else if (ComposestarModule.DEPEND_PREVIOUS.equals(dep))
			{
				// check previously executed module
			}
			else
			{
				synchronized (moduleResults)
				{
					// check dep tree
					if (!moduleResults.containsKey(dep))
					{
						// not executed
						logger.info(String.format("Module %s depends on module %s which was not executed", annot.ID(),
								dep));
						return false || undeterministic;
					}
					else if (moduleResults.get(dep) != CTCommonModule.ModuleReturnValue.OK)
					{
						// no valid execution
						logger.info(String.format("Module %s depends on module %s which did not return Ok", annot.ID(),
								dep));
						return false || undeterministic;
					}
				}
			}
		}
		return true;
	}
}
