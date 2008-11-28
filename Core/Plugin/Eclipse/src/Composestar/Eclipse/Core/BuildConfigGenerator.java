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

package Composestar.Eclipse.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.Dependency;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Config.Xml.Output.BuildConfigXmlWriter;
import Composestar.Core.Exception.ConfigurationException;

/**
 * Produces a build configuration (v2.0) file from an eclipse project
 * 
 * @author Michiel Hendriks
 */
public abstract class BuildConfigGenerator
{
	protected BuildConfig config;

	protected Project curProject;

	protected String mainclass;

	protected IPath outputDir;

	public static final String INTERMEDIATE_DIR = ".composestar";

	public BuildConfigGenerator()
	{
		config = new BuildConfig();
	}

	/**
	 * Write the configuration to the disk
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean generate(File destination)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(destination);
			BuildConfigXmlWriter.write(config, fos);
			return true;
		}
		catch (FileNotFoundException e)
		{
			// TODO nice error?
			return false;
		}
	}

	public boolean projectHasSources(IProject project) throws CoreException
	{
		return true;
	}

	public void addProject(IProject project) throws ConfigurationException
	{
		curProject = config.getNewProject();
		curProject.setName(project.getName());
		curProject.setBase(project.getLocation().toFile());
		addConcerns(getConcerns(project));

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences settings = projectScope.getNode(IComposestarConstants.BUNDLE_ID);
		if (settings != null)
		{
			mainclass = settings.get("mainclass", null);
			try
			{
				curProject.setMainclass(mainclass);
			}
			catch (IllegalArgumentException e)
			{
				throw new ConfigurationException(
						"Mainclass setting is not set. Set the mainclass in the project properties in the 'Compose*' section.");
			}
			try
			{
				for (String key : settings.keys())
				{
					if ("mainclass".equals(key))
					{
						continue;
					}
					config.addSetting(key, settings.get(key, null));
				}
			}
			catch (BackingStoreException e)
			{
				// irrelevant
			}
		}
	}

	/**
	 * Returns a relative IPath
	 * 
	 * @param path
	 * @return
	 */
	public IPath getRelativeIPath(IPath base, IPath path)
	{
		if (!path.isAbsolute())
		{
			return path;
		}
		if (base.isPrefixOf(path))
		{
			return path.removeFirstSegments(path.matchingFirstSegments(base));
		}
		return path;
	}

	protected Set<IPath> getConcerns(IProject project)
	{
		// TODO it might be better to provide some way of retrieving these files
		// through the CStarProject
		Set<IPath> concerns = new HashSet<IPath>();
		try
		{
			Stack<IResource> resources = new Stack<IResource>();
			resources.addAll(Arrays.asList(project.members()));
			while (resources.size() > 0)
			{
				IResource res = resources.pop();
				if (!res.exists())
				{
					continue;
				}
				if (res.getType() == IResource.FOLDER)
				{
					resources.addAll(Arrays.asList(((IFolder) res).members()));
				}
				else if (res.getType() == IResource.FILE)
				{
					if ("cps".equals(res.getFullPath().getFileExtension()))
					{
						concerns.add(getRelativeIPath(project.getFullPath(), res.getFullPath()));
					}
				}
			}
		}
		catch (CoreException e)
		{
			Debug.instance().Log("Error retrieving concerns: " + e.getMessage(), IComposestarConstants.MSG_ERROR);
		}
		return concerns;
	}

	protected void addConcerns(Set<IPath> concerns)
	{
		for (IPath concern : concerns)
		{
			curProject.addConcern(concern.toFile());
		}
	}

	protected void addSources(Set<IPath> sources)
	{
		for (IPath source : sources)
		{
			Source src = new Source();
			src.setFile(source.toFile());
			curProject.addSource(src);
		}
	}

	protected void addFileDependencies(Set<IPath> deps)
	{
		for (IPath dep : deps)
		{
			Dependency filedep = new Dependency(dep.toFile());
			curProject.addDependency(filedep);
		}
	}

	public IPath getOutputDir()
	{
		return outputDir;
	}

	public String getIntermediateDir()
	{
		return INTERMEDIATE_DIR;
	}
}
