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

/**
 * Base class for dependencies. Doubles as the file dependency. The meaning of
 * the dependency is subject to the platform that uses it. For example .NET 1.1
 * only refers to assemblies by means of a filename. Java can refer to either a
 * jar file or a class directory.
 * 
 * @author Michiel Hendriks
 */
public class Dependency implements Serializable
{
	private static final long serialVersionUID = 1125518139523979925L;

	/**
	 * Handle to the file dependency
	 */
	protected File file;

	protected Dependency()
	{}

	/**
	 * Create a file dependency
	 * 
	 * @param inFile
	 */
	public Dependency(File inFile)
	{
		file = inFile;
	}

	/**
	 * Get the handle to the file this dependency refers to. The value could be
	 * null when the dependency is not related to a file.
	 * 
	 * @return
	 */
	public File getFile()
	{
		return file;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (file != null)
		{
			return file.toString();
		}
		return super.toString();
	}

}
