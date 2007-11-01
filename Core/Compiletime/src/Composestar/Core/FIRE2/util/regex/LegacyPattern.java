/*
 * Created on 30-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arjan de Roo
 * @deprecated Switch to RegexPattern is more regular expression conform
 */
@Deprecated
public class LegacyPattern extends Pattern
{
	private RegularAutomaton automaton;

	private LegacyPattern(String pattern) throws PatternParseException
	{
		super(pattern);
		automaton = Parser.parse(pattern);
	}

	public static LegacyPattern compile(String pattern) throws PatternParseException
	{
		return new LegacyPattern(pattern);
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
		public static RegularAutomaton parse(String pattern) throws PatternParseException
		{
			Lexer lexer = new Lexer(pattern);
			return parseRegularExpression(lexer);
		}

		private static RegularAutomaton parseRegularExpression(Lexer lexer) throws PatternParseException
		{
			return parseUnionExpression(lexer);
		}

		private static RegularAutomaton parseUnionExpression(Lexer lexer) throws PatternParseException
		{
			RegularAutomaton machine1 = parseConcatExpression(lexer);

			Token token = lexer.peekNextToken();
			if (token.getType() == Token.UNION_OPERATOR)
			{
				lexer.nextToken();
				RegularAutomaton machine2 = parseUnionExpression(lexer);

				RegularAutomaton resultAutomaton = new RegularAutomaton();
				RegularState startState = new RegularState();
				resultAutomaton.setStartState(startState);
				RegularState endState = new RegularState();
				resultAutomaton.setEndState(endState);
				new RegularTransition(startState, machine1.getStartState());
				new RegularTransition(startState, machine2.getStartState());
				new RegularTransition(machine1.getEndState(), endState);
				new RegularTransition(machine2.getEndState(), endState);

				return resultAutomaton;
			}
			else
			{
				return machine1;
			}
		}

		private static RegularAutomaton parseConcatExpression(Lexer lexer) throws PatternParseException
		{
			RegularAutomaton machine1 = parseStarExpression(lexer);

			Token token = lexer.peekNextToken();
			switch (token.type)
			{
				case Token.LEFT_BRACKET:
				case Token.NEGATION_OPERATOR:
				case Token.WORD:
					RegularAutomaton machine2 = parseConcatExpression(lexer);
					RegularAutomaton resultAutomaton = new RegularAutomaton();
					resultAutomaton.setStartState(machine1.getStartState());
					resultAutomaton.setEndState(machine2.getEndState());
					RegularTransition transition = new RegularTransition(machine1.getEndState(), machine2
							.getStartState());
					return resultAutomaton;
				case Token.STAR_OPERATOR:
					throw new PatternParseException("Unexpected star-operator at" + " position " + token.getPosition()
							+ '.');
				default:
					return machine1;
			}
		}

		private static RegularAutomaton parseStarExpression(Lexer lexer) throws PatternParseException
		{
			RegularAutomaton machine1 = parseBasicExpression(lexer);

			Token token = lexer.peekNextToken();
			if (token.type == Token.STAR_OPERATOR)
			{
				lexer.nextToken();

				RegularAutomaton result = new RegularAutomaton();

				RegularState startState = new RegularState();
				result.setStartState(startState);

				RegularState endState = new RegularState();
				result.setEndState(endState);

				RegularTransition transition1 = new RegularTransition(startState, endState);
				RegularTransition transition2 = new RegularTransition(startState, machine1.getStartState());
				RegularTransition transition3 = new RegularTransition(machine1.getEndState(), endState);
				RegularTransition transition4 = new RegularTransition(machine1.getEndState(), machine1.getStartState());

				return result;
			}
			else
			{
				return machine1;
			}
		}

		private static RegularAutomaton parseBasicExpression(Lexer lexer) throws PatternParseException
		{
			Token token = lexer.peekNextToken();
			if (token.type == Token.WORD)
			{
				lexer.nextToken();

				RegularAutomaton result = new RegularAutomaton();

				RegularState startState = new RegularState();
				result.setStartState(startState);

				RegularState endState = new RegularState();
				result.setEndState(endState);

				// FIXME add label to transition
				RegularTransition transition1 = new RegularTransition(startState, endState);
				transition1.addLabel(token.getValue());

				return result;
			}
			else if (token.type == Token.LEFT_BRACKET)
			{
				lexer.nextToken();
				RegularAutomaton result = parseRegularExpression(lexer);
				token = lexer.nextToken();
				if (token.type != Token.RIGHT_BRACKET)
				{
					throw new PatternParseException("Unexpected token at" + " position " + token.getPosition()
							+ ". Expected right-bracket.");
				}

				return result;
			}
			else if (token.type == Token.NEGATION_OPERATOR)
			{
				lexer.nextToken();
				String[] words = parseWordSequence(lexer);

				RegularAutomaton result = new RegularAutomaton();

				RegularState startState = new RegularState();
				result.setStartState(startState);

				RegularState endState = new RegularState();
				result.setEndState(endState);

				RegularTransition transition1 = new RegularTransition(startState, endState);
				transition1.setNegation(true);
				for (String word : words)
				{
					transition1.addLabel(word);
				}

				return result;
			}
			else
			{
				throw new PatternParseException("Unexpected token at" + " position " + token.getPosition()
						+ ". Expected word, left-bracket or " + "negation-operator.");
			}
		}

		private static String[] parseWordSequence(Lexer lexer) throws PatternParseException
		{
			Token token = lexer.peekNextToken();
			if (token.type == Token.WORD)
			{
				lexer.nextToken();

				String[] result = { token.getValue() };
				return result;
			}
			else if (token.type == Token.LEFT_BRACKET)
			{
				lexer.nextToken();

				List<String> result = new ArrayList<String>();

				while (token.type != Token.RIGHT_BRACKET)
				{
					token = lexer.nextToken();
					if (token.type != Token.WORD)
					{
						throw new PatternParseException("Unexpected token at" + " position " + token.getPosition()
								+ ". Expected word.");
					}

					result.add(token.getValue());

					token = lexer.nextToken();
					if (token.type != Token.RIGHT_BRACKET && token.type != Token.UNION_OPERATOR)
					{
						throw new PatternParseException("Unexpected token at" + " position " + token.getPosition()
								+ ". Expected union-operator or right-bracket.");
					}
				}

				return result.toArray(new String[result.size()]);
			}
			else
			{
				throw new PatternParseException("Unexpected token at" + " position " + token.getPosition()
						+ ". Expected word or left-bracket.");
			}
		}
	}

	private static class Lexer
	{
		private String pattern;

		private int pos;

		private Token bufferedToken;

		public Lexer(String pattern)
		{
			this.pattern = pattern.replaceAll("\\s", "");
			pos = 0;
		}

		public boolean hasMoreTokens()
		{
			return bufferedToken != null || pos <= pattern.length();
		}

		public Token nextToken() throws PatternParseException
		{
			Token token;

			if (bufferedToken != null)
			{
				token = bufferedToken;
				bufferedToken = null;
				return token;
			}
			else
			{
				token = getNextToken();
				pos += token.getLength();
				return token;
			}
		}

		public Token peekNextToken() throws PatternParseException
		{
			if (bufferedToken == null)
			{
				bufferedToken = nextToken();
			}

			return bufferedToken;
		}

		private Token getNextToken() throws PatternParseException
		{
			if (pos == pattern.length())
			{
				return new Token(Token.END_OF_PATTERN, "", pos, 0);
			}

			char c = pattern.charAt(pos);
			switch (c)
			{
				case '[':
					return new Token(Token.LEFT_BRACKET, "" + c, pos, 1);
				case ']':
					return new Token(Token.RIGHT_BRACKET, "" + c, pos, 1);
				case '|':
					return new Token(Token.UNION_OPERATOR, "" + c, pos, 1);
				case '*':
					return new Token(Token.STAR_OPERATOR, "" + c, pos, 1);
				case '!':
					return new Token(Token.NEGATION_OPERATOR, "" + c, pos, 1);
				default:
					return getWord();
			}
		}

		private Token getWord() throws PatternParseException
		{
			int startPos = pos;
			char c = pattern.charAt(startPos);

			if (c == '(')
			{
				StringBuffer buffer = new StringBuffer();
				startPos++;
				while (startPos < pattern.length())
				{
					c = pattern.charAt(startPos);
					if (c == ')')
					{
						break;
					}

					if (c == '.' || Character.isLetterOrDigit(c))
					{
						buffer.append(c);
					}
					else
					{
						throw new PatternParseException("Unexpected character at" + " position " + startPos
								+ ". Expected dot, letter, digit or parenthesis.");
					}
					startPos++;
				}

				if (c != ')')
				{
					throw new PatternParseException("Unexpected end of pattern. Expected dot, "
							+ "letter, digit or parenthesis.");
				}

				return new Token(Token.WORD, buffer.toString(), pos, buffer.length() + 2);
			}
			else if (c == '.' || Character.isLetterOrDigit(c))
			{
				return new Token(Token.WORD, "" + c, pos, 1);
			}
			else
			{
				throw new PatternParseException("Unexpected characters at" + " position " + pos
						+ ". Expected dot, letter, digit or parenthesis. Found '" + c + "'");
			}
		}
	}

	private static class Token
	{
		private int type;

		private String value;

		private int position;

		private int length;

		public final static int LEFT_BRACKET = 1;

		public final static int RIGHT_BRACKET = 2;

		public final static int UNION_OPERATOR = 3;

		public final static int STAR_OPERATOR = 4;

		public final static int NEGATION_OPERATOR = 5;

		public final static int WORD = 100;

		public final static int END_OF_PATTERN = 1000;

		private Token(int type, String value, int position, int length)
		{
			this.type = type;
			this.value = value;
			this.position = position;
			this.length = length;
		}

		public int getType()
		{
			return type;
		}

		public String getValue()
		{
			return value;
		}

		public int getPosition()
		{
			return position;
		}

		public int getLength()
		{
			return length;
		}
	}

}
