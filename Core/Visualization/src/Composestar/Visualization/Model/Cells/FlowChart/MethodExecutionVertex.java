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
import java.util.Map;

import org.jgraph.graph.GraphConstants;

import Composestar.Visualization.Model.Cells.BaseGraphCell;

/**
 * A final method execution vertex. Will be used for the execution of the actual
 * method, when no filters can change the result. For example in a dispatch to
 * inner.
 * 
 * @author Michiel Hendriks
 */
public class MethodExecutionVertex extends BaseGraphCell
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
		Map map = getAttributes();
		GraphConstants.setOpaque(map, true);
		GraphConstants.setBackground(map, Color.WHITE);
		GraphConstants.setBorderColor(map, Color.BLACK);
		GraphConstants.setAutoSize(map, true);
	}
}
