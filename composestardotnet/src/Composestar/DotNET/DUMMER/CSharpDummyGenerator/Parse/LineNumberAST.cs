using System;
using antlr;
using antlr.collections;

namespace DDW.CSharp.Parse
{
	public class LineNumberAST : CommonAST
	{


		public LineNumberAST(){}
		public LineNumberAST(Token tok) : base(tok){}
		public override void initialize(AST t)
		{
			base.initialize(t);
		}
		public override void initialize(Token tok) 
		{
			base.initialize(tok);
			//CSharpAST.CurCol = tok.getColumn();
			//CSharpAST.CurLine = tok.getLine();
			//Console.WriteLine(tok);
		}

	}
}
