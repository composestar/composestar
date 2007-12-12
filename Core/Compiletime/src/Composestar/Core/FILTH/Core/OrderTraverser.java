/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.Core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class OrderTraverser
{
	public static LinkedList<Node> cloneLinkedList(LinkedList<Node> l)
	{
		LinkedList<Node> temp = new LinkedList<Node>();
		for (Node aL : l)
		{
			temp.add(aL);
		}
		return temp;
	}

	public LinkedList<List<Node>> multiTraverse(Graph g)
	{
		LinkedList<Node> startingNodes = g.getNodes();
		LinkedList<List<Node>> multiOrder = new LinkedList<List<Node>>();
		LinkedList<Node> order = new LinkedList<Node>();

		Node current = g.getRoot();
		multiOrder.add(takeOnePath(current, startingNodes, order, multiOrder));
		return multiOrder;

	}

	private List<Node> takeOnePath(Node current, LinkedList<Node> nodes, LinkedList<Node> order,
			LinkedList<List<Node>> multiOrder)
	{

		while (true)
		{
			// System.out.println("current --->"+current.getElement());

			// System.out.println("<--- begin --->");
			// for (Iterator t=nodes.iterator();t.hasNext(); )
			// System.out.print(((Node)t.next()).getElement()+" ");
			// System.out.println("\n<--- end --->");

			/* adding the node to the generated order */
			order.addLast(current);
			/* removing the current node from the list of available nodes */
			nodes.remove(current);

			/* when there is no node for expansion two situations are possible: */
			/* when the set of available nodes is empty */
			if (nodes.isEmpty())
			{
				/* we reached every node, the final order is ready */
				return order;
			}

			LinkedList<Node> candidates = selectCandidates(nodes, order);

			if (candidates == null)
			{
				exploreCycle(nodes, order);
			}

			// System.out.println("<--- candidates begin --->");
			// for (Iterator t=candidates.iterator();t.hasNext(); )
			// System.out.print(((Node)t.next()).getElement()+" ");
			// System.out.println("\n<--- candidates end --->");

			if (candidates.size() == 1)
			{
				/*
				 * it isn't correct, that its unendly recursive It is just the
				 * way it works, ok ? select the next node into the execution
				 * order
				 */
				current = candidates.getFirst();
			}

			if (candidates.size() > 1)
			{
				Iterator<Node> i = candidates.iterator();

				/*
				 * multiple candidates -> the case of recursion, a new path is
				 * available
				 */
				for (int c = 0; i.hasNext(); c++)
				{
					LinkedList<Node> newNodes = OrderTraverser.cloneLinkedList(nodes);
					LinkedList<Node> newOrder = OrderTraverser.cloneLinkedList(order);
					if (c == 0)
					{
						current = i.next();
					}
					else
					{
						multiOrder.add(takeOnePath(i.next(), newNodes, newOrder, multiOrder));
					}
				}
			}

		}
		// return null;

	}

	private LinkedList<Node> selectCandidates(LinkedList<Node> available, LinkedList<Node> used)
	{
		LinkedList<Node> candidates = new LinkedList<Node>();
		boolean pc;

		/* Iterating through all the available nodes */
		for (Node current : available)
		{
			/* Checking all the incoming edges of a node */
			pc = true;
			for (Edge o : current.getIncomingEdges())
			{
				Node aParent = o.getLeft();
				if (!used.contains(aParent))
				{
					pc = false;
					break;
				}
			}
			/*
			 * if all the parents has already been traversed and parents are
			 * passed
			 */
			if (pc)
			{
				candidates.add(current);
			}
		}
		/* if there is no candidate node return null */
		if (candidates.isEmpty())
		{
			return null;
		}
		/*
		 * if there are more possible candidates are available select one
		 * randomly ~ non-determinism
		 */
		return candidates;
	}

	// //////////////////////////////////////////////////
	public LinkedList<Node> traverse(Graph g)
	{
		LinkedList<Node> nodes = g.getNodes();
		LinkedList<Node> order = new LinkedList<Node>();

		Node current;

		/* By default, the traverse starts at the root node; it will be grey */
		current = g.getRoot();
		while (true)
		{
			// System.out.println("<--- begin --->");
			// for (Iterator t=nodes.iterator();t.hasNext(); )
			// System.out.print(((Node)t.next()).getElement()+" ");
			// System.out.println("\n<--- end --->");

			/* when there is a next node for expansion */
			if (current != null)
			{
				/* adding the node to the generated order */
				order.addLast(current);
				/* removing the current node from the list of available nodes */
				nodes.remove(current);
			}
			else
			{
				/*
				 * when there is no node for expansion two situations are
				 * possible:
				 */
				/* when the set of available nodes is empty */
				if (nodes.isEmpty())
				{
					/* we reached every node, the final order is ready */
					return order;
				}
				else
				{
					/* else there is a cycle in the remainder part of the graph */
					exploreCycle(nodes, order);
				}
			}
			/* select a node as a next one into the execution order */
			current = selectOpenNode(nodes, order);

		}

		// return order;
	}

	/* ====== algorithm for selecting a node into the order ======= */
	private Node selectOpenNode(LinkedList<Node> available, LinkedList<Node> used)
	{
		LinkedList<Node> candidates = new LinkedList<Node>();
		boolean pc;

		/* Iterating through all the available nodes */
		for (Node current : available)
		{
			/* Checking all the incoming edges of a node */
			pc = true;
			for (Edge o : current.getIncomingEdges())
			{
				Node aParent = o.getLeft();
				if (!used.contains(aParent))
				{
					pc = false;
					break;
				}
			}
			/*
			 * if all the parents has already been traversed and parents are
			 * passed
			 */
			if (pc)
			{
				candidates.add(current);
			}
		}
		/* if there is no candidate node return null */
		if (candidates.isEmpty())
		{
			return null;
		}
		/*
		 * if there are more possible candidates are available select one
		 * randomly ~ non-determinism
		 */
		return candidates.get(new Random().nextInt(candidates.size()));
	}

	/* ====== algoruthm for finding cycles ======= */
	private void exploreCycle(LinkedList<Node> available, LinkedList<Node> used)
	{
		// System.out.println("exploring a cycle: going back ");

		LinkedList<Pair> cycle = new LinkedList<Pair>();
		Node start = null, aParent;
		Edge currentEdge = null;

		/* select an available node that has a parent among the used nodes: */
		for (Node current : available)
		{
			for (Edge cure : current.getIncomingEdges())
			{
				currentEdge = cure;
				aParent = currentEdge.getLeft();
				/* we've found a node with a parent already passed */
				if (used.contains(aParent))
				{
					start = currentEdge.getRight();
				}
			}
		}

		if (start == null)
		{
			throw new RuntimeException("error: there is no starting node for the cycle");
		}

		/*
		 * let's go back from the starting node till we reach a node that
		 * appears in the path again
		 */
		Pair current;
		Edge e;

		current = extendPath(cycle, start, currentEdge);
		cycle.addFirst(current);

		while (true)
		{
			// select an edge for processing it
			e = current.removeAnEdge();

			// if there is no edge we have to step back
			while (e == null)
			{
				try
				{
					cycle.removeFirst();
					current = cycle.getFirst();
					e = current.removeAnEdge();
				}
				catch (NoSuchElementException xe)
				{
					throw new RuntimeException("cycle detection - finished");
				}
			}

			// else extend the database with the right node of the edge
			current = extendPath(cycle, e.getLeft(), currentEdge);

			// if the node has already been at path we found a cycle
			if (current.isInThePath(cycle))
			{
				printCycle(cycle, current);
				// go back in the database to the previous root
				current = cycle.getFirst();
			}
			else
			{
				cycle.addFirst(current);
			}

		}
	}

	/* ====== Pair: An Entity of the database for detecting cycles ======= */
	public static class Pair
	{
		private Node node;

		private LinkedList<Edge> edges = new LinkedList<Edge>();

		public Pair(Node innode)
		{
			node = innode;
		}

		public void addEdge(Edge e)
		{
			edges.add(e);
		}

		public Edge removeAnEdge()
		{
			Edge deleted = null;
			if (edges.size() != 0)
			{
				deleted = edges.removeFirst();
			}
			return deleted;
		}

		public Node getNode()
		{
			return node;
		}

		public boolean isInThePath(LinkedList<Pair> path)
		{
			for (Pair aPath : path)
			{
				if (aPath.getNode().getElement().equals(node.getElement()))
				{
					return true;
				}
			}
			return false;
		}
	}

	/* ====== End of Pair ======= */

	private Pair extendPath(LinkedList<Pair> db, Node n, Edge exc)
	{
		Pair p = new Pair(n);
		for (Edge e : n.getIncomingEdges())
		{
			// we don't extend the database with the edges to the root node and
			// the node from which we come
			if ((!e.equals(exc)) && (!e.getLeft().getElement().equals("root")))
			/*
			 * not the exception edge and the edges referring back to the root
			 * node
			 */
			{
				p.addEdge(e);
			}

		}
		// db.addFirst(p);
		return p;
	}

	private void printCycle(LinkedList<Pair> path, Pair stopPair)
	{
		// System.out.println("<<<cycle-begin>>>");
		for (Pair aPath : path)
		{
			Node n = aPath.getNode();
			// System.out.print(n.getElement()+" ");
			if (stopPair.getNode().getElement().equals(n.getElement()))
			{
				break;
			}
		}
		// System.out.println("\n<<cycle-end>>>");
	}

}
