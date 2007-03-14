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

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Michiel Hendriks
 */
public abstract class LineSeparationView extends VertexView
{
	protected static final LineSeparationRenderer RENDERER = new LineSeparationRenderer();

	/**
	 * The separation positions, relative
	 */
	protected double[] separators;

	public LineSeparationView()
	{
		super();
	}

	public LineSeparationView(Object cell)
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

	public double[] getSeperators()
	{
		return separators.clone();
	}
}
