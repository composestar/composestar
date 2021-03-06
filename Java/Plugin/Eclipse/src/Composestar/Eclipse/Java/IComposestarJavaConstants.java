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

package Composestar.Eclipse.Java;

/**
 * Constants used in the Compose* /Java Eclipse Plugin
 * 
 * @author Michiel Hendriks
 */
public interface IComposestarJavaConstants
{
	public static final String BUNDLE_ID = "composestar.java";

	public static final String NATURE_ID = BUNDLE_ID + ".nature";
	
	public static final String BUILDER_ID = BUNDLE_ID + ".builder";

	/**
	 * Base classpath container name
	 */
	public static final String CP_CONTAINER = "composestar.java.classpath";
}
