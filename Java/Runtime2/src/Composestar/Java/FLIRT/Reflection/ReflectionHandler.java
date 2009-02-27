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

import java.util.Stack;

import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * Manages the information used by the reflection classes. This class should not
 * be used by user code.
 * 
 * @author Michiel Hendriks
 */
public class ReflectionHandler
{
	/**
	 * @return The current runtime message.
	 */
	protected static final RTMessage getCurrentMessage()
	{
		return getCurrentContext().getMessage();
	}

	/**
	 * @return The current execution context
	 */
	protected static final FilterExecutionContext getCurrentContext()
	{
		return null;
	}

	/**
	 * Add a new execution context to the reflection handler
	 * 
	 * @param forThread
	 * @param ctx
	 */
	public static final void pushContext(Thread forThread, FilterExecutionContext ctx)
	{
	// getHandler(forThread).pushContext(ctx);
	}

	/**
	 * Remove a context from the reflection handler
	 * 
	 * @param forThread
	 * @param ctx
	 */
	public static final void popContext(Thread forThread, FilterExecutionContext ctx)
	{
	// getHandler(forThread).popContext(ctx);
	}

	/**
	 * @param forThread
	 * @return The reflection handler for this thread
	 */
	protected static ReflectionHandler getHandler(Thread forThread)
	{
		// TODO
		return null;
	}

	protected Stack<FilterExecutionContext> contexts;

	protected ReflectionHandler()
	{
		contexts = new Stack<FilterExecutionContext>();
	}

	/**
	 * @return The current context
	 */
	protected FilterExecutionContext currentContext()
	{
		return contexts.peek();
	}

	protected void pushContext(FilterExecutionContext ctx)
	{
		contexts.push(ctx);
	}

	protected void popContext(FilterExecutionContext ctx)
	{
		if (contexts.peek() != ctx)
		{
			throw new IllegalStateException("Current execution context is not the same as the popped execution context");
		}
		contexts.pop();
	}
}
