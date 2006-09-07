package Composestar.RuntimeDotNET.Utils;

import System.Threading.Thread;
import System.Threading.ThreadStart;
import System.Diagnostics.*;

import Composestar.RuntimeCore.Utils.*;
import java.util.*;
import java.io.*;

/**
 * Summary description for DotNETChildThread.
 */
public class DotNETChildThread implements ChildThread
{
	private System.Threading.Thread parent = null;
	private System.Threading.Thread thisThread = null;
	private ChildRunnable running = null;

	public EntryPoint getEntryPoint()
	{
		parent.Suspend();
		StackTrace st = new StackTrace(parent,true);
		for(int i = 0; i < st.get_FrameCount(); i++)
		{
			StackFrame frame = st.GetFrame(i);
			String type = frame.GetMethod().get_DeclaringType().get_FullName();
			type = type.toUpperCase();
			if(!(type.startsWith("COMPOSESTAR.") ||type.startsWith("SYSTEM.") || type.startsWith("COM.MS.VJSHARP."))){
				EntryPoint point = new EntryPoint(frame.GetFileName(),frame.GetFileLineNumber());
				parent.Resume();
				return point;
			}
		}
		parent.Resume();
		return new EntryPoint("Can't read stack");
	}

	public ChildThread createNew()
	{
		return new DotNETChildThread();
	}

	public DotNETChildThread()
	{
		thisThread = new System.Threading.Thread(new ThreadStart(run));
		thisThread.set_IsBackground(true);
		addChildThread(thisThread,this);
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

	private boolean started = false;
	public synchronized void start()
	{
		parent = Thread.get_CurrentThread();
		if(started)
		{
			thisThread.Interrupt();
		}
		else
		{
			thisThread.Start();
			started = true;
		}
	}

	public Thread getParentThread()
	{
		return parent;
	}

	public void reanimate()
	{
		thisThread.Interrupt();
	}

	public void setRunnable(ChildRunnable run)
	{
		this.running = run;
	}

	public void run()
	{
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

	private ChildThread getChildThread(System.Threading.Thread key)
	{
		synchronized(threads)
		{
			return (ChildThread) threads.get(key);
		}
	}

	private void addChildThread(System.Threading.Thread key, ChildThread thread)
	{
		synchronized(threads)
		{
			threads.put(key,thread);
		}
	}

	public boolean equals(Object o)
	{
		if(o instanceof java.lang.Thread)
		{
			throw new RuntimeException("Wrong thread type");
		}
		else if(o instanceof Thread)
		{
			return ((Thread)o).GetDomainID() == thisThread.GetDomainID();
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
