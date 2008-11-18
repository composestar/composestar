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
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Java.FLIRT.Annotations.CpsVariableType;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.JoinPointContext;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Utils.Invoker;

/**
 * @author Michiel Hendriks
 */
@FilterActionDef(name = FilterActionNames.ADVICE_ACTION, arguments = { PropertyNames.TARGET, PropertyNames.SELECTOR }, argumentTypes = {
		CpsVariableType.OBJECT, CpsVariableType.SELECTOR_OR_LIT })
public class AdviceAction extends RTFilterAction
{

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
		if (selector == null)
		{
			selector = matchedMessage.getSelector();
		}

		if (target == null)
		{
			throw new IllegalStateException("No RTCpsObject target for AdviceAction");
		}

		Object[] args = new Object[1];
		args[0] = new JoinPointContext(context.getMessage());
		Invoker.invoke(target.getObject(), selector.getName(), args);
	}
}
