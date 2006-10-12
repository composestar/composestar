/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: InnerNode.java,v 1.1 2006/10/05 12:19:15 elmuerte Exp $
 */

package Composestar.Core.DIGGER.Graph.SpecialNodes;

import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.Node;

/**
 * The inner part of a concern. Used when a message is send to the inner from an
 * inputfilter. Every concern has an inner node.
 * 
 * @author Michiel Hendriks
 */
public class InnerNode extends Node
{
	public InnerNode(Graph inGraph)
	{
		super(inGraph, "inner");
	}
}
