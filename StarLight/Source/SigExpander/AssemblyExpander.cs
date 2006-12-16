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
using System.CodeDom.Compiler;

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
		private const string BaseDir = @"C:\CPS\StarLight\Examples\InventoryTwo";
		private const string DummyDir = BaseDir + @"\obj\StarLight\Dummies";

		private string _spec;
		private IList<string> _assemblies;
		private IList<string> _sources;

		public AssemblyExpander(string spec, IList<string> assemblies)
		{
			_spec = spec;
			_assemblies = assemblies;

			_sources = new string[]
			{
				BaseDir + @"\Example.cs",
				BaseDir + @"\Product.cs",
				BaseDir + @"\Inventory.cs",
				BaseDir + @"\InventoryCount.cs",
				BaseDir + @"\InventoryDisplay.cs",
				BaseDir + @"\Bulk\BulkUpdater.cs",
				BaseDir + @"\Observer\IObserver.cs",
				BaseDir + @"\Observer\Observed.cs",
				BaseDir + @"\Observer\Subject.cs",
			};
		}

		public void Start()
		{
			Signatures sigs = EntitiesAccessor.Instance.LoadSignatureSpecification(_spec);
			ExpandAssemblies(sigs, _assemblies);

			IList<string> assemblies = CompileRealSources();
			AssemblyDefinition combined = CombineAssemblies(assemblies);
			UnlinkDummies(combined);

			ExpandAssembly(sigs, combined);
			AssemblyFactory.SaveAssembly(combined, DummyDir + @"\InventoryTwo.exe");
		}

		private void ExpandAssemblies(Signatures sigs, IList<string> assemblies)
		{
			foreach (string assembly in assemblies)
			{
				AssemblyDefinition ad = AssemblyFactory.GetAssembly(assembly);
				ExpandAssembly(sigs, ad);

				string target = GetTargetFileName(assembly);
				AssemblyFactory.SaveAssembly(ad, target);
			}
		}

		private void ExpandAssembly(Signatures sigs, AssemblyDefinition ad)
		{
			TypeResolver resolver = new TypeResolver(ad);
			ModuleDefinition module = ad.MainModule;

			foreach (ExpandedType et in sigs.ExpandedTypes)
			{
				TypeDefinition type = module.Types[et.Name];

				if (type == null) continue;

			//	Console.WriteLine(type.FullName);

				TypeExpander expander = new TypeExpander(type, resolver);
				foreach (MethodElement me in et.ExtraMethods)
				{
					expander.AddDummyMethod(me);
				}
			}
		}
		private IList<string> CompileRealSources()
		{
			CodeDomProvider provider = CodeDomProvider.CreateProvider("CSharp");

			CompilerParameters cp = new CompilerParameters();
			cp.GenerateExecutable = false;
			cp.CompilerOptions = "/nologo";
			cp.ReferencedAssemblies.Add(DummyDir + @"\InventoryTwo.Dummies.dll");

			IList<string> assemblies = new List<string>();
			foreach (string source in _sources)
			{
				string noext = Path.GetFileNameWithoutExtension(source);
				string target = Path.Combine(DummyDir, noext + ".dll");

				Console.WriteLine("Compiling {0} to {1}", source, target);

				cp.OutputAssembly = target;
				CompilerResults cr = provider.CompileAssemblyFromFile(cp, source);

				if (cr.NativeCompilerReturnValue != 0)
				{
					foreach (CompilerError s in cr.Errors)
						Console.WriteLine(s.ErrorText);

					throw new SigExpanderException("Error compiling " + source);
				}

				assemblies.Add(target);
			}

			return assemblies;
		}

		private AssemblyDefinition CombineAssemblies(IList<string> assemblies)
		{
			AssemblyDefinition combined = AssemblyFactory.DefineAssembly("InventoryTwo", AssemblyKind.Console);
			ModuleDefinition mm = combined.MainModule;

			foreach (string asm in assemblies)
			{
				Console.WriteLine(asm);

				AssemblyDefinition ad = AssemblyFactory.GetAssembly(asm);
				ModuleDefinition module = ad.MainModule;

				foreach (TypeDefinition type in module.Types)
				{
					if (type.Name == "<Module>") continue;
					Console.WriteLine("\t" + type.Name);

					mm.Types.Add(mm.Inject(type));
				}
			}

			TypeDefinition example = combined.MainModule.Types["InventoryTwo.Example"];
			combined.EntryPoint = example.Methods.GetMethod("Main", new Type[] { typeof(String[]) });

			return combined;
		}

		private void UnlinkDummies(AssemblyDefinition combined)
		{
			ModuleDefinition mm = combined.MainModule;

		//	FooVisitor v = new FooVisitor(combined);
		//	v.Start();

			// let references to dummies point to the real types
			for (int i = 0; i < mm.TypeReferences.Count; i++)
			{
				TypeReference tr = mm.TypeReferences[i];

				string scopeName = tr.Scope.Name;
				if (scopeName == "InventoryTwo.Dummies" || scopeName == "Subject")
				{
					TypeReference rtr = mm.Types[tr.FullName];
				//	tr.Scope = rtr.Scope; // this requires a change to Cecil
				}
			}

			// find references to the dummy assemblies
			IList<AssemblyNameReference> remove = new List<AssemblyNameReference>();
			foreach (AssemblyNameReference an in mm.AssemblyReferences)
			{
				if (an.Name == "InventoryTwo.Dummies" || an.Name == "Subject")
					remove.Add(an);
			}

			// remove references to the dummy assemblies
			foreach (AssemblyNameReference an in remove)
				mm.AssemblyReferences.Remove(an);
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
			if (args.Length < 2)
			{
				Console.WriteLine("Usage: Composestar.StarLight.SigExpander <spec> <assemblies>");
				Environment.Exit(1);
			}

			string spec = args[0];

			IList<string> assemblies = new List<string>();
			for (int i = 1; i < args.Length; i++)
				assemblies.Add(args[i]);

			try
			{
				AssemblyExpander program = new AssemblyExpander(spec, assemblies);
				program.Start();
			}
			catch (Exception e)
			{
				Console.WriteLine(e);
				Environment.Exit(2);
			}
 		}
	}
	/*
	// A visitor that replaces all TypeReferences can be used 
	// as an alternative to TypeReference.Scope = ...
	// but it requires significantly more effort
	internal class FooVisitor : BaseReflectionVisitor
	{
		private IMetadataScope _scope;
		private ModuleDefinition _module;

		public FooVisitor(AssemblyDefinition ad)
		{
			_scope = ad.Name;
			_module = ad.MainModule;
		}

		public void Start()
		{
			_module.Accept(this);
		}

		public override void VisitTypeDefinitionCollection(TypeDefinitionCollection types)
		{
			VisitCollection(types);
		}

		public override void VisitTypeDefinition(TypeDefinition type)
		{
		//	this.GenericParameters.Accept(visitor);
		//	this.Properties.Accept(visitor);
		//	this.Events.Accept(visitor);
		//	this.NestedTypes.Accept(visitor);
		//	this.CustomAttributes.Accept(visitor);
		//	this.SecurityDeclarations.Accept(visitor);
			Console.WriteLine("*** {0} ***", type);
		}

		public override void VisitInterfaceCollection(InterfaceCollection interfaces)
		{
			for (int i = 0; i < interfaces.Count; i++)
			{
				interfaces[i] = Replace(interfaces[i]);
			}
		}

		public override void VisitConstructorCollection(ConstructorCollection ctors)
		{
			VisitCollection(ctors);
		}

		public override void VisitConstructor(MethodDefinition ctor)
		{
		}

		public override void VisitMethodDefinitionCollection(MethodDefinitionCollection methods)
		{
			VisitCollection(methods);
		}

		public override void VisitMethodDefinition(MethodDefinition method)
		{
			Console.WriteLine("\t" + method);
		}

		public override void VisitParameterDefinitionCollection(ParameterDefinitionCollection parameters)
		{
			VisitCollection(parameters);
		}

		public override void VisitParameterDefinition(ParameterDefinition parameter)
		{
			parameter.ParameterType = Replace(parameter.ParameterType);
		}

		public override void VisitFieldDefinitionCollection(FieldDefinitionCollection fields)
		{
			VisitCollection(fields);
		}

		public override void VisitFieldDefinition(FieldDefinition field)
		{
			field.FieldType = Replace(field.FieldType);
		}

		private TypeReference Replace(TypeReference tr)
		{
			if (tr.Scope.Name == "InventoryTwo.Dummies")
			{
				TypeReference rtr = _module.Types[tr.FullName];

			//	Console.WriteLine("### Replace: {0} -> {1} ###", ToString(tr), ToString(rtr));

				return rtr;
			}
			else
				return tr;
		}

		private string ToString(TypeReference tr)
		{
			if (tr == null) return "null";

			string scope = (tr.Scope == null ? "null" : tr.Scope.Name);
			return string.Format("[{1}] {0}", tr.FullName, scope);
		}
	}*/
}
