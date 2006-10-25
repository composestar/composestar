﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILAnalyzer;
namespace TestILAnalyzer
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration and is intended
    ///to contain all Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration Unit Tests
    ///</summary>
    [TestClass()]
    public class CecilAnalyzerConfigurationTest
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
        ///A test for BinFolder
        ///</summary>
        [TestMethod()]
        public void BinFolderTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            string val = null; // TODO: Assign to an appropriate value for the property

            target.BinFolder = val;


            Assert.AreEqual(val, target.BinFolder, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.BinFolder was not set" +
                    " correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for CecilAnalyzerConfiguration (string)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for CreateDefaultConfiguration (string)
        ///</summary>
        [TestMethod()]
        public void CreateDefaultConfigurationTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration expected = null;
            CecilAnalyzerConfiguration actual;

            actual = Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.CreateDefaultConfiguration(repositoryFilename);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.CreateDefaultConfigur" +
                    "ation did not return the expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for DoFieldAnalysis
        ///</summary>
        [TestMethod()]
        public void DoFieldAnalysisTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            bool val = false; // TODO: Assign to an appropriate value for the property

            target.DoFieldAnalysis = val;


            Assert.AreEqual(val, target.DoFieldAnalysis, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.DoFieldAnalysis was n" +
                    "ot set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for DoMethodCallAnalysis
        ///</summary>
        [TestMethod()]
        public void DoMethodCallAnalysisTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            bool val = false; // TODO: Assign to an appropriate value for the property

            target.DoMethodCallAnalysis = val;


            Assert.AreEqual(val, target.DoMethodCallAnalysis, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.DoMethodCallAnalysis " +
                    "was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ExtractUnresolvedOnly
        ///</summary>
        [TestMethod()]
        public void ExtractUnresolvedOnlyTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            bool val = false; // TODO: Assign to an appropriate value for the property

            target.ExtractUnresolvedOnly = val;


            Assert.AreEqual(val, target.ExtractUnresolvedOnly, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.ExtractUnresolvedOnly" +
                    " was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for RepositoryFilename
        ///</summary>
        [TestMethod()]
        public void RepositoryFilenameTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            string val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, target.RepositoryFilename, "Composestar.StarLight.ILAnalyzer.CecilAnalyzerConfiguration.RepositoryFilename wa" +
                    "s not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for RuntimeValidate ()
        ///</summary>
        [DeploymentItem("Composestar.StarLight.ILAnalyzer.dll")]
        [TestMethod()]
        public void RuntimeValidateTest()
        {
            string repositoryFilename = null; // TODO: Initialize to an appropriate value

            CecilAnalyzerConfiguration target = new CecilAnalyzerConfiguration(repositoryFilename);

            TestILAnalyzer.Composestar_StarLight_ILAnalyzer_CecilAnalyzerConfigurationAccessor accessor = new TestILAnalyzer.Composestar_StarLight_ILAnalyzer_CecilAnalyzerConfigurationAccessor(target);

            accessor.RuntimeValidate();

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

    }


}