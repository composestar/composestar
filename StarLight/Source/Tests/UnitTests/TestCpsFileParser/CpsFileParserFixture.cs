// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
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
using Composestar.StarLight.CpsParser;
using antlr.collections;

namespace Composestar.StarLight.CpsParser.Tests.UnitTests
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.CpsParser.CpsFileParserEx and is intended
    ///to contain all Composestar.StarLight.CpsParser.CpsFileParserEx Unit Tests
    ///</summary>
    [TestClass()]
    public class CpsFileParserFixture : CpsFileParserFixtureBase
    {

# if !NUNIT
        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get { return testContextInstance; }
            set { testContextInstance = value; }
        }

#endif

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion

        /// <summary>
        ///A test for CpsFileParserEx (CpsParserConfiguration)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
			string filename = base.GetPathToConcern("test.cps");
            CpsFileParserEx target = new CpsFileParserEx(filename);

            Assert.IsNotNull(target, "Composestar.StarLight.CpsParser.CpsFileParserEx constructor did not return an object.");
        }

#if !NUNIT
        /// <summary>
        ///A test for FileName
        ///</summary>
        [DeploymentItem("Composestar.StarLight.CpsParser.dll")]
        [TestMethod()]
        public void FileNameTest()
        {
            string val = "test.cps";

            CpsParserConfiguration configuration = new CpsParserConfiguration(val);

            CpsFileParserEx target = new CpsFileParserEx(configuration);

            Composestar.StarLight.CpsParser.Tests.UnitTests.Composestar_StarLight_CpsParser_CpsFileParserExAccessor accessor = new Composestar.StarLight.CpsParser.Tests.UnitTests.Composestar_StarLight_CpsParser_CpsFileParserExAccessor(target);
             
            Assert.AreEqual(val, accessor.FileName, "Composestar.StarLight.CpsParser.CpsFileParserEx.FileName was not set correctly.");
        }
#endif

        [TestMethod()]
        [ExpectedException(typeof(Composestar.StarLight.CoreServices.Exceptions.CpsParserException)) ]
        public void InvalidConcernFileTest()
        {
			string filename = base.GetPathToConcern("invalid_concern.cps");
			CpsFileParserEx target = new CpsFileParserEx(filename);

            target.Parse();
            Assert.Fail("Composestar.StarLight.CpsParser.CpsFileParserEx.Parse did not return the expected Composestar.StarLight.CoreServices.Exceptions.CpsParserException.");
          
        }
        
        /// <summary>
        ///A test for HasOutputFilters
        ///</summary>
        [TestMethod()]
        public void HasNoOutputFiltersTest()
        {
			string filename = base.GetPathToConcern("Logging.cps");
			CpsFileParserEx target = new CpsFileParserEx(filename);

            target.Parse();
            
            Assert.AreEqual(false, target.HasOutputFilters, "Composestar.StarLight.CpsParser.CpsFileParserEx.HasOutputFilters was not set correctly.");
        }

        /// <summary>
        ///A test for HasOutputFilters
        ///</summary>
        [TestMethod()]
        public void HasOutputFiltersTest()
        {
            string filename = base.GetPathToConcern("platypus.cps");
            CpsFileParserEx target = new CpsFileParserEx(filename);

            target.Parse();

            Assert.AreEqual(true, target.HasOutputFilters, "Composestar.StarLight.CpsParser.CpsFileParserEx.HasOutputFilters was not set correctly.");
        }

        /// <summary>
        ///A test for ReferencedTypes
        ///</summary>
        [TestMethod()]
        public void ReferencedTypesCountTest()
        {
			string filename = base.GetPathToConcern("law.cps");
            CpsFileParserEx target = new CpsFileParserEx(filename);

            target.Parse();

            Assert.AreEqual(2, target.ReferencedTypes.Count, "Composestar.StarLight.CpsParser.CpsFileParserEx.ReferencedTypes does not contain the expected two types.");
        }

        /// <summary>
        ///A test to check that ReferencedTypes does not return a null value if there are no referenced types
        ///</summary>
        [TestMethod()]
        public void ReferencedTypesTest()
        {
			string filename = base.GetPathToConcern("bulkupdates.cps");
            CpsFileParserEx target = new CpsFileParserEx(filename);

            target.Parse();

            Assert.IsNotNull(target.ReferencedTypes, "Composestar.StarLight.CpsParser.CpsFileParserEx.ReferencedTypes was not set correctly.");
            Assert.AreEqual(0, target.ReferencedTypes.Count, "Composestar.StarLight.CpsParser.CpsFileParserEx.ReferencedTypes returned unexpected count.");
        }
    }
}
