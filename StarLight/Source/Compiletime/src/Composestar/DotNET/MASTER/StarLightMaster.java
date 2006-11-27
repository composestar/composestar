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

import org.apache.xmlbeans.XmlException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.INCRE.Module;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.Master.Config.ConcernSource;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

import composestar.dotNET.tym.entities.ArrayOfConcernElement;
import composestar.dotNET.tym.entities.ArrayOfKeyValueSetting;
import composestar.dotNET.tym.entities.KeyValueSetting; 
import composestar.dotNET.tym.entities.ConcernElement;
import composestar.dotNET.tym.entities.ConfigurationContainer;
import composestar.dotNET.tym.entities.ConfigurationContainerDocument;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public class StarLightMaster extends Master
{
	private static final String MODULE_NAME = "MASTER";
	private static final String VERSION = "0.2 beta";
	private static final String AUTHOR = "University of Twente";
	private static final String TITLE = "ComposeStar StarLight";

	private static String configFileName;
	private static ConfigurationContainerDocument configDocument;
	private static ConfigurationContainer configContainer;

	private CommonResources resources;
	private long timer;

	/**
	 * Default ctor.
	 * @param configurationFile
	 */
	public StarLightMaster(String configurationFile)
	{
		// Store the config filename 
		configFileName = configurationFile;

		// Init new resources
		resources = new CommonResources();

		// Set the default debug mode
		Debug.setMode(Debug.MODE_DEFAULTMODE);
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
	 * Searches the settings array for a specific key and return the value for that key.
	 * @param settings
	 * @param key
	 * @return
	 */
	private String getSettingValue(String key)
	{
		ArrayOfKeyValueSetting settings = configContainer.getSettings();
		for (int i = 0; i < settings.sizeOfSettingArray(); i++)
		{
			KeyValueSetting kv = settings.getSettingArray(i);
			if (kv.getKey().equalsIgnoreCase(key))
				return kv.getValue(); 
		}
		
		return "";
	}
	
	/**
	 * Initialize the StarLight master.
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private void initialize() throws XmlException, IOException
	{
		timer = System.currentTimeMillis();
		Debug.out(Debug.MODE_INFORMATION,MODULE_NAME,"Master initializing.");

		File configFile = new File(configFileName);
		if (!configFile.exists())
		{
			Debug.out(Debug.MODE_CRUCIAL,MODULE_NAME,"Configuration file '" + configFileName + " not found!");
			System.exit(-1);
		}

		Debug.out(Debug.MODE_DEBUG,MODULE_NAME,"Using configuration file '" + configFileName + "'");
		configDocument = ConfigurationContainerDocument.Factory.parse(configFile);
		configContainer = configDocument.getConfigurationContainer();

		// Set the debugmode
		Debug.setMode(Integer.parseInt(getSettingValue("CompiletimeDebugLevel")));
		//Debug.setMode(Debug.MODE_WARNING);

		// Apache XML driver is moved to a different package in Java 5
		if (System.getProperty("java.version").substring(0, 3).equals("1.5")) {
			System.setProperty("org.xml.sax.driver","com.sun.org.apache.xerces.internal.parsers.SAXParser");            
		}
		else {
			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");            
		}

		// Create the repository
		DataStore.instance();

		// Set the paths
		if (getSettingValue("IntermediateOutputPath").length() > 0) {
			Configuration.instance().getPathSettings().addPath("Base", getSettingValue("IntermediateOutputPath"));
		}
		Configuration.instance().getPathSettings().addPath("Composestar", getSettingValue("InstallFolder") + "\\" );

		// Enable INCRE
		ModuleSettings increSettings = new ModuleSettings();
		increSettings.setName("INCRE");
		increSettings.addProperty("enabled", "true");
		Configuration.instance().getModuleSettings().addModule("INCRE", increSettings);
		
		// Set FILTH input file
		ModuleSettings filthSettings = new ModuleSettings();
		filthSettings.setName("FILTH");
		filthSettings.addProperty("input", getSettingValue("SpecificationFILTH"));
		filthSettings.addProperty("outputEnabled",getSettingValue("OutputEnabledFILTH"));
		Configuration.instance().getModuleSettings().addModule("FILTH", filthSettings);

		Debug.out(Debug.MODE_INFORMATION,"Master","Master initialized.");
	}

	/**
	 * Calls run on all modules added to the master.
	 */
	public void run()
	{
		try
		{
			Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, StarLightMaster.TITLE + " " + StarLightMaster.VERSION);

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Creating DataStore");
			DataStore.instance();

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Reading configuration");

			ArrayOfConcernElement concerns = configContainer.getConcerns();
			for (int i = 0; i < concerns.sizeOfConcernArray(); i++)
			{
				ConcernElement ci = concerns.getConcernArray(i);
				ConcernSource concern = new ConcernSource();
				File concernFile = new File(ci.getPathName(), ci.getFileName());
				concern.setFileName(concernFile.getAbsolutePath());

				Configuration.instance().getProjects().addConcernSource(concern);                
			}

			// Initialize INCRE
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Initializing INCRE");
			INCRE incre = INCRE.instance();
			incre.run(resources);

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Starting INCRE to process modules");
			Iterator modulesIter = incre.getModules();
			while (modulesIter.hasNext())
			{
				// Execute enabled modules one by one
				Module m = (Module)modulesIter.next();
				m.execute(resources);
				Debug.out(Debug.MODE_DEBUG, "INCRE", m.getName() + " executed in " + incre.getReporter().getTotalForModule(m.getName(), INCRETimer.TYPE_ALL) + " ms");
			}
			
			incre.storeHistory();

			// Shutdown
			shutdown();
		}
		catch (ModuleException e)
		{
			String error = e.getMessage();
			if (error == null || error.equals("null")) //great information
			{
				error = e.toString();
			}

			if ((e.getErrorLocationFilename() != null) && e.getErrorLocationFilename().length() > 0)
				Debug.out(Debug.MODE_ERROR, e.getModule(), error, e.getErrorLocationFilename(), e.getErrorLocationLineNumber());
			else
				Debug.out(Debug.MODE_ERROR, e.getModule(), error);
			
			//Debug.out(Debug.MODE_DEBUG, e.getModule(), "StackTrace: " + printStackTrace(e));
			System.exit(1);
		} 
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, Debug.stackTrace(ex));
			System.exit(1);
		}
	}

	private void shutdown() throws IOException
	{		
		//write config file:
		File file = new File(configFileName);
		configDocument.save(file);

		long elapsed = System.currentTimeMillis() - timer;
		System.out.println("total elapsed time: "  + elapsed + " ms");

		// Close INCRE
		INCRE.instance().getReporter().close();

		// Successfull exit
		System.exit(0);
	}
	
	/**
	 * Compose* StarLight main function.
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Usage: java -jar StarLight.jar <config file>");
			System.exit(0);
		}

		if(args[0].equalsIgnoreCase("-v"))
		{
			System.out.println(TITLE);
			System.out.println(AUTHOR);
			System.exit(0);
		}

		// Create new master
		StarLightMaster master = new StarLightMaster(args[0]);

		try
		{ 
			// Initialize
			master.initialize();    

			// Run the master process
			master.run();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Debug.out(Debug.MODE_ERROR,MODULE_NAME, "Could not open configuration file: " + args[0]);            
			System.exit(-1);
		}
	}
}
