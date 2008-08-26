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

import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * The base interface for all first level entities in the CPS Repository.
 * 
 * @author Michiel Hendriks
 */
public interface RepositoryEntity extends Serializable
{
	/**
	 * Assign source information with this entity.
	 * 
	 * @param srcInfo
	 */
	void setSourceInformation(SourceInformation srcInfo);

	/**
	 * @return The associated source information for this entity. The return
	 *         value can be null, this is usually the case when the entity did
	 *         not originate from a source file.
	 */
	SourceInformation getSourceInformation();

	/**
	 * @return the repository that is considered the owner of this entity. Root
	 *         entities like Concern entities have no owner, and they will
	 *         return null.
	 */
	RepositoryEntity getOwner();

	/**
	 * Assigns the owner for this entity. Setting a new owner when a previous
	 * owner was set can be dangerous the previous owner could still reference
	 * to this entity in its internal state.
	 * 
	 * @param newOwner The new owner of this object.
	 * @return the previous owner or null if the entity was not owned before.
	 */
	RepositoryEntity setOwner(RepositoryEntity newOwner);
}
