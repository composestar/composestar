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

package Composestar.Core.CpsRepository2.SISpec.Constraints;

import java.util.List;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * This defines a filter module constraint. Filter module constraints are
 * processed by the FLIRT module.
 * 
 * @author Michiel Hendriks
 */
public interface Constraint extends RepositoryEntity
{
	/**
	 * @return The constraint type. This is one of the following values: pre,
	 *         include, exclude, cond, skip. But others could also be possible.
	 *         Check the FLIRT module for more information.
	 */
	String getConstraintType();

	/**
	 * @return The arguments for this constraint
	 */
	ConstraintValue[] getArguments();

	/**
	 * This method will be called for all constraints. It should return true if
	 * the provided order is legal according to this constraint.
	 * 
	 * @param order The current order of constraint values (usually generated
	 *            from the ImposedFilterModules)
	 * @param exec The execution manager. This will be null in case of static
	 *            analysis during compile time to validate the non-control
	 *            constraints.
	 * @return True if this constraint holds
	 */
	boolean evalConstraint(List<ConstraintValue> order, ExecutionManager exec);
}
