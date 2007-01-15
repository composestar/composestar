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
using System.IO;
using System.Text;
using System.Collections.Generic;

using Composestar.StarLight.TypeAnalyzer.Cps;
using Composestar.StarLight.TypeAnalyzer.CSharp;
using Composestar.StarLight.TypeAnalyzer.JSharp;
using Composestar.StarLight.Entities.LanguageModel;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.Configuration;
using System.Diagnostics;
#endregion

namespace Composestar.StarLight.TypeAnalyzer
{
	public class TypeAnalyzerImpl
	{
		private string _codeLanguage;
		private string _assemblyName;
		private List<string> _concerns;
		private List<string> _sources;
		private List<string> _weavableAssemblies;
		private List<string> _referencedAssemblies;

		public TypeAnalyzerImpl()
		{
		}

		public string AssemblyName
		{
			set { _assemblyName = value; }
		}

		public string CodeLanguage
		{
			set { _codeLanguage = value; }
		}

		public List<string> Concerns
		{
			set { _concerns = value; }
		}

		public List<string> Sources
		{
			set { _sources = value; }
		}

		public List<string> WeavableAssemblies
		{
			set { _weavableAssemblies = value; }
		}

		public List<string> ReferencedAssemblies
		{
			set { _referencedAssemblies = value; }
		}

		public void Run()
		{
			StoreAssemblyElement(AnalyzeSources());
		/*
			AssemblyAnalyzer aa = new AssemblyAnalyzer();
			foreach (string fn in _weavableAssemblies)
			{
				AssemblyElement asm = aa.Analyze(fn);
				StoreAssemblyElement(asm);
			}
		*/
		}

		private AssemblyElement AnalyzeSources()
		{
			AssemblyElement asm = new AssemblyElement();
			asm.Name = _assemblyName;
			asm.FileName = "...";

			List<string> refs = new List<string>();
			refs.Add(@"C:\WINDOWS\Microsoft.NET\Framework\v2.0.50727\mscorlib.dll");
			refs.Add(@"C:\WINDOWS\assembly\GAC_32\vjslib\2.0.0.0__b03f5f7f11d50a3a\vjslib.dll");

			if (_weavableAssemblies != null)
				refs.AddRange(_weavableAssemblies);

			if (_referencedAssemblies != null)
				refs.AddRange(_referencedAssemblies);

			AssemblyContext rc = new AssemblyContext(refs);

			SourceAnalyzer sa = GetSourceAnalyzer(_codeLanguage);
			List<TypeElement> types = sa.Analyze(rc, _sources);
			asm.Types.AddRange(types);

			return asm;
		}

		private SourceAnalyzer GetSourceAnalyzer(string language)
		{
			switch (language)
			{
				case "JSharp": return new JSharpAnalyzer();
				case "CSharp": return new CSharpAnalyzer();
				default: throw new Exception("Unknown source language " + language);
			}
		}

		private void StoreAssemblyElement(AssemblyElement asm)
		{
			string filename = @"C:\CPS\StarLight\Examples\PacmanTwo\obj\StarLight\" + asm.Name + ".xml";
			using (TextWriter writer = File.CreateText(filename))
			{
				XmlSerializer xs = new XmlSerializer(typeof(AssemblyElement));
				xs.Serialize(writer, asm);
			}
		}
	}
}
