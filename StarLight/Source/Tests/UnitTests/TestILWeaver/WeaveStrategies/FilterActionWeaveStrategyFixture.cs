﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILWeaver;
namespace TestILWeaver
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.ILWeaver.FilterActionWeaveStrategy and is intended
    ///to contain all Composestar.StarLight.ILWeaver.FilterActionWeaveStrategy Unit Tests
    ///</summary>
    [TestClass()]
    public class FilterActionWeaveStrategyTest
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
        ///A test for CreateStrategyMapping ()
        ///</summary>
        [DeploymentItem("Composestar.StarLight.ILWeaver.dll")]
        [TestMethod()]
        public void CreateStrategyMappingTest()
        {
            TestILWeaver.Composestar_StarLight_ILWeaver_FilterActionWeaveStrategyAccessor.CreateStrategyMapping();

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for GetFilterActionWeaveStrategy (string)
        ///</summary>
        [TestMethod()]
        public void GetFilterActionWeaveStrategyTest()
        {
            string filterAction = null; // TODO: Initialize to an appropriate value

            FilterActionWeaveStrategy expected = null;
            FilterActionWeaveStrategy actual;

            actual = Composestar.StarLight.ILWeaver.FilterActionWeaveStrategy.GetFilterActionWeaveStrategy(filterAction);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ILWeaver.FilterActionWeaveStrategy.GetFilterActionWeaveStra" +
                    "tegy did not return the expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
