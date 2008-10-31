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

package Composestar.Core.CpsRepository2.FilterModules;

import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public abstract class FilterTestBase extends QualifiedRepositoryEntityTestBase
{
	protected Filter filter;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#setType(Composestar.Core.Config.FilterType)}
	 * .
	 */
	public void testSetType()
	{
		FilterType ft = new DummyFT();
		assertNull(filter.getType());
		filter.setType(ft);
		assertSame(ft, filter.getType());
		try
		{
			filter.setType(null);
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#addArgument(Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)}
	 * .
	 */
	public void testAddArgument()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));
		assertNull(filter.addArgument(arg1));
		assertSame(filter, arg1.getOwner());
		assertNull(filter.addArgument(arg2));
		assertSame(arg1, filter.addArgument(arg1));

		try
		{
			filter.addArgument(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			filter.addArgument(new DummyCA(null));
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
		try
		{
			filter.addArgument(new DummyCA(new DummyCV("yyy", PropertyPrefix.MESSAGE)));
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#removeArgument(Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)}
	 * .
	 */
	public void testRemoveArgument()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		assertNull(filter.removeArgument(arg1));
		filter.addArgument(arg1);
		assertSame(arg1, filter.removeArgument(arg1));
		assertNull(filter.addArgument(arg1));

		try
		{
			filter.removeArgument(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}

		assertNull(filter.removeArgument(new DummyCA(null)));
		assertNull(filter.removeArgument(new DummyCA(new DummyCV("xxx", PropertyPrefix.MESSAGE))));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#getArgument(java.lang.String)}
	 * .
	 */
	public void testGetArgument()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));
		assertNull(filter.addArgument(arg1));
		assertNull(filter.addArgument(arg2));

		assertSame(arg1, filter.getArgument(arg1.getProperty().getBaseName()));
		assertSame(arg2, filter.getArgument(arg2.getProperty().getBaseName()));

		assertNull(filter.getArgument(null));
		assertNull(filter.getArgument(""));
		assertNull(filter.getArgument(arg1.getProperty().getName()));
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#getArguments()}
	 * .
	 */
	public void testGetArguments()
	{
		CanonAssignment arg1 = new DummyCA(new DummyCV("arg1"));
		CanonAssignment arg2 = new DummyCA(new DummyCV("arg2"));

		assertNotNull(filter.getArguments());
		assertEquals(0, filter.getArguments().size());

		assertNull(filter.addArgument(arg1));
		assertNull(filter.addArgument(arg2));
		assertEquals(2, filter.getArguments().size());

		assertSame(arg1, filter.addArgument(arg1));
		assertEquals(2, filter.getArguments().size());

		assertSame(arg1, filter.removeArgument(arg1));
		assertEquals(1, filter.getArguments().size());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Filter#setElementExpression(Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression)}
	 * .
	 */
	public void testSetElementExpression()
	{
		FilterElementExpression fee = new DummyFE();

		assertNull(filter.getElementExpression());
		filter.setElementExpression(fee);
		assertSame(filter, fee.getOwner());
		assertSame(fee, filter.getElementExpression());

		try
		{
			filter.setElementExpression(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFT extends AbstractRepositoryEntity implements FilterType
	{
		private static final long serialVersionUID = 8340125372676859121L;

		/**
		 * 
		 */
		public DummyFT()
		{
			super();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.Filters.FilterType#getFilterName()
		 */
		public String getFilterName()
		{
			return null;
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyFE extends AbstractRepositoryEntity implements FilterElementExpression
	{
		private static final long serialVersionUID = -348280562215152579L;

		/**
		 * 
		 */
		public DummyFE()
		{
			super();
		}
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
			this(vname, PropertyPrefix.FILTER);
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

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible
		 * (Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)
		 */
		public boolean compatible(CpsVariable other) throws UnsupportedOperationException
		{
			return false;
		}
	}
}
