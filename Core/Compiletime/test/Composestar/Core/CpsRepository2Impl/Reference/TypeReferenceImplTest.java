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

package Composestar.Core.CpsRepository2Impl.Reference;

import java.util.Collection;

import Composestar.Core.CpsRepository2.References.TypeReferenceTestBase;
import Composestar.Core.CpsRepository2Impl.References.TypeReferenceImpl;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author Michiel Hendriks
 */
public class TypeReferenceImplTest extends TypeReferenceTestBase
{

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		element = new DummyT();
		ref = new TypeReferenceImpl("foo");
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.References.TypeReferenceImpl#TypeReferenceImpl(java.lang.String)}
	 * .
	 */
	public void testTypeReferenceImpl()
	{
		try
		{
			new TypeReferenceImpl(null);
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyT extends Type
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
