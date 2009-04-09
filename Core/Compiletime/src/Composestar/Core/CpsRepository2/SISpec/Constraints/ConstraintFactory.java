/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.CpsRepository2.SISpec.Constraints;

import java.util.List;

/**
 * Interface for the construction of constraints
 * 
 * @author Michiel Hendriks
 */
public interface ConstraintFactory
{
	/**
	 * Create a constraint based on the name and arguments
	 * 
	 * @param name The name of the constraint
	 * @param args The arguments that were provided
	 * @return The created constraint
	 * @throws NullPointerException Thrown when any argument is null
	 * @throws IllegalArgumentException Thrown when the name is empty or list is
	 *             empty
	 * @throws InstantiationException Thrown when no constraint could be created
	 */
	Constraint createConstraint(String name, List<ConstraintValue> args) throws NullPointerException,
			IllegalArgumentException, InstantiationException;
}
