using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Weaving.Strategies;
using Mono.Cecil;
using Mono.Cecil.Cil;
using System.Reflection;

/*
 * Disclaimer: this is a very dirty implementation of the desired behavior. This filter action 
 * performas a hard return, the return flow filters are therefor not executed. It is sufficient
 * for the purpose of this example.
 */
namespace CachingFilters
{
    [WeaveStrategyAttribute("CacheAction")]
    public class CacheActionWeaveStrategy : FilterActionWeaveStrategy
    {
        public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();
            WeaveStrategyUtilities.SetJoinPointContext(visitor, (MethodReference)originalCall, filterAction);

            MethodBase mi = typeof(CacheAction).GetMethod("getCachedValue", new Type[] { typeof(JoinPointContext) });
            MethodReference methodToCall = CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, mi);

            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call, methodToCall));

            Instruction noCache = visitor.Worker.Create(OpCodes.Nop);
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Brfalse, noCache));

            // Load JoinPointContext
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // Get returnvalue
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetReturnValue)));

            // Check if returnvalue is value type, then unbox, else cast
            if (originalCall.ReturnType.ReturnType.IsValueType || originalCall.ReturnType.ReturnType is GenericParameter)
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, originalCall.ReturnType.ReturnType));
            }
            else
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, originalCall.ReturnType.ReturnType));
            }
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ret));
            visitor.Instructions.Add(noCache);
        }
    }
}
