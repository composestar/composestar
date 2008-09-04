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

import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * And matching expression operators. Both values should return true for this
 * operator to be true.
 * 
 * @author Michiel Hendriks
 */
public class AndMEOper extends AbstractRepositoryEntity implements BinaryMEOperator
{

	private static final long serialVersionUID = 5836607593115269406L;

	/**
	 * Associated matching expression
	 */
	protected MatchingExpression lhs, rhs;

	/**
	 * Create a new operator
	 */
	public AndMEOper()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#getLHS()
	 */
	public MatchingExpression getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#getRHS()
	 */
	public MatchingExpression getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#setLHS
	 * (Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)
	 */
	public void setLHS(MatchingExpression expr) throws NullPointerException
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
	 * Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#setRHS
	 * (Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)
	 */
	public void setRHS(MatchingExpression expr) throws NullPointerException
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
	public BinaryMEOperator newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
