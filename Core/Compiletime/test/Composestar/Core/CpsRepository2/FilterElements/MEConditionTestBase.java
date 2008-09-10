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
import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * @author Michiel Hendriks
 */
public class MEConditionTestBase extends RepositoryEntityTestBase
{
	protected MECondition mec;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.MECondition#setCondition(Composestar.Core.CpsRepository2.FilterModules.Condition)}
	 * .
	 */
	public void testSetCondition()
	{
		Condition cond = new DummyC();
		assertNull(mec.getCondition());
		mec.setCondition(cond);
		assertNotSame(mec, cond.getOwner());
		assertSame(cond, mec.getCondition());

		try
		{
			mec.setCondition(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyC extends AbstractQualifiedRepositoryEntity implements Condition
	{
		private static final long serialVersionUID = 7587373752550510747L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.Condition#
		 * getMethodReference()
		 */
		public MethodReference getMethodReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.Condition#
		 * setMethodReference
		 * (Composestar.Core.CpsRepository2.References.MethodReference)
		 */
		public void setMethodReference(MethodReference ref) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public Condition newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}

		public ProgramElement getProgramElement()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

}
