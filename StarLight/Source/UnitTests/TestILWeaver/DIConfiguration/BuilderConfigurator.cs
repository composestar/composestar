using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.CoreServices;
using Microsoft.Practices.ObjectBuilder;
using Composestar.Repository;
using TestILWeaver.Mocks;

namespace TestILWeaver.DIConfiguration
{
    class ILWeaverTestBuilderConfigurator : ObjectBuilderConfiguratorBase
    {
        public override void ApplyConfiguration(IBuilder<BuilderStage> builder)
        {
            builder.Policies.Set<ITypeMappingPolicy>(new TypeMappingPolicy(typeof(LanguageModelAccessorMock), null), typeof(ILanguageModelAccessor), null);

            builder.Policies.SetDefault<ISingletonPolicy>(new SingletonPolicy(true));
        }
    }
}
