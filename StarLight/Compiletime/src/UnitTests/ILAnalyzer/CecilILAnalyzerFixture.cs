using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILAnalyzer;
using System.Collections.Specialized;
using System.IO;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif

namespace TestILAnalyzer
{
    /// <summary>
    /// Summary description for AnalyzerFixture
    /// </summary>
    [TestClass]
    public class CecilILAnalyzerFixture
    {
        public CecilILAnalyzerFixture()
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

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void InitializeThrowsArgumentExceptionIfFileIsNull()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(null, null);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void InitializeThrowsArgumentExceptionIfFileDoesntExists()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(@"c:\this_file_doesnt_exist", null);
        }

        [TestMethod]
        [ExpectedException(typeof(ApplicationException))]
        public void ExtractTypesThrowsExceptionIfInitializeWasNotCalled()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            List<string> ret = analyzer.ExtractTypes();
        }

        [TestMethod]
        [ExpectedException(typeof(BadImageFormatException))]
        [DeploymentItem("InvalidImage.exe")]
        public void InitializeThrowsBadImageExceptionOnInvalidInputImage()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("InvalidImage.exe"), null);
        }

        [TestMethod]      
        [DeploymentItem("TestTarget.exe")]
        public void ExtractTypesMustReturnMethodElements()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("TestTarget.exe"), null);
            List<MethodElement> ret = analyzer.ExtractMethods();

            Assert.IsNotNull(ret, "Extract methods returned null instead of a generic list of MethodElements.") ;
        }

        [TestMethod]      
        [DeploymentItem("TestTarget.exe")]
        public void ExtractTypesReturnValues()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("TestTarget.exe"), null);
            List<MethodElement> ret = analyzer.ExtractMethods();

            Assert.IsTrue(ret.Count == 1, "There are more than one method in the testtarget.exe"); 
        }

        private string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }
    }
}
