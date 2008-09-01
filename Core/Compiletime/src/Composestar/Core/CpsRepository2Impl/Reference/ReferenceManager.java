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

package Composestar.Core.CpsRepository2Impl.Reference;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.InstanceContextProvider;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;

/**
 * Manages soft-reference instances. Self references are not included in the
 * reference manager.
 * 
 * @author Michiel Hendriks
 */
public class ReferenceManager implements Serializable
{
	private static final long serialVersionUID = -400983142236822779L;

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

	public ReferenceManager()
	{
		typeRefs = new HashMap<String, TypeReference>();
		methodRefs = new HashMap<String, MethodReference>();
		instMethodRefs = new HashMap<String, InstanceMethodReference>();
		fmRefs = new HashMap<String, FilterModuleReference>();
	}

	/**
	 * Get a type reference with the given reference id. It will create a new
	 * type reference when no previous reference existed.
	 * 
	 * @param refid The reference id.
	 * @return The type reference
	 * @throws IllegalArgumentException Thrown when the reference id is empty
	 * @throws NullPointerException Thrown when the reference id is null
	 */
	public TypeReference getTypeReference(String refid) throws IllegalArgumentException, NullPointerException
	{
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.isEmpty())
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

	/**
	 * @return Read-only list of all known type references.
	 */
	public Collection<TypeReference> getTypeReferences()
	{
		return Collections.unmodifiableCollection(typeRefs.values());
	}

	/**
	 * Get a MethodInfo reference using a reference id of a type.
	 * 
	 * @param refid The reference id of the method reference
	 * @param typeRef The reference id of the type.
	 * @param jpca The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the type reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id or type
	 *             reference is empty
	 */
	public MethodReference getMethodReference(String refid, String typeRef, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException
	{
		return getMethodReference(refid, getTypeReference(typeRef), jpca);
	}

	/**
	 * Get a method reference using a type reference
	 * 
	 * @param refid The reference id of the method reference
	 * @param typeRef A type reference
	 * @param The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the type reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id is empty
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
		if (refid.isEmpty())
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(typeRef.getReferenceId());
		sb.append(0x1E);
		sb.append(refid);
		sb.append(0x1E);
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

	/**
	 * @return Read-only list of all method references
	 */
	public Collection<MethodReference> getMethodReferences()
	{
		return Collections.unmodifiableCollection(methodRefs.values());
	}

	/**
	 * Get an instance method reference
	 * 
	 * @param refid The reference id of the method reference
	 * @param context A context
	 * @param The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the context reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id is null
	 */
	public InstanceMethodReference getInstanceMethodReference(String refid, InstanceContextProvider context,
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
		if (refid.isEmpty())
		{
			throw new IllegalArgumentException("Reference ID is empty");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(context.getFullyQualifiedName());
		sb.append(0x1E);
		sb.append(refid);
		sb.append(0x1E);
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

	/**
	 * @return Read-only list of all method references
	 */
	public Collection<InstanceMethodReference> getInstanceMethodReferences()
	{
		return Collections.unmodifiableCollection(instMethodRefs.values());
	}

	/**
	 * Get a type reference with the given reference id. It will create a new
	 * type reference when no previous reference existed.
	 * 
	 * @param refid The reference id.
	 * @return The type reference
	 * @throws IllegalArgumentException Thrown when the reference id is empty
	 * @throws NullPointerException Thrown when the reference id is null
	 */
	public FilterModuleReference getFilterModuleReference(String refid) throws NullPointerException,
			IllegalArgumentException
	{
		if (refid == null)
		{
			throw new NullPointerException("Reference ID is null");
		}
		if (refid.isEmpty())
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

	/**
	 * @return Read-only list of all known type references.
	 */
	public Collection<FilterModuleReference> getFilterModuleReferences()
	{
		return Collections.unmodifiableCollection(fmRefs.values());
	}
}
