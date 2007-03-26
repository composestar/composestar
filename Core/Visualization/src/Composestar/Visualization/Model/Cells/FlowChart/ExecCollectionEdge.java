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

import java.util.List;

import org.jgraph.graph.DefaultEdge;

import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * An edge in the FilterActionView. It combines the FIRE2 execution transitions
 * into a single edge.
 * 
 * @author Michiel Hendriks
 */
public class ExecCollectionEdge extends DefaultEdge
{
	private static final long serialVersionUID = 5311607259538183653L;

	/**
	 * The list of FIRE2 Execution Trantions this edge combines
	 */
	protected List<ExecutionTransition> execList;

	public ExecCollectionEdge(List<ExecutionTransition> inExecList)
	{
		this(inExecList, null);
	}

	public ExecCollectionEdge(List<ExecutionTransition> inExecList, Object label)
	{
		super(label);
		execList = inExecList;
	}
}
