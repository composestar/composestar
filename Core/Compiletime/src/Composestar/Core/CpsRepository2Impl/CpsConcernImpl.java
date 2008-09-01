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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.CpsConcern;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;

/**
 * An implementation of a CpsConcern
 * 
 * @author Michiel Hendriks
 */
public class CpsConcernImpl extends AbstractConcern implements CpsConcern
{
	private static final long serialVersionUID = -4657823762215658358L;

	/**
	 * A map containing all registered filter modules
	 */
	protected Map<String, FilterModule> filterModules;

	/**
	 * The optional superimposition construction
	 */
	protected SuperImposition superImposition;

	/**
	 * Create a new namespace instance
	 * 
	 * @param name
	 * @param namespace
	 * @throws IllegalArgumentException Throw when the entity name is empty
	 * @throws NullPointerException Thrown when the name is null
	 */
	public CpsConcernImpl(String name, List<String> namespace) throws IllegalArgumentException, NullPointerException
	{
		super(name, namespace);
		filterModules = new HashMap<String, FilterModule>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.CpsConcern#addFilterModule(Composestar
	 * .Core.CpsRepository2.FilterModules.FilterModule)
	 */
	public boolean addFilterModule(FilterModule newFm) throws NullPointerException
	{
		if (newFm == null)
		{
			throw new NullPointerException();
		}
		if (filterModules.containsKey(newFm.getName()))
		{
			return false;
		}
		filterModules.put(newFm.getName(), newFm);
		newFm.setOwner(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.CpsConcern#getFilterModule(java.lang.
	 * String)
	 */
	public FilterModule getFilterModule(String name)
	{
		if (name == null)
		{
			return null;
		}
		return filterModules.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.CpsConcern#getFilterModules()
	 */
	public Collection<FilterModule> getFilterModules()
	{
		return Collections.unmodifiableCollection(filterModules.values());
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.CpsConcern#getSuperImposition()
	 */
	public SuperImposition getSuperImposition()
	{
		return superImposition;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.CpsConcern#removeFilterModule(Composestar
	 * .Core.CpsRepository2.FilterModules.FilterModule)
	 */
	public FilterModule removeFilterModule(FilterModule fm) throws NullPointerException
	{
		if (fm == null)
		{
			throw new NullPointerException();
		}
		if (filterModules.containsValue(fm))
		{
			if (filterModules.values().remove(fm))
			{
				return fm;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.CpsConcern#removeFilterModule(java.lang
	 * .String)
	 */
	public FilterModule removeFilterModule(String fmName)
	{
		if (filterModules.containsKey(fmName))
		{
			FilterModule res = filterModules.get(fmName);
			filterModules.remove(fmName);
			return res;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.CpsConcern#setSuperImposition(Composestar
	 * .Core.CpsRepository2.SuperImposition.SuperImposition)
	 */
	public void setSuperImposition(SuperImposition si) throws IllegalStateException
	{
		if (superImposition != null && superImposition != si && si != null)
		{
			throw new IllegalStateException("SuperImposition has already been set. Unset it first by passing null.");
		}
		superImposition = si;
		if (superImposition != null)
		{
			superImposition.setOwner(this);
		}
	}
}
