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

package Composestar.Core.CpsRepository2.SuperImposition;

import java.util.ArrayList;
import java.util.List;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class FilterModuleConstraintTestBase extends RepositoryEntityTestBase
{
	protected static final String FMC_TYPE = "pre";

	protected FilterModuleConstraint fmc;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint#getConstraintType()}
	 * .
	 */
	public void testGetConstraintType()
	{
		assertEquals(FMC_TYPE, fmc.getConstraintType());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint#setArguments(java.util.List)}
	 * .
	 */
	public void testSetArguments()
	{
		assertNotNull(fmc.getArguments());
		assertEquals(0, fmc.getArguments().size());

		List<ConstraintValue> cvs = new ArrayList<ConstraintValue>();
		ConstraintValue cv = new DummyCV();
		try
		{
			fmc.setArguments(cvs);
		}
		catch (IllegalArgumentException e)
		{
		}
		assertEquals(0, fmc.getArguments().size());
		cvs.add(cv);
		cvs.add(cv);
		assertEquals(0, fmc.getArguments().size());
		fmc.setArguments(cvs);
		assertEquals(2, fmc.getArguments().size());
		assertTrue(fmc.getArguments().contains(cv));
		cvs.add(cv);
		assertEquals(2, fmc.getArguments().size());
		fmc.setArguments(cvs);
		assertEquals(3, fmc.getArguments().size());

		try
		{
			fmc.setArguments(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCV extends AbstractRepositoryEntity implements ConstraintValue
	{
		private static final long serialVersionUID = 1L;
	}
}
