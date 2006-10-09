using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.IO;
using System.Text;

using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;
using TestILAnalyzer.DIConfiguration;
using TestILAnalyzer.Mocks;

namespace TestILAnalyzer
{
    public abstract class ILAnalyzerFixtureBase
    {
        protected const string TestInputImage = "..\\TestTarget\\bin\\debug\\TestTarget.exe";

        /// <summary>
        /// Creates the test container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal IServiceProvider CreateTestContainer(LanguageModelAccessorMock languageModel, CecilAnalyzerConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(ILanguageModelAccessor), languageModel == null ? new LanguageModelAccessorMock() : languageModel);
            serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration == null ? CecilAnalyzerConfiguration.CreateDefaultConfiguration(CreateFullPath("test.yap")) : configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILAnalyzerTestBuilderConfigurator());

            return serviceContainer;
        }


        /// <summary>
        /// Creates the full path.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        internal string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }
    }
}
