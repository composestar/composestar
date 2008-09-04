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

package Composestar.Core.CpsRepository2.FilterModules;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.LAMA.Type;

/**
 * @author Michiel Hendriks
 */
public abstract class InternalTestBase extends QualifiedRepositoryEntityTestBase
{
	protected Internal internal;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Internal#setTypeReference(Composestar.Core.CpsRepository2.References.TypeReference)}
	 * .
	 */
	public void testSetTypeReference()
	{
		TypeReference ref = new DummTR();

		assertNull(internal.getTypeReference());
		internal.setTypeReference(ref);
		assertSame(ref, internal.getTypeReference());

		try
		{
			internal.setTypeReference(null);
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummTR implements TypeReference
	{
		private static final long serialVersionUID = 4529927326378786237L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public Type getReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(Type element) throws UnsupportedOperationException
		{}
	}

}
