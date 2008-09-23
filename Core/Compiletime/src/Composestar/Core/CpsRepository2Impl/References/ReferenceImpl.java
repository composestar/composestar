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

package Composestar.Core.CpsRepository2Impl.References;

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

	/**
	 * Create a reference with a given id;
	 * 
	 * @param refid
	 * @throws NullPointerException Thrown when the reference Id is null
	 */
	public ReferenceImpl(String refid) throws NullPointerException
	{
		super();
		if (refid == null)
		{
			throw new NullPointerException("reference id can not be null");
		}
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
}
