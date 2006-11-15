/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;

/**
 * A NOBBIN will walk the paths created by DIGGER
 *
 * @author Michiel Hendriks
 */
public class NOBBIN
{
	protected Graph graph;
	
	/**
	 * Traverse the graph up to this number of concerns
	 */
	protected int maxDepth;
	
	protected Map signatures;
	
	public NOBBIN(Graph inGraph)
	{
		signatures = new HashMap();
		graph = inGraph;
		maxDepth = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "maxdepth", 5); // just a number
	}
	
	/**
	 * Walks the paths created in the graphs
	 * 
	 * @throws ModuleException
	 */
	public void walk() throws ModuleException
	{
		Iterator concerns = graph.getConcernNodes();
		while (concerns.hasNext())
		{
			AbstractConcernNode concernNode = (AbstractConcernNode) concerns.next();
			if (concernNode instanceof ConcernNode)
			{
				walk((ConcernNode) concernNode);
			}
		}
	}
	
	/**
	 * Walks the paths of a concern as far as possible
	 * 
	 * @param concernNode
	 * @throws ModuleException
	 */
	public void walk(ConcernNode concernNode) throws ModuleException
	{
		
	}
}
