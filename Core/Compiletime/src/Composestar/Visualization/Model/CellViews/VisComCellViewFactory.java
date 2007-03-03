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

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphModel;

/**
 * Creates the proper CellView instances for our custom cells
 * 
 * @author Michiel Hendriks
 */
public class VisComCellViewFactory extends DefaultCellViewFactory
{
	private static final long serialVersionUID = -6603775190011785736L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgraph.graph.CellViewFactory#createView(org.jgraph.graph.GraphModel,
	 *      java.lang.Object)
	 */
	public CellView createView(GraphModel model, Object cell)
	{
		// TODO Auto-generated method stub
		return super.createView(model, cell);
	}

}
