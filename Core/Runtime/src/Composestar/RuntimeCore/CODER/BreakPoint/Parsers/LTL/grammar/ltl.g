///////////////////////////////////////////////////////////////////////////
// Parser for Cps files
///////////////////////////////////////////////////////////////////////////

header {
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
}

class LtlParser extends Parser;
options {
	k = 2;
	defaultErrorHandler = false;
}

formula returns [BreakPoint result = null]
	{BreakPoint right = null;}
	: GLOBAL result = formula { result = new BreakPointGlobal(result);}
	| FUTURE result = formula { result = new BreakPointFuture(result);}
	| result = subformula ( right = rightpart {((BreakPointBi)right).setLeft(result); result = right;})?
	;

rightpart returns [BreakPoint result = null]
	: OR result = formula {result = new BreakPointOr(result);}
	| AND result = formula {result = new BreakPointAnd(result);}
	| UNTIL result = formula { result = new BreakPointUntil(result);} 
	| RELEASE result = formula { result = new BreakPointRelease(result);}
	;

subformula returns [BreakPoint result = null]
	{Value left = null; Value right = null;int operatorType = 0;}
	: LPARENTHESIS result = formula RPARENTHESIS
	| left = expression (operatorType = operator right = expression { result = new BreakPointValueOperator(left,operatorType,right);})+
	;

operator returns [int result = 0]
	: BIGGEREQ{result = BreakPointValueOperator.BIGGEREQ;}
	| BIGGEREQI{result = BreakPointValueOperator.BIGGEREQ;}
	| BIGERTHEN	{result = BreakPointValueOperator.BIGGERTHEN;}
	| SMALLERTHEN{result = BreakPointValueOperator.LESSERTHEN;}
	| SMALLEREQ{result = BreakPointValueOperator.LESSEREQ;}
	| SMALLEREQI{result = BreakPointValueOperator.LESSEREQ;}
	| EQ{result = BreakPointValueOperator.EQ;}
	| NOTEQ{result = BreakPointValueOperator.NOTEQ;}
	| MINUS{result = BreakPointValueOperator.MINUS;}
	| ADD{result = BreakPointValueOperator.PLUS;}
	| DIVIDE{result = BreakPointValueOperator.DIV;}
	| TIMES{result = BreakPointValueOperator.TIMES;}
	| MOD {result = BreakPointValueOperator.DIVREST;}
	;

expression returns [Value result = null]
	: s:NAME {result = new VariableValue(s.getText());}
	| c:NAME LPARENTHESIS RPARENTHESIS {result = new FunctionCallValue(c.getText());}
	;

class LtlLexer extends Lexer;
options {
	k =2;
}

AND                     : '&';
OR				: '|';
EQ				: "==";
NOTEQ				: "!=";
BIGGEREQ                : "=>";
BIGGEREQI			: ">=";
BIGERTHEN			: ">";
SMALLERTHEN			: "=<";
SMALLERTHENI		: "<=";
TIMES				: '*';
ADD				: '+';
MINUS				: '-';
MOD				: '%';
DIVIDE			: '/';
LPARENTHESIS            : '(';
RPARENTHESIS            : ')';

GLOBAL			: G  | "[]";
FUTURE			: F | "<>";
NEXT				: X | N ;
UNTIL				: U ;
RELEASE			: R ;

protected DIGIT         : '0'..'9' ;
protected SLETTER       : 'a'..'z';
protected BLETTER		: 'A'..'Z';
protected DOT           : '.' ;
protected SPECIAL       : '_';
protected ALL		: (DIGIT | SPECIAL | SLETTER | BLETTER);
protected WORD		: (ALL)*;

//Do they have special meaning
protected G			: 'G' ;
protected F			: 'F';
protected X			: 'X';
protected N			: 'N';
protected U			: 'U';
protected R			: 'R';
protected USED 		: (G | F | X | N | R | U );
protected FREE 		: 'A'..'E''H'..'M''O'..'Q''S''T''V''W''Y''Z' ;
protected NEWLINE       : (("\r\n") => "\r\n"           //DOS
                          | '\r'                        //Macintosh
                          | '\n'){newline();};          //Unix

// NEWLINE and WS.... you can combine those
WS                      : (NEWLINE) => NEWLINE { /*newline();*/ $setType(Token.SKIP);}
                          | (' ' | '\t' | '\f') { $setType(Token.SKIP); } ;

NAME :((SLETTER | FREE | SPECIAL | DIGIT) WORD
		|'\\' WORD)
		;