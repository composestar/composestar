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

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * An abstract implementation of the RepositoryEntity interface
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractRepositoryEntity implements RepositoryEntity
{
	/**
	 * Holds the owner variable
	 */
	protected RepositoryEntity owner;

	/**
	 * Reference to the source information
	 */
	protected SourceInformation sourceInformation;

	/**
	 * Default constructor. Does not do anything.
	 */
	protected AbstractRepositoryEntity()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
	 */
	public RepositoryEntity getOwner()
	{
		return owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation()
	 */
	public SourceInformation getSourceInformation()
	{
		return sourceInformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar.Core.CpsRepository2.RepositoryEntity)
	 */
	public RepositoryEntity setOwner(RepositoryEntity newOwner)
	{
		RepositoryEntity old = owner;
		owner = newOwner;
		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation(Composestar.Core.CpsRepository2.Meta.SourceInformation)
	 */
	public void setSourceInformation(SourceInformation srcInfo)
	{
		sourceInformation = srcInfo;
	}
}
