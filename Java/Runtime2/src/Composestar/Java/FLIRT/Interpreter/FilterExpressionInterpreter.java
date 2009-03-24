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

import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.PrimitiveFilterType;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.CanonAssignmentImpl;
import Composestar.Core.CpsRepository2Impl.FilterModules.SequentialFilterOper;
import Composestar.Core.FILTH2.Model.ExecutionResult;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Actions.RTFilterActionFactory;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTMessage;

/**
 * Filter expression interpreter
 * 
 * @author Michiel Hendriks
 */
public final class FilterExpressionInterpreter
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".FilterExpr");

	private FilterExpressionInterpreter()
	{}

	/**
	 * Execute filter expressions
	 * 
	 * @param fex
	 * @param context
	 */
	public static ExecutionResult interpret(FilterExpression fex, FilterExecutionContext context)
	{
		if (fex == null)
		{
			return ExecutionResult.UNSET;
		}
		if (fex instanceof Filter)
		{
			return interpretFilter((Filter) fex, context);
		}
		else if (fex instanceof BinaryFilterOperator)
		{
			return interpretBinOper((BinaryFilterOperator) fex, context);
		}
		else
		{
			logger.severe(String.format("Unknown filter expression type: %s", fex.getClass().getName()));
			return ExecutionResult.UNSET;
		}
	}

	/**
	 * Execute binary filter operators
	 * 
	 * @param fex
	 * @param context
	 */
	public static ExecutionResult interpretBinOper(BinaryFilterOperator fex, FilterExecutionContext context)
	{
		if (fex instanceof SequentialFilterOper)
		{
			ExecutionResult lhs = interpret(fex.getLHS(), context);
			if (context.getMessageFlow() == MessageFlow.CONTINUE)
			{
				ExecutionResult rhs = interpret(fex.getRHS(), context);
				// the result of ';' is an "&&" of all results up to the
				// return/exit of the message
				lhs = lhs.and(rhs);
			}
			return lhs;
		}
		else
		{
			logger.severe(String.format("Unknown binary filter operator", fex.getClass().getName()));
			return ExecutionResult.UNSET;
		}
	}

	/**
	 * Execute a filter
	 * 
	 * @param filter
	 * @param context
	 */
	public static ExecutionResult interpretFilter(Filter filter, FilterExecutionContext context)
	{
		context.setCurrentFilter(filter);
		context.setMatchedElement(null);
		FilterArguments farg = new FilterArguments();
		for (CanonAssignment asgn : filter.getArguments())
		{
			CanonAssignmentImpl arg = new CanonAssignmentImpl();
			arg.setProperty(asgn.getProperty());
			arg.setValue(getRTObject(asgn.getValue(), context));
			farg.add(arg);
		}
		context.setFilterArguments(farg);
		RTMessage matchedMessage = new RTMessage(context.getMessage());
		try
		{
			if (FEExpressionInterpreter.interpret(filter.getElementExpression(), context))
			{
				if (filter.getType() instanceof PrimitiveFilterType)
				{
					interpretPrimitiveFilterType(matchedMessage, (PrimitiveFilterType) filter.getType(), context, true);
				}
				else
				{
					logger.severe(String.format("Unhandled filter type class: %s", filter.getType().getClass()
							.getName()));
				}
			}
			else
			{
				if (filter.getType() instanceof PrimitiveFilterType)
				{
					interpretPrimitiveFilterType(matchedMessage, (PrimitiveFilterType) filter.getType(), context, false);
				}
				else
				{
					logger.severe(String.format("Unhandled filter type class: %s", filter.getType().getClass()
							.getName()));
				}
			}
		}
		finally
		{
			context.setCurrentFilter(null);
			context.setFilterArguments(null);
		}
		return context.getCurrentFilterResult();
	}

	/**
	 * Replaces FilterModuleVariable (i.e. internal/external) to the correct
	 * RTCpsObject
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static CpsVariable getRTObject(CpsVariable value, FilterExecutionContext context)
	{
		if (!(value instanceof FilterModuleVariable))
		{
			return value;
		}
		RTCpsObject result = context.getCurrentFilterModule().getMemberObject((FilterModuleVariable) value);
		if (result != null)
		{
			return result;
		}
		// TODO: error
		return value;
	}

	/**
	 * Execute the filter type actions
	 * 
	 * @param type
	 * @param context
	 * @param onCall
	 */
	public static void interpretPrimitiveFilterType(RTMessage matchedMessage, PrimitiveFilterType type,
			FilterExecutionContext context, boolean accepted)
	{
		FilterAction onCall;
		FilterAction onReturn;
		if (accepted)
		{
			onCall = type.getAcceptCallAction();
			onReturn = type.getAcceptReturnAction();
		}
		else
		{
			onCall = type.getRejectCallAction();
			onReturn = type.getRejectReturnAction();
		}
		RTFilterAction act = RTFilterActionFactory.createAction(onReturn);
		if (act != null)
		{
			logger.fine(String.format("Add return filter action %s", act.getClass().getName()));
			context.addReturnAction(matchedMessage, act);
		}
		act = RTFilterActionFactory.createAction(onCall);
		if (act != null)
		{
			logger.fine(String.format("Executing filter action %s", act.getClass().getName()));
			act.execute(matchedMessage, context);
		}
	}
}
