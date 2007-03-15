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

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JLabel;

import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.FIRE2.model.FireModel;

/**
 * A vertex for a Filter, used for the FilterView by the FilterConcernVertex
 * 
 * @author Michiel Hendriks
 */
public class FilterVertex extends BaseGraphCell
{
	private static final long serialVersionUID = 5395354686350397849L;

	/**
	 * The string representation of this vertex. Because the Filter object
	 * doesn't return a nice enough string we use this.
	 */
	protected String label;

	protected int direction;

	public FilterVertex(Filter filter, int inDirection)
	{
		super(filter);
		direction = inDirection;
		Point2D offset;
		if (direction == FireModel.OUTPUT_FILTERS)
		{
			GraphConstants.setHorizontalAlignment(getAttributes(), JLabel.RIGHT);
			offset = new Point2D.Double(GraphConstants.PERMILLE, GraphConstants.PERMILLE / 2);
		}
		else
		{
			offset = new Point2D.Double(0, GraphConstants.PERMILLE / 2);
		}
		GraphConstants.setOffset(getPort().getAttributes(), offset);
		setLabel(filter);
	}

	public Filter getFilter()
	{
		return (Filter) getUserObject();
	}

	public String toString()
	{
		return label;
	}

	@Override
	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setFont(map, new Font("sansserif", Font.PLAIN, 11));
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, 80, 14);
		GraphConstants.setBounds(map, bounds);
	}

	/**
	 * Sets the string representation of this vertex using info from the filter.
	 * 
	 * @param filter
	 */
	protected void setLabel(Filter filter)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(filter.getName());
		sb.append(": ");
		sb.append(filter.getFilterType().getType());
		label = sb.toString();
	}

}
