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

/**
 * A filter element expression operator that takes two arguments.
 * 
 * @author Michiel Hendriks
 */
public interface BinaryFilterElementOperator extends FilterElementExpression,
		Instantiatable<BinaryFilterElementOperator>
{
	/**
	 * @return The left hand side of the operator
	 */
	FilterElementExpression getLHS();

	/**
	 * Sets the left hand side of the operator. setOwner(this) is called on the
	 * expression after assigning.
	 * 
	 * @param expr The expression
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setLHS(FilterElementExpression expr) throws NullPointerException;

	/**
	 * @return The right hand side of the operator
	 */
	FilterElementExpression getRHS();

	/**
	 * Sets the right hand side of the operator.setOwner(this) is called on the
	 * expression after assigning.
	 * 
	 * @param expr The expression
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setRHS(FilterElementExpression expr) throws NullPointerException;
}
