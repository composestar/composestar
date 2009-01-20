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

package Composestar.Core.FILTH2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.OrderingConstraint;
import Composestar.Core.FILTH2.Model.PhantomAction;
import Composestar.Core.FILTH2.Ordering.OrderGenerator;

/**
 * @author Michiel Hendriks
 */
public class OrderTest extends TestCase
{
	protected List<Action> actions;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		actions = new ArrayList<Action>();
		actions.add(new Action("A"));
		actions.add(new Action("B"));
		actions.add(new Action("C"));
		actions.add(new Action("D"));
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		actions = null;
	}

	public static int factorial(int i)
	{
		int result = 1;
		for (int n = 2; n <= i; n++)
		{
			result *= n;
		}
		return result;
	}

	public void testPermutation()
	{
		System.out.println("testPermutation");

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(factorial(actions.size()), results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testSingleConstraint()
	{
		System.out.println("testSingleConstraint");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(factorial(actions.size()) / 2, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testTwoConstraint()
	{
		System.out.println("testTwoConstraint");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(2)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(factorial(actions.size()) / 3, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testThreeConstraint()
	{
		System.out.println("testTwoConstraint");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(2)));
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(3)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(factorial(actions.size()) / 4, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testOnePossibleOrder()
	{
		System.out.println("testOnePossibleOrder");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));
		System.out.println(new OrderingConstraint(actions.get(1), actions.get(2)));
		System.out.println(new OrderingConstraint(actions.get(2), actions.get(3)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(1, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder()
	{
		System.out.println("testNoOrder");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));
		System.out.println(new OrderingConstraint(actions.get(1), actions.get(0)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(0, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder2()
	{
		System.out.println("testNoOrder2");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(1)));
		System.out.println(new OrderingConstraint(actions.get(1), actions.get(2)));
		System.out.println(new OrderingConstraint(actions.get(2), actions.get(0)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(0, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testNoOrder3()
	{
		System.out.println("testNoOrder3");
		System.out.println(new OrderingConstraint(actions.get(0), actions.get(0)));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(0, results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testPhantomActions()
	{
		System.out.println("testPhantomActions");
		System.out.println(new OrderingConstraint(actions.get(0), new PhantomAction("X")));
		System.out.println(new OrderingConstraint(actions.get(2), new PhantomAction("Y")));
		System.out.println(new OrderingConstraint(actions.get(3), new PhantomAction("Z")));

		Set<List<Action>> results = OrderGenerator.generate(actions, -1);
		assertEquals(factorial(actions.size()), results.size());
		for (List<Action> singleResult : results)
		{
			assertEquals(actions.size(), singleResult.size());
			System.out.println(singleResult);
		}
	}

	public void testAstronomical()
	{
		actions.add(new Action("E"));
		actions.add(new Action("F"));
		actions.add(new Action("G"));
		actions.add(new Action("H"));
		actions.add(new Action("I"));
		actions.add(new Action("J"));
		OrderGenerator.generate(actions, 10);
		OrderGenerator.generate(actions, 100);
		OrderGenerator.generate(actions, 1000);
		OrderGenerator.generate(actions, 10000);
	}
}
