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

package Composestar.Core.CpsRepository2.References;

import junit.framework.TestCase;

/**
 * @author Michiel Hendriks
 */
public abstract class ReferenceTestBase<T> extends TestCase
{
	protected Reference<T> ref;

	protected T element;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.References.Reference#getReferenceId()}
	 * .
	 */
	public void testGetReferenceId()
	{
		assertNotNull(ref.getReferenceId());
		assertFalse(ref.getReferenceId().length() == 0);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.References.Reference#setReference(java.lang.Object)}
	 * .
	 */
	public void testSetReference()
	{
		if (!ref.isSelfReference())
		{
			assertNull(ref.getReference());
			assertFalse(ref.isResolved());
			ref.setReference(element);
			assertSame(element, ref.getReference());
			assertTrue(ref.isResolved());
		}
		else
		{
			assertNotNull(ref.getReference());
			assertTrue(ref.isResolved());
			try
			{
				ref.setReference(element);
				fail();
			}
			catch (UnsupportedOperationException e)
			{
			}
		}
	}

	/**
	 * 
	 */
	public void testSetReferenceEx()
	{
		if (!ref.isSelfReference())
		{
			assertNull(ref.getReference());
			assertFalse(ref.isResolved());
			ref.setReference(null);
			assertNull(ref.getReference());
			assertTrue(ref.isResolved());
		}
	}

	/**
	 * 
	 */
	public void testDereference()
	{
		if (!ref.isSelfReference())
		{
			assertNull(ref.getReference());
			assertFalse(ref.isResolved());
			ref.setReference(element);
			ref.dereference();
			assertNull(ref.getReference());
			assertFalse(ref.isResolved());
		}
		else
		{
			try
			{
				ref.dereference();
				fail();
			}
			catch (UnsupportedOperationException e)
			{
			}
		}
	}
}
