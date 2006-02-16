///////////////////////////////////////////////////////////////////////////
// Parser for Regular expressions
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
package Composestar.Runtime.CODER.VisualDebugger.Parsers.Reg;
import Composestar.Runtime.CODER.VisualDebugger.Parsers.*;
}

class RegParser extends Parser;
options {
	k = 2;
	defaultErrorHandler = false;
}
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
}

expression returns [RegularExpression result = null]
	{RegularExpression right = null;}
	: result = subexpression (right = rightpart { result = operatorMerge(result,right);})?
	;

rightpart returns [RegularExpression result = null]
	: STAR { result = new RegularExpressionStar();}
	| (NEXT)+ result = expression
	| PLUS { result = new RegularExpressionPlus();}
	| OPTIONAL { result = new RegularExpressionOptional();}
	;

subexpression returns [RegularExpression result = null]
	: LPARENTHESIS result = expression RPARENTHESIS
	| n:NAME { result = new RegularExpressionName(n.getText());}
	;

class RegLexer extends Lexer;
options {
	k =2;
}

OPTIONAL			: '?';
PLUS				: '+';
STAR				: '*';
NEXT                    : (';' | ' ' | '\t' | '\f');
OR				: '|';
LPARENTHESIS            : '(';
RPARENTHESIS            : ')';

protected DIGIT         : '0'..'9' ;
protected SLETTER       : 'a'..'z';
protected BLETTER		: 'A'..'Z';
protected DOT           : '.' ;
protected SPECIAL       : '_';
protected ALL		: (DIGIT | SPECIAL | SLETTER | BLETTER);
NAME				: (ALL)+;

NEWLINE       : (("\r\n") => "\r\n"                                  //DOS
                          | '\r'                                     //Macintosh
                          | '\n'){newline(); $setType(Token.SKIP);}; //Unix
