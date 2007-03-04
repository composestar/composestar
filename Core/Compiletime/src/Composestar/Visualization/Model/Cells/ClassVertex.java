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

import javax.swing.JLabel;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.LAMA.Type;

/**
 * Implements an UML like Class shape.
 * 
 * @author Michiel Hendriks
 */
public class ClassVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -8859175705375540286L;

	protected Type platformRep;

	protected ClassFieldsVertex fields;

	protected ClassMethodsVertex methods;

	public ClassVertex(Type inPlatformRep)
	{
		super(inPlatformRep);
		platformRep = inPlatformRep;

		AttributeMap attrs = getAttributes();
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setVerticalAlignment(attrs, JLabel.TOP);
		GraphConstants.setFont(attrs, GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 12));
		GraphConstants.setGroupOpaque(attrs, true);
		GraphConstants.setOpaque(attrs, true);
		GraphConstants.setBackground(attrs, new Color(0xeeeeff));
		GraphConstants.setInset(attrs, 2);
		GraphConstants.setSizeableAxis(attrs, GraphConstants.X_AXIS);

		fields = new ClassFieldsVertex(platformRep);
		add(fields);
		fields.setParent(this);

		methods = new ClassMethodsVertex(platformRep);
		add(methods);
		methods.setParent(this);
	}
	
	public ClassFieldsVertex getFields()
	{
		return fields;
	}
	
	public ClassMethodsVertex getMethods()
	{
		return methods;
	}

	public String getClassName()
	{
		return platformRep.fullName();
	}
	
	public String toString()
	{
		return getClassName();
	}
}
