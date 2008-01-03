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
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.OrderTraverser;
import Composestar.Core.FILTH.Core.Rule;
import Composestar.Core.FILTH.Core.SoftPreRule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;

public class FILTHServiceImpl extends FILTHService
{
	protected FilterModuleReference defaultDispatch;

	protected Map<String, SyntacticOrderingConstraint> orderSpec;

	protected FILTHServiceImpl(CommonResources cr, FilterModuleReference deffmr) throws ConfigurationException
	{
		defaultDispatch = deffmr;
		orderSpec = cr.get(FILTH.FILTER_ORDERING_SPEC);
	}

	@Override
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

	@Override
	public List<List<FilterModuleSuperImposition>> getMultipleOrder(Concern c)
	{
		LinkedList<List<FilterModuleSuperImposition>> forders = new LinkedList<List<FilterModuleSuperImposition>>();

		LinkedList<FilterModuleSuperImposition> modulrefs;

		Graph g = new Graph();
		g.setRoot(new Node("root"));

		modulrefs = processModules(c, g);

		processOrderingSpecifications(g);

		OrderTraverser ot = new OrderTraverser();
		LinkedList<List<Node>> orders = ot.multiTraverse(g);
		for (List<Node> ord : orders)
		{
			Iterator<Node> i = ord.iterator();
			/* skip the root */
			i.next();
			LinkedList<FilterModuleSuperImposition> anOrder = new LinkedList<FilterModuleSuperImposition>();
			while (i.hasNext())
			{
				Action a = (Action) i.next().getElement();

				for (FilterModuleSuperImposition fmsi : modulrefs)
				{
					// System.out.println("FILTH
					// ordering>>>"+a+"::"+fr.getName() );
					if (a.getName().equals(fmsi.getFilterModule().getQualifiedName()))
					{
						anOrder.addLast(fmsi);
						break;
					}

				}
				// System.out.println(count++);
			}
			anOrder.addLast(new FilterModuleSuperImposition(defaultDispatch));
			forders.addLast(anOrder);
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

		if (forders.size() > 2)
		{
			logger.warn("Multiple Filter Module orderings possible for concern " + c.getQualifiedName());
		}

		return forders; // arrange this according to the output required!!
	}

	private LinkedList<FilterModuleSuperImposition> processModules(Concern c, Graph g)
	{
		LinkedList<FilterModuleSuperImposition> modulerefs = new LinkedList<FilterModuleSuperImposition>();

		/* get the superimposition from the repository */
		SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
		/* get the firt altnernative */
		Vector<FilterModSIinfo> msalts = sinfo.getFilterModSIAlts();

		/* get the vector of the superimposed filtermodules */
		FilterModSIinfo fmsi = msalts.get(0);

		/* add the name of each filtermodule into the graph */
		for (FilterModuleSuperImposition fms : (List<FilterModuleSuperImposition>) fmsi.getAll())
		{
			FilterModuleReference fr = fms.getFilterModule();
			Action a = new Action(fr.getQualifiedName(), Boolean.TRUE, true);
			Action.insert(a, g);
			modulerefs.add(fms);
		}
		return modulerefs;
	}

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

	private void processRule(String left, String right, Graph graph)
	{
		Action l, r;
		Node nl, nr;

		nl = Action.lookupByName(left, graph);

		if (nl != null)
		{
			l = (Action) nl.getElement();
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

		nr = Action.lookupByName(right, graph);
		if (nr != null)
		{
			r = (Action) nr.getElement();
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
