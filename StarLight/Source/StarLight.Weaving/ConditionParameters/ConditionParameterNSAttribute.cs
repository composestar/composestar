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
using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics.CodeAnalysis;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Utilities;
#endregion

namespace Composestar.StarLight.Weaving.ConditionParameters
{
	/// <summary>
	/// Apply this attribute to conditions which requires the NameSpace and Type of the current method.
	/// </summary>
	[CLSCompliant(false)]
	public class ConditionParameterNSAttribute : ConditionParameterAttribute
	{

		private readonly string _booleanReturnType = typeof(Boolean).FullName;
		private readonly string _stringType = typeof(String).FullName;
	
		/// <summary>
		/// Returns a description of this Filter Module Condition.
		/// </summary>
		/// <value></value>
		/// <returns>String</returns>
		public override string Description
		{
			get { return Properties.Resources.FMCNSDescription; }
		}

		/// <summary>
		/// Perform the actual IL code generation needed for the Condition parameters.
		/// For instance, this function can create the information which can be used by the condition function.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		/// <param name="originalCall">The original call.</param>
		/// <param name="conditionMethod">The condition method.</param>
		[CLSCompliant(false)]
		public override void Generate(ICecilInliningInstructionVisitor visitor, MethodDefinition originalCall, MethodDefinition conditionMethod)
		{
			// Determine the ns
			string ns = originalCall.DeclaringType.Namespace;

			// Place the NS of the current method onto the stack
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, ns));

			// Determine the type
			string type = originalCall.DeclaringType.Name;

			// Place the type of the current method onto the stack
			visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, type));
 
		}

		/// <summary>
		/// Determines whether the specified condition method is valid for this type of code generation.
		/// If this function returns false, then the RequiredCondition text is displayed.
		/// </summary>
		/// <param name="conditionMethod">The condition method.</param>
		/// <returns>
		/// 	<c>true</c> if the specified condition method is valid; otherwise, <c>false</c>.
		/// </returns>
		public override bool IsValidCondition(MethodDefinition conditionMethod)
		{
			// Method must return a bool
			if (!conditionMethod.ReturnType.ReturnType.FullName.Equals(_booleanReturnType))
				return false;

			// There should be no parameters
			if (conditionMethod.Parameters.Count != 2)
				return false;

			if (!conditionMethod.Parameters[0].ParameterType.FullName.Equals(_stringType))
				return false;

			if (!conditionMethod.Parameters[1].ParameterType.FullName.Equals(_stringType))
				return false; 

			return true;
		}

		/// <summary>
		/// Gets the required condition text. The developer has to specify the message to show when the IsValidCondition is false.
		/// </summary>
		/// <value>The required condition message text.</value>
		public override string RequiredCondition
		{
			get { return Properties.Resources.FMCNSRequired; }
		}
	}
}
