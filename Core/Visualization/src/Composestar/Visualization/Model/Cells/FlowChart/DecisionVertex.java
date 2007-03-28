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

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;

/**
 * Vertex used for branches. The only actual branch occures in case of
 * conditions with a variable.
 * 
 * @author Michiel Hendriks
 */
public class DecisionVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = -7492348198269898716L;

	public DecisionVertex(ConditionExpression condition)
	{
		super(condition);
	}

	public ConditionExpression getCondition()
	{
		return (ConditionExpression) getUserObject();
	}

	public String toString()
	{
		return getCondition().asSourceCode();
	}
}
