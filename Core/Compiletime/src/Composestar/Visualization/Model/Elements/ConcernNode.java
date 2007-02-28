/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Elements;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Michiel Hendriks
 */
public abstract class ConcernNode extends DefaultGraphCell
{
	protected DefaultGraphCell title;

	protected DefaultGraphCell properties;

	protected DefaultGraphCell methods;

	public ConcernNode(Concern concern)
	{
		super(concern);
		GraphConstants.setAutoSize(attributes, true);
		// title part
		title = new DefaultGraphCell(concern.getName());
		Map attr = title.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);
		Font fnt = GraphConstants.getFont(attr);
		fnt = fnt.deriveFont(Font.BOLD);
		GraphConstants.setFont(attr, fnt);
		add(title);
		
		// properties part
		properties = new DefaultGraphCell(null);
		attr = properties.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);		
		add(properties);
		
		// methods part
		methods = new DefaultGraphCell(null);
		attr = methods.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);
		add(methods);
	}
}
