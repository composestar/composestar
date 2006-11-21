using System.IO;
using antlr;

namespace Composestar.StarLight.CpsParser
{
	public class CpsPosLexer : CpsLexer
	{
		protected int col = 1;
		protected int tokenStartCol = 1;
		protected int tokenStartLine = 1;
		protected int tokenStartCharPos = 0; //byte position start at 0
		protected int charPos = 0;

		public CpsPosLexer(Stream s)
			: base(s)
		{
		}

		public CpsPosLexer(TextReader r)
			: base(r)
		{
		}

		public CpsPosLexer(InputBuffer ib)
			: base(ib)
		{
		}

		public CpsPosLexer(LexerSharedInputState s)
			: base(s)
		{
		}

		public override void consume()
		{
			base.consume();
			if (inputState.guessing == 0)
			{
				col++;
				charPos++;
			}
		}

		public override void newline()
		{
			base.newline();
			col = 1;
		}

		protected override IToken makeToken(int t)
		{
			PosToken tok = new PosToken(t);
			tok.setColumn(tokenStartCol);
			tok.setLine(tokenStartLine);
			tok.setBytePos(tokenStartCharPos);
			return tok;
		}

		public override void resetText()
		{
			base.resetText();
			tokenStartCol = col;
			tokenStartLine = getLine();
			tokenStartCharPos = charPos;
		}
	}
}