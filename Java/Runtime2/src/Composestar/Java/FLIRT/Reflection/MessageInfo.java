/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Java.FLIRT.Reflection;

import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Java.FLIRT.Env.JoinPointContext;

/**
 * Static access to the current message. It provides read-only access to certain
 * message properties. Its usage is mainly for targets of Dispatch like filter
 * actions.
 * 
 * @author Michiel Hendriks
 */
public final class MessageInfo
{
	/**
	 * @return The current inner object
	 */
	public static final Object getInner()
	{
		return JoinPointContext.getRealObject(ReflectionHandler.getCurrentMessage().getInner());
	}

	/**
	 * @return The sender of the message. This will be null in case of a static
	 *         sender
	 */
	public static final Object getSender()
	{
		return JoinPointContext.getRealObject(ReflectionHandler.getCurrentMessage().getSender());
	}

	/**
	 * @return The current argument values
	 */
	public static final Object[] getArguments()
	{
		return ReflectionHandler.getCurrentMessage().getArguments();
	}

	/**
	 * @return The server object
	 */
	public static final Object getServer()
	{
		return JoinPointContext.getRealObject(ReflectionHandler.getCurrentMessage().getServer());
	}

	/**
	 * @return The self object
	 */
	public static final Object getSelf()
	{
		return JoinPointContext.getRealObject(ReflectionHandler.getCurrentMessage().getSelf());
	}

	/**
	 * @return The selector object. This is a runtime representation of the
	 *         selector. It contains various information you might not be
	 *         interested in. Use {@link #getSelectorName()} to get just the
	 *         name.
	 * @see #getSelectorName()
	 */
	public static final CpsSelector getSelector()
	{
		return ReflectionHandler.getCurrentMessage().getSelector();
	}

	/**
	 * @return The selector name
	 */
	public static final String getSelectorName()
	{
		CpsSelector sel = getSelector();
		if (sel != null)
		{
			return sel.getName();
		}
		return null;
	}

	private MessageInfo()
	{}
}
