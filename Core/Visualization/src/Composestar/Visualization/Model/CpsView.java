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

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import Composestar.Visualization.Model.CellViews.VisComCellViewFactory;

/**
 * @author Michiel Hendriks
 */
public abstract class CpsView
{
	protected GraphModel model;

	protected GraphLayoutCache layout;

	protected CpsJGraph graph;

	public CpsView()
	{
		setGraphDefaults();
	}

	/**
	 * Return the graph associated with this view.
	 * 
	 * @return
	 */
	public CpsJGraph getGraph()
	{
		return graph;
	}

	/**
	 * Return the list of cells that may be positioned through layout
	 * algorithms.
	 * 
	 * @return
	 */
	public Object[] getLayoutCells()
	{
		return graph.getRoots();
	}

	protected void setGraphDefaults()
	{
		model = new DefaultGraphModel();
		layout = new GraphLayoutCache(model, new VisComCellViewFactory());
		graph = new CpsJGraph(model, layout);
		graph.setCpsView(this);

		graph.setAntiAliased(true);
		// graph.setBendable(false);
		// graph.setConnectable(false);
		// graph.setDisconnectable(false);
		graph.setEditable(false);
		graph.setGridVisible(true); // meh
	}
	
	public abstract String getName();
}
