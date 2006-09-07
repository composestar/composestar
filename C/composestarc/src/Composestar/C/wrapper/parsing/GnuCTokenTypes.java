// $ANTLR 2.7.4: "expandedGnuCParser.g" -> "GnuCParser.java"$

	/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: GnuCParser.g,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
	
	package Composestar.C.wrapper.parsing;

public interface GnuCTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_typedef = 4;
	int LITERAL_asm = 5;
	int LITERAL_volatile = 6;
	int LCURLY = 7;
	int RCURLY = 8;
	int SEMI = 9;
	int LITERAL_struct = 10;
	int LITERAL_union = 11;
	int LITERAL_enum = 12;
	int LITERAL_auto = 13;
	int LITERAL_register = 14;
	int LITERAL_extern = 15;
	int LITERAL_static = 16;
	int LITERAL_const = 17;
	int LITERAL_void = 18;
	int LITERAL_char = 19;
	int LITERAL_short = 20;
	int LITERAL_int = 21;
	int LITERAL_long = 22;
	int LITERAL_float = 23;
	int LITERAL_double = 24;
	int LITERAL_signed = 25;
	int LITERAL_unsigned = 26;
	int ID = 27;
	int COMMA = 28;
	int COLON = 29;
	int ASSIGN = 30;
	int STAR = 31;
	int LPAREN = 32;
	int RPAREN = 33;
	int LBRACKET = 34;
	int RBRACKET = 35;
	int VARARGS = 36;
	int LITERAL_while = 37;
	int LITERAL_do = 38;
	int LITERAL_for = 39;
	int LITERAL_goto = 40;
	int LITERAL_continue = 41;
	int LITERAL_break = 42;
	int LITERAL_return = 43;
	int LITERAL_case = 44;
	int LITERAL_default = 45;
	int LITERAL_if = 46;
	int LITERAL_else = 47;
	int LITERAL_switch = 48;
	int DIV_ASSIGN = 49;
	int PLUS_ASSIGN = 50;
	int MINUS_ASSIGN = 51;
	int STAR_ASSIGN = 52;
	int MOD_ASSIGN = 53;
	int RSHIFT_ASSIGN = 54;
	int LSHIFT_ASSIGN = 55;
	int BAND_ASSIGN = 56;
	int BOR_ASSIGN = 57;
	int BXOR_ASSIGN = 58;
	int QUESTION = 59;
	int LOR = 60;
	int LAND = 61;
	int BOR = 62;
	int BXOR = 63;
	int BAND = 64;
	int EQUAL = 65;
	int NOT_EQUAL = 66;
	int LT = 67;
	int LTE = 68;
	int GT = 69;
	int GTE = 70;
	int LSHIFT = 71;
	int RSHIFT = 72;
	int PLUS = 73;
	int MINUS = 74;
	int DIV = 75;
	int MOD = 76;
	int INC = 77;
	int DEC = 78;
	int LITERAL_sizeof = 79;
	int BNOT = 80;
	int LNOT = 81;
	int PTR = 82;
	int DOT = 83;
	int CharLiteral = 84;
	int StringLiteral = 85;
	int IntOctalConst = 86;
	int LongOctalConst = 87;
	int UnsignedOctalConst = 88;
	int IntIntConst = 89;
	int LongIntConst = 90;
	int UnsignedIntConst = 91;
	int IntHexConst = 92;
	int LongHexConst = 93;
	int UnsignedHexConst = 94;
	int FloatDoubleConst = 95;
	int DoubleDoubleConst = 96;
	int LongDoubleConst = 97;
	int NTypedefName = 98;
	int NInitDecl = 99;
	int NDeclarator = 100;
	int NStructDeclarator = 101;
	int NDeclaration = 102;
	int NCast = 103;
	int NPointerGroup = 104;
	int NExpressionGroup = 105;
	int NFunctionCallArgs = 106;
	int NNonemptyAbstractDeclarator = 107;
	int NInitializer = 108;
	int NStatementExpr = 109;
	int NEmptyExpression = 110;
	int NParameterTypeList = 111;
	int NFunctionDef = 112;
	int NCompoundStatement = 113;
	int NParameterDeclaration = 114;
	int NCommaExpr = 115;
	int NUnaryExpr = 116;
	int NLabel = 117;
	int NPostfixExpr = 118;
	int NRangeExpr = 119;
	int NStringSeq = 120;
	int NInitializerElementLabel = 121;
	int NLcurlyInitializer = 122;
	int NAsmAttribute = 123;
	int NGnuAsmExpr = 124;
	int NTypeMissing = 125;
	int Vocabulary = 126;
	int Whitespace = 127;
	int Comment = 128;
	int CPPComment = 129;
	int PREPROC_DIRECTIVE = 130;
	int Space = 131;
	int LineDirective = 132;
	int BadStringLiteral = 133;
	int Escape = 134;
	int Digit = 135;
	int LongSuffix = 136;
	int UnsignedSuffix = 137;
	int FloatSuffix = 138;
	int Exponent = 139;
	int Number = 140;
	int LITERAL___trace__ = 141;
	int LITERAL___ = 142;
	int LITERAL___label__ = 143;
	int LITERAL_inline = 144;
	int LITERAL_typeof = 145;
	int LITERAL___complex = 146;
	int LITERAL___attribute = 147;
	int LITERAL___alignof = 148;
	int LITERAL___real = 149;
	int LITERAL___imag = 150;
}
