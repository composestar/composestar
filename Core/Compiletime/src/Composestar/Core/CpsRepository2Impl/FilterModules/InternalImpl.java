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

package Composestar.Core.CpsRepository2Impl.FilterModules;

import Composestar.Core.CpsRepository2.FilterModules.Internal;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Basic internal implementation
 * 
 * @author Michiel Hendriks
 */
public class InternalImpl extends AbstractQualifiedRepositoryEntity implements Internal
{
	private static final long serialVersionUID = 1194447562107862809L;

	/**
	 * Reference to the type of this internal
	 */
	protected TypeReference typeReference;

	/**
	 * @param internalName The name of the internal
	 * @throws NullPointerException Thrown when the internal name is null
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public InternalImpl(String internalName) throws NullPointerException, IllegalArgumentException
	{
		super(internalName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Internal#setTypeReference
	 * (Composestar.Core.CpsRepository2.References.TypeReference)
	 */
	public void setTypeReference(TypeReference ref) throws NullPointerException
	{
		if (ref == null)
		{
			throw new NullPointerException();
		}
		typeReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.InstanceContextProvider#getTypeReference
	 * ()
	 */
	public TypeReference getTypeReference()
	{
		return typeReference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public Internal newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}
}
