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
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleInfo;
import Composestar.Core.Master.Config.ModuleInfoManager;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

import composestar.dotNET.tym.entities.ArrayOfKeyValueSetting;
import composestar.dotNET.tym.entities.ConcernElement;
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

	private static String configFileName;
	private static String increConfig = "INCREconfig.xml";
	private static ConfigurationContainerDocument configDocument;
	private static ConfigurationContainer configContainer;

	private long startTime;

	/**
	 * Default ctor.
	 * 
	 * @param configurationFile
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
		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Master initializing.");

		File configFile = new File(configFileName);
		if (!configFile.exists())
		{
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Configuration file '" + configFileName + " not found!");
			System.exit(-1);
		}

		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Using configuration file '" + configFileName + "'");
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
		
		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Master initialized.");
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public int run()
	{
		try
		{
			Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, TITLE + " " + VERSION);

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Reading configuration");

			// TODO: move to RepositoryCollector?
			List<ConcernElement> concerns = configContainer.getConcerns().getConcernList();
			for (ConcernElement ce : concerns)
			{
				ConcernSource concern = new ConcernSource();
				File concernFile = new File(ce.getPathName(), ce.getFileName());
				concern.setFileName(concernFile.getAbsolutePath());

				Configuration.instance().getProjects().addConcernSource(concern);
			}

			// Create new resources
			CommonResources resources = new CommonResources();

			// Initialize INCRE
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Initializing INCRE...");
			INCRE incre = INCRE.instance();
			incre.run(resources);

			// Set FILTH options
			ModuleInfo filth = ModuleInfoManager.get("FILTH");
			filth.setSettingValue("input", getSettingValue("SpecificationFILTH"));
			filth.setSettingValue("outputEnabled", getSettingValue("OutputEnabledFILTH"));

			// Execute enabled modules one by one
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Executing modules...");

			Iterator<INCREModule> it = incre.getModules();
			while (it.hasNext())
			{
				INCREModule m = it.next();
				m.execute(resources);
				
				long total = incre.getReporter().getTotalForModule(m.getName(), INCRETimer.TYPE_ALL);
				Debug.out(Debug.MODE_DEBUG, INCRE.MODULE_NAME, m.getName() + " executed in " + total + " ms");
			}

			// write config file:
			configDocument.save(new File(configFileName));

			long elapsed = System.currentTimeMillis() - startTime;
			System.out.println("total elapsed time: " + elapsed + " ms");

			// Close INCRE
			INCRE.instance().getReporter().close();

			// Successfull exit
			System.exit(0);
		}
		catch (ModuleException e)
		{
			String error = e.getMessage();
			if (error == null || error.equals("null")) // great information
			{
				error = e.toString();
			}

			if ((e.getErrorLocationFilename() != null) && e.getErrorLocationFilename().length() > 0) Debug.out(
					Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e
							.getErrorLocationLineNumber());
			else Debug.out(Debug.MODE_ERROR, e.getModule(), error);

			// Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " +
			// printStackTrace(e));
			return ECOMPILE;
		}
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, Debug.stackTrace(ex));
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
