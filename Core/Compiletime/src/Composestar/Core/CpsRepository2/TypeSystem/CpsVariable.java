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

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * The base interface for the type system in the Compose* language. All types,
 * as can be used within the Compose* language, as a subinterface for this
 * interface.
 * 
 * @author Michiel Hendriks
 */
public interface CpsVariable extends RepositoryEntity
{
	/**
	 * Similar to the equals check, except this allows equality on a super class
	 * to be valid too.
	 * 
	 * @param other
	 * @return True when both sides are compatible
	 * @throws UnsupportedOperationException Thrown by subtypes that do not
	 *             encapsulate a real value (like properties or parameters)
	 */
	boolean compatible(CpsVariable other) throws UnsupportedOperationException;
}
