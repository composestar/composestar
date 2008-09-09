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

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonValue;
import Composestar.Core.CpsRepository2.FilterElements.CanonVariable;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic implementation of the canonical assignment interface
 * 
 * @author Michiel Hendriks
 */
public class CanonAssignmentImpl extends AbstractRepositoryEntity implements CanonAssignment
{
	private static final long serialVersionUID = 5812840706033933887L;

	/**
	 * The variable that will get a new value
	 */
	protected CanonVariable variable;

	/**
	 * The new value
	 */
	protected CanonValue value;

	/**
	 * Create a new instance
	 */
	public CanonAssignmentImpl()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#getValue()
	 */
	public CanonValue getValue()
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#getVariable
	 * ()
	 */
	public CanonVariable getVariable()
	{
		return variable;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#setCanonValue
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonValue)
	 */
	public void setValue(CanonValue newValue) throws NullPointerException
	{
		if (newValue == null)
		{
			throw new NullPointerException();
		}
		value = newValue;
		value.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#setVariable
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonVariable)
	 */
	public void setVariable(CanonVariable var) throws NullPointerException
	{
		if (var == null)
		{
			throw new NullPointerException();
		}
		variable = var;
		variable.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public CanonAssignment newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
