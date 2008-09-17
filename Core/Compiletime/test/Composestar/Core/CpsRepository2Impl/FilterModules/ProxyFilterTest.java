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

package Composestar.Core.CpsRepository2Impl.FilterModules;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterTestBase;

/**
 * @author Michiel Hendriks
 */
public class ProxyFilterTest extends FilterTestBase
{
	protected Filter baseFilter;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		baseFilter = new FilterImpl("foo");
		filter = new ProxyFilter(baseFilter);
		qre = filter;
		re = filter;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FilterModules.ProxyFilter#ProxyFilter(Composestar.Core.CpsRepository2.FilterModules.Filter)}
	 * .
	 */
	public void testProxyFilter()
	{
		try
		{
			new ProxyFilter(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#setElementExpression(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)}
	 * .
	 */
	@Override
	public void testSetElementExpression()
	{
		FilterElementExpression fee = new DummyFE();

		assertNull(filter.getElementExpression());
		filter.setElementExpression(fee);
		assertSame(baseFilter, fee.getOwner());
		assertSame(fee, filter.getElementExpression());

		try
		{
			filter.setElementExpression(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#addArgument(Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)}
	 * .
	 */
	@Override
	public void testAddArgument()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));
		assertNull(filter.addArgument(arg1));
		assertSame(baseFilter, arg1.getOwner());
		assertNull(filter.addArgument(arg2));
		assertSame(arg1, filter.addArgument(arg1));

		try
		{
			filter.addArgument(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			filter.addArgument(new DummyCA(null));
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			filter.addArgument(new DummyCA(new DummyCV("yyy", PropertyPrefix.MESSAGE)));
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

}
