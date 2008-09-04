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

import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A not operator, inverts the result of it's operand.
 * 
 * @author Michiel Hendriks
 */
public class NotMEOper extends AbstractRepositoryEntity implements UnaryMEOperator
{
	private static final long serialVersionUID = -2031953131920180410L;

	/**
	 * The expression that is inverted
	 */
	protected MatchingExpression operand;

	/**
	 * Create a new instance
	 */
	public NotMEOper()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator#getOperand
	 * ()
	 */
	public MatchingExpression getOperand()
	{
		return operand;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator#setOperand
	 * (Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)
	 */
	public void setOperand(MatchingExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		operand = expr;
		operand.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public UnaryMEOperator newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}
}
