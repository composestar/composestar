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
import java.util.Arrays;
import java.util.Map.Entry;

import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.ConflictRule.RuleType;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;

import composestar.dotNET2.tym.entities.ArrayOfKeyValueSetting;
import composestar.dotNET2.tym.entities.ConfigurationContainer;
import composestar.dotNET2.tym.entities.ConfigurationContainerDocument;
import composestar.dotNET2.tym.entities.ConflictRuleElement;
import composestar.dotNET2.tym.entities.KeyValueSetting;
import composestar.dotNET2.tym.entities.ResourceElement;

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
			throw new Exception("Configuration file '" + configFilename + " not found!");
		}

		logger.info("Using configuration file '" + configFilename + "'");
		configDocument = ConfigurationContainerDocument.Factory.parse(configFile);
		configContainer = configDocument.getConfigurationContainer();

		// Set the debugmode
		setLogLevel(getSettingValue("buildDebugLevel"));

		resources = new CommonResources();
		resources.setRepository(DataStore.instance());
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

		// load SECRET config
		SECRETResources sresc = null;
		if (configContainer.getResourceElements() != null || configContainer.getConflictRuleElements() != null)
		{
			sresc = new SECRETResources();
			config.setSecretResources(sresc);
		}
		if (configContainer.getResourceElements() != null)
		{
			for (ResourceElement re : configContainer.getResourceElements().getResourceElementList())
			{
				try
				{
					if (re.getOperations() == null || re.getName() == null || re.getName().trim().length() == 0
							|| re.getOperations().trim().length() == 0)
					{
						continue;
					}
					Resource r = ResourceType.createResource(re.getName(), false);
					String[] words = re.getOperations().split(",");
					r.addVocabulary(Arrays.asList(words));
					sresc.addResource(r);
				}
				catch (IllegalArgumentException e)
				{
					logger.warn(String.format("SECRET Resource \"%s\" is not valid", re.getName()));
				}
			}
		}
		if (configContainer.getConflictRuleElements() != null)
		{
			for (ConflictRuleElement cre : configContainer.getConflictRuleElements().getConflictRuleElementList())
			{
				try
				{
					ResourceType rt = ResourceType.parse(cre.getResource());
					Resource r;
					if (rt == ResourceType.Custom)
					{
						r = sresc.getResource(cre.getResource());
					}
					else
					{
						r = sresc.getResource(rt.toString());
					}
					if (r == null)
					{
						r = ResourceType.createResource(cre.getResource(), true);
						if (!r.getType().isMeta())
						{
							sresc.addResource(r);
						}
					}
					RuleType ruletype;
					if (cre.getConstraint())
					{
						ruletype = RuleType.Constraint;
					}
					else
					{
						ruletype = RuleType.Assertion;
					}
					ConflictRule cr = new ConflictRule(r, ruletype, cre.getPattern(), cre.getMessage());
					sresc.addRule(cr);
				}
				catch (PatternParseException pe)
				{
					logger.warn(String.format("Invalid regular expression \"%s\"", cre.getPattern()));
				}
				catch (IllegalArgumentException e)
				{
					logger.warn(String.format("SECRET Rule references an invalid resource \"%s\"", cre.getResource()));
				}
			}
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
			System.out.println("Usage: java -jar StarLight.jar [options] <config file>");
			return;
		}
		main(StarLightMaster.class, args);
	}
}
