package Composestar.RuntimeJava.Utils;

import java.util.HashMap;

import Composestar.RuntimeCore.Utils.*;

public class JavaChildThread extends Thread implements ChildThread, Runnable 
{

	private ChildRunnable running = null;

	private java.lang.Thread parent = null;

	private static HashMap threads = new HashMap();

	public JavaChildThread()
	{
		synchronized(threads){
			threads.put(this,this);
		}
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

	public EntryPoint getEntryPoint()
	{
		// ..
		return null;
	}

	public java.lang.Thread getThread()
	{
		return this;
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
		return false;
	}

	public void resumeThread()
	{
	}

	public void run()
	{
		running.run();
	}

	public void setRunnable(ChildRunnable run)
	{
		this.running = run;
	}


	public void suspendThread()
	{
	}
}
