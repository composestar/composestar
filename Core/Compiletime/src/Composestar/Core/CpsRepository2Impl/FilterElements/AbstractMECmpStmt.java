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

import Composestar.Core.CpsRepository2.FilterElements.CanonValue;
import Composestar.Core.CpsRepository2.FilterElements.CanonVariable;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Abstract implementation of the MECompareStatement interface
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractMECmpStmt extends AbstractRepositoryEntity implements MECompareStatement
{
	private static final long serialVersionUID = 4093575863983309263L;

	/**
	 * The variable to compare with
	 */
	protected CanonVariable lhs;

	/**
	 * The value to compare the variable with
	 */
	protected CanonValue rhs;

	/**
	 * 
	 */
	protected AbstractMECmpStmt()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#getLHS
	 * ()
	 */
	public CanonVariable getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#getRHS
	 * ()
	 */
	public CanonValue getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setLHS
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonVariable)
	 */
	public void setLHS(CanonVariable var) throws NullPointerException
	{
		if (var == null)
		{
			throw new NullPointerException();
		}
		lhs = var;
		lhs.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setRHS
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonValue)
	 */
	public void setRHS(CanonValue value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		rhs = value;
		rhs.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public MECompareStatement newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
