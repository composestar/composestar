package Composestar.RuntimeCore.FLIRT.Policy;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.FLIRT.Debugger.*;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.*;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: DefaultFilterPolicy.java,v 1.6 2006/06/29 11:51:56 reddog33hummer Exp $
 * 
 * Filter policy with support for the Dispatch, Error and Substitution filters.
 */
class DefaultFilterPolicy extends FilterPolicy 
{
    
    /**
     * @param fm
     * @param filterList
     * @param aMessage
     * @return Composestar.Runtime.FLIRT.policy.PolicyExecutionResult
     * @roseuid 40DFDB790369
     */
    public PolicyExecutionResult executeFilterPolicy(FilterModuleRuntime fm, ArrayList filterList, MessageList aSingleMessage) {
		boolean messageWasFiltered = false;
		boolean exit = false;
		boolean shouldDispatchToInner = true;
		MessageList aMessage = new MessageList( aSingleMessage );

		Object result = null;
		ComposeStarAction csa;
		Dictionary context = new Hashtable();
		context.put("ConditionResolver", fm);
		aMessage.setInternals(fm.getInternals());
		aMessage.setExternals(fm.getExternals());

		Debugger.getInstance().event(Debugger.MESSAGE_PROCESSING_START,null,aMessage, null);
		if (Debug.SHOULD_DEBUG) 
		{
			Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tProcessing default filter policy...");
		}

		for (int j = 0; (j < filterList.size()) && !exit; j++) 
		{
			FilterRuntime f = (FilterRuntime) filterList.get(j);
			MessageList originalMessage = new MessageList(aMessage);
			
			Debugger.getInstance().event(Debugger.FILTER_EVALUATION_START,null,aMessage, null);
			if (Debug.SHOULD_DEBUG) 
			{
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tEvaluating filter '"+((Filter)f.getReference()).getName()+"' of type '"+((Filter)f.getReference()).getFilterType().getType()+"'...");
			}

			boolean eval = f.canAccept(aMessage, context);
			MessageList modifiedMessage = aMessage;

			if (eval) 
			{
				Debugger.getInstance().event(Debugger.FILTER_ACCEPTED,f,modifiedMessage, null);
				csa = f.getAcceptAction(originalMessage, modifiedMessage, context);
			}
			else 
			{
				Debugger.getInstance().event(Debugger.FILTER_REJECTED,f,modifiedMessage, null);
				csa = f.getRejectAction(originalMessage, modifiedMessage, context);
			}
			
			if(Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","#######################################");
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","Original Message: "+originalMessage.toShortString() );
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","Modified Message: "+modifiedMessage.toShortString() );
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","#######################################");
			}
			
			result = csa.execute();

			if(Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tFilterexecution result: "+result);
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tContinue filterexecution? "+csa.getShouldContinue());
			}
			
			if (!csa.getShouldContinue()) 
			{
				exit = true;
			}
			aMessage = modifiedMessage;
			messageWasFiltered = messageWasFiltered || eval;
			shouldDispatchToInner = shouldDispatchToInner && csa.getShouldContinue();
		}

	    if(Debug.SHOULD_DEBUG)
	    {
		    Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tDone processing default filter policy.");
		    Debug.out(Debug.MODE_INFORMATION,"FLIRT","Dispatch to inner? "+shouldDispatchToInner);
	    }
		return new PolicyExecutionResult(messageWasFiltered,shouldDispatchToInner, result);     
    }
}
