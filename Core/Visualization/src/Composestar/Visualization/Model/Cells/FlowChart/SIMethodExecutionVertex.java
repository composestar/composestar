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
 * Like an Method Execution Vertex but the method execution is not definitive,
 * filter behavior could have been super imposed.
 * 
 * @author Michiel Hendriks
 */
public class SIMethodExecutionVertex extends MethodExecutionVertex
{
	private static final long serialVersionUID = -4332270006016457223L;

	public SIMethodExecutionVertex(Object userObject)
	{
		super(userObject);
	}
	
	@Override
	protected void setDefaults()
	{
		super.setDefaults();
		GraphConstants.setBackground(getAttributes(), new Color(0xFCEEFF));
	}
}
