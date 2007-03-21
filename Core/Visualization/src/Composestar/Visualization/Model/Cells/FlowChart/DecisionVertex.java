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
 * Vertex used for branches. The only actual branch occures in case of
 * conditions with a variable.
 * 
 * @author Michiel Hendriks
 */
public class DecisionVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -7492348198269898716L;

	public DecisionVertex(Object userObject)
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
