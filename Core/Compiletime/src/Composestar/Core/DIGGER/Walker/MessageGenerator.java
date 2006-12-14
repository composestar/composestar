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
import Composestar.Core.DIGGER.Graph.Node;
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
	public Message getMessageFor(AbstractConcernNode concernNode, String selector)
	{
		String hashKey = concernNode.getLabel() + " " + selector;
		if (msgCache.containsKey(hashKey))
		{
			logger.debug("[MSGGEN] Return cached message for '" + hashKey + "'");
			return (Message) msgCache.get(hashKey);
		}
		Message msg = new Message(concernNode, selector);
		msgCache.put(hashKey, msg);
		return msg;
	}

	/**
	 * Creates a clone of a given message
	 * 
	 * @param base
	 * @return
	 */
	public Message cloneMessage(Message base)
	{
		Message clone = new Message(base);
		base.addClone(clone);
		return clone;
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
		Type type = (Type) concern.getPlatformRepresentation();
		if (type == null)
		{
			logger.warn("Concern without a platform representation: " + concern.getQualifiedName());
			return lst;
		}
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
		String selector = base.getSelector();
		AbstractConcernNode acn = base.getConcernNode();
		SubstitutionPart sp = edge.getSubstitutionPart();
		String sel = sp.getSelector().getName();
		if (!sel.equals("*"))
		{
			selector = sel;
		}
		Node dest = edge.getDestination();
		if (dest instanceof AbstractConcernNode)
		{
			acn = (AbstractConcernNode) edge.getDestination();
		}
		return getMessageFor(acn, selector);
	}
}
