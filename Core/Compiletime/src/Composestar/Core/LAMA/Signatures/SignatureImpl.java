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

package Composestar.Core.LAMA.Signatures;

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
public class SignatureImpl implements Signature
{
	private static final long serialVersionUID = 4483989831726824465L;

	/**
	 * All method registered, the key is a generated key through MethodInfo
	 */
	protected Map<String, MethodInfoWrapperImpl> methods;

	/**
	 * The methods by name
	 */
	protected Map<String, List<MethodInfoWrapperImpl>> methodsByName;

	/**
	 * Create a new signature for a concern
	 */
	public SignatureImpl()
	{
		super();
		methods = new HashMap<String, MethodInfoWrapperImpl>();
		methodsByName = new HashMap<String, List<MethodInfoWrapperImpl>>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.Signature#addMethodInfoWrapper
	 * (Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper)
	 */
	public boolean addMethodInfoWrapper(MethodInfoWrapperImpl miw) throws NullPointerException
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
			List<MethodInfoWrapperImpl> wrappersByName = methodsByName.get(name);
			if (wrappersByName == null)
			{
				wrappersByName = new ArrayList<MethodInfoWrapperImpl>();
				methodsByName.put(name, wrappersByName);
			}
			wrappersByName.add(miw);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#getMethods(
	 * Composestar.Core.CpsRepository2.Signatures.MethodRelation)
	 */
	public Collection<MethodInfo> getMethods(MethodRelation relation)
	{
		return getMethods(EnumSet.of(relation));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.Signature#getMethods(java
	 * .util.EnumSet)
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

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.Signature#getMethods()
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

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#
	 * getMethodInfoWrappers
	 * (Composestar.Core.CpsRepository2.Signatures.MethodRelation)
	 */
	public Collection<MethodInfoWrapperImpl> getMethodInfoWrappers(MethodRelation relation)
	{
		return getMethodInfoWrappers(EnumSet.of(relation));
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#
	 * getMethodInfoWrappers(java.util.EnumSet)
	 */
	public Collection<MethodInfoWrapperImpl> getMethodInfoWrappers(EnumSet<MethodRelation> relations)
	{
		List<MethodInfoWrapperImpl> result = new ArrayList<MethodInfoWrapperImpl>();
		for (MethodInfoWrapperImpl miw : methods.values())
		{
			if (relations.contains(miw.getRelation()))
			{
				result.add(miw);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#
	 * getMethodInfoWrappers()
	 */
	public Collection<MethodInfoWrapperImpl> getMethodInfoWrappers()
	{
		return Collections.unmodifiableCollection(methods.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.Signature#getMethodInfoWrapper
	 * (Composestar.Core.LAMA.MethodInfo)
	 */
	public MethodInfoWrapperImpl getMethodInfoWrapper(MethodInfo mi) throws NullPointerException
	{
		if (mi == null)
		{
			throw new NullPointerException("MethodInfo can not be null");
		}
		String key = mi.getHashKey();
		return methods.get(key);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#
	 * removeMethodInfoWrapper
	 * (Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper)
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
			List<MethodInfoWrapperImpl> wrappers = methodsByName.get(name);
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

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.Signature#hasMethod(
	 * Composestar.Core.LAMA.MethodInfo)
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

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.Signature#hasMethod(java
	 * .lang.String)
	 */
	public boolean hasMethod(String methodName)
	{
		return methodsByName.containsKey(methodName);
	}
}
