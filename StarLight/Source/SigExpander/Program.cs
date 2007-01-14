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
using System.Xml.Serialization;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.SigExpander.Properties;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Utilities;
#endregion

namespace Composestar.StarLight.SigExpander
{
	public class Program
	{
		private string _config;
		private IAssemblyResolver _assemblyResolver;

		public Program(string config)
		{
			_config = config;
			_assemblyResolver = new StarLightAssemblyResolver(@"bin\Debug");
		}

		public void Start()
		{
			if (!File.Exists(_config))
				throw new SigExpanderException("Configuration file '" + _config + "' does not exist");

			IEntitiesAccessor accessor = EntitiesAccessor.Instance;
			ConfigurationContainer config = accessor.LoadConfiguration(_config);
			
			foreach (AssemblyConfig ac in config.Assemblies)
			{
				if (ac.ExpansionSpecificationFile != null)
				{
					ExpandedAssembly ea = LoadExpandedAssembly(ac.ExpansionSpecificationFile);
					ExpandAssembly(ea, ac.FileName);
				}
			}
		}

		private ExpandedAssembly LoadExpandedAssembly(string file)
		{
			if (!File.Exists(file))
				throw new SigExpanderException("File '" + file + "' does not exist");

			using (Stream stream = File.OpenRead(file))
			{
				XmlSerializer xs = new XmlSerializer(typeof(ExpandedAssembly));
				return (ExpandedAssembly)xs.Deserialize(stream);
			}
		}

		private void ExpandAssembly(ExpandedAssembly ea, string file)
		{
			Console.WriteLine("Processing assembly '{0}'...", file);

			if (!File.Exists(file))
				throw new SigExpanderException("Assembly '" + file + "' does not exist");

			AssemblyDefinition ad = AssemblyFactory.GetAssembly(file);
			ModuleDefinition module = ad.MainModule;
			TypeResolver typeResolver = new TypeResolver(_assemblyResolver, ad);

			foreach (ExpandedType et in ea.Types)
			{
				TypeDefinition type = module.Types[et.Name];

				if (type == null)
				{
					Console.WriteLine("Warning: could not find type {0}.", et.Name);
					return;
				}

				TypeExpander expander = new TypeExpander(type, typeResolver);
				foreach (MethodElement me in et.ExtraMethods)
				{
					expander.AddEmptyMethod(me);
				}
			}

			string target = GetTargetFileName(file);
			AssemblyFactory.SaveAssembly(ad, target);

			Console.WriteLine("Written '{0}'.", target);
		}

		private string GetTargetFileName(string input)
		{
			return input;
		//	string ext = Path.GetExtension(input);
		//	string noext = Path.GetFileNameWithoutExtension(input);
		//	string dir = Path.GetDirectoryName(input);
		//	return Path.Combine(dir, noext + ".expanded" + ext);
		}

		public static void Main(string[] args)
		{
			if (args.Length != 1)
			{
				Console.WriteLine("Usage: Composestar.StarLight.SigExpander <config file>");
				Environment.Exit(1);
			}

			try
			{
				string config = args[0];
				Program program = new Program(config);
				program.Start();
			}
			catch (Exception e)
			{
				Console.WriteLine("Error: " + e.ToString());
				Environment.Exit(2);
			}
 		}
	}
}
