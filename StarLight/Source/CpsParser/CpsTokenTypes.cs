// $ANTLR : "cps-csharp.g" -> "CpsLexer.cs"$

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: cps.g 2772 2006-11-15 15:56:35Z arjanderoo $
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
		public const int FMSET_ = 23;
		public const int FM_ = 24;
		public const int IFILTER_ = 25;
		public const int INTERNAL_ = 26;
		public const int METHOD2_ = 27;
		public const int METHODNAMESET_ = 28;
		public const int METHODNAME_ = 29;
		public const int METHOD_ = 30;
		public const int NOTEXPR_ = 31;
		public const int MPSET_ = 32;
		public const int MP_ = 33;
		public const int MPART_ = 34;
		public const int OCL_ = 35;
		public const int OFILTER_ = 36;
		public const int OREXPR_ = 37;
		public const int PARAMETER_ = 38;
		public const int PARAMETERLIST_ = 39;
		public const int SELEC2_ = 40;
		public const int SELEC_ = 41;
		public const int SELEXP_ = 42;
		public const int SOURCE_ = 43;
		public const int SPART_ = 44;
		public const int TSSET_ = 45;
		public const int TARGET_ = 46;
		public const int TYPELIST_ = 47;
		public const int TYPE_ = 48;
		public const int VAR_ = 49;
		public const int APS_ = 50;
		public const int PROLOG_EXPRESSION = 51;
		public const int LITERAL_concern = 52;
		public const int NAME = 53;
		public const int LPARENTHESIS = 54;
		public const int RPARENTHESIS = 55;
		public const int LITERAL_in = 56;
		public const int SEMICOLON = 57;
		public const int COMMA = 58;
		public const int COLON = 59;
		public const int DOT = 60;
		public const int LCURLY = 61;
		public const int RCURLY = 62;
		public const int LITERAL_filtermodule = 63;
		public const int PARAMETER_NAME = 64;
		public const int PARAMETERLIST_NAME = 65;
		public const int LITERAL_internals = 66;
		public const int LITERAL_externals = 67;
		public const int EQUALS = 68;
		public const int DOUBLE_COLON = 69;
		public const int STAR = 70;
		public const int LITERAL_conditions = 71;
		public const int LITERAL_inputfilters = 72;
		public const int FILTER_OP = 73;
		public const int OR = 74;
		public const int AND = 75;
		public const int NOT = 76;
		public const int HASH = 77;
		public const int LSQUARE = 78;
		public const int RSQUARE = 79;
		public const int LANGLE = 80;
		public const int RANGLE = 81;
		public const int LITERAL_outputfilters = 82;
		public const int LITERAL_superimposition = 83;
		public const int LITERAL_selectors = 84;
		public const int ARROW_LEFT = 85;
		public const int LITERAL_filtermodules = 86;
		public const int LITERAL_annotations = 87;
		public const int LITERAL_constraints = 88;
		public const int LITERAL_presoft = 89;
		public const int LITERAL_pre = 90;
		public const int LITERAL_prehard = 91;
		public const int LITERAL_implementation = 92;
		public const int LITERAL_by = 93;
		public const int LITERAL_as = 94;
		public const int FILENAME = 95;
		public const int QUESTIONMARK = 96;
		public const int DIGIT = 97;
		public const int FILE_SPECIAL = 98;
		public const int LETTER = 99;
		public const int NEWLINE = 100;
		public const int SPECIAL = 101;
		public const int QUOTE = 102;
		public const int SINGLEQUOTE = 103;
		public const int PROLOG_STRING = 104;
		public const int COMMENTITEMS = 105;
		public const int COMMENT = 106;
		public const int WS = 107;
		
	}
}
