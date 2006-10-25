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
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.Instructions.Label and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.Instructions.Label Unit Tests
    ///</summary>
    [TestClass()]
    public class LabelFixture
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
            Label target = new Label();

            IVisitor visitor = null; // TODO: Initialize to an appropriate value

            target.Accept(visitor);

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for Id
        ///</summary>
        [TestMethod()]
        public void IdTest()
        {
            Label target = new Label();

            int val = 0; // TODO: Assign to an appropriate value for the property

            target.Id = val;


            Assert.AreEqual(val, target.Id, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Label.Id was not set correc" +
                    "tly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Label ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            Label target = new Label();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Label (int)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest1()
        {
            int id = 0; // TODO: Initialize to an appropriate value

            Label target = new Label(id);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

    }


}