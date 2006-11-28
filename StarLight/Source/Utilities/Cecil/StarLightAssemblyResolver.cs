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

using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

namespace Composestar.StarLight.Utilities
{
	/// <summary>
	/// Assembly resolver to resolve assemblies and store those in a cache for quick lookup. 
	/// It will try to lookup assemblies on their assembly name of assembly definition.
	/// </summary>
	/// <remarks>This class holds an internal cache for the found assemblies.</remarks>
	[CLSCompliant(false)]
	public class StarLightAssemblyResolver : BaseAssemblyResolver
	{

		#region Private variables

		private Dictionary<string, AssemblyDefinition> _cache;
		private string _binFolder;

		#endregion

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverAssemblyResolver"/> class.
		/// </summary>
		/// <param name="binFolder">The search folder containing the assemblies.</param>
		public StarLightAssemblyResolver(string binFolder)
		{
			if (String.IsNullOrEmpty(binFolder))
				throw new ArgumentNullException("binFolder");

			_binFolder = binFolder;
			_cache = new Dictionary<string, AssemblyDefinition>();
		}

		#endregion

		#region Resolvers

		/// <summary>
		/// Resolves the assembly by its full name.
		/// </summary>
		/// <param name="fullName">The full name of an assembly.</param>
		/// <example>
		/// <code>
		/// StarLightAssemblyResolver assemblyResolver = new StarLightAssemblyResolver(@"c:\project\bin");
		/// String assemblyName = "assembly.dll";
		/// AssemblyDefinition ad = assemblyResolver.Resolve(assemblyName);
		/// </code>
		/// </example>
		/// <remarks>This functions will parse the <paramref name="fullName"/> to create an <see cref="T:Mono.Cecil.AssemblyNameReference"/>.</remarks>
		/// <returns>Return an <see cref="T:Mono.Cecil.AssemblyDefinition"></see> or <see langword="null"/> when the assembly could not be found.</returns>
		[CLSCompliant(false)]
		public override AssemblyDefinition Resolve(string fullName)
		{
			if (String.IsNullOrEmpty(fullName))
				return null;

			AssemblyNameReference assemblyNameReferenceParsed;

			try
			{
				assemblyNameReferenceParsed = AssemblyNameReference.Parse(fullName);
			}
			catch (ArgumentException)
			{
				return null;
			}

			return Resolve(assemblyNameReferenceParsed);

		}

		/// <summary>
		/// Resolves the assembly by its <see cref="T:Mono.Cecil.AssemblyNameReference"/>.
		/// </summary>
		/// <param name="name">The name and details of the assembly.</param>
		/// <returns>Return an <see cref="T:Mono.Cecil.AssemblyDefinition"></see> or <see langword="null"/> when the assembly could not be found.</returns>
		[CLSCompliant(false)]
		public override AssemblyDefinition Resolve(AssemblyNameReference name)
		{
			AssemblyDefinition asm;
			if (!_cache.TryGetValue(name.FullName, out asm))
			{
				asm = ResolveInternal(name);
				if (asm != null)
					_cache[name.FullName] = asm;
			}

			return asm;
		}

		/// <summary>
		/// Resolves the assemblyname.
		/// </summary>
		/// <param name="name">The name.</param>
		/// <returns></returns>
		private AssemblyDefinition ResolveInternal(AssemblyNameReference name)
		{
			string[] exts = new string[] { ".dll", ".exe" };
			string[] dirs = new string[] { _binFolder, ".", "bin" };

			foreach (string dir in dirs)
			{
				foreach (string ext in exts)
				{
					string file = Path.Combine(dir, name.Name + ext);
					if (File.Exists(file))
					{
						return AssemblyFactory.GetAssembly(file);
					}
				}
			}

			if (name.Name == "mscorlib")
				return GetCorlib(name);

			AssemblyDefinition asm = GetAssemblyInGac(name);
			if (asm != null)
				return asm;

			return null;

		}

		#endregion

	}
}
