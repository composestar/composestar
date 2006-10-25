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
    ///This is a test class for Composestar.StarLight.Entities.WeaveSpec.WeaveSpecification and is intended
    ///to contain all Composestar.StarLight.Entities.WeaveSpec.WeaveSpecification Unit Tests
    ///</summary>
    [TestClass()]
    public class WeaveSpecificationFixture
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
        ///A test for AssemblyName
        ///</summary>
        [TestMethod()]
        public void AssemblyNameTest()
        {
            WeaveSpecification target = new WeaveSpecification();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.AssemblyName = val;


            Assert.AreEqual(val, target.AssemblyName, "Composestar.StarLight.Entities.WeaveSpec.WeaveSpecification.AssemblyName was not " +
                    "set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for WeaveTypes
        ///</summary>
        [TestMethod()]
        public void WeaveTypesTest()
        {
            WeaveSpecification target = new WeaveSpecification();

            System.Collections.Generic.List<Composestar.StarLight.Entities.WeaveSpec.WeaveType> val = null; // TODO: Assign to an appropriate value for the property

            target.WeaveTypes = val;


            Assert.AreEqual(val, target.WeaveTypes, "Composestar.StarLight.Entities.WeaveSpec.WeaveSpecification.WeaveTypes was not se" +
                    "t correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}
