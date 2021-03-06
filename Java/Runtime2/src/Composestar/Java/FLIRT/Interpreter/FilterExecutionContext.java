/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Java.FLIRT.Interpreter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ExecutionResult;
import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTFilterModule;
import Composestar.Java.FLIRT.Env.RTMessage;

/**
 * @author Michiel Hendriks
 */
public class FilterExecutionContext
{
	/**
	 * The object in which the execution currently happens.
	 */
	protected ObjectManager objectManager;

	/**
	 * The current message
	 */
	protected RTMessage message;

	/**
	 * List of filter modules
	 */
	protected List<RTFilterModule> filterModules;

	/**
	 * The current execution status of filter modules
	 */
	protected Map<RTFilterModule, ExecutionResult> fmExecutionStatus;

	/**
	 * Filter module index
	 */
	protected int activeFMIndex = -1;

	/**
	 * The current flow of the message. Interpretation is influenced by this
	 * value.
	 */
	protected MessageFlow flow = MessageFlow.CONTINUE;

	/**
	 * The filter arguments, assigned when interpreting a filter
	 */
	protected FilterArguments filterArguments;

	/**
	 * List of actions enqueued to be executed on the return flow
	 */
	protected Queue<EnqueuedAction> returnActions;

	/**
	 * The current filter which is being executed.
	 */
	protected Filter currentFilter;

	/**
	 * The execution result of the current filter. Is initialized to UNSET when
	 * the current filter is set.
	 */
	protected ExecutionResult currentFilterResult;

	/**
	 * The matched filter element. Set to the matched filter elements, is null
	 * when no filter element matched.
	 */
	protected FilterElement matchedElement;

	/**
	 * If true, then this context is no longer used by the object manager
	 */
	protected boolean contextFree;

	/**
	 * @param man
	 * @param msg
	 */
	public FilterExecutionContext(ObjectManager man, RTMessage msg)
	{
		contextFree = false;
		objectManager = man;
		message = msg;
		filterModules = man.getFilterModules();
		fmExecutionStatus = new HashMap<RTFilterModule, ExecutionResult>();
		returnActions = new LinkedList<EnqueuedAction>();
	}

	/**
	 * Free the context and notify all waiters
	 */
	public void freeContext()
	{
		contextFree = true;
		notifyAll();
	}

	/**
	 * @return the contextFree
	 */
	public boolean isContextFree()
	{
		return contextFree;
	}

	/**
	 * @return the objectManager
	 */
	public ObjectManager getObjectManager()
	{
		return objectManager;
	}

	/**
	 * @return The current message
	 */
	public RTMessage getMessage()
	{
		return message;
	}

	/**
	 * Change the message flow
	 * 
	 * @param value
	 */
	public void setMessageFlow(MessageFlow value)
	{
		switch (flow)
		{
			case EXIT:
				if (flow != value)
				{
					throw new IllegalStateException("Message flow can not be changed when in EXIT");
				}
				break;
			case RETURN:
				if (flow == MessageFlow.CONTINUE)
				{
					throw new IllegalStateException("Message flow can not return to CONTINUE");
				}
				break;
			case CONTINUE:
			default:
				break;
		}
		flow = value;
	}

	/**
	 * @return The current message flow
	 */
	public MessageFlow getMessageFlow()
	{
		return flow;
	}

	/**
	 * @return The filter arguments, can be null
	 */
	public FilterArguments getFilterArguments()
	{
		return filterArguments;
	}

	/**
	 * Set the filter arguments
	 * 
	 * @param value
	 */
	public void setFilterArguments(FilterArguments value)
	{
		filterArguments = value;
	}

	/**
	 * @param value
	 */
	public void setMatchedElement(FilterElement value)
	{
		matchedElement = value;
	}

	/**
	 * @return
	 */
	public FilterElement getMatchedElement()
	{
		return matchedElement;
	}

	/**
	 * @param value
	 */
	public void setCurrentFilter(Filter value)
	{
		currentFilter = value;
		currentFilterResult = ExecutionResult.UNSET;
	}

	/**
	 * @return
	 */
	public Filter getCurrentFilter()
	{
		return currentFilter;
	}

	/**
	 * @return the currentFilterResult
	 */
	public ExecutionResult getCurrentFilterResult()
	{
		return currentFilterResult;
	}

	/**
	 * Set the filter result to either true or false.
	 * 
	 * @param result
	 */
	public void setCurrentFilterResult(boolean result)
	{
		currentFilterResult = ExecutionResult.fromBoolean(result);
	}

	/**
	 * @return The current active filter module. Is null when not filter
	 *         expression is executed or when the end has been reached.
	 */
	public RTFilterModule getCurrentFilterModule()
	{
		if (activeFMIndex >= 0 && activeFMIndex < filterModules.size())
		{
			return filterModules.get(activeFMIndex);
		}
		return null;
	}

	/**
	 * The current execution status of a filter module.
	 * 
	 * @param filterModule
	 * @return The current execution status of the filter module.
	 */
	public ExecutionResult getExecutionStatus(RTFilterModule filterModule)
	{
		ExecutionResult result = fmExecutionStatus.get(filterModule);
		if (result == null)
		{
			result = ExecutionResult.NOT_EXECUTED;
		}
		return result;
	}

	/**
	 * Update the execution status of the given filter module
	 * 
	 * @param filterModule
	 * @param status
	 */
	public void setExecutionStatus(RTFilterModule filterModule, ExecutionResult status)
	{
		fmExecutionStatus.put(filterModule, status);
	}

	/**
	 * Update the execution status of the current filter module
	 * 
	 * @param status
	 */
	public void setExecutionStatus(ExecutionResult status)
	{
		fmExecutionStatus.put(getCurrentFilterModule(), status);
	}

	/**
	 * @return The next filter expression. Returns null when there is no next
	 *         filter module.
	 */
	public FilterExpression getNextFilterExpression()
	{
		if (activeFMIndex < -1)
		{
			activeFMIndex = -1;
		}
		while (activeFMIndex < filterModules.size() - 1)
		{
			activeFMIndex++;
			RTFilterModule fm = filterModules.get(activeFMIndex);
			if (!canEvalFM(fm))
			{
				continue;
			}
			FilterExpression result = null;
			switch (message.getDirection())
			{
				case OUTGOING:
					result = fm.getOutputFilterExpression();
					break;
				case INCOMING:
				default:
					result = fm.getInputFilterExpression();
			}
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * @param fm
	 * @return True when this filter module should be executed
	 */
	protected boolean canEvalFM(RTFilterModule fm)
	{
		if (fm.getCondition() == null)
		{
			return true;
		}
		return MethodReferenceInterpreter.boolEval(fm.getCondition(), this);
	}

	/**
	 * Enqueue an action to be executed at a later stage
	 * 
	 * @param action
	 */
	public void addReturnAction(RTMessage matchedMessage, RTFilterAction action)
	{
		EnqueuedAction item = new EnqueuedAction();
		item.action = action;
		item.matchedMessage = matchedMessage;
		if (filterArguments != null)
		{
			item.arguments = new FilterArguments(filterArguments);
		}
		returnActions.add(item);
	}

	/**
	 * @return
	 */
	public Collection<EnqueuedAction> getReturnActions()
	{
		return Collections.unmodifiableCollection(returnActions);
	}

	/**
	 * @author Michiel Hendriks
	 */
	public static class EnqueuedAction
	{
		/**
		 * The message at the time it was matched
		 */
		public RTMessage matchedMessage;

		/**
		 * The filter arguments
		 */
		public FilterArguments arguments;

		/**
		 * The return action
		 */
		public RTFilterAction action;
	}
}
