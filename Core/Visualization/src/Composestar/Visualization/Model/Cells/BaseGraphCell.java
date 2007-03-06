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

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.tree.MutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

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
		setDefaults();
	}

	public BaseGraphCell(Object userObject, AttributeMap storageMap, MutableTreeNode[] children)
	{
		super(userObject, storageMap, children);
		setDefaults();
	}

	public BaseGraphCell(Object userObject, AttributeMap storageMap)
	{
		super(userObject, storageMap);
		setDefaults();
	}

	public BaseGraphCell(Object userObject)
	{
		super(userObject);
		setDefaults();
	}

	protected void setDefaults()
	{
		GraphConstants.setFont(getAttributes(), new Font("sansserif", Font.PLAIN, 10));
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

	public void translate(double dx, double dy)
	{
		for (Object o : getChildren())
		{
			if (o instanceof BaseGraphCell)
			{
				((BaseGraphCell) o).translate(dx, dy);
			}
			else if (o instanceof DefaultGraphCell)
			{
				AttributeMap map = ((DefaultGraphCell) o).getAttributes();
				map.translate(dx, dy);
			}
		}
		getAttributes().translate(dx, dy);
	}

	public Rectangle2D calcBounds()
	{
		if (getChildCount() > 0)
		{
			Rectangle2D r = GraphConstants.getBounds(getAttributes());
			Rectangle2D ret = (r != null) ? (Rectangle2D) r.clone() : null;
			for (Object o : getChildren())
			{
				if (o instanceof BaseGraphCell)
				{
					r = ((BaseGraphCell) o).calcBounds();
				}
				else if (o instanceof DefaultGraphCell)
				{
					r = GraphConstants.getBounds(((DefaultGraphCell) o).getAttributes());
				}
				else {
					r = null;
				}
				if (r != null)
				{
					if (ret == null)
					{
						ret = (r != null) ? (Rectangle2D) r.clone() : null;
					}
					else
					{
						Rectangle2D.union(ret, r, ret);
					}
				}
			}
			return ret;
		}
		return GraphConstants.getBounds(getAttributes());
	}
}
