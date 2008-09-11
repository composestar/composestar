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

package Composestar.Core.CpsRepository2.FilterElements;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class FilterElementTestBase extends RepositoryEntityTestBase
{
	protected FilterElement fe;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.FilterElement#setMatchingExpression(Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)}
	 * .
	 */
	public void testSetMatchingExpression()
	{
		MatchingExpression me = new DummyME();
		assertNull(fe.getMatchingExpression());
		fe.setMatchingExpression(me);
		assertSame(me, fe.getMatchingExpression());

		try
		{
			fe.setMatchingExpression(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.FilterElement#addAssignment(Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)}
	 * .
	 */
	public void testAddAssignment()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));
		assertNull(fe.addAssignment(arg1));
		assertSame(fe, arg1.getOwner());
		assertNull(fe.addAssignment(arg2));
		assertSame(arg1, fe.addAssignment(arg1));

		try
		{
			fe.addAssignment(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			fe.addAssignment(new DummyCA(null));
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.FilterElement#removeAssignment(Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)}
	 * .
	 */
	public void testRemoveAssignment()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		assertNull(fe.removeAssignment(arg1));
		fe.addAssignment(arg1);
		assertSame(arg1, fe.removeAssignment(arg1));
		assertNull(fe.removeAssignment(arg1));

		try
		{
			fe.removeAssignment(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}

		assertNull(fe.removeAssignment(new DummyCA(null)));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.FilterElement#getAssignment(java.lang.String)}
	 * .
	 */
	public void testGetAssignment()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));
		assertNull(fe.addAssignment(arg1));
		assertNull(fe.addAssignment(arg2));

		assertSame(arg1, fe.getAssignment(arg1.getProperty().getName()));
		assertSame(arg2, fe.getAssignment(arg2.getProperty().getName()));

		assertNull(fe.getAssignment(null));
		assertNull(fe.getAssignment(""));
		assertNull(fe.getAssignment(arg1.getProperty().getBaseName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterElements.FilterElement#getAssignments()}
	 * .
	 */
	public void testGetAssignments()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));

		assertNotNull(fe.getAssignments());
		assertEquals(0, fe.getAssignments().size());

		assertNull(fe.addAssignment(arg1));
		assertNull(fe.addAssignment(arg2));
		assertEquals(2, fe.getAssignments().size());

		assertSame(arg1, fe.addAssignment(arg1));
		assertEquals(2, fe.getAssignments().size());

		assertSame(arg1, fe.removeAssignment(arg1));
		assertEquals(1, fe.getAssignments().size());
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyME extends AbstractRepositoryEntity implements MatchingExpression
	{
		private static final long serialVersionUID = -1535590245370173614L;
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCA extends AbstractRepositoryEntity implements CanonAssignment
	{
		private static final long serialVersionUID = 6195576554022447957L;

		/**
		 * 
		 */
		protected CanonProperty var;

		/**
		 * @param cvar
		 */
		public DummyCA(CanonProperty cvar)
		{
			super();
			var = cvar;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonAssignment#getValue
		 * ()
		 */
		public CpsVariable getValue()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterElements.CanonAssignment#
		 * getVariable()
		 */
		public CanonProperty getProperty()
		{
			return var;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterElements.CanonAssignment#
		 * setCanonValue
		 * (Composestar.Core.CpsRepository2.FilterElements.CanonValue)
		 */
		public void setValue(CpsVariable value) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.FilterElements.CanonAssignment#
		 * setVariable
		 * (Composestar.Core.CpsRepository2.FilterElements.CanonVariable)
		 */
		public void setProperty(CanonProperty var) throws NullPointerException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
		 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
		 */
		public CanonAssignment newInstance(Instantiator instantiator) throws UnsupportedOperationException
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCV extends AbstractRepositoryEntity implements CanonProperty
	{
		private static final long serialVersionUID = -3488986961403986231L;

		/**
		 * 
		 */
		protected String name;

		/**
		 * 
		 */
		protected PropertyPrefix prefix;

		/**
		 * @param vname
		 */
		public DummyCV(String vname, PropertyPrefix pf)
		{
			super();
			name = vname;
			prefix = pf;
		}

		public DummyCV(String vname)
		{
			this(vname, PropertyPrefix.MESSAGE);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getBaseName
		 * ()
		 */
		public String getBaseName()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getName
		 * ()
		 */
		public String getName()
		{
			return prefix.toString() + "." + name;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.FilterElements.CanonVariable#getPrefix
		 * ()
		 */
		public PropertyPrefix getPrefix()
		{
			return prefix;
		}
	}

}
