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

package Composestar.Core.CpsRepository2.FilterModules;

import java.util.Collection;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class FilterModuleTestBase extends QualifiedRepositoryEntityTestBase
{
	protected FilterModule fm;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#hasParameters()}
	 * .
	 */
	public void testHasParameters()
	{
		assertFalse(fm.hasParameters());
		fm.addParameter(new DummyFMP("?foo"));
		assertTrue(fm.hasParameters());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#addParameter(Composestar.Core.CpsRepository2.FMParams.FMParameter)}
	 * .
	 */
	public void testAddParameter()
	{
		FMParameter par1 = new DummyFMP("?foo");
		FMParameter par2 = new DummyFMP("??bar");
		assertFalse(fm.hasParameters());
		assertTrue(fm.addParameter(par1));
		assertTrue(fm.getParameters().contains(par1));
		assertTrue(fm.addParameter(par2));
		assertTrue(fm.getParameters().contains(par2));
		assertFalse(fm.addParameter(par1));
		assertSame(fm, par1.getOwner());
		assertSame(fm, par2.getOwner());
		try
		{
			fm.addParameter(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameters()}
	 * .
	 */
	public void testGetParameters()
	{
		assertNotNull(fm.getParameters());
		assertEquals(0, fm.getParameters().size());
		try
		{
			fm.getParameters().add(null);
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getParameter(java.lang.String)}
	 * .
	 */
	public void testGetParameter()
	{
		FMParameter par1 = new DummyFMP("?foo");
		assertNull(fm.getParameter(par1.getName()));
		fm.addParameter(par1);
		assertSame(par1, fm.getParameter(par1.getName()));
		assertNull(fm.getParameter(null));
		assertNull(fm.getParameter(""));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#addVariable(Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)}
	 * .
	 */
	public void testAddVariable()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");
		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));
		assertFalse(fm.addVariable(internal));
		assertFalse(fm.addVariable(external));
		assertFalse(fm.addVariable(condition));
		assertSame(fm, internal.getOwner());
		assertSame(fm, external.getOwner());
		assertSame(fm, external.getOwner());
		try
		{
			fm.addVariable(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariable(java.lang.String)}
	 * .
	 */
	public void testGetVariable()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");

		assertNull(fm.getVariable(internal.getName()));
		assertNull(fm.getVariable(external.getName()));
		assertNull(fm.getVariable(condition.getName()));

		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));

		assertSame(internal, fm.getVariable(internal.getName()));
		assertSame(external, fm.getVariable(external.getName()));
		assertSame(condition, fm.getVariable(condition.getName()));

		assertNull(fm.getVariable(null));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getVariables()}
	 * .
	 */
	public void testGetVariables()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");

		assertNotNull(fm.getVariables());
		assertEquals(0, fm.getVariables().size());

		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));

		assertEquals(3, fm.getVariables().size());

		assertFalse(fm.addVariable(internal));
		assertFalse(fm.addVariable(external));
		assertFalse(fm.addVariable(condition));

		assertEquals(3, fm.getVariables().size());

		try
		{
			fm.getVariables().add(null);
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getInternal(java.lang.String)}
	 * .
	 */
	public void testGetInternal()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");

		assertNull(fm.getVariable(internal.getName()));
		assertNull(fm.getVariable(external.getName()));
		assertNull(fm.getVariable(condition.getName()));

		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));

		assertSame(internal, fm.getInternal(internal.getName()));
		assertNull(fm.getInternal(external.getName()));
		assertNull(fm.getInternal(condition.getName()));

		assertNull(fm.getInternal(null));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getExternal(java.lang.String)}
	 * .
	 */
	public void testGetExternal()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");

		assertNull(fm.getVariable(internal.getName()));
		assertNull(fm.getVariable(external.getName()));
		assertNull(fm.getVariable(condition.getName()));

		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));

		assertNull(fm.getExternal(internal.getName()));
		assertSame(external, fm.getExternal(external.getName()));
		assertNull(fm.getExternal(condition.getName()));

		assertNull(fm.getExternal(null));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getCondition(java.lang.String)}
	 * .
	 */
	public void testGetCondition()
	{
		Internal internal = new DummyInt("foo");
		External external = new DummyExt("bar");
		Condition condition = new DummyCond("quux");

		assertNull(fm.getVariable(internal.getName()));
		assertNull(fm.getVariable(external.getName()));
		assertNull(fm.getVariable(condition.getName()));

		assertTrue(fm.addVariable(internal));
		assertTrue(fm.addVariable(external));
		assertTrue(fm.addVariable(condition));

		assertNull(fm.getCondition(internal.getName()));
		assertNull(fm.getCondition(external.getName()));
		assertSame(condition, fm.getCondition(condition.getName()));

		assertNull(fm.getCondition(null));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable(Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable)}
	 * .
	 */
	public void testRemoveVariableFilterModuleVariable()
	{
		Internal internal = new DummyInt("foo");
		assertNull(fm.removeVariable(internal));
		assertTrue(fm.addVariable(internal));
		assertSame(internal, fm.removeVariable(internal));
		assertTrue(fm.addVariable(internal));

		internal = null;
		try
		{
			fm.removeVariable(internal);
			fail();
		}
		catch (NullPointerException e)
		{

		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeVariable(java.lang.String)}
	 * .
	 */
	public void testRemoveVariableString()
	{
		Internal internal = new DummyInt("foo");
		assertNull(fm.removeVariable(internal.getName()));
		assertTrue(fm.addVariable(internal));
		assertSame(internal, fm.removeVariable(internal.getName()));
		assertTrue(fm.addVariable(internal));

		String s = null;
		assertNull(fm.removeVariable(s));
		assertNull(fm.removeVariable(""));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#setInputFilterExpression(Composestar.Core.CpsRepository2.FilterModules.FilterExpression)}
	 * .
	 */
	public void testSetInputFilterExpression()
	{
		FilterExpression fex = new DummyFEX();
		assertNull(fm.getInputFilterExpression());
		fm.setInputFilterExpression(fex);
		assertSame(fex, fm.getInputFilterExpression());
		fm.setInputFilterExpression(null);
		assertNull(fm.getInputFilterExpression());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#setOutputFilterExpression(Composestar.Core.CpsRepository2.FilterModules.FilterExpression)}
	 * .
	 */
	public void testSetOutputFilterExpression()
	{
		FilterExpression fex = new DummyFEX();
		assertNull(fm.getOutputFilterExpression());
		fm.setOutputFilterExpression(fex);
		assertSame(fex, fm.getOutputFilterExpression());
		fm.setOutputFilterExpression(null);
		assertNull(fm.getOutputFilterExpression());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#addFilter(Composestar.Core.CpsRepository2.FilterModules.Filter)}
	 * .
	 */
	public void testAddFilter()
	{
		Filter flt1 = new DummyFLT("foo");
		Filter flt2 = new DummyFLT("bar");
		assertTrue(fm.addFilter(flt1));
		assertTrue(fm.addFilter(flt2));
		assertFalse(fm.addFilter(flt1));
		assertFalse(fm.addFilter(flt2));
		try
		{
			fm.addFilter(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilter(java.lang.String)}
	 * .
	 */
	public void testGetFilter()
	{
		Filter flt1 = new DummyFLT("foo");
		Filter flt2 = new DummyFLT("bar");
		assertNull(fm.getFilter(flt1.getName()));
		assertNull(fm.getFilter(flt2.getName()));
		assertTrue(fm.addFilter(flt1));
		assertTrue(fm.addFilter(flt2));
		assertSame(flt1, fm.getFilter(flt1.getName()));
		assertSame(flt2, fm.getFilter(flt2.getName()));
		assertNull(fm.getFilter(null));
		assertNull(fm.getFilter(""));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter(Composestar.Core.CpsRepository2.FilterModules.Filter)}
	 * .
	 */
	public void testRemoveFilterFilter()
	{
		Filter flt1 = new DummyFLT("foo");
		Filter flt2 = new DummyFLT("bar");
		assertNull(fm.removeFilter(flt1));
		assertNull(fm.removeFilter(flt2));
		assertTrue(fm.addFilter(flt1));
		assertTrue(fm.addFilter(flt2));
		assertSame(flt1, fm.removeFilter(flt1));
		assertSame(flt2, fm.removeFilter(flt2));
		try
		{
			flt1 = null;
			assertNull(fm.removeFilter(flt1));
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#removeFilter(java.lang.String)}
	 * .
	 */
	public void testRemoveFilterString()
	{
		Filter flt1 = new DummyFLT("foo");
		Filter flt2 = new DummyFLT("bar");
		assertNull(fm.removeFilter(flt1.getName()));
		assertNull(fm.removeFilter(flt2.getName()));
		assertTrue(fm.addFilter(flt1));
		assertTrue(fm.addFilter(flt2));
		assertSame(flt1, fm.removeFilter(flt1.getName()));
		assertSame(flt2, fm.removeFilter(flt2.getName()));
		String s = null;
		assertNull(fm.removeFilter(s));
		assertNull(fm.removeFilter(""));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule#getFilters()}
	 * .
	 */
	public void testGetFilters()
	{
		Filter flt1 = new DummyFLT("foo");
		Filter flt2 = new DummyFLT("bar");
		assertNotNull(fm.getFilters());
		assertEquals(0, fm.getFilters().size());
		assertTrue(fm.addFilter(flt1));
		assertTrue(fm.addFilter(flt2));
		assertEquals(2, fm.getFilters().size());
		assertSame(flt2, fm.removeFilter(flt2));
		assertEquals(1, fm.getFilters().size());
		try
		{
			fm.getFilters().add(flt1);
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#dereference()}
	 * .
	 */
	public void testDereference()
	{
		try
		{
			fm.dereference();
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#getReference()}
	 * .
	 */
	public void testGetReference()
	{
		assertSame(fm, fm.getReference());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#getReferenceId()}
	 * .
	 */
	public void testGetReferenceId()
	{
		assertEquals(fm.getFullyQualifiedName(), fm.getReferenceId());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#isResolved()}
	 * .
	 */
	public void testIsResolved()
	{
		assertTrue(fm.isResolved());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#isSelfReference()}
	 * .
	 */
	public void testIsSelfReference()
	{
		assertTrue(fm.isSelfReference());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.FilterModuleImpl#setReference(Composestar.Core.CpsRepository2.FilterModules.FilterModule)}
	 * .
	 */
	public void testSetReference()
	{
		try
		{
			fm.setReference(null);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFMP extends AbstractQualifiedRepositoryEntity implements FMParameter
	{
		private static final long serialVersionUID = 1857620971248016961L;

		protected String rawName;

		/**
		 * @param name
		 */
		public DummyFMP(String name)
		{
			super(name);
			int idx = name.lastIndexOf('?');
			rawName = name.substring(idx + 1);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FMParams.FMParameter#getRawName()
		 */
		public String getRawName()
		{
			return rawName;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FMParams.FMParameter#isParameterList
		 * ()
		 */
		public boolean isParameterList()
		{
			return false;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFMV extends AbstractQualifiedRepositoryEntity implements FilterModuleVariable
	{
		private static final long serialVersionUID = 1684255190741855505L;

		public DummyFMV(String name)
		{
			super(name);
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyInt extends DummyFMV implements Internal
	{
		private static final long serialVersionUID = 917170227859766557L;

		/**
		 * @param name
		 */
		public DummyInt(String name)
		{
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Internal#setTypeReference
		 * (Composestar.Core.CpsRepository2.References.TypeReference)
		 */
		public void setTypeReference(TypeReference ref) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.InstanceContextProvider#getTypeReference
		 * ()
		 */
		public TypeReference getTypeReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public Internal newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyExt extends DummyFMV implements External
	{
		private static final long serialVersionUID = 2167005603292832782L;

		public DummyExt(String name)
		{
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.External#getMethodReference
		 * ()
		 */
		public MethodReference getMethodReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.External#setMethodReference
		 * (Composestar.Core.CpsRepository2.References.MethodReference)
		 */
		public void setMethodReference(MethodReference ref) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.External#setTypeReference
		 * (Composestar.Core.CpsRepository2.References.TypeReference)
		 */
		public void setTypeReference(TypeReference ref) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.InstanceContextProvider#getTypeReference
		 * ()
		 */
		public TypeReference getTypeReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public External newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCond extends DummyFMV implements Condition
	{
		private static final long serialVersionUID = 6337477929076890127L;

		/**
		 * @param name
		 */
		public DummyCond(String name)
		{
			super(name);
		}

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
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFEX extends AbstractRepositoryEntity implements FilterExpression
	{
		private static final long serialVersionUID = -4975010911068810760L;
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFLT extends AbstractQualifiedRepositoryEntity implements Filter
	{
		private static final long serialVersionUID = 7282748740588944029L;

		public DummyFLT(String name)
		{
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#addArgument(
		 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
		 */
		public CanonAssignment addArgument(CanonAssignment argument) throws NullPointerException,
				IllegalArgumentException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#getArgument(
		 * java.lang.String)
		 */
		public CanonAssignment getArgument(String argName)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#getArguments()
		 */
		public Collection<CanonAssignment> getArguments()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#getElementExpression
		 * ()
		 */
		public FilterElementExpression getElementExpression()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.FilterModules.Filter#getType()
		 */
		public FilterType getType()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#removeArgument
		 * (Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
		 */
		public CanonAssignment removeArgument(CanonAssignment argument) throws NullPointerException
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#setElementExpression
		 * (
		 * Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression
		 * )
		 */
		public void setElementExpression(FilterElementExpression expr) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterModules.Filter#setType(Composestar
		 * .Core.Config.FilterType)
		 */
		public void setType(FilterType type) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public Filter newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}
	}
}
