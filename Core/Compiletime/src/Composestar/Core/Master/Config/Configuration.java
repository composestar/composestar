package Composestar.Core.Master.Config;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Composestar.Utils.Debug;

public class Configuration implements Serializable
{
	private static final long serialVersionUID = -8812125434498730547L;

	private static Configuration cfgInstance;

	private Properties properties;

	private Projects projects;

	private Modules moduleSettings;

	private PathSettings pathSettings;

	private Platform platform;

	private BuiltLibraries libraries;

	private CustomFilters filters;

	private Map moduleInfo;

	public Configuration()
	{
		properties = new Properties();
		projects = new Projects();
		moduleSettings = new Modules();
		pathSettings = new PathSettings();
		platform = new Platform();
		libraries = new BuiltLibraries();
		filters = new CustomFilters();

		moduleInfo = new HashMap();
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

	/**
	 * @deprecated Use getPlatformName() and getBuildDebugLevel().
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public Projects getProjects()
	{
		return projects;
	}

	public Modules getModuleSettings()
	{
		return moduleSettings;
	}

	public ModuleSettings getModuleSettings(String module)
	{
		return moduleSettings.getModule(module);
	}

	public String getModuleProperty(String module, String key, String def)
	{
		ModuleSettings ms = getModuleSettings(module);
		if (ms != null)
		{
			return ms.getProperty(key, def);
		}
		return def;
	}

	public int getModuleProperty(String module, String key, int def)
	{
		ModuleSettings ms = getModuleSettings(module);
		if (ms != null)
		{
			return ms.getProperty(key, def);
		}
		return def;
	}

	public boolean getModuleProperty(String module, String key, boolean def)
	{
		ModuleSettings ms = getModuleSettings(module);
		if (ms != null)
		{
			return ms.getProperty(key, def);
		}
		return def;
	}

	public PathSettings getPathSettings()
	{
		return pathSettings;
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

	/*
	 * public void setProjects(Projects projects) { this.projects = projects; }
	 * public void setModuleSettings(ModuleSettings moduleSettings) {
	 * this.moduleSettings = moduleSettings; } public void
	 * setPathSettings(PathSettings pathSettings) { this.pathSettings =
	 * pathSettings; } public void setPlatform(Platform platform) {
	 * this.platform = platform; } public void setLibraries(BuiltLibraries
	 * libraries) { this.libraries = libraries; } public void
	 * setFilters(CustomFilters filters) { this.filters = filters; }
	 */

	/**
	 * Returns the ModuleInfo instance for the given class.
	 */
	public ModuleInfo getModuleInfo(Class forClass)
	{
		if (moduleInfo.containsKey(forClass))
		{
			return (ModuleInfo) moduleInfo.get(forClass);
		}
		else
		{
			ModuleInfo mi;
			try
			{
				InputStream miXML = forClass.getResourceAsStream("moduleinfo.xml");
				if (miXML == null) return null;
				mi = ModuleInfo.load(miXML);
				moduleInfo.put(forClass, mi);
				moduleInfo.put(mi.getId(), mi);
				return mi;
			}
			catch (ConfigurationException e)
			{
				Debug.out(Debug.MODE_ERROR, "Configuration", "Exception while loading module info for " + forClass
						+ ": " + e.getMessage());
				return null;
			}
		}
	}
}
