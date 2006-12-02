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

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Weaving;
  
namespace Composestar.StarLight.Weaving.Strategies
{
    /// <summary>
    /// Calls the substitution-message with the JoinPointContext as an argument and 
    /// then continues in the filterset. 
    /// Can be used for example to implement certain advicebehaviour before or after a dispatch. 
    /// </summary>
    [WeaveStrategyAttribute("AdviceAction")]
    [WeaveStrategyAttribute("BeforeAction")]
    [WeaveStrategyAttribute("AfterAction")]
    [CLSCompliant(false)]
    public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
    {

        private Type[] m_JpcTypes = new Type[1] { typeof(JoinPointContext) };
               
        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
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

			// Check if it is an innercall and set innercall context:
			if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget))
			{
				WeaveStrategyUtilities.SetInnerCall(visitor, methodToCall);
			}

            // Do the advice-call
            AdviceActionWeaveStrategy.CallAdvice(visitor, filterAction, parentType, methodToCall, jpcVar);

            // Add nop to enable debugging
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Nop));
        }

        /// <summary>
        /// Weaves the call to the advice.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filteraction.</param>
        /// <param name="parentType">The type containing the original method.</param>
        /// <param name="methodToCall">The advice method.</param>
        /// <param name="jpcVar">The local variable containing the JoinPointContext.</param>
        private static void CallAdvice(ICecilInliningInstructionVisitor visitor,
            FilterAction filterAction, TypeDefinition parentType, MethodReference methodToCall,
            VariableDefinition jpcVar)
        {
            // Place target on the stack:
            if(methodToCall.HasThis)
            {
                if(filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
                    filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
                {
                    WeaveStrategyUtilities.LoadSelfObject(visitor, jpcVar);
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

        /// <summary>
        /// Returns the MethodReference to the advice method.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filteraction.</param>
        /// <param name="parentType">The type containing the original method.</param>
        /// <returns>
        /// The MethodReference to the advice method.
        /// </returns>
        private MethodReference GetMethodToCall(ICecilInliningInstructionVisitor visitor,
            FilterAction filterAction, TypeDefinition parentType)
        {
            if (filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
                filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
            {
                return CecilUtilities.ResolveMethod(parentType, filterAction.SubstitutionSelector, m_JpcTypes);
            }
            else
            {
                FieldDefinition target = parentType.Fields.GetField(filterAction.SubstitutionTarget);
                if (target == null)
                {
                    // FIXME: shouldn't this be TargetNotFound instead of FieldNotFound?
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
                }

                MethodDefinition method = CecilUtilities.ResolveMethod(target.FieldType, 
                    filterAction.SubstitutionSelector, m_JpcTypes);

                if (method == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, 
                        Properties.Resources.MethodNotFound, target.FieldType, filterAction.SubstitutionSelector));
                }

                return visitor.TargetAssemblyDefinition.MainModule.Import(method);
            }
        }
    }
}
