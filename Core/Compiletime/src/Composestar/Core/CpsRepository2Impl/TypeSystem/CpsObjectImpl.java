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
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.LAMA.Type;

/**
 * @author Michiel Hendriks
 */
public class CpsObjectImpl extends CpsTypeProgramElementImpl implements CpsObject
{
	private static final long serialVersionUID = -5151470511518075199L;

	/**
	 * If true this object represents an inner object
	 */
	protected boolean innerObject;

	/**
	 * @param type
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(Type type) throws NullPointerException
	{
		this(type, false);
	}

	/**
	 * @param type
	 * @param isInner TODO
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(Type type, boolean isInner) throws NullPointerException
	{
		super(type);
		innerObject = isInner;
	}

	/**
	 * @param ref
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(TypeReference ref) throws NullPointerException
	{
		this(ref, false);
	}

	/**
	 * @param ref
	 * @param isInner TODO
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(TypeReference ref, boolean isInner) throws NullPointerException
	{
		super(ref);
		innerObject = isInner;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isInnerObject()
	 */
	public boolean isInnerObject()
	{
		return innerObject;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.TypeSystem.CpsTypeProgramElementImpl
	 * #compatible(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	@Override
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		if (other instanceof CpsObject)
		{
			// objects must match instance
			return other == this;
		}
		return super.compatible(other);
	}

	@Override
	public String toString()
	{
		if (innerObject)
		{
			return "[inner] " + super.toString();
		}
		return super.toString();
	}
}
