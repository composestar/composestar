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

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Config.Project;
import Composestar.Core.CpsRepository2Impl.RepositoryImpl;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.Master.Master;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Core.SECRET3.Model.RuleType;
import Composestar.Core.SECRET3.Model.WildcardResource;

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
	 * @see Composestar.Core.Master.Master#loadConfiguration()
	 */
	@Override
	protected boolean loadConfiguration() throws Exception
	{
		File configFile;
		if (configFilename == null)
		{
			configFile = new File("starlight.xml");
		}
		else
		{
			configFile = new File(configFilename);
		}
		if (!configFile.canRead())
		{
			if (parser != null && configFilename == null)
			{
				parser.printUsage(System.out);
				return false;
			}
			throw new Exception("Unable to open configuration file: '" + configFile.toString() + "'");
		}

		logger.info("Using configuration file '" + configFilename + "'");
		configDocument = ConfigurationContainerDocument.Factory.parse(configFile);
		configContainer = configDocument.getConfigurationContainer();

		// Set the debugmode
		setLogLevel(getSettingValue("buildDebugLevel"));

		resources = new CommonResources();
		resources.setRepository(new RepositoryImpl());
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
					Resource r = new Resource(re.getName());
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
					Resource r = sresc.getResource(cre.getResource().trim());
					if (r == null)
					{
						if (Resource.isValidName(cre.getResource().trim()))
						{
							r = new Resource(cre.getResource());
							sresc.addResource(r);
						}
						else if (WildcardResource.WILDCARD.equals(cre.getResource().trim()))
						{
							r = WildcardResource.instance();
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
		return true;
	}

	/*
	 * (non-Javadoc)
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
		main(StarLightMaster.class, "StarLight.jar", args);
	}
}
