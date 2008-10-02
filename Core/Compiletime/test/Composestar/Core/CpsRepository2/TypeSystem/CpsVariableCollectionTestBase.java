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

package Composestar.Core.CpsRepository2.TypeSystem;

import java.util.ArrayList;
import java.util.Collection;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class CpsVariableCollectionTestBase extends RepositoryEntityTestBase
{
	protected CpsVariableCollection col;

	/**
	 * Test method for {@link java.util.Collection#add(java.lang.Object)}.
	 */
	public void testAdd()
	{
		CpsVariable var = new DummyCPSV();
		assertNull(var.getOwner());
		assertTrue(col.add(var));
		assertSame(col, var.getOwner());

		try
		{
			col.add(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for {@link java.util.Collection#addAll(java.util.Collection)}
	 * .
	 */
	public void testAddAll()
	{
		CpsVariable var = new DummyCPSV();
		Collection<CpsVariable> varlist = new ArrayList<CpsVariable>();
		varlist.add(var);
		varlist.add(null); // should not be added
		assertNull(var.getOwner());
		assertTrue(col.addAll(varlist));
		assertSame(col, var.getOwner());
		assertEquals(1, col.size());

		try
		{
			col.addAll(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCPSV extends AbstractRepositoryEntity implements CpsVariable
	{
		private static final long serialVersionUID = 1L;
	}
}
