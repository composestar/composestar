using System;
using System.Configuration;
using System.Text;
using System.Collections.Generic;
using System.IO;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif

namespace Composestar.StarLight.CpsParser.Tests.UnitTests
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    public abstract class CpsFileParserFixtureBase
    {
        public CpsFileParserFixtureBase()
        {

        }

        #region Additional test attributes
        //
        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        // [ClassInitialize()]
        // public static void MyClassInitialize(TestContext testContext) { }
        //
        // Use ClassCleanup to run code after all tests in a class have run
        // [ClassCleanup()]
        // public static void MyClassCleanup() { }
        //
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion

        /// <summary>
        /// Gets the path to concern.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        internal string GetPathToConcern(string fileName)
        {
#if !UNIT
            return string.Format(@"..\..\..\..\Concerns\{0}", fileName);
#else
            return string.Format("../../../Concerns/{0}", fileName);
#endif
        }
    }
}
