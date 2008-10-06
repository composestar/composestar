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

package Composestar.Core.CpsRepository2.SISpec;

import java.util.List;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * This defines a filter module constraint. Filter module constraints are
 * processed by the FLIRT module.
 * 
 * @author Michiel Hendriks
 */
// TODO this interface might need to be adjusted to blend in with FILTH
public interface FilterModuleConstraint extends RepositoryEntity
{
	/**
	 * @return The constraint type. This is one of the following values: pre,
	 *         include, exclude, cond, skip. But others could also be possible.
	 *         Check the FLIRT module for more information.
	 */
	String getConstraintType();

	/**
	 * Set the arguments of this constraint. This overrides the previously set
	 * arguments.
	 * 
	 * @param args The list of arguments
	 * @throws NullPointerException Thrown when the list is null.
	 * @throws IllegalArgumentException Thrown when the list of arguments
	 *             contains invalid values.
	 */
	void setArguments(List<ConstraintValue> args) throws NullPointerException, IllegalArgumentException;

	/**
	 * @return The arguments for the given constraint. The type and number of
	 *         the arguments depends on the constraint type.
	 */
	List<ConstraintValue> getArguments();
}
