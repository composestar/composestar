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

import Composestar.Java.FLIRT.Utils.Invoker;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * @author Michiel Hendriks
 */
public class ReifiedMessage extends JoinPointContext implements Runnable
{
	public enum ReifiedMessageState
	{
		REIFIED, RESPONDED, PROCEEDED, FORKED
	}

	public enum ReifiedMessageAction
	{
		RESUME, REPLY, RESPOND, PROCEED
	}

	/**
	 * The current state of this message
	 */
	protected ReifiedMessageState state = ReifiedMessageState.REIFIED;

	/**
	 * The object that receives this message
	 */
	protected Object actTarget;

	/**
	 * The selector of the method that will process this message
	 */
	protected String actSelector;

	/**
	 * Synchronization buffer
	 */
	protected SyncBuffer<ReifiedMessageResult> buffer;

	public ReifiedMessage(RTMessage message, Object actTarget, String actSelector)
	{
		super(message);
		buffer = new SyncBuffer<ReifiedMessageResult>();
		this.actTarget = actTarget;
		this.actSelector = actSelector;
	}

	/**
	 * @return The ReifiedMessage action
	 */
	public ReifiedMessageResult consume()
	{
		return buffer.consume();
	}

	/**
	 * Resume execution of the filters. Control will be returned immediately.
	 * Afterwards the message can not be changed.
	 */
	public synchronized void resume()
	{
		if (state == ReifiedMessageState.PROCEEDED)
		{
			message.setResponse(null);
		}
		state = ReifiedMessageState.FORKED;
		message = new RTMessage(message);
		lockMessage();
		buffer.produce(new ReifiedMessageResult(ReifiedMessageAction.RESUME));
	}

	/**
	 * Stop execution of the filters and return the message. The return value
	 * will be the previously set return value.
	 * 
	 * @see #reply(Object)
	 */
	public synchronized void reply()
	{
		lockMessage();
		buffer.produce(new ReifiedMessageResult(ReifiedMessageAction.REPLY));
	}

	/**
	 * Just like reply, except a returnValue is implicitly set before returning
	 * 
	 * @param returnValue
	 * @see #reply()
	 */
	public synchronized void reply(Object returnValue)
	{
		setReturnValue(returnValue);
		reply();
	}

	/**
	 * Return the message to the original sender. The message will still be
	 * processed by the other filters.
	 */
	public void respond()
	{
		state = ReifiedMessageState.RESPONDED;
		throw new UnsupportedOperationException("ReifiedMessage.respond() is not (yet) supported");
	}

	/**
	 * Return the message with the given return value
	 * 
	 * @param returnValue
	 */
	public void respond(Object returnValue)
	{
		setReturnValue(returnValue);
		respond();
	}

	/**
	 * Proceed execution of the filters. This call blocks until the filters have
	 * been processed. When control is returned the message can be inspected and
	 * the return value can be modified. Modifying other message properties is
	 * not possible at that time.
	 */
	public synchronized void proceed()
	{
		if (state == ReifiedMessageState.REIFIED || state == ReifiedMessageState.RESPONDED)
		{
			SyncBuffer<Object> buff = message.getResponseBuffer().wrap();
			// makes the filter processing proceed
			buffer.produce(new ReifiedMessageResult(ReifiedMessageAction.PROCEED));
			// wait for a result
			message.getResponse(buff);
			message.getResponseBuffer().unwrap();
			state = ReifiedMessageState.PROCEEDED;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Object[] args = new Object[1];
		args[0] = this;
		Invoker.invoke(actTarget, actSelector, args);

		switch (state)
		{
			case REIFIED:
				resume();
				break;
			case PROCEEDED:
				resume();
				break;
			case RESPONDED:
				proceed();
				resume();
				break;
			case FORKED:
				break;
			default:
				throw new IllegalStateException("ReifiedMessage is in an invalid state");
		}
	}

	// TODO set message properties (target, selector, ...?)

	/**
	 * Communication structure between the reified message and the meta action.
	 * 
	 * @author Michiel Hendriks
	 */
	public static class ReifiedMessageResult
	{
		public ReifiedMessageAction action;

		public ReifiedMessageResult(ReifiedMessageAction act)
		{
			action = act;
		}
	}
}
