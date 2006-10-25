﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Utilities.Cecil;
using Composestar.StarLight.Entities.LanguageModel;
namespace TestILAnalyzer
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.ILAnalyzer.CecilILAnalyzer and is intended
    ///to contain all Composestar.StarLight.ILAnalyzer.CecilILAnalyzer Unit Tests
    ///</summary>
    [TestClass()]
    public class CecilILAnalyzerFixture
    {


        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }
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
        ///A test for AssemblyResolver
        ///</summary>
        [DeploymentItem("Composestar.StarLight.ILAnalyzer.dll")]
        [TestMethod()]
        public void AssemblyResolverTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            StarLightAssemblyResolver val = null; // TODO: Assign to an appropriate value for the property

            TestILAnalyzer.Composestar_StarLight_ILAnalyzer_CecilILAnalyzerAccessor accessor = new TestILAnalyzer.Composestar_StarLight_ILAnalyzer_CecilILAnalyzerAccessor(target);


            Assert.AreEqual(val, accessor.AssemblyResolver, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.AssemblyResolver was not set cor" +
                    "rectly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for CachedTypes
        ///</summary>
        [TestMethod()]
        public void CachedTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.CachedTypes, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.CachedTypes was not set correctl" +
                    "y.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for CecilILAnalyzer (CecilAnalyzerConfiguration, IEntitiesAccessor)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Close ()
        ///</summary>
        [TestMethod()]
        public void CloseTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            target.Close();

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for ExtractAllTypes (string)
        ///</summary>
        [TestMethod()]
        public void ExtractAllTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            string fileName = null; // TODO: Initialize to an appropriate value

            AssemblyElement expected = null;
            AssemblyElement actual;

            actual = target.ExtractAllTypes(fileName);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.ExtractAllTypes did not return t" +
                    "he expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for FilterActions
        ///</summary>
        [TestMethod()]
        public void FilterActionsTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<Composestar.StarLight.Entities.Configuration.FilterActionElement> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.FilterActions, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.FilterActions was not set correc" +
                    "tly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for FilterTypes
        ///</summary>
        [TestMethod()]
        public void FilterTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<Composestar.StarLight.Entities.Configuration.FilterTypeElement> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.FilterTypes, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.FilterTypes was not set correctl" +
                    "y.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for LastDuration
        ///</summary>
        [TestMethod()]
        public void LastDurationTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            TimeSpan val = new TimeSpan(); // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.LastDuration, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.LastDuration was not set correct" +
                    "ly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ProcessUnresolvedTypes (Dictionary&lt;string,string&gt;)
        ///</summary>
        [TestMethod()]
        public void ProcessUnresolvedTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.Dictionary<string, string> assemblyNames = null; // TODO: Initialize to an appropriate value

            System.Collections.Generic.List<Composestar.StarLight.Entities.LanguageModel.AssemblyElement> expected = null;
            System.Collections.Generic.List<Composestar.StarLight.Entities.LanguageModel.AssemblyElement> actual;

            actual = target.ProcessUnresolvedTypes(assemblyNames);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.ProcessUnresolvedTypes did not r" +
                    "eturn the expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ResolveAssemblyLocations ()
        ///</summary>
        [TestMethod()]
        public void ResolveAssemblyLocationsTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> expected = null;
            System.Collections.Generic.List<string> actual;

            actual = target.ResolveAssemblyLocations();

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.ResolveAssemblyLocations did not" +
                    " return the expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ResolvedAssemblies
        ///</summary>
        [TestMethod()]
        public void ResolvedAssembliesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.ResolvedAssemblies, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.ResolvedAssemblies was not set c" +
                    "orrectly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ResolvedTypes
        ///</summary>
        [TestMethod()]
        public void ResolvedTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.ResolvedTypes, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.ResolvedTypes was not set correc" +
                    "tly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for UnresolvedAssemblies
        ///</summary>
        [TestMethod()]
        public void UnresolvedAssembliesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.UnresolvedAssemblies, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.UnresolvedAssemblies was not set" +
                    " correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for UnresolvedTypes
        ///</summary>
        [TestMethod()]
        public void UnresolvedTypesTest()
        {
            CecilAnalyzerConfiguration configuration = null; // TODO: Initialize to an appropriate value

            IEntitiesAccessor entitiesAccessor = null; // TODO: Initialize to an appropriate value

            CecilILAnalyzer target = new CecilILAnalyzer(configuration, entitiesAccessor);

            System.Collections.Generic.List<string> val = null; // TODO: Assign to an appropriate value for the property

            target.UnresolvedTypes = val;


            Assert.AreEqual(val, target.UnresolvedTypes, "Composestar.StarLight.ILAnalyzer.CecilILAnalyzer.UnresolvedTypes was not set corr" +
                    "ectly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
