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

package Composestar.Core.CpsRepository2;

import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint;
import Composestar.Core.CpsRepository2.SuperImposition.SICondition;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class CpsConcernTestBase extends ConcernTestBase
{
	protected CpsConcern cpsconcern;

	protected FilterModule fm1, fm2;

	protected SuperImposition si1, si2;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fm1 = new DummyFM("fm1");
		fm2 = new DummyFM("fm2");
		si1 = new DummySI();
		si2 = new DummySI();
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#addFilterModule(Composestar.Core.CpsRepository2.FilterModules.FilterModule)}
	 * .
	 */
	public void testAddFilterModule()
	{
		assertTrue(cpsconcern.addFilterModule(fm1));
		assertTrue(cpsconcern.addFilterModule(fm2));
		assertFalse(cpsconcern.addFilterModule(fm1));
		assertFalse(cpsconcern.addFilterModule(fm2));
		try
		{
			cpsconcern.addFilterModule(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		assertEquals(exampleFQN + "." + fm1.getName(), fm1.getFullyQualifiedName());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getFilterModule(java.lang.String)}
	 * .
	 */
	public void testGetFilterModule()
	{
		cpsconcern.addFilterModule(fm1);
		cpsconcern.addFilterModule(fm2);
		assertSame(fm1, cpsconcern.getFilterModule(fm1.getName()));
		assertSame(fm2, cpsconcern.getFilterModule(fm2.getName()));
		assertNull(cpsconcern.getFilterModule("asdf"));
		assertNull(cpsconcern.getFilterModule(null));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getFilterModules()}
	 * .
	 */
	public void testGetFilterModules()
	{
		assertNotNull(cpsconcern.getFilterModules());
		cpsconcern.addFilterModule(fm1);
		Collection<FilterModule> fms = cpsconcern.getFilterModules();
		assertNotNull(fms);
		assertEquals(1, fms.size());
		assertTrue(fms.contains(fm1));
		cpsconcern.addFilterModule(fm2);
		assertEquals(2, fms.size());
		assertTrue(fms.contains(fm2));
		fms = cpsconcern.getFilterModules();
		assertEquals(2, fms.size());
		assertTrue(fms.contains(fm2));
		try
		{
			fms.add(fm2);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getSuperImposition()}
	 * .
	 */
	public void testGetSuperImposition()
	{
		assertNull(cpsconcern.getSuperImposition());
		cpsconcern.setSuperImposition(si1);
		assertSame(si1, cpsconcern.getSuperImposition());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#removeFilterModule(Composestar.Core.CpsRepository2.FilterModules.FilterModule)}
	 * .
	 */
	public void testRemoveFilterModuleFilterModule()
	{
		cpsconcern.addFilterModule(fm1);
		assertNull(cpsconcern.removeFilterModule(fm2));
		assertSame(fm1, cpsconcern.removeFilterModule(fm1));
		assertNull(cpsconcern.removeFilterModule(fm1));
		try
		{
			fm1 = null;
			cpsconcern.removeFilterModule(fm1);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#removeFilterModule(java.lang.String)}
	 * .
	 */
	public void testRemoveFilterModuleString()
	{
		cpsconcern.addFilterModule(fm1);
		assertSame(fm1, cpsconcern.removeFilterModule(fm1.getName()));
		assertNull(cpsconcern.removeFilterModule("sfgasgag"));
		String s = null;
		cpsconcern.removeFilterModule(s);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#setSuperImposition(Composestar.Core.CpsRepository2.SuperImposition.SuperImposition)}
	 * .
	 */
	public void testSetSuperImposition()
	{
		assertNull(cpsconcern.getSuperImposition());
		cpsconcern.setSuperImposition(si1);
		assertSame(si1, cpsconcern.getSuperImposition());
		assertEquals(exampleFQN + "." + SuperImposition.NAME, si1.getFullyQualifiedName());
		cpsconcern.setSuperImposition(si1);
		cpsconcern.setSuperImposition(null);
		cpsconcern.setSuperImposition(si2);
		assertSame(si2, cpsconcern.getSuperImposition());
		try
		{
			cpsconcern.setSuperImposition(si1);
		}
		catch (IllegalStateException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	static class DummyFM extends AbstractQualifiedRepositoryEntity implements FilterModule
	{
		private static final long serialVersionUID = 1L;

		public DummyFM(String name)
		{
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public FilterModule getReference()
		{
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return getFullyQualifiedName();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(FilterModule element) throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addParameter
		 * (Composestar.Core.CpsRepository2.FMParams.FMParameter)
		 */
		public boolean addParameter(FMParameter param) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addVariable
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
		 */
		public boolean addVariable(FilterModuleVariable var) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getCondition
		 * (java.lang.String)
		 */
		public Composestar.Core.CpsRepository2.FilterModules.Condition getCondition(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getExternal
		 * (java.lang.String)
		 */
		public External getExternal(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * getInputFilterExpression()
		 */
		public FilterExpression getInputFilterExpression()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getInternal
		 * (java.lang.String)
		 */
		public Internal getInternal(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * getOutputFilterExpression()
		 */
		public FilterExpression getOutputFilterExpression()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameter
		 * (java.lang.String)
		 */
		public FMParameter getParameter(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameters
		 * ()
		 */
		public List<FMParameter> getParameters()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariable
		 * (java.lang.String)
		 */
		public FilterModuleVariable getVariable(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#hasParameters
		 * ()
		 */
		public boolean hasParameters()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)
		 */
		public FilterModuleVariable removeVariable(FilterModuleVariable var) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable
		 * (java.lang.String)
		 */
		public FilterModuleVariable removeVariable(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * setInputFilterExpression
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
		 */
		public void setInputFilterExpression(FilterExpression expr)
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterModules.FilterModule#
		 * setOutputFilterExpression
		 * (Composestar.Core.CpsRepository2.FilterModules.FilterExpression)
		 */
		public void setOutputFilterExpression(FilterExpression expr)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public FilterModule newInstance(Instantiator context) throws UnsupportedOperationException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariables
		 * ()
		 */
		public Collection<FilterModuleVariable> getVariables()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#addFilter
		 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
		 */
		public boolean addFilter(Filter filter) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilter
		 * (java.lang.String)
		 */
		public Filter getFilter(String filterName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilters
		 * ()
		 */
		public Collection<Filter> getFilters()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
		 * (Composestar.Core.CpsRepository2.FilterModules.Filter)
		 */
		public Filter removeFilter(Filter filter) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter
		 * (java.lang.String)
		 */
		public Filter removeFilter(String filterName)
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	static class DummySI extends AbstractQualifiedRepositoryEntity implements SuperImposition
	{
		private static final long serialVersionUID = 1L;

		public DummySI()
		{
			super(NAME);
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * addAnnotationBinding
		 * (Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
		 */
		public void addAnnotationBinding(AnnotationBinding ab) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * addCondition
		 * (Composestar.Core.CpsRepository2.SuperImposition.Condition)
		 */
		public boolean addCondition(SICondition cond) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * addFilterModuleBinding
		 * (Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
		 */
		public void addFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * addFilterModuleConstraint
		 * (Composestar.Core.CpsRepository2.SuperImposition
		 * .FilterModuleConstraint)
		 */
		public void addFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * addSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
		 */
		public boolean addSelector(Selector newSel) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getAnnotationBindings()
		 */
		public Collection<AnnotationBinding> getAnnotationBindings()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getConditions()
		 */
		public Collection<SICondition> getConditions()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getFilterModuleBindings()
		 */
		public Collection<FilterModuleBinding> getFilterModuleBindings()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getFilterModuleConstraints()
		 */
		public Collection<FilterModuleConstraint> getFilterModuleConstraints()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getSelector(java.lang.String)
		 */
		public Selector getSelector(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getSelectors()
		 */
		public Collection<Selector> getSelectors()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeAnnotationBinding
		 * (Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
		 */
		public AnnotationBinding removeAnnotationBinding(AnnotationBinding ab) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeCondition
		 * (Composestar.Core.CpsRepository2.SuperImposition.Condition)
		 */
		public SICondition removeCondition(SICondition cond) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeCondition(java.lang.String)
		 */
		public SICondition removeCondition(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeFilterModuleBinding
		 * (Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
		 */
		public FilterModuleBinding removeFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeFilterModuleConstraint
		 * (Composestar.Core.CpsRepository2.SuperImposition
		 * .FilterModuleConstraint)
		 */
		public FilterModuleConstraint removeFilterModuleConstraint(FilterModuleConstraint fmc)
				throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeSelector
		 * (Composestar.Core.CpsRepository2.SuperImposition.Selector)
		 */
		public Selector removeSelector(Selector sel) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * removeSelector(java.lang.String)
		 */
		public Selector removeSelector(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SuperImposition.SuperImposition#
		 * getCondition(java.lang.String)
		 */
		public SICondition getCondition(String name)
		{
			return null;
		}

	}
}
