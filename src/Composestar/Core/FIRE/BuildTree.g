/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 *
**/

header {
package Composestar.CTCommon.FIRE;
}

// Global options.
options
{
	mangleLiteralPrefix = "TK_"; // instead of TOKEN
	language="Java";
}


//////////////////////////// PARSER //////////////////////////////////

/**
 * Parser for the TT language.
 *
 * Checks that the token stream from the lexer is according to the TT language 
 * specification and builds the corresponding abstract syntax tree.
 */
class TT_BuildTree extends Parser ;

options {
    k = 1;                             // # tokens lookahead
    exportVocab = TT_;                 // call the vocabulary "TT"
    buildAST = true;                    // build an AST
    //ASTLabelType = "RefTTASTNodeBase";
	defaultErrorHandler = false;
}

/*!
 * Imaginary tokens that are used for AST construction.
 */
tokens {
	PAREN; 	// Stuff between the LPAREN and RPAREN
	BRACE; 	// Stuff between the LBRACE and RBRACE

	PROGRAM; 	// Root node.
}


/**
 * \brief program is the toplevel parsing function of TT.
 *
 * A program can consist of several functions. The functions are encapsulated 
 * in the toplevel PROGRAM node.
 */
program
   : filterExpression EOF!
 	{#program = #([PROGRAM, "program"], #program); }
   ;

filterExpression
	: semiFilterExpression (COMMA^ semiFilterExpression)*
	;

semiFilterExpression 
	: filter (SEMI^ filter)* 
	;

filter
	:! a: IDENT EQUALS (LBRACE! b: expression RBRACE!)
	{ #filter = #(EQUALS, b, a); }
	| filterParen
	;

filterParen
	: LBRACE! filterExpression RBRACE!
	;

/**** Inside the filter **/

expression 			: commaExpression ;
commaExpression 	: semiExpression (COMMA^ semiExpression)*;
semiExpression 		: impliesExpression (SEMI^ impliesExpression)*;
impliesExpression 	: orExpression ((IMPLIES^ | IMPLIESNOT^)  orExpression)*;
orExpression 		: andExpression (OR^ andExpression)*;
andExpression 		: slashExpression (AND^ slashExpression)*;
slashExpression		: notExpression (SLASH^ notExpression)*;
notExpression 		: (NOT^)? dotExpression;
dotExpression 		: atom (DOT^ atom)*;

atom
	:  IDENT
	|  STAR
	|  trueAtom
	|  falseAtom
	|  braces
	|  signature
	;

braces
	: LBRACE! expression RBRACE!  
	;

signature : SIGNATURE^ LPAREN! atom RPAREN!;


trueAtom : ONE | TRUE ;
falseAtom : ZERO | FALSE ;


//////////////////////////// LEXER ///////////////////////////////////

/**
 * \brief Lexer for the TrueTable language.
 */
class TT_Lexer extends Lexer;
options
{
	k=5; 							// 3 tokens lookahead.
	exportVocab=TT_;
	charVocabulary = '\3'..'\377';

    caseSensitive = true;           // lower and upper case is significant
    caseSensitiveLiterals = true;   // literals are case sensitive
    testLiterals = false;           // do not check the tokens table by default
}

/*!
 * These are the keywords of TT.
 */
tokens
{
	TRUE = "true";
	FALSE = "false";

	SIGNATURE = "inSignature";
}

ZERO : "0";
ONE  : "1";

/** LPAREN is denoted by '(' */
LPAREN    options{paraphrase="(";} :   '(' ;
/** RPAREN is denoted by ')' */
RPAREN    options{paraphrase=")";} :   ')' ;
/** LBRACKET is denoted by '[' */
LBRACKET  options{paraphrase="[";} :   "[" ;
/** RBRACKET is denoted by ']' */
RBRACKET  options{paraphrase="]";} :   "]" ;
/** LBRACE is denoted by '{' */
LBRACE  options{paraphrase="{";} :   "{" ;
/** RBRACE is denoted by '}' */
RBRACE  options{paraphrase="}";} :   "}" ;
/** LTAHN is denoted by '<' */
LTHAN  options{paraphrase="<";} :   "<" ;
/** RTHAN is denoted by '>' */
RTHAN  options{paraphrase=">";} :   ">" ;

// relational operators

EQUALS : "=";

/** AND's But with different priorities **/
SEMI				: ";"; 
AND					: "&";
IMPLIES				: "=>";
IMPLIESNOT 			: "~>";	
SLASH				: "/";
DOT					: ".";

/** OR's **/
OR				: "|";
COMMA 			: ",";

/** NOT **/
NOT				: "~" | "!";

STAR			: "*";


/**
 * And IDENT is a series of characters and digits (always start with a lower char).
 * Checks for keywords with testLiterals = true.
 */
IDENT
options
{
	testLiterals = true;
	paraphrase = "an identifier";
}
    :  (LOWER | UPPER | SPECIAL) (LOWER | UPPER | DIGIT | SPECIAL)*
	;

/*
 * And XIDENT is a series of characters and digits (always start with a upper char).
 * Checks for keywords with testLiterals = true.
 */
/*
XIDENT
options
{
	testLiterals = true;
	paraphrase = "an exclusive identifier";
}
    :   UPPER (LOWER | UPPER | DIGIT | SPECIAL)*
	;
*/

// Yeah, protected. Simply: It's not a token which can be recognized.
// It's only a part or subset of an recognizable token. e.g. INT.
/**
 * a..z is a the subset of token e.g. an IDENT
 */
protected
LOWER           :   ('a'..'z') ;

/**
 * A..Z is a the subset of token e.g. an IDENT
 */
protected
UPPER           :   ('A'..'Z') ;

/**
 * 0..9 is a the subset of token e.g. an INT
 */
protected
DIGIT           :   ('0'..'9') ;

/**
 * "_" may be in a identifier and must be handled seperatly since it is 
 * not in a..z
 */
protected
SPECIAL           :  '_' ;


/**
 * Whitespace - ignore
 */
WS
    :   ( ' '
        |	'\t'
        |	'\f'
        // handle newlines
        |   (	"\r\n"  // Yeah DOS
			|	'\r' 	// Macintosh or DOS
            |	'\n'    // Unix (the right way) 
            )
			
            { newline(); }
        )
        { $setType(antlr.Token.SKIP); }
    ;
//NL			options{paraphrase="newline";} 	: ("\r\n" | '\n' | '\r');

/**
 * Single line comment - ignore
 * Single line comments are // like in C++
 */
SL_COMMENT :
	"//"
	(~'\n')* '\n'
	{ _ttype = antlr.Token.SKIP; newline(); }
	;

/*
 * Multi line comment - ignore
 * Multi line comments are like in C/C++
 */
//ML_COMMENT
//:	"/*"
//(	{ LA(2)!='/' }? '*'
//		|	'\n' { newline(); }
//		|	~('*'|'\n')
//		)*
//		"*/"
//		{ $setType(antlr.Token.SKIP); }
//	;
