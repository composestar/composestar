/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: MessageGenerator.java 2659 2006-11-12 21:30:22Z elmuerte $
 */

package Composestar.Core.DIGGER.Walker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	public List create(CondMatchEdge edge)
	{
		List lst = new ArrayList();
		Iterator it = edge.getMatchingParts();
		while (it.hasNext())
		{
		
		}		
		return lst; 
	}
	
	public Message xform(Message base, SubstitutionEdge edge)
	{
		return new Message(null, null);
	}
}
