using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Concerns;
using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.WeaveSpec;
using Composestar.StarLight.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.WeaveSpec.Instructions;   
using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices.Exceptions;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// Utilities for specific weaving tasks like creating JoinPointContext objects and reading those objects.
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
          
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();
            
            // Store current target
            if(filterAction.Target.Equals(FilterAction.InnerTarget) ||
                filterAction.Target.Equals(FilterAction.SelfTarget))
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));               
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetStartTarget)));         
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
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
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
            }
            else
            {
                // set to null for static methods
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
            }

            // store current selector
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.Selector));           
            visitor.Instructions.Add(visitor.Worker.Create(
                OpCodes.Callvirt, 
                CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                    CachedMethodDefinition.JoinPointContextSetCurrentTarget)));


            // Store substitution target
            if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
                filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));                
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextGetStartTarget)));                
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextGetSubstitutionTarget)));
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
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextSetSubstitutionTarget)));                
            }
            else
            {
                // set to null for static methods
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));                
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextSetSubstitutionTarget)));                
            }

            // store substitution selector
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.SubstitutionSelector));            
            visitor.Instructions.Add(visitor.Worker.Create(
                OpCodes.Callvirt, 
                CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                    CachedMethodDefinition.JoinPointContextSetSubstitutionSelector)));                
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
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Callvirt, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextGetArgumentValue)));

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

        /// <summary>
        /// Stores the returnvalue from the stack into the JoinPointContext object.
        /// </summary>
        /// <param name="visitor"></param>
        /// <param name="originalCall"></param>
        /// <param name="jpcVar"></param>
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
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Call, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextSetReturnValue))); 
                    
            }
        }


        /// <summary>
        /// Loads the self object (the original target) onto the stack. For inputfilters this is obtained by
        /// loading the 'this' object. For outputfilters this is obtained by loading the original target from the
        /// JoinPointContext object.
        /// </summary>
        /// <param name="visitor">The visitor</param>
        /// <param name="jpcVar">The JoinPointContext VariableDefinition</param>
        internal static void LoadSelfObject(CecilInliningInstructionVisitor visitor, VariableDefinition jpcVar)
        {
            if(visitor.FilterType == CecilInliningInstructionVisitor.FilterTypes.InputFilter)
            {
                // Load this
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
            }
            else
            {
                // Load JoinPointContext
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Call get_StartTarget in JoinPointContext
                visitor.Instructions.Add(visitor.Worker.Create(
                    OpCodes.Call, 
                    CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
                        CachedMethodDefinition.JoinPointContextGetStartTarget))); 
                    
                // Do a cast
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, visitor.CalledMethod.DeclaringType));
            }
        }
    }
}
