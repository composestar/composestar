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

package Composestar.Core.CpsRepository2.FilterModules;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * @author Michiel Hendriks
 */
public class BinaryFilterOperatorTestBase extends RepositoryEntityTestBase
{
	protected BinaryFilterOperator bfo;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#setLHS(Composestar.Core.CpsRepository2.FilterModules.FilterExpression)}
	 * .
	 */
	public void testSetLHS()
	{
		FilterExpression fex = new DummyFEX();
		assertNull(bfo.getLHS());
		bfo.setLHS(fex);
		assertSame(fex, bfo.getLHS());
		try
		{
			bfo.setLHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator#setRHS(Composestar.Core.CpsRepository2.FilterModules.FilterExpression)}
	 * .
	 */
	public void testSetRHS()
	{
		FilterExpression fex = new DummyFEX();
		assertNull(bfo.getRHS());
		bfo.setRHS(fex);
		assertSame(fex, bfo.getRHS());
		try
		{
			bfo.setRHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFEX implements FilterExpression
	{
		private static final long serialVersionUID = 1079445177143255673L;

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
		 */
		public RepositoryEntity getOwner()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation
		 * ()
		 */
		public SourceInformation getSourceInformation()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
		 * .Core.CpsRepository2.RepositoryEntity)
		 */
		public RepositoryEntity setOwner(RepositoryEntity newOwner)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
		 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
		 */
		public void setSourceInformation(SourceInformation srcInfo)
		{}
	}
}
