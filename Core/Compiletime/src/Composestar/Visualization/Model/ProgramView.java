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

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.DefaultEdge.DefaultRouting;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Elements.ConcernNode;
import Composestar.Visualization.Model.Elements.PVConcernNode;

/**
 * Highest view level. Shows the program layout.
 * 
 * @author Michiel Hendriks
 */
public class ProgramView extends View
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.ProgramView");
	
	protected Map<Concern,ConcernNode> cells;
	
	public ProgramView(CompileHistory data)
	{
		cells = new HashMap<Concern,ConcernNode>();
		model = new DefaultGraphModel();
		layout = new GraphLayoutCache(model, new DefaultCellViewFactory());
		graph = new JGraph(model, layout);
		
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
			logger.debug("Adding concern: "+concern.getName());
			addConcern(concern);
		}
		
		for (ConcernNode source : cells.values())
		{
			for (ConcernNode dest : cells.values())
			{
				if (source != dest)
				{
					DefaultEdge edge = new DefaultEdge();
					edge.setSource(source.getPort());
					edge.setTarget(dest.getPort());
					GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
					GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
					layout.insert(edge);
				}
			}
		}
	}
	
	public void addConcern(Concern concern)
	{
		ConcernNode cell = new PVConcernNode(concern);
		layout.insert(cell);
		cells.put(concern, cell);
	}
}
