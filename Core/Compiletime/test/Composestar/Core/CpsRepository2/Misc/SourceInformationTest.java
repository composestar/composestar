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

import junit.framework.TestCase;
import Composestar.Core.CpsRepository2.Meta.FileInformation;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;

/**
 * @author Michiel Hendriks
 */
public class SourceInformationTest extends TestCase
{
	File file;

	FileInformation fi;

	SourceInformation si;

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
		si = new SourceInformation(fi);
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
		si = null;
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#SourceInformation(Composestar.Core.CpsRepository2.Meta.SourceInformation)}.
	 */
	public void testSourceInformationSourceInformation()
	{
		si.setLine(123);
		si.setLinePos(456);
		SourceInformation si2 = new SourceInformation(si);
		assertEquals(si.getFileInfo(), si2.getFileInfo());
		assertEquals(si.getFilename(), si2.getFilename());
		assertEquals(si.getLine(), si2.getLine());
		assertEquals(si.getLinePos(), si2.getLinePos());
		assertTrue(si.equals(si2));
		assertTrue(si2.equals(si));
		assertEquals(si.hashCode(), si2.hashCode());
		si.setLine(789);
		assertNotSame(si.getLine(), si2.getLine());
		assertFalse(si.equals(si2));
		assertFalse(si2.equals(si));
		assertNotSame(si.hashCode(), si2.hashCode());

		try
		{
			si2 = null;
			new SourceInformation(si2);
			fail();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			fi = null;
			new SourceInformation(fi);
			fail();
		}
		catch (NullPointerException e)
		{
		}
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#getFileInfo()}.
	 */
	public void testGetFileInfo()
	{
		assertNotNull(si.getFileInfo());
		assertEquals(fi, si.getFileInfo());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#getFilename()}.
	 */
	public void testGetFilename()
	{
		assertEquals(fi.getLocation(), si.getFilename());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#getLine()}.
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#setLine(int)}.
	 */
	public void testSetLine()
	{
		assertEquals(-1, si.getLine());
		si.setLine(123);
		assertEquals(123, si.getLine());
	}

	/**
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#getLinePos()}.
	 * Test method for
	 * {@link Composestar.Core.CpsRepository2.Meta.SourceInformation#setLinePos(int)}.
	 */
	public void testSetLinePos()
	{
		assertEquals(-1, si.getLinePos());
		si.setLinePos(123);
		assertEquals(123, si.getLinePos());
	}
}
