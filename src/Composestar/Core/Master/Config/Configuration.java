package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class Configuration implements Serializable{
	
	private static Configuration Instance = null;
	private Properties properties;
	public Projects projects;
	public ModuleSettings moduleSettings;
	public PathSettings pathSettings;
	public Platform platform;
	public BuiltLibraries libraries;
	public CustomFilters filters;
		
	public Configuration() {
		properties = new Properties();
		moduleSettings = new ModuleSettings();
		pathSettings = new PathSettings();
		projects = new Projects();
		platform = new Platform();
		libraries = new BuiltLibraries();
		filters = new CustomFilters();
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
}
