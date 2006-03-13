package Composestar.RuntimeDotNET.Utils;

import System.Threading.Thread;
import System.Threading.ThreadStart;

import Composestar.RuntimeCore.Utils.*;
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
		return thisThreadJava;
	}

	public DotNETChildThread()
	{
	}
	
	public System.Threading.Thread getThisThread()
	{
		return thisThread;
	}

	public void start()
	{
		parent = System.Threading.Thread.get_CurrentThread();
		if(thisThread == null)
		{
			thisThread = new System.Threading.Thread(new ThreadStart(run));
			thisThread.set_IsBackground(true);
			thisThread.Start();
		}
		else
		{
			reanimate();
		}
	}

	public System.Threading.Thread getParentThread()
	{
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
			if(running != null)
			{
				running.run();
				running = null;
				ThreadPool.returnChildThread(this);
			}
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
		}
	}
}
