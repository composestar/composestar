package Composestar.RuntimeJava.Utils;

import java.util.HashMap;

import Composestar.RuntimeCore.Utils.*;

public class JavaChildThread implements ChildThread, Runnable
{

	private ChildRunnable running = null;

	private java.lang.Thread parent = null;

	private java.lang.Thread thisThread = null;

	private static HashMap threads = new HashMap();

	public JavaChildThread()
	{
		thisThread = new java.lang.Thread(this);
		addChildThread(thisThread, this);
		thisThread.start();
	}

	public ChildThread createNew()
	{
		return new JavaChildThread();
	}

	public ChildThread getCurrentChildThread()
	{
		ChildThread thread = getChildThread(java.lang.Thread.currentThread());
		if (thread == null)
		{
			return createNew();
		}
		return thread;
	}

	private synchronized ChildThread getChildThread(java.lang.Thread key)
	{
		return (ChildThread) threads.get(key);
	}

	private synchronized void addChildThread(java.lang.Thread key, ChildThread thread)
	{
		threads.put(key, thread);
	}

	public boolean equals(Object o)
	{
		if (o instanceof java.lang.Thread)
		{
			return ((java.lang.Thread) o).equals(thisThread);
		}
		else if (o instanceof JavaChildThread)
		{
			if (thisThread == null)
			{
				return false;
			}
			return thisThread.equals(((JavaChildThread) o).thisThread);
		}
		else
		{
			return false;
		}
	}

	public EntryPoint getEntryPoint()
	{
		// ..
		return null;
	}

	public java.lang.Thread getThread()
	{
		while (thisThread == null)
		{
			try
			{
				java.lang.Thread.yield();
			}
			catch (Exception e)
			{
			}
		}
		return thisThread;
	}

	public java.lang.Thread getParentThread()
	{
		while (parent == null)
		{
			try
			{
				java.lang.Thread.yield();
			}
			catch (Exception e)
			{
			}
		}
		return parent;
	}

	public boolean isSuspended()
	{
		return running == null || thisThread.isAlive();
	}

	public void resumeThread()
	{
		if (!isSuspended()) notify();
	}

	public void run()
	{
		thisThread = java.lang.Thread.currentThread();
		while (true) // We are a deamon thread so we will be killed
						// automatically
		{
			while (running == null)
			{
				try
				{
					synchronized (this)
					{
						wait();
					}
				}
				catch (InterruptedException e)
				{
				}
			}
			running.run();
			running = null;
			ThreadPool.returnChildThread(this);
		}

	}

	public void setRunnable(ChildRunnable run)
	{
		this.running = run;
	}

	public void start()
	{
		parent = java.lang.Thread.currentThread();
		thisThread.interrupt();
	}

	public void suspendThread()
	{
		while (true)
		{
			try
			{
				synchronized (this)
				{
					while (isSuspended())
						wait();
				}
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
