using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.ObjectBuilder;

namespace Composestar.StarLight.CoreServices
{
    public abstract class ObjectBuilderConfiguratorBase : IBuilderConfigurator<BuilderStage>
    {
        public abstract void ApplyConfiguration(IBuilder<BuilderStage> builder);

    }
}
