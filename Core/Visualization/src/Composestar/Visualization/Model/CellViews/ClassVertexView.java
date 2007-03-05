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

import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

/**
 * Renders the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public class ClassVertexView extends VertexView
{
	protected double labelHeight = 20.0;

	public ClassVertexView()
	{
		super();
	}

	public ClassVertexView(Object cell)
	{
		super(cell);
	}

	protected static final ClassVertexRenderer renderer = new ClassVertexRenderer();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.VertexView#getRenderer()
	 */
	@Override
	public CellViewRenderer getRenderer()
	{
		return renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.AbstractCellView#updateGroupBounds()
	 */
	@Override
	protected void updateGroupBounds()
	{
		// CellView[] childViews = getChildViews();
		// Rectangle2D fBounds = childViews[0].getBounds();
		// Rectangle2D mBounds = childViews[1].getBounds();
		// mBounds.setFrame(mBounds.getX(), mBounds.getY()-20,
		// mBounds.getWidth(), mBounds.getHeight());

		super.updateGroupBounds();

		// update the bounds to include the label area
		groupBounds.setFrame(groupBounds.getX(), groupBounds.getY() - labelHeight, groupBounds.getWidth(), groupBounds
				.getHeight()
				+ labelHeight);
	}

}
