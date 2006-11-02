using System;

using CommonAST				= antlr.CommonAST;
using AST					= antlr.collections.AST;
using DumpASTVisitor		= antlr.DumpASTVisitor;
using CharBuffer			= antlr.CharBuffer;
using RecognitionException	= antlr.RecognitionException;
using TokenStreamException	= antlr.TokenStreamException;
using antlr.debug;

using System.IO;

using DDW.CSharp.Parse;
using DDW.CSharp.Dom;
using DDW.CSharp.Walk;
using DDW.CSharp.Gen;
using DDW.CSharp;

namespace DDW.CSharpUI
{
	public class CSharpDummyGenerator
	{
		[STAThread]
		public static int Main(string[] args) 
		{
			if (args.Length < 1)
			{
				Console.WriteLine("Usage: CSharpDummyGenerator <attribute-xml-filename>");
				return 2; // Failure
			}

			try 
			{	
				string input = "";
				while ((input = Console.ReadLine()) != null)
				{
					currentFilename = input;

					// next line is the target filename.
					string output = Console.ReadLine();

					if (output == null || output.Length == 0)
						throw new Exception("No target filename specified for input '" + input + "'");

					IGraph graph = GetGraph(input);
					WriteDummy(graph, output);
				}

				String attributeFile = args[0];
				AttributeWriter.Instance.writeXML(attributeFile);

				return 0; // Success
			}
			catch (RecognitionException re)
			{
				Console.WriteLine();
				Console.WriteLine("DUMMER~error~" + re.getFilename() + "~" + re.getLine() + "~" + re.Message);

				return 1; // Failure!
			}
			catch (Exception e)
			{
				Console.WriteLine();
				Console.WriteLine("DUMMER~error~~0~" + e.Message);

				return 1; // Failure
			}
		}

		private static IGraph GetGraph(string filename)
		{
			CSharpAST ast = GetAST(filename);
			return (ast == null ? null : ast.GetGraph());
		}

		private static CSharpAST GetAST(string filename)
		{
			if (! File.Exists(filename)) 
				throw new Exception("File not found: '" + filename + "'");

			Console.WriteLine("Parsing " + filename + "..");

			using (FileStream s = new FileStream(filename, FileMode.Open, FileAccess.Read))
			{
				CSharpLexer lexer = new CSharpLexer(s);
				lexer.setFilename(filename);
				
				CSharpParser parser = new CSharpParser(lexer);
				parser.setFilename(filename);
				
				// Parse the input expression
				DateTime tStart = DateTime.Now;
				parser.compilation_unit();
				if (parser.ErrorCounter > 0)
					throw new Exception("Error while parsing file " + filename);
				TimeSpan compTime = DateTime.Now - tStart;
				Console.WriteLine(compTime.ToString());
				
				CSharpAST ast = (CSharpAST)parser.getAST();
				ast.FileName = filename;
				return ast;
			}
		}

		private static void WriteDummy(IGraph graph, string output)
		{
			// Create directory when missing
			FileInfo fi = new FileInfo(output);
			Directory.CreateDirectory(fi.DirectoryName);

			StreamWriter sr = fi.CreateText();
			CSharpGen csg = new CSharpGen(sr);
			csg.Parse(graph);
			csg.Close();
			sr.Close();
		}

		private static string currentFilename;
		public static string getCurrentFilename()
		{
			return currentFilename;
		}
	}
}