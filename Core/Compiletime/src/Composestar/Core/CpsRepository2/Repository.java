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

package Composestar.Core.CpsRepository2;

import java.io.Serializable;
import java.util.Set;

/**
 * The repository keeps record of all repository entities.
 * 
 * @author Michiel Hendriks
 */
public interface Repository extends Serializable, Set<RepositoryEntity>
{
	/**
	 * Retrieve a qualified repository entity with a given fully qualified name.
	 * 
	 * @param fqn The name of the repository entity to retrieve.
	 * @return The qualified repository entity or null when it does not exist.
	 */
	QualifiedRepositoryEntity get(String fqn);

	/**
	 * Retrieve a qualified repository entity of a given type.
	 * 
	 * @param <T> A subtype of QualifiedRepositoryEntity that the requested
	 *            entity should be an instance of
	 * @param fqn The name of the repository entity to retrieve.
	 * @param type The type the entity has to be an instance of
	 * @return The qualified repository entity with the given name and type.
	 *         Returns null when no entity exists with that name and type.
	 */
	<T extends QualifiedRepositoryEntity> T get(String fqn, Class<T> type);

	/**
	 * Return an iterator that returns all instances of a give type. The
	 * returned iterator is also an Iterable.
	 * 
	 * @param <T>
	 * @param type The filter type
	 * @return An RepositoryIterator iterator that returns all repository
	 *         elements with the given type.
	 * @see #getAllAsSet(Class)
	 */
	<T extends RepositoryEntity> RepositoryIterator<T> getAll(Class<T> type);

	/**
	 * Returns an immutable subset that only contains elements of the given
	 * type. This method is much slower than getAll(Class) because this
	 * constructs a new set with all elements where getAll(Class) iterates the
	 * current set.
	 * 
	 * @param <T>
	 * @param type The filter type
	 * @return A set with all elements from the repository of the given type.
	 * @see #getAll(Class)
	 */
	<T extends RepositoryEntity> Set<T> getAllAsSet(Class<T> type);
}
