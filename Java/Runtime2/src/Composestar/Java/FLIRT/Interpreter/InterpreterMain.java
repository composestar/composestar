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
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext.EnqueuedAction;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * The main entry point for the interpreter
 * 
 * @author Michiel Hendriks
 */
public final class InterpreterMain
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER);

	private InterpreterMain()
	{}

	/**
	 * Entry point for the interpreter. This will execute the provided context
	 * 
	 * @param context
	 */
	public static void interpret(FilterExecutionContext context)
	{
		// make sure we're wating for the right buffer to be filled
		SyncBuffer<Object> reponseBuffer = context.getMessage().getResponseBuffer().wrap();
		try
		{
			FilterExpression fex = context.getNextFilterExpression();
			while (fex != null)
			{
				if (context.getMessageFlow() != MessageFlow.CONTINUE)
				{
					break;
				}
				FilterExpressionInterpreter.interpret(fex, context);
				fex = context.getNextFilterExpression();
			}
			if (context.getMessageFlow() == MessageFlow.EXIT)
			{
				return;
			}
			// execute return actions
			for (EnqueuedAction act : context.getReturnActions())
			{
				if (act.action == null)
				{
					continue;
				}
				logger.fine(String.format("Executing return filter action %s", act.action.getClass().getName()));
				context.setFilterArguments(act.arguments);
				act.action.execute(act.matchedMessage, context);
				if (context.getMessageFlow() == MessageFlow.EXIT)
				{
					return;
				}
			}
			if (context.getMessageFlow() == MessageFlow.CONTINUE)
			{
				logger.warning("Message is still 'continue' at the end of interpretation");
			}
		}
		finally
		{
			finish(reponseBuffer, context);
		}
	}

	/**
	 * Finish the interpreter by syncing up
	 * 
	 * @param reponseBuffer
	 * @param context
	 */
	private static void finish(SyncBuffer<Object> reponseBuffer, FilterExecutionContext context)
	{
		context.getMessage().setResponse(null);
		context.getMessage().getResponse(reponseBuffer);
		context.getMessage().getResponseBuffer().unwrap();
	}
}
