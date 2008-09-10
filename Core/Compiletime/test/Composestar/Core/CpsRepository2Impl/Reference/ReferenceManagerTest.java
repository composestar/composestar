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

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.LAMA.ProgramElement;

/**
 * @author Michiel Hendriks
 */
public class ReferenceManagerTest extends TestCase
{
	protected ReferenceManager refman;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		refman = new ReferenceManager();
	}

	public void testGetTypeReference()
	{
		assertNotNull(refman.getTypeReferences().size());
		assertEquals(0, refman.getTypeReferences().size());
		TypeReference ref = refman.getTypeReference("1.2.3.4");
		assertNotNull(ref);
		assertEquals("1.2.3.4", ref.getReferenceId());
		TypeReference ref2 = refman.getTypeReference("1.2.3.4");
		assertSame(ref, ref2);
		assertEquals(1, refman.getTypeReferences().size());
		refman.getTypeReference("1.2.3.5");
		assertEquals(2, refman.getTypeReferences().size());
		try
		{
			refman.getTypeReference("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			refman.getTypeReference(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	public void testGetMethodReferenceStringStringJoinPointContextArgument()
	{
		TypeReference tref = refman.getTypeReference("1.2.3.4");
		MethodReference ref = refman.getMethodReference("5", tref.getReferenceId(), JoinPointContextArgument.FULL);
		assertNotNull(ref);
		assertEquals("5", ref.getReferenceId());
		assertSame(tref, ref.getTypeReference());
		assertEquals(JoinPointContextArgument.FULL, ref.getJoinPointContextArgument());
		MethodReference ref2 = refman.getMethodReference("5", tref.getReferenceId(), JoinPointContextArgument.FULL);
		assertSame(ref, ref2);
		assertEquals(1, refman.getMethodReferences().size());
		ref2 = refman.getMethodReference("5", tref.getReferenceId(), JoinPointContextArgument.UNUSED);
		assertNotSame(ref, ref2);
		assertEquals(2, refman.getMethodReferences().size());
		ref2 = refman.getMethodReference("4", "1.2.3", JoinPointContextArgument.FULL);
		assertNotSame(ref, ref2);
		assertEquals(3, refman.getMethodReferences().size());
		try
		{
			String s = null;
			refman.getMethodReference("a", s, JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getMethodReference(null, "1.2.3.4", JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getMethodReference("", "1.2.3.4", JoinPointContextArgument.FULL);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			refman.getMethodReference("5", "", JoinPointContextArgument.FULL);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	public void testGetMethodReferenceStringTypeReferenceJoinPointContextArgument()
	{
		TypeReference tref = refman.getTypeReference("1.2.3.4");
		MethodReference ref = refman.getMethodReference("5", tref, JoinPointContextArgument.FULL);
		assertNotNull(ref);
		assertEquals("5", ref.getReferenceId());
		assertSame(tref, ref.getTypeReference());
		assertEquals(JoinPointContextArgument.FULL, ref.getJoinPointContextArgument());
		MethodReference ref2 = refman.getMethodReference("5", tref.getReferenceId(), JoinPointContextArgument.FULL);
		assertSame(ref, ref2);
		try
		{
			tref = null;
			refman.getMethodReference("a", tref, JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getMethodReference(null, refman.getTypeReference("1.2.3.4"), JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getMethodReference("", refman.getTypeReference("1.2.3.4"), JoinPointContextArgument.FULL);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	public void testGetInstanceMethodReferenceStringInstanceContextProviderJoinPointContextArgument()
	{
		DummyCTX ctx = new DummyCTX(refman.getTypeReference("1.2.3.4"));
		InstanceMethodReference ref = refman.getInstanceMethodReference("5", ctx, JoinPointContextArgument.FULL);
		assertNotNull(ref);
		assertEquals("5", ref.getReferenceId());
		assertSame(ctx, ref.getCpsObject());
		assertEquals(JoinPointContextArgument.FULL, ref.getJoinPointContextArgument());
		assertSame(ctx.getTypeReference(), ref.getTypeReference());
		InstanceMethodReference ref2 = refman.getInstanceMethodReference("5", ctx, JoinPointContextArgument.FULL);
		assertSame(ref, ref2);
		assertEquals(1, refman.getInstanceMethodReferences().size());
		ref2 = refman.getInstanceMethodReference("6", ctx, JoinPointContextArgument.FULL);
		assertNotSame(ref, ref2);
		assertEquals(2, refman.getInstanceMethodReferences().size());
		try
		{
			refman.getInstanceMethodReference("a", null, JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getInstanceMethodReference(null, ctx, JoinPointContextArgument.FULL);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			refman.getInstanceMethodReference("", ctx, JoinPointContextArgument.FULL);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	public void testGetFilterModuleReference()
	{
		assertNotNull(refman.getFilterModuleReferences().size());
		assertEquals(0, refman.getFilterModuleReferences().size());
		FilterModuleReference ref = refman.getFilterModuleReference("1.2.3.4");
		assertNotNull(ref);
		assertEquals("1.2.3.4", ref.getReferenceId());
		FilterModuleReference ref2 = refman.getFilterModuleReference("1.2.3.4");
		assertSame(ref, ref2);
		assertEquals(1, refman.getFilterModuleReferences().size());
		refman.getFilterModuleReference("1.2.3.5");
		assertEquals(2, refman.getFilterModuleReferences().size());
		try
		{
			refman.getFilterModuleReference("");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			refman.getFilterModuleReference(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		TypeReference tref = refman.getTypeReference("1.2.3.4");
		assertNotSame(ref, tref);
	}

	/**
	 * @author Michiel Hendriks
	 */
	class DummyCTX implements CpsObject
	{
		private static final long serialVersionUID = -8877241455449624479L;

		TypeReference ref;

		public DummyCTX(TypeReference tref)
		{
			ref = tref;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.InstanceContextProvider#getTypeReference
		 * ()
		 */
		public TypeReference getTypeReference()
		{
			return ref;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.QualifiedRepositoryEntity#
		 * getFullyQualifiedName()
		 */
		public String getFullyQualifiedName()
		{
			return getName();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getName()
		 */
		public String getName()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
		 */
		public RepositoryEntity getOwner()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation
		 * ()
		 */
		public SourceInformation getSourceInformation()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
		 * .Core.CpsRepository2.RepositoryEntity)
		 */
		public RepositoryEntity setOwner(RepositoryEntity newOwner)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
		 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
		 */
		public void setSourceInformation(SourceInformation srcInfo)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.TypeSystem.CpsObject#getInstance()
		 */
		public Object getInstance()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
		 * getProgramElement()
		 */
		public ProgramElement getProgramElement()
		{
			return ref.getReference();
		}
	}
}
