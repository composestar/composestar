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

/**
 * Contains information about where in the source a given repository entity was
 * declared.
 * 
 * @author Michiel Hendriks
 */
public final class SourceInformation implements Serializable
{
	private static final long serialVersionUID = 5219756477301344894L;

	/**
	 * A reference to the file information that makes this source information
	 * complete. This reference is not unique to a SourceInformation instance.
	 */
	private FileInformation fileInfo;

	/**
	 * The line number where the entity was declared. A negative value means
	 * that the information is unknown.
	 */
	private int line = -1;

	/**
	 * The position on the line (or column) where this entity was declared.A
	 * negative value means that the information is unknown.
	 */
	private int linePos = -1;

	/**
	 * Create a new source information instance using the provide
	 * FileInformation instance.
	 * 
	 * @param file
	 * @throws NullPointerException when no file information instance was given.
	 */
	public SourceInformation(FileInformation file) throws NullPointerException
	{
		if (file == null)
		{
			throw new NullPointerException("A FileInformation instance is required");
		}
		fileInfo = file;
	}

	/**
	 * Create a clone of the given source information
	 * 
	 * @param srcInfo
	 * @throws IllegalArgumentException when no source information instance was
	 *             given.
	 */
	public SourceInformation(SourceInformation srcInfo) throws NullPointerException
	{
		if (srcInfo == null)
		{
			throw new NullPointerException("A SourceInformation instance is required");
		}
		fileInfo = srcInfo.fileInfo;
		line = srcInfo.line;
		linePos = srcInfo.linePos;
	}

	/**
	 * @return the associated file information instance
	 */
	public FileInformation getFileInfo()
	{
		return fileInfo;
	}

	/**
	 * @return the source filename
	 * @see FileInformation#getLocation()
	 */
	public File getFilename()
	{
		return fileInfo.getLocation();
	}

	/**
	 * @return the line
	 * @see #line
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * @param line the line to set
	 * @see #line
	 */
	public void setLine(int line)
	{
		this.line = line;
	}

	/**
	 * @return the linePos
	 * @see #linePos
	 */
	public int getLinePos()
	{
		return linePos;
	}

	/**
	 * @param linePos the linePos to set
	 * @see #linePos
	 */
	public void setLinePos(int linePos)
	{
		this.linePos = linePos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getFilename());
		if (line > -1)
		{
			sb.append(" (");
			sb.append(line);
			if (linePos > -1)
			{
				sb.append(":");
				sb.append(linePos);
			}
			sb.append(")");
		}
		return sb.toString();
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
		result = prime * result + (fileInfo == null ? 0 : fileInfo.hashCode());
		result = prime * result + line;
		result = prime * result + linePos;
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
		final SourceInformation other = (SourceInformation) obj;
		if (fileInfo == null)
		{
			if (other.fileInfo != null)
			{
				return false;
			}
		}
		else if (!fileInfo.equals(other.fileInfo))
		{
			return false;
		}
		if (line != other.line)
		{
			return false;
		}
		if (linePos != other.linePos)
		{
			return false;
		}
		return true;
	}

}
