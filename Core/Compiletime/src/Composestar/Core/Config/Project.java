/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Config;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Michiel Hendriks
 */
public class Project implements Serializable
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.Build.Project");

	private static final long serialVersionUID = 8368201695632416337L;

	/**
	 * The default value for the output path
	 */
	protected static final String DEFAULT_OUTPUT = "bin";

	/**
	 * The default value for the intermediate path
	 */
	protected static final String DEFAULT_INTERMEDIATE = "obj";

	/**
	 * The name of the project
	 */
	protected String name;

	/**
	 * The platform identifier
	 */
	protected String platformId;

	/**
	 * The platform used for this project. Will be set after the first call to
	 * getPlatform().
	 */
	protected Platform platform;

	/**
	 * The base path to the project
	 */
	protected File base;

	/**
	 * The default programming language of the project
	 */
	protected String language;

	/**
	 * The name of the main executable.
	 */
	protected String mainclass;

	/**
	 * The output directory for the compiler results.
	 */
	protected String output = DEFAULT_OUTPUT;

	protected transient File outputPath;

	/**
	 * The directory to store the intermediate results
	 */
	protected String intermediate = DEFAULT_INTERMEDIATE;

	protected transient File intermediatePath;

	/**
	 * The concern files associated with this project. All files have been
	 * completely resolved and verified for existence.
	 */
	protected Set<File> concernFiles;

	protected Set<File> disabledConcernFiles;

	/**
	 * List of sources associated with the project.
	 */
	protected Set<Source> sources;

	/**
	 * Cache with all source files
	 */
	protected transient Set<File> sourceFiles;

	/**
	 * List of project dependencies.
	 */
	protected Set<Dependency> dependencies;

	/**
	 * Cache with file dependencies (based on the dependencies list);
	 */
	protected transient Set<File> fileDependencies;

	/**
	 * Additional project resources that also need to be copied to the output.
	 */
	protected Set<FileCollection> resources;

	/**
	 * Cache with resolved resource files.
	 */
	protected transient Set<File> resourceFiles;

	public Project()
	{
		sources = new HashSet<Source>();
		concernFiles = new HashSet<File>();
		disabledConcernFiles = new HashSet<File>();
		dependencies = new HashSet<Dependency>();
		resources = new HashSet<FileCollection>();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		name = inName.trim();
	}

	public Platform getPlatform()
	{
		if (platform == null)
		{
			platform = PlatformManager.getPlatform(platformId);
		}
		return platform;
	}

	public String getPlatformId()
	{
		return platformId;
	}

	public void setPlatform(String inPlatform)
	{
		if (inPlatform == null || inPlatform.trim().length() == 0)
		{
			throw new IllegalArgumentException("Platform can not be null or empty");
		}
		platformId = inPlatform.trim();
		platform = null;
	}

	public File getBase()
	{
		return base;
	}

	public void setBase(String inBase)
	{
		if (inBase == null || inBase.trim().length() == 0)
		{
			throw new IllegalArgumentException("Base can not be null or empty");
		}
		setBase(new File(inBase.trim()));
	}

	public void setBase(File inBase)
	{
		if (!inBase.isDirectory())
		{
			throw new IllegalArgumentException("Base is not a directory");
		}
		base = inBase;
		if (!base.isAbsolute())
		{
			base = base.getAbsoluteFile();
			logger.warn("Project base is not absolute. Assuming: " + base.toString());
		}
		outputPath = null;
		intermediatePath = null;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String inLanguage)
	{
		if (inLanguage == null || inLanguage.trim().length() == 0)
		{
			throw new IllegalArgumentException("Language can not be null or empty");
		}
		language = inLanguage.trim();
	}

	public File getOutput()
	{
		if (outputPath == null)
		{
			outputPath = new File(base, output);
		}
		return outputPath;
	}

	public void setOutput(String inOutput)
	{
		if (inOutput == null || inOutput.trim().length() == 0)
		{
			output = DEFAULT_OUTPUT;
		}
		else
		{
			output = inOutput.trim();
		}
		outputPath = null;
	}

	public File getIntermediate()
	{
		if (intermediatePath == null)
		{
			intermediatePath = new File(base, intermediate);
		}
		return intermediatePath;
	}

	public void setIntermediate(String inIntermediate)
	{
		if (inIntermediate == null || inIntermediate.trim().length() == 0)
		{
			intermediate = DEFAULT_INTERMEDIATE;
		}
		else
		{
			intermediate = inIntermediate.trim();
		}
		inIntermediate = null;
	}

	public String getMainclass()
	{
		return mainclass;
	}

	public void setMainclass(String inMainclass)
	{
		if (inMainclass == null || inMainclass.trim().length() == 0)
		{
			throw new IllegalArgumentException("Mainclass can not be null or empty");
		}
		mainclass = inMainclass.trim();
	}

	public void addConcern(String inConcern)
	{
		addConcern(inConcern, true);
	}

	public void addConcern(String inConcern, boolean isEnabled)
	{
		if (inConcern == null)
		{
			return;
		}
		inConcern = inConcern.trim();
		if (inConcern.length() == 0)
		{
			return;
		}
		File concernFile = new File(inConcern);
		if (!concernFile.isAbsolute())
		{
			concernFile = new File(base, inConcern);
		}
		addConcern(concernFile, isEnabled);
	}

	public void addConcern(File inConcern)
	{
		addConcern(inConcern, true);
	}

	public void addConcern(File inConcern, boolean isEnabled)
	{
		if (inConcern == null)
		{
			throw new IllegalArgumentException("Concern can not be null");
		}
		if (!inConcern.exists())
		{
			logger.warn("Concern file does not exist: " + inConcern.toString());
		}
		else
		{
			if (isEnabled)
			{
				concernFiles.add(inConcern);
			}
			else
			{
				disabledConcernFiles.add(inConcern);
			}
		}
	}

	public Set<File> getConcernFiles()
	{
		return Collections.unmodifiableSet(concernFiles);
	}

	public Set<File> getDisabledConcernFiles()
	{
		return Collections.unmodifiableSet(disabledConcernFiles);
	}

	public void addSource(Source source)
	{
		if (source == null)
		{
			return;
		}
		sources.add(source);
		sourceFiles = null;
	}

	public void removeSource(Source source)
	{
		if (sources.remove(source))
		{
			sourceFiles = null;
		}
	}

	public void addSources(Set<Source> inSources)
	{
		if (inSources == null)
		{
			return;
		}
		if (sources.addAll(inSources))
		{
			sourceFiles = null;
		}
	}

	/**
	 * Return a list of fully resolved source files for this project
	 * 
	 * @return
	 */
	public Set<File> getSourceFiles()
	{
		if (sourceFiles == null)
		{
			sourceFiles = new HashSet<File>();
			for (Source src : sources)
			{
				for (File file : src.getFiles())
				{
					if (!file.isAbsolute())
					{
						file = new File(base, file.toString());
					}
					if (!file.exists())
					{
						logger.warn("Source file does not exist: " + file.toString());
					}
					else
					{
						sourceFiles.add(file);
					}
				}
			}
		}
		return Collections.unmodifiableSet(sourceFiles);
	}

	/**
	 * Return the source files for a given language. Not cached.
	 * 
	 * @param forLanguage
	 * @return
	 */
	public Set<File> getSourceFiles(String forLanguage)
	{
		if (forLanguage == null)
		{
			throw new IllegalArgumentException("Language can not be null");
		}
		forLanguage = forLanguage.trim();
		if (forLanguage.length() == 0)
		{
			forLanguage = language;
		}
		Set<File> files = new HashSet<File>();
		for (Source src : sources)
		{
			if (src.getLanguage() == null) // uses default language
			{
				if (!language.equals(forLanguage))
				{
					// default language was not requested
					continue;
				}
			}
			else if (!forLanguage.equals(src.getLanguage()))
			{
				// doesn't match language
				continue;
			}
			for (File file : src.getFiles(base))
			{
				if (!file.exists())
				{
					logger.warn("Source file does not exist: " + file.toString());
				}
				else
				{
					files.add(file);
				}
			}
		}
		return Collections.unmodifiableSet(files);
	}

	/**
	 * @return a readonly copy of the sources list.
	 */
	public Set<Source> getSources()
	{
		return Collections.unmodifiableSet(sources);
	}

	public void addDependency(Dependency dep)
	{
		if (dep == null)
		{
			return;
		}
		dependencies.add(dep);
		if (dep.getFile() != null)
		{
			fileDependencies = null;
		}
	}

	public void removeDependency(Dependency dep)
	{
		if (dep == null)
		{
			return;
		}
		dependencies.remove(dep);
		if (dep.getFile() != null)
		{
			fileDependencies = null;
		}
	}

	public void addDependencies(Set<Dependency> deps)
	{
		if (deps == null)
		{
			return;
		}
		if (dependencies.addAll(deps))
		{
			fileDependencies = null;
		}
	}

	/**
	 * @return a readonly copy of the sources list.
	 */
	public Set<Dependency> getDependencies()
	{
		return Collections.unmodifiableSet(dependencies);
	}

	/**
	 * @return a readonly copy of the file dependencies.
	 */
	public Set<File> getFilesDependencies()
	{
		if (fileDependencies == null)
		{
			fileDependencies = new HashSet<File>();
			for (Dependency dep : dependencies)
			{
				File file = dep.getFile();
				if (file != null)
				{
					if (!file.isAbsolute())
					{
						file = new File(base, file.toString());
					}
					if (!file.exists())
					{
						logger.warn("File dependency does not exist: " + file.toString());
					}
					else
					{
						fileDependencies.add(file);
					}
				}
			}
		}
		return Collections.unmodifiableSet(fileDependencies);
	}

	public void addResource(FileCollection res)
	{
		if (res == null)
		{
			return;
		}
		resources.add(res);
		resourceFiles = null;
	}

	public void removeResource(FileCollection res)
	{
		if (resources.remove(res))
		{
			resourceFiles = null;
		}
	}

	public void addResources(Set<FileCollection> res)
	{
		if (res == null)
		{
			return;
		}
		if (resources.addAll(res))
		{
			resourceFiles = null;
		}
	}

	/**
	 * Return a list of fully resolved source files for this project
	 * 
	 * @return
	 */
	public Set<File> getResourceFiles()
	{
		if (resourceFiles == null)
		{
			resourceFiles = new HashSet<File>();
			for (FileCollection col : resources)
			{
				for (File file : col.getFiles(base))
				{
					if (!file.exists())
					{
						logger.warn("Resource file does not exist: " + file.toString());
					}
					else
					{
						resourceFiles.add(file);
					}
				}
			}
		}
		return Collections.unmodifiableSet(resourceFiles);
	}

}
