/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ConcernNode.java,v 1.3 2006/10/05 12:19:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.DIGGER.Graph.SpecialNodes.InnerNode;

/**
 * A node for a concern containing filterchains
 * 
 * @author Michiel Hendriks
 */
public class ConcernNode extends AbstractConcernNode
{
	protected FilterChainNode inputFilters;

	protected FilterChainNode outputFilters;

	/**
	 * A special node for the inner part of a concern
	 */
	protected InnerNode innerNode;

	/**
	 * @param inConcern
     * @param inGraph
	 */
	public ConcernNode(Graph inGraph, Concern inConcern)
	{
		super(inGraph, inConcern);
		inputFilters = new FilterChainNode(inGraph, FilterChainNode.INPUT);
		inputFilters.setOwner(this);
		addOutgoingEdge(new LambdaEdge(inputFilters));
		outputFilters = new FilterChainNode(inGraph, FilterChainNode.OUTPUT);
		outputFilters.setOwner(this);
		innerNode = new InnerNode(inGraph);
		innerNode.setOwner(this);
	}

	public FilterChainNode getInputFilters()
	{
		return inputFilters;
	}

	public FilterChainNode getOutputFilters()
	{
		return outputFilters;
	}

	public InnerNode getInnerNode()
	{
		return innerNode;
	}
}
