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

package Composestar.Core.Exception;

/**
 * Generic exception for compose*
 * 
 * @author Michiel Hendriks
 */
public class ComposestarException extends Exception
{
	private static final long serialVersionUID = 6563714600230393716L;

	public ComposestarException()
	{
		super();
	}

	public ComposestarException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ComposestarException(String message)
	{
		super(message);
	}

	public ComposestarException(Throwable cause)
	{
		super(cause);
	}
}
