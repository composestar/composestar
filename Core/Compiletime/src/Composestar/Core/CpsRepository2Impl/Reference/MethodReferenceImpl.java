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

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.LAMA.MethodInfo;

/**
 * A MethodReference implementation
 * 
 * @author Michiel Hendriks
 */
public class MethodReferenceImpl extends ReferenceImpl<MethodInfo> implements MethodReference
{
	private static final long serialVersionUID = -6439329664107878342L;

	/**
	 * The type that should contain the referenced method
	 */
	protected TypeReference typeReference;

	/**
	 * The desired join point context argument
	 */
	protected JoinPointContextArgument joinPointContextArgument;

	public MethodReferenceImpl(String refid, TypeReference typeRef, JoinPointContextArgument jpca)
	{
		super(refid);
		typeReference = typeRef;
		joinPointContextArgument = jpca;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.References.MethodReference#
	 * getJoinPointContextArgument()
	 */
	public JoinPointContextArgument getJoinPointContextArgument()
	{
		return joinPointContextArgument;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.MethodReference#getTypeReference
	 * ()
	 */
	public TypeReference getTypeReference()
	{
		return typeReference;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (joinPointContextArgument == null ? 0 : joinPointContextArgument.hashCode());
		result = prime * result + (typeReference == null ? 0 : typeReference.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		MethodReferenceImpl other = (MethodReferenceImpl) obj;
		if (joinPointContextArgument == null)
		{
			if (other.joinPointContextArgument != null)
			{
				return false;
			}
		}
		else if (!joinPointContextArgument.equals(other.joinPointContextArgument))
		{
			return false;
		}
		if (typeReference == null)
		{
			if (other.typeReference != null)
			{
				return false;
			}
		}
		else if (!typeReference.equals(other.typeReference))
		{
			return false;
		}
		return true;
	}
}
