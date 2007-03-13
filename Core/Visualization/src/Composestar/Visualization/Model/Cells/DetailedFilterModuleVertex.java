/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;

/**
 * Shows the inner details of the filtermodule. Used by the FilterConcernVertex.
 * 
 * @author Michiel Hendriks
 */
public class DetailedFilterModuleVertex extends FilterModuleVertex
{
	private static final long serialVersionUID = 1346991495621474334L;

	/**
	 * Cell that contains all inputfilters cells
	 */
	protected BaseGraphCell inputFilters;

	/**
	 * Cell that contains all outputfilter cells
	 */
	protected BaseGraphCell outputFilters;

	/**
	 * Cell that contains all internals, externals and conditions
	 */
	protected BaseGraphCell members;

	public DetailedFilterModuleVertex(FilterModule fm)
	{
		super(fm);
	}

	protected void setDefaults()
	{
		AttributeMap attrs = getAttributes();
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, 80, 20);
		GraphConstants.setBounds(attrs, bounds);
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setBackground(attrs, new Color(0xDDEEFF));
		GraphConstants.setGroupOpaque(attrs, true);
	}
}
