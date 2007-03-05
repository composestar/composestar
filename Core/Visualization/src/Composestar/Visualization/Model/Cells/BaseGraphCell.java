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

import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * Base graph cell for all of our own graph vertices
 * 
 * @author Michiel Hendriks
 */
public class BaseGraphCell extends DefaultGraphCell
{
	private static final long serialVersionUID = 3870314973085521163L;

	protected DefaultPort defaultPort;

	public BaseGraphCell()
	{
		super();
	}

	public BaseGraphCell(Object userObject, AttributeMap storageMap, MutableTreeNode[] children)
	{
		super(userObject, storageMap, children);
	}

	public BaseGraphCell(Object userObject, AttributeMap storageMap)
	{
		super(userObject, storageMap);
	}

	public BaseGraphCell(Object userObject)
	{
		super(userObject);
	}

	/**
	 * Get the floating port
	 */
	public DefaultPort getPort()
	{
		if (defaultPort == null)
		{
			defaultPort = new DefaultPort();
			add(defaultPort);
			defaultPort.setParent(this);
		}
		return defaultPort;
	}

	/**
	 * Get a specific port for the input object.
	 * 
	 * @param obj
	 * @return
	 */
	public DefaultPort getPortFor(Object obj)
	{
		return getPort();
	}
}
