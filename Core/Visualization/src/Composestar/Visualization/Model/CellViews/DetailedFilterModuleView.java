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

import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;

/**
 * @author Michiel Hendriks
 */
public class DetailedFilterModuleView extends LineSeparationView
{
	private static final long serialVersionUID = 7015866144460762632L;

	public DetailedFilterModuleView()
	{
		super();
		separators = new double[2];
	}

	public DetailedFilterModuleView(Object cell)
	{
		super(cell);
		separators = new double[2];
	}

	@Override
	public void update()
	{
		CellView[] childViews = getChildViews();
		int inset = GraphConstants.getInset(getAllAttributes());
		if (childViews.length > 1)
		{
			Rectangle2D left = childViews[0].getBounds();
			for (int i = 1; (i < 3) && (i < childViews.length); i++)
			{
				Rectangle2D right = childViews[i].getBounds();
				separators[i - 1] = right.getX() - left.getX() + (inset / 2);
			}
		}
		super.update();
	}
}
