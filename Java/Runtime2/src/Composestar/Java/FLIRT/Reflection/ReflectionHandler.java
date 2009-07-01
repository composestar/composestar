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

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * Manages the information used by the reflection classes. This class should not
 * be used by user code.
 * 
 * @author Michiel Hendriks
 */
public final class ReflectionHandler
{
	private static final RTMessage EMPTY_MESSAGE = new RTMessage();

	protected static final boolean hasMessage()
	{
		return hasMessage(Thread.currentThread());
	}

	protected static final boolean hasMessage(Thread forThread)
	{
		return getHandler(forThread).hasContext();
	}

	/**
	 * @return The current runtime message.
	 * @throws IllegalStateException Thrown when no message is currently being
	 *             processed
	 */
	protected static final RTMessage getCurrentMessage() throws IllegalStateException
	{
		if (!ReflectionHandler.hasMessage())
		{
			return EMPTY_MESSAGE;
		}
		return getCurrentContext().getMessage();
	}

	/**
	 * @return The current execution context
	 * @throws IllegalStateException Thrown when no message is currently being
	 *             processed
	 */
	protected static final FilterExecutionContext getCurrentContext() throws IllegalStateException
	{
		return getCurrentContext(Thread.currentThread());
	}

	/**
	 * @param currentThread
	 * @throws IllegalStateException Thrown when no message is currently being
	 *             processe
	 */
	protected static FilterExecutionContext getCurrentContext(Thread forThread)
	{
		return getHandler(forThread).currentContext();
	}

	/**
	 * Add a new execution context to the reflection handler
	 * 
	 * @param forThread
	 * @param ctx
	 */
	public static final void pushContext(Thread forThread, FilterExecutionContext ctx)
	{
		getHandler(forThread).pushContext(ctx);
	}

	/**
	 * Remove a context from the reflection handler
	 * 
	 * @param forThread
	 * @param ctx
	 */
	public static final void popContext(Thread forThread, FilterExecutionContext ctx)
	{
		getHandler(forThread).popContext(ctx);
	}

	/**
	 * Clone the handler for a new thread. This is used by the MetaAction to
	 * make sure the reifiedmessage can access the handler.
	 * 
	 * @param from
	 * @param to
	 */
	@SuppressWarnings("unchecked")
	public static final void cloneHanlder(Thread from, Thread to)
	{
		ReflectionHandler fromHandler = getHandler(from);
		ReflectionHandler toHandler = getHandler(to);
		toHandler.contexts = (Stack<FilterExecutionContext>) fromHandler.contexts.clone();
	}

	/**
	 * The current handlers
	 */
	protected static final Map<Thread, ReflectionHandler> HANDLERS = new WeakHashMap<Thread, ReflectionHandler>();

	/**
	 * @param forThread
	 * @return The reflection handler for this thread
	 */
	protected static ReflectionHandler getHandler(Thread forThread)
	{
		if (!HANDLERS.containsKey(forThread))
		{
			ReflectionHandler handler = new ReflectionHandler();
			HANDLERS.put(forThread, handler);
			return handler;
		}
		return HANDLERS.get(forThread);
	}

	/**
	 * The current stack of filter extecution contexts. A stack because multiple
	 * context can be present at the same time. But only a single context is
	 * currently active.
	 */
	protected Stack<FilterExecutionContext> contexts;

	protected ReflectionHandler()
	{
		contexts = new Stack<FilterExecutionContext>();
	}

	protected boolean hasContext()
	{
		return !contexts.isEmpty();
	}

	/**
	 * @return The current context
	 */
	protected FilterExecutionContext currentContext() throws IllegalStateException
	{
		try
		{
			return contexts.peek();
		}
		catch (EmptyStackException e)
		{
			throw new IllegalStateException("No message being interpreted");
		}
	}

	/**
	 * Add a new context to the stack. Should be called at the beginning of the
	 * contact handling.
	 * 
	 * @param ctx
	 */
	protected void pushContext(FilterExecutionContext ctx)
	{
		contexts.push(ctx);
	}

	/**
	 * Remove the current context. Should be called when the message interpreter
	 * is finished interpreting.
	 * 
	 * @param ctx
	 */
	protected void popContext(FilterExecutionContext ctx)
	{
		if (contexts.peek() != ctx)
		{
			throw new IllegalStateException("Current execution context is not the same as the popped execution context");
		}
		contexts.pop();
	}
}
