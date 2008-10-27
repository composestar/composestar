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

package Composestar.Core.FIRE2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * @author Michiel Hendriks
 */
public class FireMessage implements CpsMessage
{
	protected FireMessage originator;

	/**
	 * Stores all properties
	 */
	protected Map<String, CpsVariable> properties;

	/**
	 * Create a new empty message
	 */
	public FireMessage()
	{
		properties = new HashMap<String, CpsVariable>();
	}

	/**
	 * Create a message based on an other message
	 * 
	 * @param from
	 */
	public FireMessage(FireMessage from)
	{
		this();
		properties.putAll(from.properties);
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
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsMessage#getProperty(java
	 * .lang.String)
	 */
	public CpsVariable getProperty(String name)
	{
		CpsVariable var = properties.get(name);
		if (var instanceof CanonProperty)
		{
			CpsVariable var2 = getProperty(name, new ArrayList<String>());
			if (var2 != null)
			{
				return var2;
			}
		}
		return var;
	}

	/**
	 * Look up the deferred value until a match is found
	 * 
	 * @param name
	 * @param visitedNames
	 * @return
	 */
	protected CpsVariable getProperty(String name, List<String> visitedNames)
	{
		CpsVariable var = properties.get(name);
		if (var instanceof CanonProperty)
		{
			CanonProperty prop = (CanonProperty) var;
			if (PropertyPrefix.MESSAGE == prop.getPrefix())
			{
				visitedNames.add(name);
				if (visitedNames.contains(prop.getBaseName()))
				{
					return null;
				}
				return getProperty(prop.getBaseName(), visitedNames);
			}
			return null;
		}
		return var;
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
		if (sender == null)
		{
			throw new NullPointerException("Sender can not be null");
		}
		FireMessage msg = new FireMessage(this);
		msg.properties.put(PropertyNames.SENDER, sender);
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		FireMessage result = new FireMessage(this);
		result.originator = this;
		return result;
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

	/**
	 * Special method used by the ExecutionModelExtractor to create the messages
	 * 
	 * @param name
	 * @param value
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public void specialSetProperty(String name, CpsVariable value) throws NullPointerException,
			IllegalArgumentException
	{
		if (value == null)
		{
			throw new NullPointerException("Value can not be null");
		}
		if (name == null)
		{
			throw new NullPointerException("Property name can not be null");
		}
		if (PropertyNames.SENDER.equals(name) && ((value instanceof CanonProperty) || (value instanceof CpsObject)))
		{
			properties.put(name, value);
			return;
		}
		else if (PropertyNames.SELF.equals(name) && (value instanceof CanonProperty))
		{
			properties.put(name, value);
			return;
		}
		else if (PropertyNames.TARGET.equals(name) && (value instanceof CanonProperty))
		{
			properties.put(name, value);
			return;
		}
		else if (PropertyNames.SERVER.equals(name) && (value instanceof CanonProperty))
		{
			properties.put(name, value);
			return;
		}
		else if (PropertyNames.SELECTOR.equals(name) && (value instanceof CanonProperty))
		{
			properties.put(name, value);
			return;
		}
		setProperty(name, value);
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return properties.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		FireMessage other = (FireMessage) obj;
		if (properties == null)
		{
			if (other.properties != null)
			{
				return false;
			}
		}
		else if (!properties.equals(other.properties))
		{
			return false;
		}
		return true;
	}
}
