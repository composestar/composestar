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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import Composestar.Core.Exception.ConfigurationException;

/**
 * @author Michiel Hendriks
 */
public abstract class ComposestarBuilder extends IncrementalProjectBuilder
{
	protected IProject currentProject;

	protected Set<IPath> sources;

	protected Set<IPath> concerns;

	protected BuildConfigGenerator configGenerator;

	protected String pluginid;

	public ComposestarBuilder()
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible())
		{
			return new IProject[0];
		}

		switch (kind)
		{
			case INCREMENTAL_BUILD:
			case FULL_BUILD:
				fullBuild(monitor);
				break;
		}
		return null;
	}

	protected IProject[] fullBuild(IProgressMonitor monitor) throws CoreException
	{
		if (configGenerator == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, pluginid, IResourceStatus.BUILD_FAILED,
					"No configuration generator", new NullPointerException()));
		}

		// quick escape, project can't be build because there are no sources
		if (!configGenerator.projectHasSources(currentProject))
		{
			return null;
		}

		monitor.beginTask("Compiling", 3);
		try
		{
			configGenerator.addProject(currentProject);
		}
		catch (ConfigurationException e)
		{
			throw new CoreException(
					new Status(IStatus.ERROR, pluginid, IResourceStatus.BUILD_FAILED, e.getMessage(), e));
		}

		IPath binPath = configGenerator.getOutputDir();
		if (binPath != null)
		{
			IFolder binFolder = currentProject.getFolder(binPath);
			if (!binFolder.exists())
			{
				binFolder.create(false, true, monitor);
			}
		}

		IFolder binFolder = currentProject.getFolder(BuildConfigGenerator.INTERMEDIATE_DIR);
		if (!binFolder.exists())
		{
			binFolder.create(false, true, monitor);
		}
		binFolder.setDerived(true);

		monitor.worked(1);
		File buildConfigFile = new File(currentProject.getLocation().toFile(), "BuildConfiguration.xml");
		if (!configGenerator.generate(buildConfigFile))
		{
			// throw CoreException
			return null;
		}
		IFile configFile = currentProject.getFile("BuildConfiguration.xml");
		if (configFile.exists())
		{
			configFile.setDerived(true);
		}
		monitor.worked(1);
		callMaster(monitor, buildConfigFile);
		monitor.worked(1);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException
	{
		if (getProject() != null)
		{
			IFolder binFolder = getProject().getFolder(BuildConfigGenerator.INTERMEDIATE_DIR);
			if (binFolder.exists())
			{
				binFolder.delete(true, false, monitor);
			}
		}
		super.clean(monitor);
	}

	protected abstract void callMaster(IProgressMonitor monitor, File buildConfigFile) throws CoreException;
}
