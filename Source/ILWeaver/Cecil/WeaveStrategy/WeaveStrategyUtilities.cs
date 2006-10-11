using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class WeaveStrategyUtilities
    {
        

        /// <summary>
        /// Sets the current selector and target in the JoinPointContext. Note that these might be different
        /// from the original selector and target, and depend on the position in the filterset.
        /// </summary>
        /// <param name="visitor">The visitor</param>
        /// <param name="originalCall">The original call</param>
        /// <param name="filterAction">The filterAction</param>
        internal static void SetJoinPointContext(
            CecilInliningInstructionVisitor visitor,
            MethodReference originalCall, FilterAction filterAction)
        {
            // TODO
        }

        /// <summary>
        /// Creates the code that loads the arguments from the JoinPointContext onto the stack
        /// </summary>
        internal static void LoadArguments(CecilInliningInstructionVisitor visitor,
            MethodReference originalMethod, VariableDefinition jpcVar)
        {
            foreach(ParameterDefinition param in originalMethod.Parameters)
            {
                // Load jpc
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load the ordinal
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                // Call the GetArgumentValue(int16) function    
                MethodInfo t = typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) });
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, visitor.CreateMethodReference(t)));

                // Check if parameter is value type, then unbox
                if(param.ParameterType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, param.ParameterType));
                }
                else
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, param.ParameterType));
                }
            }
        }


        internal static void StoreReturnValue(CecilInliningInstructionVisitor visitor,
            MethodReference originalCall, VariableDefinition jpcVar)
        {
            // Store returnvalue
            if(!originalCall.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                // Check if returnvalue is value type, then box
                if(originalCall.ReturnType.ReturnType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
                }

                // Load jpc
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Call set_ReturnValue in JoinPointContext
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call, visitor.CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("SetReturnValue", 
                    new Type[] { typeof(object), typeof(JoinPointContext) }))));
            }
        }
    }
}
