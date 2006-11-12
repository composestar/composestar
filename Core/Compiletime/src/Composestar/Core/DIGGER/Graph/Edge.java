/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph;

/**
 * Base class for the edges in the graph
 * 
 * @author Michiel Hendriks
 */
public abstract class Edge
{
	protected Node dst;

	public Edge(Node inDestination)
	{
		dst = inDestination;
	}

	public Node getDestination()
	{
		return dst;
	}
}
