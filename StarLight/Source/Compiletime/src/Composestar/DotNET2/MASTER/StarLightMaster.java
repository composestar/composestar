/*
 * This file is part of Composestar StarLight project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 *
 */

package Composestar.DotNET2.MASTER;

import java.io.File;
import java.util.Map.Entry;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;

import composestar.dotNET2.tym.entities.ArrayOfKeyValueSetting;
import composestar.dotNET2.tym.entities.ConfigurationContainer;
import composestar.dotNET2.tym.entities.ConfigurationContainerDocument;
import composestar.dotNET2.tym.entities.KeyValueSetting;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class StarLightMaster extends Master
{
	public static final String RESOURCE_CONFIGCONTAINER = MODULE_NAME + ".ConfigContainer";

	private ConfigurationContainerDocument configDocument;

	private ConfigurationContainer configContainer;

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
			if (kv.getKey().equalsIgnoreCase(key))
			{
				return kv.getValue();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.Master#loadConfiguration()
	 */
	@Override
	protected void loadConfiguration() throws Exception
	{
		File configFile = new File(configFilename);
		if (!configFile.exists())
		{
			logger.error("Configuration file '" + configFilename + " not found!");
			System.exit(ECONFIG);
		}

		logger.info("Using configuration file '" + configFilename + "'");
		configDocument = ConfigurationContainerDocument.Factory.parse(configFile);
		configContainer = configDocument.getConfigurationContainer();

		// Set the debugmode
		setLogLevel(getSettingValue("buildDebugLevel"));

		// Create the repository
		DataStore.instance();

		resources = new CommonResources();
		resources.put(RESOURCE_CONFIGFILE, configFilename);
		resources.put(RESOURCE_CONFIGCONTAINER, configContainer);
		resources.setPathResolver(getPathResolver());

		BuildConfig config = new BuildConfig();
		resources.setConfiguration(config);

		ArrayOfKeyValueSetting settings = configContainer.getSettings();
		for (int i = 0; i < settings.sizeOfSettingArray(); i++)
		{
			KeyValueSetting kv = settings.getSettingArray(i);
			config.addSetting(kv.getKey(), kv.getValue());
		}

		Project project = config.getNewProject();
		File intermPath = new File(getSettingValue("IntermediateOutputPath"));
		project.setBase(intermPath.getParentFile());
		project.setIntermediate(intermPath.getName());

		ModuleInfoManager.getInstance().setBuildConfig(resources.configuration());
		for (Entry<String, String> override : settingsOverride.entrySet())
		{
			config.addSetting(override.getKey(), override.getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.Master#postBuild()
	 */
	@Override
	protected void postBuild() throws Exception
	{
		configDocument.save(new File(configFilename));
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run
	 * method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarDotNET.jar [options] <config file>");
			return;
		}
		main(StarLightMaster.class, args);
	}
}
