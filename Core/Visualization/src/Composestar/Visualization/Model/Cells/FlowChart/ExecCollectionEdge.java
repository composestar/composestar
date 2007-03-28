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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

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

	public ExecCollectionEdge()
	{
		this(new LinkedList<ExecutionTransition>(), null);
	}

	public ExecCollectionEdge(List<ExecutionTransition> inExecList)
	{
		this(inExecList, null);
	}

	public ExecCollectionEdge(List<ExecutionTransition> inExecList, Object label)
	{
		super(label);
		execList = inExecList;
		setDefaults();
	}

	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setEndFill(map, true);
		GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
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
	 * Set the edge type.
	 * 
	 * @param newType
	 */
	public void setEdgeType(EdgeType newType)
	{
		logger.info("Setting edge type to " + newType.toString());
		edgeType = newType;
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
}
