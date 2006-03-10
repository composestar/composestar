package Composestar.RuntimeCore.Utils;

import java.util.*;
/**
 * Summary description for Subthread.
 */
public class ChildThread extends Thread
{
	private Thread parent = null;
	private ChildRunable run = null;
	private boolean running = false;

	public ChildThread()
	{
		setDaemon(true);
	}

	private boolean sleeping = false;

	public void start()
	{
		parent = Thread.currentThread();
		if(!running)
		{
			running = true;
			super.start();
		}
		else
		{
			sleeping = false;
		}
	}

	public void setRunnable(ChildRunable run)
	{
		this.run = run;
	}

	public void run()
	{
		while(true) //We are a deamon thread so we will be killed automaticly
		{
			if(run != null)
			{
				run.run();
				ThreadPool.returnChildThread(this);
			}
			else
			{
				throw new RuntimeException("Starting ChildThread without setting runable");
			}
			sleeping = true;
			while(sleeping)
			{
				try
				{
					Thread.yield();
				}
				catch(Exception e)
				{
				}
			}
		}
	}
}