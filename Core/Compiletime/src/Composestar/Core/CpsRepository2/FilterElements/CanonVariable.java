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
 * A canonical variable is identifier which can be used on the left hand side of
 * compare statements and, most of them, can be assigned a new value in the
 * assignment part.
 * 
 * @author Michiel Hendriks
 */
public interface CanonVariable extends CanonValue
{
	/**
	 * The prefix for all message properties.
	 */
	static final String MESSAGE_PREFIX = "message";

	/**
	 * The predefined message.target property name.
	 */
	static final String TARGET = "target";

	/**
	 * The predefined message.selector property name.
	 */
	static final String SELECTOR = "selector";

	/**
	 * The predefined message.self property name.
	 */
	static final String SELF = "self";

	/**
	 * The predefined message.sender property name. (read-only)
	 */
	static final String SENDER = "sender";

	/**
	 * The predefined message.server property name.
	 */
	static final String SERVER = "server";

	/**
	 * The predefined inner property name. (read-only)
	 */
	static final String INNER = "inner";

	/**
	 * The prefix for filter properties.
	 */
	static final String FILTER_PREFIX = "filter";

	/**
	 * @return The full variable name. This is a concatenation of the base and
	 *         prefix (delimited with a '.')
	 * @see #getBaseName()
	 * @see #getPrefix()
	 */
	String getName();

	/**
	 * @return Just the name of the variable without the possible prefix.
	 */
	String getBaseName();

	/**
	 * @return The prefix of the variable name. This is either {
	 *         {@link #MESSAGE_PREFIX}, {@link #FILTER_PREFIX} or null (in case
	 *         the name is {@link #INNER})
	 */
	String getPrefix();

	/**
	 * Assign a new value to this variable.
	 * 
	 * @param newValue Reference to the value instance who's value to take.
	 * @throws NullPointerException Thrown when the new value is null.
	 * @throws IllegalArgumentException Thrown when the provided value has a
	 *             different type that the current type. Values needs to be
	 *             converted to the correct type.
	 * @throws UnsupportedOperationException Thrown when this variable is
	 *             read-only (rare case).
	 */
	// TODO add @see to the CanonTypeConverter thingy
	void setValue(CanonValue newValue) throws NullPointerException, IllegalArgumentException,
			UnsupportedOperationException;

	/**
	 * Checks if the given value type is accepted as value for this variable. If
	 * this returns false {@link #setValue(CanonValue)} throws an
	 * IllegalArgumentException when called with a value of this type.
	 * 
	 * @param valueType The value type
	 * @return True if this type is accepted.
	 */
	boolean acceptsValueType(CanonValueType valueType);
}
