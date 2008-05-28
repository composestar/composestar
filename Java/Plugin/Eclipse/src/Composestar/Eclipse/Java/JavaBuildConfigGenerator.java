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

package Composestar.Eclipse.Java;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Eclipse.Core.BuildConfigGenerator;
import Composestar.Eclipse.Core.Debug;
import Composestar.Eclipse.Core.IComposestarConstants;

/**
 * @author Michiel Hendriks
 */
public class JavaBuildConfigGenerator extends BuildConfigGenerator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Eclipse.Core.BuildConfigGenerator#addProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void addProject(IProject project) throws ConfigurationException
	{
		super.addProject(project);
		curProject.setPlatform("Java");
		curProject.setLanguage("Java");

		try
		{
			IJavaProject jproj = JavaCore.create(project);

			IType mctype = jproj.findType(mainclass);
			if (mctype == null)
			{
				throw new ConfigurationException(String.format("Mainclass %s could not be found in this project",
						mainclass));
			}

			IPath path = jproj.getOutputLocation();
			if (project.getFullPath().isPrefixOf(path))
			{
				path = path.removeFirstSegments(path.matchingFirstSegments(project.getFullPath()));
				curProject.setOutput(path.toString());
			}

			Map opt = jproj.getOptions(true);
			if (opt.containsKey(JavaCore.COMPILER_SOURCE))
			{
				config.addSetting("COMP.source", opt.get(JavaCore.COMPILER_SOURCE).toString());
			}
			if (opt.containsKey(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM))
			{
				config.addSetting("COMP.target", opt.get(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM).toString());
			}
			if (opt.containsKey(JavaCore.COMPILER_COMPLIANCE))
			{
				config.addSetting("COMP.compliance", opt.get(JavaCore.COMPILER_COMPLIANCE).toString());
			}

			addJavaSources(jproj);
			addJavaDependencies(jproj);
		}
		catch (JavaModelException jme)
		{
			Debug.instance().Log("Java Model Exception: " + jme.getMessage(), IComposestarConstants.MSG_ERROR);
		}
	}

	protected void addJavaDependencies(IJavaProject project) throws JavaModelException
	{
		IClasspathEntry[] classpaths = project.getRawClasspath();
		Set<IPath> deps = new HashSet<IPath>();
		for (IClasspathEntry element : classpaths)
		{
			// dependencies
			if (element.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
			{
				deps.add(getRelativeIPath(project.getPath(), element.getPath()));
			}
			else if (element.getEntryKind() == IClasspathEntry.CPE_VARIABLE)
			{
				IClasspathEntry entry = JavaCore.getResolvedClasspathEntry(element);
				if (entry != null)
				{
					deps.add(getRelativeIPath(project.getPath(), entry.getPath()));
				}
			}
			else if (element.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
			{
				IClasspathContainer con = JavaCore.getClasspathContainer(element.getPath(), project);
				if (con.getKind() != IClasspathContainer.K_DEFAULT_SYSTEM)
				{
					IClasspathEntry[] concps = con.getClasspathEntries();
					for (IClasspathEntry cp : concps)
					{
						deps.add(getRelativeIPath(project.getPath(), cp.getPath()));
					}
				}
			}
		}
		addFileDependencies(deps);
	}

	protected void addJavaSources(IJavaProject project) throws JavaModelException
	{
		Set<IPath> sources = new HashSet<IPath>();
		IPackageFragment[] pkgs = project.getPackageFragments();
		for (IPackageFragment pkg : pkgs)
		{
			ICompilationUnit[] cus = pkg.getCompilationUnits();
			for (ICompilationUnit cu : cus)
			{
				sources.add(getRelativeIPath(project.getPath(), cu.getPath()));
			}
		}
		addSources(sources);
	}

}
