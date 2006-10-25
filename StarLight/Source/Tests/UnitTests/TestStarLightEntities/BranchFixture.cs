﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.Instructions.Branch and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.Instructions.Branch Unit Tests
    ///</summary>
    [TestClass()]
    public class BranchFixture
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
            Branch target = new Branch();

            IVisitor visitor = null; // TODO: Initialize to an appropriate value

            target.Accept(visitor);

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for Branch ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            Branch target = new Branch();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Branch (ConditionExpression)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest1()
        {
            ConditionExpression conditionExpression = null; // TODO: Initialize to an appropriate value

            Branch target = new Branch(conditionExpression);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for ConditionExpression
        ///</summary>
        [TestMethod()]
        public void ConditionExpressionTest()
        {
            Branch target = new Branch();

            ConditionExpression val = null; // TODO: Assign to an appropriate value for the property

            target.ConditionExpression = val;


            Assert.AreEqual(val, target.ConditionExpression, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Branch.ConditionExpression " +
                    "was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for FalseBlock
        ///</summary>
        [TestMethod()]
        public void FalseBlockTest()
        {
            Branch target = new Branch();

            Block val = null; // TODO: Assign to an appropriate value for the property

            target.FalseBlock = val;


            Assert.AreEqual(val, target.FalseBlock, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Branch.FalseBlock was not s" +
                    "et correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for TrueBlock
        ///</summary>
        [TestMethod()]
        public void TrueBlockTest()
        {
            Branch target = new Branch();

            Block val = null; // TODO: Assign to an appropriate value for the property

            target.TrueBlock = val;


            Assert.AreEqual(val, target.TrueBlock, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Branch.TrueBlock was not se" +
                    "t correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}