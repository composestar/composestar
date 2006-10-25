﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.And and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.And Unit Tests
    ///</summary>
    [TestClass()]
    public class AndFixture
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
        ///A test for Accept (IVisitor)
        ///</summary>
        [TestMethod()]
        public void AcceptTest()
        {
            And target = new And();

            IVisitor visitor = null; // TODO: Initialize to an appropriate value

            target.Accept(visitor);

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for And ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            And target = new And();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for BranchId
        ///</summary>
        [TestMethod()]
        public void BranchIdTest()
        {
            And target = new And();

            int val = 0; // TODO: Assign to an appropriate value for the property

            target.BranchId = val;


            Assert.AreEqual(val, target.BranchId, "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.And.BranchId was no" +
                    "t set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Left
        ///</summary>
        [TestMethod()]
        public void LeftTest()
        {
            And target = new And();

            ConditionExpression val = null; // TODO: Assign to an appropriate value for the property

            target.Left = val;


            Assert.AreEqual(val, target.Left, "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.And.Left was not se" +
                    "t correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Right
        ///</summary>
        [TestMethod()]
        public void RightTest()
        {
            And target = new And();

            ConditionExpression val = null; // TODO: Assign to an appropriate value for the property

            target.Right = val;


            Assert.AreEqual(val, target.Right, "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.And.Right was not s" +
                    "et correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
