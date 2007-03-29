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
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import Composestar.Visualization.Model.Cells.BaseGraphCell;

/**
 * @author Michiel Hendriks
 */
public abstract class BaseFlowChartVertex extends BaseGraphCell
{
	public BaseFlowChartVertex()
	{
		super();
	}

	public BaseFlowChartVertex(Object userObject, AttributeMap storageMap, MutableTreeNode[] children)
	{
		super(userObject, storageMap, children);
	}

	public BaseFlowChartVertex(Object userObject, AttributeMap storageMap)
	{
		super(userObject, storageMap);
	}

	public BaseFlowChartVertex(Object userObject)
	{
		super(userObject);
	}

	@Override
	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setEditable(map, false);
		GraphConstants.setFont(map, new Font("sansserif", Font.PLAIN, 11));
		GraphConstants.setOpaque(map, true);
		GraphConstants.setBackground(map, Color.WHITE);
		GraphConstants.setBorderColor(map, Color.BLACK);
		//GraphConstants.setAutoSize(map, true);
		GraphConstants.setResize(map, true);
		GraphConstants.setInset(map, 5);
		GraphConstants.setBounds(map, new Rectangle2D.Double(0, 0, 15, 15));
	}
}
