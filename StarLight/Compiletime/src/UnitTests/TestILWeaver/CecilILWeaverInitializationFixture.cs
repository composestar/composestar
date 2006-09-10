using System;
using System.Text;
using System.Collections.Generic;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Composestar.StarLight.ILWeaver;
using System.Collections.Specialized;
using System.IO;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif

namespace TestILWeaver
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class CecilILWeaverInitializationFixture
    {
        NameValueCollection weaverConfiguration;
        NameValueCollection _invalidWeaverConfiguration;
        [TestInitialize]
        public void TestInitialize()
        {
            weaverConfiguration = new NameValueCollection();


            //Cofniguration with 'ShouldSignAssembly' = 'true', without a SNKPath is invalid;
            _invalidWeaverConfiguration = new NameValueCollection();
            _invalidWeaverConfiguration.Add("ShouldSignAssembly", "true");
        }


        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void InitializeThrowsArgumentExceptionIfFileIsNull()
        {
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(null, weaverConfiguration);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void InitializeThrowsArgumentExceptionIfFileDoesntExists()
        {
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(@"c:\this_file_doesnt_exist", weaverConfiguration);
        }

        [TestMethod]
        [ExpectedException(typeof(ApplicationException))]
        public void DoWeaveThrowsExceptionIfInitializeWasNotCalled()
        {
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.DoWeave();
        }

        [TestMethod]
        [ExpectedException(typeof(BadImageFormatException))]
        [DeploymentItem("InvalidImage.exe")]
        public void InitializeThrowsBadImageExceptionOnInvalidInputImage()
        {
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(CreateFullPath("InvalidImage.exe"), weaverConfiguration);
        }


        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        [DeploymentItem("TestTarget.exe")]
        public void InitializeThrowsArgumentExceptionForInvalidConfiguration()
        {
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(CreateFullPath("TestTarget.exe"), _invalidWeaverConfiguration);
        }



        private string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }
    }
}
