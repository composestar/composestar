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

import java.util.List;

import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
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
	protected ObjectManager om;

	/**
	 * The current message
	 */
	protected RTMessage message;

	/**
	 * List of filter modules
	 */
	protected List<RTFilterModule> filterModules;

	/**
	 * Filter module index
	 */
	protected int fmIdx = -1;

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
	 * @param man
	 * @param msg
	 */
	public FilterExecutionContext(ObjectManager man, RTMessage msg)
	{
		om = man;
		message = msg;
		filterModules = man.getFilterModules();
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
	 * @return The current active filter module. Is null when not filter
	 *         expression is executed or when the end has been reached.
	 */
	public RTFilterModule getCurrentFilterModule()
	{
		if (fmIdx >= 0 && fmIdx < filterModules.size())
		{
			return filterModules.get(fmIdx);
		}
		return null;
	}

	/**
	 * @return The next filter expression. Returns null when there is no next
	 *         filter module.
	 */
	public FilterExpression getNextFilterExpression()
	{
		if (fmIdx < -1)
		{
			fmIdx = -1;
		}
		while (fmIdx < filterModules.size())
		{
			fmIdx++;
			RTFilterModule fm = filterModules.get(fmIdx);
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
}
