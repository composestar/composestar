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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.DIGGER.NOBBIN;
import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.SubstitutionEdge;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Creates and manages messages for certain concerns + selectors.
 * 
 * @author Michiel Hendriks
 */
public class MessageGenerator
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(NOBBIN.MODULE_NAME);
	
	protected Map msgCache;

	/**
	 * 
	 */
	public MessageGenerator()
	{
		msgCache = new HashMap();
	}

	/**
	 * Creates or gets a message based on the given input.
	 * 
	 * @param concernNode
	 * @param selector
	 * @return
	 */
	public Message getMessageFor(ConcernNode concernNode, String selector)
	{
		String hashKey = concernNode.getLabel() + "\037" + selector;
		if (msgCache.containsKey(hashKey))
		{
			logger.debug("Return cached message for " + hashKey);
			return (Message) msgCache.get(hashKey);
		}
		Message msg = new Message(concernNode, selector);
		msgCache.put(hashKey, msg);
		return msg;
	}

	/**
	 * Publicates a given message
	 * 
	 * @param base
	 * @return
	 */
	public Message cloneMessage(Message base)
	{
		return new Message(base);
	}

	/**
	 * Create a list of messages based on the methods in a concern
	 * 
	 * @param concernNode
	 * @return
	 */
	public List create(ConcernNode concernNode)
	{
		List lst = new ArrayList();
		Concern concern = concernNode.getConcern();
		Type type = (Type) concern.platformRepr;
		Iterator it = type.getMethods().iterator();
		while (it.hasNext())
		{
			MethodInfo mi = (MethodInfo) it.next();
			Message msg = getMessageFor(concernNode, mi.name());
			lst.add(msg);
		}
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
		Message msg = cloneMessage(base);
		SubstitutionPart sp = edge.getSubstitutionPart();
		String sel = sp.getSelector().getName().toString();
		if (!sel.equals("*"))
		{
			msg.setSelector(sel);
		}
		if (edge.getDestination() instanceof AbstractConcernNode)
		{
			msg.setConcernNode((AbstractConcernNode) edge.getDestination());
		}
		return msg;
	}
}
