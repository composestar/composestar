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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.Reference;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.ReferenceUsage;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;

/**
 * Manages soft-reference instances. Self references are not included in the
 * reference manager.
 * 
 * @author Michiel Hendriks
 */
public class ReferenceManagerImpl implements Serializable, ReferenceManager
{
	private static final long serialVersionUID = -400983142236822779L;

	/** Used as a separator in ID for references */
	static final char FIELD_SEP = 0x1E;

	/**
	 * Keeps a record of type references
	 */
	protected Map<String, TypeReference> typeRefs;

	/**
	 * Stores method references
	 */
	protected Map<String, MethodReference> methodRefs;

	/**
	 * Instance method references
	 */
	protected Map<String, InstanceMethodReference> instMethodRefs;

	/**
	 * Filter module references
	 */
	protected Map<String, FilterModuleReference> fmRefs;

	/**
	 * Usage information for references
	 */
	protected Map<Reference<?>, Set<ReferenceUsage>> usage;

	public ReferenceManagerImpl()
	{
		typeRefs = new HashMap<String, TypeReference>();
		methodRefs = new HashMap<String, MethodReference>();
		instMethodRefs = new HashMap<String, InstanceMethodReference>();
		fmRefs = new HashMap<String, FilterModuleReference>();
		usage = new HashMap<Reference<?>, Set<ReferenceUsage>>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getTypeReference(java.lang.String)
	 */
	public TypeReference getTypeReference(String refid) throws IllegalArgumentException, NullPointerException
	{
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.length() == 0)
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		if (!typeRefs.containsKey(refid))
		{
			TypeReference ref = new TypeReferenceImpl(refid);
			typeRefs.put(refid, ref);
			return ref;
		}
		return typeRefs.get(refid);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getTypeReferences()
	 */
	public Collection<TypeReference> getTypeReferences()
	{
		return Collections.unmodifiableCollection(typeRefs.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getMethodReference(java.lang.String, java.lang.String,
	 * Composestar.Core.CpsRepository2.JoinPointContextArgument)
	 */
	public MethodReference getMethodReference(String refid, String typeRef, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException
	{
		return getMethodReference(refid, getTypeReference(typeRef), jpca);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getMethodReference(java.lang.String,
	 * Composestar.Core.CpsRepository2.References.TypeReference,
	 * Composestar.Core.CpsRepository2.JoinPointContextArgument)
	 */
	public MethodReference getMethodReference(String refid, TypeReference typeRef, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException
	{
		if (typeRef == null)
		{
			throw new NullPointerException("TypeReference is null");
		}
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.length() == 0)
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(typeRef.getReferenceId());
		sb.append(FIELD_SEP);
		sb.append(refid);
		sb.append(FIELD_SEP);
		sb.append(jpca.name());
		String fullid = sb.toString();
		if (!methodRefs.containsKey(fullid))
		{
			MethodReference ref = new MethodReferenceImpl(refid, typeRef, jpca);
			methodRefs.put(fullid, ref);
			return ref;
		}
		return methodRefs.get(fullid);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getMethodReferences()
	 */
	public Collection<MethodReference> getMethodReferences()
	{
		return Collections.unmodifiableCollection(methodRefs.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getInstanceMethodReference(java.lang.String,
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsObject,
	 * Composestar.Core.CpsRepository2.JoinPointContextArgument)
	 */
	public InstanceMethodReference getInstanceMethodReference(String refid, CpsObject context,
			JoinPointContextArgument jpca) throws NullPointerException, IllegalArgumentException
	{
		if (context == null)
		{
			throw new NullPointerException("InstanceContextProvider is null");
		}
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.length() == 0)
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(context.getClass().getName());
		sb.append(FIELD_SEP);
		sb.append(System.identityHashCode(context));
		sb.append(FIELD_SEP);
		sb.append(refid);
		sb.append(FIELD_SEP);
		sb.append(jpca.name());
		String fullid = sb.toString();
		if (!instMethodRefs.containsKey(fullid))
		{
			InstanceMethodReference ref = new InstanceMethodReferenceImpl(refid, context, jpca);
			instMethodRefs.put(fullid, ref);
			return ref;
		}
		return instMethodRefs.get(fullid);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getInstanceMethodReferences()
	 */
	public Collection<InstanceMethodReference> getInstanceMethodReferences()
	{
		return Collections.unmodifiableCollection(instMethodRefs.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getFilterModuleReference(java.lang.String)
	 */
	public FilterModuleReference getFilterModuleReference(String refid) throws NullPointerException,
			IllegalArgumentException
	{
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.length() == 0)
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		if (!fmRefs.containsKey(refid))
		{
			FilterModuleReference ref = new FilterModuleReferenceImpl(refid);
			fmRefs.put(refid, ref);
			return ref;
		}
		return fmRefs.get(refid);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Reference.IReferenceManager#
	 * getFilterModuleReferences()
	 */
	public Collection<FilterModuleReference> getFilterModuleReferences()
	{
		return Collections.unmodifiableCollection(fmRefs.values());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.ReferenceManager#addReferenceUser
	 * (Composestar.Core.CpsRepository2.References.Reference,
	 * Composestar.Core.CpsRepository2.RepositoryEntity, boolean)
	 */
	public void addReferenceUser(Reference<?> ref, RepositoryEntity entity, boolean isRequired)
			throws NullPointerException
	{
		if (ref == null)
		{
			throw new NullPointerException("Reference can not be null");
		}
		if (entity == null)
		{
			throw new NullPointerException("RepositoryEntity can not be null");
		}
		ReferenceUsage refu = new ReferenceUsageImpl(ref, entity, isRequired);
		Set<ReferenceUsage> usageSet = usage.get(ref);
		if (usageSet == null)
		{
			usageSet = new HashSet<ReferenceUsage>();
			usage.put(ref, usageSet);
		}
		usageSet.add(refu);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.ReferenceManager#getReferenceUsage
	 * (Composestar.Core.CpsRepository2.References.Reference)
	 */
	public Collection<ReferenceUsage> getReferenceUsage(Reference<?> ref)
	{
		if (ref == null)
		{
			return Collections.emptyList();
		}
		Collection<ReferenceUsage> res = usage.get(ref);
		if (res == null)
		{
			return Collections.emptyList();
		}
		Iterator<ReferenceUsage> it = res.iterator();
		while (it.hasNext())
		{
			if (it.next().getUser() == null)
			{
				it.remove();
			}
		}
		return res;
	}
}
