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
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Based on JGraphDiamondView.java of GraphPad CE.
 * 
 * @author Michiel Hendriks
 */
public class DiamondView extends VertexView
{
	private static final long serialVersionUID = 7400831320117382325L;

	protected static final CellViewRenderer RENDERER = new DiamondRenderer();

	public DiamondView()
	{
		super();
	}

	public DiamondView(Object cell)
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
	public static class DiamondRenderer extends VertexRenderer
	{
		private static final long serialVersionUID = -2137861688918267728L;

		@Override
		public Dimension getPreferredSize()
		{
			Dimension res = super.getPreferredSize();
			res.setSize(res.getWidth() * 1.5, res.getHeight() * 2.5);
			return res;
		}

		@Override
		public Point2D getPerimeterPoint(VertexView view, Point2D source, Point2D p)
		{
			Rectangle2D bounds = view.getBounds();
			Point2D center = AbstractCellView.getCenterPoint(view);
			double halfwidth = bounds.getWidth() / 2;
			double halfheight = bounds.getHeight() / 2;

			Point2D top = new Point2D.Double(center.getX(), center.getY() - halfheight);
			Point2D bottom = new Point2D.Double(center.getX(), center.getY() + halfheight);
			Point2D left = new Point2D.Double(center.getX() - halfwidth, center.getY());
			Point2D right = new Point2D.Double(center.getX() + halfwidth, center.getY());

			// Special case for intersecting the diamond's points
			if (center.getX() == p.getX())
			{
				if (center.getY() > p.getY())
				{
					return (top);
				}
				return bottom;
			}
			if (center.getY() == p.getY())
			{
				if (center.getX() > p.getX())
				{
					return (left);
				}
				// right point
				return right;
			}

			// In which quadrant will the intersection be?
			// set the slope and offset of the border line accordingly
			Point2D res;
			if (p.getX() < center.getX())
			{
				if (p.getY() < center.getY())
				{
					res = intersection(view, p, center, top, left);
				}
				else
				{
					res = intersection(view, p, center, bottom, left);
				}
			}
			else if (p.getY() < center.getY())
			{
				res = intersection(view, p, center, top, right);
			}
			else
			{
				res = intersection(view, p, center, bottom, right);
			}
			return res;
		}

		/**
		 * Find the point of intersection of two straight lines (which follow
		 * the equation y=mx+b) one line is an incoming edge and the other is
		 * one side of the diamond.
		 */
		private Point2D intersection(VertexView view, Point2D lineOneStart, Point2D lineOneEnd, Point2D lineTwoStart,
				Point2D lineTwoEnd)
		{
			// m = delta y / delta x, the slope of a line
			// b = y - mx, the axis intercept
			double m1 = (lineOneEnd.getY() - lineOneStart.getY()) / (lineOneEnd.getX() - lineOneStart.getX());
			double b1 = lineOneStart.getY() - m1 * lineOneStart.getX();
			double m2 = (lineTwoEnd.getY() - lineTwoStart.getY()) / (lineTwoEnd.getX() - lineTwoStart.getX());
			double b2 = lineTwoStart.getY() - m2 * lineTwoStart.getX();
			double xinter = (b1 - b2) / (m2 - m1);
			double yinter = m1 * xinter + b1;
			Point2D intersection = view.getAttributes().createPoint(xinter, yinter);
			return intersection;
		}

		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			// construct the diamond
			Dimension dim = getSize();
			int width = dim.width - borderWidth;
			int height = dim.height - borderWidth;
			int[] xcoords = { width / 2, width, width / 2, 0 };
			int[] ycoords = { 0, height / 2, height, height / 2 };
			Polygon diamond = new Polygon(xcoords, ycoords, 4);

			if (isOpaque())
			{
				g2.setColor(getBackground());
				if (gradientColor != null && !preview)
				{
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), gradientColor, true));
				}
				g2.fillPolygon(diamond);
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
				g2.setColor(bordercolor);
				g2.setStroke(new BasicStroke(borderWidth));
				g2.drawPolygon(diamond);
			}
			if (selected)
			{
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g2.setColor(highlightColor);
				g2.drawPolygon(diamond);
			}
		}
	}
}
