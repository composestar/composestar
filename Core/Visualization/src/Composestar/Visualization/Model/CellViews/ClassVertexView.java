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

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Renders the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public class ClassVertexView extends VertexView
{
	private static final long serialVersionUID = 249471576219357145L;
	
	protected static final ClassVertexRenderer RENDERER = new ClassVertexRenderer();

	/**
	 * Height of the class name
	 */
	protected double labelHeight = 20.0;

	/**
	 * Location between fields and methods
	 */
	protected double separatorPos = 40.0;

	public ClassVertexView()
	{
		super();
	}

	public ClassVertexView(Object cell)
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

	/**
	 * @see #labelHeight
	 */
	public double getLabelHeight()
	{
		return labelHeight;
	}

	/**
	 * @see #separatorPos
	 */
	public double getSeparatorPos()
	{
		return separatorPos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.AbstractCellView#updateGroupBounds()
	 */
	@Override
	protected void updateGroupBounds()
	{
		super.updateGroupBounds();

		// update the bounds to include the label area
		groupBounds.setFrame(groupBounds.getX(), groupBounds.getY() - labelHeight, groupBounds.getWidth(), groupBounds
				.getHeight()
				+ labelHeight);
	}

	@Override
	public void update()
	{
		CellView[] childViews = getChildViews();
		if (childViews.length >= 2)
		{
			Rectangle2D fBounds = childViews[0].getBounds();
			int inset = GraphConstants.getInset(getAllAttributes());
			separatorPos = labelHeight + fBounds.getHeight() + inset * 2 - 1;
			// 1 is for the border

			Rectangle2D mBounds = childViews[1].getBounds();
			double dy = fBounds.getY() + fBounds.getHeight() + inset - mBounds.getY();			
			if (dy != 0)
			{
				((AbstractCellView) childViews[1]).translate(0, dy);
			}
		}
		super.update();
	}
}
