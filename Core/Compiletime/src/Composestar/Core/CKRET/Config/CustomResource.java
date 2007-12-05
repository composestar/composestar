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

package Composestar.Core.CKRET.Config;

import java.util.Collection;

/**
 * A custom resource
 * 
 * @author Michiel Hendriks
 */
public class CustomResource extends Resource
{
	private static final long serialVersionUID = -6629060849011765123L;

	/**
	 * Custom name;
	 */
	protected String name;

	/**
	 * @param intype
	 */
	public CustomResource(String inname)
	{
		super(ResourceType.Custom);
		setName(inname);
	}

	/**
	 * @param intype
	 * @param vocab
	 */
	public CustomResource(String inname, Collection<String> vocab)
	{
		super(ResourceType.Custom, vocab);
		setName(inname);
	}

	private void setName(String inname)
	{
		if (ResourceType.parse(inname) != ResourceType.Custom)
		{
			throw new IllegalArgumentException(String.format("%s is not a valid custom resource name.", inname));
		}
		name = inname;
	}

	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
		if (name != null)
		{
			result += name.hashCode();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CustomResource other = (CustomResource) obj;
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}
}
