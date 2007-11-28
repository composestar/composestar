#region License
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id: Pattern.java 3953 2007-11-27 11:26:28Z elmuerte $
 */
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    /// A regular pattern (see Composestar.Core.FIRE2.util.regex.Composestar.Core.FIRE2.util.regex;)
    /// </summary>
    public sealed class RegularPattern : Pattern
    {
        private RegularAutomaton automaton;

        /// <summary>
        /// Parses the input string and returns an instance of the pattern
        /// </summary>
        /// <param name="pattern"></param>
        /// <returns></returns>
        public static RegularPattern compile(string pattern)
        {
            return new RegularPattern(pattern);
        }

        private RegularPattern(string pattern)
            : base(pattern)
        {
            automaton = Parser.parse(pattern);
        }

        /// <summary>
        /// Get the start state
        /// </summary>
        /// <returns></returns>
        public override RegularState getStartState()
        {
            return automaton.getStartState();
        }

        /// <summary>
        /// Get the end state
        /// </summary>
        /// <returns></returns>
        public override RegularState getEndState()
        {
            return automaton.getEndState();
        }

        /// <summary>
        /// Parser for the regular language
        /// </summary>
        private static class Parser
        {
            /// <summary>
            /// Parse the input string and return a regular automaton
            /// </summary>
            /// <param name="pattern"></param>
            /// <returns></returns>
            public static RegularAutomaton parse(string pattern)
            {
                Lexer lexer = new Lexer(pattern);
                RegularState end = new FinalRegularState();
                RegularState start = pAlt(lexer, end);
                RegularAutomaton auto = new RegularAutomaton();
                auto.setStartState(start);
                auto.setEndState(end);
                if (lexer.token().type != Token.EOF)
                {
                    throw new PatternParseException(String.Format("Garbage token '%s' at #%d", lexer.token().text, lexer
                            .charPos()));
                }
                return auto;
            }

            /// <summary>
            /// Process alternatives. This will optimize the state machine where
            /// possible. Edges with identical destinations will be merged, but only
            /// when they are not a lambda transition or contain a self reference.
            /// </summary>
            /// <param name="lexer"></param>
            /// <param name="endState"></param>
            /// <returns></returns>
            private static RegularState pAlt(Lexer lexer, RegularState endState)
            {
                List<RegularState> alts = new List<RegularState>();
                alts.Add(pSeq(lexer, endState));

                while (lexer.token().type == Token.OR)
                {
                    lexer.nextToken();
                    alts.Add(pSeq(lexer, endState));
                }

                if (alts.Count == 1)
                {
                    return alts[0];
                }
                // combine transitions with identical destinations and negation

                RegularState result = new RegularState();
                Dictionary<RegularState, List<RegularTransition>> destMap = new Dictionary<RegularState, List<RegularTransition>>();
                foreach (RegularState state in alts)
                {
                    if (hasSelfReference(state))
                    {
                        // add lambda to self referencing states
                        new RegularTransition(result, state);
                        continue;
                    }
                    foreach (RegularTransition rt in state.getOutTransitions())
                    {
                        List<RegularTransition> dst;
                        if (!destMap.TryGetValue(rt.getEndState(), out dst))
                        {
                            dst = new List<RegularTransition>();
                            destMap.Add(rt.getEndState(), dst);
                        }
                        dst.Add(rt);
                    }
                }
                foreach (RegularState key in destMap.Keys)
                {
                    List<RegularTransition> value = destMap[key];
                    RegularTransition regrt = null;
                    RegularTransition neqrt = null;
                    RegularTransition lambda = null;
                    foreach (RegularTransition rt in value)
                    {
                        if (rt.isEmpty())
                        {
                            // don't combine lambda transitions with others
                            if (lambda == null)
                            {
                                lambda = new RegularTransition(result, key);
                            }
                        }
                        else if (rt.isNegation())
                        {
                            if (neqrt == null)
                            {
                                neqrt = new RegularTransition(result, key);
                                neqrt.setNegation(true);
                            }
                            neqrt.addLabels(rt.getLabels());
                        }
                        else
                        {
                            if (regrt == null)
                            {
                                regrt = new RegularTransition(result, key);
                            }
                            regrt.addLabels(rt.getLabels());
                        }
                    }
                }
                return result;
            }

            /// <summary>
            /// Return true when the provided state has a path to itself
            /// </summary>
            /// <param name="self"></param>
            /// <returns></returns>
            private static bool hasSelfReference(RegularState self)
            {
                Stack<RegularState> states = new Stack<RegularState>();
                states.Push(self);
                Set<RegularState> visited = new Set<RegularState>();
                while (states.Count > 0)
                {
                    RegularState state = states.Pop();
                    visited.Add(state);
                    foreach (RegularTransition rt in state.getOutTransitions())
                    {
                        if (rt.getEndState().Equals(self))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }

            /// <summary>
            /// Process sequences of words and subexpressions
            /// </summary>
            /// <param name="lexer"></param>
            /// <param name="endState"></param>
            /// <returns></returns>
            private static RegularState pSeq(Lexer lexer, RegularState endState)
            {
                RegularState result = null;
                RegularState lhs = null;
                while (lexer.token().type == Token.WORD || lexer.token().type == Token.NOT
                        || lexer.token().type == Token.PLEFT || lexer.token().type == Token.DOT)
                {
                    RegularState rhs;
                    if (lexer.token().type == Token.WORD)
                    {
                        rhs = pWord(lexer, endState);
                    }
                    else if (lexer.token().type == Token.DOT)
                    {
                        lexer.nextToken();
                        rhs = new RegularState();
                        RegularTransition transition = new RegularTransition(rhs, endState);
                        transition.addLabel(RegularTransition.WILDCARD);
                        multTransform(rhs, lexer, endState);
                    }
                    else if (lexer.token().type == Token.NOT)
                    {
                        // ![word1,word2,word3,word4,...]
                        rhs = new RegularState();
                        RegularTransition transition = new RegularTransition(rhs, endState);
                        transition.setNegation(true);
                        lexer.nextToken();
                        while (lexer.token().type == Token.WORD)
                        {
                            transition.addLabel(lexer.token().ToString());
                            lexer.nextToken();
                            if (lexer.token().type == Token.COMMA)
                            {
                                lexer.nextToken();
                            }
                        }
                        if (lexer.token().type != Token.SRIGHT)
                        {
                            throw new PatternParseException(String.Format("Missing right square bracket at #%d", lexer
                                    .charPos()));
                        }
                        lexer.nextToken();
                        multTransform(rhs, lexer, endState);
                    }
                    else
                    {
                        rhs = pSubexp(lexer, endState);
                    }
                    if (result == null)
                    {
                        // first item in the sequence is the return value;
                        result = rhs;
                    }
                    if (lhs != null)
                    {
                        // link end state of previous item in the list to current
                        // item
                        replaceStates(lhs, endState, rhs);
                    }
                    lhs = rhs;
                }
                if (lexer.token().type == Token.EOF && result == null)
                {
                    // empty regex
                    return endState;
                }
                if (result == null)
                {
                    throw new PatternParseException(String.Format("Unexpected token '%s' at #%d", lexer.token().text, lexer
                            .charPos()));
                }
                return result;
            }

            /// <summary>
            ///  Process a word.
            /// </summary>
            /// <param name="lexer"></param>
            /// <param name="endState"></param>
            /// <returns></returns>
            private static RegularState pWord(Lexer lexer, RegularState endState)
            {
                Token t = lexer.token();
                lexer.nextToken();
                RegularState result = new RegularState();
                RegularTransition transition = new RegularTransition(result, endState);
                transition.addLabel(t.ToString());
                return result;
            }

            /// <summary>
            /// Process a subexpression 
            /// </summary>
            /// <param name="lexer"></param>
            /// <param name="endState"></param>
            /// <returns></returns>
            private static RegularState pSubexp(Lexer lexer, RegularState endState)
            {
                lexer.nextToken();
                RegularState result = pAlt(lexer, endState);
                if (lexer.token().type != Token.PRIGHT)
                {
                    throw new PatternParseException(String.Format("Missing right paranthesis at #%d", lexer.charPos()));
                }
                lexer.nextToken();

                multTransform(result, lexer, endState);
                return result;
            }

            /// <summary>
            /// Process multiplication (?,*,+) of subexpressions.
            /// </summary>
            /// <param name="expr"></param>
            /// <param name="lexer"></param>
            /// <param name="endState"></param>
            private static void multTransform(RegularState expr, Lexer lexer, RegularState endState)
            {
                Token t = lexer.token();
                if (t.type == Token.STAR)
                {
                    lexer.nextToken();
                    // replace the end states with self
                    replaceStates(expr, endState, expr);
                    // lambda makes it optional
                    new RegularTransition(expr, endState);
                }
                else if (t.type == Token.OPT)
                {
                    lexer.nextToken();
                    // simply add lambda
                    new RegularTransition(expr, endState);
                }
                else if (t.type == Token.PLUS)
                {
                    lexer.nextToken();
                    // replace the end states with this state
                    RegularState newEnd = new RegularState();
                    replaceStates(expr, endState, newEnd);
                    // lambda to begin of expression
                    new RegularTransition(newEnd, expr);
                    // or to end state (had 1 iteration)
                    new RegularTransition(newEnd, endState);
                }
            }

            /// <summary>
            /// Replace end states in all transitions starting from base
            /// </summary>
            /// <param name="instat"></param>
            /// <param name="from"></param>
            /// <param name="to"></param>
            private static void replaceStates(RegularState instat, RegularState from, RegularState to)
            {
                Stack<RegularState> states = new Stack<RegularState>();
                states.Push(instat);
                Set<RegularState> visited = new Set<RegularState>();
                visited.Add(to);
                while (states.Count > 0)
                {
                    RegularState state = states.Pop();
                    visited.Add(state);
                    foreach (RegularTransition rt in state.getOutTransitions())
                    {

                        if (rt.getEndState().Equals(from))
                        {
                            rt.setEndState(to);
                            continue;
                        }

                        RegularState st = rt.getEndState();
                        if (!visited.Contains(st))
                        {
                            states.Push(st);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Used for the final/end state
        /// </summary>
        private sealed class FinalRegularState : RegularState
        {
            /// <summary>
            /// Not allowed
            /// </summary>
            /// <param name="transition"></param>
            public override void addOutTransition(RegularTransition transition)
            {
                throw new InvalidOperationException("FinalRegularState can not contains transition");
            }

            /// <summary>
            /// Not allowed
            /// </summary>
            /// <param name="transition"></param>
            public override void removeOutTransition(RegularTransition transition)
            {
                throw new InvalidOperationException("FinalRegularState can not contains transition");
            }
        }

        /// <summary>
        /// Parses the input string and returns tokens. Character sequences are automatically converted to a single token.
        /// </summary>
        private sealed class Lexer
        {
            private String buffer;

            private IList<Token> tokenBuffer;

            private int tokenPos;

            private int chrPos;

            /// <summary>
            /// Create a lexer for the regular pattern
            /// </summary>
            /// <param name="pattern"></param>
            public Lexer(string pattern)
            {
                tokenBuffer = new List<Token>();
                buffer = Regex.Replace(pattern, "\\s", "");
                bufferToken();
            }

            /// <summary>
            /// Get the next token
            /// </summary>
            /// <returns></returns>
            public Token nextToken()
            {
                if (tokenBuffer.Count <= tokenPos + 1)
                {
                    bufferToken();
                }
                return tokenBuffer[++tokenPos];
            }

            /// <summary>
            /// Return the current character position (this is after the current token).
            /// </summary>
            /// <returns></returns>
            public int charPos()
            {
                return chrPos;
            }

            /// <summary>
            /// Return the current token
            /// </summary>
            /// <returns></returns>
            public Token token()
            {
                return tokenBuffer[tokenPos];
            }

            /// <summary>
            /// Look ahead i tokens from the current location
            /// </summary>
            /// <param name="i"></param>
            /// <returns></returns>
            public Token ll(int i)
            {
                i += tokenPos;
                while (tokenBuffer.Count < tokenPos)
                {
                    bufferToken();
                }
                if (i < 0)
                {
                    throw new ArgumentException(String.Format("LL(%d) results in a negative token index", i
                            - tokenPos));
                }
                return tokenBuffer[i];
            }

            private void bufferToken()
            {
                if (chrPos >= buffer.Length)
                {
                    tokenBuffer.Add(EOF_TOKEN);
                    return;
                }
                char c = buffer[chrPos++];
                if (c == Token.EXCL)
                {
                    if (buffer.Length >= chrPos + 1 && buffer[chrPos] == Token.SLEFT) // 
                    {
                        // ![
                        chrPos++;
                        tokenBuffer.Add(NOT_TOKEN);
                    }
                    else
                    {
                        tokenBuffer.Add(mktok(c));
                    }
                }
                else if (Char.IsLetterOrDigit(c))
                {
                    int start = chrPos - 1;
                    while ((chrPos < buffer.Length) && Char.IsLetterOrDigit(buffer[chrPos]))
                    {
                        chrPos++;
                    }
                    tokenBuffer.Add(new Token(Token.WORD, buffer.Substring(start, chrPos-start)));
                }
                else
                {
                    tokenBuffer.Add(mktok(c));
                }
            }

            // flyweight
            private static Dictionary<Char, Token> toks = new Dictionary<Char, Token>();

            private static Token EOF_TOKEN = new Token(Token.EOF, "<EOF>");

            private static Token NOT_TOKEN = new Token(Token.NOT, "![");

            private static Token mktok(char c)
            {
                Token r;
                if (toks.ContainsKey(c))
                {
                    r = toks[c];
                }
                else
                {
                    r = new Token(c, Char.ToString(c));
                    toks.Add(c, r);
                }
                return r;
            }
        }

        /**
         * Tokens used by this expression
         * 
         * @author Michiel Hendriks
         */
        private sealed class Token
        {
            public static int EOF = -1;

            public static int WORD = -2;

            public static int NOT = -3; // ![ ... , ... ]

            public static int OR = 0x7C; // |

            public static int STAR = 0x2A; // *

            public static int PLUS = 0x2B; // +

            public static int OPT = 0x3F; // ?

            public static int PLEFT = 0x28; // (

            public static int PRIGHT = 0x29; // )

            public static int CLEFT = 0x7B; // {

            public static int CRIGHT = 0x7D; // }

            public static int SLEFT = 0x5B; // [

            public static int SRIGHT = 0x5D; // ]

            public static int COMMA = 0x2C; // ,

            public static int DOT = 0x2E; // .

            public static int EXCL = 0x21; // !

            /// <summary>
            /// The token type
            /// </summary>
            public int type;

            /// <summary>
            /// The text value of the token
            /// </summary>
            public string text;

            /// <summary>
            /// Create a new Token
            /// </summary>
            /// <param name="tokenType"></param>
            /// <param name="tokenText"></param>
            public Token(int tokenType, string tokenText)
            {
                type = tokenType;
                text = tokenText;
            }

            /// <summary>
            /// Return the next value of this token
            /// </summary>
            /// <returns></returns>
            public override string ToString()
            {
                return text;
            }
        }
    }
}
