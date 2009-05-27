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

package Composestar.Core.CpsRepository2Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;

/**
 * Abstract implementation for the Concern interface
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractConcern extends AbstractQualifiedRepositoryEntity implements Concern
{
	private static final long serialVersionUID = -2296971433819721568L;

	/**
	 * Contains the namespace with each segment as a separate entry
	 */
	protected List<String> namespace;

	/**
	 * Handle to the superimposed information.
	 */
	protected Superimposed superimposed;

	/**
	 * The associated type reference
	 */
	protected TypeReference typeReference;

	/**
	 * Create a new concern concern
	 * 
	 * @param name The name of the concern
	 * @param namesp The namespace (optional)
	 * @throws IllegalArgumentException Throw when the entity name is empty
	 * @throws NullPointerException Thrown when the name is null
	 */
	protected AbstractConcern(String name, List<String> namesp) throws NullPointerException, IllegalArgumentException
	{
		super(name);
		namespace = new ArrayList<String>();
		if (namesp != null)
		{
			namespace.addAll(namesp);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Concern#getNamespace()
	 */
	public String getNamespace()
	{
		StringBuilder ns = new StringBuilder();
		for (String elm : namespace)
		{
			if (ns.length() > 0)
			{
				ns.append('.');
			}
			ns.append(elm);
		}
		return ns.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity
	 * #getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName()
	{
		if (namespace.isEmpty())
		{
			return getName();
		}
		return getNamespace() + "." + getName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Concern#getNamespaceAsList()
	 */
	public List<String> getNamespaceAsList()
	{
		return Collections.unmodifiableList(namespace);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Concern#setTypeReference(Composestar.
	 * Core.CpsRepository2.References.TypeReference)
	 */
	public void setTypeReference(TypeReference ref)
	{
		typeReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Concern#getTypeReference()
	 */
	public TypeReference getTypeReference()
	{
		return typeReference;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Concern#getSuperimposed()
	 */
	public Superimposed getSuperimposed()
	{
		return superimposed;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Concern#setSuperimposed(Composestar.Core
	 * .CpsRepository2.SIInfo.Superimposed)
	 */
	public void setSuperimposed(Superimposed si) throws IllegalStateException
	{
		if (superimposed != null && superimposed != si && si != null)
		{
			throw new IllegalStateException("Superimposed has already been set. Unset it first by passing null.");
		}
		superimposed = si;
		if (superimposed != null)
		{
			superimposed.setOwner(this);
		}
	}
}
