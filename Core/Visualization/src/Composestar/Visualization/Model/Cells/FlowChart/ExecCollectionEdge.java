/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells.FlowChart;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Utils.Logging.CPSLogger;

/**
 * An edge in the FilterActionView. It combines the FIRE2 execution transitions
 * into a single edge.
 * 
 * @author Michiel Hendriks
 */
public class ExecCollectionEdge extends DefaultEdge
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.ExecCollectionEdge");

	private static final long serialVersionUID = 5311607259538183653L;

	/**
	 * Defines the sort of edge this is. Only used in case of decision nodes
	 * when there are multiple outgoing edges.
	 * 
	 * @author Michiel Hendriks
	 */
	public enum EdgeType
	{
		NORMAL, ACCEPT, REJECT
	}

	/**
	 * The list of FIRE2 Execution Trantions this edge combines
	 */
	protected List<ExecutionTransition> execList;

	/**
	 * @see EdgeType
	 */
	protected EdgeType edgeType = EdgeType.NORMAL;

	/**
	 * List of filter actions
	 */
	protected List<FilterAction> actions;

	public ExecCollectionEdge()
	{
		this(new LinkedList<ExecutionTransition>(), new LinkedList<FilterAction>());
	}

	public ExecCollectionEdge(ExecCollectionEdge base)
	{
		this(new LinkedList<ExecutionTransition>(base.execList), new LinkedList<FilterAction>(base.actions));
	}

	public ExecCollectionEdge(List<ExecutionTransition> inExecList, List<FilterAction> inActions)
	{
		super();
		actions = inActions;
		execList = inExecList;
		setDefaults();
	}

	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setFont(map, new Font("sansserif", Font.PLAIN, 11));
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setEndFill(map, true);
		GraphConstants.setEndSize(map, 8);
		GraphConstants.setOpaque(map, true);
		// GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
	}

	/**
	 * Add a new execution transition to this edge.
	 * 
	 * @param trans
	 */
	public void addTransition(ExecutionTransition trans)
	{
		execList.add(trans);

	}

	/**
	 * Add a filter action to the node.
	 * 
	 * @param action
	 */
	public void addAction(FilterAction action)
	{
		logger.info("Adding action " + action.getName());
		actions.add(action);
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (FilterAction action : actions)
		{
			if (sb.length() > 0)
			{
				sb.append(" \n");
			}
			sb.append("<");
			sb.append(action.getName());
			sb.append(">");
		}
		return sb.toString();
	}

	/**
	 * Set the edge type.
	 * 
	 * @param newType
	 */
	public void setEdgeType(EdgeType newType)
	{
		logger.info("Setting edge type to " + newType.toString());
		edgeType = newType;
		Map map = getAttributes();
		Object[] labels = GraphConstants.getExtraLabels(map);
		if (labels == null)
		{
			labels = new Object[1];
		}
		labels[0] = edgeType;
		GraphConstants.setExtraLabels(map, labels);
		Point2D[] positions = GraphConstants.getExtraLabelPositions(map);
		if (positions == null)
		{
			positions = new Point2D[1];
			positions[0] = new Point2D.Double(GraphConstants.PERMILLE / 10, -20);
			GraphConstants.setExtraLabelPositions(map, positions);
		}
	}

	public EdgeType getEdgeType()
	{
		return edgeType;
	}

	/**
	 * Return a readonly copy of the transitions in this edge.
	 * 
	 * @return
	 */
	public List<ExecutionTransition> getTransitions()
	{
		return Collections.unmodifiableList(execList);
	}

	/**
	 * Return a readonly copy of the ections
	 * 
	 * @return
	 */
	public List<FilterAction> getActions()
	{
		return Collections.unmodifiableList(actions);
	}
}
