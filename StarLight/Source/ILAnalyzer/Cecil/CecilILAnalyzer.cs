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
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Security.Policy;  //for evidence object

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Entities.Configuration;

using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.Utilities;
#endregion

namespace Composestar.StarLight.ILAnalyzer
{
	/// <summary>
	/// An implementation of the IILAnalyzer working with Cecil.
	/// </summary>
	public class CecilILAnalyzer : IILAnalyzer
	{

		#region Private Variables

		private IList<String> _resolvedAssemblies = new List<String>();
		private IList<String> _unresolvedAssemblies = new List<String>();
		private IList<String> _resolvedTypes = new List<String>();
		private List<String> _unresolvedTypes = new List<String>();
		private IList<String> _cachedTypes = new List<String>();

		private CecilAnalyzerConfiguration _configuration;
		private StarLightAssemblyResolver _dar;

		private List<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
		private List<FilterActionElement> _filterActions = new List<FilterActionElement>();

		#endregion

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:CecilILAnalyzer"/> class.
		/// </summary>
		/// <param name="configuration">The configuration.</param>
		/// <param name="languageModelAccessor">The language model accessor.</param>
		public CecilILAnalyzer(CecilAnalyzerConfiguration configuration, IEntitiesAccessor entitiesAccessor)
		{
			#region Check for null values

			if (configuration == null) throw new ArgumentNullException("configuration");

			#endregion

			_configuration = configuration;

		}

		#endregion

		#region Properties

		/// <summary>
		/// Gets or sets the resolved types.
		/// </summary>
		/// <value>The resolved types.</value>
		public IList<String> ResolvedTypes
		{
			get
			{
				return _resolvedTypes;
			}
		}

		/// <summary>
		/// Gets or sets the unresolved types.
		/// </summary>
		/// <value>The unresolved types.</value>
		public List<String> UnresolvedTypes
		{
			get
			{
				return _unresolvedTypes;
			}
			set
			{
				_unresolvedTypes = value;
			}
		}

		/// <summary>
		/// Gets the assembly resolver.
		/// </summary>
		/// <value>The assembly resolver.</value>
		private StarLightAssemblyResolver AssemblyResolver
		{
			get
			{
				if (_dar == null)
				{
					lock (this)
					{
						if (_dar == null)
						{
							_dar = new StarLightAssemblyResolver(_configuration.BinFolder);
						} // if
					} // lock

				} // if
				return _dar;
			}
		}


		/// <summary>
		/// Gets the unresolved assemblies.
		/// </summary>
		/// <value>The unresolved assemblies.</value>
		public IList<String> UnresolvedAssemblies
		{
			get { return _unresolvedAssemblies; }
		}

		/// <summary>
		/// Gets the resolved assemblies.
		/// </summary>
		/// <value>The resolved assemblies.</value>
		public IList<String> ResolvedAssemblies
		{
			get { return _resolvedAssemblies; }
		}

		/// <summary>
		/// Gets the cached types.
		/// </summary>
		/// <value>The cached types.</value>
		public IList<String> CachedTypes
		{
			get { return _cachedTypes; }
		}

		/// <summary>
		/// Gets all encountered FilterTypes
		/// </summary>
		/// <value></value>
		public ReadOnlyCollection<FilterTypeElement> FilterTypes
		{
			get
			{
				return new ReadOnlyCollection<FilterTypeElement>(_filterTypes);
			}
		}

		/// <summary>
		/// Gets all encountered FilterActions
		/// </summary>
		/// <value></value>
		public ReadOnlyCollection<FilterActionElement> FilterActions
		{
			get
			{
				return new ReadOnlyCollection<FilterActionElement>(_filterActions);
			}
		}

		#endregion

		#region IILAnalyzer Implementation

		/// <summary>
		/// Extracts all types.
		/// </summary>
		/// <param name="fileName">Name of the file.</param>
		/// <returns></returns>
		/// <exception cref="ArgumentException">If the filename is not specified this exception is thrown.</exception>
		/// <exception cref="FileNotFoundException">If the source file cannot be found, this exception will be thrown.</exception>
		public AssemblyElement ExtractAllTypes(String fileName)
		{

			#region Checks for null and file exists

			if (String.IsNullOrEmpty(fileName))
				throw new ArgumentException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.FileNameNullOrEmpty));

			if (!File.Exists(fileName))
			{
				throw new FileNotFoundException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.FileNotFound, fileName));
			}

			#endregion

			AssemblyElement result = null;

			// Create a stopwatch for timing
			Stopwatch sw = new Stopwatch();

			// Create the visitor
			CecilAssemblyVisitor visitor = new CecilAssemblyVisitor();

			// Set visitor properties
			visitor.ProcessMethodBody = _configuration.DoMethodCallAnalysis;
			visitor.IncludeFields = _configuration.DoFieldAnalysis;
			visitor.ExtractUnresolvedOnly = _configuration.ExtractUnresolvedOnly;
			visitor.SaveInnerType = true;
			visitor.SaveType = true;
			visitor.ResolvedAssemblies = _resolvedAssemblies;
			visitor.UnresolvedAssemblies = _unresolvedAssemblies;
			visitor.UnresolvedTypes = _unresolvedTypes;
			visitor.ResolvedTypes = _resolvedTypes;

			// Start the visitor
			result = visitor.Analyze(fileName);

			// Update the unresolved types
			_unresolvedAssemblies = visitor.UnresolvedAssemblies;
			_resolvedAssemblies = visitor.ResolvedAssemblies;
			_unresolvedTypes = visitor.UnresolvedTypes;
			_resolvedTypes = visitor.ResolvedTypes;

			// Update the filtertypes
			_filterTypes.AddRange(visitor.FilterTypes);
			_filterActions.AddRange(visitor.FilterActions);

			// Stop the timer
			sw.Stop();

			// Return the result
			return result;
		}

		/// <summary>
		/// Resolve assembly locations
		/// </summary>
		/// <returns>List</returns>
		public IList<String> ResolveAssemblyLocations()
		{
			IList<string> ret = new List<string>();

			// Go through each assembly name
			foreach (String assemblyName in _unresolvedAssemblies)
			{
				try
				{
					AssemblyDefinition ad = AssemblyResolver.Resolve(assemblyName);

					if (ad != null)
					{
						ret.Add(ad.MainModule.Image.FileInformation.FullName);
					} 
					else
					{
						throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.UnableToResolveAssembly, assemblyName), assemblyName);
					} 
				} 
				catch (Exception ex)
				{
					throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.UnableToResolveAssembly, assemblyName), assemblyName, ex);
				} 
			} 

			return ret;
		} 

		#endregion

		#region IDisposable

		/// <summary>
		/// Cleans up any resources associated with this instance.
		/// </summary>
		public void Dispose()
		{
			Dispose(true);
			GC.SuppressFinalize(this);  // Finalization is now unnecessary
		}

		/// <summary>
		/// Disposes the object.
		/// </summary>
		/// <param name="disposing">if set to <c>true</c> then the managed resources are disposed.</param>
		protected virtual void Dispose(bool disposing)
		{
			if (!m_disposed)
			{
				if (disposing)
				{
					// Dispose managed resources
					if (_dar != null)
						_dar = null;
				}

				// Dispose unmanaged resources
			}

			m_disposed = true;
		}

		private bool m_disposed;

		#endregion

		/// <summary>
		/// Returns a <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
		/// </summary>
		/// <returns>
		/// A <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
		/// </returns>
		public override string ToString()
		{
			return "Cecil IL Analyzer";
		}
	}
}
