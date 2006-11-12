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
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Utils.Debug;

/**
 * An node used for unknown filters when configured to do so. This node type
 * functions as a blank slate to keep stuff working when filter types are not
 * fully implemented. This node type should NOT be used, it's a fallback
 * 
 * @author Michiel Hendriks
 */
public class UnknownFilterNode extends FilterNode
{
	public UnknownFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
		Debug.out(Debug.MODE_WARNING, DIGGER.MODULE_NAME, "UnknownFilterNode used for filter "
				+ inFilter.getQualifiedName() + " of type " + inFilter.getFilterType().getType());
	}

	public void processElements()
	{
	// prevent additional work to be done
	}
}
