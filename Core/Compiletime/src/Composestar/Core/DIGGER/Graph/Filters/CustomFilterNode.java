/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CustomFilterNode.java,v 1.3 2006/10/05 13:19:24 elmuerte Exp $
 */

package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.FilterNode;

/**
 * Filter Node for custom filters
 * 
 * @author Michiel Hendriks
 */
public class CustomFilterNode extends FilterNode
{
	public CustomFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
	}

	// TODO: add reasoning
}
