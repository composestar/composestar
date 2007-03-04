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

import org.jgraph.graph.DefaultPort;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.LAMA.Type;

/**
 * The abstract base class for all ConcernCells used by the various views. By
 * default this class is nothing more than a wrapper for a ClassVertex.
 * 
 * @author Michiel Hendriks
 */
public abstract class ConcernVertex extends BaseGraphCell
{
	/**
	 * The class vertex
	 */
	protected ClassVertex classVertex;

	public ConcernVertex(Concern concern)
	{
		super(concern);

		classVertex = new ClassVertex((Type) concern.getPlatformRepresentation());
		add(classVertex);
		classVertex.setParent(this);
	}

	@Override
	public DefaultPort getPort()
	{
		return classVertex.getPort();
	}

	/**
	 * Return the associated concern
	 * 
	 * @return
	 */
	public Concern getConcern()
	{
		return (Concern) getUserObject();
	}
}
