#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Weaving;
using Mono.Cecil;
using Mono.Cecil.Cil;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
#endregion

namespace Composestar.StarLight.Weaving.Strategies
{
	/// <summary>
	/// Calls the substitution-message with the JoinPointContext as an argument and 
	/// then continues in the filterset. 
	/// Can be used for example to implement certain advicebehaviour before or after a dispatch. 
	/// </summary>
	/// <returns>Filter action weave strategy</returns>
	[WeaveStrategyAttribute("AdviceAction")]
	[WeaveStrategyAttribute("BeforeAction")]
	[WeaveStrategyAttribute("AfterAction")]
	[CLSCompliant(false)]
	public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
	{

		/// <summary>
		/// M jpc types
		/// </summary>
		private Type[] m_JpcTypes = new Type[1] { typeof(JoinPointContext) };
        private Type[] m_ObjectTypes = new Type[1] { typeof(Object) };
        private Type[] m_NoneTypes = new Type[0] {};

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
			MethodReference methodReference = (MethodReference)originalCall;
			TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);

			// Get method to call
			methodToCall = GetMethodToCall(visitor, filterAction, parentType);

			if (methodToCall == null)
			{
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.AdviceMethodNotFound, getSelector(filterAction), getTarget(filterAction)));
			}

			// Set JoinPointContext
			WeaveStrategyUtilities.SetJoinPointContext(visitor, methodReference, filterAction);

			// Check if it is an innercall and set innercall context:
			if (getTarget(filterAction).Equals(FilterAction.InnerTarget))
			{
				WeaveStrategyUtilities.SetInnerCall(visitor, methodToCall);
			}

			// Do the advice-call
			AdviceActionWeaveStrategy.CallAdvice(visitor, filterAction, parentType, methodToCall, jpcVar);

			// Add nop to enable debugging
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Nop));
		}

        private static string getSelector(FilterAction filterAction)
        {
            FilterArgument farg = filterAction.getArgument("selector");
            if (farg != null)
            {
                return farg.Value;
            }
            return string.Empty;
        }

        private static string getTarget(FilterAction filterAction)
        {
            FilterArgument farg = filterAction.getArgument("target");
            if (farg != null)
            {
                return farg.Value;
            }
            return string.Empty;
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
			if (methodToCall.HasThis)
			{
                String fargTarget = getTarget(filterAction);
                if (fargTarget.Equals(FilterAction.InnerTarget) ||
                    fargTarget.Equals(FilterAction.SelfTarget))
				{
					WeaveStrategyUtilities.LoadSelfObject(visitor, jpcVar);
				}
				else
				{
                    FieldDefinition target = parentType.Fields.GetField(fargTarget);
					if (target == null)
					{
						throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                            Properties.Resources.FieldNotFound, fargTarget));
					}

					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, target));
				}
			}

			// Load the JoinPointObject as the parameter
            if (methodToCall.Parameters.Count == 1)
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            }
            else if (methodToCall.Parameters.Count != 0)
            {
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                    Properties.Resources.AdviceMethodNotFound, getSelector(filterAction), getTarget(filterAction)));
            }

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
            MethodReference result = null;
            string fargTarget = getTarget(filterAction);
            string fargSelector = getSelector(filterAction);
            if (fargTarget.Equals(FilterAction.InnerTarget) ||
                fargTarget.Equals(FilterAction.SelfTarget))
			{
                result = CecilUtilities.ResolveMethod(parentType, fargSelector, m_JpcTypes);
                if (result == null)
                {
                    result = CecilUtilities.ResolveMethod(parentType, fargSelector, m_ObjectTypes);
                }
                if (result == null)
                {
                    result = CecilUtilities.ResolveMethod(parentType, fargSelector, m_NoneTypes);
                }
			}
			else
			{
				FieldDefinition target = parentType.Fields.GetField(fargTarget);
				if (target == null)
				{
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.FieldNotFound, fargTarget));
				}

				MethodDefinition method = CecilUtilities.ResolveMethod(target.FieldType,
                     fargSelector, m_JpcTypes);

                if (method == null)
                {
                    // try func(Object)
                    method = CecilUtilities.ResolveMethod(target.FieldType, fargSelector, m_ObjectTypes);
                }
                if (method == null)
                {
                    // try func()
                    method = CecilUtilities.ResolveMethod(target.FieldType, fargSelector, m_NoneTypes);
                }

				if (method == null)
				{
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.MethodNotFound, target.FieldType, fargSelector));
				}

				return visitor.TargetAssemblyDefinition.MainModule.Import(method);
			}
            return result;
		}
	}
}
