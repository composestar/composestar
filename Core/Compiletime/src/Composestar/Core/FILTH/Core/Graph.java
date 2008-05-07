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

import java.util.LinkedList;

public class Graph
{
	/**
	 * The root node
	 */
	private Node root;

	/**
	 * Add nodes in this graph
	 */
	private LinkedList<Node> nodes = new LinkedList<Node>();

	/**
	 * All edges in this graph
	 */
	private LinkedList<Edge> edges = new LinkedList<Edge>();

	public Graph()
	{
		this(new Node(null));
	}

	public Graph(Node rootNode)
	{
		root = rootNode;
	}

	/**
	 * @return the root node
	 */
	public Node getRoot()
	{
		return root;
	}

	/**
	 * @return all nodes
	 */
	public LinkedList<Node> getNodes()
	{
		return nodes;
	}

	/**
	 * @return all edges
	 */
	public LinkedList<Edge> getEdges()
	{
		return edges;
	}

	/**
	 * Add a new edge to this graph. It will also add the associated nodes to
	 * the graph.
	 * 
	 * @param edge
	 */
	public void addEdge(Edge edge)
	{
		/* adding the edge to the graph */
		edges.add(edge);
		/* adding the nodes of the edge to graph */
		if (!nodes.contains(edge.getLeft()))
		{
			nodes.add(edge.getLeft());
		}
		if (!nodes.contains(edge.getRight()))
		{
			nodes.add(edge.getRight());
		}
	}

	/**
	 * Find the node that has an action associated with the provided name
	 * 
	 * @param name
	 * @return
	 */
	public Node findNodeByName(String name)
	{
		for (Node current : getNodes())
		{
			/* the root element is only a string, we skip it */
			if (current.getAction() == null)
			{
				continue;
			}

			if (((Action) current.getAction()).getName().equals(name))
			{
				return current;
			}
		}
		return null;
	}

}
