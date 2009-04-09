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

package Composestar.Core.CpsRepository2.SISpec.Constraints;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class ConstraintTestBase extends RepositoryEntityTestBase
{
	protected static final String FMC_TYPE = "pre";

	protected Constraint fmc;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint#getConstraintType()}
	 * .
	 */
	public void testGetConstraintType()
	{
		assertEquals(FMC_TYPE, fmc.getConstraintType());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint#setArguments(java.util.List)}
	 * .
	 */
	public void testSetArguments()
	{
		assertNotNull(fmc.getArguments());
		assertEquals(0, fmc.getArguments().length);
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCV extends AbstractRepositoryEntity implements ConstraintValue
	{
		private static final long serialVersionUID = 1L;

		public String getStringValue()
		{
			return null;
		}
	}
}
