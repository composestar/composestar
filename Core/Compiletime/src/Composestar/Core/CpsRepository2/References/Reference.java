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

package Composestar.Core.CpsRepository2.References;

import java.io.Serializable;

/**
 * The generic interface for all references. A reference can be a
 * self-reference, in which case the reference is implemented by a type which is
 * also the reference type. An example of a self-reference is the
 * {@link Composestar.Core.CpsRepository2.FilterModules.FilterModule} interface
 * which is also a {@link FilterModuleReference}. In this case the filter module
 * returns itself when the reference is requested. The reference id is usually
 * the fully qualified name of the self-reference. Self-references throw an
 * UnsupportedOperationException when the {@link #setReferenceId(String)} and
 * {@link #setReference(Object)} methods are called.
 * 
 * @author Michiel Hendriks
 */
public interface Reference<T> extends Serializable
{
	/**
	 * @return The reference identifier, this is the name of the entity to which
	 *         is referred.
	 */
	String getReferenceId();

	/**
	 * @return The element to which this reference points to. The result is null
	 *         when the reference has not be resolved, or when it could not be
	 *         resolved because the referred to element does not exist.
	 * @see #isResolved()
	 */
	T getReference();

	/**
	 * Set the reference to the resolved element.
	 * 
	 * @param element The element to which the reference refers to. When the
	 *            value is null this reference is flagged as being an
	 *            non-existing element.
	 * @throws UnsupportedOperationException Thrown by so-called self-references
	 */
	void setReference(T element) throws UnsupportedOperationException;

	/**
	 * Unset the referenced element and reset the "isResolved" value.
	 * 
	 * @throws UnsupportedOperationException Thrown by self-references
	 */
	void dereference() throws UnsupportedOperationException;

	/**
	 * @return Returns true when {@link #getReference()} returns a valid result.
	 *         This result could be null when the element was not found.
	 *         Self-references always return true.
	 */
	boolean isResolved();

	/**
	 * @return True for self-references
	 */
	boolean isSelfReference();
}
