package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class Configuration implements Serializable
{
	private static Configuration s_instance = null;

	private Properties properties;
	private Projects projects;
	private ModuleSettings moduleSettings;
	private PathSettings pathSettings;
	private Platform platform;
	private BuiltLibraries libraries;
	private CustomFilters filters;

	public Configuration()
	{
		properties = new Properties();
		projects = new Projects();
		moduleSettings = new ModuleSettings();
		pathSettings = new PathSettings();
		platform = new Platform();
		libraries = new BuiltLibraries();
		filters = new CustomFilters();
	}

	public static Configuration instance()
	{
		if (s_instance == null)
			s_instance = new Configuration();

		return s_instance;
	}

	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public String getProperty(String key)
	{
		if (properties.containsKey(key)) return properties.getProperty(key);
		return null;
	}

	public Projects getProjects()
	{
		return projects;
	}

	public ModuleSettings getModuleSettings()
	{
		return moduleSettings;
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
	public void setProjects(Projects projects)
	{
		this.projects = projects;
	}

	public void setModuleSettings(ModuleSettings moduleSettings)
	{
		this.moduleSettings = moduleSettings;
	}

	public void setPathSettings(PathSettings pathSettings)
	{
		this.pathSettings = pathSettings;
	}

	public void setPlatform(Platform platform)
	{
		this.platform = platform;
	}

	public void setLibraries(BuiltLibraries libraries)
	{
		this.libraries = libraries;
	}

	public void setFilters(CustomFilters filters)
	{
		this.filters = filters;
	}
*/
}
