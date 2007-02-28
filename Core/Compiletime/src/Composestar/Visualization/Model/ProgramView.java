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

import java.util.Iterator;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Elements.PVConcernNode;

/**
 * Highest view level. Shows the program layout.
 * 
 * @author Michiel Hendriks
 */
public class ProgramView extends View
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.ProgramView");
	
	public ProgramView(CompileHistory data)
	{
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
	}
	
	public void addConcern(Concern concern)
	{
		DefaultGraphCell cell = new PVConcernNode(concern);
		layout.insert(cell);
	}
}
