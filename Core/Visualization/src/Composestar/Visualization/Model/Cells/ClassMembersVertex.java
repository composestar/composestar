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

import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * General class for parts of the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public abstract class ClassMembersVertex extends BaseGraphCell
{
	private static Map<Integer,Icon> iconMap;
	
	protected static final double BASE_X = 100;

	protected static final double BASE_Y = 100;

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

	/**
	 * Add a child cell
	 * 
	 * @param userObject
	 * @param idx position of the entry
	 * @return
	 */
	protected BaseGraphCell addEntry(Object userObject, int idx, int visibility)
	{
		BaseGraphCell cell = new BaseGraphCell(userObject);
		Rectangle2D bounds = new Rectangle2D.Double(BASE_X, BASE_Y + idx * entryHeight, entryWidth, entryHeight);
		Map map = cell.getAttributes();
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		GraphConstants.setHorizontalTextPosition(map, JLabel.RIGHT);

		Icon ico = ClassMembersVertex.getIcon(visibility);
		if (ico != null)
		{
			GraphConstants.setIcon(map, ico);
		}

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
		Rectangle2D bounds = new Rectangle2D.Double(BASE_X, BASE_Y, entryWidth, 4);
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
	public static Icon getIcon(int visibility)
	{
		if (iconMap == null)
		{
			iconMap = new HashMap<Integer,Icon>();
		}
		if (!iconMap.containsKey(visibility))
		{
			URL resUrl = null;
			switch (visibility)
			{
				case ClassVertex.MEMBERS_PUBLIC:
					resUrl = ClassMembersVertex.class.getResource("Graphics/field_public.png");
					break;
				case ClassVertex.MEMBERS_PRIVATE:
					resUrl = ClassMembersVertex.class.getResource("Graphics/field_private.png");
					break;
				case ClassVertex.MEMBERS_PROTECTED:
					resUrl = ClassMembersVertex.class.getResource("Graphics/field_protected.png");
					break;
				default:
					// no icon
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
