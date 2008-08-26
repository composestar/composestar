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

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * @author Michiel Hendriks
 */
public abstract class SuperImpositionTestBase extends TestCase
{
	protected SuperImposition si;

	protected AnnotationBinding ab1, ab2;

	protected FilterModuleBinding fmb1, fmb2;

	protected FilterModuleConstraint fmc1, fmc2;

	protected Selector sel1, sel2;

	protected SICondition con1, con2;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ab1 = new DummyAB();
		ab2 = new DummyAB();
		fmb1 = new DummyFMB();
		fmb2 = new DummyFMB();
		fmc1 = new DummyFMC();
		fmc2 = new DummyFMC();
		sel1 = new DummySel("selector1");
		sel2 = new DummySel("selector2");
		con1 = new DummyCond("condition1");
		con2 = new DummyCond("condition2");
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#addAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)}.
	 */
	public void testAddAnnotationBinding()
	{
		si.addAnnotationBinding(ab1);
		assertSame(si, ab1.getOwner());
		si.addAnnotationBinding(ab2);
		si.addAnnotationBinding(ab1);
		try
		{
			si.addAnnotationBinding(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#addCondition(Composestar.Core.CpsRepository2.SuperImposition.SICondition)}.
	 */
	public void testAddCondition()
	{
		assertTrue(si.addCondition(con1));
		assertSame(si, con1.getOwner());
		assertTrue(si.addCondition(con2));
		assertFalse(si.addCondition(con1));
		try
		{
			si.addCondition(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#addFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)}.
	 */
	public void testAddFilterModuleBinding()
	{
		si.addFilterModuleBinding(fmb1);
		assertSame(si, fmb1.getOwner());
		si.addFilterModuleBinding(fmb2);
		si.addFilterModuleBinding(fmb1);
		try
		{
			si.addFilterModuleBinding(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#addFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)}.
	 */
	public void testAddFilterModuleConstraint()
	{
		si.addFilterModuleConstraint(fmc1);
		assertSame(si, fmc1.getOwner());
		si.addFilterModuleConstraint(fmc2);
		si.addFilterModuleConstraint(fmc1);
		try
		{
			si.addFilterModuleConstraint(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#addSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)}.
	 */
	public void testAddSelector()
	{
		assertTrue(si.addSelector(sel1));
		assertSame(si, sel1.getOwner());
		assertTrue(si.addSelector(sel2));
		assertFalse(si.addSelector(sel1));
		try
		{
			si.addSelector(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getAnnotationBindings()}.
	 */
	public void testGetAnnotationBindings()
	{
		Collection<AnnotationBinding> abs = si.getAnnotationBindings();
		assertNotNull(abs);
		assertTrue(abs.isEmpty());
		si.addAnnotationBinding(ab1);
		assertTrue(abs.contains(ab1));
		abs = si.getAnnotationBindings();
		assertTrue(abs.contains(ab1));
		try
		{
			abs.add(ab1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getCondition(java.lang.String)}.
	 */
	public void testGetCondition()
	{
		assertNull(si.getCondition(null));
		assertNull(si.getCondition(""));
		assertNull(si.getCondition(con1.getName()));
		si.addCondition(con1);
		assertSame(con1, si.getCondition(con1.getName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getConditions()}.
	 */
	public void testGetConditions()
	{
		Collection<SICondition> cons = si.getConditions();
		assertNotNull(cons);
		assertTrue(cons.isEmpty());
		si.addCondition(con1);
		assertTrue(cons.contains(con1));
		cons = si.getConditions();
		assertTrue(cons.contains(con1));
		try
		{
			cons.add(con1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getFilterModuleBindings()}.
	 */
	public void testGetFilterModuleBindings()
	{
		Collection<FilterModuleBinding> fmbs = si.getFilterModuleBindings();
		assertNotNull(fmbs);
		assertTrue(fmbs.isEmpty());
		si.addFilterModuleBinding(fmb1);
		assertTrue(fmbs.contains(fmb1));
		fmbs = si.getFilterModuleBindings();
		assertTrue(fmbs.contains(fmb1));
		try
		{
			fmbs.add(fmb1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getFilterModuleConstraints()}.
	 */
	public void testGetFilterModuleConstraints()
	{
		Collection<FilterModuleConstraint> fmcs = si.getFilterModuleConstraints();
		assertNotNull(fmcs);
		assertTrue(fmcs.isEmpty());
		si.addFilterModuleConstraint(fmc1);
		assertTrue(fmcs.contains(fmc1));
		fmcs = si.getFilterModuleConstraints();
		assertTrue(fmcs.contains(fmc1));
		try
		{
			fmcs.add(fmc1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getSelector(java.lang.String)}.
	 */
	public void testGetSelector()
	{
		assertNull(si.getSelector(null));
		assertNull(si.getSelector(""));
		assertNull(si.getSelector(sel1.getName()));
		si.addSelector(sel1);
		assertSame(sel1, si.getSelector(sel1.getName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#getSelectors()}.
	 */
	public void testGetSelectors()
	{
		Collection<Selector> sels = si.getSelectors();
		assertNotNull(sels);
		assertTrue(sels.isEmpty());
		si.addSelector(sel1);
		assertTrue(sels.contains(sel1));
		sels = si.getSelectors();
		assertTrue(sels.contains(sel1));
		try
		{
			sels.add(sel1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeAnnotationBinding(Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding)}.
	 */
	public void testRemoveAnnotationBinding()
	{
		assertFalse(si.removeAnnotationBinding(ab1));
		si.addAnnotationBinding(ab1);
		assertTrue(si.removeAnnotationBinding(ab1));
		try
		{
			si.removeAnnotationBinding(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeCondition(Composestar.Core.CpsRepository2.SuperImposition.SICondition)}.
	 */
	public void testRemoveConditionCondition()
	{
		assertFalse(si.removeCondition(con1));
		si.addCondition(con1);
		assertTrue(si.removeCondition(con1));
		assertFalse(si.removeCondition(con1));
		try
		{
			con1 = null;
			si.removeCondition(con1);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeCondition(java.lang.String)}.
	 */
	public void testRemoveConditionString()
	{
		assertNull(si.removeCondition(con1.getName()));
		si.addCondition(con1);
		assertSame(con1, si.removeCondition(con1.getName()));
		assertNull(si.removeCondition(con1.getName()));
		String s = null;
		assertNull(si.removeCondition(s));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeFilterModuleBinding(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding)}.
	 */
	public void testRemoveFilterModuleBinding()
	{
		assertFalse(si.removeFilterModuleBinding(fmb1));
		si.addFilterModuleBinding(fmb1);
		assertTrue(si.removeFilterModuleBinding(fmb1));
		try
		{
			si.removeFilterModuleBinding(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeFilterModuleConstraint(Composestar.Core.CpsRepository2.SuperImposition.FilterModuleConstraint)}.
	 */
	public void testRemoveFilterModuleConstraint()
	{
		assertFalse(si.removeFilterModuleConstraint(fmc1));
		si.addFilterModuleConstraint(fmc1);
		assertTrue(si.removeFilterModuleConstraint(fmc1));
		try
		{
			si.removeFilterModuleConstraint(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)}.
	 */
	public void testRemoveSelectorSelector()
	{
		assertFalse(si.removeSelector(sel1));
		si.addSelector(sel1);
		assertTrue(si.removeSelector(sel1));
		assertFalse(si.removeSelector(sel1));
		try
		{
			sel1 = null;
			si.removeSelector(sel1);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SuperImposition.SuperImpositionImpl#removeSelector(java.lang.String)}.
	 */
	public void testRemoveSelectorString()
	{
		assertNull(si.removeSelector(sel1.getName()));
		si.addSelector(sel1);
		assertSame(sel1, si.removeSelector(sel1.getName()));
		assertNull(si.removeSelector(sel1.getName()));
		String s = null;
		assertNull(si.removeSelector(s));
	}

	protected static class DummyAB extends AbstractRepositoryEntity implements AnnotationBinding
	{
		private static final long serialVersionUID = -5674700229605225217L;

		public void addAnnotation(TypeReference annotationType) throws NullPointerException
		{}

		public Collection<TypeReference> getAnnotations()
		{
			return null;
		}

		public Selector getSelector()
		{
			return null;
		}

		public boolean removeAnnotation(TypeReference annotationType) throws NullPointerException
		{
			return false;
		}

		public TypeReference removeAnnotation(String referenceId)
		{
			return null;
		}

		public void setSelector(Selector sel) throws NullPointerException
		{}
	}

	protected static class DummyFMB extends AbstractRepositoryEntity implements FilterModuleBinding
	{
		private static final long serialVersionUID = -3638960435963408117L;

		public FilterModuleReference getFilterModuleReference()
		{
			return null;
		}

		public Selector getSelector()
		{
			return null;
		}

		public void setFilterModuleReference(FilterModuleReference fmRef) throws NullPointerException
		{}

		public void setSelector(Selector sel) throws NullPointerException
		{}

		public void addParameterValue(FMParameterValue<?> value) throws NullPointerException
		{}

		public List<FMParameterValue<?>> getParameterValues()
		{
			return null;
		}

		public void setParameterValues(List<FMParameterValue<?>> list) throws NullPointerException
		{}
	}

	protected static class DummyFMC extends AbstractRepositoryEntity implements FilterModuleConstraint
	{
		private static final long serialVersionUID = -7463428959390953212L;

		public List<ConstraintValue> getArguments()
		{
			return null;
		}

		public String getConstraintType()
		{
			return null;
		}

		public void setArguments(List<ConstraintValue> args) throws NullPointerException, IllegalArgumentException
		{}
	}

	protected static class DummySel extends AbstractQualifiedRepositoryEntity implements Selector
	{
		private static final long serialVersionUID = 2769161875238777813L;

		public DummySel(String name)
		{
			super(name);
		}

		public Collection<ProgramElement> getSelection()
		{
			return null;
		}
	}

	protected static class DummyCond extends AbstractQualifiedRepositoryEntity implements SICondition
	{
		private static final long serialVersionUID = 832788470295797104L;

		public DummyCond(String name)
		{
			super(name);
		}

		public MethodReference getMethodReference()
		{
			return null;
		}

		public void setMethodReference(MethodReference mref) throws NullPointerException, IllegalArgumentException
		{}
	}
}
