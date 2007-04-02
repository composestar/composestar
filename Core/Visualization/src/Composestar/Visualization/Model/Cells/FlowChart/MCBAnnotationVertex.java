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
 * the vertex for a Message Change Behavior annotation. Will be used when the
 * MCB is Substituted or Any. This vertex will be attached to an execution
 * trasition.
 * 
 * @author Michiel Hendriks
 */
public class MCBAnnotationVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = 7876676486392040325L;

	public MCBAnnotationVertex(Object userObject)
	{
		super(userObject);
	}
	
	@Override
	protected void setDefaults()
	{
		super.setDefaults();
		GraphConstants.setBackground(getAttributes(), new Color(0xFCFFEE));
	}
}
