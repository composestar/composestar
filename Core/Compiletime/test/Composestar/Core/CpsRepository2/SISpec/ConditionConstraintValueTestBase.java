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

package Composestar.Core.CpsRepository2.SISpec;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConditionConstraintValue;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class ConditionConstraintValueTestBase extends RepositoryEntityTestBase
{
	protected SICondition cond;

	protected ConditionConstraintValue ccv;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		cond = new DummySIC();
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.Constraints.ConditionConstraintValue#getCondition()}
	 * .
	 */
	public void testGetCondition()
	{
		assertSame(cond, ccv.getCondition());
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummySIC extends AbstractQualifiedRepositoryEntity implements SICondition
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SICondition#
		 * getMethodReference()
		 */
		public MethodReference getMethodReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SICondition#
		 * setMethodReference
		 * (Composestar.Core.CpsRepository2.References.MethodReference)
		 */
		public void setMethodReference(MethodReference ref) throws NullPointerException, IllegalArgumentException
		{}

	}
}
