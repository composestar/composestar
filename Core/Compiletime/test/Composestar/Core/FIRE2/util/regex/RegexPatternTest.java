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
		Pattern pat = RegexPattern.compile("aa");
		Pattern pat2 = RegexPattern.compile("(aa)(bb)");
		Pattern pat3 = RegexPattern.compile("(aa)bb(cc)");
		Pattern pat4 = RegexPattern.compile("aa(bb)cc");
	}

	public void testInvalid()
	{
		try
		{
			Pattern pat = RegexPattern.compile("aa:");
		}
		catch (PatternParseException e)
		{
			e.printStackTrace();
		}
		try
		{
			Pattern pat = RegexPattern.compile("aa(");
		}
		catch (PatternParseException e)
		{
			e.printStackTrace();
		}
		try
		{
			Pattern pat = RegexPattern.compile("aa(aa");
		}
		catch (PatternParseException e)
		{
			e.printStackTrace();
		}
		try
		{
			Pattern pat = RegexPattern.compile(")");
		}
		catch (PatternParseException e)
		{
			e.printStackTrace();
		}
		try
		{
			Pattern pat = RegexPattern.compile("*+?");
		}
		catch (PatternParseException e)
		{
			e.printStackTrace();
		}
	}

	public void testAlts() throws PatternParseException
	{
		// pat1 == pat2 == pat3 == pat4
		// "aa" "bb" "cc" "dd"
		Pattern pat = RegexPattern.compile("aa|bb|cc|dd");
		Pattern pat2 = RegexPattern.compile("(aa)|(bb)|(cc)|(dd)");
		Pattern pat3 = RegexPattern.compile("aa|(bb|cc|dd)");
		Pattern pat4 = RegexPattern.compile("aa|(bb|(cc|dd))");

		// "aa bb cc ee" "aa bb dd ee"
		Pattern pat5 = RegexPattern.compile("(aa)(bb)(cc|dd)(ee)");
	}

	public void testNeq() throws PatternParseException
	{
		// "bb" "cc" "dd"
		Pattern pat = RegexPattern.compile("(?!aa)");
		// "cc" "dd"
		Pattern pat2 = RegexPattern.compile("(?!aa|bb)");
		// "aa" "cc" "dd"
		Pattern pat3 = RegexPattern.compile("aa|(?!bb)");
		// "aa" "cc" "dd"
		Pattern pat4 = RegexPattern.compile("aa|(?!bb|(?!cc|dd))");
		// "bb aa" "aa aa" "aa cc"
		Pattern pat5 = RegexPattern.compile("(?!(aa)(bb))"); // is BROKEN
		// "aa dd dd" "aa aa dd"
		Pattern pat6 = RegexPattern.compile("(aa)(?!bb|cc)(dd)");
	}

	public void testDot() throws PatternParseException
	{
		// "aa" "bb cc aa"
		Pattern pat = RegexPattern.compile(".*aa");
		// "bb aa" "bb cc dd aa"
		Pattern pat2 = RegexPattern.compile(".+aa");
	}

	public void testMult() throws PatternParseException
	{
		// "bb" or ""
		Pattern pat1 = RegexPattern.compile("(bb)?");
		// "aa bb cc" or "aa cc"
		Pattern pat1a = RegexPattern.compile("aa(bb)?cc");
		// "bb bb" "bb" ""
		Pattern pat2 = RegexPattern.compile("(bb)*");
		// "aa bb bb cc" "aa bb cc" "aa cc"
		Pattern pat2a = RegexPattern.compile("aa(bb)*cc");
		// "bb bb" "bb"
		Pattern pat3 = RegexPattern.compile("(bb)+");
		// "aa bb bb bb cc" "aa bb cc"
		Pattern pat3a = RegexPattern.compile("aa(bb)+cc");

		// "aa bb dd cc" "aa cc"
		Pattern pat1b = RegexPattern.compile("aa((bb)(dd))?cc");
		// "aa bb cc" "aa dd cc" "aa cc"
		Pattern pat1c = RegexPattern.compile("aa((bb)|(dd))?cc");
		// "aa bb dd bb dd cc" "aa bb dd cc" "aa cc"
		Pattern pat2b = RegexPattern.compile("aa((bb)(dd))*cc");
		// "aa bb bb dd cc" "aa dd cc" "aa bb cc" "aa dd bb dd cc"
		Pattern pat2c = RegexPattern.compile("aa((bb)|(dd))*cc");
	}

	public static void main(String[] args)
	{
		RegexPatternTest t = new RegexPatternTest();
		try
		{
			t.testMult();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
