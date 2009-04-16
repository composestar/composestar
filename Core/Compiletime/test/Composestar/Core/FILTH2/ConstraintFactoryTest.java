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
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.SISpec.SICondition;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConditionConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.ConstraintFactoryImpl;

/**
 * Test the ConstraintFactory
 * 
 * @author Michiel Hendriks
 */
public class ConstraintFactoryTest extends TestCase
{
	protected ConstraintValue a1, a2, a3;

	protected ConstraintFactoryImpl constraintFactory;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		constraintFactory = new ConstraintFactoryImpl();
		a1 = new DummyFMCV();
		a2 = new DummyFMCV();
		a3 = new DummyCCV();
	}

	/**
	 * 
	 */
	public void testPreCreation()
	{
		try
		{
			constraintFactory.createConstraint("pre", a1, a2);
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("include", a1, a2);
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("exclude", a1, a2);
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("cond", a1, a2);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testCondCreation2()
	{
		try
		{
			constraintFactory.createConstraint("cond", a3, a2);
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("skip", a1, a2, a3);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void testSkipCreation2()
	{
		try
		{
			constraintFactory.createConstraint("skip", a3, a2, a1);
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("pre", a1);
			fail("Exception expected");
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("pre", a1, a2, a3);
			fail("Exception expected");
		}
		catch (Exception e)
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
			constraintFactory.createConstraint("this_constraint_does_not_exist", a1, a2, a3);
			fail("Exception expected");
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * 
	 */
	public void testInvalidValueTypes()
	{
		try
		{
			constraintFactory.createConstraint("pre", a3, a1);
			constraintFactory.createConstraint("include", a3, a1);
			constraintFactory.createConstraint("exclude", a3, a1);
			fail("Exception expected");
		}
		catch (Exception e)
		{
		}
	}

	static class DummyFMCV implements FilterModuleConstraintValue
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SISpec.Constraints.
		 * FilterModuleConstraintValue#getFilterModuleReference()
		 */
		public FilterModuleReference getFilterModuleReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue
		 * #getStringValue()
		 */
		public String getStringValue()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
		 */
		public RepositoryEntity getOwner()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation
		 * ()
		 */
		public SourceInformation getSourceInformation()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
		 * .Core.CpsRepository2.RepositoryEntity)
		 */
		public RepositoryEntity setOwner(RepositoryEntity newOwner)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
		 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
		 */
		public void setSourceInformation(SourceInformation srcInfo)
		{}
	}

	static class DummyCCV implements ConditionConstraintValue
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.SISpec.Constraints.
		 * ConditionConstraintValue#getCondition()
		 */
		public SICondition getCondition()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue
		 * #getStringValue()
		 */
		public String getStringValue()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
		 */
		public RepositoryEntity getOwner()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation
		 * ()
		 */
		public SourceInformation getSourceInformation()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
		 * .Core.CpsRepository2.RepositoryEntity)
		 */
		public RepositoryEntity setOwner(RepositoryEntity newOwner)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
		 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
		 */
		public void setSourceInformation(SourceInformation srcInfo)
		{}
	}
}
