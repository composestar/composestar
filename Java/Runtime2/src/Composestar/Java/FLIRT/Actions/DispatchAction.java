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

import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Java.FLIRT.MessageHandlingFacility;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Interpreter.MessageFlow;
import Composestar.Java.FLIRT.Utils.InvocationException;

/**
 * Dispatches the current message
 * 
 * @author Michiel Hendriks
 */
@FilterActionDef(name = FilterActionNames.DISPATCH_ACTION)
public class DispatchAction extends RTFilterAction
{
	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Java.FLIRT.Actions.FilterAction#execute(Composestar.Java.
	 * FLIRT.Env.RTMessage,
	 * Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context)
	{
		if (context.getMessageFlow() != MessageFlow.CONTINUE)
		{
			throw new IllegalStateException(String.format("Message flow is %s", context.getMessageFlow().toString()));
		}
		context.setMessageFlow(MessageFlow.RETURN);

		Object result = null;
		if (!(context.getMessage().getTarget() instanceof RTCpsObject))
		{
			throw new IllegalStateException("Target is not an instance of RTCpsObject");
		}

		CpsObject target = context.getMessage().getTarget();

		if (target.isInnerObject() || !(target instanceof ObjectManager))
		{
			try
			{
				result = MessageHandlingFacility.invokeMessageToInner(context.getMessage());
			}
			catch (Throwable e)
			{
				throw new InvocationException(e);
			}
		}
		else
		{
			result = dispatch((ObjectManager) target, context);
		}
		context.getMessage().setReturnValue(result);
	}

	/**
	 * Dispatch the message to a object manager for further processing
	 * 
	 * @param target
	 * @param context
	 * @return
	 */
	protected Object dispatch(ObjectManager target, FilterExecutionContext context)
	{
		RTMessage message = new RTMessage(context.getMessage());
		try
		{
			return target.deliverIncomingMessage(message.getSender(), target, message);
		}
		catch (Throwable e)
		{
			throw new InvocationException(e);
		}
	}
}
