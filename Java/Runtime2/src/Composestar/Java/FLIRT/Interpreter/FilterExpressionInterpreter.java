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
public class FilterExpressionInterpreter
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".FilterExpr");

	/**
	 * Execute filter expressions
	 * 
	 * @param fex
	 * @param context
	 */
	public static void interpret(FilterExpression fex, FilterExecutionContext context)
	{
		if (fex == null)
		{
			return;
		}
		if (fex instanceof Filter)
		{
			interpretFilter((Filter) fex, context);
		}
		else if (fex instanceof BinaryFilterOperator)
		{
			interpretBinOper((BinaryFilterOperator) fex, context);
		}
		else
		{
			logger.severe(String.format("Unknown filter expression type: %s", fex.getClass().getName()));
		}
	}

	/**
	 * Execute binary filter operators
	 * 
	 * @param fex
	 * @param context
	 */
	public static void interpretBinOper(BinaryFilterOperator fex, FilterExecutionContext context)
	{
		if (fex instanceof SequentialFilterOper)
		{
			interpret(fex.getLHS(), context);
			if (context.getMessageFlow() == MessageFlow.CONTINUE)
			{
				interpret(fex.getRHS(), context);
			}
		}
		else
		{
			logger.severe(String.format("Unknown binary filter operator", fex.getClass().getName()));
		}
	}

	/**
	 * Execute a filter
	 * 
	 * @param filter
	 * @param context
	 */
	public static void interpretFilter(Filter filter, FilterExecutionContext context)
	{
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
			context.setFilterArguments(null);
		}
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
			context.addReturnAction(matchedMessage, act);
		}
		act = RTFilterActionFactory.createAction(onCall);
		if (act != null)
		{
			act.execute(matchedMessage, context);
		}
	}
}
