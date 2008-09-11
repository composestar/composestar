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

import java.util.ArrayList;
import java.util.Collection;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class MECompareStatementTestBase extends RepositoryEntityTestBase
{
	protected MECompareStatement mecs;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setLHS(Composestar.Core.CpsRepository2.FilterElements.CanonProperty)}
	 * .
	 */
	public void testSetLHS()
	{
		CanonProperty cv = new DummyCV();
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
	 * {@link Composestar.Core.CpsRepository2.FilterElements.MECompareStatement#setRHS(Collection)}
	 * .
	 */
	public void testSetRHS()
	{
		CpsVariable cv = new DummyCV();
		Collection<CpsVariable> cvl = new ArrayList<CpsVariable>();
		cvl.add(cv);
		assertNotNull(mecs.getRHS());
		assertEquals(0, mecs.getRHS().size());
		mecs.setRHS(cvl);
		assertEquals(1, mecs.getRHS().size());
		assertTrue(mecs.getRHS().contains(cv));
		assertSame(mecs, cv.getOwner());
		assertNotSame(cvl, mecs.getRHS());

		try
		{
			mecs.setRHS(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}

		try
		{
			cvl.clear();
			mecs.setRHS(cvl);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCV extends AbstractRepositoryEntity implements CanonProperty
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getBaseName
		 * ()
		 */
		public String getBaseName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getName
		 * ()
		 */
		public String getName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getPrefix
		 * ()
		 */
		public PropertyPrefix getPrefix()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getValue
		 * ()
		 */
		public CpsVariable getValue()
		{
			return null;
		}

		public void setValue(CpsVariable newvalue) throws NullPointerException, IllegalArgumentException
		{}
	}
}
