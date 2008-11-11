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

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import Composestar.Core.LAMA.MethodInfo;

/**
 * Defines the interface for calculated concern signatures. Calculated
 * signatures are required in the runtime. Therefore this interface is part of
 * the language repository.
 * 
 * @author Michiel Hendriks
 */
public interface Signature extends Serializable
{
	/**
	 * Add a method info wrapper to the list
	 * 
	 * @param miw The wrapper to add
	 * @return True when the wrapper was added to the list
	 * @throws NullPointerException Thrown when the wrapper is null or when it
	 *             doesn't have a MethodInfo object assigned.
	 */
	boolean addMethodInfoWrapper(MethodInfoWrapperImpl miw) throws NullPointerException;

	/**
	 * @param relation
	 * @return
	 * @see #getMethods(EnumSet)
	 */
	Collection<MethodInfo> getMethods(MethodRelation relation);

	/**
	 * Get all methodinfo objects with a certain relation
	 * 
	 * @param relations
	 * @return
	 */
	Collection<MethodInfo> getMethods(EnumSet<MethodRelation> relations);

	/**
	 * @return All method info objects
	 */
	Collection<MethodInfo> getMethods();

	/**
	 * @param relation
	 * @return
	 * @see SignatureImpl#getMethodInfoWrappers(EnumSet)
	 */
	Collection<MethodInfoWrapperImpl> getMethodInfoWrappers(MethodRelation relation);

	/**
	 * Get all methodinfowrapper objects with a certain relation
	 * 
	 * @param relations
	 * @return
	 */
	Collection<MethodInfoWrapperImpl> getMethodInfoWrappers(EnumSet<MethodRelation> relations);

	/**
	 * @return All method info wrappers
	 */
	Collection<MethodInfoWrapperImpl> getMethodInfoWrappers();

	/**
	 * Get a method info wrapper for a given method info object (using its
	 * generated key)
	 * 
	 * @param mi
	 * @return
	 * @throws NullPointerException
	 */
	MethodInfoWrapperImpl getMethodInfoWrapper(MethodInfo mi) throws NullPointerException;

	/**
	 * Remove a methodinfo wrapper
	 * 
	 * @param miw
	 */
	void removeMethodInfoWrapper(MethodInfoWrapper miw);

	/**
	 * Return true if there is a method with the given key
	 * 
	 * @param dnmi
	 * @return True when a method info exists with the same key
	 * @throws NullPointerException Thrown when the methodinfo is null
	 */
	boolean hasMethod(MethodInfo dnmi) throws NullPointerException;

	/**
	 * Returns true if there is a method with the given name.
	 * 
	 * @param methodName
	 * @return
	 */
	boolean hasMethod(String methodName);

}
