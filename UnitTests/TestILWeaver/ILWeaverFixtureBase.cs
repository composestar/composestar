using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.IO;
using System.Text;

using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILWeaver;
using TestILWeaver.DIConfiguration;
using TestILWeaver.Mocks;

namespace TestILWeaver
{
    public abstract class ILWeaverFixtureBase
    {
        protected const string TestInputImage = "..\\TestTarget\\bin\\debug\\TestTarget.exe";

        internal IServiceProvider CreateTestContainer(LanguageModelAccessorMock languageModel, CecilWeaverConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(ILanguageModelAccessor), languageModel == null ? new LanguageModelAccessorMock() : languageModel);
            serviceContainer.AddService(typeof(CecilWeaverConfiguration), configuration == null ? CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe")) : configuration);
            serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new IlWeaverTestBuilderConfigurator());

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
