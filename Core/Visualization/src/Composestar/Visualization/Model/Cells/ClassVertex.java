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

	/**
	 * just show the class object without any members
	 */
	public static final int MEMBERS_NONE = 0;

	/**
	 * include all members
	 */
	public static final int MEMBERS_ALL = 255;

	/**
	 * include all public members
	 */
	public static final int MEMBERS_PUBLIC = 1;

	/**
	 * include all protected members
	 */
	public static final int MEMBERS_PROTECTED = 2;

	/**
	 * include all private members
	 */
	public static final int MEMBERS_PRIVATE = 4;

	/**
	 * force inclusion of members that are touched by compose* in any way
	 */
	public static final int MEMBERS_FORCE_RELEVANT = 128;

	protected Type platformRep;

	protected ClassFieldsVertex fields;

	protected ClassMethodsVertex methods;

	public ClassVertex(Type inPlatformRep)
	{
		this(inPlatformRep, MEMBERS_ALL);
	}

	/**
	 * @param inPlatformRep
	 * @param filter inclusion of class members
	 */
	public ClassVertex(Type inPlatformRep, int filter)
	{
		super(inPlatformRep);
		platformRep = inPlatformRep;
		addChildren(filter);
	}

	protected void addChildren(int filter)
	{
		AttributeMap attrs = getAttributes();
		GraphConstants.setEditable(attrs, false);
		GraphConstants.setChildrenSelectable(attrs, false);
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setVerticalAlignment(attrs, JLabel.TOP);
		GraphConstants.setFont(attrs, GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 12));
		GraphConstants.setGroupOpaque(attrs, true);
		GraphConstants.setOpaque(attrs, true);
		GraphConstants.setBackground(attrs, new Color(0xEEEEFF));
		GraphConstants.setInset(attrs, 4);
		GraphConstants.setSizeableAxis(attrs, GraphConstants.X_AXIS);
		GraphConstants.setHorizontalAlignment(attrs, JLabel.CENTER);

		fields = new ClassFieldsVertex(platformRep, filter);
		add(fields);
		fields.setParent(this);

		methods = new ClassMethodsVertex(platformRep, filter);
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
