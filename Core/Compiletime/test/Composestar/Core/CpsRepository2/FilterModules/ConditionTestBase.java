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

import Composestar.Core.CpsRepository2.InstanceContextProvider;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntityTestBase;
import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.LAMA.MethodInfo;

/**
 * @author Michiel Hendriks
 */
public abstract class ConditionTestBase extends QualifiedRepositoryEntityTestBase
{
	protected Condition condition;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.FilterModules.Condition#setMethodReference(Composestar.Core.CpsRepository2.References.MethodReference)}
	 * .
	 */
	public void testSetMethodReference()
	{
		MethodReference ref = new DummyMR();

		assertNull(condition.getMethodReference());
		condition.setMethodReference(ref);
		assertSame(ref, condition.getMethodReference());

		ref = new DummyIMR();
		condition.setMethodReference(ref);
		assertSame(ref, condition.getMethodReference());

		try
		{
			condition.setMethodReference(null);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyMR implements MethodReference
	{
		private static final long serialVersionUID = 3385869512155143174L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.References.MethodReference#
		 * getJoinPointContextArgument()
		 */
		public JoinPointContextArgument getJoinPointContextArgument()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.References.MethodReference#
		 * getTypeReference()
		 */
		public TypeReference getTypeReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public MethodInfo getReference()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(MethodInfo element) throws UnsupportedOperationException
		{}
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyIMR extends DummyMR implements InstanceMethodReference
	{
		private static final long serialVersionUID = -5948301381172762785L;

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.InstanceMethodReference
		 * #getContext()
		 */
		public InstanceContextProvider getContext()
		{
			return null;
		}

	}
}
