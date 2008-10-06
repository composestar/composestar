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

package Composestar.Core.CpsRepository2Impl.FMParams;

import java.util.ArrayList;
import java.util.Collection;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValueTestBase;
import Composestar.Core.CpsRepository2.SISpec.Selector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author Michiel Hendriks
 */
public class SelectorFMParamValueTest extends FMParameterValueTestBase
{
	protected SelectorFMParamValue sfmpv;

	protected DummySel sel;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		sel = new DummySel();
		sfmpv = new SelectorFMParamValue(sel);
		fmpv = sfmpv;
		re = sfmpv;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FMParams.SelectorFMParamValue#SelectorFMParamValue(Composestar.Core.CpsRepository2.SISpec.Selector)}
	 * .
	 */
	public void testSelectorFMParamValue()
	{
		try
		{
			new SelectorFMParamValue(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FMParams.SelectorFMParamValue#loadValues()}
	 * .
	 */
	public void testLoadValues()
	{
		ProgramElement pe = new DummyPE();
		Type tpe = new DummyTPE();
		sel.values.add(pe);
		sel.values.add(new DummyPE());
		sel.values.add(tpe);
		assertEquals(3, sfmpv.getValues().size());
		boolean found = false;
		for (CpsVariable cv : sfmpv.getValues())
		{
			assertTrue(cv instanceof CpsProgramElement);
			if (((CpsProgramElement) cv).getProgramElement() == tpe)
			{
				found = true;
				assertTrue(cv instanceof CpsTypeProgramElement);
			}
		}
		assertTrue(found);
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected static class DummySel extends AbstractQualifiedRepositoryEntity implements Selector
	{
		private static final long serialVersionUID = 1L;

		public Collection<ProgramElement> values;

		/**
		 * 
		 */
		public DummySel()
		{
			super("sel");
			values = new ArrayList<ProgramElement>();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SISpec.Selector#getSelection
		 * ()
		 */
		public Collection<ProgramElement> getSelection()
		{
			return values;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected static class DummyPE extends ProgramElement
	{
		private static final long serialVersionUID = 1L;

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

	/**
	 * @author Michiel Hendriks
	 */
	protected static class DummyTPE extends Type
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.LAMA.Type#addChildType(Composestar.Core.LAMA.
		 * ProgramElement)
		 */
		@Override
		public void addChildType(ProgramElement childType)
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.LAMA.Type#addFieldType(Composestar.Core.LAMA.
		 * ProgramElement)
		 */
		@Override
		public void addFieldType(ProgramElement fieldType)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.Type#addImplementedBy(Composestar.Core.LAMA
		 * .ProgramElement)
		 */
		@Override
		public void addImplementedBy(ProgramElement class1)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.Type#addMethodReturnType(Composestar.Core.LAMA
		 * .ProgramElement)
		 */
		@Override
		public void addMethodReturnType(ProgramElement returnType)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.Type#addParameterType(Composestar.Core.LAMA
		 * .ProgramElement)
		 */
		@Override
		public void addParameterType(ProgramElement paramType)
		{}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.LAMA.Type#namespace()
		 */
		@Override
		public String namespace()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.LAMA.Type#setParentNamespace(Composestar.Core.LAMA
		 * .ProgramElement)
		 */
		@Override
		public void setParentNamespace(ProgramElement parentNS)
		{}

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

	}
}
