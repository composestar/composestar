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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

/**
 * THIS IS NOT A REGULAR EXPRESSION (e.g. REGEX). But a more basic regular
 * language. The EBNF below explains the syntax. By default the pattern matches
 * a substring of the input. To make the pattern match with the beginning of a
 * string prefix it with a ^, to match it with the end of a string match it with
 * a $. Not adding using a ^ or $ to the pattern will actually add a ".*" to the
 * beginning and the end of a string.
 * 
 * <pre>
 * pattern		= alt;
 * alt			= seq {'|' seq};
 * seq			= elm { elm };
 * elm			= word | '.' [mult] | '(' pattern ')' [mult] | not [mult];
 * word			= (* character and digit sequence *)
 * mult			= '?' | '*' | '+';
 * not			= '![' word { ',' word } ']';
 * </pre>
 * 
 * @author Michiel Hendriks
 */
public final class RegularPattern extends Pattern
{
	private static final long serialVersionUID = -3870390515243330458L;

	/**
	 * The automaton that represents this pattern
	 */
	private RegularAutomaton automaton;

	/**
	 * Compile the pattern and return an instance of the RegularPattern
	 * 
	 * @param pattern
	 * @return
	 * @throws PatternParseException
	 */
	public static RegularPattern compile(String pattern) throws PatternParseException
	{
		return new RegularPattern(pattern);
	}

	private RegularPattern(String pattern) throws PatternParseException
	{
		super(pattern);
		automaton = Parser.parse(pattern);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.FIRE2.util.regex.IPattern#getStartState()
	 */
	@Override
	public RegularState getStartState()
	{
		return automaton.getStartState();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.FIRE2.util.regex.IPattern#getEndState()
	 */
	@Override
	public RegularState getEndState()
	{
		return automaton.getEndState();
	}

	/**
	 * The parser of the regular language
	 */
	private static final class Parser
	{
		private Parser()
		{}

		/**
		 * Parses the language and returns a regular automaton
		 * 
		 * @param pattern
		 * @return
		 * @throws PatternParseException
		 */
		public static RegularAutomaton parse(String pattern) throws PatternParseException
		{
			if (pattern == null)
			{
				throw new PatternParseException("Pattern can not be null");
			}

			if (!pattern.startsWith("^"))
			{
				pattern = ".*" + pattern;
			}
			else
			{
				pattern = pattern.substring(1);
			}
			if (!pattern.endsWith("$"))
			{
				pattern = pattern + ".*";
			}
			else
			{
				if (pattern.length() > 0)
				{
					pattern = pattern.substring(0, pattern.length() - 1);
				}
			}

			Lexer lexer = new Lexer(pattern);
			RegularState end = new FinalRegularState();
			RegularState start = pAlt(lexer, end);
			start.resolveGreedyEnd(new HashSet<RegularState>(), end);
			RegularAutomaton auto = new RegularAutomaton();
			auto.setStartState(start);
			auto.setEndState(end);
			if (lexer.token().type != Token.EOF)
			{
				throw new PatternParseException(String.format("Garbage token '%s' at #%d", lexer.token().text, lexer
						.charPos()));
			}
			return auto;
		}

		/**
		 * Process alternatives. This will optimize the state machine where
		 * possible. Edges with identical destinations will be merged, but only
		 * when they are not a lambda transition or contain a self reference.
		 * 
		 * @param lexer
		 * @param endState
		 * @return
		 * @throws PatternParseException
		 */
		private static RegularState pAlt(Lexer lexer, RegularState endState) throws PatternParseException
		{
			List<RegularState> alts = new ArrayList<RegularState>();
			alts.add(pSeq(lexer, endState));

			while (lexer.token().type == Token.OR)
			{
				lexer.nextToken();
				alts.add(pSeq(lexer, endState));
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
				if (hasSelfReference(state))
				{
					// add lambda to self referencing states
					new RegularTransition(result, state);
					continue;
				}
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
				RegularTransition lambda = null;
				for (RegularTransition rt : entry.getValue())
				{
					if (rt.isEmpty())
					{
						// don't combine lambda transitions with others
						if (lambda == null)
						{
							lambda = new RegularTransition(result, entry.getKey());
						}
					}
					else if (rt.isNegation())
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

		/**
		 * Return true when the provided state has a path to itself
		 * 
		 * @param self
		 * @return
		 */
		private static boolean hasSelfReference(RegularState self)
		{
			Stack<RegularState> states = new Stack<RegularState>();
			states.push(self);
			Set<RegularState> visited = new HashSet<RegularState>();
			while (states.size() > 0)
			{
				RegularState state = states.pop();
				visited.add(state);
				for (RegularTransition rt : state.getOutTransitions())
				{
					if (rt.getEndState().equals(self))
					{
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Process sequences of words and subexpressions
		 * 
		 * @param lexer
		 * @param endState
		 * @return
		 * @throws PatternParseException
		 */
		private static RegularState pSeq(Lexer lexer, RegularState endState) throws PatternParseException
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
						transition.addLabel(lexer.token().toString());
						lexer.nextToken();
						if (lexer.token().type == Token.COMMA)
						{
							lexer.nextToken();
						}
					}
					if (lexer.token().type != Token.SRIGHT)
					{
						throw new PatternParseException(String.format("Missing right square bracket at #%d", lexer
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
				throw new PatternParseException(String.format("Unexpected token '%s' at #%d", lexer.token().text, lexer
						.charPos()));
			}
			return result;
		}

		/**
		 * Process a word.
		 * 
		 * @param lexer
		 * @param endState
		 * @return
		 * @throws PatternParseException
		 */
		private static RegularState pWord(Lexer lexer, RegularState endState) throws PatternParseException
		{
			Token t = lexer.token();
			lexer.nextToken();
			RegularState result = new RegularState();
			RegularTransition transition = new RegularTransition(result, endState);
			transition.addLabel(t.toString());
			return result;
		}

		/**
		 * Process a subexpression
		 * 
		 * @param lexer
		 * @param endState
		 * @return
		 * @throws PatternParseException
		 */
		private static RegularState pSubexp(Lexer lexer, RegularState endState) throws PatternParseException
		{
			lexer.nextToken();
			RegularState result = pAlt(lexer, endState);
			if (lexer.token().type != Token.PRIGHT)
			{
				throw new PatternParseException(String.format("Missing right paranthesis at #%d", lexer.charPos()));
			}
			lexer.nextToken();

			multTransform(result, lexer, endState);
			return result;
		}

		/**
		 * Process multiplication (?,*,+) of subexpressions.
		 * 
		 * @param expr
		 * @param lexer
		 * @throws PatternParseException
		 */
		private static void multTransform(RegularState expr, Lexer lexer, RegularState endState)
				throws PatternParseException
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

		/**
		 * Replace end states in all transitions starting from base
		 * 
		 * @param base
		 * @param from
		 * @param to
		 */
		private static void replaceStates(RegularState base, RegularState from, RegularState to)
		{
			Stack<RegularState> states = new Stack<RegularState>();
			states.push(base);
			Set<RegularState> visited = new HashSet<RegularState>();
			visited.add(to);
			while (states.size() > 0)
			{
				RegularState state = states.pop();
				visited.add(state);
				for (RegularTransition rt : state.getOutTransitions())
				{

					if (rt.getEndState().equals(from))
					{
						rt.setEndState(to);
						continue;
					}

					RegularState st = rt.getEndState();
					if (!visited.contains(st))
					{
						states.push(st);
					}
				}
			}
		}
	}

	/**
	 * Used for the final/end state
	 * 
	 * @author Michiel Hendriks
	 */
	private static final class FinalRegularState extends RegularState
	{
		private static final long serialVersionUID = -5450856685894425275L;

		/*
		 * (non-Javadoc)
		 * @seeComposestar.Core.FIRE2.util.regex.RegularState#addOutTransition(
		 * Composestar.Core.FIRE2.util.regex.RegularTransition)
		 */
		@Override
		public void addOutTransition(RegularTransition transition)
		{
			throw new IllegalArgumentException("FinalRegularState can not contains transition");
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.FIRE2.util.regex.RegularState#removeOutTransition
		 * (Composestar.Core.FIRE2.util.regex.RegularTransition)
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
	private static final class Lexer
	{
		/**
		 * The input buffer
		 */
		private StringBuffer buffer;

		/**
		 * Buffer with tokens
		 */
		private List<Token> tokenBuffer;

		/**
		 * The current position in the token buffer
		 */
		private int tokenPos;

		/**
		 * The current position in the input buffer
		 */
		private int chrPos;

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
			return chrPos;
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
		public Token ll(int i)
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

		/**
		 * Add a new token to the token buffer
		 */
		private void bufferToken()
		{
			if (chrPos >= buffer.length())
			{
				tokenBuffer.add(EOF_TOKEN);
				return;
			}
			char c = buffer.charAt(chrPos++);
			if (c == Token.EXCL)
			{
				if (buffer.length() >= chrPos + 1 && buffer.charAt(chrPos) == Token.SLEFT) // 
				{
					// ![
					chrPos++;
					tokenBuffer.add(NOT_TOKEN);
				}
				else
				{
					tokenBuffer.add(mktok(c));
				}
			}
			else if (Character.isLetterOrDigit(c))
			{
				int start = chrPos - 1;
				while (chrPos < buffer.length() && Character.isLetterOrDigit(buffer.charAt(chrPos)))
				{
					chrPos++;
				}
				tokenBuffer.add(new Token(Token.WORD, buffer.substring(start, chrPos)));
			}
			else
			{
				tokenBuffer.add(mktok(c));
			}
		}

		// flyweight
		/**
		 * Cached tokens
		 */
		private static Map<Character, Token> toks = new HashMap<Character, Token>();

		/**
		 * Eond of file token
		 */
		private static final Token EOF_TOKEN = new Token(Token.EOF, "<EOF>");

		/**
		 * Special NOT token
		 */
		private static final Token NOT_TOKEN = new Token(Token.NOT, "![");

		/**
		 * Convert a character to a token
		 * 
		 * @param c
		 * @return
		 */
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
	private static final class Token
	{
		public static final int EOF = -1;

		public static final int WORD = -2;

		public static final int NOT = -3; // ![ ... , ... ]

		public static final int OR = 0x7C; // |

		public static final int STAR = 0x2A; // *

		public static final int PLUS = 0x2B; // +

		public static final int OPT = 0x3F; // ?

		public static final int PLEFT = 0x28; // (

		public static final int PRIGHT = 0x29; // )

		public static final int CLEFT = 0x7B; // {

		public static final int CRIGHT = 0x7D; // }

		public static final int SLEFT = 0x5B; // [

		public static final int SRIGHT = 0x5D; // ]

		public static final int COMMA = 0x2C; // ,

		public static final int DOT = 0x2E; // .

		public static final int EXCL = 0x21; // !

		/**
		 * The token type
		 */
		public int type;

		/**
		 * Text representation of the token
		 */
		public String text;

		public Token(int tokenType, String tokenText)
		{
			type = tokenType;
			text = tokenText;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return text;
		}
	}
}
