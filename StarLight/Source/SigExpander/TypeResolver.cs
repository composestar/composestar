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
using Composestar.StarLight.Utilities;
using Mono.Cecil;
#endregion

namespace Composestar.StarLight.SigExpander
{
	public class TypeResolver
	{
		private IAssemblyResolver _assemblyResolver;
		private ModuleDefinition _mainModule;
		private IList<ModuleDefinition> _references;
		private IDictionary<string, TypeReference> _cache;

		public TypeResolver(IAssemblyResolver ar, AssemblyDefinition assembly)
		{
			_assemblyResolver = ar;
			_mainModule = assembly.MainModule;
			_cache = new Dictionary<string, TypeReference>();
		}

		public TypeReference ForceResolve(string name)
		{
			TypeReference tr = Resolve(name);

			if (tr == null)
				throw new SigExpanderException("Could not resolve type '" + name + "'");

			return tr;
		}

		public TypeReference Resolve(string name)
		{
			TypeReference tr = null;
			if (!_cache.TryGetValue(name, out tr))
			{
				tr = InnerResolve(name);
				_cache[name] = tr;
			}

			return tr;
		}

		private TypeReference InnerResolve(string name)
		{
			// try main module
			TypeReference tr = _mainModule.Types[name];
			if (tr != null)
				return tr;

			// try referenced types
			tr = _mainModule.TypeReferences[name];
			if (tr != null)
				return tr;

			// try referenced assemblies
			if (_references == null)
				LoadReferences();

			foreach (ModuleDefinition module in _references)
			{
				tr = module.Types[name];

				if (tr != null)
					return _mainModule.Import(tr);
			}

			// not found
			return null;
		}

		private void LoadReferences()
		{
			_references = new List<ModuleDefinition>();
			foreach (AssemblyNameReference anr in _mainModule.AssemblyReferences)
			{
				AssemblyDefinition asm = _assemblyResolver.Resolve(anr);
				_references.Add(asm.MainModule);
			}
		}
	}
}
