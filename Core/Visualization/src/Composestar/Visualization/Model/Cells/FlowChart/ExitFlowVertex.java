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
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

import Composestar.Visualization.Model.Cells.BaseGraphCell;

/**
 * Basic vertex used for the exit flow. It's nothing more than an black
 * horizontal bar.
 * 
 * @author Michiel Hendriks
 */
public class ExitFlowVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -786649285564268678L;

	public ExitFlowVertex()
	{
		super();
	}

	@Override
	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setEditable(map, false);
		GraphConstants.setOpaque(map, true);
		GraphConstants.setBackground(map, Color.BLACK);
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, 40, 6);
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setSizeable(map, false);
	}

	@Override
	public String toString()
	{
		return null;
	}

}
