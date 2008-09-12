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

package Composestar.Core.CpsRepository2Impl.FMParams;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValueTestBase;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class BasicFMParamValueTest extends FMParameterValueTestBase
{
	protected BasicFMParamValue bfmpv;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		bfmpv = new BasicFMParamValue();
		fmpv = bfmpv;
		re = bfmpv;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.FMParams.BasicFMParamValue#addValue(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)}
	 * .
	 */
	public void testAddValue()
	{
		CpsVariable var = new DummyCPSV();
		bfmpv.addValue(var);
		assertTrue(bfmpv.getValues().contains(var));

		try
		{
			bfmpv.addValue(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected static class DummyCPSV extends AbstractRepositoryEntity implements CpsVariable
	{
		private static final long serialVersionUID = 1L;
	}

}
