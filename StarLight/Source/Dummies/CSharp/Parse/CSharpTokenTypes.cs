// $ANTLR : "csharp.g" -> "CSharpParser.cs"$

	using CommonAST					= antlr.CommonAST; //DDW.CSharp.Parse.LineNumberAST;
	using System.Collections;
	using System;

namespace DDW.CSharp.Parse
{
	public class CSharpTokenTypes
	{
		public const int EOF = 1;
		public const int NULL_TREE_LOOKAHEAD = 3;
		public const int CompileUnit = 4;
		public const int UsingNode = 5;
		public const int NamespaceNode = 6;
		public const int ClassNode = 7;
		public const int InterfaceNode = 8;
		public const int StructNode = 9;
		public const int EnumNode = 10;
		public const int DelegateNode = 11;
		public const int BaseTypes = 12;
		public const int BooleanLiteral = 13;
		public const int IntegerLiteral = 14;
		public const int RealLiteral = 15;
		public const int CharLiteral = 16;
		public const int StringLiteral = 17;
		public const int NullLiteral = 18;
		public const int Types = 19;
		public const int Members = 20;
		public const int MethodNode = 21;
		public const int FieldNode = 22;
		public const int PropertyNode = 23;
		public const int EventNode = 24;
		public const int ConstantNode = 25;
		public const int IndexerNode = 26;
		public const int OperatorNode = 27;
		public const int ConstructorNode = 28;
		public const int DestructorNode = 29;
		public const int AccessorNode = 30;
		public const int EnumMemberNode = 31;
		public const int Ident = 32;
		public const int QualIdent = 33;
		public const int TypeRef = 34;
		public const int BuiltInType = 35;
		public const int Args = 36;
		public const int Arg = 37;
		public const int DeclArgs = 38;
		public const int DeclArg = 39;
		public const int ArgDirection = 40;
		public const int Statements = 41;
		public const int ExprStmt = 42;
		public const int CommentNode = 43;
		public const int TryCatchFinallyStmt = 44;
		public const int TryStmt = 45;
		public const int CatchClause = 46;
		public const int FinallyStmt = 47;
		public const int IfStmt = 48;
		public const int SwitchStmt = 49;
		public const int SwitchSection = 50;
		public const int IterationStmt = 51;
		public const int InitStmt = 52;
		public const int IncStmt = 53;
		public const int ForEachStmt = 54;
		public const int GotoStmt = 55;
		public const int ReturnStmt = 56;
		public const int BreakStmt = 57;
		public const int ContinueStmt = 58;
		public const int ThrowStmt = 59;
		public const int CheckedStmt = 60;
		public const int UncheckedStmt = 61;
		public const int LockStmt = 62;
		public const int UsingStmt = 63;
		public const int LabeledStmt = 64;
		public const int VariableDeclStmt = 65;
		public const int ConstantDeclStmt = 66;
		public const int Expressions = 67;
		public const int Expression = 68;
		public const int PrimaryExpression = 69;
		public const int SubExpr = 70;
		public const int PrimitiveExpr = 71;
		public const int CastExpr = 72;
		public const int ThisRefExpr = 73;
		public const int BaseRefExpr = 74;
		public const int MemberAccessExpr = 75;
		public const int AssignExpr = 76;
		public const int UnaryExpr = 77;
		public const int BinaryExpr = 78;
		public const int TernaryExpr = 79;
		public const int ArrayCreateExpr = 80;
		public const int ObjectCreateExpr = 81;
		public const int TypeOfExpr = 82;
		public const int PostfixExpr = 83;
		public const int CheckedExpr = 84;
		public const int UncheckedExpr = 85;
		public const int InvokeExpr = 86;
		public const int IndexerExpr = 87;
		public const int ArrayRankExpr = 88;
		public const int ArrayInitExpr = 89;
		public const int Op = 90;
		public const int Declarator = 91;
		public const int CustomAttributes = 92;
		public const int CustomAttribute = 93;
		public const int ModifierAttributes = 94;
		public const int IDENTIFIER = 95;
		public const int SINGLE_LINE_COMMENT = 96;
		public const int DELIMITED_COMMENT = 97;
		public const int INTEGER_LITERAL = 98;
		public const int HEXADECIMAL_INTEGER_LITERAL = 99;
		public const int REAL_LITERAL = 100;
		public const int CHARACTER_LITERAL = 101;
		public const int STRING_LITERAL = 102;
		public const int TRUE = 103;
		public const int FALSE = 104;
		public const int NULL = 105;
		public const int DOT = 106;
		public const int OBJECT = 107;
		public const int STRING = 108;
		public const int BOOL = 109;
		public const int DECIMAL = 110;
		public const int SBYTE = 111;
		public const int BYTE = 112;
		public const int SHORT = 113;
		public const int USHORT = 114;
		public const int INT = 115;
		public const int UINT = 116;
		public const int LONG = 117;
		public const int ULONG = 118;
		public const int CHAR = 119;
		public const int FLOAT = 120;
		public const int DOUBLE = 121;
		public const int LBRACK = 122;
		public const int COMMA = 123;
		public const int RBRACK = 124;
		public const int LPAREN = 125;
		public const int RPAREN = 126;
		public const int THIS = 127;
		public const int BASE = 128;
		public const int INC = 129;
		public const int DEC = 130;
		public const int NEW = 131;
		public const int TYPEOF = 132;
		public const int VOID = 133;
		public const int CHECKED = 134;
		public const int UNCHECKED = 135;
		public const int PLUS = 136;
		public const int MINUS = 137;
		public const int LNOT = 138;
		public const int BNOT = 139;
		public const int STAR = 140;
		public const int DIV = 141;
		public const int MOD = 142;
		public const int SL = 143;
		public const int SR = 144;
		public const int LTHAN = 145;
		public const int GTHAN = 146;
		public const int LE = 147;
		public const int GE = 148;
		public const int IS = 149;
		public const int AS = 150;
		public const int EQUAL = 151;
		public const int NOT_EQUAL = 152;
		public const int BAND = 153;
		public const int BXOR = 154;
		public const int BOR = 155;
		public const int LAND = 156;
		public const int LOR = 157;
		public const int QUESTION = 158;
		public const int COLON = 159;
		public const int ASSIGN = 160;
		public const int PLUS_ASN = 161;
		public const int MINUS_ASN = 162;
		public const int STAR_ASN = 163;
		public const int DIV_ASN = 164;
		public const int MOD_ASN = 165;
		public const int BAND_ASN = 166;
		public const int BOR_ASN = 167;
		public const int BXOR_ASN = 168;
		public const int SL_ASN = 169;
		public const int SR_ASN = 170;
		public const int LBRACE = 171;
		public const int RBRACE = 172;
		public const int SEMI = 173;
		public const int CONST = 174;
		public const int IF = 175;
		public const int ELSE = 176;
		public const int SWITCH = 177;
		public const int CASE = 178;
		public const int DEFAULT = 179;
		public const int WHILE = 180;
		public const int DO = 181;
		public const int FOR = 182;
		public const int FOREACH = 183;
		public const int IN = 184;
		public const int BREAK = 185;
		public const int CONTINUE = 186;
		public const int GOTO = 187;
		public const int RETURN = 188;
		public const int THROW = 189;
		public const int LOCK = 190;
		public const int USING = 191;
		public const int TRY = 192;
		public const int CATCH = 193;
		public const int FINALLY = 194;
		public const int NAMESPACE = 195;
		public const int CLASS = 196;
		public const int PUBLIC = 197;
		public const int PROTECTED = 198;
		public const int INTERNAL = 199;
		public const int PRIVATE = 200;
		public const int ABSTRACT = 201;
		public const int SEALED = 202;
		public const int STATIC = 203;
		public const int READONLY = 204;
		public const int VOLATILE = 205;
		public const int VIRTUAL = 206;
		public const int OVERRIDE = 207;
		public const int EXTERN = 208;
		public const int PARAMS = 209;
		public const int REF = 210;
		public const int OUT = 211;
		public const int EVENT = 212;
		public const int OPERATOR = 213;
		public const int IMPLICIT = 214;
		public const int EXPLICIT = 215;
		public const int STRUCT = 216;
		public const int INTERFACE = 217;
		public const int ENUM = 218;
		public const int DELEGATE = 219;
		public const int ASSEMBLY = 220;
		public const int FIELD = 221;
		public const int METHOD = 222;
		public const int MODULE = 223;
		public const int PARAM = 224;
		public const int PROPERTY = 225;
		public const int TYPE = 226;
		public const int SIZEOF = 227;
		public const int STACKALLOC = 228;
		public const int FIXED = 229;
		public const int UNSAFE = 230;
		public const int NEW_LINE = 231;
		public const int NEW_LINE_CHARACTER = 232;
		public const int NOT_NEW_LINE = 233;
		public const int WHITESPACE = 234;
		public const int UNICODE_ESCAPE_SEQUENCE = 235;
		public const int IDENTIFIER_START_CHARACTER = 236;
		public const int IDENTIFIER_PART_CHARACTER = 237;
		public const int NUMERIC_LITERAL = 238;
		public const int DECIMAL_DIGIT = 239;
		public const int INTEGER_TYPE_SUFFIX = 240;
		public const int HEX_DIGIT = 241;
		public const int EXPONENT_PART = 242;
		public const int SIGN = 243;
		public const int REAL_TYPE_SUFFIX = 244;
		public const int CHARACTER = 245;
		public const int SINGLE_CHARACTER = 246;
		public const int SIMPLE_ESCAPE_SEQUENCE = 247;
		public const int HEXADECIMAL_ESCAPE_SEQUENCE = 248;
		public const int REGULAR_STRING_LITERAL = 249;
		public const int REGULAR_STRING_LITERAL_CHARACTER = 250;
		public const int SINGLE_REGULAR_STRING_LITERAL_CHARACTER = 251;
		public const int VERBATIM_STRING_LITERAL = 252;
		public const int BSR = 253;
		public const int BSR_ASN = 254;
		public const int HASH = 255;
		public const int QUOTE = 256;
		public const int PP_NEW_LINE = 257;
		public const int PP_WHITESPACE = 258;
		public const int PP_DIRECTIVE = 259;
		public const int PP_DECLARATION = 260;
		public const int PP_REGION = 261;
		public const int PP_MESSAGE = 262;
		public const int CONDITIONAL_SYMBOL = 263;
		public const int PPT_DEFINE = 264;
		public const int PPT_UNDEF = 265;
		public const int PPT_REGION = 266;
		public const int PPT_END_REGION = 267;
		
	}
}