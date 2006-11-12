/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER.Graph.SpecialNodes;

import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.Node;

/**
 * Special node when dispatched from the Error filter go to. This is only one
 * ExceptionNode in the whole graph.
 * 
 * @author Michiel Hendriks
 */
public class ExceptionNode extends Node
{

	public ExceptionNode(Graph inGraph)
	{
		super(inGraph, "Exception");
	}
}
