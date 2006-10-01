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
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class ParameterElementFixture
    {
        public ParameterElementFixture()
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

        [Owner("Michiel van Oudheusden"), Description("Test if the ParameterElement class persists the strings."), TestMethod]
        public void ParameterElementPersistStringData()
        {
            ParameterElement pi = new ParameterElement();
            
            string testValue = "test";
            
            pi.Name = testValue;
            pi.ParameterType = testValue;
            pi.ParameterAttributes = testValue;

            Assert.AreEqual(testValue, pi.Name, "Name does not persists data"); 
            Assert.AreEqual(testValue,pi.ParameterType, "ParameterType does not persists data");       
            Assert.AreEqual(testValue,pi.ParameterAttributes, "ParameterAttributes does not persists data");    
        }

        [Description("Test if the ParameterElement class persists the integers."), Owner("Michiel van Oudheusden"), TestMethod]
        public void ParameterElementPersistBoolData()
        {
            ParameterElement pi = new ParameterElement();
            
            short testValue = 1;
            
            pi.Ordinal  = testValue;
            
            Assert.AreEqual(testValue, pi.Ordinal, "Ordinal does not persists data");        
        }

                    
    }
}
