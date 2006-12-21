/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Message.java 2659 2006-11-12 21:30:22Z elmuerte $
 */

package Composestar.Core.DIGGER.Walker;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;

/**
 * Message used during path walking
 * 
 * @author Michiel Hendriks
 */
public class Message
{
	protected AbstractConcernNode concernNode; 
	/**
	 * The selector of the message
	 */
	protected String selector;

	/**
	 * Where did this message come from. Will be NULL in case of initial
	 * messages. But can be set in a later stage when this message is
	 * encountered again.
	 */
	//TODO: should be a list, a message can come from multiple points
	protected Message originator;

	/**
	 * Indication of the certenty of the message. < 0 message will probably
	 * never be executed. = 0 message will be executed. > 0 this number of
	 * conditions must be valid.
	 */
	protected int certenty;
	
	public Message(AbstractConcernNode inConcernNode, String inSelector)
	{
		concernNode = inConcernNode;
		selector = inSelector;
	}
	
	public Message(Message base)
	{
		concernNode = base.getConcernNode();
		selector = base.getSelector();
	}
	
	public AbstractConcernNode getConcernNode()
	{
		return concernNode;
	}
	
	public String getSelector()
	{
		return selector;
	}
	
	public void setSelector(String inval)
	{
		selector = inval;
	}
	
	public void setCertenty(int inval)
	{
		certenty = inval;
	}
	
	public int getCertenty()
	{
		return certenty;
	}
	
	/**
	 * Returns true when this message matches
	 * @param edge
	 * @return
	 */
	public boolean matches(CondMatchEdge edge)
	{
		boolean res = false;
		if (!edge.getIsMessageList())
		{
			Iterator it = edge.getMatchingParts();
			while (it.hasNext())
			{
				MatchingPart mp = (MatchingPart) it.next();
				if (mp.getSelector().equals(selector) || mp.getSelector().equals("*"))
				{
					res = true;
					break;
				}
			}
		}
		else
		{
			//TODO: ...
		}
		if (edge.getEnabler())
		{
			return res;
		}
		else
		{
			return !res;
		}
	}
}
