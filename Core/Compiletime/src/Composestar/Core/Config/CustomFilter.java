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

import java.io.Serializable;

/**
 * A custom filter definition
 * 
 * @author Michiel Hendriks
 */
public class CustomFilter implements Serializable
{
	private static final long serialVersionUID = -6773322662826146455L;

	/**
	 * Optional name for this custom filter
	 */
	protected String name;

	/**
	 * The library associated with this custom filter. Semantics of the value
	 * depend on the platform.
	 */
	protected String library;

	public CustomFilter()
	{}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		if (inName == null || inName.trim().length() == 0)
		{
			name = null;
		}
		else
		{
			name = inName.trim();
		}
	}

	public String getLibrary()
	{
		return library;
	}

	public void setLibrary(String inLibrary)
	{
		if (inLibrary == null || inLibrary.trim().length() == 0)
		{
			throw new IllegalArgumentException("Library can not be null or empty");
		}
		library = inLibrary;
	}
}
