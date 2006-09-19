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
	class CSharpDummyGenerator
	{
		private static CSharpAST antlrTree = null;
		private static string filename; // File currently being parsed

		public static string Filename
		{
			get { return filename; }
		}

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
				filename = "";
				while ((filename = Console.ReadLine()) != null)
				{
					IGraph ig = GetGraph(filename);
					// Arg[0] = output directory.
					string outputFilename = Console.ReadLine();

					// Create directory when missing
					FileInfo fi = new FileInfo(outputFilename);
					Directory.CreateDirectory(fi.DirectoryName);

					StreamWriter sr = fi.CreateText();
					CSharpGen csg = new CSharpGen(sr);
					csg.Parse(ig);
					csg.Close();
					sr.Close();
				}
				AttributeWriter.Instance.writeXML(args[0]);
			}
			catch(Exception e) 
			{
				Console.WriteLine("exception: "+e);
				return 1; // Failure
			}
			return 0; // Success
		}
		private static CSharpAST GetAST(string filename)
		{
			CSharpParser parser = null;
			if (File.Exists(filename)) 
			{
				Console.Write("Parsing " + filename + "..");
				FileStream s = new FileStream(filename, FileMode.Open, FileAccess.Read);
				CSharpLexer lexer = new CSharpLexer(s);
				lexer.setFilename(filename);
				parser = new CSharpParser(lexer);
				parser.setFilename(filename);
				//parser.setASTFactory(new LineNumberFactory() );
				// Parse the input expression
				DateTime tStart = DateTime.Now;
				parser.compilation_unit();
				TimeSpan compTime = DateTime.Now - tStart;
				Console.WriteLine(compTime.ToString());
				//App.StatsText = "Time to parse (lex, parse & attrib): " + compTime.ToString();
				s.Close();
			}
			else
				throw new Exception("File not found: '" + filename + "'");
			if(parser != null)
			{
				antlrTree = (CSharpAST)(parser.getAST());
				antlrTree.FileName = filename;
			}
			return antlrTree;
		}

		public static IGraph GetGraph(string filename)
		{
			GetAST(filename);
			if(antlrTree!=null)
				return antlrTree.GetGraph();
			else
				return null;
		}
		public static string AntlrText
		{
			get
			{
				return antlrTree.ToStringTree();
			}
		}			 
	}
}