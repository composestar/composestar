/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: PrependFilterNode.java,v 1.1 2006/10/05 13:16:20 elmuerte Exp $
 */

package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;

/**
 * @author Michiel Hendriks
 */
public class PrependFilterNode extends FilterNode
{
	public PrependFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
	}

	// TODO: need to reason about this, it doesn't actually dispatch, it appends
	// a message, required work from Wim Minnen
}
