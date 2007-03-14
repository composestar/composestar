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

	public void update()
	{
		int inset = GraphConstants.getInset(getAllAttributes());
		CellView[] childViews = getChildViews();
		double curPos = 0;
		for (int i = 0; (i < 2) && (i < childViews.length); i++)
		{
			Rectangle2D bounds = childViews[i].getBounds();
			separators[i] = curPos + bounds.getWidth() + inset;
			curPos += separators[i];
		}
		super.update();
	}
}
