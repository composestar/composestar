/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: SimpleConcernNode.java,v 1.2 2006/10/03 06:31:05 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * A concern node for concerns without filters
 * 
 * @author Michiel Hendriks
 */
public class SimpleConcernNode extends AbstractConcernNode
{
	/**
	 * @param inConcern
     * @param inGraph
	 */
	public SimpleConcernNode(Graph inGraph, Concern inConcern)
	{
		super(inGraph, inConcern);
	}

	public boolean addOutgoingEdge(Edge edge)
	{
		// not allowed to have outgoing edges
		return false;
	}
}
