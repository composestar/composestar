using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.Repository.LanguageModel.Inlining;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class DispatchActionWeaveStrategy : FilterActionWeaveStrategy
    {
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public override String FilterActionName
        {
            get
            {
                return "DispatchAction";
            }
        }


        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction, 
            MethodDefinition originalCall )
        {
            //
            // Call the same method
            //
            switch(visitor.FilterType)
            {
                case CecilInliningInstructionVisitor.FilterTypes.InputFilter:
                    FieldDefinition target = null;
                    // Self call

                    // Get the methodReference
                    MethodReference methodReference = null;

                    TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);

                    // Get the called method
                    if(filterAction.SubstitutionTarget.Equals(FilterAction.INNER_TARGET) ||
                        filterAction.SubstitutionTarget.Equals(FilterAction.SELF_TARGET))
                    {
                        if(filterAction.SubstitutionSelector.Equals(originalCall.Name))
                        {
                            methodReference = originalCall;
                        }
                        else
                        {
                            methodReference = CecilUtilities.ResolveMethod(parentType, 
                                filterAction.SubstitutionSelector, originalCall);
                        }
                    }
                    else
                    {
                        target = parentType.Fields.GetField(filterAction.SubstitutionTarget);
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
                        return;
                    }


                    // Place the arguments on the stack first

                    // Place target on the stack
                    if(methodReference.HasThis)
                    {
                        if(filterAction.SubstitutionTarget.Equals(FilterAction.INNER_TARGET) ||
                            filterAction.SubstitutionTarget.Equals(FilterAction.SELF_TARGET))
                        {
                            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                        }
                        else
                        {
                            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, target));
                        }
                    }

                    ///TODO this is only for inputfilters, outputfilters must be treated differently!
                    int numberOfArguments = originalCall.Parameters.Count;
                    for(int i = 0; i < numberOfArguments; i++)
                    {
                        visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.Parameters[i]));
                    }

                    // Call the method                    
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodReference));

                    //Store the return value:
                    if(!methodReference.ReturnType.ReturnType.FullName.Equals(
                        CecilUtilities.VoidType))
                    {
                        VariableDefinition returnValueVar = visitor.CreateReturnValueLocal(
                            originalCall.ReturnType.ReturnType);
                        visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Stloc, returnValueVar));
                    }

                    break;
                case CecilInliningInstructionVisitor.FilterTypes.OutputFilter:

                    break;
            }
        }
    }
}
