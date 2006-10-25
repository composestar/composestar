﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.Configuration;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.Configuration.ConfigurationContainer and is intended
    ///to contain all Composestar.StarLight.Entities.Configuration.ConfigurationContainer Unit Tests
    ///</summary>
    [TestClass()]
    public class ConfigurationContainerFixture
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
        ///A test for Assemblies
        ///</summary>
        [TestMethod()]
        public void AssembliesTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            System.Collections.Generic.List<Composestar.StarLight.Entities.Configuration.AssemblyConfig> val = null; // TODO: Assign to an appropriate value for the property

            target.Assemblies = val;


            Assert.AreEqual(val, target.Assemblies, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.Assemblies wa" +
                    "s not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for CompiletimeDebugLevel
        ///</summary>
        [TestMethod()]
        public void CompiletimeDebugLevelTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            short val = 0; // TODO: Assign to an appropriate value for the property

            target.CompiletimeDebugLevel = val;


            Assert.AreEqual(val, target.CompiletimeDebugLevel, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.CompiletimeDe" +
                    "bugLevel was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Concerns
        ///</summary>
        [TestMethod()]
        public void ConcernsTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            System.Collections.Generic.List<Composestar.StarLight.Entities.Concerns.ConcernElement> val = null; // TODO: Assign to an appropriate value for the property

            target.Concerns = val;


            Assert.AreEqual(val, target.Concerns, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.Concerns was " +
                    "not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for FilterActions
        ///</summary>
        [TestMethod()]
        public void FilterActionsTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            System.Collections.Generic.List<Composestar.StarLight.Entities.Configuration.FilterActionElement> val = null; // TODO: Assign to an appropriate value for the property

            target.FilterActions = val;


            Assert.AreEqual(val, target.FilterActions, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.FilterActions" +
                    " was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for FilterTypes
        ///</summary>
        [TestMethod()]
        public void FilterTypesTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            System.Collections.Generic.List<Composestar.StarLight.Entities.Configuration.FilterTypeElement> val = null; // TODO: Assign to an appropriate value for the property

            target.FilterTypes = val;


            Assert.AreEqual(val, target.FilterTypes, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.FilterTypes w" +
                    "as not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for InstallFolder
        ///</summary>
        [TestMethod()]
        public void InstallFolderTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.InstallFolder = val;


            Assert.AreEqual(val, target.InstallFolder, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.InstallFolder" +
                    " was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for IntermediateOutputPath
        ///</summary>
        [TestMethod()]
        public void IntermediateOutputPathTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.IntermediateOutputPath = val;


            Assert.AreEqual(val, target.IntermediateOutputPath, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.IntermediateO" +
                    "utputPath was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for SpecificationFILTH
        ///</summary>
        [TestMethod()]
        public void SpecificationFILTHTest()
        {
            ConfigurationContainer target = new ConfigurationContainer();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.SpecificationFILTH = val;


            Assert.AreEqual(val, target.SpecificationFILTH, "Composestar.StarLight.Entities.Configuration.ConfigurationContainer.Specification" +
                    "FILTH was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}