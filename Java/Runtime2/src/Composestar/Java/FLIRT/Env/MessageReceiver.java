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

package Composestar.Java.FLIRT.Env;

import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Java.FLIRT.FLIRTConstants;

/**
 * The base message receiver implementation
 * 
 * @author Michiel Hendriks
 */
public abstract class MessageReceiver
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".MessageReceiver");

	protected MessageReceiver()
	{
		super();
	}

	/**
	 * An incoming message, which should be processed by the input filters
	 * 
	 * @param sender cann be null in case of static context
	 * @param receiver
	 * @param msg
	 * @return The return value of the message, can be null for void methods
	 */
	public Object deliverIncomingMessage(CpsObject sender, CpsObject receiver, RTMessage msg) throws Throwable
	{
		if (receiver instanceof ObjectManager)
		{
			msg.setInner(((ObjectManager) receiver).getInnerObject());
		}
		else
		{
			// shouldn't even be possible
			msg.setInner(receiver);
		}
		msg.setSelf(receiver);
		msg.setTarget(receiver);

		return receiveMessage(msg);
	}

	/**
	 * An outgoing message, which should be processed by the output filters
	 * 
	 * @param sender
	 * @param receiver
	 * @param msg
	 * @return The result of the message
	 */
	public Object deliverOutgoingMessage(CpsObject sender, CpsObject receiver, RTMessage msg) throws Throwable
	{
		if (sender instanceof ObjectManager)
		{
			msg.setInner(((ObjectManager) sender).getInnerObject());
		}
		else
		{
			// shouldn't even be possible
			msg.setInner(sender);
		}
		msg.setSelf(sender); // TODO: old implementation used receiver, validate
		// that this really should be sender
		msg.setTarget(receiver);

		// just deliver it
		return receiveMessage(msg);
	}

	/**
	 * A message which needs to be processed
	 * 
	 * @param msg
	 * @return
	 */
	protected abstract Object receiveMessage(RTMessage msg) throws Throwable;
}
