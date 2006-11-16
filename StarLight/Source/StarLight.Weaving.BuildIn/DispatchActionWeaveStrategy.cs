using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;

namespace Composestar.StarLight.Weaving.Strategies
{
    /// <summary>
    /// Dispatches to the substitution-message and returns afterwards. 
    /// </summary>
    [WeaveStrategyAttribute("DispatchAction")]
    [CLSCompliant(false)]
    public class DispatchActionWeaveStrategy : FilterActionWeaveStrategy
    {
       
        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            // Get JoinPointContext
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();

            FieldDefinition target = null;
         
            // Get the methodReference
            MethodReference methodReference = null;

            TypeDefinition parentTypeDefinition = (TypeDefinition)(visitor.Method.DeclaringType);
            TypeReference parentTypeReference = visitor.Method.DeclaringType;

            if (parentTypeReference.GenericParameters.Count > 0)
            {
                GenericInstanceType git = new GenericInstanceType(visitor.Method.DeclaringType);

                foreach (GenericParameter gp in originalCall.DeclaringType.GenericParameters)
                {
                    git.GenericArguments.Add(gp);
                }
               
                parentTypeReference = git; 
            }

            // Get the called method
            if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
                filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
            {
                if(filterAction.SubstitutionSelector.Equals(originalCall.Name))
                {
                    if (parentTypeReference is GenericInstanceType)
                    {
                        methodReference = new MethodReference(originalCall.Name,
                            parentTypeReference, originalCall.ReturnType.ReturnType,
                            originalCall.HasThis, originalCall.ExplicitThis,
                            originalCall.CallingConvention);

                        foreach (ParameterDefinition param in originalCall.Parameters)
                        {
                            methodReference.Parameters.Add(param);
                        }

                        // TODO MethodReferences with GenericClasses
                        /* The problem:
                         *
                         * class<T> Test 
                         * {
                         *    void Func(T a)
                         *    {
                         *       // inner call
                         *       Test<T>.Func(a);
                         *    } 
                         * }
                         * 
                         * Resolving Test<T> to a GenericInstanceType is no problem
                         * The methodReference must use the GenericInstanceType as its base type
                         * Creating a new MethodReference makes this possible, but Cecil does not output the information
                         * 
                         */
                    }
                    else
                        methodReference = originalCall;                     
                }
                else
                {
                    methodReference = CecilUtilities.ResolveMethod(parentTypeDefinition,
                        filterAction.SubstitutionSelector, originalCall);
                    
                }
            }
            else
            {
                target = parentTypeDefinition.Fields.GetField(filterAction.SubstitutionTarget);
                if(target == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
                }

                TypeDefinition fieldType = CecilUtilities.ResolveTypeDefinition(target.FieldType);
                MethodDefinition md = CecilUtilities.ResolveMethod(fieldType,
                    filterAction.SubstitutionSelector, originalCall);

                methodReference = visitor.TargetAssemblyDefinition.MainModule.Import(md);
            }

            if(methodReference == null)
            {
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                                            Properties.Resources.MethodNotFound, parentTypeReference.ToString(), filterAction.SubstitutionSelector));
            }

            // Generic arguments; add the generic parameters as generic argument to a GenericInstanceMethod
            if (originalCall.GenericParameters.Count > 0)   
            {
                // Original call has generics, so add to the new memberreference
                GenericInstanceMethod gim = new GenericInstanceMethod(methodReference);                
                foreach (GenericParameter gp in originalCall.GenericParameters)
                {
                    gim.GenericArguments.Add(gp);    
                }
                methodReference = gim;
            }

            // Place the arguments on the stack first

            // Place target on the stack
            if(methodReference.HasThis)
            {
                if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
                    filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
                {
                    WeaveStrategyUtilities.LoadSelfObject(visitor, jpcVar);
                }
                else
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, target));
                }
            }

            // Place arguments on the stack
            WeaveStrategyUtilities.LoadArguments(visitor, originalCall, jpcVar);

            // Call the method         
            if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) &&
                filterAction.SubstitutionSelector.Equals(originalCall.Name))
            {
                // Because it is an inner call targeting the method itself, we must call the method
                // in the class itself. Therefore we do a Call instead of a Callvirt, to prevent that
                // the call is dispatched to an overriding method in a subclass.
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call, methodReference));
            }
            else if(visitor.CalledMethod.HasThis)
            {
                // Because we dispatch to another method than the original called method, we do a Callvirt
                // so that an overriding method in a subclass may be called.
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodReference));
            }
            else
            {
                // static method cannot be called with Callvirt.
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call, methodReference));
            }

            // Store the return value:
            WeaveStrategyUtilities.StoreReturnValue(visitor, originalCall, jpcVar);

            // Restore arguments:
            WeaveStrategyUtilities.RestoreArguments(visitor, originalCall, jpcVar);
        }        

    }

}
