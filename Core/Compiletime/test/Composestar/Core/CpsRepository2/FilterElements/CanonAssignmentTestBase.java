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

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class CanonAssignmentTestBase extends RepositoryEntityTestBase
{
	protected CanonAssignment ca;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#setProperty(Composestar.Core.CpsRepository2.FilterElements.CanonProperty)}
	 * .
	 */
	public void testSetProperty()
	{
		assertNull(ca.getProperty());
		CanonProperty cp = new DummyCP();
		ca.setProperty(cp);
		assertSame(cp, ca.getProperty());
		assertSame(ca, cp.getOwner());
		try
		{
			ca.setProperty(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#setValue(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)}
	 * .
	 */
	public void testSetValue()
	{
		assertNull(ca.getValue());
		CpsVariable cv = new DummyCP();
		ca.setValue(cv);
		assertSame(cv, ca.getValue());
		assertSame(ca, cv.getOwner());
		try
		{
			ca.setValue(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCP extends AbstractRepositoryEntity implements CanonProperty
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

	}
}
