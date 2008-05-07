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

/**
 * A directed edge in the constraint graph
 * 
 * @author nagyist
 */
public class Edge
{
	public Node left, right;

	/**
	 * The label of the edge. This is the string identifier of the applied rule.
	 */
	public String label;

	public Edge(String inlabel, Node inleft, Node inright)
	{
		label = inlabel;
		left = inleft;
		right = inright;
		left.addOutgoingEdge(this);
		right.addIncomingEdge(this);
	}

	/**
	 * @return the origin
	 */
	public Node getLeft()
	{
		return left;
	}

	/**
	 * @return the destination
	 */
	public Node getRight()
	{
		return right;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
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
		sb.append(label);
		sb.append("( ");
		sb.append(left.toString());
		sb.append(", ");
		sb.append(right.toString());
		sb.append(" )");
		return sb.toString();
	}
}
