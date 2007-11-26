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
 * $Id: RegexPatternTest.java 3917 2007-11-06 15:45:31Z elmuerte $
 */

package Composestar.Utils.Regex;

import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * @author Michiel Hendriks
 */
public class RegexPatternTest extends TestCase
{
	protected RegexPattern cpsPattern;

	protected Pattern javaPattern;

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

	protected void compile(String pattern) throws PatternParseException
	{
		System.out.println("Testing pattern: " + pattern);
		cpsPattern = RegexPattern.compile(pattern);
		javaPattern = Pattern.compile(pattern);
	}

	protected boolean matches(String input)
	{
		System.out.println("Testing input: " + input);
		boolean result1 = cpsPattern.matches(new StringListBuffer(input));
		boolean result2 = javaPattern.matcher(input.replaceAll("\\s", "")).matches();
		if (result1 != result2)
		{
			fail(String.format("RegexPattern and java.util.regex.Pattern do not agree: %s vs %s for input: %s",
					result1, result2, input));
		}
		System.out.println("      matches: " + result1);
		return result1;
	}

	public void testBasic() throws PatternParseException
	{
		compile("");
		assertTrue(matches(""));
		assertFalse(matches("NotEmpty"));

		compile("aa");
		assertTrue(matches("aa"));
		assertFalse(matches("a"));
		assertFalse(matches("aaa"));
		assertFalse(matches("bb"));

		compile("(aa)(bb)");
		assertTrue(matches("aa bb"));
		assertFalse(matches("aa"));
		assertFalse(matches("bb"));
		assertFalse(matches("aa bb cc"));

		compile("(aa)bb(cc)");
		assertTrue(matches("aa bb cc"));
		assertFalse(matches("aa bb cc dd"));
		assertFalse(matches("aa cc dd"));

		compile("aa(bb)cc");
		assertTrue(matches("aa bb cc"));
		assertFalse(matches("aa bb cc dd"));
		assertFalse(matches("aa cc dd"));
	}

	public void testInvalid()
	{
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
			RegexPattern.compile("aa:");
			fail("No PatternParseException");
		}
		catch (PatternParseException e)
		{
		}
		try
		{
			RegexPattern.compile("aa.");
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
		compile("aa|bb|cc|dd");
		assertTrue(matches("aa"));
		assertTrue(matches("bb"));
		assertTrue(matches("cc"));
		assertTrue(matches("dd"));
		assertFalse(matches("aa bb"));
		assertFalse(matches(""));
		assertFalse(matches("ee"));

		compile("(aa)|(bb)|(cc)|(dd)");
		assertTrue(matches("aa"));
		assertTrue(matches("bb"));
		assertTrue(matches("cc"));
		assertTrue(matches("dd"));
		assertFalse(matches("aa bb"));
		assertFalse(matches(""));
		assertFalse(matches("ee"));

		compile("aa|(bb|cc|dd)");
		assertTrue(matches("aa"));
		assertTrue(matches("bb"));
		assertTrue(matches("cc"));
		assertTrue(matches("dd"));
		assertFalse(matches("aa bb"));
		assertFalse(matches(""));
		assertFalse(matches("ee"));

		compile("aa|(bb|(cc|dd))");
		assertTrue(matches("aa"));
		assertTrue(matches("bb"));
		assertTrue(matches("cc"));
		assertTrue(matches("dd"));
		assertFalse(matches("aa bb"));
		assertFalse(matches(""));
		assertFalse(matches("ee"));

		// "aa bb cc ee" "aa bb dd ee"
		compile("(aa)(bb)(cc|dd)(ee)");
		assertTrue(matches("aa bb cc ee"));
		assertTrue(matches("aa bb dd ee"));
		assertFalse(matches("aa bb cc dd ee"));
		assertFalse(matches("aa bb ee"));
	}

	public void testNeq() throws PatternParseException
	{
		// Matches an empty string
		compile("(?!aa)");
		assertTrue(matches(""));
		assertFalse(matches("bb"));
		assertFalse(matches("aa"));

		// Matches an empty string
		compile("(?!aa)(?!bb)");
		assertTrue(matches(""));
		assertFalse(matches("bb"));
		assertFalse(matches("aa"));

		// "aa" or empty
		compile("aa|(?!bb)");
		assertTrue(matches("aa"));
		assertTrue(matches(""));
		assertFalse(matches("bb"));
		assertFalse(matches("cc"));

		// next must not be "bb" but "aa"
		compile("(?!bb)aa");
		assertTrue(matches("aa"));
		assertFalse(matches("bb"));
		assertFalse(matches("aa bb"));

		// next must not be "bb" or "aa" and then "aa" -> never matches
		compile("(?!bb|aa)aa");
		assertFalse(matches("bb"));
		assertFalse(matches("aa"));

		// next must not be "bb" or "aa" and then "aa" or "cc" -> only cc is
		// correct
		compile("(?!bb|aa)(aa|cc)");
		assertTrue(matches("cc"));
		assertFalse(matches("bb"));
		assertFalse(matches("aa"));

		// "aa cc" "bb cc" "bb bb" but not "aa bb"
		compile("(?!(aa)(bb))(aa|bb)(cc|bb)");
		assertTrue(matches("aa cc"));
		assertTrue(matches("bb bb"));
		assertTrue(matches("bb bb"));
		assertFalse(matches("aa bb"));

		// "aa dd" but not "aa bb" or "aa cc"
		compile("(aa)(?!bb|cc)(bb|cc|dd)");
		assertTrue(matches("aa dd"));
		assertFalse(matches("aa bb"));
		assertFalse(matches("aa cc"));

		compile("(aa)(?!(aa)(bb)|(cc))(aa|bb|cc)(aa|bb|cc)");
		assertTrue(matches("aa aa cc"));
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa bb bb"));
		assertFalse(matches("aa cc aa"));
		assertFalse(matches("aa cc bb"));
		assertFalse(matches("aa cc dd"));
		assertFalse(matches("aa aa bb"));
	}

	public void testDot() throws PatternParseException
	{
		// "aa" "bb cc aa"
		compile(".*aa");
		assertTrue(matches("aa"));
		assertTrue(matches("aa aa"));
		assertTrue(matches("bb aa"));
		assertTrue(matches("bb cc aa"));
		assertTrue(matches("bb cc dd aa"));
		assertFalse(matches("bb"));
		assertFalse(matches("aa bb"));

		// "bb aa" "bb cc dd aa"
		compile(".+aa");
		assertTrue(matches("aa aa"));
		assertTrue(matches("bb aa"));
		assertTrue(matches("bb cc aa"));
		assertTrue(matches("bb cc dd aa"));
		assertFalse(matches("aa"));
		assertFalse(matches("bb"));
		assertFalse(matches("aa bb"));

		compile("aa.*aa");
		assertTrue(matches("aa aa"));
		assertTrue(matches("aa bb aa"));
		assertTrue(matches("aa aa aa aa"));
		assertFalse(matches("bb aa"));

		compile("aa.+aa");
		assertTrue(matches("aa bb aa"));
		assertTrue(matches("aa aa aa aa"));
		assertFalse(matches("aa aa"));
		assertFalse(matches("bb aa"));
		assertFalse(matches("bb bb aa"));
	}

	public void testMult() throws PatternParseException
	{
		// "bb" or ""
		compile("(bb)?");
		assertTrue(matches(""));
		assertTrue(matches("bb"));
		assertFalse(matches("aa"));
		assertFalse(matches("bb bb"));

		// "aa bb cc" or "aa cc"
		compile("aa(bb)?cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb cc"));
		assertFalse(matches("aa bb bb cc"));

		// "bb bb" "bb" ""
		compile("(bb)*");
		assertTrue(matches(""));
		assertTrue(matches("bb"));
		assertTrue(matches("bb bb"));
		assertTrue(matches("bb bb bb"));
		assertFalse(matches("aa"));
		assertFalse(matches("bb aa"));
		assertFalse(matches("bb aa bb"));

		// "aa bb bb cc" "aa bb cc" "aa cc"
		compile("aa(bb)*cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa bb bb cc"));
		assertFalse(matches("aa cc bb"));
		assertFalse(matches("aa cc bb cc"));
		assertFalse(matches("aa dd cc"));

		// "bb bb" "bb"
		compile("(bb)+");
		assertTrue(matches("bb"));
		assertTrue(matches("bb bb"));
		assertTrue(matches("bb bb bb"));

		// "aa bb bb bb cc" "aa bb cc"
		compile("aa(bb)+cc");
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa bb bb cc"));
		assertFalse(matches("aa cc bb"));
		assertFalse(matches("aa cc bb cc"));
		assertFalse(matches("aa dd cc"));
	}

	public void testMultEx() throws PatternParseException
	{
		// "aa bb dd cc" "aa cc"
		compile("aa((bb)(dd))?cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb dd cc"));
		assertFalse(matches("aa dd cc"));

		// "aa bb cc" "aa dd cc" "aa cc"
		compile("aa((bb)|(dd))?cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa dd cc"));
		assertFalse(matches("aa bb dd cc"));
		assertFalse(matches("aa dd dd cc"));

		// "aa bb dd bb dd cc" "aa bb dd cc" "aa cc"
		compile("aa((bb)(dd))*cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb dd cc"));
		assertTrue(matches("aa bb dd bb dd cc"));
		assertFalse(matches("aa dd cc"));
		assertFalse(matches("aa bb cc"));
		assertFalse(matches("aa bb dd bb cc"));
		assertFalse(matches("aa bb bb cc"));
		assertFalse(matches("aa dd bb cc"));
		assertFalse(matches("aa bb dd dd bb cc"));

		// "aa bb bb dd cc" "aa dd cc" "aa bb cc" "aa dd bb dd cc"
		compile("aa((bb)|(dd))*cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa dd cc"));
		assertTrue(matches("aa bb dd cc"));
		assertTrue(matches("aa dd bb cc"));
		assertTrue(matches("aa bb bb cc"));
		assertTrue(matches("aa bb bb dd bb cc"));
		assertTrue(matches("aa dd bb dd dd bb cc"));

		compile("bb|(dd)*");
		assertTrue(matches("bb"));
		assertTrue(matches("dd"));
		assertTrue(matches("dd dd"));
		assertTrue(matches("dd dd dd"));
		assertFalse(matches("bb dd"));
		assertFalse(matches("dd bb"));

		// "aa bb cc" "aa dd dd cc" "aa dd dd bb dd bb cc"
		compile("aa(bb|(dd)*)+cc");
		assertTrue(matches("aa cc"));
		assertTrue(matches("aa bb cc"));
		assertTrue(matches("aa dd cc"));
		assertTrue(matches("aa dd dd cc"));
		assertTrue(matches("aa bb dd dd bb cc"));
		assertTrue(matches("aa bb bb cc"));
	}

	public static void main(String[] args)
	{
		RegexPatternTest t = new RegexPatternTest();
		try
		{
			t.testBasic();
			t.testInvalid();
			t.testAlts();
			t.testNeq();
			t.testDot();
			t.testMult();
			t.testMultEx();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
