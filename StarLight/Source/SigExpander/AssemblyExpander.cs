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
using System.Text;
using System.Collections.Generic;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.SigExpander.Properties;
using Composestar.StarLight.Entities.LanguageModel;
#endregion

namespace Composestar.StarLight.SigExpander
{
	public class AssemblyExpander
	{
		private string _spec;
		private string _assembly;

		public AssemblyExpander(string spec, string assembly)
		{
			_spec = spec;
			_assembly = assembly;
		}

		public void Start()
		{
			Signatures sigs = EntitiesAccessor.Instance.LoadSignatureSpecification(_spec);

			AssemblyDefinition ad = AssemblyFactory.GetAssembly(_assembly);
			ProcessAssembly(ad, sigs);

			AssemblyFactory.SaveAssembly(ad, "foo.dll");
		}

		private void ProcessAssembly(AssemblyDefinition ad, Signatures sigs)
		{
			TypeResolver resolver = new TypeResolver(ad);
			ModuleDefinition module = ad.MainModule;

			foreach (ExpandedType et in sigs.ExpandedTypes)
			{
				TypeDefinition type = module.Types[et.Name];
				TypeExpander expander = new TypeExpander(type, resolver);

				Console.WriteLine(type.FullName);

				foreach (MethodElement me in et.ExtraMethods)
				{
					expander.AddDummyMethod(me);
				}
			}
		}

		public static void Main(string[] args)
		{
			if (args.Length != 2)
			{
				Console.WriteLine("Usage: Composestar.StarLight.SigExpander <spec> <assembly>");
				Environment.Exit(1);
			}

			string spec = args[0];
			string assembly = args[1];

			try
			{
				AssemblyExpander program = new AssemblyExpander(spec, assembly);
				program.Start();
			}
			catch (SigExpanderException e)
			{
				Console.WriteLine(e.Message);
				Environment.Exit(2);
			}
		}
	}
}
