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

package Composestar.Core.CpsRepository2Impl.SISpec.Constraints;

import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValueTestBase;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.FilterModuleConstraintValueImpl;

/**
 * @author Michiel Hendriks
 */
public class FilterModuleConstraintValueImplTest extends FilterModuleConstraintValueTestBase
{

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.
	 * FilterModuleConstraintValueTestBase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fmcv = new FilterModuleConstraintValueImpl(fmr);
		re = fmcv;
	}

	/**
	 * 
	 */
	public void testCtor()
	{
		try
		{
			new FilterModuleConstraintValueImpl(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}
}
