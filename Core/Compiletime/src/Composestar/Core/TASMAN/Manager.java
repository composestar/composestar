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
import java.util.Map.Entry;

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
	protected Map<CTCommonModule.ModuleImportance, Boolean> importance;

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
		importance = new HashMap<CTCommonModule.ModuleImportance, Boolean>();
		importance.put(CTCommonModule.ModuleImportance.REQUIRED, true);
		importance.put(CTCommonModule.ModuleImportance.VALIDATION, true);
		importance.put(CTCommonModule.ModuleImportance.ADVISING, true);
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

			// try override file in the base directory
			File file = resources.getPathResolver().getResource("TASMANConfig.xml");
			if (file != null)
			{
				logger.info(String.format("Using configuration from: %s", file.toString()));
				return new FileInputStream(file);
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
	 * Method to call to report the result of a module.
	 * 
	 * @param result
	 * @param module
	 * @throws ModuleException
	 */
	public synchronized void reportModuleResult(CTCommonModule.ModuleReturnValue result, CTCommonModule module,
			boolean throwOnFatal) throws ModuleException
	{
		if (module == null)
		{
			return;
		}
		String moduleid = module.getModuleName();
		if (moduleid == null || moduleid.length() == 0)
		{
			// Panic! No module name
			logger.warn(String.format("Module %s does not have an ID, switching to always execute modules", module
					.getClass().getName()));
			undeterministic = true;
			moduleid = module.getClass().getName();
		}
		else
		{
			moduleResults.put(moduleid, result);
		}
		if (result == null)
		{
			result = CTCommonModule.ModuleReturnValue.OK;
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
	public boolean canExecute(CTCommonModule module) throws ModuleException
	{
		if (module == null)
		{
			return false;
		}
		// check if this importance level is included
		CTCommonModule.ModuleImportance imp = module.getImportance();
		if (imp == null)
		{
			imp = CTCommonModule.ModuleImportance.REQUIRED;
		}
		if (!importance.get(imp))
		{
			return false || undeterministic;
		}
		// check dependencies
		String[] deps = module.getDependencies();
		if (deps == null)
		{
			return true;
		}
		for (String dep : deps)
		{
			if (CTCommonModule.DEPEND_ALL.equals(dep))
			{
				synchronized (moduleResults)
				{
					// check if everything executed
					for (Entry<String, CTCommonModule.ModuleReturnValue> res : moduleResults.entrySet())
					{
						if (res.getValue() != CTCommonModule.ModuleReturnValue.OK
								&& res.getValue() != CTCommonModule.ModuleReturnValue.NO_EXECUTION)
						{
							// one failed
							logger.info(String.format(
									"Module %s depends on all previous modules, %s did not execute successfully",
									module.getModuleName(), res.getKey()));
							return false || undeterministic;
						}
					}
				}
			}
			else if (CTCommonModule.DEPEND_PREVIOUS.equals(dep))
			{
				// TODO check previously executed module
			}
			else
			{
				synchronized (moduleResults)
				{
					// check dep tree
					if (!moduleResults.containsKey(dep))
					{
						// not executed
						logger.info(String.format("Module %s depends on module %s which was not executed", module
								.getModuleName(), dep));
						return false || undeterministic;
					}
					else if (moduleResults.get(dep) != CTCommonModule.ModuleReturnValue.OK)
					{
						// no valid execution
						logger.info(String.format("Module %s depends on module %s which did not return Ok", module
								.getModuleName(), dep));
						return false || undeterministic;
					}
				}
			}
		}
		return true;
	}
}
