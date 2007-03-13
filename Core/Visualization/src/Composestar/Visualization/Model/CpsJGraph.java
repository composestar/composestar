/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model;

import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import Composestar.Visualization.Model.Cells.BaseGraphCell;

/**
 * Subclass of JGraph to add some additional functionality
 * 
 * @author Michiel Hendriks
 */
public class CpsJGraph extends JGraph
{
	private static final long serialVersionUID = 2641264269098532193L;

	public CpsJGraph()
	{
		super();
		init();
	}

	public CpsJGraph(GraphLayoutCache cache)
	{
		super(cache);
		init();
	}

	public CpsJGraph(GraphModel model, BasicMarqueeHandler mh)
	{
		super(model, mh);
		init();
	}

	public CpsJGraph(GraphModel model, GraphLayoutCache layoutCache, BasicMarqueeHandler mh)
	{
		super(model, layoutCache, mh);
		init();
	}

	public CpsJGraph(GraphModel model, GraphLayoutCache cache)
	{
		super(model, cache);
		init();
	}

	public CpsJGraph(GraphModel model)
	{
		super(model);
		init();
	}

	protected void init()
	{
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Returns the most relevant tooltip
	 */
	public String getToolTipText(MouseEvent event)
	{
		double x = event.getX();
		double y = event.getY();
		CellView cellView = getNextViewAt(null, x, y);
		CellView lastView = null;
		while (cellView != null)
		{
			// because our cells often contain childs we must go through them
			// manually
			lastView = cellView;
			CellView[] childViews = cellView.getChildViews();
			cellView = getNextViewAt(childViews, cellView, x, y);
		}
		if (lastView != null)
		{
			Object cell = lastView.getCell();
			if (cell instanceof BaseGraphCell)
			{
				// now go back up the tree to find the first hint
				String res = null;
				BaseGraphCell curCell = ((BaseGraphCell) cell);
				while ((res == null) && (curCell != null))
				{
					res = curCell.getToolTipString();
					curCell = (BaseGraphCell) curCell.getParent();
				}
				return res;
			}
		}
		return null;
	}
}
