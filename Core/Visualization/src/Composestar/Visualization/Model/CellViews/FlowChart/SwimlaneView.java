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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

import Composestar.Visualization.Model.Cells.FlowChart.SwimlaneVertex;

/**
 * @author Michiel Hendriks
 */
public class SwimlaneView extends VertexView
{
	private static final long serialVersionUID = 4848041031217246157L;

	protected static final CellViewRenderer RENDERER = new SwimlaneRenderer();

	public SwimlaneView()
	{
		super();
	}

	public SwimlaneView(Object cell)
	{
		super(cell);
	}

	@Override
	public CellViewRenderer getRenderer()
	{
		return RENDERER;
	}

	/**
	 * Don't scale children
	 */
	@Override
	public void scale(double sx, double sy, Point2D origin)
	{
		getAttributes().scale(sx, sy, origin);
	}

	@Override
	public Rectangle2D getBounds()
	{
		return GraphConstants.getBounds(getAllAttributes());
	}

	@Override
	public void setBounds(Rectangle2D newBounds)
	{
		GraphConstants.setBounds(getAllAttributes(), newBounds);
	}

	@Override
	public void translate(double dx, double dy)
	{
		getAllAttributes().translate(dx, dy);
		int moveableAxis = GraphConstants.getMoveableAxis(getAllAttributes());
		if (moveableAxis == GraphConstants.X_AXIS)
		{
			dy = 0;
		}
		else if (moveableAxis == GraphConstants.Y_AXIS)
		{
			dx = 0;
		}
		for (Object view : childViews)
		{
			if (view instanceof AbstractCellView)
			{
				AbstractCellView child = (AbstractCellView) view;
				child.translate(dx, dy);
			}
		}
	}

	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p)
	{
		Rectangle2D bounds = getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t)
		{ // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		}
		else if (alpha < -t)
		{ // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		}
		else if (alpha < t)
		{ // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		}
		else
		{ // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}

	/**
	 * Based on JSwimlaneRenderer of JGraphPad CE
	 */
	public static class SwimlaneRenderer extends JPanel implements CellViewRenderer
	{
		private static final long serialVersionUID = -1185049590928641289L;

		/**
		 * Cache the current graph for drawing
		 */
		protected transient JGraph graph;

		protected static final JLabel label = new JLabel();

		protected static final JLabel container = new JLabel();

		protected transient Rectangle2D rect;

		protected transient Color gradientColor;

		protected transient Color borderColor;

		protected transient int borderWidth;

		/** Cached hasFocus and selected value. */
		protected transient boolean focus, selected, preview, filterlane;

		public SwimlaneRenderer()
		{
			super(new BorderLayout());
			label.setPreferredSize(new Dimension(100, 25));
			label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			this.add(label, BorderLayout.NORTH);
			this.add(container, BorderLayout.CENTER);

		}

		public Component getRendererComponent(JGraph inGraph, CellView view, boolean inSelected, boolean inFocus,
				boolean inPreview)
		{
			label.setText(view.getCell().toString());
			graph = inGraph;
			selected = inSelected;
			preview = inPreview;
			focus = inFocus;
			if (view.getCell() instanceof SwimlaneVertex)
			{
				filterlane = ((SwimlaneVertex) view.getCell()).hasFilterLane();
			}
			else
			{
				filterlane = false;
			}

			installAttributes(view);
			return this;
		}

		/**
		 * Paint the renderer. Overrides superclass paint to add specific
		 * painting.
		 */
		@Override
		public void paint(Graphics g)
		{
			try
			{
				Graphics2D g2d = (Graphics2D) g;
				if (gradientColor != null && !preview)
				{
					setOpaque(false);
					g2d
							.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), gradientColor,
									true));
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}
				super.paint(g);
				paintSelectionBorder(g);
				if (filterlane)
				{
					g2d.setColor(borderColor);
					float[] pattern = { 10, 10 };
					g2d.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
							pattern, 0));
					// TODO: Use attribute thingy
					g2d.drawLine(75, 25, 75, getHeight());
				}
			}
			catch (IllegalArgumentException e)
			{
				// JDK Bug: Zero length string passed to TextLayout constructor
			}
		}

		/**
		 * Provided for subclassers to paint a selection border.
		 */
		protected void paintSelectionBorder(Graphics g)
		{
			((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
			if (focus && selected)
			{
				g.setColor(graph.getLockedHandleColor());
			}
			else if (selected)
			{
				g.setColor(graph.getHighlightColor());
			}
			if (selected)
			{
				Dimension d = getSize();
				g.drawRect(0, 0, d.width - 1, d.height - 1);
			}
		}

		protected void installAttributes(CellView view)
		{
			Map attributes = view.getAllAttributes();

			Color foreground = GraphConstants.getForeground(attributes);
			setForeground((foreground != null) ? foreground : graph.getForeground());

			Color background = GraphConstants.getBackground(attributes);
			setOpaque(GraphConstants.isOpaque(attributes));
			if (GraphConstants.isGroupOpaque(attributes))
			{
				setBackground((background != null) ? background : graph.getBackground());
			}
			else
			{
				setOpaque(false);
				setBackground(null);
				label.setOpaque(GraphConstants.isOpaque(attributes));
				label.setBackground((background != null) ? background : graph.getBackground());
			}

			Font font = GraphConstants.getFont(attributes);
			label.setFont((font != null) ? font : graph.getFont());
			rect = GraphConstants.getBounds(attributes);
			setBounds((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
			Border border = GraphConstants.getBorder(attributes);
			borderColor = GraphConstants.getBorderColor(attributes);
			borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
			if (border != null)
			{
				label.setBorder(border);
				container.setBorder(border);
			}
			else if (borderColor != null)
			{
				label.setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
				container.setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
			}
			gradientColor = GraphConstants.getGradientColor(attributes);
		}
	}

}
