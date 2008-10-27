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
import java.util.Set;

import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
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
	 * disjoint properties. For example
	 * 
	 * <pre>
	 * msg1.target = foo;
	 * msg2.target = foo;
	 * msg2.selector = bar;
	 * matchTo(msg1, msg2) == 1 (msg1 does not care about .selector)
	 * matchTo(msg2, msg1) == -1 (msg2 cares about .selector)
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
	 * @param other The message to compare this one to
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
			CpsVariable v2 = target.getProperty(prop);
			if (v1 != null && v2 == null)
			{
				--neg;
			}
			else if (v1 == null && v2 != null)
			{
				++pos;
			}
			else
			{
				// TODO: ...
				if (v1 instanceof CpsObject)
				{

				}
			}
		}
		return neg != 0 ? neg : pos;
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
	 * Complete the given message by adding missing properties from the source
	 * message
	 * 
	 * @param message
	 * @param source
	 * @return The names of the changed properties
	 * @throws NullPointerException Thrown when the message is null.
	 */
	public static Collection<String> complete(CpsMessage message, CpsMessage source) throws NullPointerException
	{
		if (message == null)
		{
			throw new NullPointerException("Message can not be null");
		}
		if (source == null)
		{
			return Collections.emptySet();
		}
		Collection<String> have = message.getAllProperties();
		Collection<String> want = source.getAllProperties();
		want.removeAll(have);
		for (String prop : want)
		{
			// TODO deferred values in source and message (cross resolve)
			CpsVariable var = source.getProperty(prop);
			if (var != null)
			{
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
