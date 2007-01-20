/*
 * This file is part of Composestar StarLight project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 *
 */

package Composestar.DotNET.MASTER;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleInfo;
import Composestar.Core.Master.Config.ModuleInfoManager;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.Logging.CPSLogger;

import composestar.dotNET.tym.entities.ArrayOfKeyValueSetting;
import composestar.dotNET.tym.entities.ConfigurationContainer;
import composestar.dotNET.tym.entities.ConfigurationContainerDocument;
import composestar.dotNET.tym.entities.KeyValueSetting;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class StarLightMaster extends Master
{
	private static final String MODULE_NAME = "MASTER";
	private static final String VERSION = "0.2 beta";
	private static final String AUTHOR = "University of Twente";
	private static final String TITLE = "ComposeStar StarLight";
	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private static String configFileName;
	private static String increConfig = "INCREconfig.xml";
	private static ConfigurationContainerDocument configDocument;
	private static ConfigurationContainer configContainer;

	private long startTime;

	/**
	 * Creates a new StarLightMaster with the specified command line arguments.
	 */
	public StarLightMaster(String[] args)
	{
		processCmdArgs(args);
	}

	public void processCmdArgs(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.startsWith("-i"))
			{
				increConfig = arg.substring(2).trim();
			}
			else if (arg.startsWith("-"))
			{
				System.out.println("Unknown option " + arg);
			}
			else
			{
				// assume it's the config file
				configFileName = args[i];
			}
		}
	}

	/**
	 * @return The repository filename.
	 */
	public static String getConfigFileName()
	{
		return configFileName;
	}

	/**
	 * @return the configContainer
	 */
	public static ConfigurationContainer getConfigContainer()
	{
		return configContainer;
	}

	/**
	 * Searches the settings array for a specific key and return the value for
	 * that key.
	 * 
	 * @return the value for the specified key if it exists; null otherwise.
	 */
	private String getSettingValue(String key)
	{
		ArrayOfKeyValueSetting settings = configContainer.getSettings();
		for (int i = 0; i < settings.sizeOfSettingArray(); i++)
		{
			KeyValueSetting kv = settings.getSettingArray(i);
			if (kv.getKey().equalsIgnoreCase(key)) return kv.getValue();
		}

		return null;
	}

	private int getSettingValue(String key, int def)
	{
		String value = getSettingValue(key);
		if (value == null) return def;
		else return Integer.parseInt(value);
	}

	/**
	 * Initialize the StarLight master.
	 * 
	 * @throws IOException
	 * @throws XmlException
	 */
	private void initialize() throws Exception
	{
		startTime = System.currentTimeMillis();
		logger.info("Master initializing.");

		File configFile = new File(configFileName);
		if (!configFile.exists())
		{
			logger.error("Configuration file '" + configFileName + " not found!");
			System.exit(-1);
		}

		logger.info("Using configuration file '" + configFileName + "'");
		configDocument = ConfigurationContainerDocument.Factory.parse(configFile);
		configContainer = configDocument.getConfigurationContainer();

		// Set the debugmode
		Debug.setMode(getSettingValue("CompiletimeDebugLevel", Debug.MODE_DEFAULTMODE));

		// Create the repository
		DataStore.instance();

		// Set the paths
		Configuration config = Configuration.instance();
		PathSettings ps = config.getPathSettings();

		if (getSettingValue("IntermediateOutputPath") != null)
		{
			ps.addPath("Base", getSettingValue("IntermediateOutputPath"));
		}
		ps.addPath("Composestar", getSettingValue("InstallFolder") + "\\");

		// Set INCRE options
		ModuleInfo incre = ModuleInfoManager.get(INCRE.class);
		incre.setSettingValue("enabled", false);
		incre.setSettingValue("config", increConfig);
		
		logger.info("Master initialized.");
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public int run()
	{
		try
		{
			Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, TITLE + " " + VERSION);

			// Create new resources
			CommonResources resources = new CommonResources();

			// Initialize INCRE
			logger.debug("Initializing INCRE...");
			INCRE incre = INCRE.instance();
			incre.init();

			// Set FILTH options (must be done after INCRE is initialized)
			ModuleInfo filth = ModuleInfoManager.get("FILTH");
			filth.setSettingValue("input", getSettingValue("SpecificationFILTH"));
			filth.setSettingValue("outputEnabled", getSettingValue("OutputEnabledFILTH"));

			// Execute enabled modules one by one
			logger.debug("Executing modules...");
			incre.runModules(resources);

			// write config file:
			configDocument.save(new File(configFileName));

			long elapsed = System.currentTimeMillis() - startTime;
			logger.info("Total elapsed time: " + elapsed + " ms");

			// Close INCRE
			incre.getReporter().close();

			// Successfull exit
			System.exit(0);
		}
		catch (ModuleException e)
		{
			String message = e.getMessage();
			if (message == null || "null".equals(message)) // great information
			{
				message = e.toString();
			}

			CPSLogger moduleLogger = CPSLogger.getCPSLogger(e.getModule());
			if (e.getFilename() == null || "".equals(e.getFilename()))
			{
				moduleLogger.error(message);
			}
			else
			{
				moduleLogger.error(message, e);
			}

		//	moduleLogger.stackTrace(e);
			return ECOMPILE;
		}
		catch (Exception e)
		{
			logger.stackTrace(e);
			return EFAIL;
		}

		return 0;
	}

	/**
	 * Compose* StarLight main function.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Usage: java -jar StarLight.jar [options] <config file>");
			System.exit(-1);
		}

		if (args[0].equalsIgnoreCase("-v"))
		{
			System.out.println(TITLE);
			System.out.println(AUTHOR);
			System.exit(0);
		}

		// Create new master
		StarLightMaster master = new StarLightMaster(args);

		try
		{
			// Initialize
			master.initialize();

			// Run the master process
			int ret = master.run();
			if (ret != 0)
			{
				System.exit(ret);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Could not open configuration file: " + args[0]);
			System.exit(-1);
		}
	}
}
