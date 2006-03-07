package Composestar.RuntimeCore.FLIRT.Policy;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Actions.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.*;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: MetaFilterPolicy.java,v 1.1 2006/02/16 23:15:54 pascal_durr Exp $
 * 
 * Policy that extends the DefaultFilterPolicy by adding support for the Meta 
 * Filter
 */
class MetaFilterPolicy extends FilterPolicy 
{
    
    /**
     * @param fm
     * @param filterList
     * @param aMessage
     * @return Composestar.Runtime.FLIRT.policy.PolicyExecutionResult
     * @roseuid 40DFDB7902FB
     */
    public PolicyExecutionResult executeFilterPolicy(FilterModuleRuntime fm, ArrayList filterList, Message aSingleMessage) {
		boolean messageWasFiltered = false;
		boolean exit = false;
		boolean shouldDispatchToInner = true;
		MessageList aMessage = new MessageList( aSingleMessage );

		FilterModule filtermod = (FilterModule)fm.getReference();

		Object result = null;
		ComposeStarAction csa;
		Dictionary context = new Hashtable();
		context.put("ConditionResolver", fm);
		aMessage.setInternals(fm.getInternals());
		aMessage.setExternals(fm.getExternals());

		// Update the JP!
		JoinPoint jp = new JoinPoint(aMessage.getTarget());
		jp.setInternals(fm.getInternals());
		jp.setExternals(fm.getExternals());
		jp.setAttributeList(Invoker.getInstance().getAttributesFor(aMessage.getTarget(), aMessage.getSelector()));
		JoinPointInfoProxy.updateJoinPoint(jp);

		if (Debug.DEBUGGER_INTERFACE || Debug.SHOULD_DEBUG) 
		{
			Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tProcessing meta filter policy...");
			DebuggerProvider.event(DebuggerProvider.MESSAGE_PROCESSING_START, null, aMessage, aMessage, filterList, context);
		}
		for (int j = 0; (j < filterList.size()) && !exit; j++) 
		{
			FilterRuntime f = (FilterRuntime) filterList.get(j);
			MessageList originalMessage = new MessageList(aMessage);

			if (Debug.DEBUGGER_INTERFACE || Debug.SHOULD_DEBUG) 
			{
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tEvaluating filter '"+((Filter)f.getReference()).getName()+"' of type '"+((Filter)f.getReference()).getFilterType().getType()+"'...");
				DebuggerProvider.event(DebuggerProvider.FILTER_EVALUATION_START, f, aMessage, originalMessage, filterList, context);
			}
			// Update the message!
            MessageInfoProxy.updateMessage(originalMessage);

			boolean eval = f.canAccept(aMessage, context);
			MessageList modifiedMessage = new MessageList(aMessage);

			if (eval) 
			{
				if (Debug.DEBUGGER_INTERFACE || Debug.SHOULD_DEBUG) 
				{
					DebuggerProvider.event(DebuggerProvider.FILTER_ACCEPTED, f, originalMessage, modifiedMessage, filterList, context);
				}
				csa = f.getAcceptAction(originalMessage, modifiedMessage, context);
			} 
			else 
			{
				if (Debug.DEBUGGER_INTERFACE || Debug.SHOULD_DEBUG) 
				{
					DebuggerProvider.event(DebuggerProvider.FILTER_REJECTED, f, originalMessage, modifiedMessage, filterList, context);
				}
				csa = f.getRejectAction(originalMessage, modifiedMessage, context);
			}


			if (Debug.SHOULD_DEBUG) 
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "############  Before call  ############");
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Original Message: " + originalMessage.getTarget().GetType().ToString() + "." + originalMessage.getSelector());
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Modified Message: " + modifiedMessage.getTarget().GetType().ToString() + "." + modifiedMessage.getSelector());
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "#######################################");
			}

			/*
			// TODO: remove this dirty hack to skip CpsDefaultInnerDispatchFilters
			//       when a message is outgoing
			if( csa instanceof DispatchToInnerAction && aMessage.getDirection() == Message.OUTGOING)
			{
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","*** Skipping DispatchToInner for outgoing messages");
				continue;
			}
			*/

			
			if( csa instanceof DispatchAction || csa instanceof DispatchToInnerAction || csa instanceof SendAction )
			{				
				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","End of filterset, returning '" + csa.getClass().getName()+"'.");
				return new PolicyExecutionResult(true, false, csa);
			}
			
			result = csa.execute();
			
			if(Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","#############  After call  ############");
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","Original Message: "+originalMessage.getTarget().GetType().ToString() +"."+ originalMessage.getSelector());
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","Filter Modified Message: "+modifiedMessage.getTarget().GetType().ToString() +"."+ modifiedMessage.getSelector());
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","Action Modified Message: "+csa.getMessageToContinueWith().getTarget().GetType().ToString() +"."+ csa.getMessageToContinueWith().getSelector());
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","#######################################");

				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tFilterexecution result: " + result);
				Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tContinue filterexecution? "+csa.getShouldContinue());
			}
			
			if (!csa.getShouldContinue()) 
			{
				exit = true;
			}

			//aMessage = csa.getMessageToContinueWith();
			aMessage.setTarget(csa.getMessageToContinueWith().getTarget());
			aMessage.setSelector(csa.getMessageToContinueWith().getSelector());
			messageWasFiltered = messageWasFiltered | eval;
			shouldDispatchToInner = shouldDispatchToInner & csa.getShouldContinue();
		}

		if(Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tDone processing meta filter policy.");
			Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tDispatch to inner? "+shouldDispatchToInner);
		}
		return new PolicyExecutionResult(messageWasFiltered,shouldDispatchToInner, result);
    }
}
