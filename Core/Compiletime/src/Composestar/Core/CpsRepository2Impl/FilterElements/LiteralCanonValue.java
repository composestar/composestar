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
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A literal canonical value
 * 
 * @author Michiel Hendriks
 */
public class LiteralCanonValue extends AbstractRepositoryEntity implements CanonValue
{
	private static final long serialVersionUID = -851598739536031013L;

	/**
	 * Contains the literal value
	 */
	protected String value;

	/**
	 * Create a new literal with the given value
	 * 
	 * @param lvalue the value of the literal
	 */
	public LiteralCanonValue(String lvalue)
	{
		super();
		value = lvalue;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterElements.CanonValue#getType()
	 */
	public CanonValueType getType()
	{
		return CanonValueType.LITERAL;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FilterElements.CanonValue#getValue()
	 */
	public Object getValue()
	{
		return value;
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
