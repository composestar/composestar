using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities.Interfaces;

namespace Composestar.StarLight.Weaving.Strategies
{

    /// <summary>
    /// Base class of the filter action weave strategies.
    /// </summary>
    public abstract class FilterActionWeaveStrategy
    {
    
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public abstract String FilterActionName
        {
            get;
        }


        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public abstract void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall);

       
    }
}
