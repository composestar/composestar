using System;
using System.Text;
using System.Collections.Generic;

using Repository.LanguageModel;

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
    /// Summary description for MethodInfoFixture
    /// </summary>
    [TestClass]
    public class MethodInfoFixture
    {
        public MethodInfoFixture()
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

        [Owner("Michiel van Oudheusden"), Description("Test if the MethodInfo class persists the strings."), TestMethod]
        public void MethodInfoPersistStringData()
        {
            MethodInfo mi = new MethodInfo();
            string testValue = "test";
            mi.Name = testValue;
            mi.ReturnType = testValue;

            Assert.AreEqual(testValue, mi.ReturnType, "ReturnType does not persists data");
            Assert.AreEqual(testValue, mi.Name, "Name does not persists data");
        }

        [Description("Test if the MethodInfo class persists the parameters."), Owner("Michiel van Oudheusden"), TestMethod]
        public void MethodInfoPersistParameters()
        {
            MethodInfo mi = new MethodInfo();
            ParameterInfo pi = new ParameterInfo();

            string v = "test";
            pi.Name = v;

            Assert.IsNull(pi.ParentMethodInfo, "Parameter should not have a parent method.");
            pi.ParentMethodInfo = mi;
            Assert.IsNotNull(pi.ParentMethodInfo, "Parameter should have a method.");
                       
        }

        [Description("Checks if the methodinfo class stores the methodbody object."), Owner("Michiel van Oudheusden"), TestMethod]
        public void MethodInfoStoresMethodBody()
        {
            MethodInfo mi = new MethodInfo();
            Assert.IsNotNull(mi.MethodBody, "MethodBody is null.");
 
            MethodBody mb = new MethodBody();
            mi.MethodBody = mb;

            Assert.IsNotNull(mi.MethodBody, "MethodBody is null.");
            Assert.AreEqual(mb, mi.MethodBody, "MethodBody is not stored in MethodInfo.");
 
            
        }

     
    }
}
