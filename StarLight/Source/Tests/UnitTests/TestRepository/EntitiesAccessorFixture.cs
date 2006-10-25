﻿// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Text;
using System.Collections.Generic;
using Composestar.Repository;
namespace TestRepository
{
    /// <summary>
    ///This is a test class for Composestar.Repository.EntitiesAccessor and is intended
    ///to contain all Composestar.Repository.EntitiesAccessor Unit Tests
    ///</summary>
    [TestClass()]
    public class EntitiesAccessorFixture
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
        ///A test for EntitiesAccessor ()
        ///</summary>
        [DeploymentItem("Composestar.StarLight.DataStore.dll")]
        [TestMethod()]
        public void ConstructorTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            // TODO: Implement code to verify target
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for ExtraTypes
        ///</summary>
        [DeploymentItem("Composestar.StarLight.DataStore.dll")]
        [TestMethod()]
        public void ExtraTypesTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            Type[] val = null; // TODO: Assign to an appropriate value for the property

            TestRepository.Composestar_Repository_EntitiesAccessorAccessor accessor = new TestRepository.Composestar_Repository_EntitiesAccessorAccessor(target);


            CollectionAssert.AreEqual(val, accessor.ExtraTypes, "Composestar.Repository.EntitiesAccessor.ExtraTypes was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for Instance
        ///</summary>
        [TestMethod()]
        public void InstanceTest()
        {
            EntitiesAccessor val = null; // TODO: Assign to an appropriate value for the property


            Assert.AreEqual(val, Composestar.Repository.EntitiesAccessor.Instance, "Composestar.Repository.EntitiesAccessor.Instance was not set correctly.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for LoadAssemblyElement (string)
        ///</summary>
        [TestMethod()]
        public void LoadAssemblyElementTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            string filename = null; // TODO: Initialize to an appropriate value

            AssemblyElement expected = 0;
            AssemblyElement actual;

            actual = target.LoadAssemblyElement(filename);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.LoadAssemblyElement did not return the ex" +
                    "pected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for LoadConfiguration (string)
        ///</summary>
        [TestMethod()]
        public void LoadConfigurationTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            string filename = null; // TODO: Initialize to an appropriate value

            ConfigurationContainer expected = 0;
            ConfigurationContainer actual;

            actual = target.LoadConfiguration(filename);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.LoadConfiguration did not return the expe" +
                    "cted value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for LoadWeaveSpecification (string)
        ///</summary>
        [TestMethod()]
        public void LoadWeaveSpecificationTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            string filename = null; // TODO: Initialize to an appropriate value

            WeaveSpecification expected = 0;
            WeaveSpecification actual;

            actual = target.LoadWeaveSpecification(filename);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.LoadWeaveSpecification did not return the" +
                    " expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for SaveAssemblyElement (string, AssemblyElement)
        ///</summary>
        [TestMethod()]
        public void SaveAssemblyElementTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            string filename = null; // TODO: Initialize to an appropriate value

            AssemblyElement assemblyElement = 0; // TODO: Initialize to an appropriate value

            bool expected = false;
            bool actual;

            actual = target.SaveAssemblyElement(filename, assemblyElement);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.SaveAssemblyElement did not return the ex" +
                    "pected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for SaveConfiguration (string, ConfigurationContainer)
        ///</summary>
        [TestMethod()]
        public void SaveConfigurationTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            string filename = null; // TODO: Initialize to an appropriate value

            ConfigurationContainer configContainer = 0; // TODO: Initialize to an appropriate value

            bool expected = false;
            bool actual;

            actual = target.SaveConfiguration(filename, configContainer);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.SaveConfiguration did not return the expe" +
                    "cted value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for SaveWeaveSpecification (WeaveSpecification, string)
        ///</summary>
        [TestMethod()]
        public void SaveWeaveSpecificationTest()
        {
            EntitiesAccessor target = TestRepository.Composestar_Repository_EntitiesAccessorAccessor.CreatePrivate();

            WeaveSpecification weaveSpecification = 0; // TODO: Initialize to an appropriate value

            string filename = null; // TODO: Initialize to an appropriate value

            bool expected = false;
            bool actual;

            actual = target.SaveWeaveSpecification(weaveSpecification, filename);

            Assert.AreEqual(expected, actual, "Composestar.Repository.EntitiesAccessor.SaveWeaveSpecification did not return the" +
                    " expected value.");
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }


}