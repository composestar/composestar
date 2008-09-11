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

package Composestar.Core.CpsRepository2Impl.FilterElements;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonPropertyTestBase;

/**
 * @author Michiel Hendriks
 */
public class CanonPropertyImplTest extends CanonPropertyTestBase
{

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		cp1 = new CanonPropertyImpl(PROP1_PREF, PROP1_NAME);
		cp2 = new CanonPropertyImpl(PROP2_PREF, PROP2_NAME);
		re = cp1;
	}

	/**
	 * 
	 */
	public void testCtor()
	{
		try
		{
			new CanonPropertyImpl(PropertyPrefix.MESSAGE, null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new CanonPropertyImpl(PropertyPrefix.MESSAGE, "");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			new CanonPropertyImpl(PropertyPrefix.NONE, "foo");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			new CanonPropertyImpl(null, "foo");
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

}
