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

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.DIGGER2.Breadcrumb;
import Composestar.Core.DIGGER2.ConcernCrumbs;
import Composestar.Core.DIGGER2.DispatchGraph;
import Composestar.Core.DIGGER2.Trail;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.Master.CompileHistory;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.AbstractFilterModuleConcernVertex;
import Composestar.Visualization.Model.Cells.FilterConcernVertex;
import Composestar.Visualization.Model.Cells.FilterModuleConcernVertex;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;
import Composestar.Visualization.Model.Routing.JGraphParallelRouter;

/**
 * The filter view. Show filter usage for a single concern.
 * 
 * @author Michiel Hendriks
 */
public class FilterView extends CpsView
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.FilterView");

	protected Map<Concern, AbstractFilterModuleConcernVertex> cells;

	protected FilterConcernVertex focusVertex;

	public FilterView(CompileHistory data, Concern focusConcern)
	{
		super();
		cells = new HashMap<Concern, AbstractFilterModuleConcernVertex>();
		focusVertex = new FilterConcernVertex(focusConcern);
		layout.insert(focusVertex);

		DispatchGraph dispatchGraph = (DispatchGraph) data.getResources().get(DispatchGraph.REPOSITORY_KEY);
		addEdges(dispatchGraph, focusConcern);
	}

	@Override
	public String getName()
	{
		return focusVertex.getConcern().getQualifiedName();
	}

	protected void addEdges(DispatchGraph dispatchGraph, Concern focusConcern)
	{
		ConcernCrumbs crumbs = dispatchGraph.getConcernCrumbs(focusConcern);
		if (crumbs != null)
		{
			Iterator<Breadcrumb> it = crumbs.getInputCrumbs();
			while (it.hasNext())
			{
				Breadcrumb crumb = it.next();
				Iterator<Trail> trailIt = crumb.getTrails();
				while (trailIt.hasNext())
				{
					Trail trail = trailIt.next();

					FilterModule fm = (FilterModule) ((ContextRepositoryEntity) trail.getRE())
							.getAncestorOfClass(FilterModule.class);
					if (fm == null)
					{
						logger.warn("Trail has an repository entity without an FilterModule parent", trail.getRE());
						continue;
					}
					if (InnerDispatcher.isDefaultDispatch(fm.getQualifiedName()))
					{
						// don't include the default dispatcher
						continue;
					}

					Concern targetC = trail.getTargetConcern();
					AbstractFilterModuleConcernVertex fmcVertex;
					if (targetC == null)
					{
						fmcVertex = focusVertex;
					}
					else
					{
						fmcVertex = cells.get(targetC);
						if (fmcVertex == null)
						{
							fmcVertex = new FilterModuleConcernVertex(targetC, MemberFlags.all());
							cells.put(targetC, fmcVertex);
							layout.insert(fmcVertex);
						}
					}
					addEdge(fmcVertex, trail);
				}
			}
		}
	}

	protected void addEdge(AbstractFilterModuleConcernVertex target, Trail trail)
	{
		FilterElement fe = (FilterElement) trail.getRE();
		Port sourcePort = focusVertex.getPortFor(fe.getParent());
		Port targetPort = target.getPortFor(trail.getResultMessage());
		Edge edge = new DefaultEdge(fe.asSourceCode());
		edge.setSource(sourcePort);
		edge.setTarget(targetPort);
		
		Map map = edge.getAttributes();
		GraphConstants.setLineColor(map, Color.BLACK);
		GraphConstants.setEndFill(map, true);
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
		GraphConstants.setRouting(map, JGraphParallelRouter.getSharedInstance());
		
		layout.insert(edge);
	}
}
