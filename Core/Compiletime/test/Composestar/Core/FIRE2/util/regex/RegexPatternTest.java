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

package Composestar.Core.FIRE2.util.regex;

import junit.framework.TestCase;

/**
 * @author Michiel Hendriks
 */
public class RegexPatternTest extends TestCase
{
	public RegexPatternTest()
	{
		super();
	}

	/**
	 * @param name
	 */
	public RegexPatternTest(String name)
	{
		super(name);
	}

	public void testBasic()
	{
		Pattern pat = RegexPattern.compile("test");
		Pattern pat2 = RegexPattern.compile("(test)(bar)");
	}

	public void testAlts()
	{
		Pattern pat = RegexPattern.compile("foo|bar|quux|frop");
		Pattern pat2 = RegexPattern.compile("(foo)|(bar)|(quux)|(frop)");
		Pattern pat3 = RegexPattern.compile("foo|(bar|quux|frop)");
		Pattern pat4 = RegexPattern.compile("foo|(bar|(quux|frop))");
		Pattern pat5 = RegexPattern.compile("(foo)(bar)(quux|frop)(friep)");
	}

	public void testNeq()
	{
		// bar
		// quux
		// xxx
		Pattern pat = RegexPattern.compile("(?foo)");
		// quux
		// xxx
		Pattern pat2 = RegexPattern.compile("(?foo|bar)");
		// foo
		// quux
		// xxx
		Pattern pat3 = RegexPattern.compile("foo|(?bar)");
		// foo
		// quux
		// frop
		// xxx
		Pattern pat4 = RegexPattern.compile("foo|(?bar|(?quux|frop))");
		// foo
		// bar
		// foo foo
		// foo xxx bar
		Pattern pat5 = RegexPattern.compile("(?(foo)(bar))"); // is BROKEN
		// foo frop
		// foo xxx frop
		Pattern pat6 = RegexPattern.compile("(foo)(?bar|quux)(frop)");
	}

	public static void main(String[] args)
	{
		RegexPatternTest t = new RegexPatternTest();
		t.testAlts();
	}
}
