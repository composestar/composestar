using System.IO;
using antlr;

namespace Composestar.StarLight.TypeAnalyzer.JSharp
{
	public class JSharpPosLexer : JSharpLexer
	{
		protected int col = 1;
		protected int tokenStartCol = 1;
		protected int tokenStartLine = 1;
		protected int tokenStartCharPos = 0;
		protected int charPos = 0;

		public JSharpPosLexer(Stream s)
			: base(s)
		{
		}

		public JSharpPosLexer(TextReader r)
			: base(r)
		{
		}

		public JSharpPosLexer(InputBuffer ib)
			: base(ib)
		{
		}

		public JSharpPosLexer(LexerSharedInputState s)
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
			tok.setPosition(tokenStartCharPos);
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