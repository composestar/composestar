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

import java.util.Collection;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * The join point context send to the advice actions
 * 
 * @author Michiel Hendriks
 */
public class JoinPointContext
{
	/**
	 * The real message
	 */
	protected RTMessage message;

	/**
	 * If true the message is locked in its state
	 */
	protected boolean locked;

	public JoinPointContext(RTMessage msg)
	{
		message = msg;
	}

	/**
	 * Lock the original message from being updated
	 */
	protected void lockMessage()
	{
		if (locked)
		{
			return;
		}
		locked = true;
		message = new RTMessage(message);
	}

	/**
	 * Resolve a CpsObject to a object instance
	 * 
	 * @param var
	 * @return
	 */
	protected Object getRealObject(CpsObject var)
	{
		if (var instanceof RTCpsObject)
		{
			return ((RTCpsObject) var).getObject();
		}
		return null;
	}

	/**
	 * @return The target object
	 */
	public Object getTarget()
	{
		return getRealObject(message.getTarget());
	}

	/**
	 * @return The selector value
	 */
	public String getSelector()
	{
		return message.getSelector().getName();
	}

	/**
	 * @return The sender of the message. This will be null when the message was
	 *         send from a static context
	 */
	public Object getSender()
	{
		return getRealObject(message.getSender());
	}

	/**
	 * @return The "self" object
	 */
	public Object getSelf()
	{
		return getRealObject(message.getSelf());
	}

	/**
	 * @return The "server" object
	 */
	public Object getServer()
	{
		return getRealObject(message.getSelf());
	}

	/**
	 * @return The arguments of the message
	 */
	public Object[] getArguments()
	{
		return message.getArguments();
	}

	/**
	 * Change an argument value
	 * 
	 * @param index
	 * @param value
	 * @throws IndexOutOfBoundsException When the index is out of bounds
	 */
	public void setArgument(int index, Object value) throws IndexOutOfBoundsException
	{
		message.setArgument(index, value);
	}

	/**
	 * @return The return value of the message, will usually be null until the
	 *         message has been dispatched as is on its return
	 */
	public Object getReturnValue()
	{
		return message.getReturnValue();
	}

	/**
	 * Set the return value
	 * 
	 * @param value
	 */
	public void setReturnValue(Object value)
	{
		message.setReturnValue(value);
	}

	/**
	 * @return The custom message properties
	 */
	public Collection<String> getCustomProperties()
	{
		return message.getCustomProperties();
	}

	/**
	 * @param name
	 * @return A custom message property
	 */
	public Object getCustomProperty(String name)
	{
		if (PropertyNames.SELECTOR.equals(name))
		{
			return getSelector();
		}
		else if (PropertyNames.TARGET.equals(name))
		{
			return getTarget();
		}
		else if (PropertyNames.SENDER.equals(name))
		{
			return getSender();
		}
		else if (PropertyNames.SELF.equals(name))
		{
			return getSelf();
		}
		else if (PropertyNames.SERVER.equals(name))
		{
			return getServer();
		}
		CpsVariable var = message.getProperty(name);
		if (var instanceof CpsArbitraryValue)
		{
			return ((CpsArbitraryValue) var).getValue();
		}
		return var;
	}

	/**
	 * Set a custom property
	 * 
	 * @param name
	 * @param value
	 * @throws IllegalArgumentException thrown when a predefined message
	 *             property is set (i.e. target, selector, etc.)
	 */
	public void setCustomProperty(String name, Object value) throws IllegalArgumentException
	{
		if (PropertyNames.SELECTOR.equals(name) || PropertyNames.TARGET.equals(name)
				|| PropertyNames.SENDER.equals(name) || PropertyNames.SELF.equals(name)
				|| PropertyNames.SERVER.equals(name))
		{
			throw new IllegalArgumentException("Can not set a predefine message property");
		}
		if (value == null)
		{
			return;
		}
		CpsVariable var;
		if (value instanceof CpsVariable)
		{
			var = (CpsVariable) value;
		}
		else
		{
			var = new CpsArbitraryValue(value);
		}
		message.setProperty(name, var);
	}
}
