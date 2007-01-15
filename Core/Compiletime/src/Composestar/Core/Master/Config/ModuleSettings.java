package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class ModuleSettings implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7909382618910056237L;

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

	/**
	 * @deprecated Do not access this directly; use
	 *             Configuration.getModuleProperty.
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	/**
	 * Do not access this directly; use Configuration.getModuleProperty.
	 */
	public String getProperty(String key, String def)
	{
		return properties.getProperty(key, def);
	}

	public int getProperty(String key, int def)
	{
		try
		{
			return Integer.parseInt(properties.getProperty(key, Integer.toString(def)));
		}
		catch (NumberFormatException e)
		{
			return def;
		}
	}

	public boolean getProperty(String key, boolean def)
	{
		return Boolean.valueOf(properties.getProperty(key, Boolean.toString(def)));
	}
}
