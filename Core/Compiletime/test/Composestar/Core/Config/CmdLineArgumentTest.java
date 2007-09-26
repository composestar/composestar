/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.Config;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Michiel Hendriks
 */
public class CmdLineArgumentTest extends TestCase
{
	protected CmdLineArgument arg;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		arg = new CmdLineArgument();
	}

	@Override
	protected void tearDown() throws Exception
	{
		ModuleInfoManager.clear();
		arg = null;
	}

	public void testReplacements()
	{
		assertEquals("foo", arg.resolve("foo", null));

		Properties prop = new Properties();
		prop.setProperty("foo", "quux");
		prop.setProperty("backslashes", "\\-\\");

		assertEquals("quux", arg.resolve("${foo}", prop));
		assertEquals("", arg.resolve("${bar}", prop));
		assertEquals("quux", arg.resolve("${bar:quux}", prop));
		assertEquals("", arg.resolve("${FOO}", prop));
		assertEquals("Result: \\-\\", arg.resolve("Result: ${backslashes}", prop));
		assertEquals("fooquuxbar", arg.resolve("foo${foo}bar", prop));
		assertEquals(System.getProperty("os.name"), arg.resolve("@{os.name}", prop));
		assertEquals("default", arg.resolve("@{os.InVaLiD:default}", prop));
		assertEquals(System.getenv("TEMP"), arg.resolve("%{TEMP}", prop));
		assertEquals("default", arg.resolve("%{InVaLiD:default}", prop));
	}
}
