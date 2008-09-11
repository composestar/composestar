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

package Composestar.Core.CpsRepository2Impl;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;

/**
 * @author Michiel Hendriks
 */
public class AbstractQualifiedRepositoryEntityTest extends QualifiedRepositoryEntityTestBase
{

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		qre = new DummyQRE("foo");
		re = qre;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity#getFullyQualifiedName()}
	 * .
	 */
	public void testGetFullyQualifiedNameEx()
	{
		DummyQRE qre2 = new DummyQRE("bar");
		DummyRE re = new DummyRE();
		re.setOwner(qre2);

		assertEquals(qre.getName(), qre.getFullyQualifiedName());
		assertNull(qre.setOwner(re));
		assertTrue(qre.getFullyQualifiedName().startsWith(qre2.getName() + "."));
		assertEquals(qre2.getName() + "." + qre.getName(), qre.getFullyQualifiedName());
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyQRE extends AbstractQualifiedRepositoryEntity
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @param entityName
		 * @throws NullPointerException
		 * @throws IllegalArgumentException
		 */
		public DummyQRE(String entityName) throws NullPointerException, IllegalArgumentException
		{
			super(entityName);
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyRE extends AbstractRepositoryEntity
	{
		private static final long serialVersionUID = 1L;

	}
}
