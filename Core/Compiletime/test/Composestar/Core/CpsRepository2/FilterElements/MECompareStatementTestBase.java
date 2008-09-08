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
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class MECompareStatementTestBase extends RepositoryEntityTestBase
{
	protected MECompareStatement mecs;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setLHS(Composestar.Core.CpsRepository2.FilterElements.CanonVariable)}
	 * .
	 */
	public void testSetLHS()
	{
		CanonVariable cv = new DummyCV();
		assertNull(mecs.getLHS());
		mecs.setLHS(cv);
		assertSame(cv, mecs.getLHS());
		assertSame(mecs, cv.getOwner());

		try
		{
			mecs.setLHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setRHS(Composestar.Core.CpsRepository2.FilterElements.CanonValue)}
	 * .
	 */
	public void testSetRHS()
	{
		CanonValue cv = new DummyCV();
		assertNull(mecs.getRHS());
		mecs.setRHS(cv);
		assertSame(cv, mecs.getRHS());
		assertSame(mecs, cv.getOwner());

		try
		{
			mecs.setRHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCV extends AbstractRepositoryEntity implements CanonVariable
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterElements.CanonVariable#
		 * acceptsValueType
		 * (Composestar.Core.CpsRepository2.FilterElements.CanonValueType)
		 */
		public boolean acceptsValueType(CanonValueType valueType)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getBaseName
		 * ()
		 */
		public String getBaseName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getName
		 * ()
		 */
		public String getName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getPrefix
		 * ()
		 */
		public String getPrefix()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#setValue
		 * (Composestar.Core.CpsRepository2.FilterElements.CanonValue)
		 */
		public void setValue(CanonValue newValue) throws NullPointerException, IllegalArgumentException,
				UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonValue#getType()
		 */
		public CanonValueType getType()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonValue#getValue()
		 */
		public Object getValue()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public CanonValue newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}
	}
}
