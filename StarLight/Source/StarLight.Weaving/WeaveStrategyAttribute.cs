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
#endregion

namespace Composestar.StarLight.Weaving.Strategies
{
	/// <summary>
	/// This custom attribute must be applied to weaving strategies to enable the naming of those strategies.      
	/// </summary>
	/// <example>
	/// To use a weaving strategy, create a class which inherits from <see cref="T:FilterActionWeaveStrategy"/> and place this custom attribute to the top.
	/// It is possible to add multiple attributes to a weaving strategy, so you can specify multiple names for a strategy.
	/// <code>
	/// <returns>Attribute</returns>
	/// [WeaveStrategyAttribute("AdviceAction")]
	/// [WeaveStrategyAttribute("BeforeAction")]
	/// [WeaveStrategyAttribute("AfterAction")]
	/// public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
	/// {
	///                
	///    public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
	///        MethodDefinition originalCall)
	///    {
	///
	///    } 
	/// 
	/// }
	/// </code>
	/// </example> 
	[AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = true)]
	[SuppressMessage("Microsoft.Design", "CA1019", Justification = "WeaveStrategyName is read only property accessor")]
	public sealed class WeaveStrategyAttribute : Attribute
	{

		#region Private Variables

		/// <summary>
		/// The name of the weaving strategy
		/// </summary>
		private string _weaveStrategyName;

		#endregion

		#region Properties

		/// <summary>
		/// Gets the name of the weaving strategy.
		/// </summary>
		/// <value>The name of the weaving strategy.</value>
		public string WeavingStrategyName
		{
			get
			{
				return _weaveStrategyName;
			}
		}

		#endregion

		#region ctors

		/// <summary>
		/// Initializes a new instance of the <see cref="T:WeaveStrategyAttribute"/> class. 
		/// This custom attribute must be applied to weaving strategies to enable the naming of those strategies.        
		/// </summary>
		/// <param name="weaveStrategyName">Name of the weave strategy.</param>
		/// <example>
		/// To use a weaving strategy, create a class which inherits from <see cref="T:FilterActionWeaveStrategy"/> and place this custom attribute to the top.     
		/// <code>
		/// [WeaveStrategyAttribute("AdviceAction")]
		/// public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
		/// {
		///                
		///    public override void Weave(ICecilInliningInstructionVisitor visitor, 
		///        FilterAction filterAction,
		///        MethodDefinition originalCall)
		///    {
		///
		///    } 
		/// 
		/// }
		/// </code>
		/// </example> 
		/// <exception cref="ArgumentNullException">Thrown when the <paramref name="weaveStrategyName"/> is <see langword="null"/> or empty.</exception>
		public WeaveStrategyAttribute(string weaveStrategyName)
		{
			if (string.IsNullOrEmpty(weaveStrategyName))
				throw new ArgumentNullException("weaveStrategyName");

			_weaveStrategyName = weaveStrategyName;

		}

		#endregion

	}
}
