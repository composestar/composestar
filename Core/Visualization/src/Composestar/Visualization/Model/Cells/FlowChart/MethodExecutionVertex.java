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

import java.awt.Color;

import org.jgraph.graph.GraphConstants;


/**
 * A final method execution vertex. Will be used for the execution of the actual
 * method, when no filters can change the result. For example in a dispatch to
 * inner.
 * 
 * @author Michiel Hendriks
 */
public class MethodExecutionVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = 1306224399479728431L;

	public MethodExecutionVertex(Object userObject)
	{
		super(userObject);
	}
	
	@Override
	protected void setDefaults()
	{
		super.setDefaults();
		GraphConstants.setBackground(getAttributes(), new Color(0xFFEEFD));
	}
}
