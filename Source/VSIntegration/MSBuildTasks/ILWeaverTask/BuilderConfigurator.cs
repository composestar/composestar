using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.Practices.ObjectBuilder;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;

namespace Composestar.StarLight.MSBuild.Tasks
{
    class ILWeaverBuilderConfigurator : ObjectBuilderConfiguratorBase
    {
        public override void ApplyConfiguration(IBuilder<BuilderStage> builder)
        {
            builder.Policies.Set<ITypeMappingPolicy>(new TypeMappingPolicy(typeof(EntitiesAccessor), null), typeof(IEntitiesAccessor), null);
        }
    }


}
