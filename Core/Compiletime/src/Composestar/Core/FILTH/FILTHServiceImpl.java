/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Edge;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.OrderTraverser;
import Composestar.Core.FILTH.Core.Rule;
import Composestar.Core.FILTH.Core.SoftPreRule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Logging.CPSLogger;

/**
 * The actual implementation of FILTH
 */
public class FILTHServiceImpl
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(FILTH.MODULE_NAME);

	protected FilterModuleReference defaultDispatch;

	/**
	 * The filter module ordering specification
	 */
	protected Map<String, SyntacticOrderingConstraint> orderSpec;

	protected FILTHServiceImpl(CommonResources cr, FilterModuleReference deffmr) throws ConfigurationException
	{
		defaultDispatch = deffmr;
		orderSpec = cr.get(SyntacticOrderingConstraint.FILTER_ORDERING_SPEC);
	}

	/**
	 * Get the selected filter module order for a given concern
	 * 
	 * @param c
	 * @return
	 */
	public List<FilterModuleSuperImposition> getOrder(Concern c)
	{
		FilterModuleOrder fo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fo == null)
		{
			return Collections.emptyList();
		}

		// getMultipleOrder(c);
		// TODO: calling getMultipleOrder(c) generates lots of exceptions for
		// CONE-IS (filenotfound & nullpointer)

		return fo.filterModuleSIList();
	}

	/**
	 * Calculate a list of possible filter module orderings for the given
	 * concern.
	 * 
	 * @param c
	 * @return
	 */
	public List<List<FilterModuleSuperImposition>> getMultipleOrder(Concern c)
	{
		// will contain all orders
		LinkedList<List<FilterModuleSuperImposition>> forders = new LinkedList<List<FilterModuleSuperImposition>>();

		Graph g = new Graph();

		processModules(c, g);
		processOrderingSpecifications(g);

		OrderTraverser ot = new OrderTraverser();
		LinkedList<List<Node>> orders = ot.multiTraverse(g);
		for (List<Node> ord : orders)
		{
			List<FilterModuleSuperImposition> anOrder = new LinkedList<FilterModuleSuperImposition>();
			for (Node node : ord)
			{
				if (node.getAction() != null)
				{
					FilterModuleSuperImposition fmsi = node.getAction().getFilterModuleSuperImposition();
					if (fmsi != null)
					{
						anOrder.add(fmsi);
					}
				}
			}
			anOrder.add(new FilterModuleSuperImposition(defaultDispatch));
			forders.add(anOrder);
		}

		/* DEBUG info end */

		/** * attaching all orders to the concern in DataStore */
		c.addDynObject(FilterModuleOrder.ALL_ORDERS_KEY, forders);

		/*
		 * attaching the first order with different key to be dumped in the
		 * repository the list is encapsulated in FilterModuleOrder for the XML
		 * generator
		 */
		FilterModuleOrder fmorder = new FilterModuleOrder(forders.getFirst());
		c.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmorder);

		if (forders.size() > 1)
		{
			logger.warn(String.format("Multiple (%d) Filter Module orderings possible for concern %s", forders.size(),
					c.getQualifiedName()));
		}

		return forders; // arrange this according to the output required!!
	}

	/**
	 * Create pre_soft constraints between the root node and all filter modules
	 * superimposed on this concern.
	 * 
	 * @param c
	 * @param g
	 * @return
	 */
	private void processModules(Concern c, Graph g)
	{
		/* get the superimposition from the repository */
		SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
		/* get the firt altnernative */
		List<FilterModSIinfo> msalts = sinfo.getFilterModSIAlts();

		/* get the vector of the superimposed filtermodules */
		FilterModSIinfo fmsi = msalts.get(0);

		/* add the name of each filtermodule into the graph */
		for (FilterModuleSuperImposition fms : (List<FilterModuleSuperImposition>) fmsi.getAll())
		{
			Action a = new Action(fms, Boolean.TRUE, true);
			g.addEdge(new Edge("pre_soft", g.getRoot(), new Node(a)));
		}
	}

	/**
	 * Process the filter module ordering constraint specification as provided
	 * from external sources (cps files).
	 * 
	 * @param g
	 */
	private void processOrderingSpecifications(Graph g)
	{
		if (orderSpec == null)
		{
			return;
		}
		logger.info("FilterModule ordering constraints: " + orderSpec);

		String left;
		for (SyntacticOrderingConstraint soc : orderSpec.values())
		{
			left = soc.getLeft();
			Iterator<String> socit = soc.getRightFilterModules();
			while (socit.hasNext())
			{
				String right = socit.next();
				processRule(left, right, g);
			}
		}
	}

	/**
	 * Process a constraint rule
	 * 
	 * @param left
	 * @param right
	 * @param graph
	 */
	private void processRule(String left, String right, Graph graph)
	{
		Action l, r;
		Node nl, nr;

		nl = graph.findNodeByName(left);

		if (nl != null)
		{
			l = (Action) nl.getAction();
		}
		else
		{
			/*
			 * l=new Action(_left,new Boolean(true),true);
			 * Action.insert(l,_graph); System.out.println("Action "+l+"
			 * added");
			 */
			l = null;
		}

		nr = graph.findNodeByName(right);
		if (nr != null)
		{
			r = (Action) nr.getAction();
		}
		else
		{
			/*
			 * r=new Action(_right,new Boolean(true),true);
			 * Action.insert(r,_graph); System.out.println("Action "+r+"
			 * added");
			 */
			r = null;
		}

		/* we add a rule only if both arguments are active */
		if (l != null && r != null)
		{
			Rule rule = new SoftPreRule(l, r);
			rule.insert(graph);
		}
	}
}
