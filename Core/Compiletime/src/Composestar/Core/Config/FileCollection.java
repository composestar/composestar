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

/**
 * A collection of files.
 * 
 * @author Michiel Hendriks
 */
public class FileCollection implements Serializable
{
	private static final long serialVersionUID = -2135088525201523789L;

	/**
	 * Files selected by this source.
	 */
	protected Set<File> files;

	public FileCollection()
	{
		files = new HashSet<File>();
	}

	/**
	 * Returns the files associated with this source. The returned set is
	 * readonly and can contain relative paths.
	 * 
	 * @return
	 */
	public Set<File> getFiles()
	{
		return Collections.unmodifiableSet(files);
	}

	/**
	 * Return the files associated with this collection with all non absolute
	 * files set to the base. Does not filter for file existence.
	 * 
	 * @param base
	 * @return
	 */
	public Set<File> getFiles(File base)
	{
		Set<File> basedfiles = new HashSet<File>();
		for (File file : files)
		{
			if (!file.isAbsolute())
			{
				basedfiles.add(new File(base, file.toString()));
			}
			else
			{
				basedfiles.add(file);
			}
		}
		return basedfiles;
	}

	/**
	 * Add a new file
	 * 
	 * @param newFile
	 */
	public void addFile(String newFile)
	{
		if (newFile == null || newFile.trim().length() == 0)
		{
			return;
		}
		files.add(new File(newFile));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (File file : files)
		{
			if (sb.length() > 0)
			{
				sb.append(File.pathSeparatorChar);
			}
			sb.append(file.toString());
		}
		return sb.toString();
	}

}
