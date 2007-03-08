/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.Port;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.CellViews.VisComCellViewFactory;
import Composestar.Visualization.Model.Cells.FilterModuleConcernVertex;
import Composestar.Visualization.Model.Cells.FilterModuleVertex;

/**
 * Highest view level. Shows the program layout.
 * 
 * @author Michiel Hendriks
 */
public class ProgramView extends View
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.ProgramView");

	protected Map<Concern, FilterModuleConcernVertex> cells;

	/**
	 * Defines the type of edge
	 * 
	 * @author elmuerte
	 */
	public enum ProgramViewEdge
	{
		/**
		 * concern usage as internal
		 */
		INTERNAL,
		/**
		 * concern usage as external
		 */
		EXTERNAL,
		/**
		 * target of a filter action
		 */
		TARGET,
	}

	public ProgramView(CompileHistory data)
	{
		super();
		cells = new HashMap<Concern, FilterModuleConcernVertex>();
		model = new DefaultGraphModel();
		layout = new GraphLayoutCache(model, new VisComCellViewFactory());
		graph = new JGraph(model, layout);
		graph.setAntiAliased(true);

		Iterator it = data.getDataStore().getAllInstancesOf(Concern.class);
		while (it.hasNext())
		{
			Concern concern = (Concern) it.next();
			if (concern.getPlatformRepresentation() == null)
			{
				// we've got no use for these things
				continue;
			}
			if (concern.getDynObject("REFERENCED") == null)
			{
				continue;
			}
			logger.debug("Adding concern: " + concern.getName());
			addConcern(concern);
		}
		for (Entry<Concern, FilterModuleConcernVertex> entry : cells.entrySet())
		{
			addEdges(entry.getKey(), entry.getValue());
		}
	}

	protected void addConcern(Concern concern)
	{
		FilterModuleConcernVertex cell = new FilterModuleConcernVertex(concern);
		layout.insert(cell);
		cells.put(concern, cell);
	}

	protected void addEdges(Concern concern, FilterModuleConcernVertex vertex)
	{
		if (!vertex.hasFilterModules())
		{
			return;
		}
		for (FilterModuleVertex fmVertex : vertex.getFmVertices())
		{
			FilterModule fm = fmVertex.getFilterModule();
			Iterator it = fm.getInternalIterator();
			while (it.hasNext())
			{
				Internal internal = (Internal) it.next();
				Concern dest = internal.getType().getRef();
				FilterModuleConcernVertex destVertex = cells.get(dest);
				addEdge(destVertex.getClassPort(), fmVertex.getPort(), ProgramViewEdge.INTERNAL);
			}

			it = fm.getExternalIterator();
			while (it.hasNext())
			{
				External external = (External) it.next();
				Concern dest = external.getType().getRef();
				FilterModuleConcernVertex destVertex = cells.get(dest);
				addEdge(destVertex.getClassPort(), fmVertex.getPort(), ProgramViewEdge.EXTERNAL);
			}
		}
	}

	protected void addEdge(Port source, Port dest, ProgramViewEdge type)
	{
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(source);
		edge.setTarget(dest);

		Map map = edge.getAttributes();
		GraphConstants.setLineColor(map, Color.BLACK);
		if ((type == ProgramViewEdge.INTERNAL) || (type == ProgramViewEdge.EXTERNAL))
		{
			GraphConstants.setLineEnd(map, GraphConstants.ARROW_DIAMOND);
			GraphConstants.setEndFill(map, type == ProgramViewEdge.INTERNAL);
		}
		else if (type == ProgramViewEdge.TARGET)
		{
			GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
		}
		//GraphConstants.setEndSize(map, 15);
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
		GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);

		layout.insert(edge);
	}
}
