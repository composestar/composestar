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

package Composestar.Core.CpsRepository2.References;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.LAMA.ProgramElement;

/**
 * @author Michiel Hendriks
 */
public abstract class InstanceMethodReferenceTestBase extends MethodReferenceTestBase
{
	protected InstanceMethodReference imref;

	protected CpsObject context;

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.References.MethodReferenceTestBase#setUp
	 * ()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		context = new DummyCPSO(typeRef);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.References.InstanceMethodReference#getCpsObject()}
	 * .
	 */
	public void testGetCpsObject()
	{
		assertSame(context, imref.getCpsObject());
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyCPSO implements CpsObject
	{
		private static final long serialVersionUID = 1L;

		protected TypeReference tref;

		public DummyCPSO(TypeReference ref)
		{
			tref = ref;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.TypeSystem.CpsObject#getInstance()
		 */
		public Object getInstance()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement#
		 * getTypeReference()
		 */
		public TypeReference getTypeReference()
		{
			return tref;
		}

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
		 * getProgramElement()
		 */
		public ProgramElement getProgramElement()
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

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isInnerObject()
		 */
		public boolean isInnerObject()
		{
			return false;
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
