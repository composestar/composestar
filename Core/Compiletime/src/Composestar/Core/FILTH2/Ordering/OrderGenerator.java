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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.FILTH2.FILTH;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.OrderingConstraint;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This creates all possible orders from a set of actions with constraints. See
 * Istvan Nagy's PhD Thesis for information about this algorithm. This is only
 * the first step in creating proper orderings. All ordering constraints will be
 * considered to be "soft pre" constraints.
 * 
 * @author Michiel Hendriks
 */
public class OrderGenerator
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(FILTH.MODULE_NAME);

	private OrderGenerator()
	{}

	/**
	 * Generates all possible orderings using the set of actions. The orders
	 * will only be validated according to the ordering constraints.
	 * 
	 * @param actions
	 * @return
	 */
	public static Set<List<Action>> generate(Collection<Action> actions)
	{
		OrderingNode rootNode = new OrderingNode();
		Set<OrderingNode> nodes = createGraph(actions, rootNode);
		Set<List<OrderingNode>> tmpResult = new HashSet<List<OrderingNode>>();
		traverseAll(rootNode, new ArrayList<OrderingNode>(), nodes, tmpResult);

		Set<List<Action>> result = new HashSet<List<Action>>();
		for (List<OrderingNode> sorder : tmpResult)
		{
			List<Action> sres = new ArrayList<Action>();
			sorder.remove(0); // remove the root node
			for (OrderingNode node : sorder)
			{
				sres.add(node.getAction());
			}
			result.add(sres);
		}
		return result;
	}

	/**
	 * Create a set of nodes from the actions. These nodes are used for easier
	 * traversal and order generation in the algorithm as defined by Istvan Nagy
	 * in his PhD thesis.
	 * 
	 * @param actions
	 * @param rootNode
	 * @return
	 */
	protected static Set<OrderingNode> createGraph(Collection<Action> actions, OrderingNode rootNode)
	{
		Set<OrderingNode> result = new HashSet<OrderingNode>();
		Map<Action, OrderingNode> lookup = new HashMap<Action, OrderingNode>();
		// create all nodes
		for (Action action : actions)
		{
			OrderingNode node = new OrderingNode(action);
			result.add(node);
			lookup.put(action, node);
			new OrderingEdge(rootNode, node);
		}
		// create edges using the constraints
		for (Entry<Action, OrderingNode> entry : lookup.entrySet())
		{
			for (OrderingConstraint constraint : entry.getKey().getConstraints(OrderingConstraint.class))
			{
				OrderingNode dest = lookup.get(constraint.getRight());
				if (dest != null)
				{
					new OrderingEdge(entry.getValue(), dest);
				}
			}
		}
		return result;
	}

	/**
	 * This method does the actual creation of orderings
	 * 
	 * @param current
	 * @param currentOrder
	 * @param nodes
	 * @param result
	 */
	protected static void traverseAll(OrderingNode current, List<OrderingNode> currentOrder, Set<OrderingNode> nodes,
			Set<List<OrderingNode>> result)
	{
		while (current != null && nodes.size() > 0)
		{
			currentOrder.add(current);
			nodes.remove(current);

			if (nodes.isEmpty())
			{
				result.add(currentOrder);
				return;
			}

			Set<OrderingNode> nextNodes = getNextNodes(nodes, currentOrder);

			current = null;
			for (OrderingNode node : nextNodes)
			{
				if (current == null)
				{
					current = node;
				}
				else
				{
					traverseAll(node, new ArrayList<OrderingNode>(currentOrder), new HashSet<OrderingNode>(nodes),
							result);
				}
			}
		}
		logger.info(String.format(
				"Finished a order traversal without an result. Current: %s; Last: Order: %s; Available: %s", current,
				currentOrder, nodes));
	}

	/**
	 * Selects all nodes from the available list that have an incoming edge from
	 * a node in the used list.
	 * 
	 * @param available
	 * @param used
	 * @return
	 */
	protected static Set<OrderingNode> getNextNodes(Set<OrderingNode> available, Collection<OrderingNode> used)
	{
		Set<OrderingNode> result = new HashSet<OrderingNode>();
		for (OrderingNode node : available)
		{
			boolean isNext = true;
			for (OrderingEdge edge : node.getIncoming())
			{
				if (!used.contains(edge.getSource()))
				{
					isNext = false;
					break;
				}
			}
			if (isNext)
			{
				result.add(node);
			}
		}
		return result;
	}
}
