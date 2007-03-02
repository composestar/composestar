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
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Michiel Hendriks
 */
public abstract class ConcernNode extends DefaultGraphCell
{
	protected DefaultPort defaultPort;
	
	protected DefaultGraphCell title;
	
	protected DefaultPort titlePort;

	protected DefaultGraphCell properties;

	protected DefaultGraphCell methods;

	public ConcernNode(Concern concern)
	{
		super(concern);
		defaultPort = new DefaultPort();
		add(defaultPort);
		defaultPort.setParent(this);
		
		//GraphConstants.setAutoSize(attributes, true);
		// title part
		title = new DefaultGraphCell(concern.getName());
		Map attr = title.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);
		GraphConstants.setBounds(attr, new Rectangle2D.Double(0, 0, 80, 20));
		Font fnt = GraphConstants.getFont(attr);
		fnt = fnt.deriveFont(Font.BOLD);
		GraphConstants.setFont(attr, fnt);
		add(title);
		title.setParent(this);
		
		titlePort = new DefaultPort();
		title.add(titlePort);
		titlePort.setParent(title);
		
		// properties part
		properties = new DefaultGraphCell(null);
		attr = properties.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);	
		GraphConstants.setBounds(attr, new Rectangle2D.Double(0, 20, 80, 10));
		add(properties);
		properties.setParent(this);
		
		// methods part
		methods = new DefaultGraphCell(null);
		attr = methods.getAttributes();
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setAutoSize(attr, true);
		GraphConstants.setBorderColor(attr, Color.BLACK);
		GraphConstants.setBounds(attr, new Rectangle2D.Double(0, 30, 80, 10));
		add(methods);
		methods.setParent(this);
		
		DefaultPort dp = new DefaultPort("DefaultPort");
		attr = dp.getAttributes();
		GraphConstants.setOpaque(attr, true);
		add(dp);
	}
	
	public DefaultPort getPort()
	{
		return titlePort;
	}
}
