package Composestar.RuntimeCore.FLIRT.Policy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * DefaultFilterPolicy.java 2072 2006-10-15 12:44:56Z elmuerte $ Filter policy
 * with support for the Dispatch, Error and Substitution filters.
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
	public PolicyExecutionResult executeFilterPolicy(FilterModuleRuntime fm, List filterList, MessageList aSingleMessage)
	{
		boolean messageWasFiltered = false;
		boolean exit = false;
		boolean shouldDispatchToInner = true;
		MessageList aMessage = new MessageList(aSingleMessage);

		Object result = null;
		ComposeStarAction csa;
		Dictionary context = new Hashtable();
		context.put("ConditionResolver", fm);
		aMessage.setInternals(fm.getInternals());
		aMessage.setExternals(fm.getExternals());

		// Debugger.getInstance().event(Debugger.MESSAGE_PROCESSING_START, null,
		// aMessage, null);
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tProcessing default filter policy...");
		}

		for (int j = 0; (j < filterList.size()) && !exit; j++)
		{
			FilterRuntime f = (FilterRuntime) filterList.get(j);
			MessageList originalMessage = new MessageList(aMessage);

			// Debugger.getInstance().event(Debugger.FILTER_EVALUATION_START,
			// null, aMessage, null);
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tEvaluating filter '"
						+ ((Filter) f.getReference()).getName() + "' of type '"
						+ ((Filter) f.getReference()).getFilterType().getClass() + "'...");
			}

			boolean eval = f.canAccept(aMessage, context);
			MessageList modifiedMessage = aMessage;

			if (eval)
			{
				// Debugger.getInstance().event(Debugger.FILTER_ACCEPTED, f,
				// modifiedMessage, null);
				csa = f.getAcceptAction(originalMessage, modifiedMessage, context);
			}
			else
			{
				// Debugger.getInstance().event(Debugger.FILTER_REJECTED, f,
				// modifiedMessage, null);
				csa = f.getRejectAction(originalMessage, modifiedMessage, context);
			}

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "#######################################");
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Original Message: " + originalMessage.toShortString());
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Modified Message: " + modifiedMessage.toShortString());
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "#######################################");
			}

			result = csa.execute();

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilterexecution result: " + result);
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tContinue filterexecution? " + csa.getShouldContinue());
			}

			if (!csa.getShouldContinue())
			{
				exit = true;
			}
			aMessage = modifiedMessage;
			messageWasFiltered = messageWasFiltered || eval;
			shouldDispatchToInner = shouldDispatchToInner && csa.getShouldContinue();
		}

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tDone processing default filter policy.");
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Dispatch to inner? " + shouldDispatchToInner);
		}
		return new PolicyExecutionResult(messageWasFiltered, shouldDispatchToInner, result);
	}
}
