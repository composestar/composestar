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

package Composestar.Core.CpsRepository2.FilterElements;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;

/**
 * @author Michiel Hendriks
 */
public abstract class CanonPropertyTestBase extends RepositoryEntityTestBase
{
	protected static final String PROP1_NAME = "testProperty";

	protected static final String PROP2_NAME = PropertyNames.INNER;

	protected static final PropertyPrefix PROP1_PREF = PropertyPrefix.MESSAGE;

	protected static final PropertyPrefix PROP2_PREF = PropertyPrefix.NONE;

	protected CanonProperty cp1, cp2;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getName()}
	 * .
	 */
	public void testGetName()
	{
		assertEquals(PROP1_PREF.toString() + "." + PROP1_NAME, cp1.getName());
		assertEquals(PROP2_NAME, cp2.getName());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getBaseName()}
	 * .
	 */
	public void testGetBaseName()
	{
		assertEquals(PROP1_NAME, cp1.getBaseName());
		assertEquals(PROP2_NAME, cp2.getBaseName());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.CanonProperty#getPrefix()}
	 * .
	 */
	public void testGetPrefix()
	{
		assertEquals(PROP1_PREF, cp1.getPrefix());
		assertEquals(PROP2_PREF, cp2.getPrefix());
	}

}
