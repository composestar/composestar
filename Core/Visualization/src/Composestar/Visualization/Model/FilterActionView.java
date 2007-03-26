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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
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

	protected Concern focusConcern;

	protected String selector;

	protected String name;

	protected Map<ExecutionState, BaseGraphCell> stateVertices;

	public FilterActionView(CompileHistory data, Concern inFocusConcern, String inSelector)
	{
		super();
		focusConcern = inFocusConcern;
		selector = inSelector;
		name = focusConcern.getQualifiedName() + "." + selector;
		FireModel fireModel = new FireModel(focusConcern);
		// TODO:
		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS, selector);
		logger.debug("ExecutionModel = " + execModel);

		stateVertices = new HashMap<ExecutionState, BaseGraphCell>();
		Iterator it = execModel.getEntranceStates();
		if (it.hasNext())
		{
			createFlowChart((ExecutionState) it.next());
		}
		else
		{
			logger.error("Execution model for " + name + " has no entrace state");
		}
		if (it.hasNext())
		{
			logger.error("Execution model for " + name + " has multiple entrance states");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.Model.CpsView#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * for debugging the custom cells
	 */
	protected void addTestCells()
	{
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

	protected void createFlowChart(ExecutionState state)
	{
		BaseGraphCell cell = new MethodCallVertex(selector);
		layout.insert(cell);
		List<ExecutionTransition> execTrans = new ArrayList<ExecutionTransition>();
		createFlowChart(state, cell, execTrans);
	}

	protected void createFlowChart(ExecutionState state, BaseGraphCell sourceCell, List<ExecutionTransition> execTrans)
	{
		while (state != null)
		{
			StringBuffer sb = new StringBuffer();
			Iterator it = state.getFlowNode().getNames();
			while (it.hasNext())
			{
				if (sb.length() > 0)
				{
					sb.append(", ");
				}
				sb.append(it.next());
			}
			logger.debug("Visiting state with names: " + sb.toString());

			// debug
			boolean loop = true;
			BaseGraphCell _cell = stateVertices.get(state);
			if (_cell == null)
			{
				_cell = new MethodExecutionVertex(sb.toString());
				layout.insert(_cell);
				stateVertices.put(state, _cell);
				loop = false;
			}
			DefaultEdge _edge = new DefaultEdge();
			_edge.setSource(sourceCell.getPort());
			_edge.setTarget(_cell.getPort());
			layout.insert(_edge);
			sourceCell = _cell;
			if (loop)
			{
				// already visited this state
				return;
			}

			Iterator transitions = state.getOutTransitions();
			List<ExecutionTransition> outTrans = null;
			int cnt = 0;
			while (transitions.hasNext())
			{
				ExecutionTransition trans = (ExecutionTransition) transitions.next();
				if (cnt == 0)
				{
					state = trans.getEndState();
				}
				else
				{
					if (outTrans == null)
					{
						outTrans = new ArrayList<ExecutionTransition>();
					}
					outTrans.add(trans);
				}
				cnt++;
			}
			if (outTrans != null)
			{
				for (ExecutionTransition trans : outTrans)
				{
					List<ExecutionTransition> list = new ArrayList<ExecutionTransition>(execTrans);
					list.add(trans);
					createFlowChart(trans.getEndState(), sourceCell, list);
				}
			}

			if (cnt == 0)
			{
				return;
			}
		}
	}
}
