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

package Composestar.Core.CpsRepository2.References;

import Composestar.Core.LAMA.Type;

/**
 * A very special type reference, this points to the inner type. It is only used
 * in case of an instance method reference that points to an inner method.
 * Because the inner type is only known after superimposition this needs to be
 * handled with extra care. Note that this is different from the CpsObject with
 * isInner set to true. This is only used for method resolving.
 * 
 * @author Michiel Hendriks
 */
public final class InnerTypeReference implements TypeReference
{
	private static final long serialVersionUID = 5474616263133226983L;

	public InnerTypeReference()
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#dereference()
	 */
	public void dereference() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#getReference()
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
		return "inner";
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#isResolved()
	 */
	public boolean isResolved()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference()
	 */
	public boolean isSelfReference()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#setReference(java
	 * .lang.Object)
	 */
	public void setReference(Type element) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}
