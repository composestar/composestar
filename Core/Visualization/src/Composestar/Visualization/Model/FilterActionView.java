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
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.DIGGER2.Resolver;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.FlowChart.BaseFlowChartVertex;
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

	/**
	 * Cache to lookup existing vertices for states, will be used to resolve
	 * loops.
	 */
	protected Map<ExecutionState, BaseFlowChartVertex> stateVertices;

	/**
	 * Swimlane lookup
	 */
	protected Map<Concern, SwimlaneVertex> swimlanes;

	protected SwimlaneVertex mainLane;

	protected int filterPos = FireModel.INPUT_FILTERS;

	public FilterActionView(CompileHistory data, Concern inFocusConcern, String inSelector)
	{
		super();

		focusConcern = inFocusConcern;
		selector = inSelector;

		// addTestCells();

		FireModel fireModel = new FireModel(focusConcern);
		// TODO: IF/OF
		ExecutionModel execModel = fireModel.getExecutionModel(filterPos, selector);
		logger.debug("ExecutionModel = " + execModel);

		stateVertices = new HashMap<ExecutionState, BaseFlowChartVertex>();
		swimlanes = new HashMap<Concern, SwimlaneVertex>();
		mainLane = getSwimlane(focusConcern);

		Iterator it = execModel.getEntranceStates();
		if (it.hasNext())
		{
			createFlowChart((ExecutionState) it.next());
		}
		else
		{
			logger.error("Execution model for " + getName() + " has no entrace state");
		}
		if (it.hasNext())
		{
			logger.error("Execution model for " + getName() + " has multiple entrance states");
		}

		// TODO: align swimlanes
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.Model.CpsView#getName()
	 */
	@Override
	public String getName()
	{
		return focusConcern.getQualifiedName() + "." + selector;
	}

	/**
	 * for debugging the custom cells
	 */
	protected void addTestCells()
	{
		// BaseFlowChartVertex cell;
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
		// cell = new SwimlaneVertex("SwimlaneVertex");
		// cell.getPort();
		// layout.insert(cell);
		layout.insert(new DefaultEdge());
	}

	protected void createFlowChart(ExecutionState state)
	{
		BaseFlowChartVertex cell = new MethodCallVertex(selector);
		// layout.insert(cell);
		mainLane.addVertex(cell);
		ExecCollectionEdge edge = new ExecCollectionEdge();
		edge.setSource(cell.getPort());
		createFlowChart(state, cell, edge, new ArrayList<ExecutionState>());
	}

	protected void createFlowChart(ExecutionState state, BaseFlowChartVertex sourceCell, ExecCollectionEdge edge,
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
			BaseFlowChartVertex nextCell = stateVertices.get(state);
			if (nextCell == null)
			{
				if (flowNode.containsName(FlowNode.CONDITION_EXPRESSION_NODE)
						&& !flowNode.containsName(FlowNode.TRUE_NODE))
				{
					logger.info("Adding decision node");
					ConditionExpression ce = (ConditionExpression) flowNode.getRepositoryLink();
					nextCell = new DecisionVertex(ce);
					mainLane.addVertex(nextCell);
				}
				else if (flowNode.containsName(FlowNode.STOP_NODE))
				{
					logger.info("Adding exit node");
					nextCell = new ExitFlowVertex();
					mainLane.addVertex(nextCell);
				}
				else if (flowNode.containsName(FlowNode.RETURN_NODE))
				{
					logger.info("Adding method execution call");
					Message msg = state.getSubstitutionMessage();

					Concern targetConcern = null;
					if (msg.getTarget().equals(Message.INNER_TARGET))
					{
						targetConcern = focusConcern;
						nextCell = new MethodExecutionVertex(msg);
					}
					else if (msg.getTarget().equals(Message.STAR_TARGET))
					{
						targetConcern = focusConcern;
						nextCell = new MethodExecutionVertex(msg);
					}
					else
					{
						try
						{
							targetConcern = Resolver.findTargetConcern(focusConcern, filterPos, msg.getTarget());
							nextCell = new MethodExecutionVertex(msg);
						}
						catch (ModuleException e)
						{
							logger.error(e);
						}
					}
					SwimlaneVertex lane = getSwimlane(targetConcern);
					lane.addVertex(nextCell);
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
						if (faction != null)
						{
							edge.addAction(faction);
						}
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
				if (nextCell != null)
				{
					// layout.insert(_cell);
					stateVertices.put(state, nextCell);
					loop = false;
				}
			}

			// if _cell was set link the edge to it
			if (nextCell != null)
			{
				edge.setTarget(nextCell.getPort());
				layout.insert(edge);
				if (loop)
				{
					// already visited this node, ignore the rest
					logger.info("Encountered a loop");
					return;
				}
				edge = new ExecCollectionEdge();
				edge.setSource(nextCell.getPort());
				sourceCell = nextCell;
			}
			
			if (flowNode.containsName(FlowNode.RETURN_NODE))
			{
				logger.info("Adding return edge");
				edge.setTarget(mainLane.getReturnPort());
				layout.insert(edge);
				for (ExecutionState returnAction : returnActions)
				{
					FilterAction faction = getFilterAction(returnAction.getFlowNode());
					edge.addAction(faction);
				}
				returnActions.clear();
				// beyond this point there should be no more edges
			}

			// traverse execution transitions
			Iterator transitions = state.getOutTransitions();
			ExecutionTransition firstTrans = null;
			while (transitions.hasNext())
			{
				ExecutionTransition trans = (ExecutionTransition) transitions.next();
				if (firstTrans == null)
				{
					firstTrans = trans;
				}
				else
				{
					// create a new edge with the same source
					ExecCollectionEdge newEdge = new ExecCollectionEdge(edge);
					newEdge.setSource(edge.getSource());
					newEdge.addTransition(trans);
					processTransition(sourceCell, newEdge, trans);
					createFlowChart(trans.getEndState(), sourceCell, newEdge, new ArrayList<ExecutionState>(
							returnActions));
				}
			}
			if (firstTrans != null)
			{
				edge.addTransition(firstTrans);
				state = firstTrans.getEndState();
				processTransition(sourceCell, edge, firstTrans);
			}
			else
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
	protected void processTransition(BaseFlowChartVertex source, ExecCollectionEdge edge, ExecutionTransition trans)
	{
		if ((source instanceof DecisionVertex) && (edge.getEdgeType() == EdgeType.NORMAL))
		{
			// only change edgetype when it's normal, subsequent acepts/rejects
			// are usually redundant or not bound to the previous DecisionVertex
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

	protected SwimlaneVertex getSwimlane(Concern concern)
	{
		if (!swimlanes.containsKey(concern))
		{
			SwimlaneVertex vert = new SwimlaneVertex(concern);
			swimlanes.put(concern, vert);
			layout.insert(vert);
			return vert;
		}
		return swimlanes.get(concern);
	}
}
