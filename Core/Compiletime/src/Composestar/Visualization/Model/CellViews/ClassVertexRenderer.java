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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellView;
import org.jgraph.graph.VertexRenderer;

/**
 * @author Michiel Hendriks
 */
public class ClassVertexRenderer extends VertexRenderer
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.VertexRenderer#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		CellView[] views = view.getChildViews();
		Rectangle2D bounds = views[0].getBounds();
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawLine(0, 20, getWidth(), 20);
	}
}
