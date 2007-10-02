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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;

import Composestar.Eclipse.Core.CStarProject;

/**
 * @author Michiel Hendriks
 */
public class CStarJavaProject extends CStarProject
{
	protected IJavaProject javaProject;

	public CStarJavaProject()
	{}

	@Override
	public void setProject(IProject project)
	{
		super.setProject(project);
		javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(project);
	}

	@Override
	public void configure() throws CoreException
	{
		super.configure();
		removeBuilder(JavaCore.BUILDER_ID);
		addBuilder(IComposestarJavaConstants.BUILDER_ID);
		addRuntimeLibraries();
	}

	@Override
	public void deconfigure() throws CoreException
	{
		super.deconfigure();
		removeBuilder(IComposestarJavaConstants.BUILDER_ID);
		addBuilder(JavaCore.BUILDER_ID);
	}

	protected void addRuntimeLibraries()
	{
		try
		{
			IClasspathEntry[] classpaths = javaProject.getRawClasspath();
			for (IClasspathEntry entry : classpaths)
			{
				if ((entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
						&& (CStarJavaRuntimeContainer.PATH.equals(entry.getPath())))
				{
					return;
				}
			}
			List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>(Arrays.asList(classpaths));
			cpEntries.add(JavaCore.newContainerEntry(CStarJavaRuntimeContainer.PATH));
			classpaths = cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
			javaProject.setRawClasspath(classpaths, new NullProgressMonitor());
		}
		catch (JavaModelException e)
		{
		}
	}
}
