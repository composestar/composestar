/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Elements;

import org.jgraph.graph.DefaultGraphCell;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * Concern Node instance for the ProgramView
 * @author Michiel Hendriks
 */
public class PVConcernNode extends ConcernNode
{
	public PVConcernNode(Concern inConcern)
	{
		super(inConcern);		
	}

}
