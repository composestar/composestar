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

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class ErrorActionWeaveStrategy : FilterActionWeaveStrategy
    {
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public override String FilterActionName
        {
            get
            {
                return "ErrorAction";
            }
        }

        /// <summary>
        /// Weaves the error action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <remarks>
        /// Generate exception throw
        /// </remarks> 
        /// <example>
        /// The following construction should be created:
        /// <code>
        /// throw new Exception();
        /// </code>
        /// or in IL code:
        /// <code>
        ///   newobj instance void [mscorlib]System.Exception::.ctor()
        ///   throw 
        /// </code>
        /// </example> 
        public override void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            // Create an exception
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, 
                CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, CachedMethodDefinition.ExceptionConstructor)));

            // Throw the exception
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Throw));
        }
    }
}