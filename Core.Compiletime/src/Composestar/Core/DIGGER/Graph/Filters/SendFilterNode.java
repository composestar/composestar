/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.DIGGER.DIGGER;
import Composestar.Core.DIGGER.Graph.FilterChainNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.Exception.ModuleException;

/**
 * Graph node for the Send Filter
 * 
 * @author Michiel Hendriks
 */
public class SendFilterNode extends FilterNode
{
	public SendFilterNode(Graph inGraph, Filter inFilter)
	{
		super(inGraph, inFilter, FilterChainNode.OUTPUT);
	}

	public void setFilterDirection(byte inDirection) throws ModuleException
	{
		if (inDirection != FilterChainNode.OUTPUT)
		{
			throw new ModuleException("Send Filter may only have filterPosition == output", DIGGER.MODULE_NAME);
		}
		else
		{
			super.setFilterDirection(inDirection);
		}
	}
}
