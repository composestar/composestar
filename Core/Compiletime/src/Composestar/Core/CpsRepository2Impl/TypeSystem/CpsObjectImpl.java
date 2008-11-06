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

	public enum CpsObjectType
	{
		NORMAL, INNER, SELF
	}

	/**
	 * The type of object this is
	 */
	protected CpsObjectType objectType = CpsObjectType.NORMAL;

	/**
	 * @param type
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(Type type) throws NullPointerException
	{
		this(type, CpsObjectType.NORMAL);
	}

	/**
	 * @param type
	 * @param objType The type of object this is
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(Type type, CpsObjectType objType) throws NullPointerException
	{
		super(type);
		objectType = objType;
	}

	/**
	 * @param ref
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(TypeReference ref) throws NullPointerException
	{
		this(ref, CpsObjectType.NORMAL);
	}

	/**
	 * @param ref
	 * @param objType The type of object to create
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(TypeReference ref, CpsObjectType objType) throws NullPointerException
	{
		super(ref);
		objectType = objType;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isInnerObject()
	 */
	public boolean isInnerObject()
	{
		return objectType == CpsObjectType.INNER;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isSelfObject()
	 */
	public boolean isSelfObject()
	{
		return objectType == CpsObjectType.SELF;
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

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.TypeSystem.CpsTypeProgramElementImpl
	 * #toString()
	 */
	@Override
	public String toString()
	{
		if (objectType == CpsObjectType.INNER)
		{
			return "[inner] " + super.toString();
		}
		if (objectType == CpsObjectType.SELF)
		{
			return "[self] " + super.toString();
		}
		return super.toString();
	}

}
