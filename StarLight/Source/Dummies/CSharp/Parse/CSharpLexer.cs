// $ANTLR : "csharp.g" -> "CSharpLexer.cs"$

	using CommonAST					= antlr.CommonAST; //DDW.CSharp.Parse.LineNumberAST;
	using System.Collections;
	using System;

namespace DDW.CSharp.Parse
{
	// Generate header specific to lexer CSharp file
	using System;
	using Stream                          = System.IO.Stream;
	using TextReader                      = System.IO.TextReader;
	using Hashtable                       = System.Collections.Hashtable;
	using Comparer                        = System.Collections.Comparer;
	
	using TokenStreamException            = antlr.TokenStreamException;
	using TokenStreamIOException          = antlr.TokenStreamIOException;
	using TokenStreamRecognitionException = antlr.TokenStreamRecognitionException;
	using CharStreamException             = antlr.CharStreamException;
	using CharStreamIOException           = antlr.CharStreamIOException;
	using ANTLRException                  = antlr.ANTLRException;
	using CharScanner                     = antlr.CharScanner;
	using InputBuffer                     = antlr.InputBuffer;
	using ByteBuffer                      = antlr.ByteBuffer;
	using CharBuffer                      = antlr.CharBuffer;
	using Token                           = antlr.Token;
	using IToken                          = antlr.IToken;
	using CommonToken                     = antlr.CommonToken;
	using SemanticException               = antlr.SemanticException;
	using RecognitionException            = antlr.RecognitionException;
	using NoViableAltForCharException     = antlr.NoViableAltForCharException;
	using MismatchedCharException         = antlr.MismatchedCharException;
	using TokenStream                     = antlr.TokenStream;
	using LexerSharedInputState           = antlr.LexerSharedInputState;
	using BitSet                          = antlr.collections.impl.BitSet;
	
	public 	class CSharpLexer : antlr.CharScanner	, TokenStream
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
		
		public CSharpLexer(Stream ins) : this(new ByteBuffer(ins))
		{
		}
		
		public CSharpLexer(TextReader r) : this(new CharBuffer(r))
		{
		}
		
		public CSharpLexer(InputBuffer ib)		 : this(new LexerSharedInputState(ib))
		{
		}
		
		public CSharpLexer(LexerSharedInputState state) : base(state)
		{
			initialize();
		}
		private void initialize()
		{
			caseSensitiveLiterals = true;
			setCaseSensitive(true);
			literals = new Hashtable(100, (float)0.4);
			literals.Add("abstract", 201);
			literals.Add("void", 133);
			literals.Add("static", 203);
			literals.Add("stackalloc", 228);
			literals.Add("ref", 210);
			literals.Add("break", 185);
			literals.Add("readonly", 204);
			literals.Add("continue", 186);
			literals.Add("catch", 193);
			literals.Add("for", 182);
			literals.Add("else", 176);
			literals.Add("is", 149);
			literals.Add("byte", 112);
			literals.Add("interface", 217);
			literals.Add("as", 150);
			literals.Add("float", 120);
			literals.Add("typeof", 132);
			literals.Add("foreach", 183);
			literals.Add("uint", 116);
			literals.Add("method", 222);
			literals.Add("explicit", 215);
			literals.Add("private", 200);
			literals.Add("param", 224);
			literals.Add("throw", 189);
			literals.Add("ulong", 118);
			literals.Add("short", 113);
			literals.Add("long", 117);
			literals.Add("try", 192);
			literals.Add("in", 184);
			literals.Add("internal", 199);
			literals.Add("operator", 213);
			literals.Add("switch", 177);
			literals.Add("sbyte", 111);
			literals.Add("assembly", 220);
			literals.Add("virtual", 206);
			literals.Add("this", 127);
			literals.Add("ushort", 114);
			literals.Add("null", 105);
			literals.Add("decimal", 110);
			literals.Add("fixed", 229);
			literals.Add("object", 107);
			literals.Add("public", 197);
			literals.Add("delegate", 219);
			literals.Add("false", 104);
			literals.Add("case", 178);
			literals.Add("implicit", 214);
			literals.Add("true", 103);
			literals.Add("bool", 109);
			literals.Add("do", 181);
			literals.Add("checked", 134);
			literals.Add("base", 128);
			literals.Add("override", 207);
			literals.Add("enum", 218);
			literals.Add("field", 221);
			literals.Add("protected", 198);
			literals.Add("finally", 194);
			literals.Add("if", 175);
			literals.Add("char", 119);
			literals.Add("const", 174);
			literals.Add("unchecked", 135);
			literals.Add("string", 108);
			literals.Add("module", 223);
			literals.Add("return", 188);
			literals.Add("default", 179);
			literals.Add("new", 131);
			literals.Add("event", 212);
			literals.Add("int", 115);
			literals.Add("unsafe", 230);
			literals.Add("params", 209);
			literals.Add("property", 225);
			literals.Add("goto", 187);
			literals.Add("using", 191);
			literals.Add("class", 196);
			literals.Add("sizeof", 227);
			literals.Add("namespace", 195);
			literals.Add("struct", 216);
			literals.Add("sealed", 202);
			literals.Add("while", 180);
			literals.Add("extern", 208);
			literals.Add("type", 226);
			literals.Add("double", 121);
			literals.Add("lock", 190);
			literals.Add("out", 211);
		}
		
		override public IToken nextToken()			//throws TokenStreamException
		{
			IToken theRetToken = null;
tryAgain:
			for (;;)
			{
				IToken _token = null;
				int _ttype = Token.INVALID_TYPE;
				resetText();
				try     // for char stream error handling
				{
					try     // for lexical error handling
					{
						switch ( cached_LA1 )
						{
						case '\t':  case '\n':  case '\u000b':  case '\u000c':
						case '\r':  case ' ':  case '\u2028':  case '\u2029':
						{
							mWHITESPACE(true);
							theRetToken = returnToken_;
							break;
						}
						case '\\':
						{
							mUNICODE_ESCAPE_SEQUENCE(true);
							theRetToken = returnToken_;
							break;
						}
						case '$':  case 'A':  case 'B':  case 'C':
						case 'D':  case 'E':  case 'F':  case 'G':
						case 'H':  case 'I':  case 'J':  case 'K':
						case 'L':  case 'M':  case 'N':  case 'O':
						case 'P':  case 'Q':  case 'R':  case 'S':
						case 'T':  case 'U':  case 'V':  case 'W':
						case 'X':  case 'Y':  case 'Z':  case '_':
						case 'a':  case 'b':  case 'c':  case 'd':
						case 'e':  case 'f':  case 'g':  case 'h':
						case 'i':  case 'j':  case 'k':  case 'l':
						case 'm':  case 'n':  case 'o':  case 'p':
						case 'q':  case 'r':  case 's':  case 't':
						case 'u':  case 'v':  case 'w':  case 'x':
						case 'y':  case 'z':
						{
							mIDENTIFIER(true);
							theRetToken = returnToken_;
							break;
						}
						case '\'':
						{
							mCHARACTER_LITERAL(true);
							theRetToken = returnToken_;
							break;
						}
						case '{':
						{
							mLBRACE(true);
							theRetToken = returnToken_;
							break;
						}
						case '}':
						{
							mRBRACE(true);
							theRetToken = returnToken_;
							break;
						}
						case '[':
						{
							mLBRACK(true);
							theRetToken = returnToken_;
							break;
						}
						case ']':
						{
							mRBRACK(true);
							theRetToken = returnToken_;
							break;
						}
						case '(':
						{
							mLPAREN(true);
							theRetToken = returnToken_;
							break;
						}
						case ')':
						{
							mRPAREN(true);
							theRetToken = returnToken_;
							break;
						}
						case '~':
						{
							mBNOT(true);
							theRetToken = returnToken_;
							break;
						}
						case ',':
						{
							mCOMMA(true);
							theRetToken = returnToken_;
							break;
						}
						case ':':
						{
							mCOLON(true);
							theRetToken = returnToken_;
							break;
						}
						case ';':
						{
							mSEMI(true);
							theRetToken = returnToken_;
							break;
						}
						case '?':
						{
							mQUESTION(true);
							theRetToken = returnToken_;
							break;
						}
						default:
							if ((cached_LA1=='>') && (cached_LA2=='>') && (LA(3)=='>') && (LA(4)=='='))
							{
								mBSR_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='<') && (cached_LA2=='<') && (LA(3)=='=')) {
								mSL_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='>') && (cached_LA2=='>') && (LA(3)=='=')) {
								mSR_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='>') && (cached_LA2=='>') && (LA(3)=='>') && (true)) {
								mBSR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='/') && (cached_LA2=='/')) {
								mSINGLE_LINE_COMMENT(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='/') && (cached_LA2=='*')) {
								mDELIMITED_COMMENT(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='0') && (cached_LA2=='X'||cached_LA2=='x')) {
								mHEXADECIMAL_INTEGER_LITERAL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='"'||cached_LA1=='@') && (tokenSet_0_.member(cached_LA2))) {
								mSTRING_LITERAL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='+') && (cached_LA2=='=')) {
								mPLUS_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='-') && (cached_LA2=='=')) {
								mMINUS_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='*') && (cached_LA2=='=')) {
								mSTAR_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='/') && (cached_LA2=='=')) {
								mDIV_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='%') && (cached_LA2=='=')) {
								mMOD_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='+') && (cached_LA2=='+')) {
								mINC(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='-') && (cached_LA2=='-')) {
								mDEC(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='<') && (cached_LA2=='<') && (true)) {
								mSL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='>') && (cached_LA2=='>') && (true)) {
								mSR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='&') && (cached_LA2=='=')) {
								mBAND_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='|') && (cached_LA2=='=')) {
								mBOR_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='^') && (cached_LA2=='=')) {
								mBXOR_ASN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='=') && (cached_LA2=='=')) {
								mEQUAL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='<') && (cached_LA2=='=')) {
								mLE(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='>') && (cached_LA2=='=')) {
								mGE(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='!') && (cached_LA2=='=')) {
								mNOT_EQUAL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='|') && (cached_LA2=='|')) {
								mLOR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='&') && (cached_LA2=='&')) {
								mLAND(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='#') && (tokenSet_1_.member(cached_LA2))) {
								mPP_DIRECTIVE(true);
								theRetToken = returnToken_;
							}
							else if ((tokenSet_2_.member(cached_LA1)) && (true)) {
								mNUMERIC_LITERAL(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='+') && (true)) {
								mPLUS(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='-') && (true)) {
								mMINUS(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='*') && (true)) {
								mSTAR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='/') && (true)) {
								mDIV(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='%') && (true)) {
								mMOD(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='&') && (true)) {
								mBAND(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='|') && (true)) {
								mBOR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='^') && (true)) {
								mBXOR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='=') && (true)) {
								mASSIGN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='<') && (true)) {
								mLTHAN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='>') && (true)) {
								mGTHAN(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='!') && (true)) {
								mLNOT(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='#') && (true)) {
								mHASH(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='"') && (true)) {
								mQUOTE(true);
								theRetToken = returnToken_;
							}
						else
						{
							if (cached_LA1==EOF_CHAR) { uponEOF(); returnToken_ = makeToken(Token.EOF_TYPE); }
				else {throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());}
						}
						break; }
						if ( null==returnToken_ ) goto tryAgain; // found SKIP token
						_ttype = returnToken_.Type;
						returnToken_.Type = _ttype;
						return returnToken_;
					}
					catch (RecognitionException e) {
							throw new TokenStreamRecognitionException(e);
					}
				}
				catch (CharStreamException cse) {
					if ( cse is CharStreamIOException ) {
						throw new TokenStreamIOException(((CharStreamIOException)cse).io);
					}
					else {
						throw new TokenStreamException(cse.Message);
					}
				}
			}
		}
		
	protected void mNEW_LINE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NEW_LINE;
		
		{
			switch ( cached_LA1 )
			{
			case '\n':
			{
				match('\u000A');
				break;
			}
			case '\u2028':
			{
				match('\u2028');
				break;
			}
			case '\u2029':
			{
				match('\u2029');
				break;
			}
			default:
				if (((cached_LA1=='\r') && (cached_LA2=='\n') && (true) && (true))&&( LA(2)=='\u000A' ))
				{
					match('\u000D');
					match('\u000A');
				}
				else if ((cached_LA1=='\r') && (true) && (true) && (true)) {
					match('\u000D');
				}
			else
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			break; }
		}
		if (0==inputState.guessing)
		{
			newline();
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mNEW_LINE_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NEW_LINE_CHARACTER;
		
		{
			switch ( cached_LA1 )
			{
			case '\r':
			{
				match('\u000D');
				break;
			}
			case '\n':
			{
				match('\u000A');
				break;
			}
			case '\u2028':
			{
				match('\u2028');
				break;
			}
			case '\u2029':
			{
				match('\u2029');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mNOT_NEW_LINE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NOT_NEW_LINE;
		
		{
			match(tokenSet_0_);
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mWHITESPACE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = WHITESPACE;
		
		{ // ( ... )+
			int _cnt527=0;
			for (;;)
			{
				switch ( cached_LA1 )
				{
				case ' ':
				{
					match(' ');
					break;
				}
				case '\t':
				{
					match('\u0009');
					break;
				}
				case '\u000b':
				{
					match('\u000B');
					break;
				}
				case '\u000c':
				{
					match('\u000C');
					break;
				}
				case '\n':  case '\r':  case '\u2028':  case '\u2029':
				{
					mNEW_LINE(false);
					break;
				}
				default:
				{
					if (_cnt527 >= 1) { goto _loop527_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
				}
				break; }
				_cnt527++;
			}
_loop527_breakloop:			;
		}    // ( ... )+
		if (0==inputState.guessing)
		{
			_ttype = Token.SKIP;
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSINGLE_LINE_COMMENT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SINGLE_LINE_COMMENT;
		
		match("//");
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_0_.member(cached_LA1)))
				{
					mNOT_NEW_LINE(false);
				}
				else
				{
					goto _loop530_breakloop;
				}
				
			}
_loop530_breakloop:			;
		}    // ( ... )*
		{
			if ((tokenSet_3_.member(cached_LA1)))
			{
				mNEW_LINE(false);
			}
			else {
			}
			
		}
		if (0==inputState.guessing)
		{
			_ttype = Token.SKIP;
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mDELIMITED_COMMENT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DELIMITED_COMMENT;
		
		match("/*");
		{    // ( ... )*
			for (;;)
			{
				if (((cached_LA1=='*') && ((cached_LA2 >= '\u0003' && cached_LA2 <= '\u7fff')) && ((LA(3) >= '\u0003' && LA(3) <= '\u7fff')))&&( LA(2)!='/' ))
				{
					match('*');
				}
				else if ((tokenSet_3_.member(cached_LA1))) {
					mNEW_LINE(false);
				}
				else if ((tokenSet_4_.member(cached_LA1))) {
					{
						match(tokenSet_4_);
					}
				}
				else
				{
					goto _loop535_breakloop;
				}
				
			}
_loop535_breakloop:			;
		}    // ( ... )*
		match("*/");
		if (0==inputState.guessing)
		{
			_ttype = Token.SKIP;
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mUNICODE_ESCAPE_SEQUENCE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = UNICODE_ESCAPE_SEQUENCE;
		
		if ((cached_LA1=='\\') && (cached_LA2=='u'))
		{
			{
				match('\\');
				match('u');
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
			}
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='U')) {
			{
				match('\\');
				match('U');
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
				mHEX_DIGIT(false);
			}
		}
		else
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mHEX_DIGIT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = HEX_DIGIT;
		
		switch ( cached_LA1 )
		{
		case '0':
		{
			match('0');
			break;
		}
		case '1':
		{
			match('1');
			break;
		}
		case '2':
		{
			match('2');
			break;
		}
		case '3':
		{
			match('3');
			break;
		}
		case '4':
		{
			match('4');
			break;
		}
		case '5':
		{
			match('5');
			break;
		}
		case '6':
		{
			match('6');
			break;
		}
		case '7':
		{
			match('7');
			break;
		}
		case '8':
		{
			match('8');
			break;
		}
		case '9':
		{
			match('9');
			break;
		}
		case 'A':
		{
			match('A');
			break;
		}
		case 'B':
		{
			match('B');
			break;
		}
		case 'C':
		{
			match('C');
			break;
		}
		case 'D':
		{
			match('D');
			break;
		}
		case 'E':
		{
			match('E');
			break;
		}
		case 'F':
		{
			match('F');
			break;
		}
		case 'a':
		{
			match('a');
			break;
		}
		case 'b':
		{
			match('b');
			break;
		}
		case 'c':
		{
			match('c');
			break;
		}
		case 'd':
		{
			match('d');
			break;
		}
		case 'e':
		{
			match('e');
			break;
		}
		case 'f':
		{
			match('f');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mIDENTIFIER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = IDENTIFIER;
		
		mIDENTIFIER_START_CHARACTER(false);
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_5_.member(cached_LA1)))
				{
					mIDENTIFIER_PART_CHARACTER(false);
				}
				else
				{
					goto _loop541_breakloop;
				}
				
			}
_loop541_breakloop:			;
		}    // ( ... )*
		_ttype = testLiteralsTable(_ttype);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mIDENTIFIER_START_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = IDENTIFIER_START_CHARACTER;
		
		{
			switch ( cached_LA1 )
			{
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mIDENTIFIER_PART_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = IDENTIFIER_PART_CHARACTER;
		
		{
			switch ( cached_LA1 )
			{
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mNUMERIC_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NUMERIC_LITERAL;
		
		bool synPredMatched556 = false;
		if ((((cached_LA1 >= '0' && cached_LA1 <= '9')) && (tokenSet_2_.member(cached_LA2)) && (tokenSet_2_.member(LA(3))) && (true)))
		{
			int _m556 = mark();
			synPredMatched556 = true;
			inputState.guessing++;
			try {
				{
					{ // ( ... )+
						int _cnt555=0;
						for (;;)
						{
							if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
							{
								mDECIMAL_DIGIT(false);
							}
							else
							{
								if (_cnt555 >= 1) { goto _loop555_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
							}
							
							_cnt555++;
						}
_loop555_breakloop:						;
					}    // ( ... )+
					match('.');
					mDECIMAL_DIGIT(false);
				}
			}
			catch (RecognitionException)
			{
				synPredMatched556 = false;
			}
			rewind(_m556);
			inputState.guessing--;
		}
		if ( synPredMatched556 )
		{
			{ // ( ... )+
				int _cnt558=0;
				for (;;)
				{
					if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
					{
						mDECIMAL_DIGIT(false);
					}
					else
					{
						if (_cnt558 >= 1) { goto _loop558_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt558++;
				}
_loop558_breakloop:				;
			}    // ( ... )+
			match('.');
			{ // ( ... )+
				int _cnt560=0;
				for (;;)
				{
					if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
					{
						mDECIMAL_DIGIT(false);
					}
					else
					{
						if (_cnt560 >= 1) { goto _loop560_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt560++;
				}
_loop560_breakloop:				;
			}    // ( ... )+
			{
				if ((cached_LA1=='E'||cached_LA1=='e'))
				{
					mEXPONENT_PART(false);
				}
				else {
				}
				
			}
			{
				if ((tokenSet_6_.member(cached_LA1)))
				{
					mREAL_TYPE_SUFFIX(false);
				}
				else {
				}
				
			}
			if (0==inputState.guessing)
			{
				_ttype = REAL_LITERAL;
			}
		}
		else {
			bool synPredMatched567 = false;
			if ((((cached_LA1 >= '0' && cached_LA1 <= '9')) && (tokenSet_7_.member(cached_LA2)) && (tokenSet_8_.member(LA(3))) && (true)))
			{
				int _m567 = mark();
				synPredMatched567 = true;
				inputState.guessing++;
				try {
					{
						{ // ( ... )+
							int _cnt565=0;
							for (;;)
							{
								if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
								{
									mDECIMAL_DIGIT(false);
								}
								else
								{
									if (_cnt565 >= 1) { goto _loop565_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
								}
								
								_cnt565++;
							}
_loop565_breakloop:							;
						}    // ( ... )+
						{
							mEXPONENT_PART(false);
						}
					}
				}
				catch (RecognitionException)
				{
					synPredMatched567 = false;
				}
				rewind(_m567);
				inputState.guessing--;
			}
			if ( synPredMatched567 )
			{
				{ // ( ... )+
					int _cnt569=0;
					for (;;)
					{
						if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
						{
							mDECIMAL_DIGIT(false);
						}
						else
						{
							if (_cnt569 >= 1) { goto _loop569_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
						}
						
						_cnt569++;
					}
_loop569_breakloop:					;
				}    // ( ... )+
				{
					mEXPONENT_PART(false);
				}
				{
					if ((tokenSet_6_.member(cached_LA1)))
					{
						mREAL_TYPE_SUFFIX(false);
					}
					else {
					}
					
				}
				if (0==inputState.guessing)
				{
					_ttype = REAL_LITERAL;
				}
			}
			else {
				bool synPredMatched548 = false;
				if (((cached_LA1=='.') && ((cached_LA2 >= '0' && cached_LA2 <= '9'))))
				{
					int _m548 = mark();
					synPredMatched548 = true;
					inputState.guessing++;
					try {
						{
							match('.');
							mDECIMAL_DIGIT(false);
						}
					}
					catch (RecognitionException)
					{
						synPredMatched548 = false;
					}
					rewind(_m548);
					inputState.guessing--;
				}
				if ( synPredMatched548 )
				{
					match('.');
					{ // ( ... )+
						int _cnt550=0;
						for (;;)
						{
							if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
							{
								mDECIMAL_DIGIT(false);
							}
							else
							{
								if (_cnt550 >= 1) { goto _loop550_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
							}
							
							_cnt550++;
						}
_loop550_breakloop:						;
					}    // ( ... )+
					{
						if ((cached_LA1=='E'||cached_LA1=='e'))
						{
							mEXPONENT_PART(false);
						}
						else {
						}
						
					}
					{
						if ((tokenSet_6_.member(cached_LA1)))
						{
							mREAL_TYPE_SUFFIX(false);
						}
						else {
						}
						
					}
					if (0==inputState.guessing)
					{
						_ttype = REAL_LITERAL;
					}
				}
				else {
					bool synPredMatched576 = false;
					if ((((cached_LA1 >= '0' && cached_LA1 <= '9')) && (tokenSet_9_.member(cached_LA2)) && (true) && (true)))
					{
						int _m576 = mark();
						synPredMatched576 = true;
						inputState.guessing++;
						try {
							{
								{ // ( ... )+
									int _cnt574=0;
									for (;;)
									{
										if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
										{
											mDECIMAL_DIGIT(false);
										}
										else
										{
											if (_cnt574 >= 1) { goto _loop574_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
										}
										
										_cnt574++;
									}
_loop574_breakloop:									;
								}    // ( ... )+
								{
									mREAL_TYPE_SUFFIX(false);
								}
							}
						}
						catch (RecognitionException)
						{
							synPredMatched576 = false;
						}
						rewind(_m576);
						inputState.guessing--;
					}
					if ( synPredMatched576 )
					{
						{ // ( ... )+
							int _cnt578=0;
							for (;;)
							{
								if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
								{
									mDECIMAL_DIGIT(false);
								}
								else
								{
									if (_cnt578 >= 1) { goto _loop578_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
								}
								
								_cnt578++;
							}
_loop578_breakloop:							;
						}    // ( ... )+
						{
							mREAL_TYPE_SUFFIX(false);
						}
						if (0==inputState.guessing)
						{
							_ttype = REAL_LITERAL;
						}
					}
					else if (((cached_LA1 >= '0' && cached_LA1 <= '9')) && (true) && (true) && (true)) {
						{ // ( ... )+
							int _cnt581=0;
							for (;;)
							{
								if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
								{
									mDECIMAL_DIGIT(false);
								}
								else
								{
									if (_cnt581 >= 1) { goto _loop581_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
								}
								
								_cnt581++;
							}
_loop581_breakloop:							;
						}    // ( ... )+
						{
							if ((tokenSet_10_.member(cached_LA1)))
							{
								mINTEGER_TYPE_SUFFIX(false);
							}
							else {
							}
							
						}
						if (0==inputState.guessing)
						{
							_ttype = INTEGER_LITERAL;
						}
					}
					else if ((cached_LA1=='.') && (true)) {
						match('.');
						if (0==inputState.guessing)
						{
							_ttype = DOT;
						}
					}
					else
					{
						throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
					}
					}}}
					if (_createToken && (null == _token) && (_ttype != Token.SKIP))
					{
						_token = makeToken(_ttype);
						_token.setText(text.ToString(_begin, text.Length-_begin));
					}
					returnToken_ = _token;
				}
				
	protected void mDECIMAL_DIGIT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DECIMAL_DIGIT;
		
		switch ( cached_LA1 )
		{
		case '0':
		{
			match('0');
			break;
		}
		case '1':
		{
			match('1');
			break;
		}
		case '2':
		{
			match('2');
			break;
		}
		case '3':
		{
			match('3');
			break;
		}
		case '4':
		{
			match('4');
			break;
		}
		case '5':
		{
			match('5');
			break;
		}
		case '6':
		{
			match('6');
			break;
		}
		case '7':
		{
			match('7');
			break;
		}
		case '8':
		{
			match('8');
			break;
		}
		case '9':
		{
			match('9');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mEXPONENT_PART(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = EXPONENT_PART;
		
		switch ( cached_LA1 )
		{
		case 'e':
		{
			match("e");
			{    // ( ... )*
				for (;;)
				{
					if ((cached_LA1=='+'||cached_LA1=='-'))
					{
						mSIGN(false);
					}
					else
					{
						goto _loop598_breakloop;
					}
					
				}
_loop598_breakloop:				;
			}    // ( ... )*
			{ // ( ... )+
				int _cnt600=0;
				for (;;)
				{
					if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
					{
						mDECIMAL_DIGIT(false);
					}
					else
					{
						if (_cnt600 >= 1) { goto _loop600_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt600++;
				}
_loop600_breakloop:				;
			}    // ( ... )+
			break;
		}
		case 'E':
		{
			match("E");
			{    // ( ... )*
				for (;;)
				{
					if ((cached_LA1=='+'||cached_LA1=='-'))
					{
						mSIGN(false);
					}
					else
					{
						goto _loop602_breakloop;
					}
					
				}
_loop602_breakloop:				;
			}    // ( ... )*
			{ // ( ... )+
				int _cnt604=0;
				for (;;)
				{
					if (((cached_LA1 >= '0' && cached_LA1 <= '9')))
					{
						mDECIMAL_DIGIT(false);
					}
					else
					{
						if (_cnt604 >= 1) { goto _loop604_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt604++;
				}
_loop604_breakloop:				;
			}    // ( ... )+
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mREAL_TYPE_SUFFIX(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = REAL_TYPE_SUFFIX;
		
		switch ( cached_LA1 )
		{
		case 'F':
		{
			match('F');
			break;
		}
		case 'f':
		{
			match('f');
			break;
		}
		case 'D':
		{
			match('D');
			break;
		}
		case 'd':
		{
			match('d');
			break;
		}
		case 'M':
		{
			match('M');
			break;
		}
		case 'm':
		{
			match('m');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mINTEGER_TYPE_SUFFIX(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = INTEGER_TYPE_SUFFIX;
		
		{
			if ((cached_LA1=='U') && (cached_LA2=='L') && (true) && (true))
			{
				match("UL");
			}
			else if ((cached_LA1=='L') && (cached_LA2=='U') && (true) && (true)) {
				match("LU");
			}
			else if ((cached_LA1=='u') && (cached_LA2=='l')) {
				match("ul");
			}
			else if ((cached_LA1=='l') && (cached_LA2=='u')) {
				match("lu");
			}
			else if ((cached_LA1=='U') && (cached_LA2=='L') && (true) && (true)) {
				match("UL");
			}
			else if ((cached_LA1=='L') && (cached_LA2=='U') && (true) && (true)) {
				match("LU");
			}
			else if ((cached_LA1=='u') && (cached_LA2=='L')) {
				match("uL");
			}
			else if ((cached_LA1=='l') && (cached_LA2=='U')) {
				match("lU");
			}
			else if ((cached_LA1=='U') && (true)) {
				match("U");
			}
			else if ((cached_LA1=='L') && (true)) {
				match("L");
			}
			else if ((cached_LA1=='u') && (true)) {
				match("u");
			}
			else if ((cached_LA1=='l') && (true)) {
				match("l");
			}
			else
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mHEXADECIMAL_INTEGER_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = HEXADECIMAL_INTEGER_LITERAL;
		
		if ((cached_LA1=='0') && (cached_LA2=='x'))
		{
			match("0x");
			{ // ( ... )+
				int _cnt585=0;
				for (;;)
				{
					if ((tokenSet_11_.member(cached_LA1)))
					{
						mHEX_DIGIT(false);
					}
					else
					{
						if (_cnt585 >= 1) { goto _loop585_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt585++;
				}
_loop585_breakloop:				;
			}    // ( ... )+
			{
				if ((tokenSet_10_.member(cached_LA1)))
				{
					mINTEGER_TYPE_SUFFIX(false);
				}
				else {
				}
				
			}
		}
		else if ((cached_LA1=='0') && (cached_LA2=='X')) {
			match("0X");
			{ // ( ... )+
				int _cnt588=0;
				for (;;)
				{
					if ((tokenSet_11_.member(cached_LA1)))
					{
						mHEX_DIGIT(false);
					}
					else
					{
						if (_cnt588 >= 1) { goto _loop588_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					
					_cnt588++;
				}
_loop588_breakloop:				;
			}    // ( ... )+
			{
				if ((tokenSet_10_.member(cached_LA1)))
				{
					mINTEGER_TYPE_SUFFIX(false);
				}
				else {
				}
				
			}
		}
		else
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mCHARACTER_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = CHARACTER_LITERAL;
		
		int _saveIndex = 0;
		_saveIndex = text.Length;
		match("'");
		text.Length = _saveIndex;
		mCHARACTER(false);
		_saveIndex = text.Length;
		match("'");
		text.Length = _saveIndex;
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mCHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = CHARACTER;
		
		if ((cached_LA1=='\\') && (tokenSet_12_.member(cached_LA2)))
		{
			mSIMPLE_ESCAPE_SEQUENCE(false);
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='x')) {
			mHEXADECIMAL_ESCAPE_SEQUENCE(false);
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='U'||cached_LA2=='u')) {
			mUNICODE_ESCAPE_SEQUENCE(false);
		}
		else if ((tokenSet_13_.member(cached_LA1))) {
			mSINGLE_CHARACTER(false);
		}
		else
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSTRING_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = STRING_LITERAL;
		
		switch ( cached_LA1 )
		{
		case '"':
		{
			mREGULAR_STRING_LITERAL(false);
			break;
		}
		case '@':
		{
			mVERBATIM_STRING_LITERAL(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mREGULAR_STRING_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = REGULAR_STRING_LITERAL;
		IToken rs = null;
		
		int _saveIndex = 0;
		_saveIndex = text.Length;
		match('\"');
		text.Length = _saveIndex;
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_14_.member(cached_LA1)))
				{
					mREGULAR_STRING_LITERAL_CHARACTER(true);
					rs = returnToken_;
				}
				else
				{
					goto _loop618_breakloop;
				}
				
			}
_loop618_breakloop:			;
		}    // ( ... )*
		_saveIndex = text.Length;
		match('\"');
		text.Length = _saveIndex;
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mVERBATIM_STRING_LITERAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = VERBATIM_STRING_LITERAL;
		char  ch = '\0';
		string s="";
		
		match('@');
		match("\"");
		{    // ( ... )*
			for (;;)
			{
				if ((cached_LA1=='"') && (cached_LA2=='"'))
				{
					match("\"\"");
					if (0==inputState.guessing)
					{
						s+=("\"");
					}
				}
				else if ((cached_LA1=='\\')) {
					match("\\");
					if (0==inputState.guessing)
					{
						s+=("\\\\");
					}
				}
				else if ((tokenSet_15_.member(cached_LA1))) {
					{
						ch = cached_LA1;
						match(tokenSet_15_);
					}
					if (0==inputState.guessing)
					{
						s+=(ch);
					}
				}
				else
				{
					goto _loop625_breakloop;
				}
				
			}
_loop625_breakloop:			;
		}    // ( ... )*
		match("\"");
		if (0==inputState.guessing)
		{
			text.Length = _begin; text.Append(s);
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mSIGN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SIGN;
		
		switch ( cached_LA1 )
		{
		case '+':
		{
			match('+');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		 }
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mSINGLE_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SINGLE_CHARACTER;
		
		{
			match(tokenSet_13_);
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mSIMPLE_ESCAPE_SEQUENCE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SIMPLE_ESCAPE_SEQUENCE;
		
		if ((cached_LA1=='\\') && (cached_LA2=='\''))
		{
			match("\\'");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='"')) {
			match("\\\"");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='\\')) {
			match("\\\\");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='0')) {
			match("\\0");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='a')) {
			match("\\a");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='b')) {
			match("\\b");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='f')) {
			match("\\f");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='n')) {
			match("\\n");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='r')) {
			match("\\r");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='t')) {
			match("\\t");
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='v')) {
			match("\\v");
		}
		else
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mHEXADECIMAL_ESCAPE_SEQUENCE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = HEXADECIMAL_ESCAPE_SEQUENCE;
		
		{
			match('\\');
			match('x');
			mHEX_DIGIT(false);
		}
		{
			if ((tokenSet_11_.member(cached_LA1)) && (tokenSet_0_.member(cached_LA2)) && (true) && (true))
			{
				mHEX_DIGIT(false);
				{
					if ((tokenSet_11_.member(cached_LA1)) && (tokenSet_0_.member(cached_LA2)) && (true) && (true))
					{
						mHEX_DIGIT(false);
						{
							if ((tokenSet_11_.member(cached_LA1)) && (tokenSet_0_.member(cached_LA2)) && (true) && (true))
							{
								mHEX_DIGIT(false);
							}
							else if ((tokenSet_0_.member(cached_LA1)) && (true) && (true) && (true)) {
							}
							else
							{
								throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
							}
							
						}
					}
					else if ((tokenSet_0_.member(cached_LA1)) && (true) && (true) && (true)) {
					}
					else
					{
						throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
					}
					
				}
			}
			else if ((tokenSet_0_.member(cached_LA1)) && (true) && (true) && (true)) {
			}
			else
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mREGULAR_STRING_LITERAL_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = REGULAR_STRING_LITERAL_CHARACTER;
		
		if ((cached_LA1=='\\') && (tokenSet_12_.member(cached_LA2)))
		{
			mSIMPLE_ESCAPE_SEQUENCE(false);
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='x')) {
			mHEXADECIMAL_ESCAPE_SEQUENCE(false);
		}
		else if ((cached_LA1=='\\') && (cached_LA2=='U'||cached_LA2=='u')) {
			mUNICODE_ESCAPE_SEQUENCE(false);
		}
		else if ((tokenSet_16_.member(cached_LA1))) {
			mSINGLE_REGULAR_STRING_LITERAL_CHARACTER(false);
		}
		else
		{
			throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
		}
		
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mSINGLE_REGULAR_STRING_LITERAL_CHARACTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SINGLE_REGULAR_STRING_LITERAL_CHARACTER;
		
		{
			match(tokenSet_16_);
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLBRACE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LBRACE;
		
		match('{');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRBRACE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RBRACE;
		
		match('}');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLBRACK(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LBRACK;
		
		match('[');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRBRACK(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RBRACK;
		
		match(']');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLPAREN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LPAREN;
		
		match('(');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRPAREN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RPAREN;
		
		match(')');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mPLUS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PLUS;
		
		match('+');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mPLUS_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PLUS_ASN;
		
		match("+=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mMINUS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = MINUS;
		
		match('-');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mMINUS_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = MINUS_ASN;
		
		match("-=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSTAR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = STAR;
		
		match('*');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSTAR_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = STAR_ASN;
		
		match("*=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mDIV(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DIV;
		
		match('/');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mDIV_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DIV_ASN;
		
		match("/=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mMOD(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = MOD;
		
		match('%');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mMOD_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = MOD_ASN;
		
		match("%=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mINC(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = INC;
		
		match("++");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mDEC(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DEC;
		
		match("--");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SL;
		
		match("<<");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSL_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SL_ASN;
		
		match("<<=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SR;
		
		match(">>");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSR_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SR_ASN;
		
		match(">>=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBSR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BSR;
		
		match(">>>");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBSR_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BSR_ASN;
		
		match(">>>=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBAND(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BAND;
		
		match('&');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBAND_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BAND_ASN;
		
		match("&=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBOR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BOR;
		
		match('|');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBOR_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BOR_ASN;
		
		match("|=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBXOR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BXOR;
		
		match('^');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBXOR_ASN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BXOR_ASN;
		
		match("^=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mBNOT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = BNOT;
		
		match('~');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mASSIGN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = ASSIGN;
		
		match('=');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mEQUAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = EQUAL;
		
		match("==");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLTHAN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LTHAN;
		
		match('<');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LE;
		
		match("<=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mGTHAN(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = GTHAN;
		
		match(">");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mGE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = GE;
		
		match(">=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLNOT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LNOT;
		
		match('!');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mNOT_EQUAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NOT_EQUAL;
		
		match("!=");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLOR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LOR;
		
		match("||");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLAND(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LAND;
		
		match("&&");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mCOMMA(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = COMMA;
		
		match(',');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mCOLON(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = COLON;
		
		match(':');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSEMI(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SEMI;
		
		match(';');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mHASH(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = HASH;
		
		match("#");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mQUOTE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = QUOTE;
		
		match("\"");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mQUESTION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = QUESTION;
		
		match('?');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPP_NEW_LINE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_NEW_LINE;
		
		{
			switch ( cached_LA1 )
			{
			case '/':
			{
				mSINGLE_LINE_COMMENT(false);
				break;
			}
			case '\n':  case '\r':  case '\u2028':  case '\u2029':
			{
				mNEW_LINE_CHARACTER(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPP_WHITESPACE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_WHITESPACE;
		
		{ // ( ... )+
			int _cnt677=0;
			for (;;)
			{
				switch ( cached_LA1 )
				{
				case ' ':
				{
					match(' ');
					break;
				}
				case '\t':
				{
					match('\u0009');
					break;
				}
				case '\u000b':
				{
					match('\u000B');
					break;
				}
				case '\u000c':
				{
					match('\u000C');
					break;
				}
				default:
				{
					if (_cnt677 >= 1) { goto _loop677_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
				}
				break; }
				_cnt677++;
			}
_loop677_breakloop:			;
		}    // ( ... )+
		if (0==inputState.guessing)
		{
			_ttype = Token.SKIP;
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mPP_DIRECTIVE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_DIRECTIVE;
		IToken dc = null;
		IToken rg = null;
		
		mHASH(false);
		{
			switch ( cached_LA1 )
			{
			case '\t':  case '\u000b':  case '\u000c':  case ' ':
			{
				mPP_WHITESPACE(false);
				break;
			}
			case 'd':  case 'e':  case 'r':  case 'u':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		{
			switch ( cached_LA1 )
			{
			case 'd':  case 'u':
			{
				mPP_DECLARATION(true);
				dc = returnToken_;
				if (0==inputState.guessing)
				{
					Console.Write("===>decl: "+dc.getText());
				}
				break;
			}
			case 'e':  case 'r':
			{
				mPP_REGION(true);
				rg = returnToken_;
				if (0==inputState.guessing)
				{
					Console.Write("===>regn: "+rg.getText());
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		if (0==inputState.guessing)
		{
			_ttype = Token.SKIP;
		}
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPP_DECLARATION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_DECLARATION;
		
		{
			switch ( cached_LA1 )
			{
			case 'd':
			{
				mPPT_DEFINE(false);
				break;
			}
			case 'u':
			{
				mPPT_UNDEF(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		{
			switch ( cached_LA1 )
			{
			case '\t':  case '\u000b':  case '\u000c':  case ' ':
			{
				mPP_WHITESPACE(false);
				break;
			}
			case '$':  case 'A':  case 'B':  case 'C':
			case 'D':  case 'E':  case 'F':  case 'G':
			case 'H':  case 'I':  case 'J':  case 'K':
			case 'L':  case 'M':  case 'N':  case 'O':
			case 'P':  case 'Q':  case 'R':  case 'S':
			case 'T':  case 'U':  case 'V':  case 'W':
			case 'X':  case 'Y':  case 'Z':  case '_':
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		mCONDITIONAL_SYMBOL(false);
		mPP_NEW_LINE(false);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPP_REGION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_REGION;
		
		{
			switch ( cached_LA1 )
			{
			case 'r':
			{
				mPPT_REGION(false);
				break;
			}
			case 'e':
			{
				mPPT_END_REGION(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		mPP_MESSAGE(false);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPPT_DEFINE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PPT_DEFINE;
		
		match("define");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPPT_UNDEF(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PPT_UNDEF;
		
		match("undef");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mCONDITIONAL_SYMBOL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = CONDITIONAL_SYMBOL;
		
		mIDENTIFIER_START_CHARACTER(false);
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_5_.member(cached_LA1)))
				{
					mIDENTIFIER_PART_CHARACTER(false);
				}
				else
				{
					goto _loop691_breakloop;
				}
				
			}
_loop691_breakloop:			;
		}    // ( ... )*
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPPT_REGION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PPT_REGION;
		
		match("region");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPPT_END_REGION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PPT_END_REGION;
		
		match("endregion");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPP_MESSAGE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PP_MESSAGE;
		
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_0_.member(cached_LA1)))
				{
					mNOT_NEW_LINE(false);
				}
				else
				{
					goto _loop688_breakloop;
				}
				
			}
_loop688_breakloop:			;
		}    // ( ... )*
		mNEW_LINE(false);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	
	private static long[] mk_tokenSet_0_()
	{
		long[] data = new long[1024];
		data[0]=-9224L;
		for (int i = 1; i<=127; i++) { data[i]=-1L; }
		data[128]=-3298534883329L;
		for (int i = 129; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_0_ = new BitSet(mk_tokenSet_0_());
	private static long[] mk_tokenSet_1_()
	{
		long[] data = new long[513];
		data[0]=4294973952L;
		data[1]=10133305320013824L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_1_ = new BitSet(mk_tokenSet_1_());
	private static long[] mk_tokenSet_2_()
	{
		long[] data = new long[513];
		data[0]=288019269919178752L;
		for (int i = 1; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_2_ = new BitSet(mk_tokenSet_2_());
	private static long[] mk_tokenSet_3_()
	{
		long[] data = new long[513];
		data[0]=9216L;
		for (int i = 1; i<=127; i++) { data[i]=0L; }
		data[128]=3298534883328L;
		for (int i = 129; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_3_ = new BitSet(mk_tokenSet_3_());
	private static long[] mk_tokenSet_4_()
	{
		long[] data = new long[1024];
		data[0]=-4398046520328L;
		for (int i = 1; i<=127; i++) { data[i]=-1L; }
		data[128]=-3298534883329L;
		for (int i = 129; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_4_ = new BitSet(mk_tokenSet_4_());
	private static long[] mk_tokenSet_5_()
	{
		long[] data = new long[513];
		data[0]=287948969894477824L;
		data[1]=576460745995190270L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_5_ = new BitSet(mk_tokenSet_5_());
	private static long[] mk_tokenSet_6_()
	{
		long[] data = new long[513];
		data[0]=0L;
		data[1]=35527969480784L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_6_ = new BitSet(mk_tokenSet_6_());
	private static long[] mk_tokenSet_7_()
	{
		long[] data = new long[513];
		data[0]=287948901175001088L;
		data[1]=137438953504L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_7_ = new BitSet(mk_tokenSet_7_());
	private static long[] mk_tokenSet_8_()
	{
		long[] data = new long[513];
		data[0]=287992881640112128L;
		data[1]=137438953504L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_8_ = new BitSet(mk_tokenSet_8_());
	private static long[] mk_tokenSet_9_()
	{
		long[] data = new long[513];
		data[0]=287948901175001088L;
		data[1]=35527969480784L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_9_ = new BitSet(mk_tokenSet_9_());
	private static long[] mk_tokenSet_10_()
	{
		long[] data = new long[513];
		data[0]=0L;
		data[1]=9024791442886656L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_10_ = new BitSet(mk_tokenSet_10_());
	private static long[] mk_tokenSet_11_()
	{
		long[] data = new long[513];
		data[0]=287948901175001088L;
		data[1]=541165879422L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_11_ = new BitSet(mk_tokenSet_11_());
	private static long[] mk_tokenSet_12_()
	{
		long[] data = new long[513];
		data[0]=282041912393728L;
		data[1]=23714567704018944L;
		for (int i = 2; i<=512; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_12_ = new BitSet(mk_tokenSet_12_());
	private static long[] mk_tokenSet_13_()
	{
		long[] data = new long[1024];
		data[0]=-549755823112L;
		data[1]=-268435457L;
		for (int i = 2; i<=127; i++) { data[i]=-1L; }
		data[128]=-3298534883329L;
		for (int i = 129; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_13_ = new BitSet(mk_tokenSet_13_());
	private static long[] mk_tokenSet_14_()
	{
		long[] data = new long[1024];
		data[0]=-17179878408L;
		for (int i = 1; i<=127; i++) { data[i]=-1L; }
		data[128]=-3298534883329L;
		for (int i = 129; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_14_ = new BitSet(mk_tokenSet_14_());
	private static long[] mk_tokenSet_15_()
	{
		long[] data = new long[1024];
		data[0]=-17179869192L;
		data[1]=-268435457L;
		for (int i = 2; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_15_ = new BitSet(mk_tokenSet_15_());
	private static long[] mk_tokenSet_16_()
	{
		long[] data = new long[1024];
		data[0]=-17179878408L;
		data[1]=-268435457L;
		for (int i = 2; i<=127; i++) { data[i]=-1L; }
		data[128]=-3298534883329L;
		for (int i = 129; i<=511; i++) { data[i]=-1L; }
		for (int i = 512; i<=1023; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_16_ = new BitSet(mk_tokenSet_16_());
	
}
}
