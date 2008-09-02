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
import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * @author "Michiel Hendriks"
 */
public abstract class FilterModuleBindingTestBase extends RepositoryEntityTestBase
{
	protected FilterModuleBinding fmb;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#setSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)}
	 * .
	 */
	public void testSetSelector()
	{
		assertNull(fmb.getSelector());
		Selector sel = new DummySel("foo");
		fmb.setSelector(sel);
		assertSame(sel, fmb.getSelector());
		try
		{
			fmb.setSelector(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#setFilterModuleReference(Composestar.Core.CpsRepository2.References.FilterModuleReference)}
	 * .
	 */
	public void testSetFilterModuleReference()
	{
		assertNull(fmb.getFilterModuleReference());
		FilterModuleReference fmr = new DummyFMR();
		fmb.setFilterModuleReference(fmr);
		assertSame(fmr, fmb.getFilterModuleReference());
		try
		{
			fmb.setFilterModuleReference(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#setParameterValues(java.util.List)}
	 * .
	 */
	public void testSetParameterValues()
	{
		assertNotNull(fmb.getParameterValues());
		assertEquals(0, fmb.getParameterValues().size());

		List<FMParameterValue<?>> params = new ArrayList<FMParameterValue<?>>();
		params.add(new DummyFMPV());
		fmb.setParameterValues(params);
		assertEquals(1, fmb.getParameterValues().size());

		params.add(new DummyFMPV());
		assertEquals(1, fmb.getParameterValues().size());
		assertEquals(2, params.size());
		fmb.setParameterValues(params);
		assertEquals(2, fmb.getParameterValues().size());

		// clear the params
		fmb.setParameterValues(new ArrayList<FMParameterValue<?>>());
		assertEquals(0, fmb.getParameterValues().size());
		assertEquals(2, params.size());

		try
		{
			fmb.setParameterValues(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author "Michiel Hendriks"
	 */
	protected class DummySel extends AbstractQualifiedRepositoryEntity implements Selector
	{
		private static final long serialVersionUID = 6233007188478847194L;

		/**
		 * @param name
		 */
		public DummySel(String name)
		{
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SuperImposition.Selector#getSelection
		 * ()
		 */
		public Collection<ProgramElement> getSelection()
		{
			return null;
		}
	}

	/**
	 * @author "Michiel Hendriks"
	 */
	protected class DummyFMR implements FilterModuleReference
	{
		private static final long serialVersionUID = 7906130603179976855L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public FilterModule getReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(FilterModule element) throws UnsupportedOperationException
		{}

	}

	/**
	 * @author "Michiel Hendriks"
	 */
	protected class DummyFMPV implements FMParameterValue<Object>
	{
		private static final long serialVersionUID = -5807844951769781151L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FMParams.FMParameterValue#getValues()
		 */
		public Collection<Object> getValues()
		{
			return null;
		}
	}

}
