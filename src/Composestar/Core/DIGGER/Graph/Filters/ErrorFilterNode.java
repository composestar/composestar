/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ErrorNode.java,v 1.6 2006/10/05 13:19:24 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.DIGGER.Graph.FilterElementNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.LambdaEdge;
import Composestar.Core.Exception.ModuleException;

/**
 * Graph Node for the Error Filter
 * 
 * @author Michiel Hendriks
 */
public class ErrorFilterNode extends FilterNode
{
	public ErrorFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
	}

	protected void processSubstitutionParts(MatchingPattern mp, FilterElementNode mpNode) throws ModuleException
	{
		mpNode.addOutgoingEdge(new LambdaEdge(graph.getExceptionNode(true)));
	}
}
