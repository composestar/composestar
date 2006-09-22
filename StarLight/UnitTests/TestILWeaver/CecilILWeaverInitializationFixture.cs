using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILWeaver;
using System.Collections.Specialized;
using System.IO;
using System.ComponentModel.Design;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Composestar.StarLight.CoreServices;
using TestILWeaver.Mocks;
using Microsoft.Practices.ObjectBuilder;
using TestILWeaver.DIConfiguration;
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
        ServiceContainer svcContainer = new ServiceContainer();

        [TestInitialize]
        public void TestInitialize()
        {
            svcContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new IlWeaverTestBuilderConfigurator());
            svcContainer.AddService(typeof(CecilWeaverConfiguration), CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe")));
            svcContainer.AddService(typeof(ILanguageModelAccessor), new LanguageModelAccessorMock());
        }


        [TestMethod]
        [DeploymentItem("TestTarget.exe")]
        public void CanCreateWeaverThroughDIHelper()
        {
            CecilILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(svcContainer);
            Assert.IsNotNull(weaver);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidOperationException))]
        public void WeaveThrowsInvalidOperationExceptionIfFileDoesntExists()
        {
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration("c:\\this_file_doesnt_exist");
            ILanguageModelAccessor langModelAccessor = new LanguageModelAccessorMock();

            new CecilILWeaver(configuration, langModelAccessor).DoWeave();
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        [DeploymentItem("TestTarget.exe")]
        public void CreatingConfigurationThrowsArgumentExceptionIfRepositoryFilenameIsNotSupplied()
        {
            new CecilWeaverConfiguration(string.Empty, false, string.Empty, string.Empty, false);
        }

       
        [TestMethod]
        [ExpectedException(typeof(BadImageFormatException))]
        [DeploymentItem("InvalidImage.exe")]
        public void InitializeThrowsBadImageExceptionOnInvalidInputImage()
        {
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("InvalidImage.exe"));
            ILanguageModelAccessor langModelAccessor = new LanguageModelAccessorMock();

            new CecilILWeaver(configuration, langModelAccessor).DoWeave();
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
