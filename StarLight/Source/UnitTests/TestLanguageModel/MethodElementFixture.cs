using System;
using System.Text;
using System.Collections.Generic;

using Composestar.Repository.LanguageModel;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif

namespace TestLanguageModel
{
    /// <summary>
    /// Summary description for MethodElementFixture
    /// </summary>
    [TestClass]
    public class MethodElementFixture
    {
        public MethodElementFixture()
        {
            //
            // TODO: Add constructor logic here
            //
        }

        #region Additional test attributes
        //
        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        // [ClassInitialize()]
        // public static void MyClassInitialize(TestContext testContext) { }
        //
        // Use ClassCleanup to run code after all tests in a class have run
        // [ClassCleanup()]
        // public static void MyClassCleanup() { }
        //
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion

        [Owner("Michiel van Oudheusden"), Description("Test if the MethodElement class persists the strings."), TestMethod]
        public void MethodElementPersistStringData()
        {
            string testValue = "test";
            MethodElement mi = new MethodElement(testValue);
            
            mi.Name = testValue;
            mi.ReturnType = testValue;

            Assert.AreEqual(testValue, mi.ReturnType, "ReturnType does not persists data");
            Assert.AreEqual(testValue, mi.Name, "Name does not persists data");
        }

        [Description("Test if the MethodElement class persists the parameters."), Owner("Michiel van Oudheusden"), TestMethod]
        public void MethodElementPersistParameters()
        {
            MethodElement mi = new MethodElement("");
            ParameterElement pi = new ParameterElement();

            string value = "test";
            pi.Name = value;

            Assert.IsTrue(String.IsNullOrEmpty(pi.ParentMethodId), "Parameter should not have a parent method.");
            pi.ParentMethodId = mi.Id;
            Assert.IsFalse(String.IsNullOrEmpty(pi.ParentMethodId), "Parameter should have a method.");
                       
        }

        [Description("Checks if the methodinfo class stores the methodbody object."), Owner("Michiel van Oudheusden"), TestMethod]
        public void MethodElementStoresMethodBody()
        {
            MethodElement mi = new MethodElement("test");
            Assert.IsNotNull(mi.MethodBody, "MethodBody is null.");
 
            MethodBody mb = new MethodBody("test", mi.Id);
            mi.MethodBody = mb;

            Assert.IsNotNull(mi.MethodBody, "MethodBody is null.");
            Assert.AreEqual(mb, mi.MethodBody, "MethodBody is not stored in MethodElement.");
 
            
        }

     
    }
}
