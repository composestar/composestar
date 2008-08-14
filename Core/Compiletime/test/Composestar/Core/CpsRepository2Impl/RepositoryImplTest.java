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

import Composestar.Core.CpsRepository2.RepositoryTestBase;

/**
 * @author Michiel Hendriks
 */
public class RepositoryImplTest extends RepositoryTestBase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		repos = new RepositoryImpl();
		item1 = new DummyRE();
		item2 = new DummyRE();
		qitem1 = new DummyQRE("qitem1");
		qitem1.setOwner(item1);
		qitem2 = new DummyQRE("qitem2");
		qitem2.setOwner(qitem1);
	}

	@Override
	protected void tearDown() throws Exception
	{
		repos = null;
		super.tearDown();
	}

	protected static class DummyQRE extends AbstractQualifiedRepositoryEntity
	{
		private static final long serialVersionUID = 859132862024136238L;

		public DummyQRE(String name)
		{
			super(name);
		}
	}

	protected static class DummyRE extends AbstractRepositoryEntity
	{
		private static final long serialVersionUID = -1088158613909041060L;

		public DummyRE()
		{
			super();
		}
	}
}
