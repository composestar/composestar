﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.WeaveSpec;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.Condition and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.Condition Unit Tests
    ///</summary>
    [TestClass()]
    public class ConditionFixture
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
        ///A test for Condition ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            Condition target = new Condition();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Name
        ///</summary>
        [TestMethod()]
        public void NameTest()
        {
            Condition target = new Condition();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.Name = val;


            Assert.AreEqual(val, target.Name, "Composestar.StarLight.Entities.WeaveSpec.Condition.Name was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Reference
        ///</summary>
        [TestMethod()]
        public void ReferenceTest()
        {
            Condition target = new Condition();

            Reference val = null; // TODO: Assign to an appropriate value for the property

            target.Reference = val;


            Assert.AreEqual(val, target.Reference, "Composestar.StarLight.Entities.WeaveSpec.Condition.Reference was not set correctl" +
                    "y.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}