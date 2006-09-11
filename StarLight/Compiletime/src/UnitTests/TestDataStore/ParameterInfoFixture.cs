﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
namespace TestDataStore
{
    /// <summary>
    ///This is a test class for Composestar.DataStore.LanguageModel.ParameterInfo and is intended
    ///to contain all Composestar.DataStore.LanguageModel.ParameterInfo Unit Tests
    ///</summary>
    [TestClass()]
    public class ParameterInfoFixture
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
        ///A test for Name
        ///</summary>
        [TestMethod()]
        public void NameTest()
        {
            global::Composestar.DataStore.LanguageModel.ParameterInfo target = new global::Composestar.DataStore.LanguageModel.ParameterInfo();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.Name = val;


            Assert.AreEqual(val, target.Name, "Composestar.DataStore.LanguageModel.ParameterInfo.Name was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Ordinal
        ///</summary>
        [TestMethod()]
        public void OrdinalTest()
        {
            global::Composestar.DataStore.LanguageModel.ParameterInfo target = new global::Composestar.DataStore.LanguageModel.ParameterInfo();

            short val = 0; // TODO: Assign to an appropriate value for the property

            target.Ordinal = val;


            Assert.AreEqual(val, target.Ordinal, "Composestar.DataStore.LanguageModel.ParameterInfo.Ordinal was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParameterAttributes
        ///</summary>
        [TestMethod()]
        public void ParameterAttributesTest()
        {
            global::Composestar.DataStore.LanguageModel.ParameterInfo target = new global::Composestar.DataStore.LanguageModel.ParameterInfo();

            global::System.Reflection.ParameterAttributes val = global::System.Reflection.ParameterAttributes.HasDefault; // TODO: Assign to an appropriate value for the property

            target.ParameterAttributes = val;


            Assert.AreEqual(val, target.ParameterAttributes, "Composestar.DataStore.LanguageModel.ParameterInfo.ParameterAttributes was not set" +
                    " correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for ParameterType
        ///</summary>
        [TestMethod()]
        public void ParameterTypeTest()
        {
            global::Composestar.DataStore.LanguageModel.ParameterInfo target = new global::Composestar.DataStore.LanguageModel.ParameterInfo();

            string val = null; // TODO: Assign to an appropriate value for the property

            target.ParameterType = val;


            Assert.AreEqual(val, target.ParameterType, "Composestar.DataStore.LanguageModel.ParameterInfo.ParameterType was not set corre" +
                    "ctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

      

    }


}
