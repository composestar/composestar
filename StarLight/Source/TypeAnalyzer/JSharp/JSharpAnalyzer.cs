using System;
using System.IO;
using System.Collections.Generic;
using System.Xml.Serialization;

using antlr.collections;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.TypeAnalyzer.JSharp
{
	public class JSharpAnalyzer : SourceAnalyzer
	{
		public JSharpAnalyzer()
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

				NamespaceContext context = new NamespaceContext(ac, cu.FileName, cu.Package, typeNames);
				foreach (string import in cu.Imports)
				{
					if (import.EndsWith(".*"))
					{
						string ns = import.Substring(0, import.Length - 2);
						context.AddNamespaceImport(ns);
					}
					else
						context.AddSpecificImport(import);
				}

				// always import java.lang.*
				context.AddNamespaceImport("java.lang");

				List<TypeElement> types = CreateTypeElements(context, cu.DefinedTypes);
				result.AddRange(types);
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

			TextReader reader = File.OpenText(source);
			JSharpLexer lexer = new JSharpLexer(reader);
			JSharpParser parser = new JSharpParser(lexer);

			parser.compilationUnit();
			AST root = parser.getAST();
		//	Console.WriteLine(root.ToStringList());

			JSharpWalker walker = new JSharpWalker();
			CompilationUnit cu = walker.compilationUnit(root);
			cu.FileName = source;
			cu.Timestamp = File.GetLastWriteTime(source).Ticks;

			using (TextWriter writer = File.CreateText(source + ".xml"))
			{
				XmlSerializer xs = new XmlSerializer(typeof(CompilationUnit));
				xs.Serialize(writer, cu);
			}

			return cu;
		}

		private Set<string> ExtractDefinedTypeNames(List<CompilationUnit> cus)
		{
			Set<string> result = new Set<string>();
			foreach (CompilationUnit cu in cus)
			{
				foreach (DefinedType dt in cu.DefinedTypes)
				{
					string typeName = cu.Package + "." + dt.Name;
					result.Add(typeName);
				}
			}
			return result;
		}

		protected override string GetBuiltInTypeName(string name)
		{
			return GetPrimitiveFullName(name);
		}

		public static bool IsPrimitive(string name)
		{
			return (GetPrimitiveFullName(name) != null);
		}

		private static string GetPrimitiveFullName(string name)
		{
			switch (name)
			{
				case "void": return "System.Void";
				case "boolean": return "System.Boolean";
				case "byte": return "System.SByte";
				case "ubyte": return "System.Byte";
				case "char": return "System.Char";
				case "short": return "System.Int16";
				case "int": return "System.Int32";
				case "long": return "System.Int64";
				case "float": return "System.Single";
				case "double": return "System.Double";
				default: return null;
			}
		}
	}
}
