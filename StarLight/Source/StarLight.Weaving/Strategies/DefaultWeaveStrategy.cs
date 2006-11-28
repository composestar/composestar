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
using Composestar.StarLight.Entities.Configuration;
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
using System.Globalization;
using System.Text;
#endregion

namespace Composestar.StarLight.Weaving.Strategies
{
	/// <summary>
	/// The default weave strategy, which will emit a call to an execute method defined by the filter action.
	/// The developer can create his/her own implementation of the filter action in this function. 
	/// If more flexibility is needed, then create a custom weave strategy.
	/// </summary>
	/// <returns>Filter action weave strategy</returns>
	[WeaveStrategyAttribute("Default")]
	[CLSCompliant(false)]
	public class DefaultWeaveStrategy : FilterActionWeaveStrategy
	{

		/// <summary>
		/// Get filter action element
		/// </summary>
		/// <param name="elements">Elements</param>
		/// <param name="fullname">The fullname.</param>
		/// <returns>Filter action element</returns>
		private static FilterActionElement GetFilterActionElement(List<FilterActionElement> elements, string fullname)
		{
			foreach (FilterActionElement fae in elements)
			{
				if (fae.FullName.Equals(fullname))
					return fae;
			}

			return null;
		}


		/// <summary>
		/// Generate the code which has to be inserted at the place of the filter specified by the visitor.
		/// </summary>
		/// <remarks>The creating of the JoinPointContext is optional and indicated by the FilterAction. If disabled, we emit a <see langword="null"></see> as the parameter.</remarks> 
		/// <param name="visitor">The visitor.</param>
		/// <param name="filterAction">The filter action.</param>
		/// <param name="originalCall">The original call.</param>
		[CLSCompliant(false)]
		public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
			MethodDefinition originalCall)
		{
			// The method we have to call, an Execute(JoinPointContext) function.
			MethodReference methodToCall;

			// Create FilterAction object:
			FilterActionElement filterActionElement;
			filterActionElement = DefaultWeaveStrategy.GetFilterActionElement(visitor.WeaveConfiguration.FilterActions, filterAction.FullName);

			if (filterActionElement == null)
				throw new ILWeaverException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotResolveFilterAction, filterAction.FullName));

			// Get JoinPointContext
			VariableDefinition jpcVar = null;
			if (filterActionElement.CreateJPC)
				jpcVar = visitor.CreateJoinPointContextLocal();

			// Get the methodReference
			MethodReference methodReference = (MethodReference)originalCall;
			// TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(methodReference.DeclaringType);

			// Set JoinPointContext
			if (filterActionElement.CreateJPC)
				WeaveStrategyUtilities.SetJoinPointContext(visitor, methodReference, filterAction);

			TypeReference typeRef =
				CecilUtilities.ResolveType(filterAction.FullName, filterActionElement.Assembly, null);
			TypeDefinition typeDef =
				CecilUtilities.ResolveTypeDefinition(typeRef);
			MethodReference constructor = typeDef.Constructors.GetConstructor(false, new Type[0]);

			if (constructor == null)
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, filterAction.FullName));

			constructor = visitor.TargetAssemblyDefinition.MainModule.Import(constructor);
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, constructor));

			// Get method to call
			methodToCall = CecilUtilities.ResolveMethod(typeDef, "Execute", new Type[] { typeof(JoinPointContext) });

			// Check for null value
			if (methodToCall == null)
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.AdviceMethodNotFound, "Execute", filterAction.FullName));

			methodToCall = visitor.TargetAssemblyDefinition.MainModule.Import(methodToCall);

			// Load the JoinPointObject as the parameter if required
			if (filterActionElement.CreateJPC)
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));
			else
				visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));

			// Do the call
			// We can safely emit a callvirt here. The JITter will make the right call.
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodToCall));

		}
	}
}
