package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

	/**
	 * Temporary storage of module settings
	 */
	private Map tmpModuleSettings;

	public Configuration()
	{
		properties = new Properties();
		projects = new Projects();
		moduleSettings = new Modules();
		pathSettings = new PathSettings();
		platform = new Platform();
		libraries = new BuiltLibraries();
		filters = new CustomFilters();

		tmpModuleSettings = new HashMap();
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

	public void addTmpModuleSettings(String moduleName, Map props)
	{
		tmpModuleSettings.put(moduleName, props);
	}

	public Map getTmpModuleSettings(String moduleName)
	{
		return (Map) tmpModuleSettings.get(moduleName);
	}
}
