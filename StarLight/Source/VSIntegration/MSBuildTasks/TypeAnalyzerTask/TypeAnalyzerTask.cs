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
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel.Design;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Analyzer;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.TypeAnalyzer;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	public class TypeAnalyzerTask : Task
	{
		// inputs
		private string _assemblyName;
		private string _codeLanguage;
		private ITaskItem[] _concerns;
		private ITaskItem[] _sources;
		private ITaskItem[] _referencedAssemblies;

		[Required]
		public string AssemblyName
		{
			set { _assemblyName = value; }
		}

		[Required]
		public string CodeLanguage
		{
			set { _codeLanguage = value; }
		}

		[Required]
		public ITaskItem[] Concerns
		{
			set { _concerns = value; }
		}

		[Required]
		public ITaskItem[] Sources
		{
			set { _sources = value; }
		}

		[Required]
		public ITaskItem[] ReferencedAssemblies
		{
			set { _referencedAssemblies = value; }
		}

		public override bool Execute()
		{
			Log.LogMessage("AssemblyName={0}", _assemblyName);
			Log.LogMessage("CodeLanguage={0}", _codeLanguage);

			Log.LogMessage("Concerns:");
			List<string> concerns = new List<string>();
			foreach (ITaskItem item in _concerns)
			{
				concerns.Add(item.ToString());
				Log.LogMessage("\t{0}", item);
			}

			Log.LogMessage("Sources:");
			List<string> sources = new List<string>();
			foreach (ITaskItem item in _sources)
			{
				sources.Add(item.ToString());
				Log.LogMessage("\t{0}", item);
			}

			Log.LogMessage("References:");
			List<string> referencedAssemblies = new List<string>();
			foreach (ITaskItem item in _referencedAssemblies)
			{
				referencedAssemblies.Add(item.ToString());
				Log.LogMessage("\t{0}", item);
			}
		
			TypeAnalyzerImpl analyzer = new TypeAnalyzerImpl();
			analyzer.AssemblyName = _assemblyName;
			analyzer.CodeLanguage = "JSharp";
			analyzer.Concerns = concerns;
			analyzer.Sources = sources;
		//	analyzer.WeavableAssemblies = weavableAssemblies;
			analyzer.ReferencedAssemblies = referencedAssemblies;
			analyzer.Run();
		
			return true;
		}
	}
}
