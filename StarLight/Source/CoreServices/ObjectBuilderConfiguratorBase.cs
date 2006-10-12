using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.ObjectBuilder;

namespace Composestar.StarLight.CoreServices
{
    /// <summary>
    /// Base type for the object builder configurator.
    /// </summary>
    public abstract class ObjectBuilderConfiguratorBase : IBuilderConfigurator<BuilderStage>
    {
        /// <summary>
        /// Applies the configuration to the builder.
        /// </summary>
        /// <param name="builder">The builder to apply the configuration to.</param>
        public abstract void ApplyConfiguration(IBuilder<BuilderStage> builder);

    }
}
