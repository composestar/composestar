#region Using directives
#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif
using System;
using System.Text;
using System.Collections.Generic;
#endregion

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern.UnitTests
{
    /// <summary>
    ///
    ///</summary>
    [TestClass()]
    public class RegularPatternTest
    {
        [TestMethod()]
        public void testBasic()
        {
            Pattern pat0 = RegularPattern.compile("^$");
            Assert.IsTrue(SimpleMatcher.matches(pat0, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat0, "notEmpty"));

            Pattern pat = RegularPattern.compile("^aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "a"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "aaa"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "bb"));

            Pattern pat2 = RegularPattern.compile("^(aa)(bb)$");
            Assert.IsTrue(SimpleMatcher.matches(pat2, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa bb cc"));

            Pattern pat3 = RegularPattern.compile("^(aa)bb(cc)$");
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "aa bb cc dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "aa cc dd"));

            Pattern pat4 = RegularPattern.compile("^aa(bb)cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat4, "aa bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "aa bb cc dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "aa cc dd"));
        }

        [TestMethod()]
        public void testInvalid()
        {
            try
            {
                RegularPattern.compile("aa:");
                Assert.Fail("No PatternParseException");
            }
            catch (PatternParseException e)
            {
            }
            try
            {
                RegularPattern.compile("aa(");
                Assert.Fail("No PatternParseException");
            }
            catch (PatternParseException e)
            {
            }
            try
            {
                RegularPattern.compile("aa(aa");
                Assert.Fail("No PatternParseException");
            }
            catch (PatternParseException e)
            {
            }
            try
            {
                RegularPattern.compile(")");
                Assert.Fail("No PatternParseException");
            }
            catch (PatternParseException e)
            {
            }
            try
            {
                RegularPattern.compile("*+?");
                Assert.Fail("No PatternParseException");
            }
            catch (PatternParseException e)
            {
            }
        }

        [TestMethod()]
        public void testAlts()
        {
            // pat1 == pat2 == pat3 == pat4
            // "aa" "bb" "cc" "dd"
            Pattern pat = RegularPattern.compile("^aa|bb|cc|dd$");
            Assert.IsTrue(SimpleMatcher.matches(pat, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat, "ee"));

            Pattern pat2 = RegularPattern.compile("^(aa)|(bb)|(cc)|(dd)$");
            Assert.IsTrue(SimpleMatcher.matches(pat2, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "ee"));

            Pattern pat3 = RegularPattern.compile("^aa|(bb|cc|dd)$");
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "ee"));

            Pattern pat4 = RegularPattern.compile("^aa|(bb|(cc|dd))$");
            Assert.IsTrue(SimpleMatcher.matches(pat4, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat4, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat4, "cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat4, "dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "ee"));

            // "aa bb cc ee" "aa bb dd ee"
            Pattern pat5 = RegularPattern.compile("^(aa)(bb)(cc|dd)(ee)$");
            Assert.IsTrue(SimpleMatcher.matches(pat5, "aa bb cc ee"));
            Assert.IsTrue(SimpleMatcher.matches(pat5, "aa bb dd ee"));
            Assert.IsFalse(SimpleMatcher.matches(pat5, "aa bb cc dd ee"));
            Assert.IsFalse(SimpleMatcher.matches(pat5, "aa bb ee"));
        }

        [TestMethod()]
        public void testNeq()
        {
            // "bb" "cc" "dd"
            Pattern pat = RegularPattern.compile("^![aa]$");
            Assert.IsTrue(SimpleMatcher.matches(pat, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "bb aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "aa"));

            // "cc" "dd"
            Pattern pat2 = RegularPattern.compile("^![aa,bb]$");
            Assert.IsTrue(SimpleMatcher.matches(pat2, "cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "bb"));

            // "aa" "cc" "dd"
            Pattern pat3 = RegularPattern.compile("^aa|![bb]$");
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "aa bb"));

            // "aa dd dd" "aa aa dd"
            Pattern pat6 = RegularPattern.compile("^(aa)![bb,cc](dd)$");
            Assert.IsTrue(SimpleMatcher.matches(pat6, "aa dd dd"));
            Assert.IsTrue(SimpleMatcher.matches(pat6, "aa aa dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat6, "aa bb dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat6, "aa cc dd"));
            Assert.IsFalse(SimpleMatcher.matches(pat6, "aa dd"));
        }

        [TestMethod()]
        public void testDot()
        {
            Pattern pat0 = RegularPattern.compile("^.$");
            Assert.IsTrue(SimpleMatcher.matches(pat0, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat0, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat0, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat0, "aa bb"));

            Pattern pat0a = RegularPattern.compile("^.?$");
            Assert.IsTrue(SimpleMatcher.matches(pat0a, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat0a, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat0a, ""));
            Assert.IsFalse(SimpleMatcher.matches(pat0a, "aa bb"));

            // "aa" "bb cc aa"
            Pattern pat = RegularPattern.compile("^.*aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat, "aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "aa aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "bb aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "bb cc aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat, "bb cc dd aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat, "aa bb"));

            // "bb aa" "bb cc dd aa"
            Pattern pat2 = RegularPattern.compile("^.+aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat2, "aa aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb cc aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb cc dd aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa bb"));

            Pattern pat3 = RegularPattern.compile("^aa.*aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa bb aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "aa aa aa aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat3, "bb aa"));

            Pattern pat4 = RegularPattern.compile("^aa.+aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat4, "aa bb aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat4, "aa aa aa aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "aa aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "bb aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat4, "bb bb aa"));

            Pattern pat5 = RegularPattern.compile("^aa.?aa$");
            Assert.IsTrue(SimpleMatcher.matches(pat5, "aa aa"));
            Assert.IsTrue(SimpleMatcher.matches(pat5, "aa bb aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat5, "aa aa aa aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat5, "bb aa"));
        }

        [TestMethod()]
        public void testMult()
        {
            // "bb" or ""
            Pattern pat1 = RegularPattern.compile("^(bb)?$");
            Assert.IsTrue(SimpleMatcher.matches(pat1, ""));
            Assert.IsTrue(SimpleMatcher.matches(pat1, "bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat1, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat1, "bb bb"));

            // "aa bb cc" or "aa cc"
            Pattern pat1a = RegularPattern.compile("^aa(bb)?cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat1a, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat1a, "aa bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat1a, "aa bb bb cc"));

            // "bb bb" "bb" ""
            Pattern pat2 = RegularPattern.compile("^(bb)*$");
            Assert.IsTrue(SimpleMatcher.matches(pat2, ""));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat2, "bb bb bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "bb aa"));
            Assert.IsFalse(SimpleMatcher.matches(pat2, "bb aa bb"));

            // "aa bb bb cc" "aa bb cc" "aa cc"
            Pattern pat2a = RegularPattern.compile("^aa(bb)*cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat2a, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2a, "aa bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2a, "aa bb bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2a, "aa cc bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat2a, "aa cc bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2a, "aa dd cc"));

            // "bb bb" "bb"
            Pattern pat3 = RegularPattern.compile("^(bb)+$");
            Assert.IsTrue(SimpleMatcher.matches(pat3, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "bb bb"));
            Assert.IsTrue(SimpleMatcher.matches(pat3, "bb bb bb"));

            // "aa bb bb bb cc" "aa bb cc"
            Pattern pat3a = RegularPattern.compile("^aa(bb)+cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat3a, "aa bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat3a, "aa bb bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat3a, "aa cc bb"));
            Assert.IsFalse(SimpleMatcher.matches(pat3a, "aa cc bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat3a, "aa dd cc"));
        }

        [TestMethod()]
        public void testMultEx()
        {
            // "aa bb dd cc" "aa cc"
            Pattern pat1b = RegularPattern.compile("^aa((bb)(dd))?cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat1b, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat1b, "aa bb dd cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat1b, "aa dd cc"));

            // "aa bb cc" "aa dd cc" "aa cc"
            Pattern pat1c = RegularPattern.compile("^aa((bb)|(dd))?cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat1c, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat1c, "aa bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat1c, "aa dd cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat1c, "aa bb dd cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat1c, "aa dd dd cc"));

            // "aa bb dd bb dd cc" "aa bb dd cc" "aa cc"
            Pattern pat2b = RegularPattern.compile("^aa((bb)(dd))*cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat2b, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2b, "aa bb dd cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2b, "aa bb dd bb dd cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa dd cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa bb dd bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa bb bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa dd bb cc"));
            Assert.IsFalse(SimpleMatcher.matches(pat2b, "aa bb dd dd bb cc"));

            // "aa bb bb dd cc" "aa dd cc" "aa bb cc" "aa dd bb dd cc"
            Pattern pat2c = RegularPattern.compile("^aa((bb)|(dd))*cc$");
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa dd cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa bb dd cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa dd bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa bb bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa bb bb dd bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(pat2c, "aa dd bb dd dd bb cc"));

            Pattern patextr1 = RegularPattern.compile("^bb|(dd)*$");
            Assert.IsTrue(SimpleMatcher.matches(patextr1, "bb"));
            Assert.IsTrue(SimpleMatcher.matches(patextr1, "dd"));
            Assert.IsTrue(SimpleMatcher.matches(patextr1, "dd dd"));
            Assert.IsTrue(SimpleMatcher.matches(patextr1, "dd dd dd"));
            Assert.IsFalse(SimpleMatcher.matches(patextr1, "bb dd"));
            Assert.IsFalse(SimpleMatcher.matches(patextr1, "dd bb"));

            // "aa bb cc" "aa dd dd cc" "aa dd dd bb dd bb cc"
            Pattern patextr2 = RegularPattern.compile("^aa(bb|(dd)*)+cc$");
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa cc"));
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa dd cc"));
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa dd dd cc"));
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa bb dd dd bb cc"));
            Assert.IsTrue(SimpleMatcher.matches(patextr2, "aa bb bb cc"));
        }

        [TestMethod()]
        public void testAdv()
        {
            Pattern rw = RegularPattern.compile("^(write)(![write,read]*(write)![write,read]*)+(read)$");
            Assert.IsTrue(SimpleMatcher.matches(rw, "write write read"));
            Assert.IsTrue(SimpleMatcher.matches(rw, "write write write read"));
            Assert.IsTrue(SimpleMatcher.matches(rw, "write foo write read"));
            Assert.IsTrue(SimpleMatcher.matches(rw, "write foo foo write read"));
            Assert.IsTrue(SimpleMatcher.matches(rw, "write foo foo write foo read"));
            Assert.IsFalse(SimpleMatcher.matches(rw, "write read"));
        }
    }
}
