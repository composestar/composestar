// $ANTLR : "cps-csharp.g" -> "CpsLexer.cs"$

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: cps.g 3328 2007-01-13 11:57:32Z arjanderoo $
 */

//package Composestar.Core.COPPER;
using StringBuffer = System.Text.StringBuilder;

namespace Composestar.StarLight.CpsParser
{
	public class CpsTokenTypes
	{
		public const int EOF = 1;
		public const int NULL_TREE_LOOKAHEAD = 3;
		public const int ANDEXPR_ = 4;
		public const int ANNOTELEM_ = 5;
		public const int ANNOTSET_ = 6;
		public const int ANNOT_ = 7;
		public const int ARGUMENT_ = 8;
		public const int DECLAREDARGUMENT_ = 9;
		public const int DECLAREDPARAMETER_ = 10;
		public const int FPMSET_ = 11;
		public const int FPMDEF_ = 12;
		public const int FPMSTYPE_ = 13;
		public const int FPMDEFTYPE_ = 14;
		public const int CONDITION_ = 15;
		public const int CONDNAMESET_ = 16;
		public const int CONDNAME_ = 17;
		public const int EXTERNAL_ = 18;
		public const int FILTERELEM_ = 19;
		public const int FILTERMODULEPARAMETERS_ = 20;
		public const int FILTERSET_ = 21;
		public const int FMELEM_ = 22;
		public const int FMCONDBIND_ = 23;
		public const int FMSET_ = 24;
		public const int FM_ = 25;
		public const int IFILTER_ = 26;
		public const int INTERNAL_ = 27;
		public const int METHOD2_ = 28;
		public const int METHODNAMESET_ = 29;
		public const int METHODNAME_ = 30;
		public const int METHOD_ = 31;
		public const int NOTEXPR_ = 32;
		public const int MPSET_ = 33;
		public const int MP_ = 34;
		public const int MPART_ = 35;
		public const int OCL_ = 36;
		public const int OFILTER_ = 37;
		public const int OREXPR_ = 38;
		public const int PARAMETER_ = 39;
		public const int PARAMETERLIST_ = 40;
		public const int SELEC2_ = 41;
		public const int SELEC_ = 42;
		public const int SELEXP_ = 43;
		public const int SOURCE_ = 44;
		public const int SPART_ = 45;
		public const int TSSET_ = 46;
		public const int TARGET_ = 47;
		public const int TYPELIST_ = 48;
		public const int TYPE_ = 49;
		public const int VAR_ = 50;
		public const int APS_ = 51;
		public const int PROLOG_EXPRESSION = 52;
		public const int LITERAL_concern = 53;
		public const int NAME = 54;
		public const int LPARENTHESIS = 55;
		public const int RPARENTHESIS = 56;
		public const int LITERAL_in = 57;
		public const int SEMICOLON = 58;
		public const int COMMA = 59;
		public const int COLON = 60;
		public const int DOT = 61;
		public const int LCURLY = 62;
		public const int RCURLY = 63;
		public const int LITERAL_filtermodule = 64;
		public const int PARAMETER_NAME = 65;
		public const int PARAMETERLIST_NAME = 66;
		public const int LITERAL_internals = 67;
		public const int LITERAL_externals = 68;
		public const int EQUALS = 69;
		public const int DOUBLE_COLON = 70;
		public const int STAR = 71;
		public const int LITERAL_conditions = 72;
		public const int LITERAL_inputfilters = 73;
		public const int FILTER_OP = 74;
		public const int OR = 75;
		public const int AND = 76;
		public const int NOT = 77;
		public const int HASH = 78;
		public const int LSQUARE = 79;
		public const int RSQUARE = 80;
		public const int LANGLE = 81;
		public const int RANGLE = 82;
		public const int LITERAL_outputfilters = 83;
		public const int LITERAL_superimposition = 84;
		public const int LITERAL_selectors = 85;
		public const int ARROW_LEFT = 86;
		public const int LITERAL_filtermodules = 87;
		public const int LITERAL_annotations = 88;
		public const int LITERAL_constraints = 89;
		public const int LITERAL_presoft = 90;
		public const int LITERAL_pre = 91;
		public const int LITERAL_prehard = 92;
		public const int LITERAL_implementation = 93;
		public const int LITERAL_by = 94;
		public const int LITERAL_as = 95;
		public const int FILENAME = 96;
		public const int QUESTIONMARK = 97;
		public const int DIGIT = 98;
		public const int FILE_SPECIAL = 99;
		public const int LETTER = 100;
		public const int NEWLINE = 101;
		public const int SPECIAL = 102;
		public const int QUOTE = 103;
		public const int SINGLEQUOTE = 104;
		public const int PROLOG_STRING = 105;
		public const int COMMENTITEMS = 106;
		public const int COMMENT = 107;
		public const int WS = 108;
		
	}
}
