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

import java.io.Serializable;

import Composestar.Core.CpsRepository2.References.Reference;

/**
 * A basic implementation of the Reference interface. Only accepts serializable
 * reference types.
 * 
 * @author Michiel Hendriks
 */
public class ReferenceImpl<T extends Serializable> implements Reference<T>
{
	private static final long serialVersionUID = -6057399805259629224L;

	/**
	 * Holds the reference ID
	 */
	protected String referenceId;

	/**
	 * Is set to true when the reference was resolved. The reference could still
	 * be null.
	 */
	protected boolean resolved;

	/**
	 * The reference.
	 */
	protected T reference;

	public ReferenceImpl()
	{}

	/**
	 * Create a reference with a given id;
	 * 
	 * @param refid
	 */
	public ReferenceImpl(String refid)
	{
		this();
		referenceId = refid;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#getReference()
	 */
	public T getReference()
	{
		return reference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
	 */
	public String getReferenceId()
	{
		return referenceId;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#isResolved()
	 */
	public boolean isResolved()
	{
		return resolved;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#setReference(java
	 * .lang.Object)
	 */
	public void setReference(T element) throws UnsupportedOperationException
	{
		reference = element;
		resolved = true;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.Reference#dereference()
	 */
	public void dereference() throws UnsupportedOperationException
	{
		reference = null;
		resolved = false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference()
	 */
	public boolean isSelfReference()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (referenceId == null ? 0 : referenceId.hashCode());
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
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ReferenceImpl<?> other = (ReferenceImpl<?>) obj;
		if (referenceId == null)
		{
			if (other.referenceId != null)
			{
				return false;
			}
		}
		else if (!referenceId.equals(other.referenceId))
		{
			return false;
		}
		return true;
	}
}
