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

package Composestar.Core.CpsRepository2Impl.SISpec;

import Composestar.Core.CpsRepository2Impl.SISpec.LegacySelector;

/**
 * @author Michiel Hendriks
 */
public class LegacySelectorTest extends AbstractSelectorTestBase
{
	protected static final String SEL_NAME = "selector1";

	protected static final String SEL_CLASS = "this.is.my.class";

	protected LegacySelector lsel;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		lsel = new LegacySelector(SEL_NAME, SEL_CLASS, true);
		asel = lsel;
		sel = lsel;
		qre = sel;
		re = sel;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.LegacySelector#getClassSelection()}
	 * .
	 */
	public void testGetClassSelection()
	{
		assertEquals(SEL_CLASS, lsel.getClassSelection());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.LegacySelector#isIncludeChildren()}
	 * .
	 */
	public void testIsIncludeChildren()
	{
		assertTrue(lsel.isIncludeChildren());
	}

	/**
	 * 
	 */
	public void testCtors()
	{
		try
		{
			new LegacySelector(null, "1", true);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new LegacySelector("", "1", true);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			new LegacySelector("1", null, true);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new LegacySelector("1", "", true);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}
}
