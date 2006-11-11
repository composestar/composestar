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
	/**
	 * 
	 */
	private static final long serialVersionUID = 4696163750932369815L;

	private Properties properties;

	private List allProjects;

	private List concernSources;

	private Map projectsByLanguage;

	// private List dependencies;
	// private List compiledDummies;
	// private List compiledSources;

	public Projects()
	{
		properties = new Properties();
		allProjects = new ArrayList();
		concernSources = new ArrayList();
		projectsByLanguage = new HashMap();

		// dependencies = new ArrayList();
		// compiledDummies = new ArrayList();
		// compiledSources = new ArrayList();
	}

	public void setRunDebugLevel(int value)
	{
		properties.setProperty("runDebugLevel", "" + value);
	}

	public int getRunDebugLevel()
	{
		String level = properties.getProperty("runDebugLevel");
		return Integer.parseInt(level);
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

	public List getConcernSources()
	{
		return concernSources;
	}

	public void addProject(Project proj)
	{
		allProjects.add(proj);

		String language = proj.getLanguageName();
		getProjectsByLanguage(language).add(proj);
	}

	public List getProjects()
	{
		return allProjects;
	}

	public List getProjectsByLanguage(String language)
	{
		List projects = (List) projectsByLanguage.get(language);
		if (projects == null)
		{
			projectsByLanguage.put(language, projects = new ArrayList());
		}

		return projects;
	}

	public List getCompiledDummies()
	{
		List compiledDummies = new ArrayList();
		Iterator projIt = allProjects.iterator();
		while (projIt.hasNext())
		{
			Project p = (Project) projIt.next();
			if (p.getCompiledDummies() != null)
			{
				compiledDummies.add(p.getCompiledDummies());
			}
		}
		return compiledDummies;
	}

	public List getCompiledSources()
	{
		List compiledSources = new ArrayList();
		Iterator projIt = allProjects.iterator();
		while (projIt.hasNext())
		{
			Project p = (Project) projIt.next();
			if (p.getCompiledSources() != null)
			{
				compiledSources.addAll(p.getCompiledSources());
			}
		}
		return compiledSources;
	}

	public List getDependencies()
	{
		List dependencies = new ArrayList();
		Iterator projIt = allProjects.iterator();
		while (projIt.hasNext())
		{
			Project p = (Project) projIt.next();
			List deps = p.getDependencies();
			Iterator depIt = deps.iterator();
			while (depIt.hasNext())
			{
				Dependency dependency = (Dependency) depIt.next();
				if (!dependencies.contains(dependency))
				{
					dependencies.add(dependency);
				}
			}
		}
		return dependencies;
	}

	public Iterator dependencies()
	{
		Set depset = new HashSet();
		Iterator projIt = allProjects.iterator();
		while (projIt.hasNext())
		{
			Project p = (Project) projIt.next();
			List deps = p.getDependencies();
			Iterator depIt = deps.iterator();
			while (depIt.hasNext())
			{
				Dependency dependency = (Dependency) depIt.next();
				depset.add(dependency);
			}
		}
		return depset.iterator();
	}

	public List getSources()
	{
		List sources = new ArrayList();
		Iterator projIt = allProjects.iterator();
		while (projIt.hasNext())
		{
			Project p = (Project) projIt.next();
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
		Iterator sourceIt = sources.iterator();
		while (sourceIt.hasNext())
		{
			Source s = (Source) sourceIt.next();
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
		Iterator sourceItr = this.getSources().iterator();
		while(sourceItr.hasNext())
		{
			Source s = (Source)sourceItr.next();
			
			if(binary.indexOf(s.getTarget())>0)
			{
				return s;
			}
		}
			
		return null;
	}
}
