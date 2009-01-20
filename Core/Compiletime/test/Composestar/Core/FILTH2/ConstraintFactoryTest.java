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

import junit.framework.TestCase;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.ConstraintFactory;
import Composestar.Core.FILTH2.Model.PhantomAction;
import Composestar.Core.FILTH2.Model.ConstraintFactory.ConstraintCreationException;

/**
 * Test the ConstraintFactory
 * 
 * @author Michiel Hendriks
 */
public class ConstraintFactoryTest extends TestCase
{
	protected Action a1, a2, a3;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		a1 = new PhantomAction("A1");
		a2 = new PhantomAction("A2");
		a3 = new PhantomAction("A3");
	}

	/**
	 * 
	 */
	public void testPreCreation()
	{
		try
		{
			ConstraintFactory.createConstraint("pre", a1, a2);
		}
		catch (ConstraintCreationException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testIncludeCreation()
	{
		try
		{
			ConstraintFactory.createConstraint("include", a1, a2);
		}
		catch (ConstraintCreationException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testExcludeCreation()
	{
		try
		{
			ConstraintFactory.createConstraint("exclude", a1, a2);
		}
		catch (ConstraintCreationException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testCondCreation()
	{
		try
		{
			ConstraintFactory.createConstraint("cond", a1, a2);
		}
		catch (ConstraintCreationException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testSkipCreation()
	{
		try
		{
			ConstraintFactory.createConstraint("skip", a1, a2, a3);
		}
		catch (ConstraintCreationException e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testIncorrectNumber()
	{
		try
		{
			ConstraintFactory.createConstraint("pre", a1);
			fail("ConstraintCreationException expected");
		}
		catch (ConstraintCreationException e)
		{
		}
	}

	/**
	 * 
	 */
	public void testIncorrectNumber2()
	{
		try
		{
			ConstraintFactory.createConstraint("pre", a1, a2, a3);
			fail("ConstraintCreationException expected");
		}
		catch (ConstraintCreationException e)
		{
		}
	}

	/**
	 * 
	 */
	public void testInvalidConstraint()
	{
		try
		{
			ConstraintFactory.createConstraint("this_constraint_does_not_exist", a1, a2, a3);
			fail("ConstraintCreationException expected");
		}
		catch (ConstraintCreationException e)
		{
		}
	}
}
