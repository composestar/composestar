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

package Composestar.Core.CpsRepository2;

import java.io.File;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * @author Michiel Hendriks
 */
public abstract class RepositoryEntityTestBase extends TestCase
{
	protected RepositoryEntity re;

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation(Composestar.Core.CpsRepository2.Meta.SourceInformation)}
	 * .
	 */
	public void testSetSourceInformation()
	{
		FileInformation fi = new FileInformation(new File("."));
		SourceInformation si = new SourceInformation(fi);
		assertNull(re.getSourceInformation());
		re.setSourceInformation(si);
		assertSame(si, re.getSourceInformation());
		re.setSourceInformation(null);
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar.Core.CpsRepository2.RepositoryEntity)}
	 * .
	 */
	public void testSetOwner()
	{
		DummyRE re1 = new DummyRE();
		DummyRE re2 = new DummyRE();
		assertNull(re.getOwner());
		assertNull(re.setOwner(re1));
		assertSame(re1, re.getOwner());
		assertSame(re1, re.setOwner(re2));
		assertSame(re2, re.setOwner(null));
	}

	/**
	 * @author Michiel Hendriks
	 */
	protected class DummyRE implements RepositoryEntity
	{
		private static final long serialVersionUID = -8720785963243856468L;

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
