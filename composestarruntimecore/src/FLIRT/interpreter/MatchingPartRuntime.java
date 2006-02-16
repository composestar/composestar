package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: MatchingPartRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public class MatchingPartRuntime extends AbstractPatternRuntime implements Interpretable 
{
    public MatchingTypeRuntime theMatchingTypeRuntime;
    
    /**
     * @roseuid 40DD6885009C
     */
    public MatchingPartRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD9697005F
     */
    public boolean interpret(MessageList m, Dictionary context) {
		// TODO WM: Iterate over messages here? probably not
    	return theMatchingTypeRuntime.interpret(m,context);
		// TODO: Fix it here so we do make a distinction between name and signature matching!!
    	// We now only allow for name.
		
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DDFDF702CD
     */
    public boolean resolveSelector(MessageList m, Dictionary context) {
		// TODO WM: iterate here? nah
		String message_selector = m.getSelector();
		String ct_selector = ((MessageSelector)this.theSelectorRuntime.getReference()).getName();
    	if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tResolving selector '" + ct_selector + "'...");
		
    	Object target = context.get("target");
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tSelector target is '"+target+"'.");
		if (target == null) //target was *
		{
			if (ct_selector.equals("*")) // *.*
			{
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound '*.*'.");
				return true;
			}
			else // *.something, match to the current 
			{
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound '*.something'.");
				return message_selector.equals(ct_selector);
			}
		}
		else //target was something
		{
			if (ct_selector.equals("*")) // something.*
			{
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound 'something.*'.");
				return true;
			}
			else // something.something, match to the current 
			{
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tFound 'something.something'.");
				return message_selector.equals(ct_selector);
			}
		}     
    }
    
    /**
     * @param internals
     * @param externals
     * @param m
     * @param context
     * @roseuid 40DDFE10002A
     */
    public void resolveTarget(Dictionary internals, Dictionary externals, MessageList m, Dictionary context) {
		String target = ((Target)this.theTargetRuntime.getReference()).getName();
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
