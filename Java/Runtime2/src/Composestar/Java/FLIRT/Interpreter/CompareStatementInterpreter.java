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

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Java.FLIRT.Interpreter.CompareOperators.AnnotationMatchingInterp;
import Composestar.Java.FLIRT.Interpreter.CompareOperators.CompareOperatorInterpreter;
import Composestar.Java.FLIRT.Interpreter.CompareOperators.CompatibilityMatchingInterp;
import Composestar.Java.FLIRT.Interpreter.CompareOperators.InstanceMatchingInterp;
import Composestar.Java.FLIRT.Interpreter.CompareOperators.SignatureMatchingInterp;

/**
 * Generic interpreter for compare statements
 * 
 * @author Michiel Hendriks
 */
public class CompareStatementInterpreter
{
	private static Map<Class<? extends MECompareStatement>, CompareOperatorInterpreter<?>> interps;

	static
	{
		interps = new HashMap<Class<? extends MECompareStatement>, CompareOperatorInterpreter<?>>();
		registerInterpreter(new InstanceMatchingInterp());
		registerInterpreter(new SignatureMatchingInterp());
		registerInterpreter(new AnnotationMatchingInterp());
		registerInterpreter(new CompatibilityMatchingInterp());
	}

	/**
	 * Register an interpreter
	 * 
	 * @param interp
	 */
	public static void registerInterpreter(CompareOperatorInterpreter<?> interp)
	{
		interps.put(interp.acceptsClass(), interp);
	}

	/**
	 * @param expr
	 * @param context
	 * @return
	 */
	public static boolean interpret(MECompareStatement expr, FilterExecutionContext context)
	{
		CompareOperatorInterpreter<? extends MECompareStatement> interp = interps.get(expr.getClass());
		if (interp != null)
		{
			return interp.interpret(expr, context);
		}
		return false;
	}
}
