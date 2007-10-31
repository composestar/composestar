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
 * $Id$
 */

package Composestar.Core.FIRE2.util.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic regualr expression pattern. Only a small subset of regex is
 * supported.
 * 
 * <pre>
 * alt		=	mult { '|' mult };
 * mult		=	seq [ '?' | '*' ];
 * seq		=	word | rseq {rseq};
 * rseq		=	'(' ['?'] alt ')';
 * </pre>
 * 
 * @author Michiel Hendriks
 */
public class RegexPattern extends Pattern
{
	private RegularAutomaton automaton;

	public static RegexPattern compile(String pattern)
	{
		return new RegexPattern(pattern);
	}

	private RegexPattern(String pattern)
	{
		super(pattern);
		automaton = Parser.parse(pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.IPattern#getStartState()
	 */
	@Override
	public RegularState getStartState()
	{
		return automaton.getStartState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.IPattern#getEndState()
	 */
	@Override
	public RegularState getEndState()
	{
		return automaton.getEndState();
	}

	private static class Parser
	{
		public static RegularAutomaton parse(String pattern)
		{
			Lexer lexer = new Lexer(pattern);
			return pAlt(lexer);
		}

		private static List<RegularTransition> getCompactStates(RegularState st)
		{
			List<RegularTransition> res = new ArrayList<RegularTransition>();
			for (RegularTransition rt : st.getOutTransitions())
			{
				if (rt.isEmpty())
				{
					res.addAll(getCompactStates(rt.getEndState()));
				}
				else
				{
					res.add(rt);
				}
			}
			return res;
		}

		private static RegularAutomaton pAlt(Lexer lexer)
		{
			List<RegularAutomaton> alts = new ArrayList<RegularAutomaton>();
			alts.add(pMultp(lexer));

			while (lexer.token().type == Token.OR)
			{
				lexer.nextToken();
				alts.add(pMultp(lexer));
			}

			if (alts.size() == 1)
			{
				return alts.get(0);
			}

			RegularAutomaton result = new RegularAutomaton();
			RegularState startState = new RegularState();
			result.setStartState(startState);
			RegularState endState = new RegularState();
			result.setEndState(endState);

			for (RegularAutomaton alt : alts)
			{
				// TODO: combine similar transitions
				for (RegularTransition rt : getCompactStates(alt.getStartState()))
				{
					rt.setStartState(startState);
					rt.setEndState(endState);
				}
			}
			return result;
		}

		private static RegularAutomaton pMultp(Lexer lexer)
		{
			RegularAutomaton expr = pSeq(lexer);
			Token t = lexer.token();
			if (t.type == Token.STAR)
			{
				lexer.nextToken();
				// TODO:
			}
			else if (t.type == Token.OPT)
			{
				lexer.nextToken();
				// TODO:
			}
			return expr;
		}

		private static RegularAutomaton pSeq(Lexer lexer)
		{
			Token t = lexer.token();
			if (t.type == Token.WORD)
			{
				lexer.nextToken();

				RegularAutomaton result = new RegularAutomaton();
				RegularState startState = new RegularState();
				result.setStartState(startState);
				RegularState endState = new RegularState();
				result.setEndState(endState);

				RegularTransition transition = new RegularTransition(startState, endState);
				transition.addLabel(t.toString());
				return result;
			}
			else if (t.type == Token.NEQ || t.type == Token.PLEFT)
			{
				RegularAutomaton lhs = pRseq(lexer);
				RegularAutomaton result = lhs;
				while (lexer.token().type == Token.NEQ || lexer.token().type == Token.PLEFT)
				{
					RegularAutomaton rhs = pRseq(lexer);
					new RegularTransition(lhs.getEndState(), rhs.getStartState());
					lhs = rhs;
				}
				return result;
			}
			// throw exception
			return null;
		}

		private static RegularAutomaton pRseq(Lexer lexer)
		{
			Token t = lexer.token();
			lexer.nextToken();
			RegularAutomaton result = pAlt(lexer);

			if (t.type == Token.NEQ)
			{
				// FIXME: this doesn't negate sequence, just the first node
				// Expected: not(x y z)
				// Currently: not(x) y z
				for (RegularTransition rt : getCompactStates(result.getStartState()))
				{
					rt.setNegation(!rt.isNegation());
				}
			}

			if (lexer.token().type != Token.PRIGHT)
			{
				// throw exception
			}
			lexer.nextToken();
			return result;
		}
	}

	/**
	 * Parses the input string and returns tokens. Character sequences are
	 * automatically converted to a single token.
	 * 
	 * @author mhendrik
	 */
	private static class Lexer
	{
		private StringBuffer buffer;

		private List<Token> tokenBuffer;

		private int tokenPos;

		private int charPos;

		public Lexer(String pattern)
		{
			tokenBuffer = new ArrayList<Token>();
			buffer = new StringBuffer(pattern.replaceAll("\\s", ""));
			bufferToken();
		}

		/**
		 * Get the next token
		 * 
		 * @return
		 */
		public Token nextToken()
		{
			if (tokenBuffer.size() <= tokenPos + 1)
			{
				bufferToken();
			}
			return tokenBuffer.get(++tokenPos);
		}

		/**
		 * Return the current character position (this is after the current
		 * token).
		 * 
		 * @return
		 */
		public int charPos()
		{
			return charPos;
		}

		/**
		 * Return the current token
		 * 
		 * @return
		 */
		public Token token()
		{
			return tokenBuffer.get(tokenPos);
		}

		/**
		 * Look ahead i tokens from the current location
		 * 
		 * @param i
		 * @return
		 */
		public Token LL(int i)
		{
			i += tokenPos;
			while (tokenBuffer.size() < tokenPos)
			{
				bufferToken();
			}
			return tokenBuffer.get(i);
		}

		private void bufferToken()
		{
			if (charPos >= buffer.length())
			{
				tokenBuffer.add(EOF_TOKEN);
				return;
			}
			char c = buffer.charAt(charPos++);
			if (c == Token.PLEFT)
			{
				if (buffer.charAt(charPos) == Token.OPT)
				{
					charPos++;
					tokenBuffer.add(NEQ_TOKEN);
				}
				else
				{
					tokenBuffer.add(mktok(c));
				}
			}
			else if (Character.isLetterOrDigit(c))
			{
				int start = charPos - 1;
				while ((charPos < buffer.length()) && Character.isLetterOrDigit(buffer.charAt(charPos)))
				{
					charPos++;
				}
				tokenBuffer.add(new Token(Token.WORD, buffer.substring(start, charPos)));
			}
			else
			{
				tokenBuffer.add(mktok(c));
			}
		}

		// flyweight
		private static Map<Character, Token> toks = new HashMap<Character, Token>();

		private static Token EOF_TOKEN = new Token(Token.EOF, "<EOF>");

		private static Token NEQ_TOKEN = new Token(Token.NEQ, "(?");

		private static Token mktok(char c)
		{
			Token r;
			if (toks.containsKey(c))
			{
				r = toks.get(c);
			}
			else
			{
				r = new Token(c, Character.toString(c));
				toks.put(c, r);
			}
			return r;
		}
	}

	private static class Token
	{
		public static final int EOF = -1;

		public static final int WORD = -2;

		public static final int OR = 0x7C; // |

		public static final int STAR = 0x2A; // *

		public static final int OPT = 0x3F; // ?

		public static final int PLEFT = 0x28; // (

		public static final int PRIGHT = 0x29; // )

		public static final int NEQ = -3; // (?

		public int type;

		public String text;

		public Token(int tokenType, String tokenText)
		{
			type = tokenType;
			text = tokenText;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return text;
		}
	}
}
