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
#endregion

namespace Composestar.StarLight.Filters.FilterTypes
{
	/// <summary>
	/// The base class of every FilterType. Each subclass should be annotated with the FilterTypeAnnotation
	/// custom attribute, to provide information to the StarLight compiler about the actions in this filtertype.
	/// </summary>
	/// <example>
	/// The following code examples shows how a filtertype can be defined.
	/// <code>
	/// [FilterTypeAttribute("Tracing", "TracingInAction", "ContinueAction", "TracingOutAction", "ContinueAction")]
	/// public class TracingFilterType : FilterType
	/// {
	/// }
	/// </code>
	/// </example> 
	/// <remarks>
	/// This class does not need any implementation and currently provides only a way to declare filtertypes. 
	/// In future releases this class can be extended with more functionality.
	/// </remarks> 
	public abstract class FilterType
	{

		#region BuildIn FilterTypes

		/// <summary>
		/// Specifies an After filter type.
		/// </summary>
		public const string AfterFilter = "After";

		/// <summary>
		/// Specifies a Before filter type.
		/// </summary>
		public const string BeforeFilter = "Before";

		/// <summary>
		/// Specifies a Dispatch filter type.
		/// </summary>
		public const string DispatchFilter = "Dispatch";

		/// <summary>
		/// Specifies an Error filter type.
		/// </summary>
		public const string ErrorFilter = "Error";

		/// <summary>
		/// Specifies a Substitution filter type.
		/// </summary>
		public const string SubstitutionFilter = "Substitution";

		#endregion
	}
}
