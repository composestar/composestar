using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.Repository.LanguageModel.Inlining;

using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
    {
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public override String FilterActionName
        {
            get
            {
                return "AdviceAction";
            }
        }
        
        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            MethodReference methodToCall;

            // Get JoinPointContext
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();

            // Get the methodReference
            MethodReference methodReference = (MethodReference) originalCall;
            TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);

            // Get method to call
            methodToCall = GetMethodToCall(visitor, filterAction, parentType);
            if(methodToCall == null)
            {
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.AdviceMethodNotFound, filterAction.SubstitutionSelector, filterAction.SubstitutionTarget));
            }


            // Set JoinPointContext
            WeaveStrategyUtilities.SetJoinPointContext(visitor, methodReference, filterAction);

            // Do the advice-call
            CallAdvice(visitor, filterAction, parentType, methodToCall, jpcVar);

            // Add nop to enable debugging
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Nop));
        }

        /// <summary>
        /// Weaves the call to the advice
        /// </summary>
        /// <param name="filterAction">The filteraction</param>
        /// <param name="parentType">The type containing the original method</param>
        /// <param name="methodToCall">The advice method</param>
        /// <param name="jpcVar">The local variable containing the JoinPointContext</param>
        private void CallAdvice(CecilInliningInstructionVisitor visitor,
            FilterAction filterAction, TypeDefinition parentType, MethodReference methodToCall,
            VariableDefinition jpcVar)
        {
            // Place target on the stack:
            if(methodToCall.HasThis)
            {
                if(filterAction.SubstitutionTarget.Equals(FilterAction.INNER_TARGET) ||
                    filterAction.SubstitutionTarget.Equals(FilterAction.SELF_TARGET))
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                }
                else
                {
                    FieldDefinition target = parentType.Fields.GetField(filterAction.SubstitutionTarget);
                    if(target == null)
                    {
                        throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                            Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
                    }

                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, target));
                }
            }

            // Load the JoinPointObject as the parameter
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // We can safely emit a callvirt here. The JITter will make the right call.
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodToCall));

        }


        private Type[] m_JpcTypes = new Type[1] { typeof(JoinPointContext) };

        /// <summary>
        /// Returns the MethodReference to the advice method
        /// </summary>
        /// <param name="filterAction">The filteraction</param>
        /// <param name="parentType">The type containing the original method</param>
        /// <returns>The MethodReference to the advice method</returns>
        private MethodReference GetMethodToCall(CecilInliningInstructionVisitor visitor,
            FilterAction filterAction, TypeDefinition parentType)
        {
            if(filterAction.SubstitutionTarget.Equals(FilterAction.INNER_TARGET) ||
                filterAction.SubstitutionTarget.Equals(FilterAction.SELF_TARGET))
            {
                return CecilUtilities.ResolveMethod(parentType, filterAction.SubstitutionSelector, m_JpcTypes);
            }
            else
            {
                FieldDefinition target = parentType.Fields.GetField(filterAction.SubstitutionTarget);
                if(target == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
                }


                MethodDefinition md = CecilUtilities.ResolveMethod(target.FieldType, 
                    filterAction.SubstitutionSelector, m_JpcTypes);

                return visitor.TargetAssemblyDefinition.MainModule.Import(md);
            }
        }
    }
}
