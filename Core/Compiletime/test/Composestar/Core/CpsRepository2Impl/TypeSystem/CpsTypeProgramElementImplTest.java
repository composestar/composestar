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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElementTestBase;
import Composestar.Core.LAMA.Type;

/**
 * @author Michiel Hendriks
 */
public class CpsTypeProgramElementImplTest extends CpsTypeProgramElementTestBase
{

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElementTestBase
	 * #setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ctpe = new CpsTypeProgramElementImpl(tr);
		cpe = ctpe;
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.TypeSystem.CpsTypeProgramElementImpl#CpsTypeProgramElementImpl(Composestar.Core.LAMA.Type)}
	 * .
	 */
	public void testCpsTypeProgramElementImplType()
	{
		CpsTypeProgramElement cp = new CpsTypeProgramElementImpl(pe);
		assertSame(pe, cp.getProgramElement());
		assertNotNull(cp.getTypeReference());
		assertSame(pe, cp.getTypeReference().getReference());
		assertTrue(cp.getTypeReference().isResolved());
		assertTrue(cp.getTypeReference().isSelfReference());
		try
		{
			cp.getTypeReference().dereference();
		}
		catch (UnsupportedOperationException e)
		{
		}
		try
		{
			cp.getTypeReference().setReference(pe);
		}
		catch (UnsupportedOperationException e)
		{
		}
		assertEquals(pe.getFullName(), cp.getTypeReference().getReferenceId());
		try
		{
			Type nt = null;
			new CpsTypeProgramElementImpl(nt);
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2Impl.TypeSystem.CpsTypeProgramElementImpl#CpsTypeProgramElementImpl(Composestar.Core.CpsRepository2.References.TypeReference)}
	 * .
	 */
	public void testCpsTypeProgramElementImplTypeReference()
	{
		try
		{
			TypeReference nt = null;
			new CpsTypeProgramElementImpl(nt);
		}
		catch (NullPointerException e)
		{
		}
	}

}
