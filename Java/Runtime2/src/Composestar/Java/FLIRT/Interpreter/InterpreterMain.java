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

import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Java.FLIRT.FLIRTConstants;

/**
 * The main entry point for the interpreter
 * 
 * @author Michiel Hendriks
 */
public class InterpreterMain
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER);

	/**
	 * Entry point for the interpreter. This will execute the provided context
	 * 
	 * @param context
	 */
	public static void interpret(FilterExecutionContext context)
	{
		FilterExpression fex = context.getNextFilterExpression();
		while (fex != null)
		{
			// TODO execute
			if (context.getMessageFlow() != MessageFlow.CONTINUE)
			{
				break;
			}
			fex = context.getNextFilterExpression();
			FilterExpressionInterpreter.interpret(fex, context);
		}
		// TODO ...
	}
}
