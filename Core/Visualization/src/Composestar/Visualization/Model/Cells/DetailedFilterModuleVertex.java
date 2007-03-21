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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingConstants;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.CpsGraphConstants;

/**
 * Shows the inner details of the filtermodule. Used by the FilterConcernVertex.
 * Note: not resizeable because it contains only group vertices
 * 
 * @author Michiel Hendriks
 */
public class DetailedFilterModuleVertex extends FilterModuleVertex
{
	private static final long serialVersionUID = 1346991495621474334L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Cells.DetailedFilterModuleVertex");

	/**
	 * Cell that contains all inputfilters cells
	 */
	protected BaseGraphCell inputFilters;

	/**
	 * Cell that contains all outputfilter cells
	 */
	protected BaseGraphCell outputFilters;

	/**
	 * Cell that contains all internals, externals and conditions
	 */
	protected BaseGraphCell members;

	protected final static int INSET = 4;

	protected Map<Filter, FilterVertex> filterVertices;

	public DetailedFilterModuleVertex(FilterModule fm)
	{
		super(fm);
		filterVertices = new HashMap<Filter, FilterVertex>();

		inputFilters = new BaseGraphCell("input filters");
		add(inputFilters);
		members = new BaseGraphCell("members");
		add(members);
		outputFilters = new BaseGraphCell("output filters");
		add(outputFilters);

		addFilters(inputFilters, fm.getInputFilterIterator(), FireModel.INPUT_FILTERS);
		addFilters(outputFilters, fm.getOutputFilterIterator(), FireModel.OUTPUT_FILTERS);
		addMembers(fm);

		// calculat the max bounds
		Rectangle2D bounds = calcBounds();
		if (inputFilters.isLeaf())
		{
			logger.info("No input filters for " + fm);
			setEmptyCell(inputFilters, "no input", bounds.getHeight() - INSET * 2);
		}
		if (outputFilters.isLeaf())
		{
			logger.info("No output filters for " + fm);
			setEmptyCell(outputFilters, "no output", bounds.getHeight() - INSET * 2);
		}
		if (members.isLeaf())
		{
			logger.info("No members for " + fm);
			setEmptyCell(members, null, bounds.getHeight() - INSET * 2);
		}
		bounds = inputFilters.calcBounds();
		members.translate(bounds.getWidth() + INSET, 0);
		bounds = members.calcBounds();
		outputFilters.translate(bounds.getX() + bounds.getWidth() + INSET, 0);

		translate(INSET, INSET); // inset
		bounds = calcBounds();
	}

	@Override
	public DefaultPort getPort()
	{
		// no default port
		return null;
	}

	/**
	 * Accepts a Filter object and returns the port associated with the given
	 * Filter. Otherwise it will return null.
	 */
	@Override
	public DefaultPort getPortFor(Object obj)
	{
		if (obj instanceof Filter)
		{
			FilterVertex vertex = filterVertices.get(obj);
			if (vertex != null)
			{
				return vertex.getPort();
			}
		}
		else if (obj instanceof String)
		{
			String fname = (String) obj;
			for (FilterVertex vertex : filterVertices.values())
			{
				if (vertex.getFilter().getName().equals(fname))
				{
					return vertex.getPort();
				}
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return null;
	}

	public BaseGraphCell getInputVertex()
	{
		return inputFilters;
	}

	public BaseGraphCell getOutputVertex()
	{
		return outputFilters;
	}

	public BaseGraphCell getMemberVertex()
	{
		return members;
	}

	/**
	 * Set the widths of the cells to the given value
	 * 
	 * @param widths array with 3 items for: input, members, output
	 */
	public void setWidthSpec(double[] widths)
	{
		Rectangle2D bounds;
		bounds = GraphConstants.getBounds(inputFilters.getAttributes());
		if (bounds != null)
		{
			bounds.setFrame(bounds.getX(), bounds.getY(), widths[0], bounds.getHeight());
			GraphConstants.setBounds(inputFilters.getAttributes(), bounds);
		}
		bounds = GraphConstants.getBounds(members.getAttributes());
		if (bounds != null)
		{
			bounds.setFrame(bounds.getX(), bounds.getY(), widths[1], bounds.getHeight());
			GraphConstants.setBounds(members.getAttributes(), bounds);
		}
		bounds = GraphConstants.getBounds(outputFilters.getAttributes());
		if (bounds != null)
		{
			bounds.setFrame(bounds.getX(), bounds.getY(), widths[0], bounds.getHeight());
			GraphConstants.setBounds(outputFilters.getAttributes(), bounds);
		}

		members.translate(inputFilters.calcBounds().getWidth() - members.calcBounds().getX() + INSET * 2, 0);
		bounds = members.calcBounds();
		outputFilters.translate(bounds.getX() + bounds.getWidth() - outputFilters.calcBounds().getX() + INSET, 0);
	}

	@Override
	protected void setDefaults()
	{
		AttributeMap attrs = getAttributes();
		GraphConstants.setBorderColor(attrs, Color.BLACK);
		GraphConstants.setBackground(attrs, new Color(0xDDEEFF));
		GraphConstants.setOpaque(attrs, true);
		GraphConstants.setGroupOpaque(attrs, true);
		GraphConstants.setInset(attrs, INSET);
		CpsGraphConstants.setSeparatorLayout(attrs, CpsGraphConstants.Layout.VERTICAL);
		float[] dash = { 5f, 5f };
		CpsGraphConstants.setSeparatorPattern(attrs, dash);
	}

	protected void addFilters(BaseGraphCell parentVertex, Iterator filterIterator, int direction)
	{
		FilterVertex last = null;
		while (filterIterator.hasNext())
		{
			Filter filter = (Filter) filterIterator.next();
			FilterVertex vertex = new FilterVertex(filter, direction);
			logger.debug("Added filter vertex for " + vertex.toString());
			if (last != null)
			{
				Rectangle2D bounds = last.calcBounds();
				vertex.translate(0, bounds.getY() + bounds.getHeight() - 1);
			}

			parentVertex.add(vertex);
			filterVertices.put(filter, vertex);
			last = vertex;
		}
	}

	protected void addMembers(FilterModule fm)
	{
		Map<String, DeclaredRepositoryEntity> entries = new TreeMap<String, DeclaredRepositoryEntity>(
				String.CASE_INSENSITIVE_ORDER);
		Iterator it = fm.getInternalIterator();
		while (it.hasNext())
		{
			Internal internal = (Internal) it.next();
			entries.put(internal.getName(), internal);
		}
		it = fm.getExternalIterator();
		while (it.hasNext())
		{
			External external = (External) it.next();
			entries.put(external.getName(), external);
		}
		it = fm.getConditionIterator();
		while (it.hasNext())
		{
			Condition condition = (Condition) it.next();
			entries.put(condition.getName(), condition);
		}

		FilterModuleMemberVertex last = null;
		for (DeclaredRepositoryEntity entry : entries.values())
		{
			logger.debug("Adding member " + entry.getName());
			FilterModuleMemberVertex vertex = new FilterModuleMemberVertex(entry);
			if (last != null)
			{
				Rectangle2D bounds = last.calcBounds();
				vertex.translate(0, bounds.getY() + bounds.getHeight() - 1);
			}
			members.add(vertex);
			last = vertex;
		}
	}

	/**
	 * Makes the given cell behave as an empty cell
	 * 
	 * @param cell
	 * @param msg
	 */
	protected void setEmptyCell(BaseGraphCell cell, String msg, double destHeight)
	{
		AttributeMap map = cell.getAttributes();
		cell.setUserObject(msg);
		GraphConstants.setForeground(map, Color.GRAY);
		GraphConstants.setHorizontalAlignment(map, SwingConstants.CENTER);
		GraphConstants.setFont(map, new Font("sansserif", Font.ITALIC, 10));
		int width = 10;
		if (msg != null)
		{
			width = msg.length() * 6;
		}
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, width, destHeight);
		GraphConstants.setBounds(map, bounds);
	}

}
