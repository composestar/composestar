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
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;

import javax.swing.SwingConstants;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * Implements an UML like Class shape.
 * 
 * @author Michiel Hendriks
 */
public class ClassVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -8859175705375540286L;

	public static final int INSET = 4;

	protected static final double LABELHEIGHT = 20.0;

	/**
	 * Various flags for members.
	 */
	public enum MemberFlags
	{
		PUBLIC, PROTECTED, PRIVATE,
		/**
		 * flags the member as being relevate for the current cause. Which
		 * usually means the member is being used as a source or target for a
		 * filter.
		 */
		RELEVANT;

		public static EnumSet<MemberFlags> all()
		{
			return EnumSet.allOf(MemberFlags.class);
		}

		public static EnumSet<MemberFlags> none()
		{
			return EnumSet.noneOf(MemberFlags.class);
		}
	}

	protected Type platformRep;

	protected ClassFieldsVertex fields;

	protected ClassMethodsVertex methods;

	public ClassVertex(Type inPlatformRep)
	{
		this(inPlatformRep, MemberFlags.all());
	}

	/**
	 * @param inPlatformRep
	 * @param filter inclusion of class members
	 */
	public ClassVertex(Type inPlatformRep, EnumSet<MemberFlags> filter)
	{
		super(inPlatformRep);
		platformRep = inPlatformRep;
		addChildren(filter);
		translate(INSET, LABELHEIGHT + INSET); // 20 = label height
	}

	@Override
	protected void setDefaults()
	{
		AttributeMap attrs = getAttributes();
		GraphConstants.setEditable(attrs, false);
		GraphConstants.setChildrenSelectable(attrs, false);
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setVerticalAlignment(attrs, SwingConstants.TOP);
		GraphConstants.setFont(attrs, GraphConstants.getFont(attrs).deriveFont(Font.BOLD, 12));
		GraphConstants.setGroupOpaque(attrs, true);
		GraphConstants.setOpaque(attrs, true);
		GraphConstants.setBackground(attrs, new Color(0xEEEEFF));
		GraphConstants.setInset(attrs, INSET);
		GraphConstants.setSizeableAxis(attrs, GraphConstants.X_AXIS);
		GraphConstants.setHorizontalAlignment(attrs, SwingConstants.CENTER);
		CpsGraphConstants.setLabelHeight(attrs, LABELHEIGHT);
	}

	protected void addChildren(EnumSet<MemberFlags> filter)
	{
		fields = new ClassFieldsVertex(platformRep, filter);
		add(fields);

		methods = new ClassMethodsVertex(platformRep, filter);
		add(methods);
		Rectangle2D fBounds = fields.calcBounds();
		methods.translate(0, INSET + fBounds.getHeight());
	}

	public ClassFieldsVertex getFields()
	{
		return fields;
	}

	public ClassMethodsVertex getMethods()
	{
		return methods;
	}

	/**
	 * Accepts MethodInfo, FieldInfo for the respectively method and field port.
	 * Fire2 message for a method port (uses the selector of the message). In
	 * case of a string input it first tries to find the method then the field.
	 * Otherwise it performs the default behavior of getPortFor() which would
	 * return the default class vertex port.
	 */
	@Override
	public DefaultPort getPortFor(Object obj)
	{
		if (obj instanceof MethodInfo)
		{
			return methods.getPortFor(obj);
		}
		else if (obj instanceof FieldInfo)
		{
			return fields.getPortFor(obj);
		}
		else if (obj instanceof Message)
		{
			return methods.getPortFor(((Message) obj).getSelector());
		}
		else if (obj instanceof String)
		{
			DefaultPort port = methods.getPortFor(obj);
			if (port != null)
			{
				return port;
			}
			port = fields.getPortFor(obj);
			if (port != null)
			{
				return port;
			}
		}
		return super.getPortFor(obj);
	}

	public String getClassName()
	{
		return platformRep.getFullName();
	}

	@Override
	public String toString()
	{
		return getClassName();
	}
}
