using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using Microsoft.VisualStudio.Package;
using System.Text.RegularExpressions;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
    /// <summary>
    /// This class implements IScanner interface and performs
    /// text parsing on the base of rules' table. 
    /// </summary>
    internal class ComposeStarScanner : IScanner, IDisposable
    {
        private CpsLexer m_lexer;
        private Token _prevToken;

        public void Dispose()
        {
            Dispose(true);
        }

        private void Dispose(bool disposing)
        {
            if (disposing)
            {

            }
            GC.SuppressFinalize(this);
        }

        #region Private fields
        /// <summary>
        /// Store line of text to parse
        /// </summary>
        private string sourceString;
        /// <summary>
        /// Store position where next token starts in line
        /// </summary>
        private int currentPos;

        #endregion

        public ComposeStarScanner()
        {

        }



        #region IScanner Members

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
            tokenInfo.Type = TokenType.Unknown;
            tokenInfo.Color = TokenColor.Text;

            Token token;
            try
            {
                token = ScanToken(ref state);
            }
            catch (Exception ex)
            {
                return true;
            }

            switch (token.Kind)
            {
                case TokenKind.EndOfFile:
                    return false;

                case TokenKind.Whitespace:
                    tokenInfo.Type = TokenType.WhiteSpace;
                    tokenInfo.Color = TokenColor.Text;

                    if (_prevToken != null)
                        switch (_prevToken.Kind)
                        {
                            case TokenKind.PrologExpression:
                                tokenInfo.Trigger = TokenTriggers.MemberSelect;
                                break;
                        }

                    break;

                // Comment
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

                // Operators
                case TokenKind.Operator:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    break;

                // Indentifiers
                case TokenKind.FileName:
                case TokenKind.FilterType:
                case TokenKind.Identifier:
                    tokenInfo.Type = TokenType.Identifier;
                    tokenInfo.Color = TokenColor.Identifier;
                    break;

                // Keywords
                case TokenKind.Keyword:
                    tokenInfo.Type = TokenType.Keyword;
                    tokenInfo.Color = TokenColor.Keyword;
                    tokenInfo.Trigger = TokenTriggers.MethodTip;
                    break;

                case TokenKind.Number:
                    tokenInfo.Type = TokenType.String;
                    tokenInfo.Color = TokenColor.Number;
                    break;


                case TokenKind.PrologExpression:
                    tokenInfo.Type = TokenType.Text;
                    tokenInfo.Color = TokenColor.Text;
                    break;


                case TokenKind.LeftParenthesis:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterStart | TokenTriggers.MatchBraces;
                    break;

                case TokenKind.RightParenthesis:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterEnd | TokenTriggers.MatchBraces;
                    break;

                case TokenKind.Comma:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterNext;
                    break;

                // Delimiter
                case TokenKind.Dot:
                    tokenInfo.Type = TokenType.Delimiter;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.MemberSelect;
                    break;

                default:
                    tokenInfo.Type = TokenType.Unknown;
                    tokenInfo.Color = TokenColor.Text;
                    break;
            }

            _prevToken = token;

            tokenInfo.StartIndex = token.Start;
            tokenInfo.EndIndex = token.End;

            return true;
        }

        public void SetSource(String source, int offset)
        {
            //	Debug.Print("SetSource: source='{0}'", source);
            _prevToken = null;
            m_lexer = new CpsLexer(source, offset);

        }
        #endregion
    }
}
