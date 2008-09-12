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

package Composestar.Core.CpsRepository2.FMParams;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;

/**
 * @author Michiel Hendriks
 */
public abstract class FMParameterTestBase extends QualifiedRepositoryEntityTestBase
{
	public static final String FMP_NAME = "foo";

	protected FMParameter sfmp, fmpl;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FMParams.FMParameter#getRawName()}
	 * .
	 */
	public void testGetRawName()
	{
		assertEquals(FMP_NAME, sfmp.getRawName());
		assertEquals(FMP_NAME, fmpl.getRawName());
		assertEquals("?" + FMP_NAME, sfmp.getName());
		assertEquals("??" + FMP_NAME, fmpl.getName());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FMParams.FMParameter#isParameterList()}
	 * .
	 */
	public void testIsParameterList()
	{
		assertTrue(fmpl.isParameterList());
		assertFalse(sfmp.isParameterList());
	}

}
