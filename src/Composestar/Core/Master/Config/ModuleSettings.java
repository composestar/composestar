package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class ModuleSettings implements Serializable
{
	private String name;
	private Properties properties;

	public ModuleSettings()
	{
		properties = new Properties();
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public String getProperty(String key, String def)
	{
		return properties.getProperty(key, def);
	}
}
