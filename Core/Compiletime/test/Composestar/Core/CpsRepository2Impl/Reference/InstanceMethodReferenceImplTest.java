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

package Composestar.Core.CpsRepository2Impl.Reference;

import Composestar.Core.CpsRepository2.References.InstanceMethodReferenceTestBase;

/**
 * @author Michiel Hendriks
 */
public class InstanceMethodReferenceImplTest extends InstanceMethodReferenceTestBase
{

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.MethodReferenceTestBase#setUp
	 * ()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		imref = new InstanceMethodReferenceImpl("foo", context, JPCA);
		mref = imref;
		ref = mref;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.Reference.InstanceMethodReferenceImpl#InstanceMethodReferenceImpl(java.lang.String, Composestar.Core.CpsRepository2.TypeSystem.CpsObject, Composestar.Core.CpsRepository2.JoinPointContextArgument)}
	 * .
	 */
	public void testInstanceMethodReferenceImpl()
	{
		try
		{
			new InstanceMethodReferenceImpl(null, context, JPCA);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new InstanceMethodReferenceImpl("foo", null, JPCA);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			new InstanceMethodReferenceImpl("foo", context, null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

}
