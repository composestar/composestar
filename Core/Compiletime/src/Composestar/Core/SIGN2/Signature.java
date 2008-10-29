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

package Composestar.Core.SIGN2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.LAMA.MethodInfo;

/**
 * A record of all methods for a given concern (includes information about added
 * and removed methods).
 * 
 * @author Michiel Hendriks
 */
public class Signature
{
	/**
	 * All method registered, the key is a generated key through MethodInfo
	 */
	protected Map<String, MethodInfoWrapper> methods;

	/**
	 * The methods by name
	 */
	protected Map<String, List<MethodInfoWrapper>> methodsByName;

	/**
	 * Create a new signature for a concern
	 */
	public Signature()
	{
		super();
		methods = new HashMap<String, MethodInfoWrapper>();
		methodsByName = new HashMap<String, List<MethodInfoWrapper>>();
	}

	/**
	 * Add a method info wrapper to the list
	 * 
	 * @param miw The wrapper to add
	 * @return True when the wrapper was added to the list
	 * @throws NullPointerException Thrown when the wrapper is null or when it
	 *             doesn't have a MethodInfo object assigned.
	 */
	public boolean addMethodInfoWrapper(MethodInfoWrapper miw) throws NullPointerException
	{
		if (miw == null)
		{
			throw new NullPointerException("MethodInfoWrapper can not be null");
		}
		if (miw.getMethodInfo() == null)
		{
			throw new NullPointerException("MethodInfoWrapper has no MethodInfo assigned");
		}
		String key = miw.getMethodInfo().getHashKey();
		if (!methods.containsKey(key))
		{
			methods.put(key, miw);
			String name = miw.getMethodInfo().getName();
			List<MethodInfoWrapper> wrappersByName = methodsByName.get(name);
			if (wrappersByName == null)
			{
				wrappersByName = new ArrayList<MethodInfoWrapper>();
				methodsByName.put(name, wrappersByName);
			}
			wrappersByName.add(miw);
			return true;
		}
		return false;
	}

	/**
	 * @param relation
	 * @return
	 * @see #getMethods(EnumSet)
	 */
	public Collection<MethodInfo> getMethods(MethodRelation relation)
	{
		return getMethods(EnumSet.of(relation));
	}

	/**
	 * Get all methodinfo objects with a certain relation
	 * 
	 * @param relations
	 * @return
	 */
	public Collection<MethodInfo> getMethods(EnumSet<MethodRelation> relations)
	{
		List<MethodInfo> result = new ArrayList<MethodInfo>();
		for (MethodInfoWrapper miw : methods.values())
		{
			if (relations.contains(miw.getRelation()))
			{
				result.add(miw.getMethodInfo());
			}
		}
		return result;
	}

	/**
	 * @return All method info objects
	 */
	public Collection<MethodInfo> getMethods()
	{
		List<MethodInfo> result = new ArrayList<MethodInfo>();
		for (MethodInfoWrapper miw : methods.values())
		{
			result.add(miw.getMethodInfo());
		}
		return result;
	}

	/**
	 * @param relation
	 * @return
	 * @see Signature#getMethodInfoWrappers(EnumSet)
	 */
	public Collection<MethodInfoWrapper> getMethodInfoWrappers(MethodRelation relation)
	{
		return getMethodInfoWrappers(EnumSet.of(relation));
	}

	/**
	 * Get all methodinfowrapper objects with a certain relation
	 * 
	 * @param relations
	 * @return
	 */
	public Collection<MethodInfoWrapper> getMethodInfoWrappers(EnumSet<MethodRelation> relations)
	{
		List<MethodInfoWrapper> result = new ArrayList<MethodInfoWrapper>();
		for (MethodInfoWrapper miw : methods.values())
		{
			if (relations.contains(miw.getRelation()))
			{
				result.add(miw);
			}
		}
		return result;
	}

	/**
	 * @return All method info wrappers
	 */
	public Collection<MethodInfoWrapper> getMethodInfoWrappers()
	{
		return Collections.unmodifiableCollection(methods.values());
	}

	/**
	 * Get a method info wrapper for a given method info object (using its
	 * generated key)
	 * 
	 * @param mi
	 * @return
	 * @throws NullPointerException
	 */
	public MethodInfoWrapper getMethodInfoWrapper(MethodInfo mi) throws NullPointerException
	{
		if (mi == null)
		{
			throw new NullPointerException("MethodInfo can not be null");
		}
		String key = mi.getHashKey();
		return methods.get(key);
	}

	/**
	 * Remove a methodinfo wrapper
	 * 
	 * @param miw
	 */
	public void removeMethodInfoWrapper(MethodInfoWrapper miw)
	{
		if (miw == null)
		{
			return;
		}
		MethodInfo mi = miw.getMethodInfo();
		String key = mi.getHashKey();
		if (methods.containsKey(key))
		{
			methods.remove(key);

			String name = mi.getName();
			List<MethodInfoWrapper> wrappers = methodsByName.get(name);
			if (wrappers != null)
			{
				wrappers.remove(miw);
				if (wrappers.isEmpty())
				{
					methodsByName.remove(name);
				}
			}
		}
	}

	/**
	 * Return true if there is a method with the given key
	 * 
	 * @param dnmi
	 * @return True when a method info exists with the same key
	 * @throws NullPointerException Thrown when the methodinfo is null
	 */
	public boolean hasMethod(MethodInfo dnmi) throws NullPointerException
	{
		if (dnmi == null)
		{
			throw new NullPointerException("MethodInfo can not be null");
		}
		String key = dnmi.getHashKey();
		return methods.containsKey(key);
	}

	/**
	 * Returns true if there is a method with the given name.
	 * 
	 * @param methodName
	 * @return
	 */
	public boolean hasMethod(String methodName)
	{
		return methodsByName.containsKey(methodName);
	}
}
