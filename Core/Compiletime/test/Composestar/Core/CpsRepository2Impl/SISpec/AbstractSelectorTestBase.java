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

package Composestar.Core.CpsRepository2Impl.SISpec;

import java.util.ArrayList;
import java.util.Collection;

import Composestar.Core.CpsRepository2.SISpec.SelectorTestBase;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author Michiel Hendriks
 */
public abstract class AbstractSelectorTestBase extends SelectorTestBase
{
	protected AbstractSelector asel;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.AbstractSelector#setSelection(java.util.Collection)}
	 * .
	 */
	public void testSetSelection()
	{
		Collection<ProgramElement> selection = new ArrayList<ProgramElement>();
		asel.setSelection(selection);
		assertEquals(0, asel.getSelection().size());
		ProgramElement pelm = new DummyPE();
		selection.add(pelm);
		assertEquals(0, asel.getSelection().size());
		asel.setSelection(selection);
		assertEquals(1, asel.getSelection().size());
		assertTrue(asel.getSelection().contains(pelm));
		try
		{
			asel.setSelection(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyPE extends ProgramElement
	{
		private static final long serialVersionUID = 2232439311827848271L;

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Collection getUnitAttributes()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
		 */
		@Override
		public String getUnitName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String
		 * )
		 */
		@Override
		public UnitResult getUnitRelation(String argumentName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
		 */
		@Override
		public String getUnitType()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String
		 * )
		 */
		@Override
		public boolean hasUnitAttribute(String attribute)
		{
			return false;
		}

	}
}
