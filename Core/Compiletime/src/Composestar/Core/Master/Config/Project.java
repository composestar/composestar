package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Project implements Serializable
{
	private Properties properties;
	//private String name;
	//private String language;
	//private String basePath;
	private Language language;
	private List sources;
	private List dependencies;
	private List typeSources;
	private String compiledDummies;
	private List compiledSources;

	public Project()
	{
		properties = new Properties();
		language = new Language();
		sources = new ArrayList();
		dependencies = new ArrayList();
		typeSources = new ArrayList();
		compiledSources = new ArrayList();
	}
	
	public void setName(String name)
	{
		properties.setProperty("name", name);
	}
	
	public String getName()
	{
		return properties.getProperty("name");
	}
	
	public void setLanguageName(String language)
	{
		properties.setProperty("language", language);
	}
	
	public String getLanguageName()
	{
		return properties.getProperty("language");
	}
	
	public void setBasePath(String basePath)
	{
		properties.setProperty("basePath", basePath);
	}
	
	public String getBasePath()
	{
		return properties.getProperty("basePath");
	}

	/**
	 * @deprecated Use setName/setLanguageName/setBasePath.
     * @param key
     * @param value
	 */ 
	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	/**
	 * @deprecated Use getName/getLanguageName/getBasePath.
     * @param key
	 */ 
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public void addDependency(Dependency dep)
	{
		dependencies.add(dep);
	}

	public List getDependencies()
	{
		return dependencies;
	}

	public void addSource(Source source)
	{
		sources.add(source);
		source.setProject(this);
	}

	public List getTypeSources()
	{
		return typeSources;
	}

	public void addTypeSource(TypeSource typesource)
	{
		typeSources.add(typesource);
	}

	public List getSources()
	{
		return sources;
	}

	public void setLanguage(Language lang)
	{
		this.language = lang;
	}

	public Language getLanguage()
	{
		return this.language;
	}

	public void setCompiledDummies(String fileName)
	{
		this.compiledDummies = fileName;
	}

	public String getCompiledDummies()
	{
		return compiledDummies;
	}

	public void addCompiledSource(String source)
	{
		compiledSources.add(source);
	}

	public List getCompiledSources()
	{
		return this.compiledSources;
	}
}
