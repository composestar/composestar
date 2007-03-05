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
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * General class for parts of the ClassVertex
 * 
 * @author Michiel Hendriks
 */
public abstract class ClassPartVertex extends BaseGraphCell
{
	protected double entryHeight = 14;
	
	protected double entryWidth = 80;

	protected BaseGraphCell addEntry(Object userObject, int idx)
	{
		BaseGraphCell cell = new BaseGraphCell(userObject);
		Rectangle2D bounds = new Rectangle2D.Double(0, idx * entryHeight, entryWidth, entryHeight);
		Map map = cell.getAttributes();
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		GraphConstants.setAutoSize(map, true); // Hmmm....
		// TODO: set icon

		add(cell);
		cell.setParent(this);
		return cell;
	}
	
	/**
	 * ClassPart doesn't contain any childs, add a dummy entry for padding.
	 */
	protected void addDummy()
	{
		BaseGraphCell cell = new BaseGraphCell("@");
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, entryWidth, entryHeight / 2);
		Map map = cell.getAttributes();
		GraphConstants.setBounds(map, bounds);
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		add(cell);
		cell.setParent(this);
	}

}
