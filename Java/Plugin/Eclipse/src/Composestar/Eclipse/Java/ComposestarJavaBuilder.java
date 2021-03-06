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

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import Composestar.Eclipse.Core.ComposestarBuilder;

/**
 * @author Michiel Hendriks
 */
public class ComposestarJavaBuilder extends ComposestarBuilder
{
	public ComposestarJavaBuilder()
	{
		pluginid = "composestar.java.plugin";
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		configGenerator = new JavaBuildConfigGenerator();
		return super.build(kind, args, monitor);
	}

	@Override
	protected void callMaster(IProgressMonitor monitor, File buildConfigFile) throws CoreException
	{
		MasterManager m = MasterManager.getInstance();
		m.run(currentProject, buildConfigFile, monitor);
		if (!m.completed)
		{
			throw new CoreException(new Status(IStatus.ERROR, pluginid, IResourceStatus.BUILD_FAILED,
					"Build failed. See the console output and problems view for more information.", null));
		}
	}
}
