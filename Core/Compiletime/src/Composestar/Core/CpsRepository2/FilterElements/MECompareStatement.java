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

import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariableCollection;

/**
 * The matching expression compare statement performs a compare operation on
 * certain message properties.
 * 
 * @author Michiel Hendriks
 */
public interface MECompareStatement extends MatchingExpression, Instantiatable<MECompareStatement>
{
	/**
	 * @return The variable which is being checked.
	 */
	CanonProperty getLHS();

	/**
	 * Set the variable on the left hand side. setOwner(this) is called on the
	 * variable after assigning.
	 * 
	 * @param var The variable
	 * @throws NullPointerException Thrown when the variable is null.
	 */
	void setLHS(CanonProperty var) throws NullPointerException;

	/**
	 * @return The values on the right hand side. A compare statement is true if
	 *         the left hand side matches any of the values on the right hand
	 *         side.
	 */
	CpsVariableCollection getRHS();

	/**
	 * The set of values on the right hand side to compare with
	 * 
	 * @param values The values to add
	 * @throws NullPointerException Thrown when the value is null.
	 */
	void setRHS(CpsVariableCollection values) throws NullPointerException;
}
