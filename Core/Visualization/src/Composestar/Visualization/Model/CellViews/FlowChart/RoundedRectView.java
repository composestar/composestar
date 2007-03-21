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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Renders a sort of rounded rectangle. The left and right side are half
 * circles.
 * 
 * @author Michiel Hendriks
 */
public class RoundedRectView extends VertexView
{
	private static final long serialVersionUID = 8931326745995615983L;

	protected static final CellViewRenderer RENDERER = new RoundedRectRenderer();

	public RoundedRectView()
	{
		super();
	}

	public RoundedRectView(Object cell)
	{
		super(cell);
	}

	@Override
	public CellViewRenderer getRenderer()
	{
		return RENDERER;
	}

	/**
	 * TODO: perminiter
	 * 
	 * @author Michiel Hendriks
	 */
	public static class RoundedRectRenderer extends VertexRenderer
	{
		private static final long serialVersionUID = -694834744164930859L;

		@Override
		public Dimension getPreferredSize()
		{
			Dimension res = super.getPreferredSize();
			res.setSize(res.getWidth() * 1.2, res.getHeight() * 1.5);
			return res;
		}

		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			Dimension dim = getSize();
			int roundRectArc = dim.height - borderWidth;
			if (super.isOpaque())
			{
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview)
				{
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), gradientColor, true));
				}
				g.fillRoundRect(borderWidth / 2, borderWidth / 2, dim.width - (int) (borderWidth * 1.5), dim.height
						- (int) (borderWidth * 1.5), roundRectArc, roundRectArc);
			}

			boolean origSelected = selected;
			try
			{
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			}
			finally
			{
				selected = origSelected;
			}
			if (bordercolor != null)
			{
				g.setColor(bordercolor);
				g2.setStroke(new BasicStroke(borderWidth));
				g.drawRoundRect(borderWidth / 2, borderWidth / 2, dim.width - (int) (borderWidth * 1.5), dim.height
						- (int) (borderWidth * 1.5), roundRectArc, roundRectArc);
			}
			if (selected)
			{
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				g.drawRoundRect(borderWidth / 2, borderWidth / 2, dim.width - (int) (borderWidth * 1.5), dim.height
						- (int) (borderWidth * 1.5), roundRectArc, roundRectArc);
			}
		}
	}
}
