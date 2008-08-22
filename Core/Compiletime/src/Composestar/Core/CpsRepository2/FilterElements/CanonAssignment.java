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

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;

/**
 * This interface defines a assignment of a given variable. It is used both by
 * the {@link Composestar.Core.CpsRepository2.FilterModules.Filter} for the
 * filter argument assignments and the {@link FilterElement} for the assignment
 * part.
 * 
 * @author Michiel Hendriks
 */
public interface CanonAssignment extends RepositoryEntity, Instantiatable<CanonAssignment>
{
	/**
	 * @return The variable that is set.
	 */
	CanonVariable getVariable();

	/**
	 * Sets the variable that is set in this assignment.
	 * 
	 * @param var The variable
	 * @throws NullPointerException Thrown when the variable is null
	 */
	void setVariable(CanonVariable var) throws NullPointerException;

	/**
	 * @return The value the variable should get.
	 */
	CanonValue getValue();

	/**
	 * Set the value that the variable should get when the assignment is
	 * executed.
	 * 
	 * @param value The new value
	 * @throws NullPointerException Thrown when the value is null
	 */
	void setCanonValue(CanonValue value) throws NullPointerException;
}
