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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import Composestar.Eclipse.Core.ComposestarEclipsePluginPlugin;
import Composestar.Eclipse.Core.IComposestarConstants;
import Composestar.Eclipse.Core.Utils.FileUtils;

/**
 * Runtime classpath container
 * 
 * @author Michiel Hendriks
 */
public class CStarJavaRuntimeContainer implements IClasspathContainer
{
	public static final String NAME = IComposestarJavaConstants.CP_CONTAINER + ".runtime";

	public static final IPath PATH = new Path(NAME);

	public IClasspathEntry[] getClasspathEntries()
	{
		String[] cp = getClasspath();
		IClasspathEntry[] cpEntries = new IClasspathEntry[cp.length];
		for (int i = 0; i < cp.length; i++)
		{
			cpEntries[i] = JavaCore.newLibraryEntry(new Path(cp[i]), null, null, false);
		}
		return cpEntries;
	}

	/**
	 * Return the classpath entries
	 */
	public String[] getClasspath()
	{
		String[] cp = new String[3];
		// Compiletime Core
		// TODO: shouldn't be needed
		cp[0] = FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath(IComposestarConstants.LIB_DIR
				+ "ComposestarCORE.jar"));

		// Compiletime Java
		// TODO: shouldn't be needed
		cp[1] = FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath(IComposestarConstants.LIB_DIR
				+ "ComposestarJava.jar", IComposestarJavaConstants.BUNDLE_ID));

		// Runtime Java
		cp[2] = FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath(IComposestarConstants.LIB_DIR
				+ "ComposestarRuntimeInterpreter.jar", IComposestarJavaConstants.BUNDLE_ID));

		// // TODO: shouldn't be needed
		// cp[3] =
		// FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath(IComposestarConstants.LIB_DIR
		// + "prolog.jar"));

		return cp;
	}

	public String getDescription()
	{
		return "Compose*/Java Runtime Libraries";
	}

	public int getKind()
	{
		return K_APPLICATION;
	}

	public IPath getPath()
	{
		return PATH;
	}

}
