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
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests for {@link Composestar.Core.CpsRepository2Impl.AbstractConcern}
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractConcernTestBase extends TestCase
{
	protected static final String exampleNS = "composestar.example.namespace";

	protected static final String exampleName = "class";

	protected static final String exampleFQN = exampleNS + "." + exampleName;

	protected AbstractConcern concern;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.AbstractConcern#getFullyQualifiedName()}.
	 */
	public void testGetFullyQualifiedName()
	{
		assertEquals(exampleFQN, concern.getFullyQualifiedName());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.AbstractConcern#getNamespace()}.
	 */
	public void testGetNamespace()
	{
		assertEquals(exampleNS, concern.getNamespace());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.AbstractConcern#getNamespaceAsList()}.
	 */
	public void testGetNamespaceAsList()
	{
		List<String> s = Arrays.asList(exampleNS.split("\\."));
		assertEquals(s, concern.getNamespaceAsList());
	}

}
