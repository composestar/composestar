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

import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2Impl.FilterElements.AndMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;

/**
 * Interpret the matching expression
 * 
 * @author Michiel Hendriks
 */
public class MatchingExpressionInterpreter
{
	/**
	 * @param expr
	 * @param context
	 * @return True when the message is accepted
	 */
	public static boolean interpret(MatchingExpression expr, FilterExecutionContext context)
	{
		if (expr instanceof BinaryMEOperator)
		{
			return interpretBinOper((BinaryMEOperator) expr, context);
		}
		else if (expr instanceof UnaryMEOperator)
		{
			return interpretUnOper((UnaryMEOperator) expr, context);
		}
		else if (expr instanceof BinaryMEOperator)
		{
			return ((MELiteral) expr).getLiteralValue();
		}
		else if (expr instanceof MECondition)
		{
			return interpretCondition((MECondition) expr, context);
		}
		else if (expr instanceof MECompareStatement)
		{
			return CompareStatementInterpreter.interpret((MECompareStatement) expr, context);
		}
		else
		{
			// TODO error
		}
		return false;
	}

	/**
	 * @param expr
	 * @param context
	 * @return
	 */
	public static boolean interpretBinOper(BinaryMEOperator expr, FilterExecutionContext context)
	{
		if (expr instanceof AndMEOper)
		{
			if (!interpret(expr.getLHS(), context))
			{
				return false;
			}
			else interpret(expr.getRHS(), context);
		}
		else if (expr instanceof OrMEOper)
		{
			if (interpret(expr.getLHS(), context))
			{
				return true;
			}
			else interpret(expr.getRHS(), context);
		}
		else
		{
			// TODO error
		}
		return false;
	}

	/**
	 * @param expr
	 * @param context
	 * @return
	 */
	public static boolean interpretUnOper(UnaryMEOperator expr, FilterExecutionContext context)
	{
		if (expr instanceof NotMEOper)
		{
			return !interpret(expr.getOperand(), context);
		}
		else
		{
			// TODO error
		}
		return false;
	}

	/**
	 * @param expr
	 * @param context
	 * @return
	 */
	public static boolean interpretCondition(MECondition expr, FilterExecutionContext context)
	{
		return MethodReferenceInterpreter.boolEval(expr.getCondition().getMethodReference(), context);
	}
}
