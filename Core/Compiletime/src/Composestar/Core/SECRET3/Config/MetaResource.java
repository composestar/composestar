/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.SECRET3.Config;

import java.util.Collection;
import java.util.Set;

/**
 * Meta resource class, used for rules. MetaResources can not be added to the
 * resource resource list in the secret repository.
 * 
 * @author Michiel Hendriks
 */
public final class MetaResource extends Resource
{
	private static final long serialVersionUID = -6159814813704593170L;

	/**
	 * @param intype
	 */
	public MetaResource(ResourceType intype)
	{
		if (!intype.isMeta())
		{
			throw new IllegalArgumentException("Only meta resource types are allowed");
		}
		type = intype;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#addVocabulary(java.util.Collection)
	 */
	@Override
	public void addVocabulary(Collection<String> words)
	{
		throw new IllegalArgumentException("MetaResources can not contain a vocabulary");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#addVocabulary(java.lang.String)
	 */
	@Override
	public void addVocabulary(String word)
	{
		throw new IllegalArgumentException("MetaResources can not contain a vocabulary");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#clearVocabulary()
	 */
	@Override
	public void clearVocabulary()
	{
		throw new IllegalArgumentException("MetaResources can not contain a vocabulary");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Resource other = (Resource) obj;
		if (type == null)
		{
			if (other.getType() != null)
			{
				return false;
			}
		}
		else if (!type.equals(other.getType()))
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#getVocabulary()
	 */
	@Override
	public Set<String> getVocabulary()
	{
		throw new IllegalArgumentException("MetaResources can not contain a vocabulary");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.SECRET3.Config.Resource#toString()
	 */
	@Override
	public String toString()
	{
		return type.toString();
	}
}
