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
import org.jgraph.graph.VertexRenderer;

import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * Normal vertex renderer but adds seperator lines at set positions.
 * 
 * @author Michiel Hendriks
 */
public class LineSeparationRenderer extends VertexRenderer
{
	private static final long serialVersionUID = -5036490366123202126L;

	private static final double[] EMPTY_ARRAY = new double[0];

	protected transient double[] seps;

	protected transient CpsGraphConstants.Layout layout;

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
		Graphics2D g2d = (Graphics2D) g;
		if (pattern != null)
		{
			g2d
					.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
							pattern, 0));
		}
		for (double pos : seps)
		{
			if (layout == CpsGraphConstants.Layout.VERTICAL)
			{
				g2d.drawLine((int) pos, 0, (int) pos, getHeight());
			}
			else
			{
				g2d.drawLine(0, (int) pos, getWidth(), (int) pos);
			}
		}
	}
}
