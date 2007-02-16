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
 * @author nagyist
 */
public class Edge
{
	public Node left, right;

	public String label;

	public Edge(String inlabel, Node inleft, Node inright)
	{
		label = inlabel;
		left = inleft;
		right = inright;
		left.addOutgoingEdge(this);
		right.addIncomingEdge(this);
	}

	public Node getLeft()
	{
		return left;
	}

	public Node getRight()
	{
		return right;
	}

	public String getLabel()
	{
		return label;
	}
}
