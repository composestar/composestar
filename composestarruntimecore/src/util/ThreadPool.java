package Composestar.RuntimeCore.Utils;

import java.util.*;

/**
 * Summary description for ThreadPool.
 */
public class ThreadPool
{
	private ArrayList pool;

	private static ThreadPool instance = null;

	private ThreadPool()
	{
		pool = new ArrayList();
	}

	private static ThreadPool getInstance()
	{
		if(instance == null)
		{
			instance = new ThreadPool();
		}
		return instance;
	}

	private synchronized void add(ChildThread returned)
	{
		pool.add(returned);
	}

	private synchronized ChildThread get()
	{
		if(pool.isEmpty())
		{
			return new ChildThread();
		}
		else
		{
			ChildThread thread = (ChildThread) pool.get(pool.size() - 1);
			pool.remove(pool.size() - 1);
			return thread;
		}
	}

	public static void returnChildThread(ChildThread returned)
	{
		getInstance().add(returned);
	}

	public static ChildThread getChildThread()
	{
		return getInstance().get();
	}

	public static ChildThread getChildThread(ChildRunnable runnable)
	{
		ChildThread thread = getInstance().get();
		thread.setRunnable(runnable);
		return thread;
	}
}