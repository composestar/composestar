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

package Composestar.Core.FILTH2.Ordering;

/**
 * An edge in the ordering graph
 * 
 * @author Michiel Hendrik
 */
public class OrderingEdge
{
	/**
	 * The origin
	 */
	protected OrderingNode source;

	/**
	 * The destination
	 */
	protected OrderingNode target;

	public OrderingEdge(OrderingNode from, OrderingNode to)
	{
		source = from;
		target = to;
		source.addOutgoingEdge(this);
		target.addIncomingEdge(this);
	}

	/**
	 * @return the source node
	 */
	public OrderingNode getSource()
	{
		return source;
	}

	/**
	 * @return the target node
	 */
	public OrderingNode getTarget()
	{
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return source.toString() + " -> " + target.toString();
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
		int result = 1;
		result = prime * result + (source == null ? 0 : source.hashCode());
		result = prime * result + (target == null ? 0 : target.hashCode());
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
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final OrderingEdge other = (OrderingEdge) obj;
		if (source == null)
		{
			if (other.source != null)
			{
				return false;
			}
		}
		else if (!source.equals(other.source))
		{
			return false;
		}
		if (target == null)
		{
			if (other.target != null)
			{
				return false;
			}
		}
		else if (!target.equals(other.target))
		{
			return false;
		}
		return true;
	}
}
