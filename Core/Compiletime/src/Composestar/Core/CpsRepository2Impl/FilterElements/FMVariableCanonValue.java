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
import Composestar.Core.CpsRepository2.FilterElements.CanonValueType;
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A canonical value that links to a filter module variable
 * 
 * @author Michiel Hendriks
 */
public class FMVariableCanonValue extends AbstractRepositoryEntity implements CanonValue
{
	private static final long serialVersionUID = -3209014454150477674L;

	/**
	 * The variable being linked to
	 */
	protected FilterModuleVariable variable;

	/**
	 * @param fmvar
	 * @throws NullPointerException Thrown when the variable is null
	 */
	public FMVariableCanonValue(FilterModuleVariable fmvar) throws NullPointerException
	{
		super();
		if (fmvar == null)
		{
			throw new NullPointerException("filter module variable is null");
		}
		variable = fmvar;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterElements.CanonValue#getType()
	 */
	public CanonValueType getType()
	{
		if (variable instanceof Condition)
		{
			return CanonValueType.METHOD;
		}
		if (variable instanceof Internal || variable instanceof External)
		{
			return CanonValueType.OBJECT;
		}
		throw new IllegalStateException("Unknown filter module variable type");
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterElements.CanonValue#getValue()
	 */
	public Object getValue()
	{
		if (variable instanceof Condition)
		{
			return ((Condition) variable).getMethodReference();
		}
		if (variable instanceof Internal || variable instanceof External)
		{
			// FIXME shouldn't something else be returned? What does an object
			// return?
			return variable;
		}
		throw new IllegalStateException("Unknown filter module variable type");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public CanonValue newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
