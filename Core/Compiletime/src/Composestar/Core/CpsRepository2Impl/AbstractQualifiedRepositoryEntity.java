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

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class AbstractQualifiedRepositoryEntity extends AbstractRepositoryEntity implements
		QualifiedRepositoryEntity
{
	/**
	 * The entity's name
	 */
	protected String name;

	/**
	 * Create an entity without a name. Only use this when you are setting the
	 * name in a other constructor.
	 * 
	 * @see #AbstractQualifiedRepositoryEntity(String)
	 */
	protected AbstractQualifiedRepositoryEntity()
	{
		super();
	}

	/**
	 * Creates an qualified repository entity
	 * 
	 * @param entityName
	 * @throws IllegalArgumentException Throw when the entity name is null or
	 *             empty
	 */
	protected AbstractQualifiedRepositoryEntity(String entityName) throws IllegalArgumentException
	{
		this();
		name = entityName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		StringBuilder sb = new StringBuilder(name);
		RepositoryEntity o = getOwner();
		while (o != null)
		{
			if (o instanceof QualifiedRepositoryEntity)
			{
				sb.insert(0, '.');
				sb.insert(0, ((QualifiedRepositoryEntity) o).getName());
			}
			o = o.getOwner();
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getName()
	 */
	public String getName()
	{
		return name;
	}

}
