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

public interface RegParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int STAR = 4;
	int NEXT = 5;
	int PLUS = 6;
	int OPTIONAL = 7;
	int LPARENTHESIS = 8;
	int RPARENTHESIS = 9;
	int NAME = 10;
	int OR = 11;
	int DIGIT = 12;
	int SLETTER = 13;
	int BLETTER = 14;
	int DOT = 15;
	int SPECIAL = 16;
	int ALL = 17;
	int NEWLINE = 18;
}
