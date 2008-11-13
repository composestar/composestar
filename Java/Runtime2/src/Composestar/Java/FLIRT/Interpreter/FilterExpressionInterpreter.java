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

import Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2Impl.FilterModules.SequentialFilterOper;
import Composestar.Java.FLIRT.FLIRTConstants;

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
		farg.addAll(filter.getArguments());
		context.setFilterArguments(farg);
		try
		{
			if (FEExpressionInterpreter.interpret(filter.getElementExpression(), context))
			{
				// TODO exec accept actions
			}
			else
			{
				// TODO exec reject actions
			}
		}
		finally
		{
			context.setFilterArguments(null);
		}
	}
}
