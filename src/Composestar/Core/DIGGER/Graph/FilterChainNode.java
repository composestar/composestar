/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph;

/**
 * Used for input/output filterchains of a concern
 * 
 * @author Michiel Hendriks
 */
public class FilterChainNode extends Node
{
	public static final byte INPUT = 0;

	public static final byte OUTPUT = 1;

	protected FilterNode lastFilter;

	protected byte direction;

	public FilterChainNode(Graph inGraph, byte inDirection)
	{
		super(inGraph);
		direction = inDirection;
		if (direction == INPUT)
		{
			setLabel("inputfilters");
		}
		else if (direction == OUTPUT)
		{
			setLabel("outputfilters");
		}
	}

	public byte getDirection()
	{
		return direction;
	}

	/**
	 * Append a new filter to this chain
	 * 
	 * @param inFilter
	 */
	public void appendFilter(FilterNode inFilter)
	{
		inFilter.setOwner(this);
		if (lastFilter == null)
		{
			addOutgoingEdge(new LambdaEdge(inFilter));
		}
		else
		{
			lastFilter.addOutgoingEdge(new LambdaEdge(inFilter));
		}
		lastFilter = inFilter;
	}
}
