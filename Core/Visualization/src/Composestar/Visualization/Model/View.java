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

import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * @author Michiel Hendriks
 */
public abstract class View
{
	protected GraphModel model;

	protected GraphLayoutCache layout;

	protected JGraph graph;
	
	public View()
	{
	}

	/**
	 * Return the graph associated with this view.
	 * 
	 * @return
	 */
	public JGraph getGraph()
	{
		return graph;
	}
}
