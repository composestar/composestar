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
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;

/**
 * A vertex for a FilterModuleConcernVertex with an assigned filtermodule
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -2474091449484054983L;

	public FilterModuleVertex(FilterModule fm)
	{
		super(fm);
	}

	protected void setDefaults()
	{
		AttributeMap attrs = getAttributes();
		GraphConstants.setFont(getAttributes(), new Font("sansserif", Font.PLAIN, 11));
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, 80, 20);
		GraphConstants.setBounds(attrs, bounds);
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setBackground(attrs, new Color(0xDDEEFF));
		GraphConstants.setOpaque(attrs, true);

		DefaultPort port = getPort();
		if (port != null)
		{
			Point2D pt = new Point2D.Double(GraphConstants.PERMILLE, GraphConstants.PERMILLE / 2);
			GraphConstants.setOffset(port.getAttributes(), pt);
		}
	}

	public FilterModule getFilterModule()
	{
		return (FilterModule) getUserObject();
	}

	public String toString()
	{
		return getFilterModule().getOriginalQualifiedName();
	}
}
