package Composestar.RuntimeCore.Utils;

import java.util.LinkedList;

public class SyncBuffer
{
	LinkedList queue = new LinkedList();

	public SyncBuffer()
	{
	}

	public synchronized void produce( Object o )
	{
		this.queue.addLast(o);
		this.notifyAll();
	}
	
	public synchronized Object consume()
	{
		while( this.isEmpty() )
		{
			try
			{
				wait();
			}
			catch(InterruptedException e)
			{
				//resume
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		Object o = this.queue.getFirst();
		this.queue.removeFirst();
		return o;
	}

	private int getSize()
	{
		return this.queue.size();
	}

	public boolean isEmpty()
	{
		return (this.queue.isEmpty());
	}
}
