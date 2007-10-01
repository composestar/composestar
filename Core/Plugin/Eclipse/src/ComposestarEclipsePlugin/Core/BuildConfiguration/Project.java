package ComposestarEclipsePlugin.Core.BuildConfiguration;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Class representing a project. Used by BuildConfigurationManager to build
 * configuration-file.
 */
@Deprecated
public class Project
{

	private Properties properties;

	private ArrayList dependencies;

	private ArrayList sources;

	private ArrayList typeSources;

	private String language = "";

	public Project()
	{
		properties = new Properties();
		dependencies = new ArrayList();
		sources = new ArrayList();
		typeSources = new ArrayList();
	}

	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public String getProperty(String key)
	{
		if (properties.containsKey(key))
		{
			return properties.getProperty(key);
		}
		return null;
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void addDependency(String dep)
	{
		dependencies.add(dep);
	}

	public ArrayList getDependencies()
	{
		return dependencies;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String lang)
	{
		this.language = lang;
	}

	public void addSource(String source)
	{
		sources.add(source);
	}

	public ArrayList getSources()
	{
		return sources;
	}

	public ArrayList getTypeSources()
	{
		return typeSources;
	}

	public void addTypeSource(TypeSource typesource)
	{
		typeSources.add(typesource);
	}
}
