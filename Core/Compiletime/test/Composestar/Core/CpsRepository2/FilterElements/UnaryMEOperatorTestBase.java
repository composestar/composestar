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
public abstract class UnaryMEOperatorTestBase extends RepositoryEntityTestBase
{
	protected UnaryMEOperator meop;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator#setOperand(Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)}
	 * .
	 */
	public void testSetOperand()
	{
		MatchingExpression mex = new DummyMEX();
		assertNull(meop.getOperand());
		meop.setOperand(mex);
		assertSame(mex, meop.getOperand());
		try
		{
			meop.setOperand(null);
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
