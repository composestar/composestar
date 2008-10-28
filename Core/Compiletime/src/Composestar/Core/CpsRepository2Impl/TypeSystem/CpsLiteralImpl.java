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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic implementation of the CpsLiteral type
 * 
 * @author Michiel Hendriks
 */
public class CpsLiteralImpl extends AbstractRepositoryEntity implements CpsLiteral
{
	private static final long serialVersionUID = 6880713290852824246L;

	/**
	 * Contains the value of this literal
	 */
	protected String literalValue;

	/**
	 * @param value the value of the literal
	 * @throws NullPointerException Thrown when the value is null
	 */
	public CpsLiteralImpl(String value) throws NullPointerException
	{
		super();
		if (value == null)
		{
			throw new NullPointerException("value cannot be null");
		}
		literalValue = value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral#getLiteralValue()
	 */
	public String getLiteralValue()
	{
		return literalValue;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		if (!(other instanceof CpsLiteral))
		{
			return false;
		}
		CpsLiteral o = (CpsLiteral) other;
		if (literalValue == null)
		{
			return o.getLiteralValue() == null;
		}
		return literalValue.equals(o.getLiteralValue());
	}
}
