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
        private antlr.IToken _prevToken;

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

        private antlr.IToken ScanToken(ref int state)
        {
            if (state == 123)
                return m_lexer.nextToken(); //  .ContinueBlockComment();
            else
                return m_lexer.nextToken();
        }

        public bool ScanTokenAndProvideInfoAboutIt(TokenInfo tokenInfo, ref int state)
        {
            //	Debug.Print("ScanToken: kind={0} start={1} end={2} state={3}", token.Kind, token.Start, token.End, state);
            tokenInfo.Trigger = TokenTriggers.None;
            tokenInfo.Type = TokenType.Unknown;
            tokenInfo.Color = TokenColor.Text;

            antlr.IToken token;
            try
            {
                token = ScanToken(ref state);
            }
            catch (Exception ex)
            {                
                return true;
            }            
			
            switch (token.Type)
            {
                case CpsTokenTypes.EOF:
                    return false;

                case CpsTokenTypes.WS:
                    tokenInfo.Type = TokenType.WhiteSpace;
                    tokenInfo.Color = TokenColor.Text;

                    if (_prevToken != null)
                        switch (_prevToken.Type)
                        {
                            case CpsTokenTypes.PROLOG_EXPRESSION:
                            case CpsTokenTypes.PROLOG_SUB_EXPRESSION:
                                tokenInfo.Trigger = TokenTriggers.MemberSelect;
                                break;
                        }

                    break;

                // Comment
                case CpsTokenTypes.COMMENT:
                    tokenInfo.Type = TokenType.LineComment;
                    tokenInfo.Color = TokenColor.Comment;
                    break;

                case CpsTokenTypes.COMMENTITEMS:
                    tokenInfo.Type = TokenType.Comment;
                    tokenInfo.Color = TokenColor.Comment;
                    state = 0;
                    break;

                // Operators
                case CpsTokenTypes.AND:
                case CpsTokenTypes.ANDEXPR_:
                case CpsTokenTypes.ANNOT_:
                case CpsTokenTypes.ANNOTELEM_:
                case CpsTokenTypes.ANNOTSET_:
                case CpsTokenTypes.OR:
                case CpsTokenTypes.OREXPR_:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    break;

                // Indentifiers
                case CpsTokenTypes.NAME:
                case CpsTokenTypes.FILENAME:
                case CpsTokenTypes.PARAMETER_NAME:                    
                    tokenInfo.Type = TokenType.Identifier;
                    tokenInfo.Color = TokenColor.Identifier;
                    break;

                // Keywords
                case CpsTokenTypes.LITERAL_annotations:
                case CpsTokenTypes.LITERAL_as:
                case CpsTokenTypes.LITERAL_by:
                case CpsTokenTypes.LITERAL_concern:
                case CpsTokenTypes.LITERAL_conditions:
                case CpsTokenTypes.LITERAL_constraints:
                case CpsTokenTypes.LITERAL_externals:
                case CpsTokenTypes.LITERAL_filtermodule:
                case CpsTokenTypes.LITERAL_filtermodules:
                case CpsTokenTypes.LITERAL_implementation:
                case CpsTokenTypes.LITERAL_in:
                case CpsTokenTypes.LITERAL_inputfilters:
                case CpsTokenTypes.LITERAL_internals:
                case CpsTokenTypes.LITERAL_outputfilters:
                case CpsTokenTypes.LITERAL_pre:
                case CpsTokenTypes.LITERAL_prehard:
                case CpsTokenTypes.LITERAL_presoft:
                case CpsTokenTypes.LITERAL_selectors:
                case CpsTokenTypes.LITERAL_superimposition:
                    tokenInfo.Type = TokenType.Keyword;
                    tokenInfo.Color = TokenColor.Keyword;
                    tokenInfo.Trigger = TokenTriggers.MethodTip;
                    break;

                // String
                case CpsTokenTypes.LETTER:
                    tokenInfo.Type = TokenType.String;
                    tokenInfo.Color = TokenColor.String;
                    break;
                case CpsTokenTypes.DIGIT:
                    tokenInfo.Type = TokenType.String;
                    tokenInfo.Color = TokenColor.Number;
                    break;


                case CpsTokenTypes.PROLOG_EXPRESSION:
                    tokenInfo.Type = TokenType.String;
                    tokenInfo.Color = TokenColor.String;
                    break;

                case CpsTokenTypes.CONDITION_:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    break;

                case CpsTokenTypes.LCURLY:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterStart | TokenTriggers.MatchBraces;
                    break;

                case CpsTokenTypes.RCURLY:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterEnd | TokenTriggers.MatchBraces;
                    break;

                case CpsTokenTypes.COMMA:
                    tokenInfo.Type = TokenType.Operator;
                    tokenInfo.Color = TokenColor.String;
                    tokenInfo.Trigger = TokenTriggers.ParameterNext;
                    break;

                // Delimiter
                case CpsTokenTypes.DOT:
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

            tokenInfo.StartIndex = token.getColumn() - 2;
            tokenInfo.EndIndex = tokenInfo.StartIndex + token.getText().Length;

            return true;
        }

        public void SetSource(String source, int offset)
        {
            //	Debug.Print("SetSource: source='{0}'", source);
            _prevToken  = null;
            m_lexer = new CpsLexer(new StringReader(source));
            m_lexer.setTabSize(4);
            m_lexer.setLine(offset);
        }
        #endregion
    }
}
