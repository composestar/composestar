/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE.ActionNode;
import Composestar.Core.FIRE.FilterReasoningEngine;
import Composestar.Core.FIRE.Node;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OrderAnalysis {

	private Concern concern;
	private FilterModuleOrder order;
	private Map messages;

	public OrderAnalysis(Concern concern, FilterModuleOrder order)
	{
		this.concern = concern;
		this.order = order;
		this.messages = new HashMap();
	}
	
	public void analyze()
	{
		FilterReasoningEngine fire = new FilterReasoningEngine(new LinkedList(order.orderAsList()));
		fire.run();
		Node fireTree = fire.getTree();

		for( int i = 0; i < fireTree.numberOfChildren(); i++ )
		{
			ActionNode node = (ActionNode) fireTree.getChild(i);
			if( node.getTarget().getName().equals("*") )
			{
				if( node instanceof ActionNode )
				{
					String selector = node.getSelector().getName();

					if( hasMethod(selector) )
					{
						MessageAnalysis ma = (MessageAnalysis) messages.get(selector);
						if( ma == null )
						{
							ma = new MessageAnalysis(this.concern, selector);
							this.messages.put(selector, ma);
						}
						ma.addState(node);
					}
					else
					{
						System.err.println("CKRET: Skipping method " + selector);
					}
				}
			}
		}
		
		for( Iterator i = this.messages.values().iterator(); i.hasNext(); )
		{
			((MessageAnalysis)i.next()).analyze();
		}
	}
	
	private boolean hasMethod(String selector)
	{
		
		Signature s = concern.getSignature();
		return (selector.equals("*") || s.getMethodStatus(selector) != MethodWrapper.REMOVED); 
	}
	
}
