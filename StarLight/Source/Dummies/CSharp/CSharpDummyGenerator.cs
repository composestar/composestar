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
		public static void GenerateDummy(string input, string output)
		{
			IGraph graph = GetGraph(input);
			WriteDummy(graph, output);
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

			using (FileStream s = new FileStream(filename, FileMode.Open, FileAccess.Read))
			{
				CSharpLexer lexer = new CSharpLexer(s);
				lexer.setFilename(filename);
				
				CSharpParser parser = new CSharpParser(lexer);
				parser.setFilename(filename);
				
				parser.compilation_unit();
				if (parser.ErrorCounter > 0)
					throw new Exception("Error while parsing file " + filename);
				
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

			using (StreamWriter sw = fi.CreateText())
			{
				CSharpGen csg = new CSharpGen(sw);
				csg.Parse(graph);
				csg.Close();
			}
		}
	}
}