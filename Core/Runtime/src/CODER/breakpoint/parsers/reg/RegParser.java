// $ANTLR 2.7.6 (2005-12-22): "Reg.g" -> "RegParser.java"$

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 */

/**
 * Parser / lexer class for LTL files
 */
package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Reg;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class RegParser extends antlr.LLkParser       implements RegParserTokenTypes
 {

	protected RegularExpression operatorMerge(RegularExpression left, RegularExpression right){
		if(right == null)
		{
			return left;
		}
		if(right instanceof RegularExpressionOperator && !right.hasNext())
		{
			right.setNext(left);
			return right;
		}
		else{
			left.setNext(right);
			return left;
		}
 	}

protected RegParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public RegParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected RegParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public RegParser(TokenStream lexer) {
  this(lexer,2);
}

public RegParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final RegularExpression  expression() throws RecognitionException, TokenStreamException {
		RegularExpression result;
		
		RegularExpression right;
		
		result=subexpression();
		{
		switch ( LA(1)) {
		case STAR:
		case NEXT:
		case PLUS:
		case OPTIONAL:
		{
			right=rightpart();
			result = operatorMerge(result,right);
			break;
		}
		case RPARENTHESIS:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return result;
	}
	
	public final RegularExpression  subexpression() throws RecognitionException, TokenStreamException {
		RegularExpression result;
		
		Token  n;
		
		switch ( LA(1)) {
		case LPARENTHESIS:
		{
			match(LPARENTHESIS);
			result=expression();
			match(RPARENTHESIS);
			break;
		}
		case NAME:
		{
			n = LT(1);
			match(NAME);
			result = new RegularExpressionName(n.getText());
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return result;
	}
	
	public final RegularExpression  rightpart() throws RecognitionException, TokenStreamException {
		RegularExpression result;
		
		
		switch ( LA(1)) {
		case STAR:
		{
			match(STAR);
			result = new RegularExpressionStar();
			break;
		}
		case NEXT:
		{
			{
			int _cnt5=0;
			_loop5:
			do {
				if ((LA(1)==NEXT)) {
					match(NEXT);
				}
				else {
					if ( _cnt5>=1 ) { break _loop5; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt5++;
			} while (true);
			}
			result=expression();
			break;
		}
		case PLUS:
		{
			match(PLUS);
			result = new RegularExpressionPlus();
			break;
		}
		case OPTIONAL:
		{
			match(OPTIONAL);
			result = new RegularExpressionOptional();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return result;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"STAR",
		"NEXT",
		"PLUS",
		"OPTIONAL",
		"LPARENTHESIS",
		"RPARENTHESIS",
		"NAME",
		"OR",
		"DIGIT",
		"SLETTER",
		"BLETTER",
		"DOT",
		"SPECIAL",
		"ALL",
		"NEWLINE"
	};
	
	
	}
