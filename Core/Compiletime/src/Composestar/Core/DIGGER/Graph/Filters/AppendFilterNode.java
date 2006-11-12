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
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;

/**
 * @author Michiel Hendriks
 */
public class AppendFilterNode extends FilterNode
{
	public AppendFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
	}
	// TODO: need to reason about this, it doesn't actually dispatch, it appends
	// a message, required work from Wim Minnen
}
