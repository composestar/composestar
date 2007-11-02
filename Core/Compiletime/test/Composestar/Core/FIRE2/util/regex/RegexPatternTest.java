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

	public void testBasic() throws PatternParseException
	{
		Pattern pat0 = RegexPattern.compile("");
		assertTrue(SimpleMatcher.matches(pat0, ""));
		assertFalse(SimpleMatcher.matches(pat0, "notEmpty"));

		Pattern pat = RegexPattern.compile("aa");
		assertTrue(SimpleMatcher.matches(pat, "aa"));
		assertFalse(SimpleMatcher.matches(pat, "a"));
		assertFalse(SimpleMatcher.matches(pat, "aaa"));
		assertFalse(SimpleMatcher.matches(pat, "bb"));

		Pattern pat2 = RegexPattern.compile("(aa)(bb)");
		assertTrue(SimpleMatcher.matches(pat2, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat2, "aa"));
		assertFalse(SimpleMatcher.matches(pat2, "bb"));
		assertFalse(SimpleMatcher.matches(pat2, "aa bb cc"));

		Pattern pat3 = RegexPattern.compile("(aa)bb(cc)");
		assertTrue(SimpleMatcher.matches(pat3, "aa bb cc"));
		assertFalse(SimpleMatcher.matches(pat3, "aa bb cc dd"));
		assertFalse(SimpleMatcher.matches(pat3, "aa cc dd"));

		Pattern pat4 = RegexPattern.compile("aa(bb)cc");
		assertTrue(SimpleMatcher.matches(pat4, "aa bb cc"));
		assertFalse(SimpleMatcher.matches(pat4, "aa bb cc dd"));
		assertFalse(SimpleMatcher.matches(pat4, "aa cc dd"));
	}

	public void testInvalid()
	{
		try
		{
			RegexPattern.compile("aa:");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
		try
		{
			RegexPattern.compile("aa(");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
		try
		{
			RegexPattern.compile("aa(aa");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
		try
		{
			RegexPattern.compile(")");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
		try
		{
			RegexPattern.compile("*+?");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
	}

	public void testAlts() throws PatternParseException
	{
		// pat1 == pat2 == pat3 == pat4
		// "aa" "bb" "cc" "dd"
		Pattern pat = RegexPattern.compile("aa|bb|cc|dd");
		assertTrue(SimpleMatcher.matches(pat, "aa"));
		assertTrue(SimpleMatcher.matches(pat, "bb"));
		assertTrue(SimpleMatcher.matches(pat, "cc"));
		assertTrue(SimpleMatcher.matches(pat, "dd"));
		assertFalse(SimpleMatcher.matches(pat, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat, ""));
		assertFalse(SimpleMatcher.matches(pat, "ee"));

		Pattern pat2 = RegexPattern.compile("(aa)|(bb)|(cc)|(dd)");
		assertTrue(SimpleMatcher.matches(pat2, "aa"));
		assertTrue(SimpleMatcher.matches(pat2, "bb"));
		assertTrue(SimpleMatcher.matches(pat2, "cc"));
		assertTrue(SimpleMatcher.matches(pat2, "dd"));
		assertFalse(SimpleMatcher.matches(pat2, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat2, ""));
		assertFalse(SimpleMatcher.matches(pat2, "ee"));

		Pattern pat3 = RegexPattern.compile("aa|(bb|cc|dd)");
		assertTrue(SimpleMatcher.matches(pat3, "aa"));
		assertTrue(SimpleMatcher.matches(pat3, "bb"));
		assertTrue(SimpleMatcher.matches(pat3, "cc"));
		assertTrue(SimpleMatcher.matches(pat3, "dd"));
		assertFalse(SimpleMatcher.matches(pat3, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat3, ""));
		assertFalse(SimpleMatcher.matches(pat3, "ee"));

		Pattern pat4 = RegexPattern.compile("aa|(bb|(cc|dd))");
		assertTrue(SimpleMatcher.matches(pat4, "aa"));
		assertTrue(SimpleMatcher.matches(pat4, "bb"));
		assertTrue(SimpleMatcher.matches(pat4, "cc"));
		assertTrue(SimpleMatcher.matches(pat4, "dd"));
		assertFalse(SimpleMatcher.matches(pat4, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat4, ""));
		assertFalse(SimpleMatcher.matches(pat4, "ee"));

		// "aa bb cc ee" "aa bb dd ee"
		Pattern pat5 = RegexPattern.compile("(aa)(bb)(cc|dd)(ee)");
		assertTrue(SimpleMatcher.matches(pat5, "aa bb cc ee"));
		assertTrue(SimpleMatcher.matches(pat5, "aa bb dd ee"));
		assertFalse(SimpleMatcher.matches(pat5, "aa bb cc dd ee"));
		assertFalse(SimpleMatcher.matches(pat5, "aa bb ee"));
	}

	public void testNeq() throws PatternParseException
	{
		// "bb" "cc" "dd"
		Pattern pat = RegexPattern.compile("(?!aa)");
		assertTrue(SimpleMatcher.matches(pat, "bb"));
		assertFalse(SimpleMatcher.matches(pat, "bb aa"));
		assertFalse(SimpleMatcher.matches(pat, "aa"));

		// "cc" "dd"
		Pattern pat2 = RegexPattern.compile("(?!aa|bb)");
		assertTrue(SimpleMatcher.matches(pat2, "cc"));
		assertFalse(SimpleMatcher.matches(pat2, "aa"));
		assertFalse(SimpleMatcher.matches(pat2, "aa bb"));
		assertFalse(SimpleMatcher.matches(pat2, "bb"));

		// "aa" "cc" "dd"
		Pattern pat3 = RegexPattern.compile("aa|(?!bb)");
		assertTrue(SimpleMatcher.matches(pat3, "aa"));
		assertTrue(SimpleMatcher.matches(pat3, "cc"));
		assertTrue(SimpleMatcher.matches(pat3, "dd"));
		assertFalse(SimpleMatcher.matches(pat3, "bb"));
		assertFalse(SimpleMatcher.matches(pat3, "aa bb"));

		// "aa" "cc" "dd"
		Pattern pat4 = RegexPattern.compile("aa|(?!bb|(?!cc|dd))");
		assertTrue(SimpleMatcher.matches(pat4, "aa"));
		assertTrue(SimpleMatcher.matches(pat4, "cc"));
		assertTrue(SimpleMatcher.matches(pat4, "dd"));
		assertFalse(SimpleMatcher.matches(pat4, "bb"));

		// "bb aa" "aa aa" "aa cc"
		Pattern pat5 = RegexPattern.compile("(?!(aa)(bb))"); // is BROKEN

		// "aa dd dd" "aa aa dd"
		Pattern pat6 = RegexPattern.compile("(aa)(?!bb|cc)(dd)");
		assertTrue(SimpleMatcher.matches(pat6, "aa dd dd"));
		assertTrue(SimpleMatcher.matches(pat6, "aa aa dd"));
		assertFalse(SimpleMatcher.matches(pat6, "aa bb dd"));
		assertFalse(SimpleMatcher.matches(pat6, "aa cc dd"));
		assertFalse(SimpleMatcher.matches(pat6, "aa dd"));
	}

	public void testDot() throws PatternParseException
	{
		// "aa" "bb cc aa"
		Pattern pat = RegexPattern.compile(".*aa");
		assertTrue(SimpleMatcher.matches(pat, "aa"));
		assertTrue(SimpleMatcher.matches(pat, "aa aa"));
		assertTrue(SimpleMatcher.matches(pat, "bb aa"));
		assertTrue(SimpleMatcher.matches(pat, "bb cc aa"));
		assertTrue(SimpleMatcher.matches(pat, "bb cc dd aa"));
		assertFalse(SimpleMatcher.matches(pat, "bb"));
		assertFalse(SimpleMatcher.matches(pat, "aa bb"));

		// "bb aa" "bb cc dd aa"
		Pattern pat2 = RegexPattern.compile(".+aa");
		assertTrue(SimpleMatcher.matches(pat2, "aa aa"));
		assertTrue(SimpleMatcher.matches(pat2, "bb aa"));
		assertTrue(SimpleMatcher.matches(pat2, "bb cc aa"));
		assertTrue(SimpleMatcher.matches(pat2, "bb cc dd aa"));
		assertFalse(SimpleMatcher.matches(pat2, "aa"));
		assertFalse(SimpleMatcher.matches(pat2, "bb"));
		assertFalse(SimpleMatcher.matches(pat2, "aa bb"));

		Pattern pat3 = RegexPattern.compile("aa.*aa");
		assertTrue(SimpleMatcher.matches(pat3, "aa aa"));
		assertTrue(SimpleMatcher.matches(pat3, "aa bb aa"));
		assertTrue(SimpleMatcher.matches(pat3, "aa aa aa aa"));
		assertFalse(SimpleMatcher.matches(pat3, "bb aa"));

		Pattern pat4 = RegexPattern.compile("aa.+aa");
		assertTrue(SimpleMatcher.matches(pat4, "aa bb aa"));
		assertTrue(SimpleMatcher.matches(pat4, "aa aa aa aa"));
		assertFalse(SimpleMatcher.matches(pat4, "aa aa"));
		assertFalse(SimpleMatcher.matches(pat4, "bb aa"));
		assertFalse(SimpleMatcher.matches(pat4, "bb bb aa"));
	}

	public void testMult() throws PatternParseException
	{
		// "bb" or ""
		Pattern pat1 = RegexPattern.compile("(bb)?");
		assertTrue(SimpleMatcher.matches(pat1, ""));
		assertTrue(SimpleMatcher.matches(pat1, "bb"));
		assertFalse(SimpleMatcher.matches(pat1, "aa"));
		assertFalse(SimpleMatcher.matches(pat1, "bb bb"));

		// "aa bb cc" or "aa cc"
		Pattern pat1a = RegexPattern.compile("aa(bb)?cc");
		assertTrue(SimpleMatcher.matches(pat1a, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat1a, "aa bb cc"));
		assertFalse(SimpleMatcher.matches(pat1a, "aa bb bb cc"));

		// "bb bb" "bb" ""
		Pattern pat2 = RegexPattern.compile("(bb)*");
		assertTrue(SimpleMatcher.matches(pat2, ""));
		assertTrue(SimpleMatcher.matches(pat2, "bb"));
		assertTrue(SimpleMatcher.matches(pat2, "bb bb"));
		assertTrue(SimpleMatcher.matches(pat2, "bb bb bb"));
		assertFalse(SimpleMatcher.matches(pat2, "aa"));
		assertFalse(SimpleMatcher.matches(pat2, "bb aa"));
		assertFalse(SimpleMatcher.matches(pat2, "bb aa bb"));

		// "aa bb bb cc" "aa bb cc" "aa cc"
		Pattern pat2a = RegexPattern.compile("aa(bb)*cc");
		assertTrue(SimpleMatcher.matches(pat2a, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat2a, "aa bb cc"));
		assertTrue(SimpleMatcher.matches(pat2a, "aa bb bb cc"));
		assertFalse(SimpleMatcher.matches(pat2a, "aa cc bb"));
		assertFalse(SimpleMatcher.matches(pat2a, "aa cc bb cc"));
		assertFalse(SimpleMatcher.matches(pat2a, "aa dd cc"));

		// "bb bb" "bb"
		Pattern pat3 = RegexPattern.compile("(bb)+");
		assertTrue(SimpleMatcher.matches(pat3, "bb"));
		assertTrue(SimpleMatcher.matches(pat3, "bb bb"));
		assertTrue(SimpleMatcher.matches(pat3, "bb bb bb"));

		// "aa bb bb bb cc" "aa bb cc"
		Pattern pat3a = RegexPattern.compile("aa(bb)+cc");
		assertTrue(SimpleMatcher.matches(pat3a, "aa bb cc"));
		assertTrue(SimpleMatcher.matches(pat3a, "aa bb bb cc"));
		assertFalse(SimpleMatcher.matches(pat3a, "aa cc bb"));
		assertFalse(SimpleMatcher.matches(pat3a, "aa cc bb cc"));
		assertFalse(SimpleMatcher.matches(pat3a, "aa dd cc"));
	}

	public void testMultEx() throws PatternParseException
	{
		// "aa bb dd cc" "aa cc"
		Pattern pat1b = RegexPattern.compile("aa((bb)(dd))?cc");
		assertTrue(SimpleMatcher.matches(pat1b, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat1b, "aa bb dd cc"));
		assertFalse(SimpleMatcher.matches(pat1b, "aa dd cc"));

		// "aa bb cc" "aa dd cc" "aa cc"
		Pattern pat1c = RegexPattern.compile("aa((bb)|(dd))?cc");
		assertTrue(SimpleMatcher.matches(pat1c, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat1c, "aa bb cc"));
		assertTrue(SimpleMatcher.matches(pat1c, "aa dd cc"));
		assertFalse(SimpleMatcher.matches(pat1c, "aa bb dd cc"));
		assertFalse(SimpleMatcher.matches(pat1c, "aa dd dd cc"));

		// "aa bb dd bb dd cc" "aa bb dd cc" "aa cc"
		Pattern pat2b = RegexPattern.compile("aa((bb)(dd))*cc");
		assertTrue(SimpleMatcher.matches(pat2b, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat2b, "aa bb dd cc"));
		assertTrue(SimpleMatcher.matches(pat2b, "aa bb dd bb dd cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa dd cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa bb cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa bb dd bb cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa bb bb cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa dd bb cc"));
		assertFalse(SimpleMatcher.matches(pat2b, "aa bb dd dd bb cc"));

		// "aa bb bb dd cc" "aa dd cc" "aa bb cc" "aa dd bb dd cc"
		Pattern pat2c = RegexPattern.compile("aa((bb)|(dd))*cc");
		assertTrue(SimpleMatcher.matches(pat2c, "aa cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa bb cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa dd cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa bb dd cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa dd bb cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa bb bb cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa bb bb dd bb cc"));
		assertTrue(SimpleMatcher.matches(pat2c, "aa dd bb dd dd bb cc"));

		Pattern patextr1 = RegexPattern.compile("bb|(dd)*");
		assertTrue(SimpleMatcher.matches(patextr1, "bb"));
		assertTrue(SimpleMatcher.matches(patextr1, "dd"));
		assertTrue(SimpleMatcher.matches(patextr1, "dd dd"));
		assertTrue(SimpleMatcher.matches(patextr1, "dd dd dd"));
		assertFalse(SimpleMatcher.matches(patextr1, "bb dd"));
		assertFalse(SimpleMatcher.matches(patextr1, "dd bb"));

		// "aa bb cc" "aa dd dd cc" "aa dd dd bb dd bb cc"
		Pattern patextr2 = RegexPattern.compile("aa(bb|(dd)*)+cc");
		assertTrue(SimpleMatcher.matches(patextr2, "aa cc"));
		assertTrue(SimpleMatcher.matches(patextr2, "aa bb cc"));
		assertTrue(SimpleMatcher.matches(patextr2, "aa dd cc"));
		assertTrue(SimpleMatcher.matches(patextr2, "aa dd dd cc"));
		assertTrue(SimpleMatcher.matches(patextr2, "aa bb dd dd bb cc"));
		assertTrue(SimpleMatcher.matches(patextr2, "aa bb bb cc"));
	}

	public static void main(String[] args)
	{
		RegexPatternTest t = new RegexPatternTest();
		try
		{
			t.testDot();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
