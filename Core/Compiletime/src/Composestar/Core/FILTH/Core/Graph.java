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
	private Node root;

	private LinkedList<Node> nodes = new LinkedList<Node>();

	private LinkedList<Edge> edges = new LinkedList<Edge>();

	public void setRoot(Node inroot)
	{
		root = inroot;
		nodes.add(root);
	}

	public Node getRoot()
	{
		return root;
	}

	public LinkedList<Node> getNodes()
	{
		return nodes;
	}

	public LinkedList<Edge> getEdges()
	{
		return edges;
	}

	// public void addNode(Node node){ nodes.add(node);}
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

}
