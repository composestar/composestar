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
import org.jgraph.graph.GraphConstants;

import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * Renders the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public class ClassVertexView extends LineSeparationView
{
	private static final long serialVersionUID = 249471576219357145L;

	public ClassVertexView()
	{
		super();
		separators = new double[2];
	}

	public ClassVertexView(Object cell)
	{
		super(cell);
		separators = new double[2];
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
		//groupBounds.setFrame(groupBounds.getX(), groupBounds.getY() - separators[0], groupBounds.getWidth(),
		//		groupBounds.getHeight() + separators[0]);
	}

	@Override
	public void update()
	{
		separators[0] = CpsGraphConstants.getLabelHeight(getAllAttributes());
		CellView[] childViews = getChildViews();
		if (childViews.length >= 2)
		{
			Rectangle2D fBounds = childViews[0].getBounds();
			int inset = GraphConstants.getInset(getAllAttributes());
			separators[1] = separators[0] + fBounds.getHeight() + inset * 2 - 1;
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
