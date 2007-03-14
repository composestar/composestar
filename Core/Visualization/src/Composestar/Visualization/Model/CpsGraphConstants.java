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

import java.util.Map;

import org.jgraph.graph.GraphConstants;

/**
 * @author Michiel Hendriks
 */
@SuppressWarnings("unchecked")
public class CpsGraphConstants extends GraphConstants
{

	/**
	 * AttributeMap entry for the height of the vertex label
	 */
	public static final String LABEL_HEIGHT = "cpsLabelHeight";

	/**
	 * The layout model for the LineSeparatorView
	 */
	public static final String SEPARATOR_LAYOUT = "cpsSeparatorLayout";

	public enum Layout
	{
		HORIZONTAL, VERTICAL
	}

	public static final double getLabelHeight(Map map)
	{
		Double res = (Double) map.get(LABEL_HEIGHT);
		if (res == null)
		{
			return 0.0;
		}
		return res.doubleValue();
	}

	public static final void setLabelHeight(Map map, double height)
	{
		map.put(LABEL_HEIGHT, new Double(height));
	}

	public static final Layout getSeparatorLayout(Map map)
	{
		Layout res = (Layout) map.get(SEPARATOR_LAYOUT);
		if (res == null)
		{
			return Layout.HORIZONTAL;
		}
		return res;
	}

	public static final void getSeparatorLayout(Map map, Layout layout)
	{
		map.put(SEPARATOR_LAYOUT, layout);
	}
}
