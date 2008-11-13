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

package Composestar.Java.FLIRT.Interpreter.CompareOperators;

import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Java.FLIRT.Interpreter.FEExpressionInterpreter;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * Interface for all compare operator interpreters.
 * 
 * @author Michiel Hendriks
 */
public abstract class CompareOperatorInterpreter<T extends MECompareStatement>
{
	/**
	 * Returns true if the compare operator statement is true within the given
	 * context
	 * 
	 * @param expr
	 * @param context
	 * @return
	 */
	public boolean interpret(MECompareStatement expr, FilterExecutionContext context)
	{
		return matches(acceptsClass().cast(expr), context);
	}

	/**
	 * @return the class this interpreter accepts
	 */
	public abstract Class<T> acceptsClass();

	/**
	 * Implement this method
	 * 
	 * @param expr
	 * @param context
	 * @return
	 */
	public boolean matches(T expr, FilterExecutionContext context)
	{
		CpsVariable lhs = FEExpressionInterpreter.getValue(expr.getLHS(), context.getMessage(), null);
		if (lhs == null)
		{
			return false;
		}
		for (CpsVariable rhs : expr.getRHS())
		{
			if ((rhs != null) && matches(lhs, rhs, context))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Matches two variables.
	 * 
	 * @param lhs This should be either an CpsSelector or an CpsObject, other
	 *            values are currently not allowed on the left hand side of
	 *            compare statements
	 * @param rhs
	 * @return true when they match according to the rules.
	 */
	public abstract boolean matches(CpsVariable lhs, CpsVariable rhs, FilterExecutionContext context);
}
