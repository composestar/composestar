/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import java.util.Dictionary;

import Composestar.Core.FIRE2.util.queryengine.Predicate;

public class CtlParser
{

	public static CtlFormula parse(String pattern, Dictionary<String, Predicate> predicates) throws CtlParseException
	{
		Parser parser = new Parser(predicates);
		return parser.parse(pattern);
	}

	private static class Parser
	{
		private Dictionary predicates;

		public Parser(Dictionary<String, Predicate> predicates)
		{
			this.predicates = predicates;
			predicates.put("true", new True());
			predicates.put("false", new False());
		}

		public CtlFormula parse(String pattern) throws CtlParseException
		{
			return parseLevel1(new Lexer(pattern));
		}

		private CtlFormula parseLevel1(Lexer lexer) throws CtlParseException
		{
			CtlFormula formula1 = parseLevel2(lexer);

			Token token = lexer.peekNextToken();
			if (token.getType() != Token.IMPLIES_OPERATOR)
			{
				return formula1;
			}
			else
			{
				lexer.nextToken();
			}

			CtlFormula formula2 = parseLevel1(lexer);

			return new Implies(formula1, formula2);
		}

		private CtlFormula parseLevel2(Lexer lexer) throws CtlParseException
		{
			CtlFormula formula1 = parseLevel3(lexer);

			Token token = lexer.peekNextToken();
			if (token.getType() != Token.OR_OPERATOR)
			{
				return formula1;
			}
			else
			{
				lexer.nextToken();
			}

			CtlFormula formula2 = parseLevel2(lexer);

			return new Or(formula1, formula2);
		}

		private CtlFormula parseLevel3(Lexer lexer) throws CtlParseException
		{
			CtlFormula formula1 = parseLevel4(lexer);

			Token token = lexer.peekNextToken();
			if (token.getType() != Token.AND_OPERATOR)
			{
				return formula1;
			}
			else
			{
				lexer.nextToken();
			}

			CtlFormula formula2 = parseLevel3(lexer);

			return new And(formula1, formula2);
		}

		private CtlFormula parseLevel4(Lexer lexer) throws CtlParseException
		{
			CtlFormula formula1;
			CtlFormula formula2;

			Token token = lexer.peekNextToken();
			switch (token.getType())
			{
				case Token.A_OPERATOR:
					lexer.nextToken();
					token = lexer.nextToken();

					switch (token.getType())
					{

						case Token.X_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new AX(formula1);

						case Token.F_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new AF(formula1);

						case Token.G_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new AG(formula1);

						case Token.LEFT_BOXBRACKET:
							formula1 = parseLevel1(lexer);

							token = lexer.nextToken();
							if (token.getType() != Token.U_OPERATOR)
							{
								throw new CtlParseException();
							}

							formula2 = parseLevel1(lexer);

							token = lexer.nextToken();
							if (token.getType() != Token.RIGHT_BOXBRACKET)
							{
								throw new CtlParseException();
							}

							return new AU(formula1, formula2);
						default:
							throw new CtlParseException();
					}
				case Token.E_OPERATOR:
					lexer.nextToken();
					token = lexer.nextToken();

					switch (token.getType())
					{

						case Token.X_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new EX(formula1);

						case Token.F_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new EF(formula1);

						case Token.G_OPERATOR:
							formula1 = parseLevel4(lexer);
							return new EG(formula1);

						case Token.LEFT_BOXBRACKET:
							formula1 = parseLevel1(lexer);

							token = lexer.nextToken();
							if (token.getType() != Token.U_OPERATOR)
							{
								throw new CtlParseException();
							}

							formula2 = parseLevel1(lexer);

							token = lexer.nextToken();
							if (token.getType() != Token.RIGHT_BOXBRACKET)
							{
								throw new CtlParseException();
							}

							return new EU(formula1, formula2);
						default:
							throw new CtlParseException();
					}
				case Token.NOT_OPERATOR:
					lexer.nextToken();
					formula1 = parseLevel4(lexer);
					return new Not(formula1);
				case Token.REVERSE_OPERATOR:
					lexer.nextToken();
					formula1 = parseLevel4(lexer);
					return new Reverse(formula1);
				default:
					return parseLevel5(lexer);
			}
		}

		private CtlFormula parseLevel5(Lexer lexer) throws CtlParseException
		{
			Token token = lexer.peekNextToken();
			switch (token.getType())
			{
				case Token.LEFT_BRACKET:
					lexer.nextToken();
					CtlFormula formula1 = parseLevel1(lexer);
					token = lexer.nextToken();
					if (token.getType() != Token.RIGHT_BRACKET)
					{
						throw new CtlParseException();
					}

					return formula1;
				case Token.IDENTIFIER:
					lexer.nextToken();
					return (CtlFormula) predicates.get(token.getValue());
				default:
					throw new CtlParseException();
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
			this.pattern = pattern.trim();
			this.pos = 0;
		}

		public boolean hasMoreTokens()
		{
			return bufferedToken != null || pos < pattern.length();
		}

		public Token nextToken() throws CtlParseException
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

		public Token peekNextToken() throws CtlParseException
		{
			if (bufferedToken == null)
			{
				bufferedToken = nextToken();
			}

			return bufferedToken;
		}

		private Token getNextToken() throws CtlParseException
		{
			if (pos == pattern.length())
			{
				return new Token(Token.END_OF_PATTERN, "", pos, 0);
			}

			char c = getChar(pos);
			char c2;
			switch (c)
			{
				case '(':
					return new Token(Token.LEFT_BRACKET, "" + c, pos, 1);
				case ')':
					return new Token(Token.RIGHT_BRACKET, "" + c, pos, 1);
				case '[':
					return new Token(Token.LEFT_BOXBRACKET, "" + c, pos, 1);
				case ']':
					return new Token(Token.RIGHT_BOXBRACKET, "" + c, pos, 1);
				case '!':
					return new Token(Token.NOT_OPERATOR, "" + c, pos, 1);
				case '&':
					c2 = getChar(pos + 1);
					if (c2 == '&')
					{
						return new Token(Token.AND_OPERATOR, "&&", pos, 2);
					}
					else
					{
						throw new CtlParseException();
					}
				case '|':
					c2 = getChar(pos + 1);
					if (c2 == '|')
					{
						return new Token(Token.OR_OPERATOR, "||", pos, 2);
					}
					else
					{
						throw new CtlParseException();
					}
				case '-':
					c2 = getChar(pos + 1);
					if (c2 == '>')
					{
						return new Token(Token.IMPLIES_OPERATOR, "->", pos, 2);
					}
					else
					{
						throw new CtlParseException();
					}
				case 'A':
					return new Token(Token.A_OPERATOR, "" + c, pos, 1);
				case 'E':
					return new Token(Token.E_OPERATOR, "" + c, pos, 1);
				case 'X':
					return new Token(Token.X_OPERATOR, "" + c, pos, 1);
				case 'F':
					return new Token(Token.F_OPERATOR, "" + c, pos, 1);
				case 'G':
					return new Token(Token.G_OPERATOR, "" + c, pos, 1);
				case 'U':
					return new Token(Token.U_OPERATOR, "" + c, pos, 1);
				case '~':
					return new Token(Token.REVERSE_OPERATOR, "" + c, pos, 1);
				default:
					if (Character.isWhitespace(c))
					{
						pos++;
						return getNextToken();
					}
					else
					{
						return getIdentifier();
					}
			}
		}

		private char getChar(int position) throws CtlParseException
		{
			if (position == pattern.length())
			{
				throw new CtlParseException();
			}
			else
			{
				return pattern.charAt(position);
			}
		}

		private Token getIdentifier() throws CtlParseException
		{
			int startPos = pos;
			char c = pattern.charAt(startPos);

			if (Character.isLetter(c) && Character.isLowerCase(c))
			{
				StringBuffer buffer = new StringBuffer();
				buffer.append(c);
				startPos++;
				while (startPos < pattern.length())
				{
					c = getChar(startPos);
					if (c == '_' || Character.isLetterOrDigit(c))
					{
						buffer.append(c);
					}
					else
					{
						break;
					}

					startPos++;
				}

				return new Token(Token.IDENTIFIER, buffer.toString(), pos, buffer.length());
			}
			else
			{
				throw new CtlParseException();
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

		public final static int LEFT_BOXBRACKET = 3;

		public final static int RIGHT_BOXBRACKET = 4;

		public final static int AND_OPERATOR = 101;

		public final static int OR_OPERATOR = 102;

		public final static int NOT_OPERATOR = 103;

		public final static int IMPLIES_OPERATOR = 104;

		public final static int X_OPERATOR = 201;

		public final static int F_OPERATOR = 202;

		public final static int G_OPERATOR = 203;

		public final static int U_OPERATOR = 204;

		public final static int A_OPERATOR = 301;

		public final static int E_OPERATOR = 302;

		public final static int REVERSE_OPERATOR = 401;

		public final static int IDENTIFIER = 501;

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
