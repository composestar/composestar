package Composestar.Runtime.util;

import System.Collections.*;
/**
 * Summary description for Queue.
 */
public class Queue
{
	private System.Collections.Queue inner ;

	public Queue()
	{
		inner= new System.Collections.Queue();
	}

	public Object pop()
	{
		return inner.Dequeue();
	}

	public Object top()
	{
		return inner.Peek();
	}

	public void push (Object o)
	{
		inner.Enqueue(o);
	}

	public int size()
	{
		return inner.get_Count();
	}
}
