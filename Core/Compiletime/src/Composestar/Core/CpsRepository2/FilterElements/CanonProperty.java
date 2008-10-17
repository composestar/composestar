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

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * A canonical property is identifier which can be used on the left hand side of
 * compare statements. The value of these properties are stored in a context
 * which should be passed along during evaluation of the filters.
 * 
 * @author Michiel Hendriks
 */
public interface CanonProperty extends CpsVariable
{
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
	 * @return The prefix of the variable name. This is either
	 *         {@link PropertyPrefix#FILTER}, {@link PropertyPrefix#MESSAGE} or
	 *         {@link PropertyPrefix#NONE}
	 */
	PropertyPrefix getPrefix();
}
