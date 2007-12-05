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

public class Node
{
	private LinkedList<Edge> incomingEdges = new LinkedList<Edge>();

	private LinkedList<Edge> outgoingEdges = new LinkedList<Edge>();

	private Object element;

	public Node(Object inelement)
	{
		element = inelement;
	}

	public Object getElement()
	{
		return element;
	}

	public void addIncomingEdge(Edge e)
	{
		incomingEdges.add(e);
	}

	public void addOutgoingEdge(Edge e)
	{
		outgoingEdges.add(e);
	}

	public LinkedList<Edge> getIncomingEdges()
	{
		return incomingEdges;
	}

	public LinkedList<Edge> getOutgoingEdges()
	{
		return outgoingEdges;
	}
}
