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

import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Java.FLIRT.MessageHandlingFacility;
import Composestar.Java.FLIRT.Utils.Invoker;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * A reigied message passed to methods who serve as target for the meta filters.
 * 
 * @author Michiel Hendriks
 */
public class ReifiedMessage extends JoinPointContext implements Runnable
{
	/**
	 * The status of this message
	 * 
	 * @author Michiel Hendriks
	 */
	public enum ReifiedMessageState
	{
		/**
		 * The message is reified (initial state)
		 */
		REIFIED,
		/**
		 * A respond action was executed
		 */
		RESPONDED,
		/**
		 * A proceed action was executed
		 */
		PROCEEDED,
		/**
		 * The the message execution is now decoupled from the main thread
		 * (final state)
		 */
		FORKED
	}

	/**
	 * Used to communicate actions to the waiting meta action
	 * 
	 * @author Michiel Hendriks
	 */
	public enum ReifiedMessageAction
	{
		/**
		 * A resume action was issued
		 */
		RESUME,
		/**
		 * A reply action was issued
		 */
		REPLY,
		/**
		 * A respond action was issued
		 */
		RESPOND,
		/**
		 * The message requested to proceed with the filters. This message
		 * itself issues the wait for a result.
		 */
		PROCEED
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
	 * This method is blocking until an action was taken by this message
	 * 
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
	public synchronized void respond()
	{
		state = ReifiedMessageState.RESPONDED;
		throw new UnsupportedOperationException("ReifiedMessage.respond() is not (yet) supported");
	}

	/**
	 * Return the message with the given return value
	 * 
	 * @param returnValue
	 */
	public synchronized void respond(Object returnValue)
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
			// issue a new sync buffer to wait for, this is needed in case of
			// multiple concurrently waiting actions (i.e. meta filter actions)
			// otherwise the action might awaken in the wrong order
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
		finish();
	}

	/**
	 * Finish the execution of the reified message. This is executed after the
	 * called method returns.
	 */
	private synchronized void finish()
	{
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

	/**
	 * Set the arguments of the message
	 * 
	 * @param values
	 */
	public void setArguments(Object[] values)
	{
		message.setArguments(values);
	}

	/**
	 * Set the target
	 * 
	 * @param obj
	 */
	public void setTarget(Object obj)
	{
		message.setTarget(MessageHandlingFacility.getRTCpsObject(obj));
	}

	/**
	 * Set the selector
	 * 
	 * @param selector
	 */
	public void setSelector(String selector)
	{
		message.setSelector(new CpsSelectorImpl(selector));
	}

	public void setSelector(CpsSelector selector)
	{
		message.setSelector(selector);
	}

	/**
	 * Change "self" the self of a message
	 * 
	 * @param obj
	 */
	public void setSelf(Object obj)
	{
		message.setSelf(MessageHandlingFacility.getRTCpsObject(obj));
	}

	/**
	 * Change the server of the message
	 * 
	 * @param obj
	 */
	public void setServer(Object obj)
	{
		message.setServer(MessageHandlingFacility.getRTCpsObject(obj));
	}

	/**
	 * @return The "inner" object. This is a special object that the runtime
	 *         interpreter knows how to handle. It can be used to set the new
	 *         target.
	 */
	public CpsObject getInner()
	{
		return message.getInner();
	}

	/**
	 * Communication structure between the reified message and the meta action.
	 * 
	 * @author Michiel Hendriks
	 */
	public static class ReifiedMessageResult
	{
		/**
		 * The action that was taken/requested
		 */
		public ReifiedMessageAction action;

		public ReifiedMessageResult(ReifiedMessageAction act)
		{
			action = act;
		}
	}
}
