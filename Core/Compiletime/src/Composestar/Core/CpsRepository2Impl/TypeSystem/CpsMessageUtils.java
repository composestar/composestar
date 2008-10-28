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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
public final class CpsMessageUtils
{
	private CpsMessageUtils()
	{}

	/**
	 * Compares both messages so see to what degree they are compatible. When 0
	 * is returned both message have equals values. Otherwise it returns the
	 * number of properties that do not match. It returns a negative number for
	 * the number of conflicting properties, otherwise a positive number of
	 * disjoint properties in the first message. For example:
	 * 
	 * <pre>
	 * msg1.target = foo;
	 * msg2.target = foo;
	 * msg2.selector = bar;
	 * 
	 * matchTo(msg1, msg2) == -1 
	 *  (msg1 does not have a selector, but msg2 does, msg2 is more restrictive)
	 * matchTo(msg2, msg1) == 1
	 * 	(msg2 does have a selector, msg1 doesn't care about the selector, 
	 *  msg1 is less restrictive)
	 * 
	 * msg1.target = foo;
	 * msg2.target = bar;
	 * matchTo(msg1, msg2) == -1 (targets do not match)
	 * </pre>
	 * 
	 * Note: this method does not have the same behavior as
	 * {@link Comparable#compareTo(Object)}
	 * 
	 * @param target The current message to match
	 * @param other The message to compare the current message to.
	 * @return 0 when the messages are equal, >0 when the other message matches
	 *         to a certain degree, <0 when the other message conflicts with
	 *         certain properties in this message. A result closer to 0 is
	 *         better.
	 * @throws NullPointerException when either message is null
	 */
	public static int compare(CpsMessage target, CpsMessage other) throws NullPointerException
	{
		int neg = 0;
		int pos = 0;
		Set<String> props = new HashSet<String>();
		props.addAll(target.getAllProperties());
		props.addAll(other.getAllProperties());
		for (String prop : props)
		{
			CpsVariable v1 = target.getProperty(prop);
			CpsVariable v2 = other.getProperty(prop);
			if (v2 instanceof CanonProperty)
			{
				if (PropertyPrefix.MESSAGE == ((CanonProperty) v2).getPrefix())
				{
					v2 = target.getProperty(((CanonProperty) v2).getBaseName());
				}
				else if (PropertyNames.INNER.equals(((CanonProperty) v2).getName()))
				{
					v2 = target.getInner();
				}
				else
				{
					// TODO error?
				}
			}
			if (v1 == null && v2 != null)
			{
				// other has a value set, thus more restrictive
				--neg;
			}
			else if (v1 != null && v2 == null)
			{
				// other is less restrictive
				++pos;
			}
			else if (v1 == v2)
			{
				// same object, thus match
				continue;
			}
			else if (!expectedType(prop).isInstance(v2))
			{
				--neg;
			}
			else if (!expectedType(prop).isInstance(v1))
			{
				// this shouldn't even be possible
				--neg;
			}
			else
			{
				// compare value 2 with value 1
				if (!v1.equals(v2) && !v2.equals(v1))
				{
					--neg;
				}
			}
		}
		return neg != 0 ? neg : pos;
	}

	/**
	 * Returns the expected property type for a given message property. Returns
	 * a specific type for the default message properties. And CpsVariable for
	 * custom message properties.
	 * 
	 * @param propertyName
	 * @return
	 */
	public static final Class<? extends CpsVariable> expectedType(String propertyName)
	{
		if (PropertyNames.SELECTOR.equals(propertyName))
		{
			return CpsSelector.class;
		}
		else if (PropertyNames.TARGET.equals(propertyName) || PropertyNames.SELF.equals(propertyName)
				|| PropertyNames.SENDER.equals(propertyName) || PropertyNames.SERVER.equals(propertyName))
		{
			return CpsObject.class;
		}
		return CpsVariable.class;
	}

	/**
	 * Returns the most fitting message from a list
	 * 
	 * @param message the message to compare the message in the list from
	 * @param list the list of possible candidates
	 * @return the message that matches the input message the most, could be
	 *         null if there are only conflicts
	 * @throws NullPointerException Thrown when the message is null.
	 */
	public static CpsMessage getClosestMatch(CpsMessage message, Set<CpsMessage> list) throws NullPointerException
	{
		if (message == null)
		{
			throw new NullPointerException("Message can not be null");
		}
		if (list == null)
		{
			return null;
		}
		CpsMessage result = null;
		int bestScore = Integer.MAX_VALUE;
		for (CpsMessage other : list)
		{
			int curScore = compare(message, other);
			if (curScore < 0)
			{
				continue;
			}
			else if (curScore == 0)
			{
				return result;
			}
			else if (curScore < bestScore)
			{
				bestScore = curScore;
				result = other;
			}
		}
		return result;
	}

	/**
	 * Update the given message with the values from the source message
	 * 
	 * @param message
	 * @param source
	 * @return The names of the changed properties
	 * @throws NullPointerException Thrown when the message is null.
	 */
	public static Collection<String> update(CpsMessage message, CpsMessage source) throws NullPointerException
	{
		if (message == null)
		{
			throw new NullPointerException("Message can not be null");
		}
		if (source == null)
		{
			return Collections.emptySet();
		}
		Collection<String> want = new HashSet<String>(source.getAllProperties());
		Iterator<String> it = want.iterator();
		while (it.hasNext())
		{
			String prop = it.next();
			CpsVariable var = source.getProperty(prop);
			if (var instanceof CanonProperty)
			{
				if (PropertyPrefix.MESSAGE == ((CanonProperty) var).getPrefix())
				{
					var = message.getProperty(((CanonProperty) var).getBaseName());
				}
				else if (PropertyNames.INNER.endsWith(((CanonProperty) var).getName()))
				{
					var = message.getInner();
				}
				else
				{
					var = null;
					// TODO: error
				}
			}
			if (var != null)
			{
				CpsVariable old = message.getProperty(prop);
				if (old != null)
				{
					if (old.compatible(var) || var.compatible(old))
					{
						it.remove();
						continue;
					}
				}
				try
				{
					message.setProperty(prop, var);
				}
				catch (IllegalArgumentException e)
				{
					// TODO proper handling
					e.printStackTrace();
				}
			}
		}
		return want;
	}
}
