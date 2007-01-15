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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.DIGGER.Graph.FilterElementNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.LambdaEdge;
import Composestar.Core.Exception.ModuleException;

/**
 * Graph Node for the Error Filter. The CondMatchEdge's will go to the next
 * filter in the chain where the last lamba edge will go to the exception node.
 * 
 * @author Michiel Hendriks
 */
public class ErrorFilterNode extends FilterNode
{
	protected List acceptEdges;
	
	public ErrorFilterNode(Graph inGraph, Filter inFilter, byte inDirection)
	{
		super(inGraph, inFilter, inDirection);
		acceptEdges = new ArrayList();
	}
	
	public void appendFilterElement(FilterElementNode inElement, FilterElement fe)
	{
		// add to queue
		acceptEdges.add(fe);
	}
	
	public void addNextFilter(FilterNode inFilter)
	{
		// process queue
		Iterator it = acceptEdges.iterator();
        for (Object acceptEdge : acceptEdges) {
            addOutgoingEdge(new CondMatchEdge(inFilter, (FilterElement) acceptEdge));
        }
        acceptEdges.clear();
		// add lamba edge to exception
		addOutgoingEdge(new LambdaEdge(graph.getExceptionNode(true)));
	}

	protected void processSubstitutionParts(MatchingPattern mp, FilterElementNode mpNode) throws ModuleException
	{
		// nop
	}
}
