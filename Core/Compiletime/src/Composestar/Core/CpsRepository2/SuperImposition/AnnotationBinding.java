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

package Composestar.Core.CpsRepository2.SuperImposition;

import java.util.Collection;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.References.TypeReference;

/**
 * This defined an annotation binding which binds annotations (full type given)
 * to a set of program elements as selected by the selector.
 * 
 * @author Michiel Hendriks
 */
public interface AnnotationBinding extends RepositoryEntity
{
	/**
	 * Sets the selector to which annotations should be bound
	 * 
	 * @param sel The selector to bind annotations to
	 * @throws NullPointerException thrown when the given selector is null
	 */
	void setSelector(Selector sel) throws NullPointerException;

	/**
	 * @return The selector to which annotations should be bound.
	 */
	Selector getSelector();

	/**
	 * Adds a annotation to be superimposed. Annotations are only superimposed
	 * once. Calling this method multiple times with the same annotation type
	 * has no effect.
	 * 
	 * @param annotationType The annotation type to superimposed
	 * @throws NullPointerException Thrown when the given type is null.
	 */
	void addAnnotation(TypeReference annotationType) throws NullPointerException;

	/**
	 * Removes an annotation type from the binding.
	 * 
	 * @param annotationType The annotation type to remove
	 * @return Returns the annotation type when it was removed, or null when it
	 *         was not found.
	 * @throws NullPointerException Thrown when the annotation type is null.
	 */
	TypeReference removeAnnotation(TypeReference annotationType) throws NullPointerException;

	/**
	 * Remove an annotation type by the reference id.
	 * 
	 * @param referenceId The id of the type reference to remove.
	 * @return The annotation type removed, or null when no type with the given
	 *         reference id was bound.
	 */
	TypeReference removeAnnotation(String referenceId);

	/**
	 * @return The collection of annotations which should be superimposed on the
	 *         selector. If no annotations should be superimposed an empty
	 *         collection is returned. The returned collection is read only.
	 */
	Collection<TypeReference> getAnnotations();
}
