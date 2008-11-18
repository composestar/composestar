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

package Composestar.Java.FLIRT.Actions;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.LAMA.Type;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Annotations.CpsVariableType;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Interpreter.MessageFlow;

/**
 * Throws an exception when executed
 * 
 * @author Michiel Hendriks
 */
@FilterActionDef(name = FilterActionNames.ERROR_ACTION, messageChangeBehavior = MessageFlow.EXIT, arguments = {
		"exception", "message" }, argumentTypes = { CpsVariableType.TYPE_OR_LIT, CpsVariableType.LITERAL })
public class ErrorAction extends RTFilterAction
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".ErrorAction");

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Java.FLIRT.Actions.RTFilterAction#execute(Composestar.Java
	 * .FLIRT.Env.RTMessage,
	 * Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context)
	{
		context.setMessageFlow(MessageFlow.EXIT);
		CpsVariable var = context.getFilterArguments().get("message");
		String msg = null;
		if (var instanceof CpsLiteral)
		{
			msg = ((CpsLiteral) var).getLiteralValue();
		}
		var = context.getFilterArguments().get("exception");
		Class<?> excpClass = null;
		if (var instanceof CpsLiteral)
		{
			try
			{
				excpClass = Class.forName(((CpsLiteral) var).getLiteralValue());
			}
			catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, String.format("Exception type %s not found", ((CpsLiteral) var)
						.getLiteralValue()), e);
			}
		}
		else if (var instanceof CpsTypeProgramElement)
		{
			Type type = ((CpsTypeProgramElement) var).getTypeReference().getReference();
			try
			{
				excpClass = Class.forName(type.getFullName());
			}
			catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, String.format("Exception type %s not found", type.getFullName()), e);
			}
		}

		if (!RuntimeException.class.isAssignableFrom(excpClass))
		{
			excpClass = null;
			logger.log(Level.SEVERE, String.format("%s is not a runtime exception", excpClass));
		}

		if (excpClass == null)
		{
			excpClass = ErrorFilterException.class;
		}

		Class<? extends RuntimeException> rtec = excpClass.asSubclass(RuntimeException.class);
		RuntimeException exception = null;
		try
		{
			if (msg != null)
			{

				Constructor<? extends RuntimeException> ctor = rtec.getConstructor(String.class);
				exception = ctor.newInstance(msg);
			}
			else
			{
				exception = rtec.newInstance();
			}
			throw exception;
		}
		catch (Exception e)
		{
			throw new IllegalStateException(String.format(
					"Unable to create exception of type %s for the error filter action", rtec.getName()), e);
		}
	}
}
