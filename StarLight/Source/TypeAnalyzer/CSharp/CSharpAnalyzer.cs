#region Using directives
using System;
using System.Collections.Generic;
using System.Xml.Serialization;

using Mono.Cecil;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities;
using Composestar.StarLight.CoreServices;
#endregion

namespace Composestar.StarLight.TypeAnalyzer.CSharp
{
	public class CSharpAnalyzer : SourceAnalyzer
	{
		public CSharpAnalyzer()
		{
		}

		public override List<TypeElement> Analyze(AssemblyContext ac, List<string> sources)
		{
			List<TypeElement> result = new List<TypeElement>();

			// parse sources, yielding a list of CompilationUnits.
			List<CompilationUnit> cus = ParseSources(sources);

			// create a set of type names that are defined in the sources 
			Set<string> typeNames = ExtractDefinedTypeNames(cus);

			// resolve referenced types
			foreach (CompilationUnit cu in cus)
			{
				Console.WriteLine("Analyzing {0}...", cu.FileName);

				Namespace root = cu.Root;
				NamespaceContext context = new NamespaceContext(ac, cu.FileName, root.Name, typeNames);
				result.AddRange(ResolveReferencedTypes(context, root));
			}

			return result;
		}

		private List<CompilationUnit> ParseSources(List<string> sources)
		{
			List<CompilationUnit> result = new List<CompilationUnit>();

			foreach (string source in sources)
			{
				result.Add(ParseSource(source));
			}

			return result;
		}

		private CompilationUnit ParseSource(string source)
		{
			Console.WriteLine("Parsing {0}...", source);
			CompilationUnit cu = new CompilationUnit();
			cu.FileName = source;
			cu.Timestamp = 123456;
			cu.Root = new Namespace();
			cu.Root.Imports.Add("System");			
		/*
			TextReader reader = File.OpenText(source);
			CSharpLexer lexer = new CSharpLexer(reader);
			CSharpParser parser = new CSharpParser(lexer);

			parser.compilationUnit();
			AST root = parser.getAST();
			//	Console.WriteLine(root.ToStringList());

			CSharpWalker walker = new CSharpWalker();
			CompilationUnit cu = walker.compilationUnit(root);
			cu.FileName = source;
			cu.Timestamp = File.GetLastWriteTime(source).Ticks;

			using (TextWriter writer = File.CreateText(source + ".xml"))
			{
				XmlSerializer xs = new XmlSerializer(typeof(CompilationUnit));
				xs.Serialize(writer, cu);
			}
		*/
			return cu;
		}
		
		private Set<string> ExtractDefinedTypeNames(List<CompilationUnit> cus)
		{
			Set<string> typeNames = new Set<string>();
			foreach (CompilationUnit cu in cus)
			{
				ExtractDefinedTypeNames(cu.Root, typeNames);
			}
			return typeNames;
		}

		private void ExtractDefinedTypeNames(Namespace ns, Set<string> typeNames)
		{
			foreach (DefinedType dt in ns.DefinedTypes)
			{
				string typeName = ns.Name + "." + dt.Name;
				typeNames.Add(typeName);
			}

			foreach (Namespace nested in ns.Namespaces)
			{
				ExtractDefinedTypeNames(nested, typeNames);
			}
		}

		private List<TypeElement> ResolveReferencedTypes(NamespaceContext context, Namespace ns)
		{
			List<TypeElement> result = CreateTypeElements(context, ns.DefinedTypes);

			foreach (Namespace nested in ns.Namespaces)
			{
				NamespaceContext c = new NamespaceContext(context, nested.Name);
				result.AddRange(ResolveReferencedTypes(c, nested));
			}

			return result;
		}

		protected override string GetBuiltInTypeName(string name)
		{
			switch (name)
			{
				case "void": return "System.Void";
				case "bool": return "System.Boolean";
				case "byte": return "System.Byte";
				case "sbyte": return "System.SByte";
				case "char": return "System.Char";
				case "decimal": return "System.Decimal";
				case "double": return "System.Double";
				case "float": return "System.Single";
				case "short": return "System.Int16";
				case "ushort": return "System.UInt16";
				case "int": return "System.Int32";
				case "uint": return "System.UInt32";
				case "long": return "System.Int64";
				case "ulong": return "System.UInt64";
				case "object": return "System.Object";
				case "string": return "System.String";
				default: return null;
			}
		}
	}
}
