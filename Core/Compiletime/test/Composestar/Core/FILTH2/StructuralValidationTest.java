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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.FILTH2.Model.Action;

/**
 * Performs validation of conflicts in the structural constraints.
 * 
 * @author Michiel Hendriks
 */
public class StructuralValidationTest extends TestCase
{
	protected List<Action> actions;

	protected Map<Constraint, Constraint> constraints;

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
		constraints = new HashMap<Constraint, Constraint>();
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
		constraints = null;
	}

	public void testConflict0()
	{
	// System.out.println("testConflict0");
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(0)),
	// null);
	// System.out.println(constraints.keySet());
	// assertFalse(StructuralValidation.isValid(constraints));
	}

	public void testConflict()
	{
	// System.out.println("testConflict");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// System.out.println(constraints.keySet());
	// assertFalse(StructuralValidation.isValid(constraints));
	}

	public void testConflict2()
	{
	// System.out.println("testConflict2");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new IncludeConstraint(actions.get(1), actions.get(2)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(2)),
	// null);
	// System.out.println(constraints.keySet());
	// assertFalse(StructuralValidation.isValid(constraints));
	}

	public void testConflict3()
	{
	// System.out.println("testConflict3");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new IncludeConstraint(actions.get(1), actions.get(2)),
	// null);
	// constraints.put(new IncludeConstraint(actions.get(2), actions.get(3)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(3)),
	// null);
	// System.out.println(constraints.keySet());
	// assertFalse(StructuralValidation.isValid(constraints));
	}

	public void testNoConflict()
	{
	// System.out.println("testNoConflict");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(2)),
	// null);
	// System.out.println(constraints.keySet());
	// assertTrue(StructuralValidation.isValid(constraints));
	}

	public void testNoConflict2()
	{
	// System.out.println("testNoConflict2");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new IncludeConstraint(actions.get(2), actions.get(3)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(2)),
	// null);
	// System.out.println(constraints.keySet());
	// assertTrue(StructuralValidation.isValid(constraints));
	}

	public void testNoConflict3()
	{
	// System.out.println("testNoConflict3");
	// constraints.put(new IncludeConstraint(actions.get(0), actions.get(1)),
	// null);
	// constraints.put(new IncludeConstraint(actions.get(2), actions.get(3)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(0), actions.get(2)),
	// null);
	// constraints.put(new ExcludeConstraint(actions.get(1), actions.get(3)),
	// null);
	// System.out.println(constraints.keySet());
	// assertTrue(StructuralValidation.isValid(constraints));
	}
}
