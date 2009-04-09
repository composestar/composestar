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

import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.ControllConstraint;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.OrderingConstraint;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * This creates all possible orders from a set of actions with constraints. See
 * Istvan Nagy's PhD Thesis for information about this algorithm. This is only
 * the first step in creating proper orderings. All ordering constraints will be
 * considered to be "soft pre" constraints.
 * 
 * @author Michiel Hendriks
 */
public final class OrderGenerator
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FILTH);

	private OrderGenerator()
	{}

	/**
	 * Generates all possible orderings using the set of actions. The orders
	 * will only be validated according to the ordering constraints.
	 * 
	 * @param actions
	 * @param maxOrders if >0 limit the maximum orders to this number
	 * @return
	 */
	public static Set<List<ImposedFilterModule>> generate(Collection<ImposedFilterModule> actions,
			Set<Constraint> constraints, int maxOrders)
	{
		OrderingNode rootNode = new OrderingNode();
		Set<OrderingNode> nodes = createGraph(actions, constraints, rootNode);
		Set<List<OrderingNode>> tmpResult = new HashSet<List<OrderingNode>>();
		traverseAll(rootNode, new ArrayList<OrderingNode>(), nodes, tmpResult, maxOrders);

		Set<List<ImposedFilterModule>> result = new HashSet<List<ImposedFilterModule>>();
		for (List<OrderingNode> sorder : tmpResult)
		{
			List<ImposedFilterModule> sres = new ArrayList<ImposedFilterModule>();
			sorder.remove(0); // remove the root node
			for (OrderingNode node : sorder)
			{
				sres.add(node.getImposedFilterModule());
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
	protected static Set<OrderingNode> createGraph(Collection<ImposedFilterModule> actions,
			Set<Constraint> constraints, OrderingNode rootNode)
	{
		Set<OrderingNode> result = new HashSet<OrderingNode>();
		Map<String, OrderingNode> lookup = new HashMap<String, OrderingNode>();
		// create all nodes
		for (ImposedFilterModule action : actions)
		{
			OrderingNode node = new OrderingNode(action);
			result.add(node);
			lookup.put(action.getFilterModule().getFullyQualifiedName(), node);
			new OrderingEdge(rootNode, node);
		}

		for (Constraint constraint : constraints)
		{
			if (constraint instanceof OrderingConstraint || constraint instanceof ControllConstraint)
			{
				ConstraintValue[] args = constraint.getArguments();
				if (args.length < 2)
				{
					continue;
				}
				if (!(args[0] instanceof FilterModuleConstraintValue))
				{
					// filter Skip,Cond constraints which have a non FM first
					// argument
					continue;
				}
				if (!(args[1] instanceof FilterModuleConstraintValue))
				{
					// this should probably never happen
					continue;
				}
				OrderingNode n1 = lookup.get(args[0].getStringValue());
				if (n1 == null)
				{
					// doesn't exist, so don't create an edge
					continue;
				}
				OrderingNode n2 = lookup.get(args[1].getStringValue());
				if (n2 == null)
				{
					// doesn't exist, so don't create an edge
					continue;
				}
				new OrderingEdge(n1, n2);
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
	 * @param maxOrders
	 */
	protected static void traverseAll(OrderingNode current, List<OrderingNode> currentOrder, Set<OrderingNode> nodes,
			Set<List<OrderingNode>> result, int maxOrders)
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
							result, maxOrders);
				}
				if (maxOrders > 0 && maxOrders <= result.size())
				{
					logger.info(String.format("Reached maximum allowed number of orders: %d", maxOrders));
					return;
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
