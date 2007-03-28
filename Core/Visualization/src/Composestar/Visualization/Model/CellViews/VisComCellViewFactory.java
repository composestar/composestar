/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.CellViews;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphModel;

import Composestar.Visualization.Model.CellViews.FlowChart.DiamondView;
import Composestar.Visualization.Model.CellViews.FlowChart.LaneRectView;
import Composestar.Visualization.Model.CellViews.FlowChart.ParallelogramView;
import Composestar.Visualization.Model.CellViews.FlowChart.RoundedRectView;
import Composestar.Visualization.Model.CellViews.FlowChart.SwimlaneView;
import Composestar.Visualization.Model.Cells.ClassVertex;
import Composestar.Visualization.Model.Cells.DetailedFilterModuleVertex;
import Composestar.Visualization.Model.Cells.FlowChart.DecisionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MCBAnnotationVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MethodCallVertex;
import Composestar.Visualization.Model.Cells.FlowChart.SIMethodExecutionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.SwimlaneVertex;

/**
 * Creates the proper CellView instances for our custom cells
 * 
 * @author Michiel Hendriks
 */
public class VisComCellViewFactory extends DefaultCellViewFactory
{
	private static final long serialVersionUID = -6603775190011785736L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.CellViewFactory#createView(org.jgraph.graph.GraphModel,
	 *      java.lang.Object)
	 */
	@Override
	public CellView createView(GraphModel model, Object cell)
	{
		if (cell instanceof ClassVertex)
		{
			return new ClassVertexView(cell);
		}
		else if (cell instanceof DetailedFilterModuleVertex)
		{
			return new DetailedFilterModuleView(cell);
		}
		// Filter Action View cells
		else if (cell instanceof DecisionVertex)
		{
			return new DiamondView(cell);
		}
		else if (cell instanceof MCBAnnotationVertex)
		{
			return new ParallelogramView(cell);
		}
		else if (cell instanceof MethodCallVertex)
		{
			return new RoundedRectView(cell);
		}
		else if (cell instanceof SIMethodExecutionVertex)
		{
			return new LaneRectView(cell);
		}
		else if (cell instanceof SwimlaneVertex)
		{
			return new SwimlaneView(cell);
		}
		return super.createView(model, cell);
	}

}
