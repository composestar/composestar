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

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Visualization.Model.Cells.ClassVertex.MemberFlags;

/**
 * A concern vertex with detailed filter module information. Used by the
 * FilterViewF
 * 
 * @author Michiel Hendriks
 */
public class FilterConcernVertex extends AbstractFilterModuleConcernVertex
{
	private static final long serialVersionUID = 5214277399628413966L;

	/**
	 * @param concern
	 */
	public FilterConcernVertex(Concern concern)
	{
		super(concern, MemberFlags.all());
	}

	@Override
	protected void addFmVertices(Concern concern)
	{
	// TODO Auto-generated method stub
	}
}
