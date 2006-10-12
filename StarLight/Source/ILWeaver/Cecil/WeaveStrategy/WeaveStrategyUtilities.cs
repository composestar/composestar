using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices.Exceptions;

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
            MethodInfo methodInfo;
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();
            
            // Store current target
            if(filterAction.Target.Equals(FilterAction.INNER_TARGET) ||
                filterAction.Target.Equals(FilterAction.SELF_TARGET))
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                methodInfo = typeof(JoinPointContext).GetMethod("get_StartTarget", new Type[0]);
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
                methodInfo = typeof(JoinPointContext).GetMethod("set_CurrentTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }
            else if(visitor.Method.HasThis)
            {
                TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);
                FieldDefinition field = parentType.Fields.GetField(filterAction.Target);
                if(field == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.Target));
                }
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, field));
                methodInfo = typeof(JoinPointContext).GetMethod("set_CurrentTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }
            else
            {
                // set to null for static methods
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
                methodInfo = typeof(JoinPointContext).GetMethod("set_CurrentTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }

            // store current selector
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.Selector));
            methodInfo = typeof(JoinPointContext).GetMethod("set_CurrentSelector", new Type[] { typeof(string) });
            visitor.Instructions.Add(visitor.Worker.Create(
                OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));


            // Store substitution target
            if(filterAction.SubstitutionTarget.Equals(FilterAction.INNER_TARGET) ||
                filterAction.SubstitutionTarget.Equals(FilterAction.SELF_TARGET))
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                methodInfo = typeof(JoinPointContext).GetMethod("get_StartTarget", new Type[0]);
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
                methodInfo = typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }
            else if(visitor.Method.HasThis)
            {
                TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);
                FieldDefinition field = parentType.Fields.GetField(filterAction.SubstitutionTarget);
                if(field == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
                }
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, field));
                methodInfo = typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }
            else
            {
                // set to null for static methods
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
                methodInfo = typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) });
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
            }

            // store substitution selector
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.SubstitutionSelector));
            methodInfo = typeof(JoinPointContext).GetMethod("set_SubstitutionSelector", new Type[] { typeof(string) });
            visitor.Instructions.Add(visitor.Worker.Create(
                OpCodes.Callvirt, visitor.CreateMethodReference(methodInfo)));
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
