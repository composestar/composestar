package Composestar.RuntimeDotNET.Utils;

import System.Threading.Thread;
import System.Threading.ThreadStart;

import Composestar.RuntimeCore.Utils.*;
import java.util.*;

/**
 * Summary description for DotNETChildThread.
 */
public class DotNETChildThread implements ChildThread
{
	private System.Threading.Thread parent = null;
	private System.Threading.Thread thisThread = null;
	private java.lang.Thread thisThreadJava = null;
	private ChildRunnable running = null;
	
	public ChildThread createNew()
	{
		return new DotNETChildThread();
	}

	public java.lang.Thread getThread()
	{
		while(thisThreadJava == null)
		{
			try
			{
				java.lang.Thread.yield();
			}
			catch(Exception e)
			{
			}
		}
		return thisThreadJava;
	}

	public DotNETChildThread()
	{
		thisThread = new System.Threading.Thread(new ThreadStart(run));
		thisThread.set_IsBackground(true);
		addChildThread(thisThread,this);
		thisThread.Start();
	}
	
	public System.Threading.Thread getThisThread()
	{
		return thisThread;
	}


	public void suspendThread()
	{
		thisThread.Suspend();
	}

	public void resumeThread()
	{
		thisThread.Resume();
	}

	public boolean isSuspended()
	{
		return running == null || thisThread.get_IsAlive();
	}

	public void start()
	{
		parent = System.Threading.Thread.get_CurrentThread();
		reanimate();
	}

	public System.Threading.Thread getParentThread()
	{
		while(parent == null)
		{
			try
			{
				java.lang.Thread.yield();
			}
			catch(Exception e)
			{
			}
		}
		return parent;
	}

	public synchronized void reanimate()
	{
		thisThread.Interrupt();
	}

	public void setRunnable(ChildRunnable run)
	{
		this.running = run;
	}

	public void run()
	{
		thisThreadJava = java.lang.Thread.currentThread();
		while(true) //We are a deamon thread so we will be killed automaticly
		{
			while(running == null)
			{
				try
				{
					synchronized(this)
					{
						wait();
					}
				}
				catch(InterruptedException e)
				{
				}
			}
			running.run();
			running = null;
			ThreadPool.returnChildThread(this);
		}
	}

	private static HashMap threads = new HashMap();
	public ChildThread getCurrentChildThread()
	{
		ChildThread thread = getChildThread(System.Threading.Thread.get_CurrentThread());
		if(thread == null)
		{
			return createNew();
		}
		return thread;
	}

	private synchronized ChildThread getChildThread(System.Threading.Thread key)
	{
		return (ChildThread) threads.get(key);
	}

	private synchronized void addChildThread(System.Threading.Thread key, ChildThread thread)
	{
		threads.put(key,thread);
	}

	public boolean equals(Object o)
	{
		if(o instanceof java.lang.Thread)
		{
			return ((java.lang.Thread)o).equals(thisThreadJava);
		}
		else if(o instanceof Thread)
		{
			return ((Thread)o).equals(thisThread);
		}
		else if(o instanceof DotNETChildThread)
		{
			if(thisThread == null)
			{
				return false;
			}
			return thisThread.equals(((DotNETChildThread) o).thisThread);
		}
		else
		{
			return false;
		}
	}
}
