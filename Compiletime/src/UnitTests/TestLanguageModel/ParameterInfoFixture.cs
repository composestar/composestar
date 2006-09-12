using System;
using System.Text;
using System.Collections.Generic;
 
using Composestar.Repository.LanguageModel;  
using Composestar.Repository.LanguageModel.InlineInstructions; 
using Composestar.Repository.LanguageModel.ConditionExpressions;

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
    public class ParameterInfoFixture
    {
        public ParameterInfoFixture()
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

        [Owner("Michiel van Oudheusden"), Description("Test if the ParameterInfo class persists the strings."), TestMethod]
        public void ParameterInfoPersistStringData()
        {
            ParameterInfo pi = new ParameterInfo();
            
            string testValue = "test";
            
            pi.Name = testValue;
            pi.ParameterType = testValue;

            Assert.AreEqual(testValue, pi.Name, "Name does not persists data"); 
            Assert.AreEqual(testValue,pi.ParameterType, "ParameterType does not persists data");           
        }

        [Description("Test if the ParameterInfo class persists the integers."), Owner("Michiel van Oudheusden"), TestMethod]
        public void ParameterInfoPersistBoolData()
        {
            ParameterInfo pi = new ParameterInfo();
            
            short testValue = 1;
            
            pi.Ordinal  = testValue;
            
            Assert.AreEqual(testValue, pi.Ordinal, "Ordinal does not persists data");        
        }

        [Description("Test if the ParameterInfo class persists the paramater type."), Owner("Michiel van Oudheusden"), TestMethod]
        public void ParameterInfoPersistParameterType()
        {
            ParameterInfo pi = new ParameterInfo();
            
            System.Reflection.ParameterAttributes testValue = System.Reflection.ParameterAttributes.In;
            System.Reflection.ParameterAttributes testValue2 = System.Reflection.ParameterAttributes.Out;
            
            pi.ParameterAttributes = testValue;
            Assert.AreEqual(testValue, pi.ParameterAttributes, "ParameterAttributes does not persists data");        
            pi.ParameterAttributes = testValue2;
            Assert.AreEqual(testValue2, pi.ParameterAttributes, "ParameterAttributes does not persists data");        
        }
              
    }
}
