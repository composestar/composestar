/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Node.java,v 1.5 2006/10/05 12:19:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for all nodes in the graph
 * 
 * @author Michiel Hendriks
 */
public abstract class Node
{
	/**
	 * Reference to the graph this node is in
	 */
	protected Graph graph;

	/**
	 * A description for the node
	 */
	protected String label;

	/**
	 * The owning node. Not all nodes are owned.
	 */
	protected Node owner;

	/**
	 * And ordered list of outgoing edges
	 */
	protected List outgoingEdges;

	public Node(Graph inGraph)
	{
		outgoingEdges = new ArrayList();
		graph = inGraph;
	}

	public Node(Graph inGraph, String inLabel)
	{
		this(inGraph);
		setLabel(inLabel);
	}

	public void setLabel(String inLabel)
	{
		label = inLabel;
	}

	public String getLabel()
	{
		return label;
	}

	public void setOwner(Node inOwner)
	{
		owner = inOwner;
	}

	public Node getOwner()
	{
		return owner;
	}

	/**
	 * Add a new outgoing edge
	 * 
	 * @param edge
	 * @return
	 */
	public boolean addOutgoingEdge(Edge edge)
	{
		return outgoingEdges.add(edge);
	}

	/**
	 * Return the outgoing edges
	 * 
	 * @return
	 */
	public Iterator getOutgoingEdges()
	{
		return outgoingEdges.iterator();
	}
}
