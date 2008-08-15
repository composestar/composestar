/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.CpsRepository2.Meta;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * This class keeps records of source files. It is shared between multiple
 * SourceInformation instances.
 * 
 * @author Michiel Hendriks
 */
public final class FileInformation implements Serializable
{
	private static final long serialVersionUID = 3394426163184154737L;

	/**
	 * The location to the source file.
	 */
	private File location;

	/**
	 * The last modification timestamp of the file when it was last processed.
	 * It can be used to check if the in-memory data is out of date with the
	 * on-disk data.
	 */
	private long lastModified;

	/**
	 * Constructs a new FileInformation instance using the provided file as
	 * source for information.
	 * 
	 * @param file
	 * @see #update(File)
	 */
	public FileInformation(File file)
	{
		update(file);
	}

	/**
	 * Update the file information. This will set the location to the provided
	 * file and update the lastModified value.
	 * 
	 * @param file the file to update the information with
	 * @throws NullPointerException when file is null
	 */
	public void update(File file) throws NullPointerException
	{
		if (file == null)
		{
			throw new NullPointerException("file can not be null");
		}
		location = file;
		lastModified = file.lastModified();
	}

	/**
	 * @return the stored location
	 */
	public File getLocation()
	{
		return location;
	}

	/**
	 * @return the stored last modified value
	 */
	public long getLastModified()
	{
		return lastModified;
	}

	/**
	 * @return true if the last modified value of the file is higher than the
	 *         stored last modified value, or if the stored location no longer
	 *         exists.
	 */
	public boolean isOutdated()
	{
		if (location == null)
		{
			throw new IllegalStateException("location is null");
		}
		long lastMod = location.lastModified();
		return lastMod == 0 || lastMod > lastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final DateFormat format = DateFormat.getDateInstance();
		return String.format("%s [%s]", location, format.format(new Date(lastModified)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lastModified ^ lastModified >>> 32);
		result = prime * result + (location == null ? 0 : location.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final FileInformation other = (FileInformation) obj;
		if (lastModified != other.lastModified)
		{
			return false;
		}
		if (location == null)
		{
			if (other.location != null)
			{
				return false;
			}
		}
		else if (!location.equals(other.location))
		{
			return false;
		}
		return true;
	}

}
