/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER.Walker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.DIGGER.Graph.SubstitutionEdge;

/**
 * Creates and manages messages for certain concerns + selectors.
 * 
 *
 * @author Michiel Hendriks
 */
public class MessageGenerator
{
	/**
	 * 
	 */
	public MessageGenerator()
	{

	}
	
	//TODO: this sucks
	public List create(CondMatchEdge edge)
	{
		List lst = new ArrayList();
		if (!edge.getEnabler())
		{
			// can't do much with that (yet)
			return lst;
		}			
		Iterator it = edge.getMatchingParts();
		while (it.hasNext())
		{
			MatchingPart mp = (MatchingPart) it.next();
			if (mp.getSelector().equals("*"))
			{
				// we can't create a selector for that (yet)
				continue;
			}
			Message msg = new Message(null, mp.getSelector().toString());
			lst.add(msg);
		}		
		return lst; 
	}
	
	public List create(ConcernNode concernNode)
	{
		List lst = new ArrayList();
		
		return lst;
	}
	
	/**
	 * Create a new message based an an existing message and a substitution rule
	 * 
	 * @param base
	 * @param edge
	 * @return
	 */
	public Message xform(Message base, SubstitutionEdge edge)
	{
		Message msg = new Message(base);
		SubstitutionPart sp = edge.getSubstitutionPart();
		msg.setSelector(sp.getSelector().toString());
		return msg;
	}
}
