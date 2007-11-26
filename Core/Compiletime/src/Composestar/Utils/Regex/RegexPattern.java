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
 * $Id: RegexPattern.java 3936 2007-11-19 12:43:44Z elmuerte $
 */

package Composestar.Utils.Regex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

/**
 * A simple regular expression evaluator. Only a subset of the standard regular
 * expressions functionality is supported. Most important of all. This system
 * does not function on individual characters, only words are allowed. So, in
 * the expression all characters are combined into a single word. If you want to
 * match individual characters you should change them to a sub expression:
 * <code>(a)(b)(c)</code> and not <code>abc</code>. Because individual
 * characters are not supported you can not use character classes. Matching is
 * done using words instead of character streams, the input is read per word.
 * Whitespace is not supported.
 * <p>
 * The following features are supported, see
 * <href="http://www.regular-expressions.info/refflavors.html">regular-expressions.info</a>
 * for more information.
 * <ul>
 * <li>dot (but only in conjection with * and +)</li>
 * <li>Alternation</li>
 * <li>Quantifier: ? (only for subexpresions, not for dot)</li>
 * <li>Quantifier: *</li>
 * <li>Quantifier: +</li>
 * <li>Grouping: (regex)</li>
 * <li>Positive lookahead</li>
 * <li>Nagative lookahead</li>
 * </ul>
 * </p>
 * 
 * @author Michiel Hendriks
 */
public final class RegexPattern implements Serializable
{
	private static final long serialVersionUID = -3870390515243330458L;

	private Automaton automaton;

	private String pattern;

	/**
	 * Compile a string pattern;
	 * 
	 * @param pattern
	 * @return
	 * @throws PatternParseException
	 */
	public static RegexPattern compile(String pattern) throws PatternParseException
	{
		return new RegexPattern(pattern);
	}

	private RegexPattern(String pattern) throws PatternParseException
	{
		this.pattern = pattern;
		automaton = Parser.parse(pattern);
	}

	public boolean matches(MatchingBuffer buffer)
	{
		return automaton.matches(buffer);
	}

	@Override
	public String toString()
	{
		return pattern;
	}

	private static final class Parser
	{
		private Parser()
		{}

		public static Automaton parse(String pattern) throws PatternParseException
		{
			Lexer lexer = new Lexer(pattern);
			State end = new FinalState();
			State start;
			try
			{
				start = pAlt(lexer, end);
			}
			catch (PatternParseException e)
			{
				throw new PatternParseException("boo", "foo", -1);
				// throw new PatternParseException(e.getDescription(), pattern,
				// e.getIndex());
			}
			Automaton auto = new Automaton(start, end);
			if (lexer.token().type != Token.EOF)
			{
				throw new PatternParseException(String.format("Garbage token '%s'", lexer.token().text), pattern, lexer
						.charPos());
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
		private static State pAlt(Lexer lexer, State endState) throws PatternParseException
		{
			List<State> alts = new ArrayList<State>();
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
			// combine transitions with identical destinations

			State result = new State();
			Map<State, List<Transition>> destMap = new HashMap<State, List<Transition>>();
			for (State state : alts)
			{
				if (!state.getClass().equals(State.class))
				{
					// don't combine subclasses of State
					new Transition(result, state);
					continue;
				}
				else if (hasSelfReference(state))
				{
					// add lambda to self referencing states
					new Transition(result, state);
					continue;
				}
				for (Transition rt : state.getTransitions())
				{
					List<Transition> dst = destMap.get(rt.getEndState());
					if (dst == null)
					{
						dst = new ArrayList<Transition>();
						destMap.put(rt.getEndState(), dst);
					}
					dst.add(rt);
				}
			}
			for (Entry<State, List<Transition>> entry : destMap.entrySet())
			{
				Transition regrt = null;
				Transition lambda = null;
				for (Transition rt : entry.getValue())
				{
					if (rt.isLambda())
					{
						// don't combine lambda transitions with others
						if (lambda == null)
						{
							lambda = new Transition(result, entry.getKey());
						}
					}
					else
					{
						if (regrt == null)
						{
							regrt = new Transition(result, entry.getKey());
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
		private static boolean hasSelfReference(State self)
		{
			Stack<State> states = new Stack<State>();
			states.push(self);
			Set<State> visited = new HashSet<State>();
			while (states.size() > 0)
			{
				State state = states.pop();
				visited.add(state);
				for (Transition rt : state.getTransitions())
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
		private static State pSeq(Lexer lexer, State endState) throws PatternParseException
		{
			State result = null;
			State lhs = null;
			while (lexer.token().type == Token.WORD || lexer.token().type == Token.LOOKAHEAD
					|| lexer.token().type == Token.PLEFT || lexer.token().type == Token.DOT)
			{
				State rhs;
				if (lexer.token().type == Token.WORD)
				{
					rhs = pWord(lexer, endState);
				}
				else if (lexer.token().type == Token.DOT)
				{
					Token t = lexer.nextToken();
					if (t.type != Token.STAR && t.type != Token.PLUS)
					{
						throw new PatternParseException(
								"Dot may only be used in conjuction with the star or plus multiplier (.* or .+)", "",
								lexer.charPos);
					}
					lexer.nextToken();
					rhs = new State();
					State self = rhs;
					Transition transition;
					if (t.type == Token.PLUS)
					{
						// state for the first word
						self = new State();
						transition = new Transition(rhs, self);
						transition.addLabel(Transition.WILDCARD);
					}
					// lambda
					new Transition(self, endState);
					// wildcard to self
					transition = new Transition(self, self);
					transition.addLabel(Transition.WILDCARD);
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
				throw new PatternParseException(String.format("Unexpected token '%s'", lexer.token().text), "", lexer
						.charPos());
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
		private static State pWord(Lexer lexer, State endState) throws PatternParseException
		{
			Token t = lexer.token();
			lexer.nextToken();
			State result = new State();
			Transition transition = new Transition(result, endState);
			transition.addLabel(t.toString());
			return result;
		}

		/**
		 * Process a subexpression or negation (which implies a subexpr).
		 * 
		 * @param lexer
		 * @param endState
		 * @return
		 * @throws PatternParseException
		 */
		private static State pSubexp(Lexer lexer, State endState) throws PatternParseException
		{
			Token t = lexer.token();
			lexer.nextToken();
			State result;

			if (t.type == Token.LOOKAHEAD)
			{
				result = new LookaheadState();
				if (lexer.token().type == Token.EQUALS)
				{
					((LookaheadState) result).setNegation(false);
				}
				else if (lexer.token().type == Token.NOT)
				{
					((LookaheadState) result).setNegation(true);
				}
				else
				{
					throw new PatternParseException("Invalid lookahead type", "", lexer.charPos());
				}
				lexer.nextToken(); // eat the ! or =

				State end = new FinalState();
				State begin = pAlt(lexer, end);
				((LookaheadState) result).setAutomaton(new Automaton(begin, end), endState);
			}
			else
			{
				// consuming subexpression
				result = pAlt(lexer, endState);
				if (result == endState)
				{
					throw new PatternParseException("Empty subexpression", "", lexer.charPos());
				}
			}

			if (lexer.token().type != Token.PRIGHT)
			{
				throw new PatternParseException("Missing right paranthesis", "", lexer.charPos());
			}
			lexer.nextToken();

			if (t.type != Token.LOOKAHEAD)
			{
				multTransform(result, lexer, endState);
			}
			return result;
		}

		/**
		 * Process multiplication (?,*,+) of subexpressions.
		 * 
		 * @param expr
		 * @param lexer
		 * @throws PatternParseException
		 */
		private static void multTransform(State expr, Lexer lexer, State endState) throws PatternParseException
		{
			// TODO: breaks for subexpressions
			Token t = lexer.token();
			if (t.type == Token.STAR)
			{
				if (lexer.ll(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException("Multiplication only supported for subexpressions", "", lexer
							.charPos());
				}
				lexer.nextToken();
				// replace the end states with self
				replaceStates(expr, endState, expr);
				// lambda makes it optional
				new Transition(expr, endState);
			}
			else if (t.type == Token.OPT)
			{
				if (lexer.ll(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException("Multiplication only supported for subexpressions", "", lexer
							.charPos());
				}
				lexer.nextToken();
				// simply add lambda
				new Transition(expr, endState);
			}
			else if (t.type == Token.PLUS)
			{
				if (lexer.ll(-1).type != Token.PRIGHT)
				{
					throw new PatternParseException("Multiplication only supported for subexpressions", "", lexer
							.charPos());
				}
				lexer.nextToken();
				// replace the end states with this state
				State newEnd = new State();
				replaceStates(expr, endState, newEnd);
				// lambda to begin of expression
				new Transition(newEnd, expr);
				// or to end state (had 1 iteration)
				new Transition(newEnd, endState);
			}
		}

		/**
		 * Replace end states in all transitions starting from base
		 * 
		 * @param base
		 * @param from
		 * @param to
		 */
		private static void replaceStates(State base, State from, State to)
		{
			Stack<State> states = new Stack<State>();
			states.push(base);
			Set<State> visited = new HashSet<State>();
			visited.add(to); // don't update self
			while (states.size() > 0)
			{
				State state = states.pop();
				visited.add(state);
				for (Transition rt : state.getTransitions())
				{

					if (rt.getEndState().equals(from))
					{
						rt.setEndState(to);
						continue;
					}

					State st = rt.getEndState();
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
	private static final class FinalState extends State
	{
		private static final long serialVersionUID = 4634154080044477451L;

		@Override
		public String getName()
		{
			return "(F)" + label;
		}

		@Override
		public void addTransition(Transition transition)
		{
			throw new IllegalArgumentException("FinalState can not contains transition");
		}

		@Override
		public void removeTransition(Transition transition)
		{
			throw new IllegalArgumentException("FinalState can not contains transition");
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
				if (buffer.length() >= charPos + 1 && buffer.charAt(charPos) == Token.OPT)
				{
					charPos++;
					if (buffer.length() >= charPos + 1 && buffer.charAt(charPos) == 0x3C) // <
					{
						charPos++;
						tokenBuffer.add(LOOKBEHIND_TOKEN);
					}
					else
					{
						tokenBuffer.add(LOOKAHEAD_TOKEN);
					}
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

		private static final Token EOF_TOKEN = new Token(Token.EOF, "<EOF>");

		private static final Token LOOKAHEAD_TOKEN = new Token(Token.LOOKAHEAD, "(?");

		private static final Token LOOKBEHIND_TOKEN = new Token(Token.LOOKBEHIND, "(?<");

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

		public static final int EQUALS = 0x3D; // =

		public static final int NOT = 0x21; // !

		public static final int LOOKAHEAD = -3; // (?

		public static final int LOOKBEHIND = -4; // (?<

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
