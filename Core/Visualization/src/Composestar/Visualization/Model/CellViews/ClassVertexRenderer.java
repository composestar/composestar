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

import org.jgraph.graph.VertexRenderer;

/**
 * @author Michiel Hendriks
 */
public class ClassVertexRenderer extends VertexRenderer
{
	private static final long serialVersionUID = -5036490366123202126L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.VertexRenderer#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		ClassVertexView classView = (ClassVertexView) view;
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawLine(0, (int) classView.getLabelHeight(), getWidth(), (int) classView.getLabelHeight());
		g2d.drawLine(0, (int) classView.getSeparatorPos(), getWidth(), (int) classView.getSeparatorPos());
	}
}
