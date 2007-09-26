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

import Composestar.Utils.Logging.CPSLogger;

/**
 * This class managed information about the source files.
 * 
 * @author Michiel Hendriks
 */
public class Source implements Serializable
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.Build");

	private static final long serialVersionUID = -2838432989846583032L;

	/**
	 * Language override. Not supported by most platforms.
	 */
	protected String language;

	/**
	 * The handle to the file of this source
	 */
	protected File file;

	/**
	 * True if this source file was an embedded source (extracted from a concern
	 * file). This will be set by EMBEX for extracted sources.
	 */
	protected boolean isEmbedded;

	/**
	 * Location to the stub/dummy created for this source entry. This value is
	 * set in DUMMER. And should be used by the Dummy emitters as target.
	 */
	protected File stub;

	/**
	 * The project this file belongs to.
	 */
	protected Project project;

	/**
	 * The assembly file this source has been compiled to. Might be set by the
	 * platform's compiler.
	 */
	protected File assembly;

	public Source()
	{}

	public String getLanguage()
	{
		return language;
	}

	/**
	 * Return the language override. Returns null when there is no override.
	 * 
	 * @param inLanguage
	 */
	public void setLanguage(String inLanguage)
	{
		if (inLanguage == null || inLanguage.trim().length() == 0)
		{
			language = null;
		}
		else
		{
			language = inLanguage;
		}
	}

	/**
	 * @return the isEmbedded
	 */
	public boolean isEmbedded()
	{
		return isEmbedded;
	}

	/**
	 * @param inIsEmbedded the isEmbedded to set
	 */
	public void setEmbedded(boolean inIsEmbedded)
	{
		isEmbedded = inIsEmbedded;
	}

	/**
	 * This will set the project value of this source. It will automatically
	 * remove the source from the previous project if set. To add a source to a
	 * project call Project.addSource(..) it will automatically set the project
	 * for this source file.
	 * 
	 * @param inProject
	 */
	public void setProject(Project inProject)
	{
		if (project == inProject)
		{
			return;
		}
		if (project != null)
		{
			project.removeSource(this);
		}
		project = inProject;
	}

	/**
	 * Get the assigned project
	 * 
	 * @return
	 */
	public Project getProject()
	{
		return project;
	}

	/**
	 * Return the file value which is resolved to an absolute path using the
	 * source's project (if set).
	 * 
	 * @return the file
	 */
	public File getFile()
	{
		if (project != null)
		{
			return getFile(project.getBase());
		}
		return file;
	}

	/**
	 * Return the raw file value.
	 * 
	 * @return
	 */
	public File getRawFile()
	{
		return file;
	}

	/**
	 * Resolve the relative filename to an absolute path using the provided
	 * base.
	 * 
	 * @param base
	 * @return
	 */
	public File getFile(File base)
	{
		if (!file.isAbsolute())
		{
			return new File(base, file.toString());
		}
		return file;
	}

	/**
	 * @param inFile the file to set
	 */
	public void setFile(File inFile)
	{
		if (inFile == null)
		{
			throw new IllegalArgumentException("File can not be null");
		}
		file = inFile;
	}

	/**
	 * Get the location of the stub/dummy. This path should always be absolute.
	 * 
	 * @return the stub
	 */
	public File getStub()
	{
		return stub;
	}

	/**
	 * @param inStub the stub to set
	 */
	public void setStub(File inStub)
	{
		if (file == inStub && file != null)
		{
			throw new IllegalArgumentException("Stub can not be identical to the source file");
		}
		if (!file.isAbsolute())
		{
			logger.warn(String.format("Stub file for %s is not absolute: %s", file, inStub));
		}
		stub = inStub;
	}

	public void setAssembly(File asm)
	{
		assembly = asm;
	}

	public File getAssembly()
	{
		return assembly;
	}
}
