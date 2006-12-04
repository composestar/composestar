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
using System;
using System.Collections.Generic;
using System.Text;
#endregion

namespace Composestar.StarLight.Filters.FilterTypes
{
	/// <summary>
	/// The base class of every FilterAction. Each subclass should be annotated with the FilterActionAnnotation
	/// custom attribute, to provide information to the StarLight compiler about the behavior of this action concerning
	/// the message in the filterset.
	/// </summary>
	/// <example>
	/// <para>
	/// To create a custom filter action, create a class inheriting <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterAction"></see>. Override the <c>Execute</c> method and supply your own implementation.
	/// Next, place a <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"></see> describing the filter action to the class.</para>
	/// <para>
	/// The following code shows a <i>StartTimerAction</i>, which will start a timer and place the timer values in the context properties of the <see cref="T:JoinPointContext"></see> parameter.</para>
	/// <code>
	/// [FilterActionAttribute("StartTimerAction", FilterFlowBehavior.Continue, MessageSubstitutionBehavior.Original)]
	/// public class StartTimerAction : FilterAction
	/// {
	///     [DllImport("Kernel32.dll")]
	///     private static extern bool QueryPerformanceCounter(
	///         out long lpPerformanceCount);
	///
	///     [DllImport("Kernel32.dll")]
	///     private static extern bool QueryPerformanceFrequency(
	///         out long lpFrequency);
	///        
	///     public override void Execute(JoinPointContext context)
	///     {
	///         long starttime = 0;
	///         long freq = 0;
	///         
	///         if (QueryPerformanceFrequency(out freq) == false) 
	///         {
	///             starttime = DateTime.Now.Ticks;
	///         }
	///         else 
	///         {
	///             QueryPerformanceCounter(out starttime);
	///         }
	///
	///         context.AddProperty("frequency", freq);
	///         context.AddProperty("starttime", starttime);
	///     }
	/// }
	/// </code>
	/// <para>
	/// To use this filter action, implement a <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterType"/> and specify, using the <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterTypeAttribute"/>, the name of this filter action. 
	/// Now you can use the filter type in your concern specification.   
	/// </para> 
	/// <note>
	/// To make a properly working filter, implement also a <i>StopTimerAction</i>, which retrieves the properties from the context and calculates the end time and thus the duration.
	/// </note> 
	/// <note>
	/// The above example uses kernel calls to a high performance counter. It is also possible to use the <see cref="T:System.Diagnostics.Stopwatch"></see> class.
	/// </note> 
	/// </example> 
	/// <seealso cref="T:Composestar.StarLight.Filters.FilterTypes.FilterType"/>
	/// <seealso cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"/>
	public abstract class FilterAction
	{
		/// <summary>
		/// Implements the behavior of the FilterAction. You must override this method and supply your own filteraction implementation.
		/// </summary>
		/// <param name="context">Join Point Context information.</param>
		/// <remarks>If the developer has set the CreateJoinPointContext to <see langword="false"/> 
		/// in the <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"/>, then the weaver injects <see langword="null"/> instead of a 
		/// <see cref="Composestar.StarLight.ContextInfo.JoinPointContext"/>.</remarks>
		public abstract void Execute(JoinPointContext context);

		#region BuiltIn FilterActions

		/// <summary>
		/// Advice action constant value.
		/// </summary>
		public const string AdviceAction = "AdviceAction";

		/// <summary>
		/// Continue action constant value.
		/// </summary>
		public const string ContinueAction = "ContinueAction";

		/// <summary>
		/// Dispatch action constant value.
		/// </summary>
		public const string DispatchAction = "DispatchAction";

		/// <summary>
		/// Error action constant value.
		/// </summary>
		public const string ErrorAction = "ErrorAction";

		/// <summary>
		/// Substitution action constant value.
		/// </summary>
		public const string SubstitutionAction = "SubstitutionAction";

		#endregion

	}
}
