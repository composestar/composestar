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

package Composestar.Core.CpsRepository2.FilterElements;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class BinaryFilterElementOperatorTestBase extends RepositoryEntityTestBase
{
	protected BinaryFilterElementOperator bfeo;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator#setLHS(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)}
	 * .
	 */
	public void testSetLHS()
	{
		FilterElementExpression fex = new DummyFEX();
		assertNull(bfeo.getLHS());
		bfeo.setLHS(fex);
		assertSame(fex, bfeo.getLHS());
		assertSame(bfeo, fex.getOwner());
		try
		{
			bfeo.setLHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator#setRHS(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)}
	 * .
	 */
	public void testSetRHS()
	{
		FilterElementExpression fex = new DummyFEX();
		assertNull(bfeo.getRHS());
		bfeo.setRHS(fex);
		assertSame(fex, bfeo.getRHS());
		assertSame(bfeo, fex.getOwner());
		try
		{
			bfeo.setRHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFEX extends AbstractRepositoryEntity implements FilterElementExpression
	{
		private static final long serialVersionUID = 1079445177143255673L;
	}
}
