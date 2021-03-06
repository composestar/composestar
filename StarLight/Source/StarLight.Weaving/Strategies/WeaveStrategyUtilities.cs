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
using Mono.Cecil;
using Mono.Cecil.Cil;
using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.Reflection;
using System.Text;
#endregion

namespace Composestar.StarLight.Weaving.Strategies
{
	/// <summary>
	/// Utilities for specific weaving tasks like creating JoinPointContext objects and reading those objects.
	/// </summary>
	public sealed partial class WeaveStrategyUtilities
	{

		/// <summary>
		/// Disable creating of this class.
		/// </summary>
		private WeaveStrategyUtilities()
		{

		}

		/// <summary>
		/// Sets the current selector and target in the JoinPointContext. Note that these might be different
		/// from the original selector and target, and depend on the position in the filterset.
		/// </summary>
		/// <param name="visitor">The visitor</param>
		/// <param name="originalCall">The original call</param>
		/// <param name="filterAction">The filterAction</param>
		[SuppressMessage("Microsoft.Performance", "CA1801:AvoidUnusedParameters", Justification = "May be needed in the future.")]
		[CLSCompliant(false)]
		public static void SetJoinPointContext(
			ICecilInliningInstructionVisitor visitor,
			MethodReference originalCall, FilterAction filterAction)
		{

			VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();

			// Store current target
			if (filterAction.Target.Equals(FilterAction.InnerTarget) ||
				filterAction.Target.Equals(FilterAction.SelfTarget))
			{
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
					CachedMethodDefinition.JoinPointContextGetStartTarget)));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
					CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
			}
			else if (visitor.Method.HasThis)
			{
				TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);
				FieldDefinition field = parentType.Fields.GetField(filterAction.Target);
				if (field == null)
				{
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.FieldNotFound, filterAction.Target));
				}
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, field));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
						CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
			}
			else
			{
				// set to null for static methods
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
						CachedMethodDefinition.JoinPointContextSetCurrentTarget)));
			}

			// store current selector
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.Selector));
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
				CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
					CachedMethodDefinition.JoinPointContextSetCurrentSelector)));


			// Store substitution target
            //if (filterAction.SubstitutionTarget.Equals(FilterAction.InnerTarget) ||
            //    filterAction.SubstitutionTarget.Equals(FilterAction.SelfTarget))
            //{
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
            //        CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
            //            CachedMethodDefinition.JoinPointContextGetStartTarget)));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
            //        CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
            //            CachedMethodDefinition.JoinPointContextGetSubstitutionTarget)));
            //}
            //else if (visitor.Method.HasThis)
            //{
            //    TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(visitor.Method.DeclaringType);
            //    FieldDefinition field = parentType.Fields.GetField(filterAction.SubstitutionTarget);
            //    if (field == null)
            //    {
            //        throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
            //            Properties.Resources.FieldNotFound, filterAction.SubstitutionTarget));
            //    }
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldfld, field));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
            //        CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
            //            CachedMethodDefinition.JoinPointContextSetSubstitutionTarget)));
            //}
            //else
            //{
            //    // set to null for static methods
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
            //    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
            //        CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
            //            CachedMethodDefinition.JoinPointContextSetSubstitutionTarget)));
            //}

			// store substitution selector
            // TODO no longer relevant
			//visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
			//visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, filterAction.SubstitutionSelector));
			//visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
			//	CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
			//		CachedMethodDefinition.JoinPointContextSetSubstitutionSelector)));
		}

		/// <summary>
		/// Creates the code that loads the arguments from the JoinPointContext onto the stack.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		/// <param name="originalMethod">The original method.</param>
		/// <param name="joinPointContextVariable">The join point context variable.</param>
		[CLSCompliant(false)]
		public static void LoadArguments(ICecilInliningInstructionVisitor visitor,
			MethodReference originalMethod, VariableDefinition joinPointContextVariable)
		{
			foreach (ParameterDefinition param in originalMethod.Parameters)
			{
                int ordinal = param.Sequence-1;// -(originalMethod.HasThis ? 1 : 0);

                //check for reference:
				if (param.ParameterType.FullName.EndsWith("&"))
				{

					if (visitor.FilterType == FilterType.InputFilter)
					{

						// For out parameters that are value type, check whether a value was set
						if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) == Mono.Cecil.ParameterAttributes.Out &&
							param.ParameterType.IsValueType)
						{
							//
							// Store value from joinpointcontext into out
							//

							// Load param
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

							// Load jpc
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

							// Load the ordinal
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, ordinal));

							// Call the GetArgumentValue(int16) function                    
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
								CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
									CachedMethodDefinition.JoinPointContextGetArgumentValue)));

							// Duplicate value
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Dup));

							// Load null
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));

							// Check equals
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ceq));

							// If not null, branch
							Instruction falseNop = visitor.Worker.Create(OpCodes.Nop);
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Brfalse, falseNop));

							// True branch

							// Pop unnecessary argument value
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Pop));

							// Jump to end (let parameter on the stack
							Instruction endNop = visitor.Worker.Create(OpCodes.Nop);
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Br, endNop));

							// End True branch

							// False branch

							// Nop instruction
							visitor.Instructions.Add(falseNop);

							// Unbox value
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, param.ParameterType));

							// Store value
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Stobj, param.ParameterType));

							// Load param
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

							// End False branch

							// End nop instruction
							visitor.Instructions.Add(endNop);
						}
						else
						{
							//
							// Store value from joinpointcontext into out
							//

							// Load param
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

							// Duplicate
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Dup));

							// Load jpc
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

							// Load the ordinal
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, ordinal));

							// Call the GetArgumentValue(int16) function                    
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
								CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
									CachedMethodDefinition.JoinPointContextGetArgumentValue)));

							// If valuetype unbox, else cast
							if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
							{
								// Unbox value
								visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, param.ParameterType));
							}
							else
							{
								// Cast
								visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, param.ParameterType));
							}

							// Store value
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Stobj, param.ParameterType));
						}
					}
				}
				else //not a reference parameter
				{
					// Load jpc
					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

					// Load the ordinal
					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, ordinal));

					// Call the GetArgumentValue(int16) function                    
					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
						CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
							CachedMethodDefinition.JoinPointContextGetArgumentValue)));

					// Check if parameter is value type, then unbox
					if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
					{
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, param.ParameterType));
					}
					else
					{
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, param.ParameterType));
					}
				}
			}
		}

		/// <summary>
		/// Restores the values of reference arguments in the JoinPointContext.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		/// <param name="originalMethod">The original method.</param>
		/// <param name="joinPointContextVariable">The join point context variable.</param>
		[CLSCompliant(false)]
		public static void RestoreArguments(ICecilInliningInstructionVisitor visitor,
			MethodReference originalMethod, VariableDefinition joinPointContextVariable)
		{
			foreach (ParameterDefinition param in originalMethod.Parameters)
			{
                int ordinal = param.Sequence - 1;// (originalMethod.HasThis ? 1 : 0);

				//check for reference:
				if (param.ParameterType.FullName.EndsWith("&"))
				{
					if (visitor.FilterType == FilterType.InputFilter)
					{

						//
						// Store value from out/ref into JPC
						//

						// Load jpc
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

						// Load ordinal
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, ordinal));

						// Load param
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

						// Load value
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldobj, param.ParameterType));

						// If valuetype, box
						if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
						{
							visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, param.ParameterType));
						}

						// Store value
						// Call the SetArgumentValue(int16, object) function                    
						visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
							CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
								CachedMethodDefinition.JoinPointContextSetArgumentValue)));
					}
				}
			}
		}

		/// <summary>
		/// Stores the returnvalue from the stack into the JoinPointContext object.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		/// <param name="originalCall">The original call.</param>
		/// <param name="joinPointContextVariable">The join point context variable.</param>
		[CLSCompliant(false)]
		public static void StoreReturnValue(ICecilInliningInstructionVisitor visitor,
			MethodReference originalCall, VariableDefinition joinPointContextVariable)
		{
			// Store returnvalue
			if (!originalCall.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
			{
				// Check if returnvalue is value type, then box
				if (originalCall.ReturnType.ReturnType.IsValueType || originalCall.ReturnType.ReturnType is GenericParameter)
				{
					visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
				}

				// Load jpc
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

				// Call set_ReturnValue in JoinPointContext
				visitor.Instructions.Add(visitor.Worker.Create(
					OpCodes.Call,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
						CachedMethodDefinition.JoinPointContextSetReturnValue)));

			}
		}


		/// <summary>
		/// Loads the self object (the original target) onto the stack. For inputfilters this is obtained by
		/// loading the 'this' object. For outputfilters, this is obtained by loading the original target from the
		/// JoinPointContext object.
		/// </summary>
		/// <param name="visitor">The visitor</param>
		/// <param name="joinPointContextVariable">The join point context variable.</param>
		[CLSCompliant(false)]
		public static void LoadSelfObject(ICecilInliningInstructionVisitor visitor, VariableDefinition joinPointContextVariable)
		{
			if (visitor.FilterType == FilterType.InputFilter)
			{
				// Load this
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));

			}
			else
			{
				// Load JoinPointContext
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, joinPointContextVariable));

				// Call get_StartTarget in JoinPointContext
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call,
					CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition,
						CachedMethodDefinition.JoinPointContextGetStartTarget)));

				// Do a cast
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, visitor.CalledMethod.DeclaringType));
			}
		}


		/// <summary>
		/// Sets the inner call.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		/// <param name="method">The method.</param>
		[CLSCompliant(false)]
		public static void SetInnerCall(ICecilInliningInstructionVisitor visitor, MethodReference method)
		{
			MethodDefinition methodDef = CecilUtilities.ResolveMethod(
				visitor.Method.DeclaringType, method.Name, method);

			// Get the weaveMethod to which the innercall is directed
			WeaveMethod weaveMethod = visitor.WeaveType.GetMethod(methodDef.ToString());

			//if(weaveMethod == null)
			//{
			//    // if using MethodReference failed, try using the MethodDefinition (in some cases the ToString
			//    // leads to different signatures).
			//    MethodDefinition methodDef = CecilUtilities.ResolveMethodDefinition(method);
			//    if(methodDef != null)
			//    {
			//        weaveMethod = visitor.WeaveType.GetMethod(method.ToString());
			//    }

			if (weaveMethod == null)
			{
				// If weave method could not be found, this means that the method
				// does not have filters woven and so no inner call context needs
			    // to be set.

				return;
			}
			//}

			// Load the this parameter
			if (!visitor.Method.HasThis || visitor.Method.DeclaringType.IsValueType)
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));
			else
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));

			// Load the methodId
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, weaveMethod.Id));

			// Call the SetInnerCall
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, CachedMethodDefinition.SetInnerCall)));
		}
	}
}
