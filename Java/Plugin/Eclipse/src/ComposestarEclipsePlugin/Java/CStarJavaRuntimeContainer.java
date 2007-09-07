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

package ComposestarEclipsePlugin.Java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.IComposestarConstants;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;

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
		List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>();
		for (String lib : IComposestarJavaConstants.COMPILETIME_LIBS)
		{
			IPath composestarLibPath = new Path(FileUtils.fixFilename(ComposestarEclipsePluginPlugin
					.getAbsolutePath(IComposestarConstants.BIN_DIR + lib)));
			cpEntries.add(JavaCore.newLibraryEntry(composestarLibPath, null, null, false));
		}
		return (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
	}

	public String getDescription()
	{
		return "Compose*/Java Runtime Libraries";
	}

	public int getKind()
	{
		return K_SYSTEM;
	}

	public IPath getPath()
	{
		return PATH;
	}

}
