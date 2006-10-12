/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MatchingEdge.java,v 1.5 2006/10/05 12:19:15 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;

/**
 * An edge containing matching parts
 * 
 * @author Michiel Hendriks
 */
public class MatchingEdge extends Edge
{
	protected List matchingParts;

	/**
	 * @param inDestination
	 */
	public MatchingEdge(Node inDestination, List inMatchingParts)
	{
		super(inDestination);
		matchingParts = inMatchingParts;
	}

	public Iterator getMatchingParts()
	{
		return matchingParts.iterator();
	}

	/**
	 * Return the matching parts as a string representation
	 * 
	 * @return
	 */
	public String getMatchingPartsAsString()
	{
		// yes, it's not great code
		StringBuffer sb = new StringBuffer();
		Iterator it = matchingParts.iterator();
		while (it.hasNext())
		{
			MatchingPart mp = (MatchingPart) it.next();
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			if (mp.getMatchType() instanceof NameMatchingType)
			{
				sb.append("[");
			}
			else if (mp.getMatchType() instanceof SignatureMatchingType)
			{
				sb.append("<");
			}
			sb.append(mp.getTarget().getName());
			sb.append(".");
			sb.append(mp.getSelector().getName());
			if (mp.getMatchType() instanceof NameMatchingType)
			{
				sb.append("]");
			}
			else if (mp.getMatchType() instanceof SignatureMatchingType)
			{
				sb.append(">");
			}
		}
		return sb.toString();
	}
}
