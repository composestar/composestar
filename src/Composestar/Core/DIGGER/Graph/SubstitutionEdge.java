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

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;

/**
 * Edge for substitutions
 * 
 * @author Michiel Hendriks
 */
public class SubstitutionEdge extends LambdaEdge
{
	protected SubstitutionPart substitutionPart;

	/**
	 * @param inDestination
	 * @param inSubstitutionPart
	 */
	public SubstitutionEdge(Node inDestination, SubstitutionPart inSubstitutionPart)
	{
		super(inDestination);
		substitutionPart = inSubstitutionPart;
	}

	public SubstitutionPart getSubstitutionPart()
	{
		return substitutionPart;
	}

	public String getSubstitutionPartAsString()
	{
		return substitutionPart.getTarget().getName() + "." + substitutionPart.getSelector().getName();
	}
}
