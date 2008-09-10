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

import Composestar.Core.CpsRepository2.FilterModules.External;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * Implementation of the external instance
 * 
 * @author Michiel Hendriks
 */
public class ExternalImpl extends AbstractQualifiedRepositoryEntity implements External
{
	private static final long serialVersionUID = -36809726249004416L;

	/**
	 * Reference to the type of this external
	 */
	protected TypeReference typeReference;

	/**
	 * Reference to a method used to initialize this external. Could be null
	 */
	protected MethodReference methodReference;

	/**
	 * @param externalName Name of the external
	 * @throws NullPointerException Thrown when the name is null
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public ExternalImpl(String externalName) throws NullPointerException, IllegalArgumentException
	{
		super(externalName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.External#getMethodReference
	 * ()
	 */
	public MethodReference getMethodReference()
	{
		return methodReference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.External#setMethodReference
	 * (Composestar.Core.CpsRepository2.References.MethodReference)
	 */
	public void setMethodReference(MethodReference ref)
	{
		methodReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.External#setTypeReference
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
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
	 * getProgramElement()
	 */
	public ProgramElement getProgramElement()
	{
		if (typeReference != null)
		{
			return typeReference.getReference();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#getInstance()
	 */
	public Object getInstance()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public External newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
