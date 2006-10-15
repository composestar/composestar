package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public class SubstitutionPartRuntime extends AbstractPatternRuntime 
{
    
    /**
     * @roseuid 40DD68840311
     */
    public SubstitutionPartRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD969D002B
     */
    public boolean interpret(Message m, Dictionary context) {
    	resolveTarget(m.getInternals(), m.getExternals(), m, context);
		return resolveSelector(m, context);     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DE04230257
     */
    public boolean resolveSelector(Message m, Dictionary context) {
		//m.addFilterParameter("SubstitionSelector",m.getSelector());
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tResolve selector: "+((MessageSelector)this.theSelectorRuntime.getReference()).getName());
		//String message_selector = m.getSelector();
		
		String ct_selector = ((MessageSelector)this.theSelectorRuntime.getReference()).getName();
		m.setSelector(ct_selector);
		return true;     
    }
    
    /**
     * @param internals
     * @param externals
     * @param m
     * @param context
     * @roseuid 40DE042C0191
     */
    public void resolveTarget(Dictionary internals, Dictionary externals, Message m, Dictionary context) {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\t\tResolve target: "+((Target)this.theTargetRuntime.getReference()).getName());
		String target = ((Target)this.theTargetRuntime.getReference()).getName();
		m.setTarget(target);
    }
}
