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
 * $Id: InterpreterMain.java 4536 2008-12-10 21:38:16Z elmuerte $
 */

package Composestar.Java.FLIRT.Interpreter;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext.EnqueuedAction;
import Composestar.Java.FLIRT.Reflection.ReflectionHandler;
import Composestar.Java.FLIRT.Utils.SyncBuffer;

/**
 * (EXPERIMENTAL) A threaded interpreter, this should open the possibility of
 * ReifiedMessage.resume().
 * 
 * @author Michiel Hendriks
 */
public final class ThreadedInterpreter extends Thread
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER);

	/**
	 * An interpreter per base thread
	 */
	private static final ThreadLocal<ThreadedInterpreter> interpreterThread = new ThreadLocal<ThreadedInterpreter>()
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		@Override
		protected ThreadedInterpreter initialValue()
		{
			ThreadedInterpreter thread = new ThreadedInterpreter(Thread.currentThread());
			threadLocals.put(thread, this);
			return thread;
		}
	};

	/**
	 * Mapping from interpreter to thread local, so that the interpreter can be
	 * unlicked from the thread when the interpreter forks.
	 */
	private static final WeakHashMap<ThreadedInterpreter, ThreadLocal<ThreadedInterpreter>> threadLocals =
			new WeakHashMap<ThreadedInterpreter, ThreadLocal<ThreadedInterpreter>>();

	/**
	 * Entry point for the interpreter. This will execute the provided context
	 * 
	 * @param context
	 */
	public static void interpret(FilterExecutionContext context) throws Throwable
	{
		ThreadedInterpreter thread = getInterpreterThread();
		ReflectionHandler.pushContext(thread, context);
		thread.start(context);
		thread.waitForResult();
		ReflectionHandler.popContext(thread, context);
		thread.finish();
	}

	/**
	 * @return The interpreter thread for this thread
	 */
	private static ThreadedInterpreter getInterpreterThread()
	{
		return interpreterThread.get();
	}

	/**
	 * Clear the threadlocal with this interpreter assigned to it. This is
	 * needed when the interpreter is forked.
	 * 
	 * @param thisThread
	 */
	private static void clearThreadLocal(ThreadedInterpreter thisThread)
	{
		ThreadLocal<ThreadedInterpreter> tl = threadLocals.get(thisThread);
		tl.remove();
	}

	/**
	 * The exception that was thrown during interpretation. It should be
	 * rethrown by the main method.
	 */
	private Throwable thrownException;

	/**
	 * The context to interpret
	 */
	private FilterExecutionContext context;

	/**
	 * Flag to check for an already processing interpreter
	 */
	private boolean isRunning;

	/**
	 * Reference to the parent thread, used for life management
	 */
	private WeakReference<Thread> parentThread;

	/**
	 * Sync object for the workload for this thread
	 */
	private Object workSync;

	/**
	 * Sync object for the interpreter
	 */
	private Object interpSync;

	private ThreadedInterpreter(Thread thread)
	{
		super(FLIRTConstants.INTERPRETER);
		parentThread = new WeakReference<Thread>(thread);
		workSync = new Object();
		interpSync = new Object();
	}

	private void start(FilterExecutionContext ctx) throws InterruptedException
	{
		if (isRunning)
		{
			throw new IllegalStateException("Interpreter thread is already executing a message.");
		}
		isRunning = true;
		context = ctx;
		if (!isAlive())
		{
			start();
		}
		else
		{
			wakeupForWork();
		}
	}

	/**
	 * Notify the waiting threads
	 */
	private void wakeupForResult()
	{
		synchronized (interpSync)
		{
			interpSync.notify();
		}
	}

	/**
	 * Wait till the interpreter is finished
	 * 
	 * @throws InterruptedException
	 */
	private synchronized void waitForResult() throws InterruptedException
	{
		synchronized (interpSync)
		{
			if (isRunning)
			{
				interpSync.wait();
			}
		}
	}

	/**
	 * Finish interpretation. Clean up local variables and rethrow the thrown
	 * exception
	 * 
	 * @throws Throwable
	 */
	private void finish() throws Throwable
	{
		if (isRunning)
		{
			throw new IllegalStateException("Message interpreation has not finished");
		}
		Throwable throwMe = thrownException;
		thrownException = null;
		if (throwMe != null)
		{
			throw throwMe;
		}
	}

	/**
	 * Fork the interpreter thread
	 */
	public void forkInterpreter()
	{
		clearThreadLocal(this);
		parentThread.clear();
		isRunning = false;
		wakeupForResult();
	}

	private void interpret()
	{
		isRunning = true;
		// make sure we're wating for the right buffer to be filled
		SyncBuffer<Object> reponseBuffer = context.getMessage().getResponseBuffer().wrap();
		try
		{
			interpretInner();
		}
		catch (Throwable t)
		{
			thrownException = t;
		}
		finally
		{
			context.getMessage().setResponse(null);
			context.getMessage().getResponse(reponseBuffer);
			context.getMessage().getResponseBuffer().unwrap();
			context = null;
			isRunning = false;
			wakeupForResult();
		}
	}

	private void interpretInner()
	{
		FilterExpression fex = context.getNextFilterExpression();
		while (fex != null)
		{
			if (context.getMessageFlow() != MessageFlow.CONTINUE)
			{
				break;
			}
			FilterExpressionInterpreter.interpret(fex, context);
			fex = context.getNextFilterExpression();
		}
		if (context.getMessageFlow() == MessageFlow.EXIT)
		{
			return;
		}
		// execute return actions
		for (EnqueuedAction act : context.getReturnActions())
		{
			if (act.action == null)
			{
				continue;
			}
			logger.fine(String.format("Executing return filter action %s", act.action.getClass().getName()));
			context.setFilterArguments(act.arguments);
			act.action.execute(act.matchedMessage, context);
			if (context.getMessageFlow() == MessageFlow.EXIT)
			{
				return;
			}
		}
		if (context.getMessageFlow() == MessageFlow.CONTINUE)
		{
			logger.warning("Message is still 'continue' at the end of interpretation");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		while (parentThread.get() != null)
		{
			if (!parentThread.get().isAlive())
			{
				return;
			}
			if (context != null)
			{
				interpret();
			}
			else
			{
				try
				{
					waitForWork(100);
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		}
	}

	private void wakeupForWork()
	{
		synchronized (workSync)
		{
			workSync.notify();
		}
	}

	private void waitForWork(long time) throws InterruptedException
	{
		synchronized (workSync)
		{
			workSync.wait(time);
		}
	}
}
