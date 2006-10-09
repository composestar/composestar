using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel.Design;
using System.IO;
using System.Text;

using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions; 
using Composestar.StarLight.ILAnalyzer;
using Composestar.Repository.LanguageModel;
  
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

namespace TestILAnalyzer
{
    /// <summary>
    /// 
    /// </summary>
    [TestClass]
    public class CecilILAnalyzerInitializationFixture
    {
        private ServiceContainer svcContainer = new ServiceContainer();

        [TestInitialize]
        public void TestInitialize()
        {
            svcContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILAnalyzerTestBuilderConfigurator());
            svcContainer.AddService(typeof(CecilAnalyzerConfiguration), CecilAnalyzerConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe")));
            svcContainer.AddService(typeof(ILanguageModelAccessor), new LanguageModelAccessorMock());
        }


        [TestMethod]
        [DeploymentItem("TestTarget.exe")]
        public void CanCreateAnalyzerThroughDIHelper()
        {
            CecilILAnalyzer analyzer = DIHelper.CreateObject<CecilILAnalyzer>(svcContainer);
            Assert.IsNotNull(analyzer);
        }

        [TestMethod]
        [ExpectedException(typeof(StarLightException))]
        public void AnalyzerThrowsExceptionIfFileDoesntExists()
        {
            CecilAnalyzerConfiguration configuration = CecilAnalyzerConfiguration.CreateDefaultConfiguration(string.Empty);
            ILanguageModelAccessor langModelAccessor = new LanguageModelAccessorMock();

            AssemblyElement ae = ((IILAnalyzer) new CecilILAnalyzer(configuration, langModelAccessor)).ExtractAllTypes("c:\\this_file_doesnt_exist");
        }

       
        [TestMethod]
        [ExpectedException(typeof(StarLightException))]
        [DeploymentItem("InvalidImage.exe")]
        public void InitializeThrowsBadImageExceptionOnInvalidInputImage()
        {
            CecilAnalyzerConfiguration configuration = CecilAnalyzerConfiguration.CreateDefaultConfiguration(string.Empty);
            ILanguageModelAccessor langModelAccessor = new LanguageModelAccessorMock();

            AssemblyElement ae = ((IILAnalyzer) new CecilILAnalyzer(configuration, langModelAccessor)).ExtractAllTypes(CreateFullPath("InvalidImage.exe"));
        }

        /// <summary>
        /// Creates the full path.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        private string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }
    }
}
