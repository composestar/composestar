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

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Java.FLIRT.Annotations.CpsVariableType;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Env.ReifiedMessage;
import Composestar.Java.FLIRT.Env.ReifiedMessage.ReifiedMessageResult;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Interpreter.MessageFlow;

/**
 * @author Michiel Hendriks
 */
@FilterActionDef(name = FilterActionNames.META_ACTION, arguments = { "target", "selector" }, argumentTypes = {
		CpsVariableType.OBJECT, CpsVariableType.SELECTOR_OR_LIT })
public class MetaAction extends RTFilterAction
{
	protected ReifiedMessage reifiedMessage;

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
		RTCpsObject target = null;
		CpsSelector selector = null;

		CpsVariable var = context.getFilterArguments().get(PropertyNames.TARGET);
		if (var instanceof RTCpsObject)
		{
			target = (RTCpsObject) var;
		}
		if (target == null && matchedMessage.getTarget() instanceof RTCpsObject)
		{
			target = (RTCpsObject) matchedMessage.getTarget();
		}
		var = context.getFilterArguments().get(PropertyNames.SELECTOR);
		if (var instanceof CpsSelector)
		{
			selector = (CpsSelector) var;
		}
		else if (var instanceof CpsLiteral)
		{
			selector = new CpsSelectorImpl(((CpsLiteral) var).getLiteralValue());
		}
		if (selector == null)
		{
			selector = matchedMessage.getSelector();
		}

		if (target == null)
		{
			throw new IllegalStateException("No RTCpsObject target for AdviceAction");
		}

		reifiedMessage = new ReifiedMessage(context.getMessage(), target.getObject(), selector.getName());
		procMessage(context);
	}

	private void procMessage(FilterExecutionContext context)
	{
		Thread thread = new Thread(reifiedMessage, "MetaAction");
		thread.start();

		ReifiedMessageResult result = reifiedMessage.consume();
		switch (result.action)
		{
			case RESUME:
				// do nothing special, just continue
				break;
			case REPLY:
				context.setMessageFlow(MessageFlow.RETURN);
				break;
			case RESPOND:
				// TODO
				break;
			case PROCEED:
				// ... erm!?
				break;
		}
	}
}
