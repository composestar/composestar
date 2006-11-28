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
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics.CodeAnalysis;
using System.Text;
#endregion

namespace Composestar.StarLight.CoreServices
{

	/// <summary>
	/// Interface for the IL analyzer
	/// </summary>
	public interface IILAnalyzer : IDisposable
	{

		/// <summary>
		/// Gets the unresolved assemblies.
		/// </summary>
		/// <value>The unresolved assemblies.</value>
		[SuppressMessage("Microsoft.Design", "CA1002")]
		IList<string> UnresolvedAssemblies { get; }

		/// <summary>
		/// Gets the resolved assemblies.
		/// </summary>
		/// <value>The resolved assemblies.</value>
		[SuppressMessage("Microsoft.Design", "CA1002")]
		IList<string> ResolvedAssemblies { get; }

		/// <summary>
		/// Gets or sets the unresolved types.
		/// </summary>
		/// <value>The unresolved types.</value>
		[SuppressMessage("Microsoft.Usage", "CA2227")]
		List<string> UnresolvedTypes { get; set; }

		/// <summary>
		/// Gets the resolved types.
		/// </summary>
		/// <value>The resolved types.</value>
		[SuppressMessage("Microsoft.Design", "CA1002")]
		IList<string> ResolvedTypes { get; }

		/// <summary>
		/// Resolves the assembly locations.
		/// </summary>
		/// <returns></returns>
		[SuppressMessage("Microsoft.Design", "CA1002")]
		IList<String> ResolveAssemblyLocations();

		/// <summary>
		/// Extracts all types.
		/// </summary>
		/// <param name="fileName">Name of the file.</param>
		/// <returns></returns>
		AssemblyElement ExtractAllTypes(String fileName);

		/// <summary>
		/// Gets all encountered FilterTypes
		/// </summary>
		ReadOnlyCollection<FilterTypeElement> FilterTypes { get; }

		/// <summary>
		/// Gets all encountered FilterActions
		/// </summary>
		ReadOnlyCollection<FilterActionElement> FilterActions { get; }

	}
}
