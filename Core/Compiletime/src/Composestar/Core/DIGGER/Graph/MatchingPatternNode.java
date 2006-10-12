/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingPatternNode.java,v 1.3 2006/10/05 12:19:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;

/**
 * A node for a matching pattern.
 * 
 * @author Michiel Hendriks
 */
public class MatchingPatternNode extends Node
{
	protected MatchingPattern pattern;

	/**
	 * @param inGraph
     * @param inPattern
	 */
	public MatchingPatternNode(Graph inGraph, MatchingPattern inPattern)
	{
		super(inGraph);
		pattern = inPattern;
	}

	public MatchingPattern getPattern()
	{
		return pattern;
	}
}
