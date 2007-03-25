/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells;

import org.jgraph.graph.DefaultEdge;

import Composestar.Core.DIGGER2.Trail;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * And edge for a DIGGER Trail
 * 
 * @author Michiel Hendriks
 */
public class TrailEdge extends DefaultEdge
{
	private static final long serialVersionUID = 1613495556831778244L;

	protected Trail trail;

	protected String selector;

	public TrailEdge(Trail inTrail)
	{
		super(((ContextRepositoryEntity) inTrail.getRE()).asSourceCode());
		trail = inTrail;
	}

	public Trail getTrail()
	{
		return trail;
	}
}
