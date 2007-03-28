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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.BaseGraphCell;
import Composestar.Visualization.Model.Cells.FlowChart.DecisionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.ExecCollectionEdge;
import Composestar.Visualization.Model.Cells.FlowChart.ExitFlowVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MethodCallVertex;
import Composestar.Visualization.Model.Cells.FlowChart.MethodExecutionVertex;
import Composestar.Visualization.Model.Cells.FlowChart.SwimlaneVertex;
import Composestar.Visualization.Model.Cells.FlowChart.ExecCollectionEdge.EdgeType;

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

		// addTestCells();

		FireModel fireModel = new FireModel(focusConcern);
		// TODO: IF/OF
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
		// cell = new DecisionVertex(new True());
		// cell.getPort();
		// layout.insert(cell);
		// cell = new ExitFlowVertex();
		// cell.getPort();
		// layout.insert(cell);
		// cell = new MCBAnnotationVertex("MCBAnnotationVertex");
		// cell.getPort();
		// layout.insert(cell);
		// cell = new MethodCallVertex("MethodCallVertex");
		// cell.getPort();
		// layout.insert(cell);
		// cell = new MethodExecutionVertex("MethodExecutionVertex");
		// cell.getPort();
		// layout.insert(cell);
		// cell = new SIMethodExecutionVertex("SIMethodExecutionVertex");
		// cell.getPort();
		// layout.insert(cell);
		cell = new SwimlaneVertex("SwimlaneVertex");
		cell.getPort();
		layout.insert(cell);
		layout.insert(new DefaultEdge());
	}

	protected void createFlowChart(ExecutionState state)
	{
		BaseGraphCell cell = new MethodCallVertex(selector);
		layout.insert(cell);
		ExecCollectionEdge edge = new ExecCollectionEdge();
		edge.setSource(cell.getPort());
		createFlowChart(state, cell, edge, new ArrayList<ExecutionState>());
	}

	protected void createFlowChart(ExecutionState state, BaseGraphCell sourceCell, ExecCollectionEdge edge,
			List<ExecutionState> returnActions)
	{
		while (state != null)
		{
			FlowNode flowNode = state.getFlowNode();

			StringBuffer sb = new StringBuffer();
			Iterator it = flowNode.getNames();
			while (it.hasNext())
			{
				if (sb.length() > 0)
				{
					sb.append(", ");
				}
				sb.append(it.next());
			}
			logger.debug("Visiting state with names: " + sb.toString());

			boolean loop = true;
			BaseGraphCell _cell = stateVertices.get(state);
			if (_cell == null)
			{
				if (flowNode.containsName(FlowNode.CONDITION_EXPRESSION_NODE)
						&& !flowNode.containsName(FlowNode.TRUE_NODE))
				{
					logger.info("Adding decision node");
					ConditionExpression ce = (ConditionExpression) flowNode.getRepositoryLink();
					_cell = new DecisionVertex(ce);
				}
				else if (flowNode.containsName(FlowNode.STOP_NODE))
				{
					logger.info("Adding exit node");
					_cell = new ExitFlowVertex();
				}
				else if (flowNode.containsName(FlowNode.RETURN_NODE))
				{
					logger.info("Adding method execution call");
					Message msg = state.getSubstitutionMessage();
					_cell = new MethodExecutionVertex(msg);
					// TODO:
					// if (target == inner)
					// ..use MEV
					// else
					// ..use SIMEV
					// if (target == inner || *)
					// ..in this swimlane
					// else
					// ..fetch result swimlane
					// add outedge to "return" port
					// process return actions

				}
				else if (flowNode.containsName(FlowNode.FILTER_ACTION_NODE)
						&& !flowNode.containsName(FlowNode.CONTINUE_ACTION_NODE))
				{

					if (flowNode.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE)
							|| flowNode.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
					{
						logger.info("Appending return action");
						returnActions.add(state);
					}
					else
					{
						logger.info("Setting filter action label");
						FilterAction faction = getFilterAction(flowNode);
					}
				}
				else if (flowNode.containsName(FlowNode.SUBSTITUTED_MESSAGE_ACTION_NODE))
				{
					logger.info("Adding MCB Annotation");
					// TODO:
					// set MCB to edge
					// convert edge
				}
				else if (flowNode.containsName(FlowNode.ANY_MESSAGE_ACTION_NODE))
				{
					logger.info("Adding Any MCB Annotation");
					// TODO:
					// set MCB to edge
					// convert edge
				}
				if (_cell != null)
				{
					layout.insert(_cell);
					stateVertices.put(state, _cell);
					loop = false;
				}
			}

			// if _cell was set link the edge to it
			if (_cell != null)
			{
				edge.setTarget(_cell.getPort());
				layout.insert(edge);
				if (loop)
				{
					// already visited this node, ignore the rest
					logger.info("Encountered a loop");
					return;
				}
				edge = new ExecCollectionEdge();
				edge.setSource(_cell.getPort());
				sourceCell = _cell;
			}

			// traverse execution transitions
			Iterator transitions = state.getOutTransitions();
			List<ExecutionTransition> outTrans = null;
			int cnt = 0;
			while (transitions.hasNext())
			{
				ExecutionTransition trans = (ExecutionTransition) transitions.next();
				if (cnt == 0)
				{
					edge.addTransition(trans);
					state = trans.getEndState();
					processTransition(sourceCell, edge, trans);
				}
				else
				{
					if (outTrans == null)
					{
						outTrans = new LinkedList<ExecutionTransition>();
					}
					outTrans.add(trans);
				}
				cnt++;
			}
			if (outTrans != null)
			{
				for (ExecutionTransition trans : outTrans)
				{
					// create a new edge with the same source
					// copy all but last, because that's the first exec state
					List<ExecutionTransition> execLst = edge.getTransitions();
					ExecCollectionEdge newEdge = new ExecCollectionEdge(new LinkedList<ExecutionTransition>(execLst
							.subList(0, execLst.size() - 1)));

					newEdge.setSource(edge.getSource());
					newEdge.addTransition(trans);

					processTransition(sourceCell, newEdge, trans);
					createFlowChart(trans.getEndState(), sourceCell, newEdge, new ArrayList<ExecutionState>(
							returnActions));
				}
			}

			if (cnt == 0)
			{
				return;
			}
		}
	}

	/**
	 * Perform some processing on the Execution transition. Should be used to
	 * extract labels of the transision.
	 * 
	 * @param source
	 * @param edge
	 * @param trans
	 */
	protected void processTransition(BaseGraphCell source, ExecCollectionEdge edge, ExecutionTransition trans)
	{
		if (source instanceof DecisionVertex)
		{
			if (trans.getLabel().equals(ExecutionTransition.CONDITION_EXPRESSION_TRUE))
			{
				edge.setEdgeType(EdgeType.ACCEPT);
			}
			else if (trans.getLabel().equals(ExecutionTransition.CONDITION_EXPRESSION_FALSE))
			{
				edge.setEdgeType(EdgeType.REJECT);
			}
		}
	}

	/**
	 * Returns the FilterAction instance accociated with the FlowNode
	 * 
	 * @param flowNode
	 * @return
	 */
	protected FilterAction getFilterAction(FlowNode flowNode)
	{
		if (!(flowNode.getRepositoryLink() instanceof Filter))
		{
			return null;
		}
		Filter filter = (Filter) flowNode.getRepositoryLink();
		if (flowNode.containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
		{
			return filter.getFilterType().getAcceptCallAction();
		}
		else if (flowNode.containsName(FlowNode.REJECT_CALL_ACTION_NODE))
		{
			return filter.getFilterType().getRejectCallAction();
		}
		else if (flowNode.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE))
		{
			return filter.getFilterType().getAcceptReturnAction();
		}
		else if (flowNode.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
		{
			return filter.getFilterType().getRejectReturnAction();
		}
		return null;
	}
}
