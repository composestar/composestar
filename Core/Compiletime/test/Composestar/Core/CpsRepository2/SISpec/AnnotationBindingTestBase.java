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

package Composestar.Core.CpsRepository2.SISpec;

import java.util.Collection;

import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.SISpec.AnnotationBinding;
import Composestar.Core.CpsRepository2.SISpec.Selector;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;

/**
 * @author Michiel Hendriks
 */
public abstract class AnnotationBindingTestBase extends RepositoryEntityTestBase
{
	protected AnnotationBinding ab;

	protected TypeReference ref1;

	protected TypeReference ref2;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ref1 = new DummyATR("foo");
		ref2 = new DummyATR("bar");
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.AnnotationBinding#setSelector(Composestar.Core.CpsRepository2.SISpec.Selector)}
	 * .
	 */
	public void testSetSelector()
	{
		assertNull(ab.getSelector());
		Selector sel = new DummySel("foo");
		ab.setSelector(sel);
		assertSame(sel, ab.getSelector());
		try
		{
			ab.setSelector(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.AnnotationBinding#addAnnotation(Composestar.Core.CpsRepository2.References.TypeReference)}
	 * .
	 */
	public void testAddAnnotation()
	{
		assertNotNull(ab.getAnnotations());
		assertEquals(0, ab.getAnnotations().size());
		ab.addAnnotation(ref1);
		assertEquals(1, ab.getAnnotations().size());
		assertTrue(ab.getAnnotations().contains(ref1));
		assertFalse(ab.getAnnotations().contains(ref2));
		ab.addAnnotation(ref2);
		assertEquals(2, ab.getAnnotations().size());
		assertTrue(ab.getAnnotations().contains(ref1));
		assertTrue(ab.getAnnotations().contains(ref2));
		// test dup
		ab.addAnnotation(ref2);
		assertEquals(2, ab.getAnnotations().size());
		assertTrue(ab.getAnnotations().contains(ref1));
		assertTrue(ab.getAnnotations().contains(ref2));

		try
		{
			ab.addAnnotation(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.AnnotationBinding#removeAnnotation(Composestar.Core.CpsRepository2.References.TypeReference)}
	 * .
	 */
	public void testRemoveAnnotationTypeReference()
	{
		assertNull(ab.removeAnnotation(ref1));
		assertNull(ab.removeAnnotation(ref2));
		ab.addAnnotation(ref1);
		ab.addAnnotation(ref2);
		assertEquals(2, ab.getAnnotations().size());
		assertSame(ref1, ab.removeAnnotation(ref1));
		assertEquals(1, ab.getAnnotations().size());
		assertNull(ab.removeAnnotation(ref1));
		assertEquals(1, ab.getAnnotations().size());
		try
		{
			ref1 = null;
			ab.removeAnnotation(ref1);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.SISpec.AnnotationBinding#removeAnnotation(java.lang.String)}
	 * .
	 */
	public void testRemoveAnnotationString()
	{
		assertNull(ab.removeAnnotation(ref1.getReferenceId()));
		assertNull(ab.removeAnnotation(ref2.getReferenceId()));
		ab.addAnnotation(ref1);
		ab.addAnnotation(ref2);
		assertEquals(2, ab.getAnnotations().size());
		assertEquals(ref1, ab.removeAnnotation(ref1.getReferenceId()));
		assertEquals(1, ab.getAnnotations().size());
		assertNull(ab.removeAnnotation(ref1.getReferenceId()));
		assertEquals(1, ab.getAnnotations().size());

		String s = null;
		assertNull(ab.removeAnnotation(s));
	}

	/**
	 * @author Michiel Hendriks
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
		 * Composestar.Core.CpsRepository2.SISpec.Selector#getSelection
		 * ()
		 */
		public Collection<ProgramElement> getSelection()
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyATR implements TypeReference
	{
		private static final long serialVersionUID = -84203286629919428L;

		protected String refid;

		public DummyATR(String refid)
		{
			this.refid = refid;
		}

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
		public Type getReference()
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
			return refid;
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
		public void setReference(Type element) throws UnsupportedOperationException
		{

		}

	}
}
