package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: NameMatchingRuntime.java,v 1.3 2006/02/13 13:20:06 composer Exp $
 */
public class NameMatchingRuntime extends MatchingTypeRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD6884020D
     */
    public NameMatchingRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD96920333
     */
    public boolean interpret(MessageList m, Dictionary context) {
		// TODO W<: Iterate over messages or at least over internals/externals
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tInterpreting NAME MatchingPartRuntime...");
		resolveTarget(m.getInternals(), m.getExternals(), m, context);
		return resolveSelector(m, context);
    }

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DDFDF702CD
	 */
	public boolean resolveSelector(MessageList m, Dictionary context) 
	{
		String ct_selector = ((MessageSelector)this.parentMatchingPart.theSelectorRuntime.getReference()).getName();
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tResolving selector '"+ ct_selector+"'...");
		String message_selector = m.getSelector();

		Object target = context.get("target");
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tTarget: " + target);

		if (ct_selector.equals("*")) // *.*
		{
			// fixme Olaf:Shouldn't [dontcare.*] be [*.*]?
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound '[dontcare.*]'.");
			return true;
		}

		// *.something, match to the current
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound '[dontcare.something]'.");
		return message_selector.equals(ct_selector);
	}

	/**
	 * @param internals
	 * @param externals
	 * @param m
	 * @param context
	 * @roseuid 40DDFE10002A
	 */
	public void resolveTarget(Dictionary internals, Dictionary externals, MessageList m, Dictionary context) 
	{
		String target = ((Target)this.parentMatchingPart.theTargetRuntime.getReference()).getName();
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tResolving target '" + target + "'...");
		
		if (target.equals("*"))
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound '*'.");
		}
		else if (target.equalsIgnoreCase("inner"))
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound 'inner'.");
			context.put("target", m.getInner());
		}
		else if (target.equals(m.getTarget().GetType().ToString()))
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound target '"+m.getTarget().GetType().ToString()+"'.");
			context.put("target", m.getTarget());
		}
	}
}
