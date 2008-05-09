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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.FILTH2.Model.Action;

/**
 * A node used in the generation of all possible orders
 * 
 * @author Michiel Hendriks
 */
public class OrderingNode
{
	/**
	 * The associated action
	 */
	protected Action action;

	/**
	 * Incoming edges
	 */
	protected Set<OrderingEdge> incoming;

	/**
	 * Outgoing edges
	 */
	protected Set<OrderingEdge> outgoing;

	public OrderingNode()
	{
		incoming = new HashSet<OrderingEdge>();
		outgoing = new HashSet<OrderingEdge>();
	}

	public OrderingNode(Action inaction)
	{
		this();
		action = inaction;
	}

	/**
	 * @return the associated action, is null for the root node
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * Add a new incoming edge
	 * 
	 * @param edge
	 */
	public void addIncomingEdge(OrderingEdge edge)
	{
		incoming.add(edge);
	}

	/**
	 * Add a new outgoing edge
	 * 
	 * @param edge
	 */
	public void addOutgoingEdge(OrderingEdge edge)
	{
		outgoing.add(edge);
	}

	/**
	 * @return all incoming edges
	 */
	public Set<OrderingEdge> getIncoming()
	{
		return Collections.unmodifiableSet(incoming);
	}

	/**
	 * @return all outgoing edges
	 */
	public Set<OrderingEdge> getOutgoing()
	{
		return Collections.unmodifiableSet(outgoing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (action != null)
		{
			return action.getName();
		}
		else
		{
			return "[root]";
		}
	}
}
