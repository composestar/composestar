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

package Composestar.Core.CpsRepository2.FilterModules;

import java.util.Collection;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;

/**
 * This interface defines a filter definition in either the input or output
 * filter expression.
 * 
 * @author Michiel Hendriks
 */
public interface Filter extends QualifiedRepositoryEntity, FilterExpression, Instantiatable<Filter>
{
	/**
	 * @return The type of this filter
	 */
	// FIXME change to FilterType
	Object getType();

	/**
	 * Sets the filter type. This is the filter which will be executed when this
	 * filter matches the given message.
	 * 
	 * @param type The type of this filter definition.
	 * @throws NullPointerException Thrown when the filter type is null.
	 */
	// FIXME change to FilterType
	void setType(Object type) throws NullPointerException;

	// FIXME change to ???
	/**
	 * Add a new filter argument. It will overwrite a previous argument with the
	 * same name.
	 * 
	 * @param argument The argument to add.
	 * @throws NullPointerException Thrown when the argument is null.
	 */
	void addArgument(Object argument) throws NullPointerException;

	// FIXME change to ???
	Object getArgument(String name);

	// FIXME change to ???
	boolean removeArgument(Object argument) throws NullPointerException;

	// FIXME change to ???
	Object removeArgument(String name);

	// FIXME change to ???
	Collection<Object> getArguments();

	/**
	 * @return The filter element expression. A filter should always have a
	 *         filter element expression.
	 */
	FilterElementExpression getElementExpression();

	/**
	 * Set the filter element expression for this filter.
	 * 
	 * @param expr The filter element expression.
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setElementExpression(FilterElementExpression expr) throws NullPointerException;
}
