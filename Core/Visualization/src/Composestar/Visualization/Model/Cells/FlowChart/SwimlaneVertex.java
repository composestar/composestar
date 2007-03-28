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
import java.util.List;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

/**
 * @author Michiel Hendriks
 */
public class SwimlaneVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = 4668365001411262131L;

	/**
	 * If true this swimlane has a filter lane (so actually it has two lanes)
	 */
	protected boolean filterLane = true;
	
	/**
	 * Contains the grid of elements.
	 */
	protected List<List<BaseFlowChartVertex>> grid;

	public SwimlaneVertex(Object userObject)
	{
		super(userObject);
	}

	public boolean hasFilterLane()
	{
		return filterLane;
	}

	/**
	 * Add a vertex to this swimlane. It will automatically position the vertex
	 * in the swimlane at the proper location and adjust the Swimlane's bounds.
	 * 
	 * @param newVert
	 */
	public void addVertex(BaseFlowChartVertex newVert)
	{
		if (newVert instanceof DecisionVertex)
		{
			filterLane = true;
		}
		// TODO:
	}

	@Override
	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setEditable(map, false);
		GraphConstants.setFont(map, new Font("sansserif", Font.PLAIN, 12));
		GraphConstants.setOpaque(map, true);
		GraphConstants.setBackground(map, Color.WHITE);
		GraphConstants.setBorderColor(map, Color.BLACK);
		GraphConstants.setInset(map, 5);
	}
}
