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
	 * @param entityName the name of the entity
	 * @throws IllegalArgumentException Throw when the entity name is empty
	 * @throws NullPointerException Thrown when the name is null
	 */
	protected AbstractQualifiedRepositoryEntity(String entityName) throws NullPointerException,
			IllegalArgumentException
	{
		this();
		if (entityName == null)
		{
			throw new NullPointerException("Name can not be null");
		}
		if (entityName.length() == 0)
		{
			throw new IllegalArgumentException("Name can not be empty");
		}
		name = entityName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.QualifiedRepositoryEntity#getFullyQualifiedName()
	 */
	public String getFullyQualifiedName()
	{
		StringBuilder sb = new StringBuilder();
		if (getOwner() instanceof QualifiedRepositoryEntity)
		{
			sb.append(((QualifiedRepositoryEntity) getOwner()).getFullyQualifiedName());
			sb.append('.');
		}
		else
		{
			RepositoryEntity o = getOwner();
			while (o != null)
			{
				if (o instanceof QualifiedRepositoryEntity)
				{
					sb.append(((QualifiedRepositoryEntity) getOwner()).getFullyQualifiedName());
					sb.append('.');
					break;
				}
				o = o.getOwner();
			}
		}
		sb.append(getName());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + super.toString() + "] " + getFullyQualifiedName();
	}
}
