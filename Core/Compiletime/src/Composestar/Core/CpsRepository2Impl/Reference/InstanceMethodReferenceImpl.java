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

import Composestar.Core.CpsRepository2.InstanceContextProvider;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;

/**
 * An implementation of the instance method reference
 * 
 * @author Michiel Hendriks
 */
public class InstanceMethodReferenceImpl extends MethodReferenceImpl implements InstanceMethodReference
{
	private static final long serialVersionUID = 6490955236934459253L;

	/**
	 * The context for this method reference
	 */
	protected InstanceContextProvider context;

	/**
	 * Create a new instance method reference
	 * 
	 * @param refid
	 * @param ctx
	 * @param jpca
	 */
	public InstanceMethodReferenceImpl(String refid, InstanceContextProvider ctx, JoinPointContextArgument jpca)
	{
		super(refid, ctx.getTypeReference(), jpca);
		context = ctx;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.InstanceMethodReference#getContext
	 * ()
	 */
	public InstanceContextProvider getContext()
	{
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.MethodReferenceImpl#
	 * getTypeReference()
	 */
	@Override
	public TypeReference getTypeReference()
	{
		return context.getTypeReference();
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
		result = prime * result + ((context == null) ? 0 : context.hashCode());
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
		InstanceMethodReferenceImpl other = (InstanceMethodReferenceImpl) obj;
		if (context == null)
		{
			if (other.context != null)
			{
				return false;
			}
		}
		else if (!context.equals(other.context))
		{
			return false;
		}
		return true;
	}
}
