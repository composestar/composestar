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

package Composestar.Utils.Regex;

/**
 * Exception throw when the regular expression can not be parsed.
 * 
 * @author Michiel Hendriks
 */
public class PatternParseException extends Exception
{
	private static final long serialVersionUID = -3184789641737942176L;

	protected String description;

	protected String pattern;

	protected int location;

	public PatternParseException(String desc, String patt, int loc)
	{
		description = desc;
		pattern = patt;
		location = loc;
	}

	public String getDescription()
	{
		return description;
	}

	public String getPattern()
	{
		return pattern;
	}

	public int getIndex()
	{
		return location;
	}

	public String getMessage()
	{
		return String.format("%s at %d in %s", description, pattern, location);
	}
}
