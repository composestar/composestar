using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.CoreServices;
using Microsoft.Practices.ObjectBuilder;
using Composestar.Repository;
using TestILAnalyzer.Mocks;

namespace TestILAnalyzer.DIConfiguration
{
    class ILAnalyzerTestBuilderConfigurator : ObjectBuilderConfiguratorBase
    {
        public override void ApplyConfiguration(IBuilder<BuilderStage> builder)
        {
            builder.Policies.Set<ITypeMappingPolicy>(new TypeMappingPolicy(typeof(LanguageModelAccessorMock), null), typeof(ILanguageModelAccessor), null);

            builder.Policies.SetDefault<ISingletonPolicy>(new SingletonPolicy(true));
        }
    }
}
