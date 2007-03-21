/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.CellViews;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * @author Michiel Hendriks
 */
public abstract class LineSeparationView extends VertexView
{
	protected static final CellViewRenderer RENDERER = new LineSeparationRenderer();

	/**
	 * The separation positions, relative
	 */
	protected double[] separators;

	public LineSeparationView()
	{
		super();
	}

	public LineSeparationView(Object cell)
	{
		super(cell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.VertexView#getRenderer()
	 */
	@Override
	public CellViewRenderer getRenderer()
	{
		return RENDERER;
	}

	public double[] getSeperators()
	{
		return separators.clone();
	}

	/**
	 * Normal vertex renderer but adds seperator lines at set positions.
	 * 
	 * @author Michiel Hendriks
	 */
	public static class LineSeparationRenderer extends VertexRenderer
	{
		private static final long serialVersionUID = -5036490366123202126L;

		private static final double[] EMPTY_ARRAY = new double[0];

		/**
		 * Separator positions
		 */
		protected transient double[] seps;

		/**
		 * Separator orientation
		 */
		protected transient CpsGraphConstants.Layout layout;

		/**
		 * Separator pattern
		 */
		protected transient float[] pattern;

		@Override
		protected void installAttributes(CellView view)
		{
			super.installAttributes(view);
			Map map = view.getAllAttributes();
			layout = CpsGraphConstants.getSeparatorLayout(map);
			pattern = CpsGraphConstants.getSeparatorPattern(map);
			if (view instanceof LineSeparationView)
			{
				seps = ((LineSeparationView) view).getSeperators();
			}
			else
			{
				seps = EMPTY_ARRAY;
			}
		}

		@Override
		protected void resetAttributes()
		{
			super.resetAttributes();
			seps = EMPTY_ARRAY;
			layout = CpsGraphConstants.Layout.HORIZONTAL;
			pattern = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jgraph.graph.VertexRenderer#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			if (bordercolor != null)
			{
				g2.setColor(bordercolor);
				if (pattern != null)
				{
					g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
							pattern, 0));
				}
				for (double pos : seps)
				{
					if (layout == CpsGraphConstants.Layout.VERTICAL)
					{
						g2.drawLine((int) pos, 0, (int) pos, getHeight());
					}
					else
					{
						g2.drawLine(0, (int) pos, getWidth(), (int) pos);
					}
				}
			}
			if (selected)
			{
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g2.setColor(highlightColor);
				for (double pos : seps)
				{
					if (layout == CpsGraphConstants.Layout.VERTICAL)
					{
						g2.drawLine((int) pos, 0, (int) pos, getHeight());
					}
					else
					{
						g2.drawLine(0, (int) pos, getWidth(), (int) pos);
					}
				}
			}
		}
	}
}
