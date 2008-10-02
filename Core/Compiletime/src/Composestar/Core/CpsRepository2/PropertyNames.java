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
 * This interface has as only purpose to provide a single entry point for
 * predefined property names.
 * 
 * @author Michiel Hendriks
 */
public final class PropertyNames
{
	/**
	 * The predefined inner property name. (read-only)
	 */
	public static final String INNER = "inner";

	/**
	 * The predefined message.selector property name.
	 */
	public static final String SELECTOR = "selector";

	/**
	 * The predefined message.self property name.
	 */
	public static final String SELF = "self";

	/**
	 * The predefined message.sender property name. (read-only)
	 */
	public static final String SENDER = "sender";

	/**
	 * The predefined message.server property name.
	 */
	public static final String SERVER = "server";

	/**
	 * The predefined message.target property name.
	 */
	public static final String TARGET = "target";

	/**
	 * Utility class
	 */
	private PropertyNames()
	{}
}
