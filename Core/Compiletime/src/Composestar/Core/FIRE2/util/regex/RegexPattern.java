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
import java.util.Map.Entry;

/**
 * A basic regular expression pattern. Only a small subset of regex is
 * supported. And not in the usual flexibility. There are no character matches,
 * just words. All whitespace is ignored. Multiplicity is only allowed for
 * subexpressions and not for characters. For example <code>ab*c</code> is not
 * allowed, use <code>(a)(b)*(c)</code>. Matching always happens on the whole
 * sentence (i.e. pattern => ^pattern$ ). The dot wildcard may only be used in
 * conjuction with + or * multipliers. ENBF grammar of the accepted expressions:
 * 
 * <pre>
 * alt		= seq { '|' seq };
 * seq		= (word | subExpr | not) { (word | subExpr | not) };
 * word		= letterOrDigit {letterOrDigit};
 * not		= '(?!' alt ')';
 * subExpr	= '(' alt ')' [ '?' | '*' | '+' ];
 * </pre>
 * 
 * @author Michiel Hendriks
 */
public class RegexPattern extends Pattern
{
	private RegularAutomaton automaton;

	public static RegexPattern compile(String pattern) throws PatternParseException
	{
		return new RegexPattern(pattern);
	}

	private RegexPattern(String pattern) throws PatternParseException
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
		protected static RegularState END_STATE;

		public static RegularAutomaton parse(String pattern) throws PatternParseException
		{
			Lexer lexer = new Lexer(pattern);
			END_STATE = new FinalRegularState();
			RegularState start = pAlt(lexer);
			RegularAutomaton auto = new RegularAutomaton();
			auto.setStartState(start);
			auto.setEndState(END_STATE);
			if (lexer.token().type != Token.EOF)
			{
				throw new PatternParseException(String.format("Garbage token '%s' at #%d", lexer.token().text, lexer
						.charPos()));
			}
			return auto;
		}

		private static RegularState pAlt(Lexer lexer) throws PatternParseException
		{
			List<RegularState> alts = new ArrayList<RegularState>();
			alts.add(pSeq(lexer));

			while (lexer.token().type == Token.OR)
			{
				lexer.nextToken();
				alts.add(pSeq(lexer));
			}

			if (alts.size() == 1)
			{
				return alts.get(0);
			}
			// combine transitions with identical destinations and negation

			RegularState result = new RegularState();
			Map<RegularState, List<RegularTransition>> destMap = new HashMap<RegularState, List<RegularTransition>>();
			for (RegularState state : alts)
			{
				for (RegularTransition rt : state.getOutTransitions())
				{
					List<RegularTransition> dst = destMap.get(rt.getEndState());
					if (dst == null)
					{
						dst = new ArrayList<RegularTransition>();
						destMap.put(rt.getEndState(), dst);
					}
					dst.add(rt);
				}
			}
			for (Entry<RegularState, List<RegularTransition>> entry : destMap.entrySet())
			{
				RegularTransition regrt = null;
				RegularTransition neqrt = null;
				for (RegularTransition rt : entry.getValue())
				{
					if (rt.isNegation())
					{
						if (neqrt == null)
						{
							neqrt = new RegularTransition(result, entry.getKey());
							neqrt.setNegation(true);
						}
						neqrt.addLabels(rt.getLabels());
					}
					else
					{
						if (regrt == null)
						{
							regrt = new RegularTransition(result, entry.getKey());
						}
						regrt.addLabels(rt.getLabels());
					}
				}
			}
			return result;
		}

		private static RegularState pSeq(Lexer lexer) throws PatternParseException
		{
			RegularState result = null;
			RegularState lhs = null;
			while (lexer.token().type == Token.WORD || lexer.token().type == Token.NEQ
					|| lexer.token().type == Token.PLEFT || lexer.token().type == Token.DOT)
			{
				RegularState rhs;
				if (lexer.token().type == Token.WORD)
				{
					rhs = pWord(lexer);
				}
				else if (lexer.token().type == Token.DOT)
				{
					Token t = lexer.nextToken();
					if (t.type != Token.STAR && t.type != Token.PLUS)
					{
						throw new PatternParseException(String.format(
								"Dot may only be used in conjuction with the star or plus multiplier (.* or .+)",
								lexer.charPos));
					}
					lexer.nextToken();
					result = new RegularState();
					rhs = result;
					RegularTransition transition;
					if (t.type == Token.PLUS)
					{
						// state for the first word
						rhs = new RegularState();
						transition = new RegularTransition(result, rhs);
						transition.addLabel(RegularTransition.WILDCARD);
					}
					// lambda
					transition = new RegularTransition(rhs, END_STATE);
					// wildcard to self
					transition = new RegularTransition(rhs, rhs);
					transition.addLabel(RegularTransition.WILDCARD);
				}
				else
				{
					rhs = pSubexp(lexer);
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
					for (RegularTransition rt : lhs.getOutTransitions())
					{
						if (rt.getEndState().equals(END_STATE))
						{
							rt.setEndState(rhs);
						}
					}
				}
				lhs = rhs;
			}
			if (lexer.token().type == Token.EOF && result == null)
			{
				// empty regex
				return END_STATE;
			}
			if (result == null)
			{
				throw new PatternParseException(String.format("Unexpected token '%s' at #%d", lexer.token().text, lexer
						.charPos()));
			}
			return result;
		}

		private static RegularState pWord(Lexer lexer) throws PatternParseException
		{
			Token t = lexer.token();
			lexer.nextToken();
			RegularState result = new RegularState();
			RegularTransition transition = new RegularTransition(result, END_STATE);
			transition.addLabel(t.toString());
			return result;
		}

		private static RegularState pSubexp(Lexer lexer) throws PatternParseException
		{
			Token t = lexer.token();
			lexer.nextToken();
			RegularState result = pAlt(lexer);

			if (t.type == Token.NEQ)
			{
				notTransform(result);
			}

			if (lexer.token().type != Token.PRIGHT)
			{
				throw new PatternParseException(String.format("Missing right paranthesis at #%d", lexer.charPos()));
			}
			lexer.nextToken();

			if (t.type != Token.NEQ)
			{
				multTransform(result, lexer);
			}
			return result;
		}

		/**
		 * Process multiplication
		 * 
		 * @param expr
		 * @param lexer
		 * @throws PatternParseException
		 */
		private static void multTransform(RegularState expr, Lexer lexer) throws PatternParseException
		{
			Token t = lexer.token();
			if (t.type == Token.STAR)
			{
				if (lexer.LL(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException(String.format("Multiplication only supported for subexpressions",
							lexer.charPos()));
				}
				lexer.nextToken();
				// replace the end states with self
				replaceStates(expr, END_STATE, expr, true);
				// lambda makes it optional
				new RegularTransition(expr, END_STATE);
			}
			else if (t.type == Token.OPT)
			{
				if (lexer.LL(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException(String.format("Multiplication only supported for subexpressions",
							lexer.charPos()));
				}
				lexer.nextToken();
				// simply add lambda
				new RegularTransition(expr, END_STATE);
			}
			else if (t.type == Token.PLUS)
			{
				if (lexer.LL(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException(String.format("Multiplication only supported for subexpressions",
							lexer.charPos()));
				}
				lexer.nextToken();
				// replace the end states with this state
				RegularState newEnd = new RegularState();
				replaceStates(expr, END_STATE, newEnd, true);
				// lambda to begin of expression
				new RegularTransition(newEnd, expr);
				// or to end state (had 1 iteration)
				new RegularTransition(newEnd, END_STATE);
			}
		}

		/**
		 * @param base
		 * @param from
		 * @param to
		 * @param endState
		 */
		private static void replaceStates(RegularState base, RegularState from, RegularState to, boolean endState)
		{
			for (RegularTransition rt : base.getOutTransitions())
			{
				if (endState)
				{
					if (rt.getEndState().equals(from))
					{
						rt.setEndState(to);
					}
					else
					{
						replaceStates(rt.getEndState(), from, to, endState);
					}
				}
				else
				{
					if (rt.getStartState().equals(from))
					{
						rt.setStartState(to);
					}
					else
					{
						replaceStates(rt.getEndState(), from, to, endState);
					}
				}
			}
		}

		// FIXME: this doesn't negate sequence, just the first node
		// Expected: not(x y z)
		// Currently: not(x) y z
		private static void notTransform(RegularState base)
		{
			for (RegularTransition rt : base.getOutTransitions())
			{
				// dirty invert negation (only works for
				rt.setNegation(!rt.isNegation());
			}
			/*
			 * To properly support negation of a sequence the sequence needs to
			 * be copied and each segment needs to get a negation edge to the
			 * actual end state. The above dirty hack only works for
			 * alternatives, not sequences.
			 */
		}
	}

	/**
	 * Used for the final/end state
	 * 
	 * @author Michiel Hendriks
	 */
	private static class FinalRegularState extends RegularState
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.FIRE2.util.regex.RegularState#addOutTransition(Composestar.Core.FIRE2.util.regex.RegularTransition)
		 */
		@Override
		public void addOutTransition(RegularTransition transition)
		{
			throw new IllegalArgumentException("FinalRegularState can not contains transition");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Composestar.Core.FIRE2.util.regex.RegularState#removeOutTransition(Composestar.Core.FIRE2.util.regex.RegularTransition)
		 */
		@Override
		public void removeOutTransition(RegularTransition transition)
		{
			throw new IllegalArgumentException("FinalRegularState can not contains transition");
		}
	}

	/**
	 * Parses the input string and returns tokens. Character sequences are
	 * automatically converted to a single token.
	 * 
	 * @author Michiel Hendriks
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
			if (i < 0)
			{
				throw new IllegalArgumentException(String.format("LL(%d) results in a negative token index", i
						- tokenPos));
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
				if (buffer.length() >= charPos + 2 && buffer.charAt(charPos) == Token.OPT
						&& buffer.charAt(charPos + 1) == 0x21) // !
				{
					charPos += 2;
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

	/**
	 * Tokens used by this expression
	 * 
	 * @author Michiel Hendriks
	 */
	private static class Token
	{
		public static final int EOF = -1;

		public static final int WORD = -2;

		public static final int OR = 0x7C; // |

		public static final int STAR = 0x2A; // *

		public static final int PLUS = 0x2B; // *

		public static final int OPT = 0x3F; // ?

		public static final int PLEFT = 0x28; // (

		public static final int PRIGHT = 0x29; // )

		public static final int CLEFT = 0x7B8; // {

		public static final int CRIGHT = 0x7D; // }

		public static final int COMMA = 0x2C; // )

		public static final int DOT = 0x2E; // .

		public static final int NEQ = -3; // (?!

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
