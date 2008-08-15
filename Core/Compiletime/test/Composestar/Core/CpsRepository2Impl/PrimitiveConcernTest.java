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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michiel Hendriks
 */
public class PrimitiveConcernTest extends AbstractConcernTestBase
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		concern = new PrimitiveConcern(exampleFQN.split("\\."));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.PrimitiveConcern#PrimitiveConcern(java.lang.String[])}.
	 */
	public void testPrimitiveConcernStringArray()
	{
		concern = new PrimitiveConcern(exampleFQN.split("\\."));
		assertEquals(exampleFQN, concern.getFullyQualifiedName());
		try
		{
			new PrimitiveConcern();
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.PrimitiveConcern#PrimitiveConcern(java.util.List)}.
	 */
	public void testPrimitiveConcernListOfString()
	{
		List<String> s = Arrays.asList(exampleFQN.split("\\."));
		concern = new PrimitiveConcern(s);
		assertEquals(exampleFQN, concern.getFullyQualifiedName());
		try
		{
			new PrimitiveConcern(new ArrayList<String>());
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
		}
		try
		{
			s = null;
			new PrimitiveConcern(s);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.PrimitiveConcern#PrimitiveConcern(java.lang.String, java.util.List)}.
	 */
	public void testPrimitiveConcernStringListOfString()
	{
		List<String> s = Arrays.asList(exampleNS.split("\\."));
		concern = new PrimitiveConcern(exampleName, s);
		assertEquals(exampleFQN, concern.getFullyQualifiedName());
		try
		{
			new PrimitiveConcern(null, null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new PrimitiveConcern("", null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		concern = new PrimitiveConcern(exampleName, null);
		assertEquals(exampleName, concern.getFullyQualifiedName());
	}

}
