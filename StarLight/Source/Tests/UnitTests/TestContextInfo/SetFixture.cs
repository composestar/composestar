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
    [TestClass()]
    public class SetFixture
    {
        [TestMethod()]
        public void testSet()
        {
            Set<string> set = new Set<string>();
            Assert.AreEqual(set.Count, 0);
            set.Add("string1");
            set.Add("string2");
            set.Add("string3");
            set.Add("string4");
            set.Add("string5");
            Assert.AreEqual(set.Count, 5);
            set.Add("string3");
            set.Add("string4");
            Assert.AreEqual(set.Count, 5);
            set.Remove("string1");
            Assert.AreEqual(set.Count, 4);
        }

        [TestMethod()]
        public void testSet2()
        {
            List<string> lst = new List<string>();

            lst.Add("string1");
            lst.Add("string2");
            lst.Add("string1");
            lst.Add("string2");
            lst.Add("string1");
            Assert.AreEqual(lst.Count, 5);

            Set<string> set = new Set<string>(lst);            
            Assert.AreEqual(set.Count, 2);

            set.AddRange(lst);
            Assert.AreEqual(set.Count, 2);
        }

        [TestMethod()]
        public void testSet3()
        {
            Set<Object> set = new Set<Object>();
            Object o1, o2, o3;
            o1 = new Object();
            o2 = new Object();
            o3 = new Object();

            set.Add(o1);
            set.Add(o2);
            set.Add(o3);
            Assert.AreEqual(set.Count, 3);

            set.Add(o2);
            Assert.AreEqual(set.Count, 3);

            Object o4 = o2;
            set.Add(o4);
            Assert.AreEqual(set.Count, 3);

            o4 = new Object();
            set.Add(o4);
            Assert.AreEqual(set.Count, 4);

            o2 = o4;
            set.Add(o4);
            set.Add(o2);
            Assert.AreEqual(set.Count, 4);
        }
    }
}
