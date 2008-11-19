/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Java.FLIRT.Utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The message buffer
 * 
 * @author Michiel Hendriks
 */
public class SyncBuffer<T>
{
	protected Queue<T> queue;

	public SyncBuffer()
	{
		queue = new LinkedList<T>();
	}

	/**
	 * Add an item to the queue and notify consumers
	 * 
	 * @param o
	 */
	public synchronized void produce(T o)
	{
		queue.add(o);
		notifyAll();
	}

	/**
	 * Consume an object, this blocks until a result is ready
	 * 
	 * @return
	 */
	public synchronized T consume()
	{
		while (this.isEmpty())
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// resume
			}
			catch (Exception e)
			{
				// TODO error
				e.printStackTrace();
			}
		}
		T o = queue.poll();
		return o;
	}

	/**
	 * @return The number of elements in the queue
	 */
	public int size()
	{
		return queue.size();
	}

	/**
	 * @return True when the buffer is empty
	 */
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
}
