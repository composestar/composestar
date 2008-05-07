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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node in the constraint dependency graph. The root node of the graph does
 * not contain an action element.
 */
public class Node
{
	/**
	 * Incoming edges
	 */
	private List<Edge> incomingEdges = new ArrayList<Edge>();

	/**
	 * Outgoing edges
	 */
	private List<Edge> outgoingEdges = new ArrayList<Edge>();

	/**
	 * Action associated with this node
	 */
	private Action action;

	public Node(Action nodeAction)
	{
		action = nodeAction;
	}

	/**
	 * @return the action associated with this node, the root node does not have
	 *         an action.
	 */
	public Action getAction()
	{
		return action;
	}

	/**
	 * Add a incoming edge edge to this node
	 * 
	 * @param e
	 */
	public void addIncomingEdge(Edge e)
	{
		incomingEdges.add(e);
	}

	/**
	 * Add a new outgoing edge to this node
	 * 
	 * @param e
	 */
	public void addOutgoingEdge(Edge e)
	{
		outgoingEdges.add(e);
	}

	/**
	 * @return the incoming edges
	 */
	public List<Edge> getIncomingEdges()
	{
		return Collections.unmodifiableList(incomingEdges);
	}

	/**
	 * @return the outgoing edges
	 */
	public List<Edge> getOutgoingEdges()
	{
		return Collections.unmodifiableList(outgoingEdges);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (action instanceof Action)
		{
			sb.append("[Action] ");
		}
		if (action != null)
		{
			sb.append(action.toString());
		}
		else
		{
			sb.append("[root]");
		}
		return sb.toString();
	}

}
