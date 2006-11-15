/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: DispatchNode.java,v 1.6 2006/10/10 22:38:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.DIGGER.DIGGER;
import Composestar.Core.DIGGER.Graph.FilterChainNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.Exception.ModuleException;

/**
 * Graph node for the Dispatch Filter
 * 
 * @author Michiel Hendriks
 */
public class DispatchFilterNode extends FilterNode
{
	public DispatchFilterNode(Graph inGraph, Filter inFilter)
	{
		super(inGraph, inFilter, FilterChainNode.INPUT);
	}

	public void setFilterDirection(byte inDirection) throws ModuleException
	{
		if (inDirection != FilterChainNode.INPUT)
		{
			throw new ModuleException("Dispatch Filter may only have direction == input", DIGGER.MODULE_NAME);
		}
		else
		{
			super.setFilterDirection(inDirection);
		}
	}
}
