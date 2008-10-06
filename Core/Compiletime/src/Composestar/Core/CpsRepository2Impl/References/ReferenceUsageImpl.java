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

import java.lang.ref.WeakReference;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.References.Reference;
import Composestar.Core.CpsRepository2.References.ReferenceUsage;

/**
 * Default implementation for the ReferenceUsage record
 * 
 * @author Michiel Hendriks
 */
public class ReferenceUsageImpl implements ReferenceUsage
{
	private static final long serialVersionUID = -940121129404513355L;

	/**
	 * The reference
	 */
	protected Reference<?> reference;

	/**
	 * A weak reference to a repository entity
	 */
	protected WeakReference<RepositoryEntity> reposEntity;

	/**
	 * Flag to see if the reference value is required
	 */
	protected boolean reqFlag;

	public ReferenceUsageImpl(Reference<?> ref, RepositoryEntity entity, boolean isRequired)
			throws NullPointerException
	{
		if (ref == null)
		{
			throw new NullPointerException("Reference can not be null");
		}
		if (entity == null)
		{
			throw new NullPointerException("Entity can not be null");
		}
		reference = ref;
		reposEntity = new WeakReference<RepositoryEntity>(entity);
		reqFlag = isRequired;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.ReferenceUsage#getReference()
	 */
	public Reference<?> getReference()
	{
		return reference;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.References.ReferenceUsage#getUser()
	 */
	public RepositoryEntity getUser()
	{
		return reposEntity.get();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.ReferenceUsage#isRequired()
	 */
	public boolean isRequired()
	{
		return reqFlag;
	}

}
