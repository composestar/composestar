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


/**
 * Represents the call to a method, it's the start of the inputfilter stack. One
 * of these vertices is usually the entrance state of the graph.
 * 
 * @author Michiel Hendriks
 */
public class MethodCallVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = 4654173103966723608L;

	public MethodCallVertex(Object userObject)
	{
		super(userObject);
	}
}
