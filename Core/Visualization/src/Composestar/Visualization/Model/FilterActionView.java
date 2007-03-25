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

import org.jgraph.graph.DefaultEdge;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.BaseGraphCell;
import Composestar.Visualization.Model.Cells.FlowChart.DecisionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.ExitFlowVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MCBAnnotationVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MethodCallVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MethodExecutionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.SIMethodExecutionVertex;

/**
 * @author Michiel Hendriks
 */
public class FilterActionView extends CpsView
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.FilterActionView");

	private static final long serialVersionUID = -8843085075899150582L;

	public FilterActionView(CompileHistory data, Concern focusConcern, String selector)
	{
		super();

		FireModel fireModel = new FireModel(focusConcern);
		// TODO:
		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, selector);
		logger.debug("ExecutionModel = " + execModel);

		BaseGraphCell cell;
		cell = new DecisionVertex("DecisionVertex");
		cell.getPort();
		layout.insert(cell);
		cell = new ExitFlowVertex();
		cell.getPort();
		layout.insert(cell);
		cell = new MCBAnnotationVertex("MCBAnnotationVertex");
		cell.getPort();
		layout.insert(cell);
		cell = new MethodCallVertex("MethodCallVertex");
		cell.getPort();
		layout.insert(cell);
		cell = new MethodExecutionVertex("MethodExecutionVertex");
		cell.getPort();
		layout.insert(cell);
		cell = new SIMethodExecutionVertex("SIMethodExecutionVertex");
		cell.getPort();
		layout.insert(cell);
		layout.insert(new DefaultEdge());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.Model.CpsView#getName()
	 */
	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
