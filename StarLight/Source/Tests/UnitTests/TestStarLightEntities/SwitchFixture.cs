﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
namespace TestStarLightEntities
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.Instructions.Switch and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.Instructions.Switch Unit Tests
    ///</summary>
    [TestClass()]
    public class SwitchFixture
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
            SwitchInstruction target = new SwitchInstruction();

            IVisitor visitor = null; // TODO: Initialize to an appropriate value

            target.Accept(visitor);

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for Cases
        ///</summary>
        [TestMethod()]
        public void CasesTest()
        {
            SwitchInstruction target = new SwitchInstruction();

            System.Collections.Generic.List<Composestar.StarLight.Entities.WeaveSpec.Instructions.CaseInstruction> val = null; // TODO: Assign to an appropriate value for the property

            target.Cases = val;


            Assert.AreEqual(val, target.Cases, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Switch.Cases was not set co" +
                    "rrectly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Expression
        ///</summary>
        [TestMethod()]
        public void ExpressionTest()
        {
            SwitchInstruction target = new SwitchInstruction();

            ContextExpression val = ContextExpression.HasMoreActions; // TODO: Assign to an appropriate value for the property

            target.Expression = val;


            Assert.AreEqual(val, target.Expression, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Switch.Expression was not s" +
                    "et correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Switch ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            SwitchInstruction target = new SwitchInstruction();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Switch (ContextExpression)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest1()
        {
            ContextExpression expression = ContextExpression.HasMoreActions; // TODO: Initialize to an appropriate value

            SwitchInstruction target = new SwitchInstruction(expression);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

    }


}
