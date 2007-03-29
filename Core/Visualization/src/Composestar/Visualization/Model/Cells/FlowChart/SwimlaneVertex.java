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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Michiel Hendriks
 */
public class SwimlaneVertex extends BaseFlowChartVertex
{
	private static final long serialVersionUID = 4668365001411262131L;

	/**
	 * If true this swimlane has a filter lane (so actually it has two lanes)
	 */
	protected boolean filterLane;

	/**
	 * Contains the grid of elements.
	 */
	protected List<List<BaseFlowChartVertex>> grid;

	protected List<DefaultPort> returnPorts;

	protected int lastReportPort = -1;

	protected static final int INSET = 10;

	public SwimlaneVertex(Concern concern)
	{
		super(concern);
		grid = new ArrayList<List<BaseFlowChartVertex>>();
		returnPorts = new ArrayList<DefaultPort>();

		// dummy cell for padding
		DefaultGraphCell dummy = new DefaultGraphCell();
		GraphConstants.setBounds(dummy.getAttributes(), new Rectangle2D.Double(INSET, INSET, 1, 1));
		add(dummy);
	}

	public boolean hasFilterLane()
	{
		return filterLane;
	}

	public String toString()
	{
		return ((Concern) getUserObject()).getQualifiedName();
	}

	/**
	 * Add a vertex to this swimlane. It will automatically position the vertex
	 * in the swimlane at the proper location and adjust the Swimlane's bounds.
	 * 
	 * @param newVert
	 */
	public void addVertex(BaseFlowChartVertex newVert)
	{
		int y = grid.size() - 1;
		if (newVert instanceof DecisionVertex)
		{
			filterLane = true;
		}
		else
		{
			// MethodCall/Execute vertex
			y++;
		}
		if (y >= grid.size())
		{
			grid.add(new ArrayList<BaseFlowChartVertex>());
		}
		List<BaseFlowChartVertex> row = grid.get(y);
		newVert.translate(INSET + row.size() * 100, INSET + 25 + y * 100 + row.size() * 20);
		row.add(newVert);
		add(newVert);

		// TODO: update bounds
	}

	/**
	 * Returns a "return" port
	 * 
	 * @return
	 */
	public DefaultPort getReturnPort()
	{
		if (lastReportPort < grid.size())
		{
			lastReportPort = grid.size();
			DefaultPort port = new DefaultPort();
			GraphConstants.setOffset(port.getAttributes(), new Point2D.Double(0, lastReportPort * 100));
			add(port);
			returnPorts.add(port);
			return port;
		}
		return returnPorts.get(returnPorts.size() - 1);
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
		GraphConstants.setInset(map, INSET);
	}
}
