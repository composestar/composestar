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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.jgraph.graph.DefaultPort;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * A ConcernVertex that add filter module information.
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractFilterModuleConcernVertex extends ConcernVertex
{

	/**
	 * Map with filter module name -> filter module vertex
	 */
	protected Map<String, FilterModuleVertex> fmVertices;

	/**
	 * If set then this is the new default port
	 */
	protected DefaultPort filterInputPort;

	/**
	 * @param concern
	 */
	public AbstractFilterModuleConcernVertex(Concern concern)
	{
		super(concern);
		fmVertices = new HashMap<String, FilterModuleVertex>();
		addFmVertices(concern);
	}

	/**
	 * @param concern
	 * @param filter
	 */
	public AbstractFilterModuleConcernVertex(Concern concern, EnumSet<MemberFlags> filter)
	{
		super(concern, filter);
		fmVertices = new HashMap<String, FilterModuleVertex>();
		addFmVertices(concern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.Model.Cells.ConcernVertex#getPort()
	 */
	@Override
	public DefaultPort getPort()
	{
		if (filterInputPort != null)
		{
			return filterInputPort;
		}
		return super.getPort();
	}

	public Collection<FilterModuleVertex> getFmVertices()
	{
		return fmVertices.values();
	}

	/**
	 * @return true if this concern has filter modules
	 */
	public boolean hasFilterModules()
	{
		return fmVertices.size() > 0;
	}

	protected abstract void addFmVertices(Concern concern);
}
