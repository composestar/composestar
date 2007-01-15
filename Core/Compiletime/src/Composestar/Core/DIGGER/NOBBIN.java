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

import java.io.File;
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
import Composestar.Core.DIGGER.Graph.SpecialNodes.ExceptionNode;
import Composestar.Core.DIGGER.Graph.SpecialNodes.InnerNode;
import Composestar.Core.DIGGER.Walker.ExceptionMessage;
import Composestar.Core.DIGGER.Walker.Message;
import Composestar.Core.DIGGER.Walker.MessageGenerator;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;
import Composestar.Utils.Logging.CPSLogger;

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

	protected boolean exportMessages = true;

	protected MessageGenerator gen = new MessageGenerator();

	protected NobbinExporter exporter;

	public NOBBIN(Graph inGraph)
	{
		graph = inGraph;
		maxDepth = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "maxdepth", maxDepth);
		exportMessages = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "exportMessages",
				exportMessages);
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
		if (exportMessages)
		{
			exporter = new NobbinXmlExporter();
		}
		Iterator concerns = graph.getConcernNodes();
		while (concerns.hasNext())
		{
			AbstractConcernNode concernNode = (AbstractConcernNode) concerns.next();
			if (concernNode instanceof ConcernNode)
			{
				walk((ConcernNode) concernNode);
			}
		}
		if (exportMessages)
		{
			exporter.store(Configuration.instance().getPathSettings().getPath("Base") + File.separator
					+ Configuration.instance().getPathSettings().getPath("Analyses", "analyses") + File.separator
					+ "NobbinResults");
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

		List messages = gen.create(concernNode);
		Iterator it = messages.iterator();
        for (Object message : messages) {
            Message inMsg = (Message) message;
            List outMsgs = walk(concernNode.getInputFilters(), inMsg, new ArrayList());
            if (exporter != null) {
                if ((outMsgs.size() != 1) || (!outMsgs.get(0).equals(inMsg))) {
                    exporter.addResult(inMsg, outMsgs);
                }
            }
            if (outMsgs.size() > 0) {
                if (Debug.willLog(Debug.MODE_DEBUG)) {
                    logger.debug("[RESULT] " + inMsg + " :");
                    Iterator msgit = outMsgs.iterator();
                    for (Object outMsg : outMsgs) {
                        logger.debug("[RESULT] + " + outMsg);
                    }
                }
            }
        }
    }

	/**
	 * Walk the the graph from a given node. This will actually more or less
	 * simulate the filter execution. The result is a list of Messages that
	 * might be the result. The entries of the result list will be in order, the
	 * first entry will be the first message that might be the result. The list
	 * contains multiple entries in case Messages depend on variable conditions.
	 * In case of multiple messages the entry will be another list???
	 * 
	 * @param node
	 * @param msg
	 * @param trace
	 * @return
	 * @throws ModuleException
	 */
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
				logger.debug("[WALKING] " + node + " (label:" + node.getLabel() + ") at depth " + trace.size());
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
						// will never be executed
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
						if (Debug.willLog(Debug.MODE_DEBUG))
						{
							logger.debug("Branch");
						}
						Message newMsg = gen.cloneMessage(msg);
						newMsg.setCertainty(expr);
						List newTrace = new ArrayList(trace);
						result.addAll(walk(e.getDestination(), newMsg, newTrace));
						continue;
					}
					else
					{
						msg.setCertainty(expr);
						node = e.getDestination();
						break;
					}
				}
				else if (e instanceof SubstitutionEdge)
				{
					trace.add(msg);
					Message newMsg = gen.xform(msg, (SubstitutionEdge) e);
					msg.addResult(newMsg);
					logger.debug("[SUBST]" + msg + " => " + newMsg);

					newMsg.setRE(origNode.getRepositoryEntity());
					Node snode = e.getDestination();

					// only check for recursion when the destination is a
					// concern
					if (snode instanceof AbstractConcernNode)
					{
						if (trace.contains(newMsg))
						{
							reportRecursion(newMsg, trace);
							return result;
						}
					}
					// TODO: fork? because of possible inline append\prepend?
					msg = newMsg;
					node = snode;
					break;

					// List newTrace = new ArrayList(trace);
					// result.addAll(walk(snode, newMsg, newTrace));
					// continue;
				}
				else if (e instanceof LambdaEdge)
				{
					// always traverse LambdaEdges
					// a node should only have a single lambda edge and it
					// should also be the last edge in the list.
					node = e.getDestination();
					if (edges.hasNext())
					{
						throw new ModuleException("LambdaEdge " + e + " is not the last edge", MODULE_NAME);
					}
					if (node instanceof ExceptionNode)
					{
						msg = ExceptionMessage.getExceptionMessage();
					}
					else if (node instanceof InnerNode)
					{
						// signal message to be final
					}
					break;
				}
			}
		}
		if (msg != null)
		{
			result.add(msg);
		}
		return result;
	}

	/**
	 * Will be called in case of a recursive dispatch
	 * 
	 * @param msg
	 * @param trace
	 */
	protected void reportRecursion(Message msg, List trace)
	{
		msg.setRecursive(true);
		StringBuffer sb = new StringBuffer();
		int certenty = msg.getCertainty();

		// list of repositoryentries that caused the recursive behavior
		List res = new ArrayList();
		// start of the recursion
		int idx = trace.indexOf(msg);
		Iterator it = trace.iterator();
		while (idx > 0) // skip to start of recursion
		{
			--idx;
			it.next();
		}
		// construct list of affecting repositoryentries and create a "friendly"
		// message with trace
		while (it.hasNext())
		{
			if (sb.length() > 0)
			{
				sb.append(" -> ");
			}
			Message m = (Message) it.next();
			certenty += m.getCertainty();
			m.setRecursive(true);
			if (m.getRE() != null)
			{
				res.add(m.getRE());
			}

			sb.append(m.getConcern().getName());
			sb.append(".");
			sb.append(m.getSelector());
		}

		if (msg.getRE() != null)
		{
			res.add(msg.getRE());
		}

		sb.append(" -> ");
		sb.append(msg.getConcern().getName());
		sb.append(".");
		sb.append(msg.getSelector());

		if (certenty == ConditionExpression.RESULT_TRUE)
		{
			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Infinite recursive filter definition: " + sb.toString(), msg
					.getRE());
		}
		else
		{
			Debug.out(Debug.MODE_WARNING, MODULE_NAME, "Possibly infitite recursive filter definition (depended on ~"
					+ certenty + " conditionals): " + sb.toString(), msg.getRE());
		}
	}
}
