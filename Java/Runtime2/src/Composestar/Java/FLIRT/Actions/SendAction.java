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
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * Sends a message to the target, this changes the sender of the message to the
 * value of, at that time "self"
 * 
 * @author Michiel Hendriks
 */
@FilterActionDef(name = FilterActionNames.SEND_ACTION)
public class SendAction extends DispatchAction
{
	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Java.FLIRT.Actions.DispatchAction#dispatch(Composestar.Java
	 * .FLIRT.Env.ObjectManager,
	 * Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	protected Object dispatch(ObjectManager target, FilterExecutionContext context)
	{
		RTMessage message = (RTMessage) context.getMessage().send(context.getMessage().getSelf());
		return target.deliverIncomingMessage(message.getSender(), target, message);
	}
}
