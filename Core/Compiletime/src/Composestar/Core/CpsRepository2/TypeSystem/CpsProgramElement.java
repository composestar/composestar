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

package Composestar.Core.CpsRepository2.TypeSystem;

import Composestar.Core.LAMA.ProgramElement;

/**
 * Encapsulates all program elements as a variable which can be used by filter
 * module parameters and in assignments. Program Elements can only be indirectly
 * accessed by using the results of selectors, and filter module variables like
 * internals, externals and conditions. The CpsTypeProgramElement should be used
 * in case the program element is a type.
 * 
 * @author Michiel Hendriks
 */
public interface CpsProgramElement extends CpsVariable
{
	/**
	 * @return The program element this variable contains. It can return null
	 *         when no such program element exists, this usually only happens in
	 *         case of indirect CpsProgramElements (for Conditions, Internals,
	 *         Externals, and fully qualified names which should be resolved to
	 *         a Type).
	 */
	ProgramElement getProgramElement();
}
