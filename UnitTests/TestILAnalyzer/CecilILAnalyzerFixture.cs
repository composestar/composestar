using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILAnalyzer;
using Composestar.Repository.LanguageModel;  
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
        [ExpectedException(typeof(ArgumentException))]
        [DeploymentItem("TestTarget.exe")]
        public void InitializeThrowsArgumentExceptionIfRepositoryFilenameIsNotSupplied()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("TestTarget.exe"), null);
        }

        [TestMethod]
        [ExpectedException(typeof(ILAnalyzerException))]
        public void ExtractTypesThrowsExceptionIfInitializeWasNotCalled()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            IList<TypeElement> ret = analyzer.ExtractTypeElements();
        }

        [TestMethod]
        [ExpectedException(typeof(BadImageFormatException))]
        [DeploymentItem("InvalidImage.exe")]
        public void InitializeThrowsBadImageExceptionOnInvalidInputImage()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("InvalidImage.exe"), GetDefaultConfig());
        }

        [TestMethod]      
        [DeploymentItem("TestTarget.exe")]
        public void ExtractTypesMustReturnTypeElements()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("TestTarget.exe"), GetDefaultConfig());
            IList<TypeElement> ret = analyzer.ExtractTypeElements();

            Assert.IsNotNull(ret, "ExtractTypeElements returned null instead of a generic list of TypeElement.") ;
        }

        [TestMethod]      
        [DeploymentItem("TestTarget.exe")]
        public void ExtractTypesReturnsValues()
        {
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(CreateFullPath("TestTarget.exe"), GetDefaultConfig());
            IList<TypeElement> ret = analyzer.ExtractTypeElements();

            Assert.IsTrue(ret.Count == 2, "There are more than one type in the testtarget.exe. Expected <module> and program type."); 
        }

        private NameValueCollection GetDefaultConfig()
        {
            NameValueCollection config = new NameValueCollection();
            config.Add("RepositoryFilename", CreateFullPath("starlight.yap")); 

            return config;
        }

        private string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }
    }
}
