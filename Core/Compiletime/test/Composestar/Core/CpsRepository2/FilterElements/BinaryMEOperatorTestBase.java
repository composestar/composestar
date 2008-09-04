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
public abstract class BinaryMEOperatorTestBase extends RepositoryEntityTestBase
{
	protected BinaryMEOperator beop;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#setLHS(Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)}
	 * .
	 */
	public void testSetLHS()
	{
		MatchingExpression mex = new DummyMEX();
		assertNull(beop.getLHS());
		beop.setLHS(mex);
		assertSame(mex, beop.getLHS());
		try
		{
			beop.setLHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}

	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator#setRHS(Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)}
	 * .
	 */
	public void testSetRHS()
	{
		MatchingExpression mex = new DummyMEX();
		assertNull(beop.getRHS());
		beop.setRHS(mex);
		assertSame(mex, beop.getRHS());
		try
		{
			beop.setRHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyMEX extends AbstractRepositoryEntity implements MatchingExpression
	{
		private static final long serialVersionUID = -1051877554942568018L;
	}
}
