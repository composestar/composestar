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

import java.util.Iterator;
import java.util.List;

import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.DIGGER.Graph.Edge;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.LambdaEdge;
import Composestar.Core.DIGGER.Graph.Node;
import Composestar.Core.DIGGER.Graph.SubstitutionEdge;
import Composestar.Core.DIGGER.Walker.Message;
import Composestar.Core.DIGGER.Walker.MessageGenerator;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;

/**
 * A NOBBIN will walk the paths created by DIGGER in order to produce a set of
 * resulting messages for an incomming message. The goal is to figure out the actual
 * destination of a message through all concerns.
 * 
 * @author Michiel Hendriks
 */
public class NOBBIN
{
	protected Graph graph;

	/**
	 * Traverse the graph up to this number of concerns
	 */
	protected int maxDepth = 5;

	MessageGenerator gen = new MessageGenerator();

	public NOBBIN(Graph inGraph)
	{
		graph = inGraph;
		maxDepth = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "maxdepth", 5);
		// just a number
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
		Debug.out(Debug.MODE_DEBUG, "Walking concern " + concernNode.getLabel(), DIGGER.MODULE_NAME);
		/*
		 * Walk through the graph from the concernNode and spawn a new Message
		 * for each FilterElement then walk the whole graph using that message.
		 * Creating new messages on the fly.
		 */

		walk(concernNode.getOutputFilters(), null, 0);
		walk(concernNode.getInputFilters(), null, 0);

		// Node n = concernNode.getOutputFilters();
		// while (n != null)
		// {
		// Debug.out(Debug.MODE_DEBUG, "Walking node " + n.getLabel(),
		// DIGGER.MODULE_NAME);
		// Iterator edges = n.getOutgoingEdges();
		// n = null;
		// while (edges.hasNext())
		// {
		// Edge e = (Edge) edges.next();
		// if (e instanceof CondMatchEdge)
		// {
		// Message msg = gen.create((CondMatchEdge) e);
		// walk(e.getDestination(), msg);
		// }
		// else if (e instanceof LambdaEdge)
		// {
		// n = e.getDestination();
		// }
		// }
		// }
		//		
		// n = concernNode.getInputFilters();
		// while (n != null)
		// {
		// Debug.out(Debug.MODE_DEBUG, "Walking node " + n.getLabel(),
		// DIGGER.MODULE_NAME);
		// Iterator edges = n.getOutgoingEdges();
		// n = null;
		// while (edges.hasNext())
		// {
		// Edge e = (Edge) edges.next();
		// if (e instanceof CondMatchEdge)
		// {
		// Message msg = gen.create((CondMatchEdge) e);
		// walk(e.getDestination(), msg);
		// }
		// else if (e instanceof LambdaEdge)
		// {
		// n = e.getDestination();
		// }
		// }
		// }
	}

	public void walk(Node node, Message msg, int curDepth) throws ModuleException
	{
		while (node != null)
		{
			Debug.out(Debug.MODE_DEBUG, "Walking node " + node.getLabel(), DIGGER.MODULE_NAME);
			Iterator edges = node.getOutgoingEdges();
			node = null;
			while (edges.hasNext())
			{
				Edge e = (Edge) edges.next();
				if (e instanceof CondMatchEdge)
				{
					CondMatchEdge cme = (CondMatchEdge) e;
					if (msg != null)
					{
						if (!msg.matches(cme)) continue;

						walk(e.getDestination(), msg, curDepth);
					}
					else
					{
						List messages = gen.create(cme);

					}
				}
				else if (e instanceof SubstitutionEdge)
				{
					Message newMsg = gen.xform(msg, (SubstitutionEdge) e);
					walk(e.getDestination(), newMsg, curDepth++);
				}
				else if (e instanceof LambdaEdge)
				{
					node = e.getDestination();
				}
			}
		}
	}
}
