using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.VisualStudio.Package;
using System.Diagnostics;
using System.IO;

namespace Trese.ComposestarSupport
{
    class ComposestarScanner : IScanner
    {
		private CpsLexer m_lexer;

		public ComposestarScanner()
		{
			m_lexer = null;
		}

		private Token ScanToken(ref int state)
		{
			if (state == 123)
				return m_lexer.ContinueBlockComment();
			else
				return m_lexer.NextToken();
		}

		public bool ScanTokenAndProvideInfoAboutIt(TokenInfo tokenInfo, ref int state)
		{
		//	Debug.Print("ScanToken: kind={0} start={1} end={2} state={3}", token.Kind, token.Start, token.End, state);

			Token token = ScanToken(ref state);

			switch (token.Kind)
			{
				case TokenKind.EndOfFile:
					return false;

				case TokenKind.Whitespace:
					tokenInfo.Type = TokenType.WhiteSpace;
					tokenInfo.Color = TokenColor.Text;
					break;

				case TokenKind.LineComment:
					tokenInfo.Type = TokenType.LineComment;
					tokenInfo.Color = TokenColor.Comment;
					break;

				case TokenKind.BlockComment:
					tokenInfo.Type = TokenType.Comment;
					tokenInfo.Color = TokenColor.Comment;
					state = 0;
					break;

				case TokenKind.IncompleteComment:
					tokenInfo.Type = TokenType.Comment;
					tokenInfo.Color = TokenColor.Comment;
					state = 123;
					break;

				case TokenKind.FileName:
					tokenInfo.Type = TokenType.String;
					tokenInfo.Color = TokenColor.String;
					break;

				case TokenKind.PrologExpression:
					tokenInfo.Type = TokenType.String;
					tokenInfo.Color = TokenColor.String;
					break;

				case TokenKind.Operator:
					tokenInfo.Type = TokenType.Operator;
					tokenInfo.Color = TokenColor.String;
					break;

				case TokenKind.Number:
					tokenInfo.Type = TokenType.Literal;
					tokenInfo.Color = TokenColor.Number;
					break;

				case TokenKind.Keyword:
					tokenInfo.Type = TokenType.Keyword;
					tokenInfo.Color = TokenColor.Keyword;
					break;

				case TokenKind.FilterType:
					tokenInfo.Type = TokenType.Keyword;
					tokenInfo.Color = TokenColor.Keyword;
					break;

				default:
					tokenInfo.Type = TokenType.Unknown;
					tokenInfo.Color = TokenColor.Text;
					break;
			}

			tokenInfo.StartIndex = token.Start;
			tokenInfo.EndIndex = token.End;

			return true;
		}

		public void SetSource(String source, int offset)
		{
		//	Debug.Print("SetSource: source='{0}'", source);
			m_lexer = new CpsLexer(source, offset);
		}
	}
}
