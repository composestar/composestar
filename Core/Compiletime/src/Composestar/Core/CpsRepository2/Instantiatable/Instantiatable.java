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

package Composestar.Core.CpsRepository2.Instantiatable;

/**
 * This is an implementation of the Visitor design pattern, this implements the
 * visitable part of the pattern. All child elements of a filter module should
 * implement this interface. The instantiatable interface is used to create new
 * instances of filter modules where the filter module parameter values have
 * been resolved. It is also used by the runtime to create runtime instances of
 * filter modules for each concern instance.
 * 
 * @author Michiel Hendriks
 * @see Instantiator
 */
public interface Instantiatable<T>
{
	/**
	 * This defines the "vists" method for the visitable interface. In most
	 * cases implementing classes would simply containg the following
	 * implementation for this method:
	 * 
	 * <pre>
	 * {
	 * 	return context.instantiate(this);
	 * }
	 * </pre>
	 * 
	 * @param instantiator The visitor which creates the actual instanses when
	 *            needed.
	 * @return The new instance according to the rules of the instantiator, or
	 *         null when no new instance could be created. Null should not be
	 *         returned directly by the implementation, the null should only be
	 *         returned if the context returned null. If the implementation
	 *         should never be instantiated an UnsupportedOperationException
	 *         exception should be thrown instead.
	 * @throws UnsupportedOperationException In extreme exceptions an
	 *             implementation may refuse to the instantiatable. In this case
	 *             it should throw this exception.
	 */
	T newInstance(Instantiator instantiator) throws UnsupportedOperationException;
}
