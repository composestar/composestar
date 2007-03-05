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

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;

/**
 * A port for a PVConcernNode with an assigned filtermodule
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -2474091449484054983L;

	public FilterModuleVertex(FilterModule fm)
	{
		super(fm);
		
		AttributeMap attrs = getAttributes();
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setOpaque(attrs, true);
	}

	public FilterModule getFilterModule()
	{
		return (FilterModule) getUserObject();
	}
}
