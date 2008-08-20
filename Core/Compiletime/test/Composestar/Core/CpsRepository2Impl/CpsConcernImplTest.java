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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.CpsConcern;
import Composestar.Core.CpsRepository2.FMParams.Parameter;
import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.InstantiatableContext;
import Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsRepository2.SuperImposition.Condition;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;

/**
 * @author Michiel Hendriks
 */
public class CpsConcernImplTest extends AbstractConcernTestBase
{
	protected CpsConcern cpsconcern;

	protected FilterModule fm1, fm2;

	protected SuperImposition si1, si2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		List<String> ns = Arrays.asList(exampleNS.split("\\."));
		CpsConcernImpl cpscrnimpl = new CpsConcernImpl(exampleName, ns);
		cpsconcern = cpscrnimpl;
		concern = cpscrnimpl;
		fm1 = new DummyFM("fm1");
		fm2 = new DummyFM("fm2");
		si1 = new DummySI();
		si2 = new DummySI();
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#addFilterModule(Composestar.Core.CpsRepository2.FilterModules.FilterModule)}.
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
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getFilterModule(java.lang.String)}.
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
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getFilterModules()}.
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
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#getSuperImposition()}.
	 */
	public void testGetSuperImposition()
	{
		assertNull(cpsconcern.getSuperImposition());
		cpsconcern.setSuperImposition(si1);
		assertSame(si1, cpsconcern.getSuperImposition());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#removeFilterModule(Composestar.Core.CpsRepository2.FilterModules.FilterModule)}.
	 */
	public void testRemoveFilterModuleFilterModule()
	{
		cpsconcern.addFilterModule(fm1);
		assertFalse(cpsconcern.removeFilterModule(fm2));
		assertTrue(cpsconcern.removeFilterModule(fm1));
		assertFalse(cpsconcern.removeFilterModule(fm1));
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
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#removeFilterModule(java.lang.String)}.
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
	 * {@link Composestar.Core.CpsRepository2Impl.CpsConcernImpl#setSuperImposition(Composestar.Core.CpsRepository2.SuperImposition.SuperImposition)}.
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

		public FilterModule getReference()
		{
			return null;
		}

		public String getReferenceId()
		{
			return null;
		}

		public boolean isResolved()
		{
			return false;
		}

		public void setReference(FilterModule element)
		{}

		public void setReferenceId(String id) throws UnsupportedOperationException
		{}

		public boolean addParameter(Parameter param) throws NullPointerException
		{
			return false;
		}

		public boolean addVariable(FilterModuleVariable var) throws NullPointerException
		{
			return false;
		}

		public Composestar.Core.CpsRepository2.FilterModules.Condition getCondition(String name)
		{
			return null;
		}

		public External getExternal(String name)
		{
			return null;
		}

		public FilterExpression getInputFilterExpression()
		{
			return null;
		}

		public Internal getInternal(String name)
		{
			return null;
		}

		public FilterExpression getOutputFilterExpression()
		{
			return null;
		}

		public Parameter getParameter(String name)
		{
			return null;
		}

		public List<Parameter> getParameters()
		{
			return null;
		}

		public FilterModuleVariable getVariable(String name)
		{
			return null;
		}

		public boolean hasParameters()
		{
			return false;
		}

		public boolean removeVariable(FilterModuleVariable var) throws NullPointerException
		{
			return false;
		}

		public FilterModuleVariable removeVariable(String name)
		{
			return null;
		}

		public void setInputFilterExpression(FilterExpression expr)
		{}

		public void setOutputFilterExpression(FilterExpression expr)
		{}

		public FilterModule newInstance(InstantiatableContext context)
		{
			return null;
		}

		public Collection<FilterModuleVariable> getVariables()
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
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
		 */
		public void addAnnotationBinding(AnnotationBinding ab) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addCondition(Composestar.Core.CpsRepository2.SuperImposition.Condition)
		 */
		public boolean addCondition(Condition cond) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
		 */
		public void addFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)
		 */
		public void addFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#addSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
		 */
		public boolean addSelector(Selector newSel) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getAnnotationBindings()
		 */
		public Collection<AnnotationBinding> getAnnotationBindings()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getConditions()
		 */
		public Collection<Condition> getConditions()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getFilterModuleBindings()
		 */
		public Collection<FilterModuleBinding> getFilterModuleBindings()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getFilterModuleConstraints()
		 */
		public Collection<FilterModuleConstraint> getFilterModuleConstraints()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getSelector(java.lang.String)
		 */
		public Selector getSelector(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getSelectors()
		 */
		public Collection<Selector> getSelectors()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)
		 */
		public boolean removeAnnotationBinding(AnnotationBinding ab) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeCondition(Composestar.Core.CpsRepository2.SuperImposition.Condition)
		 */
		public boolean removeCondition(Condition cond) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeCondition(java.lang.String)
		 */
		public Condition removeCondition(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)
		 */
		public boolean removeFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)
		 */
		public boolean removeFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
		 */
		public boolean removeSelector(Selector sel) throws NullPointerException
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#removeSelector(java.lang.String)
		 */
		public Selector removeSelector(String name)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.CpsRepository2.SuperImposition.SuperImposition#getCondition(java.lang.String)
		 */
		public Condition getCondition(String name)
		{
			return null;
		}

	}
}
