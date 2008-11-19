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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Java.FLIRT.Utils.ResponseBuffer;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * A runtime message
 * 
 * @author Michiel Hendriks
 */
/**
 * @author Michiel Hendriks
 */
public class RTMessage implements CpsMessage
{
	/**
	 * Stores all properties
	 */
	protected Map<String, CpsVariable> properties;

	/**
	 * The inner object
	 */
	protected CpsObject inner;

	/**
	 * The direction the message is traveling in
	 */
	protected MessageDirection direction = MessageDirection.OUTGOING;

	/**
	 * The message state
	 */
	protected EnumSet<MessageState> state;

	/**
	 * The arguments passed on the method call
	 */
	protected Object[] args;

	/**
	 * The return value of the message
	 */
	protected Object returnValue;

	/**
	 * Synchronization buffer. This responsebuffer stuff could be simplified.
	 */
	protected ResponseBuffer responseBuffer;

	public RTMessage()
	{
		responseBuffer = new ResponseBuffer();
		properties = new HashMap<String, CpsVariable>();
		args = new Object[0];
	}

	public RTMessage(RTMessage copyFrom)
	{
		this();
		properties.putAll(copyFrom.properties);
		inner = copyFrom.inner;
		args = Arrays.copyOf(copyFrom.args, copyFrom.args.length);
		direction = copyFrom.direction;
		state = copyFrom.state;
		returnValue = copyFrom.returnValue;
	}

	public RTMessage(CpsObject sender)
	{
		this();
		if (sender != null)
		{
			properties.put(PropertyNames.SENDER, sender);
		}
	}

	public RTMessage(CpsObject sender, RTMessage copyFrom)
	{
		this(copyFrom);
		if (sender != null)
		{
			properties.put(PropertyNames.SENDER, sender);
		}
	}

	/**
	 * Set the message direction
	 * 
	 * @param value
	 */
	public void setDirection(MessageDirection value)
	{
		direction = value;
	}

	/**
	 * @return The message direction
	 */
	public MessageDirection getDirection()
	{
		return direction;
	}

	/**
	 * @param value
	 */
	public void setState(EnumSet<MessageState> value)
	{
		state = value;
	}

	/**
	 * @param value
	 */
	public void setState(MessageState value)
	{
		state = EnumSet.of(value);
	}

	/**
	 * @param value
	 * @return True if the given state is in the message
	 */
	public boolean hasState(MessageState value)
	{
		return state != null && state.contains(value);
	}

	/**
	 * @return The arguments
	 */
	public Object[] getArguments()
	{
		return Arrays.copyOf(args, args.length);
	}

	/**
	 * Set the arguments
	 * 
	 * @param value
	 */
	public void setArguments(Object[] value)
	{
		args = Arrays.copyOf(value, value.length);
	}

	/**
	 * @param index
	 * @param value
	 * @throws IndexOutOfBoundsException Thrown when the index is not within the
	 *             bounds
	 */
	public void setArgument(int index, Object value) throws IndexOutOfBoundsException
	{
		if (index < 0 || index > args.length)
		{
			throw new IndexOutOfBoundsException(String.format("Index out of bounds: %d. Argument length: %d", index,
					args.length));
		}
		args[index] = value;
	}

	/**
	 * @return The return value of the message
	 */
	public Object getReturnValue()
	{
		return returnValue;
	}

	/**
	 * Set the return value
	 * 
	 * @param value
	 */
	public void setReturnValue(Object value)
	{
		returnValue = value;
	}

	/**
	 * @return The response buffer for this message
	 */
	public ResponseBuffer getResponseBuffer()
	{
		return responseBuffer;
	}

	/**
	 * Set the response value for the message
	 * 
	 * @param value
	 */
	public void setResponse(Object value)
	{
		responseBuffer.produce(value);
	}

	/**
	 * @return The response value of the message
	 */
	public Object getResponse()
	{
		return responseBuffer.consume();
	}

	/**
	 * @param buff
	 * @return
	 */
	public Object getResponse(SyncBuffer<Object> buff)
	{
		if (buff == null)
		{
			return responseBuffer.consume();
		}
		else
		{
			return responseBuffer.consume(buff);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getAllProperties()
	 */
	public Collection<String> getAllProperties()
	{
		return Collections.unmodifiableCollection(properties.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getCustomProperties
	 * ()
	 */
	public Collection<String> getCustomProperties()
	{
		Set<String> props = new HashSet<String>(properties.keySet());
		props.remove(PropertyNames.SELECTOR);
		props.remove(PropertyNames.TARGET);
		props.remove(PropertyNames.SENDER);
		props.remove(PropertyNames.SELF);
		props.remove(PropertyNames.SERVER);
		return Collections.unmodifiableCollection(props);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getInner()
	 */
	public CpsObject getInner()
	{
		return inner;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getProperty(java
	 * .lang.String)
	 */
	public CpsVariable getProperty(String name)
	{
		return properties.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getSelector()
	 */
	public CpsSelector getSelector()
	{
		CpsVariable var = getProperty(PropertyNames.SELECTOR);
		if (var instanceof CpsSelector)
		{
			return (CpsSelector) var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getSelf()
	 */
	public CpsObject getSelf()
	{
		CpsVariable var = getProperty(PropertyNames.SELF);
		if (var instanceof CpsObject)
		{
			return (CpsObject) var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getSender()
	 */
	public CpsObject getSender()
	{
		CpsVariable var = getProperty(PropertyNames.SENDER);
		if (var instanceof CpsObject)
		{
			return (CpsObject) var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getServer()
	 */
	public CpsObject getServer()
	{
		CpsVariable var = getProperty(PropertyNames.SERVER);
		if (var instanceof CpsObject)
		{
			return (CpsObject) var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getTarget()
	 */
	public CpsObject getTarget()
	{
		CpsVariable var = getProperty(PropertyNames.TARGET);
		if (var instanceof CpsObject)
		{
			return (CpsObject) var;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#send(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsObject)
	 */
	public CpsMessage send(CpsObject sender) throws NullPointerException
	{
		RTMessage newmsg = new RTMessage(sender, this);
		newmsg.setDirection(MessageDirection.INCOMING);
		return newmsg;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setInner(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsObject)
	 */
	public void setInner(CpsObject value) throws NullPointerException
	{
		inner = value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setProperty(java
	 * .lang.String, Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public void setProperty(String name, CpsVariable value) throws NullPointerException, IllegalArgumentException
	{
		if (value == null)
		{
			throw new NullPointerException("Value can not be null");
		}
		if (name == null)
		{
			throw new NullPointerException("Property name can not be null");
		}
		if (PropertyNames.SENDER.equals(name))
		{
			throw new IllegalArgumentException("Sender can not be set, use the send() method");
		}
		else if (PropertyNames.SELF.equals(name) && !(value instanceof CpsObject))
		{
			throw new IllegalArgumentException("Self must be an CpsObject");
		}
		else if (PropertyNames.TARGET.equals(name) && !(value instanceof CpsObject))
		{
			throw new IllegalArgumentException("Target must be an CpsObject");
		}
		else if (PropertyNames.SERVER.equals(name) && !(value instanceof CpsObject))
		{
			throw new IllegalArgumentException("Server must be an CpsObject");
		}
		else if (PropertyNames.SELECTOR.equals(name) && !(value instanceof CpsSelector))
		{
			throw new IllegalArgumentException("Selector must be an CpsSelector");
		}
		properties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setSelector(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsSelector)
	 */
	public void setSelector(CpsSelector value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException("Selector can not be null");
		}
		properties.put(PropertyNames.SELECTOR, value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setSelf(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsObject)
	 */
	public void setSelf(CpsObject value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException("Self can not be null");
		}
		properties.put(PropertyNames.SELF, value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setServer(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsObject)
	 */
	public void setServer(CpsObject value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException("Server can not be null");
		}
		properties.put(PropertyNames.SERVER, value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#setTarget(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsObject)
	 */
	public void setTarget(CpsObject value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException("Target can not be null");
		}
		properties.put(PropertyNames.TARGET, value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
}
