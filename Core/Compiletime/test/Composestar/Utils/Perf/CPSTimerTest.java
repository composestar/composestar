/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Utils.Perf;

import junit.framework.TestCase;
import Composestar.Utils.Perf.Report.CPSTimerTree;

/**
 * @author Michiel Hendriks
 */
public class CPSTimerTest extends TestCase
{

	public void testSimple() throws InterruptedException
	{
		CPSTimer timer = CPSTimer.getTimer("Simple");
		timer.start("sleep 300ms");
		Thread.sleep(300);
		timer.stop();
		timer.start("nesting (should be logged after nester timer)");
		Thread.sleep(50);
		timer.start("nested timer");
		Thread.sleep(30);
		timer.stop();
		timer.stop();
	}

	public void testGetTimer()
	{
		CPSTimer.getTimer("Simple");
		CPSTimer.getTimer("Simple.Child");
		CPSTimer.getTimer("Simple.Child.Child");
		CPSTimer.getTimer("Simple.Child");
		CPSTimer.getTimer("Simple");
		CPSTimer.getTimer("Foo.Bar");
		CPSTimer.getTimer("Foo.Bar.Quux.Baz");
		CPSTimer.getTimer("One.Two.Three");
		CPSTimerTree root = CPSTimerTree.constructTree(CPSTimer.getTimers());
		root.getTimer(); // = null
	}

	public void testThreads()
	{
		CPSTimer timer = CPSTimer.getTimer("MultiThread", "Main");
		new Thread(new Runnable()
		{
			public void run()
			{
				CPSTimer timer = CPSTimer.getTimer("MultiThread.Thread1", "Stuff in thread 1");
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
				timer.stop();
			}

		}).start();
		new Thread(new Runnable()
		{
			public void run()
			{
				CPSTimer timer = CPSTimer.getTimer("MultiThread.Thread2", "Stuff in thread 2");
				try
				{
					Thread.sleep(30);
				}
				catch (InterruptedException e)
				{
				}
				timer.stop();
			}

		}).start();
		timer.stop();
	}

	public void testRacing()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				CPSTimer timer = CPSTimer.getTimer("MultiThread");
				try
				{
					for (int i = 0; i < 50; i++)
					{
						timer.start("thead 1: " + i);
						Thread.sleep(20);
						timer.stop();
					}
				}
				catch (InterruptedException e)
				{
				}
			}

		}).start();
		new Thread(new Runnable()
		{
			public void run()
			{
				CPSTimer timer = CPSTimer.getTimer("MultiThread");
				try
				{
					for (int i = 0; i < 75; i++)
					{
						timer.start("thead 2: " + i);
						Thread.sleep(15);
						timer.stop();
					}
				}
				catch (InterruptedException e)
				{
				}
			}

		}).start();
		new Thread(new Runnable()
		{
			public void run()
			{
				CPSTimer timer = CPSTimer.getTimer("MultiThread");
				try
				{
					for (int i = 0; i < 100; i++)
					{
						timer.start("thead 3: " + i);
						Thread.sleep(10);
						timer.stop();
					}
				}
				catch (InterruptedException e)
				{
				}
			}

		}).start();
	}

	public static void main(String[] args)
	{
		CPSTimerTest test = new CPSTimerTest();
		try
		{
			test.testSimple();
			test.testGetTimer();
			test.testThreads();
			test.testRacing();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
