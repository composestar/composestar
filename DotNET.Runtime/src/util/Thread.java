package java.lang;

import Composestar.RuntimeCore.Utils.*;
import java.util.*;
import java.io.*;
import java.lang.Runnable;

/**
 * Summary description for DotNETChildThread.
 */
public class Thread
{
	private System.Threading.Thread innerThread;
	private Runnable runable;

	public static void sleep(int l) throws InterruptedException
	{
		System.Threading.Thread.Sleep(l);
	}

	public Thread(System.Threading.Thread thread)
	{
		this.innerThread = thread;
	}

	public Thread(Runnable runable)
	{
		this.runable = runable;
	}

	public void start()
	{
		// Queue the task.
		System.Threading.ThreadPool.QueueUserWorkItem(new System.Threading.WaitCallback(run));
	}

	private void run(Object state)
	{
		innerThread = System.Threading.Thread.get_CurrentThread();
		runable .run();
	}

	public int hashCode()
	{
		while(innerThread == null)
		{
			System.Threading.Thread.Sleep(1);
		}
		return innerThread.hashCode();
	}

	public boolean equals(Object a)
	{
		if( a == null)
		{
			return false;
		}
		else if(this == a) //Cheap optimalisation
		{
			return true;
		}

		while(innerThread == null)
		{
			System.Threading.Thread.Sleep(1);
		}

		if(a instanceof Thread)
		{
			while(((Thread)a).innerThread == null)
			{
				System.Threading.Thread.Sleep(1);
			}
			return innerThread.equals(((Thread)a).innerThread);
		}
		else
		{
			return innerThread.equals(a);
		}
	}

	public static Thread currentThread()
	{
		return new Thread(System.Threading.Thread.get_CurrentThread());
	}
}
