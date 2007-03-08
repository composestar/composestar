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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * General class for parts of the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public abstract class ClassMembersVertex extends BaseGraphCell
{
	private static Map<MemberFlags, Icon> iconMap;

	protected double entryHeight = 14;

	protected double entryWidth = 80;

	protected Map<String, BaseGraphCell> members;

	public ClassMembersVertex()
	{
		super();
		members = new HashMap<String, BaseGraphCell>();
	}

	public ClassMembersVertex(Object userObject)
	{
		super(userObject);
		members = new HashMap<String, BaseGraphCell>();
	}

	public ClassMembersVertex(Object userObject, AttributeMap storageMap)
	{
		super(userObject, storageMap);
		members = new HashMap<String, BaseGraphCell>();
	}

	public ClassMembersVertex(Object userObject, AttributeMap storageMap, MutableTreeNode[] children)
	{
		super(userObject, storageMap, children);
		members = new HashMap<String, BaseGraphCell>();
	}

	@Override
	public DefaultPort getPort()
	{
		// this cell doesn't have a default port
		return null;
	}

	/**
	 * Add a child cell
	 * 
	 * @param userObject
	 * @param idx position of the entry
	 * @return
	 */
	protected BaseGraphCell addEntry(Object userObject, int idx, MemberFlags visibility)
	{
		BaseGraphCell cell = new BaseGraphCell(userObject);
		Rectangle2D bounds = new Rectangle2D.Double(0, idx * entryHeight, entryWidth, entryHeight);
		Map map = cell.getAttributes();
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		GraphConstants.setHorizontalTextPosition(map, JLabel.RIGHT);

		Icon ico = ClassMembersVertex.getIcon(visibility);
		if (ico != null)
		{
			GraphConstants.setIcon(map, ico);
		}

		// update the default port to be placed at the left
		DefaultPort port = cell.getPort();
		Point2D portOffset = new Point2D.Double(0, GraphConstants.PERMILLE / 2);
		GraphConstants.setOffset(port.getAttributes(), portOffset);

		add(cell);
		cell.setParent(this);
		return cell;
	}

	/**
	 * Add a dummy entry for padding. Only to be used when there are no other
	 * members.
	 */
	protected void addDummy()
	{
		BaseGraphCell cell = new BaseGraphCell();
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, entryWidth, 4);
		Map map = cell.getAttributes();
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		add(cell);
		cell.setParent(this);
	}

	/**
	 * Returns an icon for the provided visibility. Uses the MEMBERS_PUBLIC,
	 * MEMBERS_PRIVATE, MEMBERS_PROTECTED contstants of ClassVertex
	 * 
	 * @param visibility
	 * @return
	 */
	public static Icon getIcon(MemberFlags visibility)
	{
		if (iconMap == null)
		{
			iconMap = new HashMap<MemberFlags, Icon>();
		}
		if (!iconMap.containsKey(visibility))
		{
			URL resUrl = null;
			if (visibility == MemberFlags.PUBLIC)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/field_public.png");
			}
			else if (visibility == MemberFlags.PRIVATE)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/field_private.png");
			}
			else if (visibility == MemberFlags.PROTECTED)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/field_protected.png");
			}
			System.out.println(resUrl);
			Icon ico = null;
			if (resUrl != null)
			{
				ico = new ImageIcon(resUrl);
			}
			iconMap.put(visibility, ico);
		}
		return iconMap.get(visibility);
	}

}
