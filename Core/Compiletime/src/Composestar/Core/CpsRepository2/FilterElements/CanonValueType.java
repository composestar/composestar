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

package Composestar.Core.CpsRepository2.FilterElements;

/**
 * The various internal value types.
 * 
 * @author Michiel Hendriks
 */
public enum CanonValueType
{
	/**
	 * A literal value.
	 */
	LITERAL,
	/**
	 * A fully qualified name. This should be resolved to one of the other
	 * types.
	 */
	FQN,
	/**
	 * An object instance. Used for most standard message properties and
	 * internals/externals
	 */
	OBJECT,
	/**
	 * A selector, only used by message.selector
	 */
	SELECTOR,
	/**
	 * A type program element
	 */
	TYPE,
	/**
	 * A method program element. For example used for conditions.
	 */
	METHOD,
	/**
	 * A different program element (no type or method).
	 */
	PROGRAM_ELEMENT,
	/**
	 * Unknown. Used by parameterized values when their actual value has not
	 * been resolved.
	 */
	UNKNOWN,
}
