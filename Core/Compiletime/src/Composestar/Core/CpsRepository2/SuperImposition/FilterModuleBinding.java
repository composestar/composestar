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

package Composestar.Core.CpsRepository2.SuperImposition;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;

/**
 * The interface defines the binding of filter modules to a selector. Each
 * filter module binding contains a mapping from a single filter module to a
 * single selector. The CPS language allows a list of filter modules to be bound
 * to a selector, this has to be translated to a list of bindings.
 * 
 * @author Michiel Hendriks
 */
public interface FilterModuleBinding extends RepositoryEntity
{
	/**
	 * Sets the selector to which annotations should be bound
	 * 
	 * @param sel The selector to bind annotations to
	 * @throws NullPointerException thrown when the given selector is null
	 */
	void setSelector(Selector sel) throws NullPointerException;

	/**
	 * @return The selector to which annotations should be bound.
	 */
	Selector getSelector();

	/**
	 * Sets the reference to a filter module which should be bound to a
	 * selector.
	 * 
	 * @param fmRef The filter module reference to use
	 * @throws NullPointerException Thrown when the reference is null
	 */
	void setFilterModuleReference(FilterModuleReference fmRef) throws NullPointerException;

	/**
	 * @return The filter module reference
	 */
	FilterModuleReference getFilterModuleReference();

	// TODO: ... filter module parameter values
}
