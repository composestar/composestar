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
}
