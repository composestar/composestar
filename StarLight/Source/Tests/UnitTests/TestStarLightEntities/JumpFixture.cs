// The following code was generated by Microsoft Visual Studio 2005.
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
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.Instructions.Jump and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.Instructions.Jump Unit Tests
    ///</summary>
    [TestClass()]
    public class JumpFixture
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
            JumpInstruction target = new JumpInstruction();

            IVisitor visitor = null; // TODO: Initialize to an appropriate value

            target.Accept(visitor);

            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for Jump ()
        ///</summary>
        [TestMethod()]
        public void ConstructorTest()
        {
            JumpInstruction target = new JumpInstruction();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Jump (int)
        ///</summary>
        [TestMethod()]
        public void ConstructorTest1()
        {
            int target_target1 = 0; // TODO: Initialize to an appropriate value

            JumpInstruction target2 = new JumpInstruction(target_target1);

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for Target
        ///</summary>
        [TestMethod()]
        public void TargetTest()
        {
            JumpInstruction target = new JumpInstruction();

            int val = 0; // TODO: Assign to an appropriate value for the property

            target.Target = val;


            Assert.AreEqual(val, target.Target, "Composestar.StarLight.Entities.WeaveSpec.Instructions.Jump.Target was not set cor" +
                    "rectly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
