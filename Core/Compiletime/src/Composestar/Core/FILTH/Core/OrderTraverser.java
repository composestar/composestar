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
import java.util.NoSuchElementException;
import java.util.Random;

public class OrderTraverser
{
	public static LinkedList cloneLinkedList(LinkedList l)
	{
		LinkedList temp = new LinkedList();
		for (Iterator i = l.iterator(); i.hasNext();)
		{
			temp.add(i.next());
		}
		return temp;
	}

	public LinkedList multiTraverse(Graph g)
	{
		LinkedList startingNodes = g.getNodes();
		LinkedList multiOrder = new LinkedList();
		LinkedList order = new LinkedList();

		Node current = g.getRoot();
		multiOrder.add(takeOnePath(current, startingNodes, order, multiOrder));
		return multiOrder;

	}

	private LinkedList takeOnePath(Node current, LinkedList nodes, LinkedList order, LinkedList multiOrder)
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

			LinkedList candidates = selectCandidates(nodes, order);

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
				 * ez a rész nem jó, ez már nem kell , a rekurziónak a két
				 * kettévált sorokat kell felhalmoznia, gy?jtenie /* select the
				 * next node into the execution order
				 */
				current = (Node) candidates.getFirst();
			}

			if (candidates.size() > 1)
			{
				Iterator i = candidates.iterator();

				/*
				 * multiple candidates -> the case of recursion, a new path is
				 * available
				 */
				for (int c = 0; i.hasNext(); c++)
				{
					LinkedList newNodes = OrderTraverser.cloneLinkedList(nodes);
					LinkedList newOrder = OrderTraverser.cloneLinkedList(order);
					if (c == 0)
					{
						current = (Node) i.next();
					}
					else
					{
						multiOrder.add(takeOnePath((Node) i.next(), newNodes, newOrder, multiOrder));
					}
				}
			}

		}
		// return null;

	}

	private LinkedList selectCandidates(LinkedList available, LinkedList used)
	{
		LinkedList candidates = new LinkedList();
		boolean pc;

		/* Iterating through all the available nodes */
		for (Iterator i = available.iterator(); i.hasNext();)
		{
			Node current = (Node) i.next();
			/* Checking all the incoming edges of a node */
			pc = true;
			for (Iterator j = current.getIncomingEdges().iterator(); j.hasNext();)
			{
				Node aParent = ((Edge) j.next()).getLeft();
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
	public LinkedList traverse(Graph g)
	{
		LinkedList nodes = g.getNodes();
		LinkedList order = new LinkedList();

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
	private Node selectOpenNode(LinkedList available, LinkedList used)
	{
		LinkedList candidates = new LinkedList();
		boolean pc;

		/* Iterating through all the available nodes */
		for (Iterator i = available.iterator(); i.hasNext();)
		{
			Node current = (Node) i.next();
			/* Checking all the incoming edges of a node */
			pc = true;
			for (Iterator j = current.getIncomingEdges().iterator(); j.hasNext();)
			{
				Node aParent = ((Edge) j.next()).getLeft();
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
		return (Node) candidates.get(new Random().nextInt(candidates.size()));
	}

	/* ====== algoruthm for finding cycles ======= */
	private void exploreCycle(LinkedList available, LinkedList used)
	{
		// System.out.println("exploring a cycle: going back ");

		LinkedList cycle = new LinkedList();
		Node start = null, aParent;
		Edge currentEdge = null;

		/* select an available node that has a parent among the used nodes: */
		for (Iterator i = available.iterator(); i.hasNext();)
		{
			Node current = (Node) i.next();

			for (Iterator j = current.getIncomingEdges().iterator(); (start == null) && (j.hasNext());)
			{
				currentEdge = (Edge) j.next();
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
					current = (Pair) cycle.getFirst();
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
				current = (Pair) cycle.getFirst();
			}
			else
			{
				cycle.addFirst(current);
			}

		}
	}

	/* ====== Pair: An Entity of the database for detecting cycles ======= */
	public class Pair
	{
		private Node _node;

		private LinkedList _edges = new LinkedList();

		public Pair(Node node)
		{
			_node = node;
		}

		public void addEdge(Edge e)
		{
			_edges.add(e);
		}

		public Edge removeAnEdge()
		{
			Edge deleted = null;
			if (_edges.size() != 0)
			{
				deleted = (Edge) _edges.removeFirst();
			}
			return deleted;
		}

		public Node getNode()
		{
			return _node;
		}

		public boolean isInThePath(LinkedList path)
		{

			for (Iterator i = path.iterator(); i.hasNext();)
			{
				if (((Pair) i.next()).getNode().getElement().equals(this._node.getElement()))
				{
					return true;
				}
			}
			return false;
		}
	}

	/* ====== End of Pair ======= */

	private Pair extendPath(LinkedList db, Node n, Edge exc)
	{
		Edge e;

		Pair p = new Pair(n);
		for (Iterator i = n.getIncomingEdges().iterator(); i.hasNext();)
		{
			e = (Edge) i.next();
			// we don't extend the database with the edges to the root node and
			// the node from which we come
			if ((!e.equals(exc)) && (!e.getLeft().getElement().equals("root"))) // not
			// the
			// exception
			// edge
			// and
			// the
			// edges
			// referring
			// back
			// to
			// the
			// root
			// node
			{
				p.addEdge(e);
			}

		}
		// db.addFirst(p);
		return p;
	}

	private void printCycle(LinkedList path, Pair stopPair)
	{
		Node n;
		// System.out.println("<<<cycle-begin>>>");
		for (Iterator i = path.iterator(); i.hasNext();)
		{
			n = ((Pair) i.next()).getNode();
			// System.out.print(n.getElement()+" ");
			if (stopPair.getNode().getElement().equals(n.getElement()))
			{
				break;
			}
		}
		// System.out.println("\n<<cycle-end>>>");
	}

}
