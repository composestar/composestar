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
package Composestar.Core.CpsProgramRepository.Filters;

/**
 * Exception thrown when a given filter action is not supported by the current
 * factory/platform
 * 
 * @author Michiel Hendriks
 */
public class UnsupportedFilterActionException extends Exception
{
	private static final long serialVersionUID = 120051574939262658L;

	protected String filterAction;

	public UnsupportedFilterActionException(String type)
	{
		super();
		filterAction = type;
	}

	public UnsupportedFilterActionException(String type, Throwable cause)
	{
		super(cause);
		filterAction = type;
	}

	public String getType()
	{
		return filterAction;
	}

	@Override
	public String toString()
	{
		return String.format("The filter action \"%s\" is not supported.", filterAction);
	}
}
