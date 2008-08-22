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

import java.util.Collection;

import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;

/**
 * A filter element expression contains at least one FilterElement.
 * 
 * @author Michiel Hendriks
 */
public interface FilterElement extends FilterElementExpression, Instantiatable<FilterElement>
{
	/**
	 * @return The matching expression for this filter element.
	 */
	MatchingExpression getMatchingExpression();

	/**
	 * Sets the matching expression for this filter element.
	 * 
	 * @param expr The matching expression.
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setMatchingExpression(MatchingExpression expr) throws NullPointerException;

	/**
	 * Add a new assignment to this filter element. Variables can only be
	 * assigned a new value once in a assignment block. The last assignment made
	 * will be the effective assignment.
	 * 
	 * @param ass The assignment to add.
	 * @return The assignment that was replaced by the new assignment, or null
	 *         if no previous assignment was replace.
	 * @throws NullPointerException Thrown when the assignment is null.
	 */
	CanonAssignment addAssignment(CanonAssignment ass) throws NullPointerException;

	/**
	 * Remove the passed assignment from the assignment block.
	 * 
	 * @param ass The assignment to remove
	 * @return True if the assignment was removed
	 * @throws NullPointerException Thrown when the passed assignment is null.
	 */
	boolean removeAssignment(CanonAssignment ass) throws NullPointerException;

	/**
	 * Retrieves an assignment by the variable name.
	 * 
	 * @param name The name of the variable who's assignment to retrieve.
	 * @return The assignment with the given variable, or null if no
	 *         assignmentfor that variable could be found..
	 */
	CanonAssignment getArgument(String name);

	/**
	 * @return The assignments. If no assignments where defined an empty
	 *         collection is returned. The collection is read-only.
	 */
	Collection<CanonAssignment> getAssignments();

}
