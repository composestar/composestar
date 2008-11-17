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

import java.util.logging.Level;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * The base message receiver implementation
 * 
 * @author Michiel Hendriks
 */
public abstract class MessageReceiver implements Runnable
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME + ".MessageReceiver");

	/**
	 * The message queue
	 */
	private SyncBuffer<RTMessage> messageQueue;

	/**
	 * True if this receiver is busy processing a message
	 */
	private boolean working = false;

	/**
	 * Mutex used for locking
	 */
	private Object workingMutex = new Object();

	protected MessageReceiver()
	{
		messageQueue = new SyncBuffer<RTMessage>();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run()
	{
		// The message processing loop
		this.working = true;
		do
		{
			RTMessage msg = messageQueue.consume();
			Object reply = null;
			try
			{
				reply = receiveMessage(msg);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "An exception was thrown from within a filter.", e);
				// Debug.out(Debug.MODE_ERROR, "FLIRT",
				// "An exception was thrown from within a filter.");
				// Debug.out(Debug.MODE_ERROR, "FLIRT", "Message was `" +
				// msg.getSelector() + "' for target `"
				// + msg.getTarget() + "' from sender `" + msg.getSender() +
				// "'.");
				// Debug.out(Debug.MODE_ERROR, "FLIRT",
				// "Internal Compose* stack trace:");
				// e.printStackTrace();
				// This method is executed in a seperate thread and will die on
				// exceptions.
			}
			finally
			{
				msg.setResponse(reply);
			}

			if (this.messageQueue.isEmpty())
			{
				synchronized (workingMutex)
				{
					if (this.messageQueue.isEmpty())
					{
						this.working = false;
						return;
					}
				}
			}
		} while (true); // dowhile because of concurency
	}

	/**
	 * Start the message consuming thread when needed.
	 */
	public void notifyMessageConsumer()
	{
		synchronized (workingMutex)
		{
			if (this.working) return;
			this.working = true;
		}
		Thread child = new Thread(this, "MessageReceiver");
		child.start();
	}

	/**
	 * An incoming message, which should be processed by the input filters
	 * 
	 * @param sender cann be null in case of static context
	 * @param receiver
	 * @param msg
	 * @return The return value of the message, can be null for void methods
	 */
	public Object deliverIncomingMessage(CpsObject sender, CpsObject receiver, RTMessage msg)
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

		// add the message to the message queue
		messageQueue.produce(msg);

		// notify objectmanager that messagequeue is not empty
		notifyMessageConsumer();

		return msg.getReturnValue();
	}

	/**
	 * An outgoing message, which should be processed by the output filters
	 * 
	 * @param sender
	 * @param receiver
	 * @param msg
	 * @return The result of the message
	 */
	public Object deliverOutgoingMessage(CpsObject sender, CpsObject receiver, RTMessage msg)
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
		msg.setSelf(receiver); // TODO: verify this value, shouldn't this be
								// "sender" ?
		msg.setTarget(receiver);

		// add the message to the objectmanager's message queue
		messageQueue.produce(msg);

		// notify objectmanager that queue is not empty
		notifyMessageConsumer();

		return msg.getReturnValue();
	}

	/**
	 * A message which needs to be processed
	 * 
	 * @param msg
	 * @return
	 */
	protected abstract Object receiveMessage(RTMessage msg);
}
