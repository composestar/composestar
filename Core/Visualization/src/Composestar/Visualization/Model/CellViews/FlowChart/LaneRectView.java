/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.CellViews.FlowChart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * Creates a normal rectangular vertex with 2 lanes on the left and right side.
 * This resembles a "predefined process" in a flowchart.
 * 
 * @author Michiel Hendriks
 */
public class LaneRectView extends VertexView
{
	private static final long serialVersionUID = -2750640988712808402L;

	protected static final CellViewRenderer RENDERER = new LaneRectRenderer();

	public LaneRectView()
	{
		super();
	}

	public LaneRectView(Object cell)
	{
		super(cell);
	}

	@Override
	public CellViewRenderer getRenderer()
	{
		return RENDERER;
	}

	/**
	 * @author Michiel Hendriks
	 */
	public static class LaneRectRenderer extends VertexRenderer
	{
		private static final long serialVersionUID = -8633210651241946681L;

		/**
		 * The width of the lanes.
		 */
		protected int laneWidth;

		@Override
		public Dimension getPreferredSize()
		{
			Dimension res = super.getPreferredSize();
			res.setSize(res.getWidth() * 1.05, res.getHeight() * 1.5);
			return res;
		}

		@Override
		protected void installAttributes(CellView view)
		{
			super.installAttributes(view);
			laneWidth = CpsGraphConstants.getLaneWidth(view.getAllAttributes());
		}

		@Override
		protected void resetAttributes()
		{
			super.resetAttributes();
			laneWidth = CpsGraphConstants.DEFAULT_LANE_WIDTH;
		}

		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			int width = getWidth();
			if ((laneWidth > 0) && (laneWidth * 2 < width))
			{
				int height = getHeight();
				if (bordercolor != null)
				{
					g.setColor(bordercolor);
					g.drawLine(laneWidth, 0, laneWidth, height);
					g.drawLine(width - laneWidth, 0, width - laneWidth, height);
				}
				if (selected)
				{
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(GraphConstants.SELECTION_STROKE);
					g2.setColor(highlightColor);
					g.drawLine(laneWidth, 0, laneWidth, height);
					g.drawLine(width - laneWidth, 0, width - laneWidth, height);
				}
			}

		}

		@Override
		public Insets getInsets(Insets insets)
		{
			Insets ni = super.getInsets(insets);
			if (laneWidth > 0)
			{
				ni.left += laneWidth;
				ni.right += laneWidth;
			}
			return ni;
		}
	}
}
