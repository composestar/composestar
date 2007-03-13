/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Utils.Logging.CPSLogger;

/**
 * ConcernVertex class for the ProgramView. The class view shows no class
 * members but shows the filtermodule list.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleConcernVertex extends AbstractFilterModuleConcernVertex
{
	private static final long serialVersionUID = -6066054436195352608L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.FilterModuleConcernVertex");

	public FilterModuleConcernVertex(Concern concern)
	{
		super(concern);
	}

	@Override
	public DefaultPort getPortFor(Object obj)
	{
		String key = null;
		if (obj instanceof FilterModule)
		{
			key = ((FilterModule) obj).getQualifiedName();
		}
		else if (obj != null)
		{
			key = obj.toString();
		}
		if (fmVertices.containsKey(key))
		{
			return fmVertices.get(key).getPort();
		}
		else
		{
			return getPort();
		}
	}

	/**
	 * Adds the filter module vertices to this concern vertex
	 * 
	 * @param concern
	 */
	@SuppressWarnings("unchecked")
	protected void addFmVertices(Concern concern)
	{
		FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fmOrder == null)
		{
			return;
		}
		int idx = 0;
		FilterModuleVertex last = null;
		for (FilterModuleSuperImposition fmsi : (List<FilterModuleSuperImposition>) fmOrder.filterModuleSIList())
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			if (InnerDispatcher.isDefaultDispatch(fm))
			{
				continue;
			}
			logger.debug("Adding FilterModuleVertex " + fm.getQualifiedName() + " for " + concern);

			FilterModuleVertex fmVertex = new FilterModuleVertex(fm);
			if (last != null)
			{
				Rectangle2D bounds = GraphConstants.getBounds(last.getAttributes());
				fmVertex.translate(60, bounds.getY() + bounds.getHeight() - 1);
			}
			else
			{
				fmVertex.translate(60, 0);
			}

			if (idx == 0)
			{
				logger.debug("Adding filter entry point");
				// move the inputfilter port here
				filterInputPort = new DefaultPort("Filter Entry Point");
				fmVertex.add(filterInputPort);
				filterInputPort.setParent(fmVertex);
				Point2D pt = new Point2D.Double(GraphConstants.PERMILLE / 2, 0);
				GraphConstants.setOffset(filterInputPort.getAttributes(), pt);
			}

			fmVertices.put(fm.getQualifiedName(), fmVertex);
			add(fmVertex);
			fmVertex.setParent(this);

			idx++;
			last = fmVertex;
		}
		if (last != null)
		{
			// nudge the class vertex
			Rectangle2D bounds = GraphConstants.getBounds(last.getAttributes());
			classVertex.translate(0, bounds.getY() + bounds.getHeight() - 10);
		}
	}
}
