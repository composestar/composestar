package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class Configuration implements Serializable
{	
	private static Configuration Instance = null;
	private Properties properties;
	private Projects projects;
	private ModuleSettings moduleSettings;
	private PathSettings pathSettings;
	private Platform platform;
	private BuiltLibraries libraries;
	private CustomFilters filters;
		
	public Configuration() {
		properties = new Properties();
		setModuleSettings(new ModuleSettings());
		setPathSettings(new PathSettings());
		setProjects(new Projects());
		setPlatform(new Platform());
		setLibraries(new BuiltLibraries());
		setFilters(new CustomFilters());
	}
		
	public static Configuration instance()
	{
		if (Instance == null) 
		{
			Instance = new Configuration();
		}
		return (Instance);
	} 
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}

	public void setProjects(Projects projects) {
		this.projects = projects;
	}

	public Projects getProjects() {
		return projects;
	}

	public void setModuleSettings(ModuleSettings moduleSettings) {
		this.moduleSettings = moduleSettings;
	}

	public ModuleSettings getModuleSettings() {
		return moduleSettings;
	}

	public void setPathSettings(PathSettings pathSettings) {
		this.pathSettings = pathSettings;
	}

	public PathSettings getPathSettings() {
		return pathSettings;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setLibraries(BuiltLibraries libraries) {
		this.libraries = libraries;
	}

	public BuiltLibraries getLibraries() {
		return libraries;
	}

	public void setFilters(CustomFilters filters) {
		this.filters = filters;
	}

	public CustomFilters getFilters() {
		return filters;
	}
}
