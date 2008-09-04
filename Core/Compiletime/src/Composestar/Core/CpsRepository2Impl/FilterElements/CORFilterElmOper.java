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

package Composestar.Core.CpsRepository2Impl.FilterElements;

import Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Conditional OR operator. If the LHS matched, the RHS will not be checked.
 * 
 * @author Michiel Hendriks
 */
public class CORFilterElmOper extends AbstractRepositoryEntity implements BinaryFilterElementOperator
{
	private static final long serialVersionUID = 2805344382913049175L;

	/**
	 * The associated filter element expressions
	 */
	protected FilterElementExpression lhs, rhs;

	/**
	 * Create a new COR operator
	 */
	public CORFilterElmOper()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator
	 * #getLHS()
	 */
	public FilterElementExpression getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator
	 * #getRHS()
	 */
	public FilterElementExpression getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator
	 * #
	 * setLHS(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression
	 * )
	 */
	public void setLHS(FilterElementExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		lhs = expr;
		lhs.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator
	 * #
	 * setRHS(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression
	 * )
	 */
	public void setRHS(FilterElementExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		rhs = expr;
		rhs.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public BinaryFilterElementOperator newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
