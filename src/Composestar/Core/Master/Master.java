/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ConfigurationException;
import Composestar.Core.Master.Config.ModuleInfo;
import Composestar.Core.Master.Config.ModuleInfoManager;
import Composestar.Core.Master.Config.XmlHandlers.BuildConfigHandler;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Version;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public abstract class Master
{
	public static final String MODULE_NAME = "Master";

	/**
	 * Compile process failed (e.g. a ModuleException was thrown)
	 */
	public static final int ECOMPILE = 1;

	/**
	 * Return code when no config file was found (or an exception was raised
	 * during parsing of the configuration)
	 */
	public static final int ECONFIG = 2;

	/**
	 * General execution failure
	 */
	public static final int EFAIL = 3;

	/**
	 * Errors were raised during compiling
	 */
	public static final int EERRORS = 4;

	protected String configFilename;

	protected int debugOverride = -1;

	protected Map<String, Map<String, String>> settingsOverride;

	protected CommonResources resources;

	public Master()
	{
		settingsOverride = new HashMap<String, Map<String, String>>();
	}

	
	public Master(String[] args)
	{
		processCmdArgs(args);
	}

	public void processCmdArgs(String[] args)
	{
		for (String arg : args)
		{
			if (arg.startsWith("-d"))
			{
				debugOverride = Integer.parseInt(arg.substring(2));
				Debug.setMode(debugOverride);
			}
			else if (arg.startsWith("-t"))
			{
				int et = Integer.parseInt(arg.substring(2));
				Debug.setErrThreshold(et);
			}
			else if (arg.startsWith("-D"))
			{
				// -D<module>.<setting>=<value>
				String[] setting = arg.substring(2).split("\\.|=", 3);
				if (setting.length != 3)
				{
					System.err.println("Correct format is: -D<module>.<setting>=<value>");
					continue;
				}
				Map<String, String> ms = settingsOverride.get(setting[0]);
				if (ms == null)
				{
					ms = new HashMap<String, String>();
					settingsOverride.put(setting[0], ms);
				}
				ms.put(setting[1], setting[2]);
			}
			else if (arg.startsWith("-"))
			{
				System.out.println("Unknown option " + arg);
			}
			else
			{
				// assume it's the config file
				configFilename = arg;
			}
		}
	}

	public void loadConfiguration() throws Exception
	{
		File configFile = new File(configFilename);
		if (!configFile.canRead())
		{
			throw new Exception(
					"Unable to open configuration file: '" + configFilename + "'");
		}

		// create the repository and common resources
		DataStore.instance();
		resources = CommonResources.instance();

		// load the project configuration file
		try
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Reading build configuration from: " + configFilename);

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();

			BuildConfigHandler handler = new BuildConfigHandler(parser);
			parser.setContentHandler(handler);
			parser.parse(new InputSource(configFilename));
		}
		catch (Exception e)
		{
			throw new Exception(
					"An error occured while reading the build configuration file '" + configFilename + 
					"': " + e.getMessage());
		}

		// Set debug level
		if (debugOverride == -1)
		{
			Debug.setMode(Configuration.instance().getBuildDebugLevel());
		}
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public int run()
	{
		try
		{
			long beginTime = System.currentTimeMillis();

			ModuleInfo mi = ModuleInfoManager.get(INCRE.class);
			Map<String, String> overrideSet = settingsOverride.get(INCRE.MODULE_NAME);
			if ((overrideSet != null) && (mi != null))
			{
				for (Entry<String, String> entry : overrideSet.entrySet())
				{
					try
					{
						mi.setSettingValue(entry.getKey(), entry.getValue());
					}
					catch (ConfigurationException ce)
					{
						Debug.out(Debug.MODE_ERROR, INCRE.MODULE_NAME, 
								"Configuration override error: " + ce.getMessage());
					}
				}
			}

			// initialize INCRE
			INCRE incre = INCRE.instance();
			incre.init();

			// load override settings
			for (Entry<String, Map<String, String>> entry : settingsOverride.entrySet())
			{
				if (entry.getKey().equals(INCRE.MODULE_NAME))
				{
					continue;
				}
				mi = ModuleInfoManager.get(entry.getKey());
				if (mi != null)
				{
					for (Entry<String, String> se : entry.getValue().entrySet())
					{
						try
						{
							mi.setSettingValue(se.getKey(), se.getValue());
						}
						catch (ConfigurationException ce)
						{
							Debug.out(Debug.MODE_ERROR, entry.getKey(), 
									"Configuration override error: " + ce.getMessage());
						}
					}
				}
				else
				{
					Debug.out(Debug.MODE_ERROR, MODULE_NAME, 
							"Configuration override error: no such module: " + entry.getKey());
				}
			}

			// execute enabled modules one by one
			incre.runModules(resources);

			// close the incre reporter
			incre.getReporter().close();

			// display total time elapsed
			long total = System.currentTimeMillis() - beginTime;
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Total time: " + total + "ms");

			// display number of warnings
			if (Debug.willLog(Debug.MODE_WARNING))
			{
				Debug.outWarnings();
			}

			if (Debug.numErrors() > 0)
			{
				Debug.outWarnings();
				return EERRORS;
			}
		}
		catch (ModuleException e)
		{
			String error = e.getMessage();
			String filename = e.getErrorLocationFilename();
			int lineNumber = e.getErrorLocationLineNumber();

			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			if (filename == null || "".equals(filename))
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			}
			else
			{
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, filename, lineNumber);
			}

			Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + Debug.stackTrace(e));
			return ECOMPILE;
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			if (error == null || "null".equals(error))
			{
				error = e.toString();
			}

			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Internal compiler error: " + error);
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "StackTrace: " + Debug.stackTrace(e));
			return EFAIL;
		}
		return 0;
	}

	protected static void main(Class masterClass, String[] args)
	{
		if (args.length == 0)
		{
			return;
		}

		String arg = args[0].toLowerCase();
		if (arg.equals("-v") || arg.equals("--version"))
		{
			Version.reportVersion(System.out);
			return;
		}

		Master master;
		try
		{
			master = (Master) masterClass.newInstance();
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize master: " + e.getMessage());
			e.printStackTrace();
			System.exit(EFAIL);
			return;
		}
		master.processCmdArgs(args);
		try
		{
			master.loadConfiguration();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(ECONFIG);
		}

		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, Version.getTitle() + " " + Version.getVersionString());
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Compiled on " + Version.getCompileDate().toString());
		int ret = master.run();
		if (ret != 0)
		{
			System.exit(ret);
		}
	}
}
