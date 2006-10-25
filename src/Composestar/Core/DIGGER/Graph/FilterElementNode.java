/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterElementNode.java,v 1.4 2006/10/10 22:38:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;

/**
 * @author Michiel Hendriks
 */
public class FilterElementNode extends Node
{
	protected FilterElement element;

	/**
	 * @deprecated
	 */
	protected MatchingPatternNode lastPattern;

	/**
	 * @param inGraph
     * @param inElement
	 */
	public FilterElementNode(Graph inGraph, FilterElement inElement)
	{
		super(inGraph, "");
		element = inElement;
	}

	/**
	 * 
	 * @param inPattern
	 * @param inMatchingParts
	 * @deprecated
	 */
	public void appendPattern(MatchingPatternNode inPattern, List inMatchingParts)
	{
		inPattern.setOwner(this);
		addOutgoingEdge(new MatchingEdge(inPattern, inMatchingParts));
		lastPattern = inPattern;
	}
}
