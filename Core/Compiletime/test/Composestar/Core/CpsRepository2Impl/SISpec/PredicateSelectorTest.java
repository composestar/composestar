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

/**
 * @author Michiel Hendriks
 */
public class PredicateSelectorTest extends AbstractSelectorTestBase
{
	protected static final String SEL_NAME = "selector1";

	protected static final String SEL_TERM = "C";

	protected static final String SEL_EXPR = "isClass(C)";

	protected PredicateSelector psel;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		psel = new PredicateSelector(SEL_NAME, SEL_TERM, SEL_EXPR);
		asel = psel;
		sel = psel;
		qre = sel;
		re = sel;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.PredicateSelector#PredicateSelector(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	public void testPredicateSelector()
	{
		try
		{
			new PredicateSelector(null, "x", "x");
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new PredicateSelector("x", null, "x");
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new PredicateSelector("x", "x", null);
			fail();
		}
		catch (NullPointerException e)
		{
		}

		try
		{
			new PredicateSelector("", "x", "x");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			new PredicateSelector("x", "", "x");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			new PredicateSelector("x", "x", "");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.PredicateSelector#getResultTerm()}
	 * .
	 */
	public void testGetResultTerm()
	{
		assertEquals(SEL_TERM, psel.getResultTerm());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.SISpec.PredicateSelector#getExpression()}
	 * .
	 */
	public void testGetExpression()
	{
		assertEquals(SEL_EXPR, psel.getExpression());
	}

}
