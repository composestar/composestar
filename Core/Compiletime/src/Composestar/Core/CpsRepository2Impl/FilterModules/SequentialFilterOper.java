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

package Composestar.Core.CpsRepository2Impl.FilterModules;

import Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A sequential filter operator. The expressions on both sides are executed in
 * order (first left, then right) until a filter initiates a return or exit in
 * the flow.
 * 
 * @author Michiel Hendriks
 */
public class SequentialFilterOper extends AbstractRepositoryEntity implements BinaryFilterOperator
{
	private static final long serialVersionUID = -2619522777888618361L;

	/**
	 * The expressions on either side
	 */
	protected FilterExpression lhs, rhs;

	/**
	 * Create a new SequentialFilterOper
	 */
	public SequentialFilterOper()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#getLHS
	 * ()
	 */
	public FilterExpression getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#getRHS
	 * ()
	 */
	public FilterExpression getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#setLHS
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setLHS(FilterExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		lhs = expr;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#setRHS
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
	 */
	public void setRHS(FilterExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		rhs = expr;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public BinaryFilterOperator newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}
}
