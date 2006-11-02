#region Using directives
using System;
using System.Text;
using System.Collections.Generic;
using System.Reflection; 
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel; 
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using System.Collections.Specialized;
using System.IO;

using TestILAnalyzer.DIConfiguration;
using TestILAnalyzer.Mocks;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif
#endregion

namespace Composestar.StarLight.ILAnalyzer.Tests
{

    /// <summary>
    /// Summary description for AnalyzerFixture
    /// </summary>
    [TestClass]
    public class CecilILAnalyzerFixture : ILAnalyzerFixtureBase
    {
        public CecilILAnalyzerFixture()
        {

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

        /// <summary>
        /// Test target must return assembly
        /// </summary>
        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void TestTargetMustReturnCorrectAssembly()
        {
            // set up model 
            LanguageModelAccessorMock model = new LanguageModelAccessorMock();

            // create configuration
            CecilAnalyzerConfiguration configuration = CecilAnalyzerConfiguration.CreateDefaultConfiguration(string.Empty);

            // do weaving
            IILAnalyzer analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateTestContainer(model, configuration));
            AssemblyElement ae = analyzer.ExtractAllTypes(CreateFullPath("TestTarget.exe"));

            Assert.IsNotNull(ae, "Could not create an AssemblyElement.");
            Assert.IsFalse(string.IsNullOrEmpty(ae.FileName), "Filename is not set.");
            Assert.IsFalse(string.IsNullOrEmpty(ae.Name), "Assembly name is not set.");
            Assert.IsFalse(ae.Timestamp == 0, "Timestamp is not set.");

            Assert.IsTrue(ae.TypeElements.Length > 0, "TypeElements were not retrieved.");
            Assert.IsTrue(ae.TypeElements.Length == 1, "Found {0} TypeElements, expecting 1.",  ae.TypeElements.Length);

            TypeElement te = ae.TypeElements[0];
 
            Assert.IsNotNull(te, "Could not set the TypeElement.");

            Assert.IsFalse(string.IsNullOrEmpty(te.BaseType), "BaseType has not been stored.");
            Assert.IsFalse(string.IsNullOrEmpty(te.FullName), "FullName has not been stored.");
            Assert.IsFalse(string.IsNullOrEmpty(te.Name), "Name has not been stored.");
            Assert.IsFalse(string.IsNullOrEmpty(te.Namespace), "Namespace has not been stored.");
             
            Assert.IsTrue(te.MethodElements.Length == 2, "Methods not stored in the TypeElement. {0} methods found", te.MethodElements.Length);
 
            MethodElement me = te.MethodElements[0];
 
            Assert.IsNotNull(me, "Could not set the methodElement");

            Assert.IsFalse(string.IsNullOrEmpty(me.Name), "Name has not been stored.");
            Assert.IsFalse(string.IsNullOrEmpty(me.ReturnType), "Returntype has not been stored.");
            Assert.IsFalse(string.IsNullOrEmpty(me.Signature), "Signature has not been stored.");

        } // TestTargetMustReturnAssembly()
    }
}