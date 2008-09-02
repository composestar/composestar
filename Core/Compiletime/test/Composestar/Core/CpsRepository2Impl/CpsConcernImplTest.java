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

import Composestar.Core.CpsRepository2.CpsConcernTestBase;

/**
 * @author Michiel Hendriks
 */
public class CpsConcernImplTest extends CpsConcernTestBase
{
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		List<String> ns = Arrays.asList(exampleNS.split("\\."));
		CpsConcernImpl cpscrnimpl = new CpsConcernImpl(exampleName, ns);
		cpsconcern = cpscrnimpl;
		concern = cpscrnimpl;
		qre = concern;
		re = concern;
	}
}
