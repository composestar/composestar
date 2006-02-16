///////////////////////////////////////////////////////////////////////////
// Parser for end of embedded source code
///////////////////////////////////////////////////////////////////////////

header {
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: cpssrc.g,v 1.2 2006/02/13 11:53:08 pascal Exp $
 */
/**
 * Parser / lexer class to find the ending position of embedded sourcecode
 */
package Composestar.Core.COPPER;

import java.util.Vector;
}

class CpsSrcParser extends Parser;
options {
	exportVocab = CpsSrc;
}
{

  public int endPos = 0;
}

src : ((r:RCURLY)(SEMICOLON)? ) 
      {endPos = ((PosToken) r).getBytePos() + 1; };
      // + 3 = length of "end", sourcecode starts after that
      // + 1 = length of ";"
  


///////////////////////////////////////////////////////////////////////////
// Lexer for end of embedded source code
///////////////////////////////////////////////////////////////////////////

class CpsSrcLexer extends Lexer;
options {
	k = 1;
	exportVocab = CpsSrc; 
	charVocabulary = '\3'..'\377'; // just handle ASCII not Unicode
}
														 

RCURLY 		: '}' ;
SEMICOLON 		: ';' ;

protected DIGIT 	: '0'..'9' ;
protected LETTER 	: 'a'..'z'|'A'..'Z' ;
protected NEWLINE 	: '\r'    		
          		      | '\n';
protected SPECIAL	: '_';

NAME		: (LETTER | DIGIT | SPECIAL)+;	//since we reversed the string, we can now start with a digit

WS 			: (NEWLINE) => NEWLINE { newline(); $setType(Token.SKIP);}
			  | (' ' | '\t' | '\f') { $setType(Token.SKIP); } ;
	
//fixme: add support for comments
