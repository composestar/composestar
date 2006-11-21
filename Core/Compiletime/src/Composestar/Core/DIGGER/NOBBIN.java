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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
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
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Debug;
import Composestar.Utils.CPSLogger;

/**
 * A NOBBIN will walk the paths created by DIGGER in order to produce a set of
 * resulting messages for an incomming message. The goal is to figure out the
 * actual destination of a message through all concerns.
 * 
 * @author Michiel Hendriks
 */
public class NOBBIN
{
	public static final String MODULE_NAME = "NOBBIN";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected Graph graph;

	/**
	 * Traverse the graph up to this number of concerns
	 */
	protected int maxDepth = 5;

	MessageGenerator gen = new MessageGenerator();

	public NOBBIN(Graph inGraph)
	{
		graph = inGraph;
		maxDepth = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "maxdepth", maxDepth);
	}

	/**
	 * Walks the paths created in the graphs
	 * 
	 * @throws ModuleException
	 */
	public void walk() throws ModuleException
	{
		INCRETimer filthinit = INCRE.instance().getReporter().openProcess(DIGGER.MODULE_NAME, "Walking dispatch graph",
				INCRETimer.TYPE_NORMAL);
		Iterator concerns = graph.getConcernNodes();
		while (concerns.hasNext())
		{
			AbstractConcernNode concernNode = (AbstractConcernNode) concerns.next();
			if (concernNode instanceof ConcernNode)
			{
				walk((ConcernNode) concernNode);
			}
		}
		filthinit.stop();
	}

	/**
	 * Walks the paths of a concern as far as possible
	 * 
	 * @param concernNode
	 * @throws ModuleException
	 */
	public void walk(ConcernNode concernNode) throws ModuleException
	{
		if (Debug.willLog(Debug.MODE_DEBUG))
		{
			logger.debug("Walking concern " + concernNode.getLabel());
		}
		/*
		 * Walk through the graph from the concernNode and spawn a new Message
		 * for each FilterElement then walk the whole graph using that message.
		 * Creating new messages on the fly.
		 */

		// walk(concernNode.getOutputFilters(), null, 0);
		List messages = gen.create(concernNode);
		Iterator it = messages.iterator();
		while (it.hasNext())
		{
			Message inMsg = (Message) it.next();
			List outMsgs = walk(concernNode.getInputFilters(), inMsg, new ArrayList());
			if (outMsgs.size() > 0)
			{
				if (Debug.willLog(Debug.MODE_DEBUG))
				{
					logger.debug(inMsg + " resulted in:");
					Iterator msgit = outMsgs.iterator();
					while (msgit.hasNext())
					{
						logger.debug(": " + msgit.next());
					}
				}
			}
		}
	}

	public List walk(Node node, Message msg, List trace) throws ModuleException
	{
		List result = new ArrayList();
		if (msg.isRecursive())
		{
			// skip recursive messages
			logger.info("Message is recursive, skippping. " + msg.toString());
			return result;
		}
		while (node != null)
		{
			if (trace.size() >= maxDepth)
			{
				logger.info("Reached maximum walking at " + node + " (label:" + node.getLabel() + ") at depth "
						+ trace.size());
				break;
			}

			if (Debug.willLog(Debug.MODE_DEBUG))
			{
				logger.debug("Walking node " + node + " (label:" + node.getLabel() + ") at depth " + trace.size());
			}
			Iterator edges = node.getOutgoingEdges();
			Node origNode = node;
			node = null;
			while (edges.hasNext())
			{
				Edge e = (Edge) edges.next();
				if (e instanceof CondMatchEdge)
				{
					CondMatchEdge cme = (CondMatchEdge) e;
					int expr = cme.getExpression().simulateResult();
					if (expr == ConditionExpression.RESULT_FALSE)
					{
						// will never be run
						continue;
					}
					if (!msg.matches(cme))
					{
						// doesn't match
						continue;
					}
					if (expr > ConditionExpression.RESULT_TRUE)
					{
						// branch
						Message newMsg = gen.cloneMessage(msg);
						newMsg.setCertenty(expr);
						List newTrace = new ArrayList(trace);
						result.addAll(walk(e.getDestination(), newMsg, newTrace));
						continue;
					}
					else
					{
						msg.setCertenty(expr);
						node = e.getDestination();
						break;
					}
				}
				else if (e instanceof SubstitutionEdge)
				{
					trace.add(msg);
					msg = gen.xform(msg, (SubstitutionEdge) e);
					msg.setRE(origNode.getRepositoryEntity());
					node = e.getDestination();

					// only check for recursion when the destination is a
					// concern
					if (node instanceof AbstractConcernNode)
					{
						if (trace.contains(msg))
						{
							reportRecursion(msg, trace);
							return result;
						}
					}
					break;
				}
				else if (e instanceof LambdaEdge)
				{
					node = e.getDestination();
				}
			}
		}
		if (msg != null)
		{
			result.add(msg);
		}
		return result;
	}

	protected void reportRecursion(Message msg, List trace)
	{
		msg.setRecursive(true);
		StringBuffer sb = new StringBuffer();
		int certenty = msg.getCertenty();

		List res = new ArrayList();
		int idx = trace.indexOf(msg);
		Iterator it = trace.iterator();
		while (idx > 0)
		{
			--idx;
			it.next();
		}
		while (it.hasNext())
		{
			if (sb.length() > 0) sb.append(" -> ");
			Message m = (Message) it.next();
			certenty += m.getCertenty();
			m.setRecursive(true);
			if (m.getRE() != null) res.add(m.getRE());

			sb.append(m.getConcern().getName());
			sb.append(".");
			sb.append(m.getSelector());
		}
		
		if (msg.getRE() != null) res.add(msg.getRE());
		
		if (certenty == ConditionExpression.RESULT_TRUE)
		{
			logger.error(sb.toString() + " is infinitie recursive");
			it = res.iterator();
			while (it.hasNext())
			{
				Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Recursive filter definition", (RepositoryEntity) it.next());
			}
		}
		else
		{
			logger.warn(sb.toString() + " might be infinite recursive (depended on " + certenty + " conditionals)");
		}
	}
}
