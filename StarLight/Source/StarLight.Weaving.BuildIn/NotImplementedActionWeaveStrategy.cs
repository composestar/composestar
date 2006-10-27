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
using Composestar.StarLight.Utilities;

namespace Composestar.StarLight.Weaving.Strategies
{
    /// <summary>
    /// Strategy to create the NotImplementedAction. This action injects NotImplementedExceptions into the code.
    /// </summary>
    [WeaveStrategyAttribute("NotImplementedAction")]
    public class NotImplementedActionWeaveStrategy : FilterActionWeaveStrategy
    {

        /// <summary>
        /// Weaves the NotImplementedException action.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        /// <remarks>
        /// Generate exception throw
        /// </remarks>
        /// <example>
        /// The following construction should be created:
        /// <code>
        /// throw new NotYetImplementedException();
        /// </code>
        /// or in IL code:
        /// <code>
        /// newobj instance void [mscorlib]System.Exception::.ctor()
        /// throw
        /// </code>
        /// </example>
        public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            // Create an exception
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, 
                CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                typeof(NotImplementedException).GetConstructors()[0])));

            // Throw the exception
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Throw));
        }
    }
}
