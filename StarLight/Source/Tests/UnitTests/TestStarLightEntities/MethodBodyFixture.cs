﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.LanguageModel;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.LanguageModel.MethodBody and is intended
    ///to contain all Composestar.StarLight.Entities.LanguageModel.MethodBody Unit Tests
    ///</summary>
    [TestClass()]
    public class MethodBodyFixture
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
        ///A test for Calls
        ///</summary>
        [TestMethod()]
        public void CallsTest()
        {
            MethodBody target = new MethodBody();

            System.Collections.Generic.List<Composestar.StarLight.Entities.LanguageModel.CallElement> val = null; // TODO: Assign to an appropriate value for the property

            target.Calls = val;


            Assert.AreEqual(val, target.Calls, "Composestar.StarLight.Entities.LanguageModel.MethodBody.Calls was not set correct" +
                    "ly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
