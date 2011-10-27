/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2011 University of Twente.
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.ValueMatching;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 *
 * @author arjan
 */
public class ValueMatchingInterp extends CompareOperatorInterpreter<ValueMatching>
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".MatchValue");

	private Map<Thread, Integer> currentMatchOper = new HashMap<Thread, Integer>(); 
	
	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter#acceptsClass()
	 */
	@Override
	public Class<ValueMatching> acceptsClass()
	{
		return ValueMatching.class;
	}
	
	@Override
	public boolean interpret(MECompareStatement expr, FilterExecutionContext context)
	{
		ValueMatching vMatch = (ValueMatching) expr;
		
		currentMatchOper.put(Thread.currentThread(), vMatch.getCompType());
		
		boolean result = super.interpret(expr, context);
		
		// Remove current Match Oper:
		currentMatchOper.remove(Thread.currentThread());
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter
	 * #matches(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable,
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	@Override
	public boolean matches(CpsVariable lhs, CpsVariable rhs, FilterExecutionContext context)
	{
		if (lhs instanceof CpsValue && rhs instanceof CpsValue){
			return compare ((CpsValue) lhs, (CpsValue) rhs);
		}
		
		return false;
	}

	/**
	 * @param value
	 * @param value2
	 * @return
	 */
	private boolean compare(CpsValue cpsVal1, CpsValue cpsVal2)
	{
		double value1 = cpsVal1.getValue();
		double value2 = cpsVal2.getValue();
		
		switch(currentMatchOper.get(Thread.currentThread())){
			case ValueMatching.GEQ:
				return value1 >= value2;
			case ValueMatching.GT:
				return value1 > value2;
			case ValueMatching.LEQ:
				return value1 <= value2;
			case ValueMatching.LT:
				return value1 < value2;
			default:
				return false;
		}
	}
}