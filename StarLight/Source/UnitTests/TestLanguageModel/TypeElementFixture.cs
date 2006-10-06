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
    public class TypeElementFixture
    {
        public TypeElementFixture()
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

        [Owner("Michiel van Oudheusden"), Description("Test if the TypeElement class persists the strings."), TestMethod]
        public void TypeElementPersistStringData()
        {
            string testValue = "test";
            TypeElement ti = new TypeElement(testValue);
            ti.FullName = testValue;
            ti.BaseType = testValue;
            ti.Name = testValue;
            ti.Assembly = testValue;
            
            Assert.AreEqual(testValue, ti.FullName, "Fullname does not persists data"); 
            Assert.AreEqual(testValue,ti.BaseType, "BaseType does not persists data"); 
            Assert.AreEqual(testValue, ti.Name, "Name does not persists data"); 
            Assert.AreEqual(testValue, ti.Assembly, "Assembly does not persists data"); 
        }

        [Description("Test if the TypeElement class persists the booleans."), Owner("Michiel van Oudheusden"), TestMethod]
        public void TypeElementPersistBoolData()
        {
            TypeElement ti = new TypeElement("test");
            bool testValue = true;

            ti.IsAbstract = testValue;
            ti.IsEnum = testValue;
            ti.IsInterface = testValue;
            ti.IsSealed = testValue;
            ti.IsValueType = testValue;

            Assert.AreEqual(testValue, ti.IsAbstract, "IsAbstract does not persists data");
            Assert.AreEqual(testValue, ti.IsEnum, "IsEnum does not persists data");
            Assert.AreEqual(testValue, ti.IsInterface, "IsInterface does not persists data");
            Assert.AreEqual(testValue, ti.IsValueType, "IsValueType does not persists data");
            Assert.AreEqual(testValue, ti.IsSealed, "IsSealed does not persists data");
        }

       
    }
}
