using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
	public class CpsLexer
	{
		private char[] m_source;
		private int m_pos;
		private int m_end;

		private IDictionary<string, TokenKind> m_keywords;

		public CpsLexer(String source, int offset)
		{
			int length = source.Length - offset;
			m_source = source.ToCharArray(offset, length);
			m_pos = 0;
			m_end = m_source.Length;

			m_keywords = CreateKeywords();
		}

		private IDictionary<string, TokenKind> CreateKeywords()
		{
			IDictionary<String, TokenKind> keywords = new Dictionary<String, TokenKind>();

			keywords["concern"] = TokenKind.Keyword;
			keywords["in"] = TokenKind.Keyword;
			keywords["filtermodules"] = TokenKind.Keyword;
			keywords["filtermodule"] = TokenKind.Keyword;
			keywords["internals"] = TokenKind.Keyword;
			keywords["externals"] = TokenKind.Keyword;
			keywords["conditions"] = TokenKind.Keyword;
			keywords["inputfilters"] = TokenKind.Keyword;
			keywords["outputfilters"] = TokenKind.Keyword;
			keywords["superimposition"] = TokenKind.Keyword;
			keywords["selectors"] = TokenKind.Keyword;
			keywords["methods"] = TokenKind.Keyword;
			keywords["annotations"] = TokenKind.Keyword;
			keywords["constraints"] = TokenKind.Keyword;
			keywords["presoft"] = TokenKind.Keyword;
			keywords["pre"] = TokenKind.Keyword;
			keywords["prehard"] = TokenKind.Keyword;
			keywords["implementation"] = TokenKind.Keyword;
			keywords["by"] = TokenKind.Keyword;
			keywords["as"] = TokenKind.Keyword;

			keywords["Dispatch"] = TokenKind.FilterType;
			keywords["Send"] = TokenKind.FilterType;
			keywords["Error"] = TokenKind.FilterType;
			keywords["Before"] = TokenKind.FilterType;
			keywords["After"] = TokenKind.FilterType;

			return keywords;
		}

		public Token NextToken()
		{
			if (EOF()) return Token.EOF;

			Token result = new Token();
			result.Start = m_pos;

			int ch1 = PeekChar(1);
			int ch2 = PeekChar(2);

			// whitespace
			if (Char.IsWhiteSpace((char)ch1))
			{
				result.Kind = ReadWhitespace();
			}
			// keyword/number/identifier
			else if (Char.IsLetterOrDigit((char)ch1))
			{
				String word = ReadWord();
				result.Kind = GetWordKind(word);
			}
			// single- or multi-line comment
			else if (ch1 == '/')
			{
				if (ch2 == '/')
					result.Kind = ReadLineComment();
				else if (ch2 == '*')
					result.Kind = ReadBlockComment();
				else
					Advance();
			}
			// filename
			else if (ch1 == '"')
			{
				result.Kind = ReadFileName();
			}
			// prolog expression
			else if (ch1 == '|')
			{
				result.Kind = ReadPrologExpression();
			}
			// <- => ~> ::
			else if (TryRead("<-") || TryRead("::"))
			{
				result.Kind = TokenKind.Operator;
			}
			else if (TryRead("=>") || TryRead("~>"))
			{
				result.Kind = TokenKind.ConditionOperator;
			}
			// single-char operator
			else if (IsOperator(ch1))
			{
				Advance();
				result.Kind = TokenKind.Operator;
			}
			else if (TryRead("{"))
				result.Kind = TokenKind.LeftParenthesis;
			else if (TryRead("}"))
				result.Kind = TokenKind.RightParenthesis;
			else if (TryRead(","))
				result.Kind = TokenKind.Comma;
			else if (TryRead("."))
				result.Kind = TokenKind.Dot;
			else
				Advance();

			result.End = m_pos - 1;
			return result;
		}

		private bool IsOperator(int ch1)
		{
			switch (ch1)
			{
			//	case '|': // conflicts with PrologExpression
				case '&':
				case ':':
				case '#':
				case '(':
				case '[':
				case '!':
				case '>':
				case ')':
				case ']':
				case ';':
				case '*':
				case '\\':
				case '?':
					return true;
				default:
					return false;
			}
		}

		public Token ContinueBlockComment()
		{
			if (EOF()) return Token.EOF;

			Token result = new Token();
			result.Start = m_pos;
			result.Kind = ReadBlockCommentEnd();
			result.End = m_pos - 1;
			return result;
		}

		private TokenKind ReadWhitespace()
		{
			while (!EOF())
			{
				char ch = (char)PeekChar(1);
				if (!Char.IsWhiteSpace(ch))
					break;
				Advance();
			}
			return TokenKind.Whitespace;
		}

		private TokenKind ReadLineComment()
		{
			if (!TryRead("//"))
				throw new Exception("// expected");

			while (!EOF())
				Advance();

			return TokenKind.LineComment;
		}

		private TokenKind ReadBlockComment()
		{
			if (!TryRead("/*"))
				throw new Exception("/* expected");

			return ReadBlockCommentEnd();
		}

		private TokenKind ReadBlockCommentEnd()
		{
			while (!EOF())
			{
				if (ReadChar() == '*')
				{
					if (EOF())
						return TokenKind.IncompleteComment;
					else if (ReadChar() == '/')
						return TokenKind.BlockComment;
				}
			}

			return TokenKind.IncompleteComment;
		}

		private TokenKind ReadFileName()
		{
			if (ReadChar() != '"')
				throw new Exception("/* expected");

			while (!EOF() && ReadChar() != '"');
			return TokenKind.FileName;
		}

		// TODO: support multiple lines
		private TokenKind ReadPrologExpression()
		{
			while (!EOF() && ReadChar() != '}') ;
			return TokenKind.PrologExpression;
		}

		private String ReadWord()
		{
			StringBuilder sb = new StringBuilder();
			while (!EOF())
			{
				int ch = PeekChar(1);
				if (Char.IsLetterOrDigit((char)ch))
				{
					sb.Append((char)ch);
					Advance();
				}
				else
					break;
			}
			return sb.ToString();
		}

		private TokenKind GetWordKind(String word)
		{
			if (m_keywords.ContainsKey(word))
				return m_keywords[word];
			else if (Char.IsDigit(word[0]))
				return TokenKind.Number;
			else
				return TokenKind.Identifier;
		}

		private bool EOF()
		{
			return (m_pos >= m_end);
		}

		private void Advance()
		{
			m_pos++;
		}

		private void Advance(int offset)
		{
			m_pos += offset;
		}

		private int ReadChar()
		{
		//	Debug.Print("ReadChar: {0}", m_source[m_pos]);
			return (m_pos >= m_end ? -1 : m_source[m_pos++]);
		}

		private int PeekChar(int offset)
		{
			int p = m_pos + offset - 1;
			return (p >= m_end ? -1 : m_source[p]);
		}

		private bool Peek(String s)
		{
			for (int i = 0; i < s.Length; i++)
			{
				if (s[i] != PeekChar(i + 1))
					return false;
			}
			return true;
		}

		private bool TryRead(String s)
		{
			if (Peek(s))
			{
				Advance(s.Length);
				return true;
			}
			else
				return false;
		}
	}

	public class Token
	{
		public static readonly Token EOF = new Token(TokenKind.EndOfFile, 0, 0);

		private TokenKind m_kind;
		private int m_start;
		private int m_end;

		public Token(TokenKind kind, int start, int end)
		{
			m_kind = kind;
			m_start = start;
			m_end = end;
		}

		public Token()
			: this(TokenKind.Unknown, 0, 0)
		{
		}

		public TokenKind Kind
		{
			get { return m_kind; }
			set { m_kind = value; }
		}

		public int Start
		{
			get { return m_start; }
			set { m_start = value; }
		}

		public int End
		{
			get { return m_end; }
			set { m_end = value; }
		}
	}

	public enum TokenKind
	{
		EndOfFile,
		Unknown,
		Whitespace,
		LineComment,
		BlockComment,
		IncompleteComment,
		FileName,
		PrologExpression,
		Operator,
		Number,
		Identifier,
		Keyword,
		FilterType,
		ConditionOperator,
		LeftParenthesis,
		RightParenthesis,
		Comma,
		Dot,
	}
}
