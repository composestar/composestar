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
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.Filters.FilterType;
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
	FilterType getType();

	/**
	 * Sets the filter type. This is the filter which will be executed when this
	 * filter matches the given message.
	 * 
	 * @param type The type of this filter definition.
	 * @throws NullPointerException Thrown when the filter type is null.
	 */
	void setType(FilterType type) throws NullPointerException;

	/**
	 * Add a new filter argument. It will overwrite a previous argument with the
	 * same name. setOwner(this) is called on the argument after adding.
	 * 
	 * @param argument The argument to add.
	 * @return The argument that was replaced by the current argument, or null
	 *         of no argument was replaced.
	 * @throws NullPointerException Thrown when the argument is null.
	 * @throws IllegalArgumentException Thrown when the assignment assigns a
	 *             non-filter variable.
	 */
	CanonAssignment addArgument(CanonAssignment argument) throws NullPointerException, IllegalArgumentException;

	/**
	 * Remove a filter argument
	 * 
	 * @param argument The argument to remove
	 * @return The removed argument, or null if nothing was removed.
	 * @throws NullPointerException Thrown when the passed argument is null.
	 */
	CanonAssignment removeArgument(CanonAssignment argument) throws NullPointerException;

	/**
	 * Retrieves an argument by the name.
	 * 
	 * @param argName The name of the argument to retrieve, the name should not
	 *            contain the filter prefix
	 *            {@link Composestar.Core.CpsRepository2.FilterElements.CanonProperty#FILTER_PREFIX}
	 *            .
	 * @return The argument with the given name, or null if no argument with
	 *         that name could be found.
	 */
	CanonAssignment getArgument(String argName);

	/**
	 * @return The filter arguments. An empty collection is returned when there
	 *         no arguments where defined. The collection is read-only.
	 */
	Collection<CanonAssignment> getArguments();

	/**
	 * @return The filter element expression. A filter should always have a
	 *         filter element expression.
	 */
	FilterElementExpression getElementExpression();

	/**
	 * Set the filter element expression for this filter. setOwner(this) is
	 * called on the expression after assigning.
	 * 
	 * @param expr The filter element expression.
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setElementExpression(FilterElementExpression expr) throws NullPointerException;
}
