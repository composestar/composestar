// $ANTLR 2.7.6 (2005-12-22): "ltl.g" -> "LtlLexer.java"$

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
package Composestar.RuntimeCore.CODER.VisualDebugger.Parsers.LTL;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.VisualDebugger.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Value.*;

public interface LtlParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int GLOBAL = 4;
	int FUTURE = 5;
	int OR = 6;
	int AND = 7;
	int UNTIL = 8;
	int RELEASE = 9;
	int LPARENTHESIS = 10;
	int RPARENTHESIS = 11;
	int BIGGEREQ = 12;
	int BIGGEREQI = 13;
	int BIGERTHEN = 14;
	int SMALLERTHEN = 15;
	int SMALLEREQ = 16;
	int SMALLEREQI = 17;
	int EQ = 18;
	int NOTEQ = 19;
	int MINUS = 20;
	int ADD = 21;
	int DIVIDE = 22;
	int TIMES = 23;
	int MOD = 24;
	int NAME = 25;
	int SMALLERTHENI = 26;
	int NEXT = 27;
	int DIGIT = 28;
	int SLETTER = 29;
	int BLETTER = 30;
	int DOT = 31;
	int SPECIAL = 32;
	int ALL = 33;
	int WORD = 34;
	int G = 35;
	int F = 36;
	int X = 37;
	int N = 38;
	int U = 39;
	int R = 40;
	int USED = 41;
	int FREE = 42;
	int NEWLINE = 43;
	int WS = 44;
}
