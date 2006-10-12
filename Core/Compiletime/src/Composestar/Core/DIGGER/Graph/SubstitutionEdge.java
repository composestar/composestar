/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: SubstitutionEdge.java,v 1.1 2006/10/05 12:19:15 elmuerte Exp $
 */

package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;

/**
 * @author Michiel Hendriks
 */
public class SubstitutionEdge extends Edge
{
	protected SubstitutionPart substitutionPart;

	/**
	 * @param inDestination
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
