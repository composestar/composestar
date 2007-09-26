/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;

/**
 * Singleton containing the build configuration
 * 
 * @deprecated
 */
@Deprecated
public class Configuration implements Serializable
{
	private static final long serialVersionUID = -8812125434498730547L;

	private static Configuration cfgInstance;

	private Properties properties;

	private Projects projects;

	private PathSettings pathSettings;

	private Platform platform;

	private BuiltLibraries libraries;

	private CustomFilters filters;

	/**
	 * Temporary storage of module settings. ModuleInfo uses this to populate
	 * it's settings when it's loaded.
	 */
	private Map<String, Map> tmpModuleSettings;

	public Configuration()
	{
		properties = new Properties();
		projects = new Projects();
		pathSettings = new PathSettings();
		platform = new Platform();
		libraries = new BuiltLibraries();
		filters = new CustomFilters();

		tmpModuleSettings = new HashMap<String, Map>();
	}

	public static Configuration instance()
	{
		if (cfgInstance == null)
		{
			cfgInstance = new Configuration();
		}

		return cfgInstance;
	}

	public void setBuildDebugLevel(int level)
	{
		properties.setProperty("buildDebugLevel", "" + level);
	}

	public int getBuildDebugLevel()
	{
		String level = properties.getProperty("buildDebugLevel");
		return Integer.parseInt(level);
	}

	public void setPlatformName(String name)
	{
		properties.setProperty("Platform", name);
	}

	public String getPlatformName()
	{
		return properties.getProperty("Platform");
	}

	public Projects getProjects()
	{
		return projects;
	}

	/**
	 * @deprecated use ModuleInfoManager
	 */
	public String getModuleProperty(String module, String key, String def)
	{
		ModuleInfo mi = ModuleInfoManager.get(module);
		if (mi != null)
		{
			return mi.getStringSetting(key, def);
		}
		return def;
	}

	/**
	 * @deprecated use ModuleInfoManager
	 */
	public int getModuleProperty(String module, String key, int def)
	{
		ModuleInfo mi = ModuleInfoManager.get(module);
		if (mi != null)
		{
			return mi.getIntSetting(key, def);
		}
		return def;
	}

	/**
	 * @deprecated use ModuleInfoManager
	 */
	public boolean getModuleProperty(String module, String key, boolean def)
	{
		ModuleInfo mi = ModuleInfoManager.get(module);
		if (mi != null)
		{
			return mi.getBooleanSetting(key, def);
		}
		return def;
	}

	public PathSettings getPathSettings()
	{
		return pathSettings;
	}

	protected static String LIB_PATH = "lib";

	/**
	 * @return get a File instance to a file in the compose* lib directory
	 */
	public File getLibFile(String file)
	{
		URL s = Configuration.class.getResource("/");
		File res;
		try
		{
			res = new File(s.toURI());
		}
		catch (URISyntaxException e)
		{
			// TODO: report error
			return null;
		}
		res = new File(res, file);
		if (!res.exists())
		{
			// fall back to the "installed" files
			res = new File(pathSettings.getPath("Composestar") + LIB_PATH, file);
		}
		return res;
	}

	public Platform getPlatform()
	{
		return platform;
	}

	public BuiltLibraries getLibraries()
	{
		return libraries;
	}

	public CustomFilters getFilters()
	{
		return filters;
	}

	public void addTmpModuleSettings(String moduleName, Map props)
	{
		tmpModuleSettings.put(moduleName, props);
	}

	public Map getTmpModuleSettings(String moduleName)
	{
		return tmpModuleSettings.get(moduleName);
	}
}
