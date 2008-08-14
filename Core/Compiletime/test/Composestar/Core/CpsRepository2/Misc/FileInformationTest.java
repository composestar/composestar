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

package Composestar.Core.CpsRepository2.Misc;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.Meta.FileInformation;

/**
 * @author Michiel Hendriks
 */
public class FileInformationTest extends TestCase
{
	/**
	 * The test file
	 */
	File file;

	/**
	 * The file information instance under observation
	 */
	FileInformation fi;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		file = File.createTempFile("junittest", null);
		fi = new FileInformation(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		if (!file.delete())
		{
			file.deleteOnExit();
		}
		file = null;
		fi = null;
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#update(java.io.File)}.
	 * 
	 * @throws IOException
	 */
	public void testUpdate() throws IOException
	{
		File file2 = File.createTempFile("junittest", null);
		try
		{
			assertEquals(file, fi.getLocation());
			fi.update(file2);
			assertEquals(file2, fi.getLocation());
			assertNotSame(file, fi.getLocation());
		}
		finally
		{
			if (!file2.delete())
			{
				file2.deleteOnExit();
			}
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#update(java.io.File)}.
	 */
	public void testUpdate2()
	{
		try
		{
			fi.update(null);
			fail("No exception thrown");
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#getLocation()}.
	 */
	public void testGetLocation()
	{
		assertEquals(file, fi.getLocation());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#getLastModified()}.
	 */
	public void testGetLastModified()
	{
		assertEquals(file.lastModified(), fi.getLastModified());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#isOutdated()}.
	 */
	public void testIsOutdated()
	{
		assertEquals(false, fi.isOutdated());
		file.setLastModified(file.lastModified() + 10);
		assertEquals(true, fi.isOutdated());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.FileInformation#isOutdated()}.
	 */
	public void testEquals()
	{
		FileInformation fi2 = new FileInformation(fi.getLocation());
		assertEquals(fi2, fi);
		assertTrue(fi.equals(fi2));
	}
}
