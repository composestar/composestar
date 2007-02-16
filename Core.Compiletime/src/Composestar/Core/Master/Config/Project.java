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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Project implements Serializable
{
	private static final long serialVersionUID = 8492654317711347755L;

	private Properties properties;

	private Language language;

	private List<Source> sources;

	private List<Dependency> dependencies;

	private List<TypeSource> typeSources;

	private String compiledDummies;

	private List<String> compiledSources;

	public Project()
	{
		properties = new Properties();
		language = new Language();
		sources = new ArrayList<Source>();
		dependencies = new ArrayList<Dependency>();
		typeSources = new ArrayList<TypeSource>();
		compiledSources = new ArrayList<String>();
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
	 */
	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	/**
	 * @deprecated Use getName/getLanguageName/getBasePath.
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public void addDependency(Dependency dep)
	{
		dependencies.add(dep);
	}

	public List<Dependency> getDependencies()
	{
		return dependencies;
	}

	public void addSource(Source source)
	{
		sources.add(source);
		source.setProject(this);
	}

	public List<TypeSource> getTypeSources()
	{
		return typeSources;
	}

	public void addTypeSource(TypeSource typesource)
	{
		typeSources.add(typesource);
	}

	public List<Source> getSources()
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

	public List<String> getCompiledSources()
	{
		return this.compiledSources;
	}
}
