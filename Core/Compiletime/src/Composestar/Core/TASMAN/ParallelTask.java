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

package Composestar.Core.TASMAN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Executes tasks in parallel. The execute method will return when all child
 * tasks were completed.
 * 
 * @author Michiel Hendriks
 */
public class ParallelTask extends TaskCollection
{
	/**
	 * System property. Overrides the number of processors for parallel
	 * execution
	 */
	public static final String NUM_PROCESSORS = "composestar.numprocessors";

	/**
	 * System property. Disables parallel processing if true
	 */
	public static final String NO_PARALLEL = "composestar.noparallel";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Manager.MODULE_NAME);

	protected int max = 0;

	protected int perprocessor = 0;

	protected Object semaphore = new Object();

	public ParallelTask()
	{
		super();
	}

	/**
	 * @param value the max to set
	 */
	public void setMax(int value)
	{
		max = value;
	}

	/**
	 * @param value the perprocessor to set
	 */
	public void setPerProcessor(int value)
	{
		perprocessor = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.TASMAN.Task#execute()
	 */
	@Override
	public void execute(Manager manager, CommonResources resources) throws ModuleException
	{
		updateMaxThreads();
		logger.info(String.format("Executing %d tasks in %d parallel threads", tasks.size(), max));

		int threadCnt = 0;
		RunnableTask[] runtasks = new RunnableTask[tasks.size()];
		for (Task task : tasks)
		{
			runtasks[threadCnt++] = new RunnableTask(task, manager, resources);
		}

		int numTasks = tasks.size();
		threadCnt = 0;
		ThreadGroup group = new ThreadGroup("TASMAN.ParallelTask");
		RunnableTask[] running = new RunnableTask[max];
		synchronized (semaphore)
		{
			for (int i = 0; i < max; i++)
			{
				running[i] = runtasks[threadCnt++];
				running[i].task.setProcessId(getProcessId() + ".t" + i);
				Thread t = new Thread(group, running[i]);
				logger.debug(String.format("Starting thread %d", i));
				t.start();
			}

			startThreads: while (threadCnt < numTasks)
			{
				for (int i = 0; i < max; i++)
				{
					if (running[i] == null || running[i].isDone())
					{
						running[i] = runtasks[threadCnt++];
						running[i].setProcessId(getProcessId() + ".t" + i);
						Thread t = new Thread(group, running[i]);
						logger.debug(String.format("Re-starting thread %d", i));
						t.start();
						continue startThreads;
					}
				}
				try
				{
					logger.debug(String.format("Waiting for a thread slot to open"));
					semaphore.wait();
				}
				catch (InterruptedException e)
				{
				}
			}

			boolean keepWaiting = true;
			waiting: while (keepWaiting)
			{
				for (int i = 0; i < max; i++)
				{
					if (running[i] != null && !running[i].isDone())
					{
						try
						{
							logger.debug(String.format("Waiting for task %d", i));
							semaphore.wait();
						}
						catch (InterruptedException e)
						{
						}
						continue waiting;
					}
				}
				keepWaiting = false;
			}
		}

		logger.debug(String.format("%d tasks executed", numTasks));

		List<ModuleException> exceptions = new ArrayList<ModuleException>();
		for (RunnableTask rt : runtasks)
		{
			if (rt.getException() != null)
			{
				exceptions.add(rt.getException());
			}
		}

		if (exceptions.size() == 1)
		{
			throw exceptions.get(0);
		}
		else if (exceptions.size() > 1)
		{
			throw new CompositeModuleException(exceptions);
		}
	}

	protected void updateMaxThreads()
	{
		if (perprocessor != 0)
		{
			int procs;
			if (System.getProperty(NUM_PROCESSORS) != null)
			{
				procs = Integer.getInteger(NUM_PROCESSORS);
			}
			else
			{
				procs = Runtime.getRuntime().availableProcessors();
			}
			max = perprocessor * procs;
		}
		if (max <= 0)
		{
			max = tasks.size();
		}
		if (System.getProperty(NO_PARALLEL) != null)
		{
			if (Boolean.getBoolean(NO_PARALLEL))
			{
				max = 1;
			}
		}
		if (max > tasks.size())
		{
			max = tasks.size();
		}
	}

	class RunnableTask implements Runnable
	{
		protected Task task;

		protected CommonResources resources;

		protected Manager manager;

		protected ModuleException exception;

		protected boolean done;

		public RunnableTask(Task fortask, Manager withManager, CommonResources withResources)
		{
			task = fortask;
			resources = withResources;
			manager = withManager;
		}

		public void setProcessId(String id)
		{
			if (task != null)
			{
				task.setProcessId(id);
			}
		}

		public void run()
		{
			done = false;
			try
			{
				task.execute(manager, resources);
			}
			catch (ModuleException e)
			{
				exception = e;
			}
			finally
			{
				synchronized (semaphore)
				{
					done = true;
					semaphore.notifyAll();
				}
			}
		}

		public boolean isDone()
		{
			return done;
		}

		public ModuleException getException()
		{
			return exception;
		}
	}

	public static class CompositeModuleException extends ModuleException
	{
		private static final long serialVersionUID = 6994368745737178001L;

		protected ModuleException[] exceptions;

		public CompositeModuleException(List<ModuleException> exes)
		{
			this(exes.toArray(new ModuleException[exes.size()]));
		}

		public CompositeModuleException(ModuleException[] exes)
		{
			super("Multiple module exceptions", Manager.MODULE_NAME);
			exceptions = exes;
			processModuleExceptions();
		}

		public ModuleException[] getExceptions()
		{
			return exceptions;
		}

		protected void processModuleExceptions()
		{
			Set<String> moduleNames = new HashSet<String>();
			Set<String> fileNames = new HashSet<String>();
			for (ModuleException mex : exceptions)
			{
				moduleNames.add(mex.getModule());
				fileNames.add(mex.getFilename());
			}
			if (moduleNames.size() == 1)
			{
				module = moduleNames.iterator().next();
			}
			if (fileNames.size() == 1)
			{
				errorLocationFilename = fileNames.iterator().next();
			}

		}

		@Override
		public String getMessage()
		{
			StringBuffer sb = new StringBuffer();
			for (ModuleException mex : exceptions)
			{
				if (sb.length() > 0)
				{
					sb.append("\n");
				}
				sb.append(mex.getMessage());
			}
			return sb.toString();
		}
	}
}
