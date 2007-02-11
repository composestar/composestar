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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import Composestar.Utils.FileUtils;

public class Projects implements Serializable
{
	private static final long serialVersionUID = 4696163750932369815L;

	private Properties properties;

	private List<Project> allProjects;

	private List<ConcernSource> concernSources;

	private Map<String, List<Project>> projectsByLanguage;

	public Projects()
	{
		properties = new Properties();
		allProjects = new ArrayList<Project>();
		concernSources = new ArrayList<ConcernSource>();
		projectsByLanguage = new HashMap<String, List<Project>>();
	}

	public void setRunDebugLevel(int value)
	{
		properties.setProperty("runDebugLevel", "" + value);
	}

	public int getRunDebugLevel()
	{
		String level = properties.getProperty("runDebugLevel");
		if (level != null)
		{
			return Integer.parseInt(level);
		}

		return 0;
	}

	public void setOutputPath(String value)
	{
		properties.setProperty("outputPath", value);
	}

	public String getOutputPath()
	{
		return properties.getProperty("outputPath");
	}

	public void setApplicationStart(String value)
	{
		properties.setProperty("applicationStart", value);
	}

	public String getApplicationStart()
	{
		return properties.getProperty("applicationStart");
	}

	/**
	 * @deprecated Use
	 *             setRunDebugLevel/setOutputPath/setApplicationStart/setExecutable.
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	/**
	 * @deprecated Use
	 *             getRunDebugLevel/getOutputPath/getApplicationStart/getExecutable.
	 * @param key
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public void addConcernSource(ConcernSource concernsource)
	{
		concernSources.add(concernsource);
	}

	public List<ConcernSource> getConcernSources()
	{
		return concernSources;
	}

	public void addProject(Project proj)
	{
		allProjects.add(proj);

		String language = proj.getLanguageName();
		getProjectsByLanguage(language).add(proj);
	}

	public List<Project> getProjects()
	{
		return allProjects;
	}

	public List<Project> getProjectsByLanguage(String language)
	{
		List<Project> projects = projectsByLanguage.get(language);
		if (projects == null)
		{
			projects = new ArrayList<Project>();
			projectsByLanguage.put(language, projects);
		}

		return projects;
	}

	public List<String> getCompiledDummies()
	{
		List<String> compiledDummies = new ArrayList<String>();
		for (Project p : allProjects)
		{
			if (p.getCompiledDummies() != null)
			{
				compiledDummies.add(p.getCompiledDummies());
			}
		}
		return compiledDummies;
	}

	public List<String> getCompiledSources()
	{
		List<String> compiledSources = new ArrayList<String>();
		for (Project p : allProjects)
		{
			if (p.getCompiledSources() != null)
			{
				compiledSources.addAll(p.getCompiledSources());
			}
		}
		return compiledSources;
	}

	public List<Dependency> getDependencies()
	{
		List<Dependency> dependencies = new ArrayList<Dependency>();
		for (Project p : allProjects)
		{
			for (Dependency dependency : p.getDependencies())
			{
				if (!dependencies.contains(dependency))
				{
					dependencies.add(dependency);
				}
			}
		}
		return dependencies;
	}

	public Iterator<Dependency> dependencies()
	{
		Set<Dependency> depset = new HashSet<Dependency>();
		for (Project p : allProjects)
		{
			for (Dependency dependency : p.getDependencies())
			{
				depset.add(dependency);
			}
		}
		return depset.iterator();
	}

	public List<Source> getSources()
	{
		List<Source> sources = new ArrayList<Source>();
		for (Project p : allProjects)
		{
			sources.addAll(p.getSources());
		}
		return sources;
	}

	/**
	 * Returns the source instance for the specified filename. TODO: this can be
	 * implemented a lot more efficiently by using a map from filename to
	 * source.
	 * 
	 * @param fileName
	 */
	public Source getSource(String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("specified filename cannot be null");
		}

		// normalize specified filename
		String nfn = FileUtils.normalizeFilename(fileName);

		List sources = getSources();
		for (Object source : sources)
		{
			Source s = (Source) source;
			String sfn = s.getFileName();
			if (sfn == null)
			{
				continue; // FIXME: can this even happen? if so: how should it
				// be handled?
			}

			// normalize source filename
			String nsfn = FileUtils.normalizeFilename(sfn);

			// compare specified and source
			if (nfn.equals(nsfn))
			{
				return s;
			}
		}

		return null;
	}

	/**
	 * returns original source of binary if available
	 */
	public Source getSourceOfBinary(String binary)
	{
		for (Source s : this.getSources())
		{
			if (binary.indexOf(s.getTarget()) > 0)
			{
				return s;
			}
		}
		return null;
	}
}
