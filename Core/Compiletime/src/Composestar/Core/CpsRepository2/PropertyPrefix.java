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

package Composestar.Core.CpsRepository2;

/**
 * The property prefixes which can be used by the canonical properties
 * 
 * @author Michiel Hendriks
 */
public enum PropertyPrefix
{

	/**
	 * Message properties
	 */
	MESSAGE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "message";
		}
	},
	/**
	 * Filter properties
	 */
	FILTER
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "filter";
		}
	},
	/**
	 * no prefix, only allowed for inner
	 */
	NONE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "";
		}
	};

	/**
	 * Get the enum value from a string
	 * 
	 * @param name The name of the desired prefi
	 * @return The prefix
	 * @throws IllegalArgumentException Thrown when the name is not a valid
	 *             prefix
	 */
	public static PropertyPrefix fromString(String name) throws IllegalArgumentException
	{
		if (name == null || name.isEmpty())
		{
			return NONE;
		}
		else if (MESSAGE.toString().equals(name))
		{
			return MESSAGE;
		}
		else if (FILTER.toString().equals(name))
		{
			return FILTER;
		}
		throw new IllegalArgumentException(String.format("%s is not a valid property prefix", name));
	}
}
