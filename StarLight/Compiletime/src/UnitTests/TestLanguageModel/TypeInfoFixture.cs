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
    public class TypeInfoFixture
    {
        public TypeInfoFixture()
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

        [Owner("Michiel van Oudheusden"), Description("Test if the TypeInfo class persists the strings."), TestMethod]
        public void TypeInfoPersistStringData()
        {
            TypeInfo ti = new TypeInfo();
            string testValue = "test";
            ti.FullName = testValue;
            ti.BaseType = testValue;
            ti.Name = testValue;
            
            Assert.AreEqual(testValue, ti.FullName, "Fullname does not persists data"); 
            Assert.AreEqual(testValue,ti.BaseType, "BaseType does not persists data"); 
            Assert.AreEqual(testValue, ti.Name, "Name does not persists data"); 
        }

        [Description("Test if the TypeInfo class persists the booleans."), Owner("Michiel van Oudheusden"), TestMethod]
        public void TypeInfoPersistBoolData()
        {
            TypeInfo ti = new TypeInfo();
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

        [Description("Checks if the methods added to the TypeInfo object are stored."), Owner("Michiel van Oudheusden"), TestMethod]
        public void TypeInfoPersistMethodData()
        {
            TypeInfo ti = new TypeInfo();
            MethodInfo mi = new MethodInfo();
            mi.Name = "test";

            Assert.IsNotNull(ti.Methods, "Method list is null");

            ti.Methods.Add(mi);

            Assert.IsTrue(ti.Methods.Count == 1, "Method list does not contain a single methodinfo.");
            Assert.AreEqual(mi, ti.Methods[0], "Method list does not contain the same method info as added to the list.");
            
        }

        [Description("Checks if the AssemblyInfo is still saved when assigned."), Owner("Michiel van Oudheusden"), TestMethod]
        public void TypeInfoPersistAssemblyInfoData()
        {
            TypeInfo ti = new TypeInfo();
            
            AssemblyInfo ai = new AssemblyInfo();
            string v = "test";
            ai.Name = v;

            Assert.IsNotNull(ti.Assembly, "Assembly info is null");

            ti.Assembly = ai;

            Assert.AreEqual(ai, ti.Assembly, "TypeInfo does not persists AssemblyInfo");
            Assert.AreEqual(v, ti.Assembly.Name, "AssemblyInfo does not persists the data"); 
        }
    }
}
