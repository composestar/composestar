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
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.FilterChainNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.Filters.FilterNodeFactory;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * DIGGER - DIspatch Graph GEneratoR
 * 
 * @author Michiel Hendriks
 */
public class DIGGER implements CTCommonModule
{
	public static final String MODULE_NAME = "DIGGER";

	/**
	 * The key for the dispatch graph in repository
	 */
	public static final String DISPATCH_GRAPH_KEY = "DispatchGraph";

	/**
	 * The key used to store the concern nodes in the concerns
	 */
	public static final String CONCERN_NODE_KEY = "DiggerNode";

	protected Graph graph;

	public void run(CommonResources resources) throws ModuleException
	{
		// if (incremental) digIncremental(); // how? I've got no idea
		// else
		dig();

		// export to XML
		if (Configuration.instance().getModuleProperty(MODULE_NAME, "exportToXML", true))
		{
			new XmlExporter(graph, new File(Configuration.instance().getPathSettings().getPath("Base") + File.separator
					+ Configuration.instance().getPathSettings().getPath("Analysis", "analyses") + File.separator
					+ "DispatchGraph.xml"));
		}

		NOBBIN nobbin = new NOBBIN(graph);
		nobbin.walk();
	}

	/**
	 * Dig through the repository and process all concerns with a
	 * FilterModuleOrder object
	 * 
	 * @throws ModuleException
	 */
	protected void dig() throws ModuleException
	{
		graph = new Graph();
		DataStore.instance().addObject(DISPATCH_GRAPH_KEY, graph);

		Iterator concerns = DataStore.instance().getAllInstancesOf(Concern.class);
		while (concerns.hasNext())
		{
			Concern concern = (Concern) concerns.next();
			FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
			if (fmOrder != null)
			{
				Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Generating dispatch graph for: "
						+ concern.getQualifiedName());

				ConcernNode concernNode = (ConcernNode) graph.getConcernNode(concern);
				if (concernNode == null)
				{
					throw new ModuleException("Failed to get proper ConcernNode for " + concern.getQualifiedName(),
							MODULE_NAME);
				}

				// TODO: move to ConcernNode?
				Iterator filterModules = fmOrder.orderAsList().iterator();
				while (filterModules.hasNext())
				{
					FilterModule fm = (FilterModule) DataStore.instance().getObjectByID((String) filterModules.next());
					Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing FilterModule: " + fm.getQualifiedName());
					digFilterChain(fm.getInputFilterIterator(), concernNode.getInputFilters());
					digFilterChain(fm.getOutputFilterIterator(), concernNode.getOutputFilters());
				}
			}
		}
	}

	/**
	 * Dig through a filterchain; this is either an input- or outputfilter list
	 * 
	 * @param filterChain
	 * @param chainNode
	 * @throws ModuleException
	 */
	protected void digFilterChain(Iterator filterChain, FilterChainNode chainNode) throws ModuleException
	{
		// TODO: move to FilterChainNode?
		while (filterChain.hasNext())
		{
			Filter filter = (Filter) filterChain.next();
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Processing Filter: " + filter.getQualifiedName());
			FilterNode filterNode = FilterNodeFactory.createFilterNode(graph, filter, chainNode.getDirection());
			chainNode.appendFilter(filterNode);
			filterNode.processElements();
			// digFilter(filter, filterNode);
		}
	}
}
