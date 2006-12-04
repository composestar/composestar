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
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Mono.Cecil;
using Mono.Cecil.Cil;
using System;
using System.Collections.Generic;
using System.Text;
#endregion

namespace Composestar.StarLight.Utilities.Interfaces
{
	#region FilterType Enumeration

	/// <summary>
	/// Possible filter types this visitor can generate code for.
	/// </summary>
	public enum FilterType
	{
		/// <summary>
		/// Default none.
		/// </summary>
		None = 0,
		/// <summary>
		/// Input filter.
		/// </summary>
		InputFilter = 1,
		/// <summary>
		/// Output filter.
		/// </summary>
		OutputFilter = 2,
	}

	#endregion

	/// <summary>
	/// Interface for the Cecil inlining instructions.
	/// </summary>
	[CLSCompliant(false)]
	public interface ICecilInliningInstructionVisitor
	{
		/// <summary>
		/// Creates the local variable.
		/// </summary>
		/// <param name="type">The type of the variable to create.</param>
		/// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
		[CLSCompliant(false)]
		VariableDefinition CreateLocalVariable(Type type);

		/// <summary>
		/// Creates the local variable.
		/// </summary>
		/// <param name="type">The typereference to create.</param>
		/// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
		[CLSCompliant(false)]
		VariableDefinition CreateLocalVariable(TypeReference type);

		/// <summary>
		/// Creates the join point context variable.
		/// </summary>
		/// <returns>Returns the <see cref="T:Composestar.StarLight.ContextInfo.JoinPointContext"></see> as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
		[CLSCompliant(false)]
		VariableDefinition CreateJoinPointContextLocal();

		/// <summary>
		/// Gets or sets the WeaveType object.
		/// </summary>
		/// <value>The type of the weave.</value>
		WeaveType WeaveType { get;set;}
		/// <summary>
		/// Gets or sets the type of the filter.
		/// </summary>
		/// <value>The type of the filter.</value>
		FilterType FilterType { get;set;}
		/// <summary>
		/// Weave configuration
		/// </summary>
		/// <returns>Configuration container</returns>
		ConfigurationContainer WeaveConfiguration { get;set;}
		/// <summary>
		/// Gets or sets the target assembly definition.
		/// </summary>
		/// <value>The target assembly definition.</value>
		[CLSCompliant(false)]
		AssemblyDefinition TargetAssemblyDefinition { get;set;}
		/// <summary>
		/// Gets or sets the containing method.
		/// </summary>
		/// <value>The method.</value>
		[CLSCompliant(false)]
		MethodDefinition Method { get;set;}
		/// <summary>
		/// Gets or sets the called method. For inputfilters this is the containing method.
		/// For outputfilters this is the method to which the outgoing call is targeted
		/// </summary>
		/// <value>The method.</value>
		[CLSCompliant(false)]
		MethodDefinition CalledMethod { get;set;}
		/// <summary>
		/// Gets or sets the worker.
		/// </summary>
		/// <value>The worker.</value>
		[CLSCompliant(false)]
		CilWorker Worker { get;set;}
		/// <summary>
		/// Gets or sets the instructions.
		/// </summary>
		/// <value>The instructions.</value>
		IList<Instruction> Instructions { get;set;}
	}

}
