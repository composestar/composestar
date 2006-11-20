/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;

/**
 * @author Michiel Hendriks
 */
public class FilterElementNode extends Node
{
	protected FilterElement element;

	/**
	 * @param inGraph
	 * @param inElement
	 */
	public FilterElementNode(Graph inGraph, FilterElement inElement)
	{
		super(inGraph, "");
		element = inElement;
	}
}
