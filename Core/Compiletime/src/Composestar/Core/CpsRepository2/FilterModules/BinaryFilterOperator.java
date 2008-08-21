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

import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;

/**
 * A filter operator which has a left hand side and right hand side.
 * 
 * @author Michiel Hendriks
 */
public interface BinaryFilterOperator extends FilterExpression, Instantiatable<BinaryFilterOperator>
{
	/**
	 * @return The expression on the left hand side of this operator.
	 */
	FilterExpression getLHS();

	/**
	 * Set the expression on the left hand side of the operator. After the LHS
	 * has been set setOwner(this) is called on the expression.
	 * 
	 * @param expr The expression.
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setLHS(FilterExpression expr) throws NullPointerException;

	/**
	 * @return The expression on the right hand side of this operator.
	 */
	FilterExpression getRHS();

	/**
	 * Set the expression on the right hand side of the operator. After the RHS
	 * has been set setOwner(this) is called on the expression.
	 * 
	 * @param expr The expression.
	 * @throws NullPointerException Thrown when the expression is null.
	 */
	void setRHS(FilterExpression expr) throws NullPointerException;
}
