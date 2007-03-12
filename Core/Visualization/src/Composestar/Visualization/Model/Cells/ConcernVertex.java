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

import java.util.EnumSet;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.LAMA.Type;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

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
		addClassVertex(concern, MemberFlags.none());
	}

	public ConcernVertex(Concern concern, EnumSet<MemberFlags> filter)
	{
		super(concern);
		addClassVertex(concern, filter);
	}
	
	protected void addClassVertex(Concern concern, EnumSet<MemberFlags> filter)
	{
		classVertex = new ClassVertex((Type) concern.getPlatformRepresentation(), filter);
		add(classVertex);
		classVertex.setParent(this);
	}

	@Override
	protected void setDefaults()
	{
		super.setDefaults();
		AttributeMap attrs = getAttributes();
		GraphConstants.setEditable(attrs, false);
		GraphConstants.setChildrenSelectable(attrs, false);
		GraphConstants.setSizeableAxis(attrs, GraphConstants.X_AXIS);
	}

	@Override
	public DefaultPort getPort()
	{
		return classVertex.getPort();
	}

	public DefaultPort getClassPort()
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
