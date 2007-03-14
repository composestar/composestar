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
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * A concern vertex with detailed filter module information. Used by the
 * FilterViewF
 * 
 * @author Michiel Hendriks
 */
public class FilterConcernVertex extends AbstractFilterModuleConcernVertex
{
	private static final long serialVersionUID = 5214277399628413966L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.FilterConcernVertex");

	/**
	 * @param concern
	 */
	public FilterConcernVertex(Concern concern)
	{
		super(concern, MemberFlags.all());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addFmVertices(Concern concern)
	{
		FilterModuleOrder fmOrder = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fmOrder == null)
		{
			return;
		}
		int idx = 0;
		DetailedFilterModuleVertex last = null;
		for (FilterModuleSuperImposition fmsi : (List<FilterModuleSuperImposition>) fmOrder.filterModuleSIList())
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			if (InnerDispatcher.isDefaultDispatch(fm))
			{
				continue;
			}
			logger.debug("Adding DetailedFilterModuleVertex " + fm.getQualifiedName() + " for " + concern);

			DetailedFilterModuleVertex fmVertex = new DetailedFilterModuleVertex(fm);
			if (last != null)
			{
				Rectangle2D bounds = GraphConstants.getBounds(last.getAttributes());
				fmVertex.translate(0, bounds.getY() + bounds.getHeight() - 1);
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
			// Rectangle2D cvBounds =
			// GraphConstants.getBounds(classVertex.getAttributes());
			classVertex.translate(0, bounds.getY() + bounds.getHeight());
		}
	}
}
