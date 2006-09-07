// $ANTLR 2.7.6 (2005-12-22): "ltl.g" -> "LtlParser.java"$

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
package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.LTL;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Value.*;

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

public class LtlParser extends antlr.LLkParser       implements LtlParserTokenTypes
 {

	private Halter halt;

	public void setHalter(Halter halt)
	{
		this.halt = halt;
	}

protected LtlParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public LtlParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected LtlParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public LtlParser(TokenStream lexer) {
  this(lexer,2);
}

public LtlParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final BreakPoint  formula() throws RecognitionException, TokenStreamException {
		BreakPoint result;
		
		BreakPoint right;
		
		switch ( LA(1)) {
		case GLOBAL:
		{
			match(GLOBAL);
			result=formula();
			result = new BreakPointGlobal(halt,result);
			break;
		}
		case FUTURE:
		{
			match(FUTURE);
			result=formula();
			result = new BreakPointFuture(halt,result);
			break;
		}
		case LPARENTHESIS:
		case NAME:
		{
			result=subformula();
			{
			switch ( LA(1)) {
			case OR:
			case AND:
			case UNTIL:
			case RELEASE:
			{
				right=rightpart();
				((BreakPointBi)right).setLeft(result); result = right;
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
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return result;
	}
	
	public final BreakPoint  subformula() throws RecognitionException, TokenStreamException {
		BreakPoint result = null;
		
		Value left; Value right;int operatorType;
		
		switch ( LA(1)) {
		case LPARENTHESIS:
		{
			match(LPARENTHESIS);
			result=formula();
			match(RPARENTHESIS);
			break;
		}
		case NAME:
		{
			left=expression();
			{
			int _cnt6=0;
			_loop6:
			do {
				if (((LA(1) >= BIGGEREQ && LA(1) <= MOD))) {
					operatorType=operator();
					right=expression();
					result = new BreakPointValueOperator(halt,left,operatorType,right);
				}
				else {
					if ( _cnt6>=1 ) { break _loop6; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt6++;
			} while (true);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return result;
	}
	
	public final BreakPoint  rightpart() throws RecognitionException, TokenStreamException {
		BreakPoint result;
		
		
		switch ( LA(1)) {
		case OR:
		{
			match(OR);
			result=formula();
			result = new BreakPointOr(halt,result);
			break;
		}
		case AND:
		{
			match(AND);
			result=formula();
			result = new BreakPointAnd(halt,result);
			break;
		}
		case UNTIL:
		{
			match(UNTIL);
			result=formula();
			result = new BreakPointUntil(halt,result);
			break;
		}
		case RELEASE:
		{
			match(RELEASE);
			result=formula();
			result = new BreakPointRelease(halt,result);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		return result;
	}
	
	public final Value  expression() throws RecognitionException, TokenStreamException {
		Value result;
		
		Token  s;
		Token  c;
		
		if ((LA(1)==NAME) && (_tokenSet_0.member(LA(2)))) {
			s = LT(1);
			match(NAME);
			result = new VariableValue(s.getText());
		}
		else if ((LA(1)==NAME) && (LA(2)==LPARENTHESIS)) {
			c = LT(1);
			match(NAME);
			match(LPARENTHESIS);
			match(RPARENTHESIS);
			result = new FunctionCallValue(c.getText());
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		return result;
	}
	
	public final int  operator() throws RecognitionException, TokenStreamException {
		int result;
		
		
		switch ( LA(1)) {
		case BIGGEREQ:
		{
			match(BIGGEREQ);
			result = BreakPointValueOperator.BIGGEREQ;
			break;
		}
		case BIGGEREQI:
		{
			match(BIGGEREQI);
			result = BreakPointValueOperator.BIGGEREQ;
			break;
		}
		case BIGERTHEN:
		{
			match(BIGERTHEN);
			result = BreakPointValueOperator.BIGGERTHEN;
			break;
		}
		case SMALLERTHEN:
		{
			match(SMALLERTHEN);
			result = BreakPointValueOperator.LESSERTHEN;
			break;
		}
		case SMALLEREQ:
		{
			match(SMALLEREQ);
			result = BreakPointValueOperator.LESSEREQ;
			break;
		}
		case SMALLEREQI:
		{
			match(SMALLEREQI);
			result = BreakPointValueOperator.LESSEREQ;
			break;
		}
		case EQ:
		{
			match(EQ);
			result = BreakPointValueOperator.EQ;
			break;
		}
		case NOTEQ:
		{
			match(NOTEQ);
			result = BreakPointValueOperator.NOTEQ;
			break;
		}
		case MINUS:
		{
			match(MINUS);
			result = BreakPointValueOperator.MINUS;
			break;
		}
		case ADD:
		{
			match(ADD);
			result = BreakPointValueOperator.PLUS;
			break;
		}
		case DIVIDE:
		{
			match(DIVIDE);
			result = BreakPointValueOperator.DIV;
			break;
		}
		case TIMES:
		{
			match(TIMES);
			result = BreakPointValueOperator.TIMES;
			break;
		}
		case MOD:
		{
			match(MOD);
			result = BreakPointValueOperator.DIVREST;
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
		"GLOBAL",
		"FUTURE",
		"OR",
		"AND",
		"UNTIL",
		"RELEASE",
		"LPARENTHESIS",
		"RPARENTHESIS",
		"BIGGEREQ",
		"BIGGEREQI",
		"BIGERTHEN",
		"SMALLERTHEN",
		"SMALLEREQ",
		"SMALLEREQI",
		"EQ",
		"NOTEQ",
		"MINUS",
		"ADD",
		"DIVIDE",
		"TIMES",
		"MOD",
		"NAME",
		"SMALLERTHENI",
		"NEXT",
		"DIGIT",
		"SLETTER",
		"BLETTER",
		"DOT",
		"SPECIAL",
		"ALL",
		"WORD",
		"G",
		"F",
		"X",
		"N",
		"U",
		"R",
		"USED",
		"FREE",
		"NEWLINE",
		"WS"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 33553344L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
