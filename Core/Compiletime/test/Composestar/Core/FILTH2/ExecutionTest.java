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

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ExecutionResult;
import Composestar.Core.FILTH2.Execution.Simulator;

/**
 * @author Michiel Hendriks
 */
public class ExecutionTest extends TestCase
{
	protected List<ConstraintValue> actions;

	protected Map<ConstraintValue, ExecutionResult> fakeResults;

	protected Simulator sim;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		// actions = new ArrayList<Action>();
		// actions.add(new Action("A"));
		// actions.add(new Action("B"));
		// actions.add(new Action("C"));
		// actions.add(new Action("D"));
		// sim = new Simulator();
		// fakeResults = new HashMap<Action, ExecutionResult>();
		// fakeResults.put(actions.get(0), ExecutionResult.FALSE);
		// fakeResults.put(actions.get(1), ExecutionResult.TRUE);
		// sim.setFakeResults(fakeResults);
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
		sim = null;
	}

	public void testNoControlConstraints()
	{
	// System.out.println("testNoControlConstraints");
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// }
	}

	public void testCondConstraints()
	{
	// System.out.println("testCondConstraints");
	// System.out.println(new CondConstraint(actions.get(1), actions.get(2)));
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// }
	}

	public void testCondPreConstraints()
	{
	// System.out.println("testCondPreConstraints");
	// System.out.println(new CondConstraint(actions.get(1), actions.get(2)));
	// System.out.println(new OrderingConstraint(actions.get(0),
	// actions.get(1)));
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// }
	}

	public void testSkipConstraints()
	{
	// System.out.println("testSkipConstraints");
	// System.out.println(new SkipConstraint(actions.get(1), actions.get(2),
	// actions.get(0)));
	// System.out.println(new OrderingConstraint(actions.get(0),
	// actions.get(1)));
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// }
	}

	public void testComboTT()
	{
	// System.out.println("testComboTT");
	// System.out.println(new OrderingConstraint(actions.get(1),
	// actions.get(2)));
	// System.out.println(new CondConstraint(actions.get(0), actions.get(1)));
	// Action resAct = new PhantomAction("R");
	// System.out.println(new SkipConstraint(actions.get(1), actions.get(3),
	// resAct));
	//
	// fakeResults.clear();
	// fakeResults.put(resAct, ExecutionResult.FALSE);
	//
	// fakeResults.put(actions.get(0), ExecutionResult.TRUE);
	// fakeResults.put(actions.get(1), ExecutionResult.TRUE);
	//
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// assertEquals(ExecutionResult.TRUE, sim.getResult(actions.get(0)));
	// assertEquals(ExecutionResult.TRUE, sim.getResult(actions.get(1)));
	// assertEquals(ExecutionResult.UNSET, sim.getResult(actions.get(2)));
	// assertEquals(ExecutionResult.FALSE, sim.getResult(actions.get(3)));
	// }
	}

	public void testComboTF()
	{
	// System.out.println("testComboTF");
	// System.out.println(new OrderingConstraint(actions.get(1),
	// actions.get(2)));
	// System.out.println(new CondConstraint(actions.get(0), actions.get(1)));
	// Action resAct = new PhantomAction("R");
	// System.out.println(new SkipConstraint(actions.get(1), actions.get(3),
	// resAct));
	//
	// fakeResults.clear();
	// fakeResults.put(resAct, ExecutionResult.FALSE);
	//
	// fakeResults.put(actions.get(0), ExecutionResult.TRUE);
	// fakeResults.put(actions.get(1), ExecutionResult.FALSE);
	//
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// assertEquals(ExecutionResult.TRUE, sim.getResult(actions.get(0)));
	// assertEquals(ExecutionResult.FALSE, sim.getResult(actions.get(1)));
	// assertEquals(ExecutionResult.UNSET, sim.getResult(actions.get(2)));
	// assertEquals(ExecutionResult.UNSET, sim.getResult(actions.get(3)));
	// }
	}

	public void testComboFF()
	{
	// System.out.println("testComboFF");
	// System.out.println(new OrderingConstraint(actions.get(1),
	// actions.get(2)));
	// System.out.println(new CondConstraint(actions.get(0), actions.get(1)));
	// Action resAct = new PhantomAction("R");
	// System.out.println(new SkipConstraint(actions.get(1), actions.get(3),
	// resAct));
	//
	// fakeResults.clear();
	// fakeResults.put(resAct, ExecutionResult.FALSE);
	//
	// fakeResults.put(actions.get(0), ExecutionResult.FALSE);
	// fakeResults.put(actions.get(1), ExecutionResult.FALSE);
	//
	// Set<List<Action>> results = OrderGenerator.generate(actions, -1);
	// for (List<Action> order : results)
	// {
	// System.out.println(order);
	// assertTrue(sim.simulate(order));
	// printExecution(order);
	// assertEquals(ExecutionResult.FALSE, sim.getResult(actions.get(0)));
	// assertEquals(ExecutionResult.NOT_EXECUTED,
	// sim.getResult(actions.get(1)));
	// assertEquals(ExecutionResult.NOT_EXECUTED,
	// sim.getResult(actions.get(2)));
	// assertEquals(ExecutionResult.UNSET, sim.getResult(actions.get(3)));
	// }
	}

	protected void printExecution(List<ConstraintValue> order)
	{
		System.out.print("[");
		int i = 0;
		for (ConstraintValue action : order)
		{
			if (i > 0)
			{
				System.out.print(", ");
			}
			i++;
			System.out.print(sim.getResult(action));
		}
		System.out.print("] -> ");
		System.out.println(sim.getExecuted());
	}
}
