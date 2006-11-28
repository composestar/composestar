// $ANTLR : "csharp.g" -> "CSharpParser.cs"$

	using CommonAST					= antlr.CommonAST; //DDW.CSharp.Parse.LineNumberAST;
	using System.Collections;
	using System;

namespace DDW.CSharp.Parse
{
	// Generate the header common to all output files.
	using System;
	
	using TokenBuffer              = antlr.TokenBuffer;
	using TokenStreamException     = antlr.TokenStreamException;
	using TokenStreamIOException   = antlr.TokenStreamIOException;
	using ANTLRException           = antlr.ANTLRException;
	using LLkParser = antlr.LLkParser;
	using Token                    = antlr.Token;
	using IToken                   = antlr.IToken;
	using TokenStream              = antlr.TokenStream;
	using RecognitionException     = antlr.RecognitionException;
	using NoViableAltException     = antlr.NoViableAltException;
	using MismatchedTokenException = antlr.MismatchedTokenException;
	using SemanticException        = antlr.SemanticException;
	using ParserSharedInputState   = antlr.ParserSharedInputState;
	using BitSet                   = antlr.collections.impl.BitSet;
	using AST                      = antlr.collections.AST;
	using ASTPair                  = antlr.ASTPair;
	using ASTFactory               = antlr.ASTFactory;
	using ASTArray                 = antlr.collections.impl.ASTArray;
	
	public 	class CSharpParser : antlr.LLkParser
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
		
		
	private int errorCounter = 0;
	
	public int ErrorCounter
	{
		get { return errorCounter; }
	}
	
	public override void reportError(RecognitionException ex)
	{
		Console.WriteLine("DUMMER~error~" + ex.getFilename() + "~" + ex.getLine() + "~" + ex.Message);
		this.errorCounter++;
	}
	
	public override void reportError(string s)
	{
		string filename = getFilename();
		Console.WriteLine("DUMMER~error~" + (filename == null ? "" : filename) + "~0~" + s);
		this.errorCounter++;
	}

		
		protected void initialize()
		{
			tokenNames = tokenNames_;
			initializeFactory();
		}
		
		
		protected CSharpParser(TokenBuffer tokenBuf, int k) : base(tokenBuf, k)
		{
			initialize();
		}
		
		public CSharpParser(TokenBuffer tokenBuf) : this(tokenBuf,2)
		{
		}
		
		protected CSharpParser(TokenStream lexer, int k) : base(lexer,k)
		{
			initialize();
		}
		
		public CSharpParser(TokenStream lexer) : this(lexer,2)
		{
		}
		
		public CSharpParser(ParserSharedInputState state) : base(state,2)
		{
			initialize();
		}
		
	public void identifier() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		IToken  id = null;
		AST id_AST = null;
		
		try {      // for error handling
			id = LT(1);
			id_AST = astFactory.create(id);
			match(IDENTIFIER);
			if (0==inputState.guessing)
			{
				identifier_AST = (AST)currentAST.root;
				identifier_AST = (AST) astFactory.make(identifier_AST, (AST) astFactory.make((DDW.CSharp.Parse.Ident) astFactory.create(Ident,id_AST.getText())));
				currentAST.root = identifier_AST;
				if ( (null != identifier_AST) && (null != identifier_AST.getFirstChild()) )
					currentAST.child = identifier_AST.getFirstChild();
				else
					currentAST.child = identifier_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_0_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = identifier_AST;
	}
	
	public void comment() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comment_AST = null;
		IToken  slc = null;
		AST slc_AST = null;
		IToken  dc = null;
		AST dc_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case SINGLE_LINE_COMMENT:
			{
				slc = LT(1);
				slc_AST = astFactory.create(slc);
				match(SINGLE_LINE_COMMENT);
				if (0==inputState.guessing)
				{
					comment_AST = (AST)currentAST.root;
					comment_AST = (AST) astFactory.make((DDW.CSharp.Parse.CommentNode) astFactory.create(CommentNode,slc_AST.getText()));
					currentAST.root = comment_AST;
					if ( (null != comment_AST) && (null != comment_AST.getFirstChild()) )
						currentAST.child = comment_AST.getFirstChild();
					else
						currentAST.child = comment_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case DELIMITED_COMMENT:
			{
				dc = LT(1);
				dc_AST = astFactory.create(dc);
				match(DELIMITED_COMMENT);
				if (0==inputState.guessing)
				{
					comment_AST = (AST)currentAST.root;
					comment_AST = (AST) astFactory.make((DDW.CSharp.Parse.CommentNode) astFactory.create(CommentNode,dc_AST.getText()));
					currentAST.root = comment_AST;
					if ( (null != comment_AST) && (null != comment_AST.getFirstChild()) )
						currentAST.child = comment_AST.getFirstChild();
					else
						currentAST.child = comment_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_1_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = comment_AST;
	}
	
	public void literal() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case TRUE:
			case FALSE:
			{
				boolean_literal();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.BooleanLiteral) astFactory.create(BooleanLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case INTEGER_LITERAL:
			{
				AST tmp1_AST = null;
				tmp1_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp1_AST);
				match(INTEGER_LITERAL);
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.IntegerLiteral) astFactory.create(IntegerLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case HEXADECIMAL_INTEGER_LITERAL:
			{
				AST tmp2_AST = null;
				tmp2_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp2_AST);
				match(HEXADECIMAL_INTEGER_LITERAL);
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.IntegerLiteral) astFactory.create(IntegerLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case REAL_LITERAL:
			{
				AST tmp3_AST = null;
				tmp3_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp3_AST);
				match(REAL_LITERAL);
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.RealLiteral) astFactory.create(RealLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case CHARACTER_LITERAL:
			{
				AST tmp4_AST = null;
				tmp4_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp4_AST);
				match(CHARACTER_LITERAL);
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.CharLiteral) astFactory.create(CharLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case STRING_LITERAL:
			{
				AST tmp5_AST = null;
				tmp5_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp5_AST);
				match(STRING_LITERAL);
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.StringLiteral) astFactory.create(StringLiteral,literal_AST.getText()));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			case NULL:
			{
				null_literal();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				if (0==inputState.guessing)
				{
					literal_AST = (AST)currentAST.root;
					literal_AST = (AST) astFactory.make((DDW.CSharp.Parse.NullLiteral) astFactory.create(NullLiteral,"null"));
					currentAST.root = literal_AST;
					if ( (null != literal_AST) && (null != literal_AST.getFirstChild()) )
						currentAST.child = literal_AST.getFirstChild();
					else
						currentAST.child = literal_AST;
					currentAST.advanceChildToEnd();
				}
				literal_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = literal_AST;
	}
	
	public void boolean_literal() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST boolean_literal_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case TRUE:
			{
				AST tmp6_AST = null;
				tmp6_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp6_AST);
				match(TRUE);
				boolean_literal_AST = currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp7_AST = null;
				tmp7_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp7_AST);
				match(FALSE);
				boolean_literal_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = boolean_literal_AST;
	}
	
	public void null_literal() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST null_literal_AST = null;
		
		try {      // for error handling
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp8_AST);
			match(NULL);
			null_literal_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = null_literal_AST;
	}
	
	public void namespace_name() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_name_AST = null;
		
		try {      // for error handling
			namespace_or_type_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			namespace_name_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = namespace_name_AST;
	}
	
	public void namespace_or_type_name() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_or_type_name_AST = null;
		
		try {      // for error handling
			simple_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==DOT))
					{
						match(DOT);
						simple_name();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop10_breakloop;
					}
					
				}
_loop10_breakloop:				;
			}    // ( ... )*
			namespace_or_type_name_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_4_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = namespace_or_type_name_AST;
	}
	
	public void type_name() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_name_AST = null;
		
		try {      // for error handling
			namespace_or_type_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			type_name_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_4_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = type_name_AST;
	}
	
	public void simple_name() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simple_name_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			simple_name_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_5_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = simple_name_AST;
	}
	
	public void type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		IToken  obj = null;
		AST obj_AST = null;
		IToken  str = null;
		AST str_AST = null;
		
		try {      // for error handling
			if (((LA(1) >= BOOL && LA(1) <= DOUBLE)) && (tokenSet_6_.member(LA(2))))
			{
				value_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				type_AST = currentAST.root;
			}
			else {
				bool synPredMatched13 = false;
				if (((tokenSet_7_.member(LA(1))) && (LA(2)==DOT||LA(2)==LBRACK)))
				{
					int _m13 = mark();
					synPredMatched13 = true;
					inputState.guessing++;
					try {
						{
							array_type();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched13 = false;
					}
					rewind(_m13);
					inputState.guessing--;
				}
				if ( synPredMatched13 )
				{
					array_type();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					type_AST = currentAST.root;
				}
				else if ((LA(1)==IDENTIFIER) && (tokenSet_8_.member(LA(2)))) {
					type_name();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					type_AST = currentAST.root;
				}
				else if ((LA(1)==OBJECT) && (tokenSet_6_.member(LA(2)))) {
					obj = LT(1);
					obj_AST = astFactory.create(obj);
					match(OBJECT);
					if (0==inputState.guessing)
					{
						type_AST = (AST)currentAST.root;
						type_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,obj_AST.getText()));
						currentAST.root = type_AST;
						if ( (null != type_AST) && (null != type_AST.getFirstChild()) )
							currentAST.child = type_AST.getFirstChild();
						else
							currentAST.child = type_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else if ((LA(1)==STRING) && (tokenSet_6_.member(LA(2)))) {
					str = LT(1);
					str_AST = astFactory.create(str);
					match(STRING);
					if (0==inputState.guessing)
					{
						type_AST = (AST)currentAST.root;
						type_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,str_AST.getText()));
						currentAST.root = type_AST;
						if ( (null != type_AST) && (null != type_AST.getFirstChild()) )
							currentAST.child = type_AST.getFirstChild();
						else
							currentAST.child = type_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex)
			{
				if (0 == inputState.guessing)
				{
					reportError(ex);
					recover(ex,tokenSet_6_);
				}
				else
				{
					throw ex;
				}
			}
			returnAST = type_AST;
		}
		
	public void value_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST value_type_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			struct_type();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				value_type_AST = (AST)currentAST.root;
					value_type_AST =	 
						(AST) astFactory.make(value_type_AST, (DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,st_AST.getText()));
					
				currentAST.root = value_type_AST;
				if ( (null != value_type_AST) && (null != value_type_AST.getFirstChild()) )
					currentAST.child = value_type_AST.getFirstChild();
				else
					currentAST.child = value_type_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_9_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = value_type_AST;
	}
	
	public void array_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_type_AST = null;
		
		try {      // for error handling
			non_array_type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			rank_specifiers();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			array_type_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_10_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = array_type_AST;
	}
	
	public void struct_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_type_AST = null;
		
		try {      // for error handling
			simple_type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			struct_type_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_9_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = struct_type_AST;
	}
	
	public void simple_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simple_type_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			{
				numeric_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				simple_type_AST = currentAST.root;
				break;
			}
			case BOOL:
			{
				AST tmp10_AST = null;
				tmp10_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp10_AST);
				match(BOOL);
				simple_type_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_9_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = simple_type_AST;
	}
	
	public void numeric_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numeric_type_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			{
				integral_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				numeric_type_AST = currentAST.root;
				break;
			}
			case FLOAT:
			case DOUBLE:
			{
				floating_point_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				numeric_type_AST = currentAST.root;
				break;
			}
			case DECIMAL:
			{
				AST tmp11_AST = null;
				tmp11_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp11_AST);
				match(DECIMAL);
				numeric_type_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_9_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = numeric_type_AST;
	}
	
	public void integral_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST integral_type_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case SBYTE:
			{
				AST tmp12_AST = null;
				tmp12_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp12_AST);
				match(SBYTE);
				integral_type_AST = currentAST.root;
				break;
			}
			case BYTE:
			{
				AST tmp13_AST = null;
				tmp13_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp13_AST);
				match(BYTE);
				integral_type_AST = currentAST.root;
				break;
			}
			case SHORT:
			{
				AST tmp14_AST = null;
				tmp14_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp14_AST);
				match(SHORT);
				integral_type_AST = currentAST.root;
				break;
			}
			case USHORT:
			{
				AST tmp15_AST = null;
				tmp15_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp15_AST);
				match(USHORT);
				integral_type_AST = currentAST.root;
				break;
			}
			case INT:
			{
				AST tmp16_AST = null;
				tmp16_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp16_AST);
				match(INT);
				integral_type_AST = currentAST.root;
				break;
			}
			case UINT:
			{
				AST tmp17_AST = null;
				tmp17_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp17_AST);
				match(UINT);
				integral_type_AST = currentAST.root;
				break;
			}
			case LONG:
			{
				AST tmp18_AST = null;
				tmp18_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp18_AST);
				match(LONG);
				integral_type_AST = currentAST.root;
				break;
			}
			case ULONG:
			{
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp19_AST);
				match(ULONG);
				integral_type_AST = currentAST.root;
				break;
			}
			case CHAR:
			{
				AST tmp20_AST = null;
				tmp20_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp20_AST);
				match(CHAR);
				integral_type_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_11_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = integral_type_AST;
	}
	
	public void floating_point_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST floating_point_type_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case FLOAT:
			{
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp21_AST);
				match(FLOAT);
				floating_point_type_AST = currentAST.root;
				break;
			}
			case DOUBLE:
			{
				AST tmp22_AST = null;
				tmp22_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp22_AST);
				match(DOUBLE);
				floating_point_type_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_9_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = floating_point_type_AST;
	}
	
	public void reference_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST reference_type_AST = null;
		
		try {      // for error handling
			bool synPredMatched22 = false;
			if (((LA(1)==IDENTIFIER||LA(1)==OBJECT||LA(1)==STRING) && (LA(2)==EOF||LA(2)==DOT)))
			{
				int _m22 = mark();
				synPredMatched22 = true;
				inputState.guessing++;
				try {
					{
						class_type();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched22 = false;
				}
				rewind(_m22);
				inputState.guessing--;
			}
			if ( synPredMatched22 )
			{
				class_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				reference_type_AST = currentAST.root;
			}
			else {
				bool synPredMatched24 = false;
				if (((tokenSet_7_.member(LA(1))) && (LA(2)==DOT||LA(2)==LBRACK)))
				{
					int _m24 = mark();
					synPredMatched24 = true;
					inputState.guessing++;
					try {
						{
							array_type();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched24 = false;
					}
					rewind(_m24);
					inputState.guessing--;
				}
				if ( synPredMatched24 )
				{
					array_type();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					reference_type_AST = currentAST.root;
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex)
			{
				if (0 == inputState.guessing)
				{
					reportError(ex);
					recover(ex,tokenSet_1_);
				}
				else
				{
					throw ex;
				}
			}
			returnAST = reference_type_AST;
		}
		
	public void class_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_type_AST = null;
		IToken  obj = null;
		AST obj_AST = null;
		IToken  str = null;
		AST str_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			{
				type_name();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				class_type_AST = currentAST.root;
				break;
			}
			case OBJECT:
			{
				obj = LT(1);
				obj_AST = astFactory.create(obj);
				match(OBJECT);
				if (0==inputState.guessing)
				{
					class_type_AST = (AST)currentAST.root;
					class_type_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,obj_AST.getText()));
					currentAST.root = class_type_AST;
					if ( (null != class_type_AST) && (null != class_type_AST.getFirstChild()) )
						currentAST.child = class_type_AST.getFirstChild();
					else
						currentAST.child = class_type_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case STRING:
			{
				str = LT(1);
				str_AST = astFactory.create(str);
				match(STRING);
				if (0==inputState.guessing)
				{
					class_type_AST = (AST)currentAST.root;
					class_type_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,str_AST.getText()));
					currentAST.root = class_type_AST;
					if ( (null != class_type_AST) && (null != class_type_AST.getFirstChild()) )
						currentAST.child = class_type_AST.getFirstChild();
					else
						currentAST.child = class_type_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_12_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = class_type_AST;
	}
	
	public void non_array_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST non_array_type_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case OBJECT:
			case STRING:
			{
				class_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				non_array_type_AST = currentAST.root;
				break;
			}
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			{
				value_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				non_array_type_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_13_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = non_array_type_AST;
	}
	
	public void rank_specifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rank_specifiers_AST = null;
		
		try {      // for error handling
			{ // ( ... )+
				int _cnt32=0;
				for (;;)
				{
					if ((LA(1)==LBRACK) && (LA(2)==COMMA||LA(2)==RBRACK))
					{
						array_rank();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						if (_cnt32 >= 1) { goto _loop32_breakloop; } else { throw new NoViableAltException(LT(1), getFilename());; }
					}
					
					_cnt32++;
				}
_loop32_breakloop:				;
			}    // ( ... )+
			rank_specifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_5_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = rank_specifiers_AST;
	}
	
	public void interface_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_type_AST = null;
		
		try {      // for error handling
			type_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			interface_type_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_1_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_type_AST;
	}
	
	public void delegate_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST delegate_type_AST = null;
		
		try {      // for error handling
			type_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			delegate_type_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_1_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = delegate_type_AST;
	}
	
	public void array_rank() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_rank_AST = null;
		int cnt=1;
		
		try {      // for error handling
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			match(LBRACK);
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						AST tmp24_AST = null;
						tmp24_AST = astFactory.create(LT(1));
						match(COMMA);
						if (0==inputState.guessing)
						{
							cnt++;
						}
					}
					else
					{
						goto _loop35_breakloop;
					}
					
				}
_loop35_breakloop:				;
			}    // ( ... )*
			AST tmp25_AST = null;
			tmp25_AST = astFactory.create(LT(1));
			match(RBRACK);
			if (0==inputState.guessing)
			{
				array_rank_AST = (AST)currentAST.root;
				array_rank_AST =	 
							(AST) astFactory.make(array_rank_AST, (DDW.CSharp.Parse.ArrayRankExpr) astFactory.create(ArrayRankExpr,cnt.ToString()));
						
				currentAST.root = array_rank_AST;
				if ( (null != array_rank_AST) && (null != array_rank_AST.getFirstChild()) )
					currentAST.child = array_rank_AST.getFirstChild();
				else
					currentAST.child = array_rank_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_5_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = array_rank_AST;
	}
	
	public void variable_reference() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_reference_AST = null;
		
		try {      // for error handling
			expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			variable_reference_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_14_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = variable_reference_AST;
	}
	
	public void expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			bool synPredMatched126 = false;
			if (((tokenSet_15_.member(LA(1))) && (tokenSet_16_.member(LA(2)))))
			{
				int _m126 = mark();
				synPredMatched126 = true;
				inputState.guessing++;
				try {
					{
						conditional_expression();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched126 = false;
				}
				rewind(_m126);
				inputState.guessing--;
			}
			if ( synPredMatched126 )
			{
				conditional_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				expression_AST = currentAST.root;
			}
			else if ((tokenSet_15_.member(LA(1))) && (tokenSet_17_.member(LA(2)))) {
				assignment();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				expression_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_18_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = expression_AST;
	}
	
	public void argument_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argument_list_AST = null;
		
		try {      // for error handling
			argument();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						argument();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop39_breakloop;
					}
					
				}
_loop39_breakloop:				;
			}    // ( ... )*
			if (0==inputState.guessing)
			{
				argument_list_AST = (AST)currentAST.root;
				argument_list_AST = (AST) astFactory.make((DDW.CSharp.Parse.Args) astFactory.create(Args), argument_list_AST);
				currentAST.root = argument_list_AST;
				if ( (null != argument_list_AST) && (null != argument_list_AST.getFirstChild()) )
					currentAST.child = argument_list_AST.getFirstChild();
				else
					currentAST.child = argument_list_AST;
				currentAST.advanceChildToEnd();
			}
			argument_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_19_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = argument_list_AST;
	}
	
	public void argument() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argument_AST = null;
		AST ex_AST = null;
		AST dir_AST = null;
		AST vr_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case INTEGER_LITERAL:
			case HEXADECIMAL_INTEGER_LITERAL:
			case REAL_LITERAL:
			case CHARACTER_LITERAL:
			case STRING_LITERAL:
			case TRUE:
			case FALSE:
			case NULL:
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			case LPAREN:
			case THIS:
			case BASE:
			case INC:
			case DEC:
			case NEW:
			case TYPEOF:
			case CHECKED:
			case UNCHECKED:
			case PLUS:
			case MINUS:
			case LNOT:
			case BNOT:
			case STAR:
			case SIZEOF:
			{
				expression();
				if (0 == inputState.guessing)
				{
					ex_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					argument_AST = (AST)currentAST.root;
					argument_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.Arg) astFactory.create(Arg)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ex_AST)); 
						
					currentAST.root = argument_AST;
					if ( (null != argument_AST) && (null != argument_AST.getFirstChild()) )
						currentAST.child = argument_AST.getFirstChild();
					else
						currentAST.child = argument_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case REF:
			case OUT:
			{
				parameter_direction();
				if (0 == inputState.guessing)
				{
					dir_AST = (AST)returnAST;
				}
				variable_reference();
				if (0 == inputState.guessing)
				{
					vr_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					argument_AST = (AST)currentAST.root;
					argument_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.Arg) astFactory.create(Arg)), dir_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), vr_AST)); 
						
					currentAST.root = argument_AST;
					if ( (null != argument_AST) && (null != argument_AST.getFirstChild()) )
						currentAST.child = argument_AST.getFirstChild();
					else
						currentAST.child = argument_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_14_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = argument_AST;
	}
	
	public void parameter_direction() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameter_direction_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case REF:
			{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				match(REF);
				if (0==inputState.guessing)
				{
					parameter_direction_AST = (AST)currentAST.root;
					parameter_direction_AST = (AST) astFactory.make((DDW.CSharp.Parse.ArgDirection) astFactory.create(ArgDirection,"ref"));
					currentAST.root = parameter_direction_AST;
					if ( (null != parameter_direction_AST) && (null != parameter_direction_AST.getFirstChild()) )
						currentAST.child = parameter_direction_AST.getFirstChild();
					else
						currentAST.child = parameter_direction_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case OUT:
			{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				match(OUT);
				if (0==inputState.guessing)
				{
					parameter_direction_AST = (AST)currentAST.root;
					parameter_direction_AST = (AST) astFactory.make((DDW.CSharp.Parse.ArgDirection) astFactory.create(ArgDirection,"out"));
					currentAST.root = parameter_direction_AST;
					if ( (null != parameter_direction_AST) && (null != parameter_direction_AST.getFirstChild()) )
						currentAST.child = parameter_direction_AST.getFirstChild();
					else
						currentAST.child = parameter_direction_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = parameter_direction_AST;
	}
	
	public void parenthesized_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parenthesized_expression_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				parenthesized_expression_AST = (AST)currentAST.root;
				parenthesized_expression_AST = 
						(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.SubExpr) astFactory.create(SubExpr)), ex_AST); 
					
				currentAST.root = parenthesized_expression_AST;
				if ( (null != parenthesized_expression_AST) && (null != parenthesized_expression_AST.getFirstChild()) )
					currentAST.child = parenthesized_expression_AST.getFirstChild();
				else
					currentAST.child = parenthesized_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = parenthesized_expression_AST;
	}
	
	public void primary_start() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primary_start_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case INTEGER_LITERAL:
			case HEXADECIMAL_INTEGER_LITERAL:
			case REAL_LITERAL:
			case CHARACTER_LITERAL:
			case STRING_LITERAL:
			case TRUE:
			case FALSE:
			case NULL:
			{
				literal();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case IDENTIFIER:
			{
				simple_name();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case LPAREN:
			{
				parenthesized_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case THIS:
			{
				this_access();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case BASE:
			{
				base_access();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case TYPEOF:
			{
				typeof_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case SIZEOF:
			{
				sizeof_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case CHECKED:
			{
				checked_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case UNCHECKED:
			{
				unchecked_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			{
				predefined_type_access();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				primary_start_AST = currentAST.root;
				break;
			}
			default:
				bool synPredMatched45 = false;
				if (((LA(1)==NEW) && (tokenSet_7_.member(LA(2)))))
				{
					int _m45 = mark();
					synPredMatched45 = true;
					inputState.guessing++;
					try {
						{
							array_creation_expression();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched45 = false;
					}
					rewind(_m45);
					inputState.guessing--;
				}
				if ( synPredMatched45 )
				{
					array_creation_expression();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					primary_start_AST = currentAST.root;
				}
				else if ((LA(1)==NEW) && (tokenSet_7_.member(LA(2)))) {
					object_creation_expression();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					primary_start_AST = currentAST.root;
				}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			break; }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = primary_start_AST;
	}
	
	public void this_access() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST this_access_AST = null;
		
		try {      // for error handling
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			match(THIS);
			if (0==inputState.guessing)
			{
				this_access_AST = (AST)currentAST.root;
				this_access_AST = (AST) astFactory.make((DDW.CSharp.Parse.ThisRefExpr) astFactory.create(ThisRefExpr));
				currentAST.root = this_access_AST;
				if ( (null != this_access_AST) && (null != this_access_AST.getFirstChild()) )
					currentAST.child = this_access_AST.getFirstChild();
				else
					currentAST.child = this_access_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = this_access_AST;
	}
	
	public void base_access() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST base_access_AST = null;
		AST id_AST = null;
		AST ea_AST = null;
		
		try {      // for error handling
			if ((LA(1)==BASE) && (LA(2)==DOT))
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				match(BASE);
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				match(DOT);
				identifier();
				if (0 == inputState.guessing)
				{
					id_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					base_access_AST = (AST)currentAST.root;
					base_access_AST = (AST) astFactory.make((DDW.CSharp.Parse.BaseRefExpr) astFactory.create(BaseRefExpr), id_AST);
					currentAST.root = base_access_AST;
					if ( (null != base_access_AST) && (null != base_access_AST.getFirstChild()) )
						currentAST.child = base_access_AST.getFirstChild();
					else
						currentAST.child = base_access_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==BASE) && (LA(2)==LBRACK)) {
				AST tmp34_AST = null;
				tmp34_AST = astFactory.create(LT(1));
				match(BASE);
				element_access();
				if (0 == inputState.guessing)
				{
					ea_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					base_access_AST = (AST)currentAST.root;
					base_access_AST = (AST) astFactory.make((DDW.CSharp.Parse.BaseRefExpr) astFactory.create(BaseRefExpr), ea_AST);
					currentAST.root = base_access_AST;
					if ( (null != base_access_AST) && (null != base_access_AST.getFirstChild()) )
						currentAST.child = base_access_AST.getFirstChild();
					else
						currentAST.child = base_access_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = base_access_AST;
	}
	
	public void array_creation_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_creation_expression_AST = null;
		AST tp_AST = null;
		AST ini_AST = null;
		AST tp2_AST = null;
		AST el2_AST = null;
		AST rnk2_AST = null;
		AST ini2_AST = null;
		
		try {      // for error handling
			bool synPredMatched74 = false;
			if (((LA(1)==NEW) && (tokenSet_7_.member(LA(2)))))
			{
				int _m74 = mark();
				synPredMatched74 = true;
				inputState.guessing++;
				try {
					{
						match(NEW);
						array_type();
						array_initializer();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched74 = false;
				}
				rewind(_m74);
				inputState.guessing--;
			}
			if ( synPredMatched74 )
			{
				AST tmp35_AST = null;
				tmp35_AST = astFactory.create(LT(1));
				match(NEW);
				array_type();
				if (0 == inputState.guessing)
				{
					tp_AST = (AST)returnAST;
				}
				array_initializer();
				if (0 == inputState.guessing)
				{
					ini_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					array_creation_expression_AST = (AST)currentAST.root;
					array_creation_expression_AST = 
								(AST) astFactory.make((DDW.CSharp.Parse.ArrayCreateExpr) astFactory.create(ArrayCreateExpr), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), ini_AST);
							
					currentAST.root = array_creation_expression_AST;
					if ( (null != array_creation_expression_AST) && (null != array_creation_expression_AST.getFirstChild()) )
						currentAST.child = array_creation_expression_AST.getFirstChild();
					else
						currentAST.child = array_creation_expression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==NEW) && (tokenSet_7_.member(LA(2)))) {
				AST tmp36_AST = null;
				tmp36_AST = astFactory.create(LT(1));
				match(NEW);
				non_array_type();
				if (0 == inputState.guessing)
				{
					tp2_AST = (AST)returnAST;
				}
				match(LBRACK);
				expression_list();
				if (0 == inputState.guessing)
				{
					el2_AST = (AST)returnAST;
				}
				match(RBRACK);
				{
					if ((LA(1)==LBRACK) && (LA(2)==COMMA||LA(2)==RBRACK))
					{
						rank_specifiers();
						if (0 == inputState.guessing)
						{
							rnk2_AST = (AST)returnAST;
						}
					}
					else if ((tokenSet_20_.member(LA(1))) && (tokenSet_21_.member(LA(2)))) {
					}
					else
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					
				}
				{
					switch ( LA(1) )
					{
					case LBRACE:
					{
						array_initializer();
						if (0 == inputState.guessing)
						{
							ini2_AST = (AST)returnAST;
						}
						break;
					}
					case IDENTIFIER:
					case DOT:
					case LBRACK:
					case COMMA:
					case RBRACK:
					case LPAREN:
					case RPAREN:
					case INC:
					case DEC:
					case PLUS:
					case MINUS:
					case STAR:
					case DIV:
					case MOD:
					case SL:
					case SR:
					case LTHAN:
					case GTHAN:
					case LE:
					case GE:
					case IS:
					case AS:
					case EQUAL:
					case NOT_EQUAL:
					case BAND:
					case BXOR:
					case BOR:
					case LAND:
					case LOR:
					case QUESTION:
					case COLON:
					case ASSIGN:
					case PLUS_ASN:
					case MINUS_ASN:
					case STAR_ASN:
					case DIV_ASN:
					case MOD_ASN:
					case BAND_ASN:
					case BOR_ASN:
					case BXOR_ASN:
					case SL_ASN:
					case SR_ASN:
					case RBRACE:
					case SEMI:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				if (0==inputState.guessing)
				{
					array_creation_expression_AST = (AST)currentAST.root;
					array_creation_expression_AST = 
								(AST) astFactory.make((DDW.CSharp.Parse.ArrayCreateExpr) astFactory.create(ArrayCreateExpr), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp2_AST), (AST) astFactory.make((DDW.CSharp.Parse.IndexerExpr) astFactory.create(IndexerExpr), el2_AST), rnk2_AST, ini2_AST);
							
					currentAST.root = array_creation_expression_AST;
					if ( (null != array_creation_expression_AST) && (null != array_creation_expression_AST.getFirstChild()) )
						currentAST.child = array_creation_expression_AST.getFirstChild();
					else
						currentAST.child = array_creation_expression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = array_creation_expression_AST;
	}
	
	public void object_creation_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST object_creation_expression_AST = null;
		AST tp_AST = null;
		AST al_AST = null;
		
		try {      // for error handling
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			match(NEW);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case REF:
				case OUT:
				case SIZEOF:
				{
					argument_list();
					if (0 == inputState.guessing)
					{
						al_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp41_AST = null;
			tmp41_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				object_creation_expression_AST = (AST)currentAST.root;
				object_creation_expression_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.ObjectCreateExpr) astFactory.create(ObjectCreateExpr), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), al_AST);
						
				currentAST.root = object_creation_expression_AST;
				if ( (null != object_creation_expression_AST) && (null != object_creation_expression_AST.getFirstChild()) )
					currentAST.child = object_creation_expression_AST.getFirstChild();
				else
					currentAST.child = object_creation_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = object_creation_expression_AST;
	}
	
	public void typeof_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeof_expression_AST = null;
		AST tt_AST = null;
		
		try {      // for error handling
			AST tmp42_AST = null;
			tmp42_AST = astFactory.create(LT(1));
			match(TYPEOF);
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			match(LPAREN);
			typeof_types();
			if (0 == inputState.guessing)
			{
				tt_AST = (AST)returnAST;
			}
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				typeof_expression_AST = (AST)currentAST.root;
				typeof_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.TypeOfExpr) astFactory.create(TypeOfExpr), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tt_AST));
				currentAST.root = typeof_expression_AST;
				if ( (null != typeof_expression_AST) && (null != typeof_expression_AST.getFirstChild()) )
					currentAST.child = typeof_expression_AST.getFirstChild();
				else
					currentAST.child = typeof_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = typeof_expression_AST;
	}
	
	public void sizeof_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sizeof_expression_AST = null;
		
		try {      // for error handling
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp45_AST);
			match(SIZEOF);
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp46_AST);
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			AST tmp47_AST = null;
			tmp47_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp47_AST);
			match(RPAREN);
			sizeof_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = sizeof_expression_AST;
	}
	
	public void checked_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST checked_expression_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp48_AST = null;
			tmp48_AST = astFactory.create(LT(1));
			match(CHECKED);
			match(LPAREN);
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			match(RPAREN);
			if (0==inputState.guessing)
			{
				checked_expression_AST = (AST)currentAST.root;
				checked_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.CheckedExpr) astFactory.create(CheckedExpr), ex_AST);
				currentAST.root = checked_expression_AST;
				if ( (null != checked_expression_AST) && (null != checked_expression_AST.getFirstChild()) )
					currentAST.child = checked_expression_AST.getFirstChild();
				else
					currentAST.child = checked_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = checked_expression_AST;
	}
	
	public void unchecked_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unchecked_expression_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp51_AST = null;
			tmp51_AST = astFactory.create(LT(1));
			match(UNCHECKED);
			match(LPAREN);
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			match(RPAREN);
			if (0==inputState.guessing)
			{
				unchecked_expression_AST = (AST)currentAST.root;
				unchecked_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.UncheckedExpr) astFactory.create(UncheckedExpr), ex_AST);
				currentAST.root = unchecked_expression_AST;
				if ( (null != unchecked_expression_AST) && (null != unchecked_expression_AST.getFirstChild()) )
					currentAST.child = unchecked_expression_AST.getFirstChild();
				else
					currentAST.child = unchecked_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = unchecked_expression_AST;
	}
	
	public void predefined_type_access() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST predefined_type_access_AST = null;
		AST apt_AST = null;
		AST id_AST = null;
		
		try {      // for error handling
			accessible_predefined_types();
			if (0 == inputState.guessing)
			{
				apt_AST = (AST)returnAST;
			}
			AST tmp54_AST = null;
			tmp54_AST = astFactory.create(LT(1));
			match(DOT);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				predefined_type_access_AST = (AST)currentAST.root;
				predefined_type_access_AST = (AST) astFactory.make(predefined_type_access_AST, (DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,apt_AST.getText()), (DDW.CSharp.Parse.MemberAccessExpr) astFactory.create(MemberAccessExpr,id_AST.getText()));
				currentAST.root = predefined_type_access_AST;
				if ( (null != predefined_type_access_AST) && (null != predefined_type_access_AST.getFirstChild()) )
					currentAST.child = predefined_type_access_AST.getFirstChild();
				else
					currentAST.child = predefined_type_access_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = predefined_type_access_AST;
	}
	
	public void primary_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primary_expression_AST = null;
		AST ps_AST = null;
		
		try {      // for error handling
			primary_start();
			if (0 == inputState.guessing)
			{
				ps_AST = (AST)returnAST;
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case INC:
					case DEC:
					{
						postfix_expression();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						break;
					}
					case LBRACK:
					{
						element_access();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						break;
					}
					case LPAREN:
					{
						invocation_expression();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						break;
					}
					case DOT:
					{
						member_access();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						break;
					}
					default:
					{
						goto _loop50_breakloop;
					}
					 }
				}
_loop50_breakloop:				;
			}    // ( ... )*
			primary_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_22_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = primary_expression_AST;
	}
	
	public void postfix_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfix_expression_AST = null;
		AST po_AST = null;
		
		try {      // for error handling
			postfix_op();
			if (0 == inputState.guessing)
			{
				po_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				postfix_expression_AST = (AST)currentAST.root;
				postfix_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.PostfixExpr) astFactory.create(PostfixExpr,po_AST.getText()));
				currentAST.root = postfix_expression_AST;
				if ( (null != postfix_expression_AST) && (null != postfix_expression_AST.getFirstChild()) )
					currentAST.child = postfix_expression_AST.getFirstChild();
				else
					currentAST.child = postfix_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = postfix_expression_AST;
	}
	
	public void element_access() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST element_access_AST = null;
		AST el_AST = null;
		
		try {      // for error handling
			AST tmp55_AST = null;
			tmp55_AST = astFactory.create(LT(1));
			match(LBRACK);
			expression_list();
			if (0 == inputState.guessing)
			{
				el_AST = (AST)returnAST;
			}
			AST tmp56_AST = null;
			tmp56_AST = astFactory.create(LT(1));
			match(RBRACK);
			if (0==inputState.guessing)
			{
				element_access_AST = (AST)currentAST.root;
				element_access_AST = (AST) astFactory.make((DDW.CSharp.Parse.IndexerExpr) astFactory.create(IndexerExpr), el_AST);
				currentAST.root = element_access_AST;
				if ( (null != element_access_AST) && (null != element_access_AST.getFirstChild()) )
					currentAST.child = element_access_AST.getFirstChild();
				else
					currentAST.child = element_access_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = element_access_AST;
	}
	
	public void invocation_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST invocation_expression_AST = null;
		AST args_AST = null;
		
		try {      // for error handling
			AST tmp57_AST = null;
			tmp57_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case REF:
				case OUT:
				case SIZEOF:
				{
					argument_list();
					if (0 == inputState.guessing)
					{
						args_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp58_AST = null;
			tmp58_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				invocation_expression_AST = (AST)currentAST.root;
				invocation_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.InvokeExpr) astFactory.create(InvokeExpr), args_AST);
				currentAST.root = invocation_expression_AST;
				if ( (null != invocation_expression_AST) && (null != invocation_expression_AST.getFirstChild()) )
					currentAST.child = invocation_expression_AST.getFirstChild();
				else
					currentAST.child = invocation_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = invocation_expression_AST;
	}
	
	public void member_access() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST member_access_AST = null;
		AST id_AST = null;
		
		try {      // for error handling
			AST tmp59_AST = null;
			tmp59_AST = astFactory.create(LT(1));
			match(DOT);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				member_access_AST = (AST)currentAST.root;
				member_access_AST = (AST) astFactory.make((DDW.CSharp.Parse.MemberAccessExpr) astFactory.create(MemberAccessExpr,id_AST.getText()));
				currentAST.root = member_access_AST;
				if ( (null != member_access_AST) && (null != member_access_AST.getFirstChild()) )
					currentAST.child = member_access_AST.getFirstChild();
				else
					currentAST.child = member_access_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = member_access_AST;
	}
	
	public void accessible_predefined_types() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessible_predefined_types_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case BOOL:
				{
					AST tmp60_AST = null;
					tmp60_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp60_AST);
					match(BOOL);
					break;
				}
				case BYTE:
				{
					AST tmp61_AST = null;
					tmp61_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp61_AST);
					match(BYTE);
					break;
				}
				case CHAR:
				{
					AST tmp62_AST = null;
					tmp62_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp62_AST);
					match(CHAR);
					break;
				}
				case DECIMAL:
				{
					AST tmp63_AST = null;
					tmp63_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp63_AST);
					match(DECIMAL);
					break;
				}
				case DOUBLE:
				{
					AST tmp64_AST = null;
					tmp64_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp64_AST);
					match(DOUBLE);
					break;
				}
				case FLOAT:
				{
					AST tmp65_AST = null;
					tmp65_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp65_AST);
					match(FLOAT);
					break;
				}
				case INT:
				{
					AST tmp66_AST = null;
					tmp66_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp66_AST);
					match(INT);
					break;
				}
				case LONG:
				{
					AST tmp67_AST = null;
					tmp67_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp67_AST);
					match(LONG);
					break;
				}
				case OBJECT:
				{
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp68_AST);
					match(OBJECT);
					break;
				}
				case SBYTE:
				{
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp69_AST);
					match(SBYTE);
					break;
				}
				case SHORT:
				{
					AST tmp70_AST = null;
					tmp70_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp70_AST);
					match(SHORT);
					break;
				}
				case STRING:
				{
					AST tmp71_AST = null;
					tmp71_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp71_AST);
					match(STRING);
					break;
				}
				case UINT:
				{
					AST tmp72_AST = null;
					tmp72_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp72_AST);
					match(UINT);
					break;
				}
				case ULONG:
				{
					AST tmp73_AST = null;
					tmp73_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp73_AST);
					match(ULONG);
					break;
				}
				case USHORT:
				{
					AST tmp74_AST = null;
					tmp74_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp74_AST);
					match(USHORT);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			accessible_predefined_types_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_23_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = accessible_predefined_types_AST;
	}
	
	public void expression_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_list_AST = null;
		
		try {      // for error handling
			expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						expression();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop60_breakloop;
					}
					
				}
_loop60_breakloop:				;
			}    // ( ... )*
			expression_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_24_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = expression_list_AST;
	}
	
	public void postfix_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfix_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case INC:
			{
				AST tmp76_AST = null;
				tmp76_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp76_AST);
				match(INC);
				postfix_op_AST = currentAST.root;
				break;
			}
			case DEC:
			{
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp77_AST);
				match(DEC);
				postfix_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = postfix_op_AST;
	}
	
	public void typeof_types() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeof_types_AST = null;
		IToken  vd = null;
		AST vd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				{
					type();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case VOID:
				{
					vd = LT(1);
					vd_AST = astFactory.create(vd);
					match(VOID);
					if (0==inputState.guessing)
					{
						typeof_types_AST = (AST)currentAST.root;
						typeof_types_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,vd_AST.getText()));
						currentAST.root = typeof_types_AST;
						if ( (null != typeof_types_AST) && (null != typeof_types_AST.getFirstChild()) )
							currentAST.child = typeof_types_AST.getFirstChild();
						else
							currentAST.child = typeof_types_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			typeof_types_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_19_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = typeof_types_AST;
	}
	
	public void array_initializer() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_initializer_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case LBRACE:
				case SIZEOF:
				{
					variable_initializer();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					{    // ( ... )*
						for (;;)
						{
							if ((LA(1)==COMMA))
							{
								match(COMMA);
								variable_initializer();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
							}
							else
							{
								goto _loop426_breakloop;
							}
							
						}
_loop426_breakloop:						;
					}    // ( ... )*
					break;
				}
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			match(RBRACE);
			if (0==inputState.guessing)
			{
				array_initializer_AST = (AST)currentAST.root;
				array_initializer_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ArrayInitExpr) astFactory.create(ArrayInitExpr)), array_initializer_AST);
						
				currentAST.root = array_initializer_AST;
				if ( (null != array_initializer_AST) && (null != array_initializer_AST.getFirstChild()) )
					currentAST.child = array_initializer_AST.getFirstChild();
				else
					currentAST.child = array_initializer_AST;
				currentAST.advanceChildToEnd();
			}
			array_initializer_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_2_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = array_initializer_AST;
	}
	
	public void unary_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unary_expression_AST = null;
		AST ce_AST = null;
		AST cue_AST = null;
		AST pe_AST = null;
		AST op_AST = null;
		AST ue_AST = null;
		
		try {      // for error handling
			bool synPredMatched79 = false;
			if (((LA(1)==LPAREN) && (tokenSet_7_.member(LA(2)))))
			{
				int _m79 = mark();
				synPredMatched79 = true;
				inputState.guessing++;
				try {
					{
						cast_expression();
						unary_expression();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched79 = false;
				}
				rewind(_m79);
				inputState.guessing--;
			}
			if ( synPredMatched79 )
			{
				cast_expression();
				if (0 == inputState.guessing)
				{
					ce_AST = (AST)returnAST;
				}
				unary_expression();
				if (0 == inputState.guessing)
				{
					cue_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					unary_expression_AST = (AST)currentAST.root;
					unary_expression_AST = (AST) astFactory.make((DDW.CSharp.Parse.CastExpr) astFactory.create(CastExpr), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), ce_AST), cue_AST);
					currentAST.root = unary_expression_AST;
					if ( (null != unary_expression_AST) && (null != unary_expression_AST.getFirstChild()) )
						currentAST.child = unary_expression_AST.getFirstChild();
					else
						currentAST.child = unary_expression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((tokenSet_25_.member(LA(1))) && (tokenSet_26_.member(LA(2)))) {
				primary_expression();
				if (0 == inputState.guessing)
				{
					pe_AST = (AST)returnAST;
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				unary_expression_AST = currentAST.root;
			}
			else if ((tokenSet_27_.member(LA(1)))) {
				uanry_op();
				if (0 == inputState.guessing)
				{
					op_AST = (AST)returnAST;
				}
				unary_expression();
				if (0 == inputState.guessing)
				{
					ue_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					unary_expression_AST = (AST)currentAST.root;
					unary_expression_AST = (AST) astFactory.make(unary_expression_AST, (DDW.CSharp.Parse.UnaryExpr) astFactory.create(UnaryExpr,op_AST.getText()), ue_AST);
					currentAST.root = unary_expression_AST;
					if ( (null != unary_expression_AST) && (null != unary_expression_AST.getFirstChild()) )
						currentAST.child = unary_expression_AST.getFirstChild();
					else
						currentAST.child = unary_expression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_22_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = unary_expression_AST;
	}
	
	public void cast_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cast_expression_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			match(RPAREN);
			cast_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = cast_expression_AST;
	}
	
	public void uanry_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST uanry_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case PLUS:
			{
				AST tmp83_AST = null;
				tmp83_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp83_AST);
				match(PLUS);
				uanry_op_AST = currentAST.root;
				break;
			}
			case MINUS:
			{
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp84_AST);
				match(MINUS);
				uanry_op_AST = currentAST.root;
				break;
			}
			case LNOT:
			{
				AST tmp85_AST = null;
				tmp85_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp85_AST);
				match(LNOT);
				uanry_op_AST = currentAST.root;
				break;
			}
			case BNOT:
			{
				AST tmp86_AST = null;
				tmp86_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp86_AST);
				match(BNOT);
				uanry_op_AST = currentAST.root;
				break;
			}
			case STAR:
			{
				AST tmp87_AST = null;
				tmp87_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp87_AST);
				match(STAR);
				uanry_op_AST = currentAST.root;
				break;
			}
			case INC:
			{
				AST tmp88_AST = null;
				tmp88_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp88_AST);
				match(INC);
				uanry_op_AST = currentAST.root;
				break;
			}
			case DEC:
			{
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp89_AST);
				match(DEC);
				uanry_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = uanry_op_AST;
	}
	
	public void pre_increment_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pre_increment_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ue_AST = null;
		
		try {      // for error handling
			op = LT(1);
			op_AST = astFactory.create(op);
			match(INC);
			unary_expression();
			if (0 == inputState.guessing)
			{
				ue_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				pre_increment_expression_AST = (AST)currentAST.root;
				pre_increment_expression_AST = 
						(AST) astFactory.make(pre_increment_expression_AST, (DDW.CSharp.Parse.UnaryExpr) astFactory.create(UnaryExpr,op_AST.getText()), ue_AST);
				currentAST.root = pre_increment_expression_AST;
				if ( (null != pre_increment_expression_AST) && (null != pre_increment_expression_AST.getFirstChild()) )
					currentAST.child = pre_increment_expression_AST.getFirstChild();
				else
					currentAST.child = pre_increment_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = pre_increment_expression_AST;
	}
	
	public void pre_decrement_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pre_decrement_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ue_AST = null;
		
		try {      // for error handling
			op = LT(1);
			op_AST = astFactory.create(op);
			match(DEC);
			unary_expression();
			if (0 == inputState.guessing)
			{
				ue_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				pre_decrement_expression_AST = (AST)currentAST.root;
				pre_decrement_expression_AST = 
						(AST) astFactory.make(pre_decrement_expression_AST, (DDW.CSharp.Parse.UnaryExpr) astFactory.create(UnaryExpr,op_AST.getText()), ue_AST);
				currentAST.root = pre_decrement_expression_AST;
				if ( (null != pre_decrement_expression_AST) && (null != pre_decrement_expression_AST.getFirstChild()) )
					currentAST.child = pre_decrement_expression_AST.getFirstChild();
				else
					currentAST.child = pre_decrement_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = pre_decrement_expression_AST;
	}
	
	public void multiplicative_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicative_expression_AST = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			unary_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if (((LA(1) >= STAR && LA(1) <= MOD)))
					{
						multiplicative_op();
						if (0 == inputState.guessing)
						{
							op_AST = (AST)returnAST;
						}
						unary_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							multiplicative_expression_AST = (AST)currentAST.root;
							multiplicative_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), multiplicative_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = multiplicative_expression_AST;
							if ( (null != multiplicative_expression_AST) && (null != multiplicative_expression_AST.getFirstChild()) )
								currentAST.child = multiplicative_expression_AST.getFirstChild();
							else
								currentAST.child = multiplicative_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop86_breakloop;
					}
					
				}
_loop86_breakloop:				;
			}    // ( ... )*
			multiplicative_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_29_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = multiplicative_expression_AST;
	}
	
	public void multiplicative_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicative_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case STAR:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp90_AST);
				match(STAR);
				multiplicative_op_AST = currentAST.root;
				break;
			}
			case DIV:
			{
				AST tmp91_AST = null;
				tmp91_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp91_AST);
				match(DIV);
				multiplicative_op_AST = currentAST.root;
				break;
			}
			case MOD:
			{
				AST tmp92_AST = null;
				tmp92_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp92_AST);
				match(MOD);
				multiplicative_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = multiplicative_op_AST;
	}
	
	public void additive_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additive_expression_AST = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			multiplicative_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==PLUS||LA(1)==MINUS))
					{
						additive_op();
						if (0 == inputState.guessing)
						{
							op_AST = (AST)returnAST;
						}
						multiplicative_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							additive_expression_AST = (AST)currentAST.root;
							additive_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), additive_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = additive_expression_AST;
							if ( (null != additive_expression_AST) && (null != additive_expression_AST.getFirstChild()) )
								currentAST.child = additive_expression_AST.getFirstChild();
							else
								currentAST.child = additive_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop90_breakloop;
					}
					
				}
_loop90_breakloop:				;
			}    // ( ... )*
			additive_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_30_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = additive_expression_AST;
	}
	
	public void additive_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additive_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case PLUS:
			{
				AST tmp93_AST = null;
				tmp93_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp93_AST);
				match(PLUS);
				additive_op_AST = currentAST.root;
				break;
			}
			case MINUS:
			{
				AST tmp94_AST = null;
				tmp94_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp94_AST);
				match(MINUS);
				additive_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = additive_op_AST;
	}
	
	public void shift_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shift_expression_AST = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			additive_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==SL||LA(1)==SR))
					{
						shift_op();
						if (0 == inputState.guessing)
						{
							op_AST = (AST)returnAST;
						}
						additive_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							shift_expression_AST = (AST)currentAST.root;
							shift_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), shift_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = shift_expression_AST;
							if ( (null != shift_expression_AST) && (null != shift_expression_AST.getFirstChild()) )
								currentAST.child = shift_expression_AST.getFirstChild();
							else
								currentAST.child = shift_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop94_breakloop;
					}
					
				}
_loop94_breakloop:				;
			}    // ( ... )*
			shift_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_31_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = shift_expression_AST;
	}
	
	public void shift_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shift_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case SL:
			{
				AST tmp95_AST = null;
				tmp95_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp95_AST);
				match(SL);
				shift_op_AST = currentAST.root;
				break;
			}
			case SR:
			{
				AST tmp96_AST = null;
				tmp96_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp96_AST);
				match(SR);
				shift_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = shift_op_AST;
	}
	
	public void relational_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relational_expression_AST = null;
		AST op1_AST = null;
		AST ex_AST = null;
		AST op2_AST = null;
		AST tp_AST = null;
		
		try {      // for error handling
			shift_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case LTHAN:
					case GTHAN:
					case LE:
					case GE:
					{
						relational_op();
						if (0 == inputState.guessing)
						{
							op1_AST = (AST)returnAST;
						}
						shift_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							relational_expression_AST = (AST)currentAST.root;
							relational_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op1_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), relational_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = relational_expression_AST;
							if ( (null != relational_expression_AST) && (null != relational_expression_AST.getFirstChild()) )
								currentAST.child = relational_expression_AST.getFirstChild();
							else
								currentAST.child = relational_expression_AST;
							currentAST.advanceChildToEnd();
						}
						break;
					}
					case IS:
					case AS:
					{
						type_comp_op();
						if (0 == inputState.guessing)
						{
							op2_AST = (AST)returnAST;
						}
						type();
						if (0 == inputState.guessing)
						{
							tp_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							relational_expression_AST = (AST)currentAST.root;
							relational_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op2_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), relational_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef,"right"), tp_AST))
									 ;
							currentAST.root = relational_expression_AST;
							if ( (null != relational_expression_AST) && (null != relational_expression_AST.getFirstChild()) )
								currentAST.child = relational_expression_AST.getFirstChild();
							else
								currentAST.child = relational_expression_AST;
							currentAST.advanceChildToEnd();
						}
						break;
					}
					default:
					{
						goto _loop98_breakloop;
					}
					 }
				}
_loop98_breakloop:				;
			}    // ( ... )*
			relational_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_32_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = relational_expression_AST;
	}
	
	public void relational_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relational_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LTHAN:
			{
				AST tmp97_AST = null;
				tmp97_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp97_AST);
				match(LTHAN);
				relational_op_AST = currentAST.root;
				break;
			}
			case GTHAN:
			{
				AST tmp98_AST = null;
				tmp98_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp98_AST);
				match(GTHAN);
				relational_op_AST = currentAST.root;
				break;
			}
			case LE:
			{
				AST tmp99_AST = null;
				tmp99_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp99_AST);
				match(LE);
				relational_op_AST = currentAST.root;
				break;
			}
			case GE:
			{
				AST tmp100_AST = null;
				tmp100_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp100_AST);
				match(GE);
				relational_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = relational_op_AST;
	}
	
	public void type_comp_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_comp_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IS:
			{
				AST tmp101_AST = null;
				tmp101_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp101_AST);
				match(IS);
				type_comp_op_AST = currentAST.root;
				break;
			}
			case AS:
			{
				AST tmp102_AST = null;
				tmp102_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp102_AST);
				match(AS);
				type_comp_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_7_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = type_comp_op_AST;
	}
	
	public void equality_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equality_expression_AST = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			relational_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==EQUAL||LA(1)==NOT_EQUAL))
					{
						equality_op();
						if (0 == inputState.guessing)
						{
							op_AST = (AST)returnAST;
						}
						relational_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							equality_expression_AST = (AST)currentAST.root;
							equality_expression_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), equality_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = equality_expression_AST;
							if ( (null != equality_expression_AST) && (null != equality_expression_AST.getFirstChild()) )
								currentAST.child = equality_expression_AST.getFirstChild();
							else
								currentAST.child = equality_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop103_breakloop;
					}
					
				}
_loop103_breakloop:				;
			}    // ( ... )*
			equality_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_33_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = equality_expression_AST;
	}
	
	public void equality_op() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equality_op_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case EQUAL:
			{
				AST tmp103_AST = null;
				tmp103_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp103_AST);
				match(EQUAL);
				equality_op_AST = currentAST.root;
				break;
			}
			case NOT_EQUAL:
			{
				AST tmp104_AST = null;
				tmp104_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp104_AST);
				match(NOT_EQUAL);
				equality_op_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = equality_op_AST;
	}
	
	public void and_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST and_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			equality_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==BAND))
					{
						op = LT(1);
						op_AST = astFactory.create(op);
						match(BAND);
						equality_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							and_expression_AST = (AST)currentAST.root;
							and_expression_AST = 
									(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), and_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = and_expression_AST;
							if ( (null != and_expression_AST) && (null != and_expression_AST.getFirstChild()) )
								currentAST.child = and_expression_AST.getFirstChild();
							else
								currentAST.child = and_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop107_breakloop;
					}
					
				}
_loop107_breakloop:				;
			}    // ( ... )*
			and_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_34_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = and_expression_AST;
	}
	
	public void exclusive_or_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exclusive_or_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			and_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==BXOR))
					{
						op = LT(1);
						op_AST = astFactory.create(op);
						match(BXOR);
						and_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							exclusive_or_expression_AST = (AST)currentAST.root;
							exclusive_or_expression_AST = 
									(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), exclusive_or_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = exclusive_or_expression_AST;
							if ( (null != exclusive_or_expression_AST) && (null != exclusive_or_expression_AST.getFirstChild()) )
								currentAST.child = exclusive_or_expression_AST.getFirstChild();
							else
								currentAST.child = exclusive_or_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop110_breakloop;
					}
					
				}
_loop110_breakloop:				;
			}    // ( ... )*
			exclusive_or_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_35_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = exclusive_or_expression_AST;
	}
	
	public void inclusive_or_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inclusive_or_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			exclusive_or_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==BOR))
					{
						op = LT(1);
						op_AST = astFactory.create(op);
						match(BOR);
						exclusive_or_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							inclusive_or_expression_AST = (AST)currentAST.root;
							inclusive_or_expression_AST = 
									(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), inclusive_or_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = inclusive_or_expression_AST;
							if ( (null != inclusive_or_expression_AST) && (null != inclusive_or_expression_AST.getFirstChild()) )
								currentAST.child = inclusive_or_expression_AST.getFirstChild();
							else
								currentAST.child = inclusive_or_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop113_breakloop;
					}
					
				}
_loop113_breakloop:				;
			}    // ( ... )*
			inclusive_or_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_36_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = inclusive_or_expression_AST;
	}
	
	public void conditional_and_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditional_and_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			inclusive_or_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==LAND))
					{
						op = LT(1);
						op_AST = astFactory.create(op);
						match(LAND);
						inclusive_or_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							conditional_and_expression_AST = (AST)currentAST.root;
							conditional_and_expression_AST = 
									(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), conditional_and_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = conditional_and_expression_AST;
							if ( (null != conditional_and_expression_AST) && (null != conditional_and_expression_AST.getFirstChild()) )
								currentAST.child = conditional_and_expression_AST.getFirstChild();
							else
								currentAST.child = conditional_and_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop116_breakloop;
					}
					
				}
_loop116_breakloop:				;
			}    // ( ... )*
			conditional_and_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_37_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = conditional_and_expression_AST;
	}
	
	public void conditional_or_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditional_or_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			conditional_and_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==LOR))
					{
						op = LT(1);
						op_AST = astFactory.create(op);
						match(LOR);
						conditional_and_expression();
						if (0 == inputState.guessing)
						{
							ex_AST = (AST)returnAST;
						}
						if (0==inputState.guessing)
						{
							conditional_or_expression_AST = (AST)currentAST.root;
							conditional_or_expression_AST = 
									(AST) astFactory.make((DDW.CSharp.Parse.BinaryExpr) astFactory.create(BinaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), conditional_or_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST))
									 ;
							currentAST.root = conditional_or_expression_AST;
							if ( (null != conditional_or_expression_AST) && (null != conditional_or_expression_AST.getFirstChild()) )
								currentAST.child = conditional_or_expression_AST.getFirstChild();
							else
								currentAST.child = conditional_or_expression_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else
					{
						goto _loop119_breakloop;
					}
					
				}
_loop119_breakloop:				;
			}    // ( ... )*
			conditional_or_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_38_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = conditional_or_expression_AST;
	}
	
	public void conditional_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditional_expression_AST = null;
		IToken  op = null;
		AST op_AST = null;
		AST ex1_AST = null;
		AST ex2_AST = null;
		
		try {      // for error handling
			conditional_or_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{
				switch ( LA(1) )
				{
				case QUESTION:
				{
					op = LT(1);
					op_AST = astFactory.create(op);
					match(QUESTION);
					expression();
					if (0 == inputState.guessing)
					{
						ex1_AST = (AST)returnAST;
					}
					match(COLON);
					expression();
					if (0 == inputState.guessing)
					{
						ex2_AST = (AST)returnAST;
					}
					if (0==inputState.guessing)
					{
						conditional_expression_AST = (AST)currentAST.root;
						conditional_expression_AST = 
								(AST) astFactory.make((DDW.CSharp.Parse.TernaryExpr) astFactory.create(TernaryExpr,op_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"test"), conditional_expression_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"true"), ex1_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"false"), ex2_AST))
							;
						currentAST.root = conditional_expression_AST;
						if ( (null != conditional_expression_AST) && (null != conditional_expression_AST.getFirstChild()) )
							currentAST.child = conditional_expression_AST.getFirstChild();
						else
							currentAST.child = conditional_expression_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case IDENTIFIER:
				case COMMA:
				case RBRACK:
				case RPAREN:
				case COLON:
				case RBRACE:
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			conditional_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_18_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = conditional_expression_AST;
	}
	
	public void assignment() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_AST = null;
		AST ue_AST = null;
		AST ao_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			unary_expression();
			if (0 == inputState.guessing)
			{
				ue_AST = (AST)returnAST;
			}
			assignment_operator();
			if (0 == inputState.guessing)
			{
				ao_AST = (AST)returnAST;
			}
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				assignment_AST = (AST)currentAST.root;
				assignment_AST = 
						(AST) astFactory.make((DDW.CSharp.Parse.AssignExpr) astFactory.create(AssignExpr,ao_AST.getText()), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"left"), ue_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"right"), ex_AST));
					
				currentAST.root = assignment_AST;
				if ( (null != assignment_AST) && (null != assignment_AST.getFirstChild()) )
					currentAST.child = assignment_AST.getFirstChild();
				else
					currentAST.child = assignment_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_18_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = assignment_AST;
	}
	
	public void assignment_operator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_operator_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case ASSIGN:
			{
				AST tmp106_AST = null;
				tmp106_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp106_AST);
				match(ASSIGN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case PLUS_ASN:
			{
				AST tmp107_AST = null;
				tmp107_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp107_AST);
				match(PLUS_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case MINUS_ASN:
			{
				AST tmp108_AST = null;
				tmp108_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp108_AST);
				match(MINUS_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case STAR_ASN:
			{
				AST tmp109_AST = null;
				tmp109_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp109_AST);
				match(STAR_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case DIV_ASN:
			{
				AST tmp110_AST = null;
				tmp110_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp110_AST);
				match(DIV_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case MOD_ASN:
			{
				AST tmp111_AST = null;
				tmp111_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp111_AST);
				match(MOD_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case BAND_ASN:
			{
				AST tmp112_AST = null;
				tmp112_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp112_AST);
				match(BAND_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case BOR_ASN:
			{
				AST tmp113_AST = null;
				tmp113_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp113_AST);
				match(BOR_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case BXOR_ASN:
			{
				AST tmp114_AST = null;
				tmp114_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp114_AST);
				match(BXOR_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case SL_ASN:
			{
				AST tmp115_AST = null;
				tmp115_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp115_AST);
				match(SL_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			case SR_ASN:
			{
				AST tmp116_AST = null;
				tmp116_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp116_AST);
				match(SR_ASN);
				assignment_operator_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_15_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = assignment_operator_AST;
	}
	
	public void constant_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_expression_AST = null;
		
		try {      // for error handling
			expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			constant_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_39_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constant_expression_AST;
	}
	
	public void boolean_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST boolean_expression_AST = null;
		
		try {      // for error handling
			expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			boolean_expression_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_40_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = boolean_expression_AST;
	}
	
	public void statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			if ((LA(1)==IDENTIFIER) && (LA(2)==COLON))
			{
				labeled_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				statement_AST = currentAST.root;
			}
			else {
				bool synPredMatched131 = false;
				if (((tokenSet_41_.member(LA(1))) && (tokenSet_42_.member(LA(2)))))
				{
					int _m131 = mark();
					synPredMatched131 = true;
					inputState.guessing++;
					try {
						{
							declaration_statement();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched131 = false;
					}
					rewind(_m131);
					inputState.guessing--;
				}
				if ( synPredMatched131 )
				{
					declaration_statement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					statement_AST = currentAST.root;
				}
				else if ((tokenSet_43_.member(LA(1))) && (tokenSet_44_.member(LA(2)))) {
					embedded_statement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					statement_AST = currentAST.root;
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex)
			{
				if (0 == inputState.guessing)
				{
					reportError(ex);
					recover(ex,tokenSet_45_);
				}
				else
				{
					throw ex;
				}
			}
			returnAST = statement_AST;
		}
		
	public void labeled_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST labeled_statement_AST = null;
		AST id_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp117_AST = null;
			tmp117_AST = astFactory.create(LT(1));
			match(COLON);
			statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				labeled_statement_AST = (AST)currentAST.root;
				labeled_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.LabeledStmt) astFactory.create(LabeledStmt)), id_AST, st_AST);
						
				currentAST.root = labeled_statement_AST;
				if ( (null != labeled_statement_AST) && (null != labeled_statement_AST.getFirstChild()) )
					currentAST.child = labeled_statement_AST.getFirstChild();
				else
					currentAST.child = labeled_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_45_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = labeled_statement_AST;
	}
	
	public void declaration_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			{
				local_variable_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(SEMI);
				declaration_statement_AST = currentAST.root;
				break;
			}
			case CONST:
			{
				local_constant_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(SEMI);
				declaration_statement_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_45_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = declaration_statement_AST;
	}
	
	public void embedded_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST embedded_statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				empty_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case IF:
			case SWITCH:
			{
				selection_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case WHILE:
			case DO:
			case FOR:
			case FOREACH:
			{
				iteration_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case BREAK:
			case CONTINUE:
			case GOTO:
			case RETURN:
			case THROW:
			{
				jump_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case TRY:
			{
				try_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case LOCK:
			{
				lock_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			case USING:
			{
				using_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				embedded_statement_AST = currentAST.root;
				break;
			}
			default:
				if ((tokenSet_15_.member(LA(1))) && (tokenSet_46_.member(LA(2))))
				{
					expression_statement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					embedded_statement_AST = currentAST.root;
				}
				else if ((LA(1)==CHECKED) && (LA(2)==LBRACE)) {
					checked_statement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					embedded_statement_AST = currentAST.root;
				}
				else if ((LA(1)==UNCHECKED) && (LA(2)==LBRACE)) {
					unchecked_statement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					embedded_statement_AST = currentAST.root;
				}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			break; }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = embedded_statement_AST;
	}
	
	public void block() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST block_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			statement_list();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			match(RBRACE);
			block_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_48_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = block_AST;
	}
	
	public void empty_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST empty_statement_AST = null;
		
		try {      // for error handling
			match(SEMI);
			empty_statement_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = empty_statement_AST;
	}
	
	public void expression_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_statement_AST = null;
		AST se_AST = null;
		
		try {      // for error handling
			statement_expression();
			if (0 == inputState.guessing)
			{
				se_AST = (AST)returnAST;
			}
			match(SEMI);
			if (0==inputState.guessing)
			{
				expression_statement_AST = (AST)currentAST.root;
				expression_statement_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.ExprStmt) astFactory.create(ExprStmt), se_AST);
						
				currentAST.root = expression_statement_AST;
				if ( (null != expression_statement_AST) && (null != expression_statement_AST.getFirstChild()) )
					currentAST.child = expression_statement_AST.getFirstChild();
				else
					currentAST.child = expression_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = expression_statement_AST;
	}
	
	public void selection_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selection_statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IF:
			{
				if_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				selection_statement_AST = currentAST.root;
				break;
			}
			case SWITCH:
			{
				switch_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				selection_statement_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = selection_statement_AST;
	}
	
	public void iteration_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteration_statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case WHILE:
			{
				while_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				iteration_statement_AST = currentAST.root;
				break;
			}
			case DO:
			{
				do_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				iteration_statement_AST = currentAST.root;
				break;
			}
			case FOR:
			{
				for_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				iteration_statement_AST = currentAST.root;
				break;
			}
			case FOREACH:
			{
				foreach_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				iteration_statement_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = iteration_statement_AST;
	}
	
	public void jump_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST jump_statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case BREAK:
			{
				break_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				jump_statement_AST = currentAST.root;
				break;
			}
			case CONTINUE:
			{
				continue_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				jump_statement_AST = currentAST.root;
				break;
			}
			case GOTO:
			{
				goto_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				jump_statement_AST = currentAST.root;
				break;
			}
			case RETURN:
			{
				return_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				jump_statement_AST = currentAST.root;
				break;
			}
			case THROW:
			{
				throw_statement();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				jump_statement_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = jump_statement_AST;
	}
	
	public void try_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST try_statement_AST = null;
		AST ts_AST = null;
		AST cc_AST = null;
		AST fc_AST = null;
		
		try {      // for error handling
			AST tmp124_AST = null;
			tmp124_AST = astFactory.create(LT(1));
			match(TRY);
			block();
			if (0 == inputState.guessing)
			{
				ts_AST = (AST)returnAST;
			}
			catch_clauses();
			if (0 == inputState.guessing)
			{
				cc_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case FINALLY:
				{
					finally_clause();
					if (0 == inputState.guessing)
					{
						fc_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case LBRACE:
				case RBRACE:
				case SEMI:
				case CONST:
				case IF:
				case ELSE:
				case SWITCH:
				case CASE:
				case DEFAULT:
				case WHILE:
				case DO:
				case FOR:
				case FOREACH:
				case BREAK:
				case CONTINUE:
				case GOTO:
				case RETURN:
				case THROW:
				case LOCK:
				case USING:
				case TRY:
				case SIZEOF:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				try_statement_AST = (AST)currentAST.root;
				try_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.TryCatchFinallyStmt) astFactory.create(TryCatchFinallyStmt)), (AST) astFactory.make((DDW.CSharp.Parse.TryStmt) astFactory.create(TryStmt), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), ts_AST)), cc_AST, fc_AST);
				currentAST.root = try_statement_AST;
				if ( (null != try_statement_AST) && (null != try_statement_AST.getFirstChild()) )
					currentAST.child = try_statement_AST.getFirstChild();
				else
					currentAST.child = try_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = try_statement_AST;
	}
	
	public void checked_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST checked_statement_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp125_AST = null;
			tmp125_AST = astFactory.create(LT(1));
			match(CHECKED);
			block();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				checked_statement_AST = (AST)currentAST.root;
				checked_statement_AST = (AST) astFactory.make((DDW.CSharp.Parse.CheckedStmt) astFactory.create(CheckedStmt), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = checked_statement_AST;
				if ( (null != checked_statement_AST) && (null != checked_statement_AST.getFirstChild()) )
					currentAST.child = checked_statement_AST.getFirstChild();
				else
					currentAST.child = checked_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = checked_statement_AST;
	}
	
	public void unchecked_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unchecked_statement_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp126_AST = null;
			tmp126_AST = astFactory.create(LT(1));
			match(UNCHECKED);
			block();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				unchecked_statement_AST = (AST)currentAST.root;
				unchecked_statement_AST = (AST) astFactory.make((DDW.CSharp.Parse.UncheckedStmt) astFactory.create(UncheckedStmt), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = unchecked_statement_AST;
				if ( (null != unchecked_statement_AST) && (null != unchecked_statement_AST.getFirstChild()) )
					currentAST.child = unchecked_statement_AST.getFirstChild();
				else
					currentAST.child = unchecked_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = unchecked_statement_AST;
	}
	
	public void lock_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lock_statement_AST = null;
		AST ex_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp127_AST = null;
			tmp127_AST = astFactory.create(LT(1));
			match(LOCK);
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			AST tmp129_AST = null;
			tmp129_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				lock_statement_AST = (AST)currentAST.root;
				lock_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.LockStmt) astFactory.create(LockStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ex_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = lock_statement_AST;
				if ( (null != lock_statement_AST) && (null != lock_statement_AST.getFirstChild()) )
					currentAST.child = lock_statement_AST.getFirstChild();
				else
					currentAST.child = lock_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = lock_statement_AST;
	}
	
	public void using_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST using_statement_AST = null;
		AST ra_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp130_AST = null;
			tmp130_AST = astFactory.create(LT(1));
			match(USING);
			AST tmp131_AST = null;
			tmp131_AST = astFactory.create(LT(1));
			match(LPAREN);
			resource_acquisition();
			if (0 == inputState.guessing)
			{
				ra_AST = (AST)returnAST;
			}
			AST tmp132_AST = null;
			tmp132_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				using_statement_AST = (AST)currentAST.root;
				using_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.UsingStmt) astFactory.create(UsingStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ra_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = using_statement_AST;
				if ( (null != using_statement_AST) && (null != using_statement_AST.getFirstChild()) )
					currentAST.child = using_statement_AST.getFirstChild();
				else
					currentAST.child = using_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = using_statement_AST;
	}
	
	public void statement_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_list_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_49_.member(LA(1))))
					{
						statement();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop136_breakloop;
					}
					
				}
_loop136_breakloop:				;
			}    // ( ... )*
			statement_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_50_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = statement_list_AST;
	}
	
	public void local_variable_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST local_variable_declaration_AST = null;
		AST tp_AST = null;
		AST lvd_AST = null;
		
		try {      // for error handling
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			local_variable_declarators();
			if (0 == inputState.guessing)
			{
				lvd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				local_variable_declaration_AST = (AST)currentAST.root;
				local_variable_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.VariableDeclStmt) astFactory.create(VariableDeclStmt)), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), lvd_AST);
						
				currentAST.root = local_variable_declaration_AST;
				if ( (null != local_variable_declaration_AST) && (null != local_variable_declaration_AST.getFirstChild()) )
					currentAST.child = local_variable_declaration_AST.getFirstChild();
				else
					currentAST.child = local_variable_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_40_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = local_variable_declaration_AST;
	}
	
	public void local_constant_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST local_constant_declaration_AST = null;
		AST tp_AST = null;
		AST cd_AST = null;
		
		try {      // for error handling
			AST tmp133_AST = null;
			tmp133_AST = astFactory.create(LT(1));
			match(CONST);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			constant_declarators();
			if (0 == inputState.guessing)
			{
				cd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				local_constant_declaration_AST = (AST)currentAST.root;
				local_constant_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ConstantDeclStmt) astFactory.create(ConstantDeclStmt)), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), cd_AST);
						
				currentAST.root = local_constant_declaration_AST;
				if ( (null != local_constant_declaration_AST) && (null != local_constant_declaration_AST.getFirstChild()) )
					currentAST.child = local_constant_declaration_AST.getFirstChild();
				else
					currentAST.child = local_constant_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = local_constant_declaration_AST;
	}
	
	public void local_variable_declarators() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST local_variable_declarators_AST = null;
		
		try {      // for error handling
			local_variable_declarator();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						local_variable_declarator();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop143_breakloop;
					}
					
				}
_loop143_breakloop:				;
			}    // ( ... )*
			local_variable_declarators_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_40_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = local_variable_declarators_AST;
	}
	
	public void local_variable_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST local_variable_declarator_AST = null;
		AST id_AST = null;
		AST lvi_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case ASSIGN:
				{
					AST tmp135_AST = null;
					tmp135_AST = astFactory.create(LT(1));
					match(ASSIGN);
					local_variable_initializer();
					if (0 == inputState.guessing)
					{
						lvi_AST = (AST)returnAST;
					}
					break;
				}
				case COMMA:
				case RPAREN:
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				local_variable_declarator_AST = (AST)currentAST.root;
				local_variable_declarator_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.Declarator) astFactory.create(Declarator), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), lvi_AST));
						
				currentAST.root = local_variable_declarator_AST;
				if ( (null != local_variable_declarator_AST) && (null != local_variable_declarator_AST.getFirstChild()) )
					currentAST.child = local_variable_declarator_AST.getFirstChild();
				else
					currentAST.child = local_variable_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = local_variable_declarator_AST;
	}
	
	public void local_variable_initializer() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST local_variable_initializer_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case INTEGER_LITERAL:
			case HEXADECIMAL_INTEGER_LITERAL:
			case REAL_LITERAL:
			case CHARACTER_LITERAL:
			case STRING_LITERAL:
			case TRUE:
			case FALSE:
			case NULL:
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			case LPAREN:
			case THIS:
			case BASE:
			case INC:
			case DEC:
			case NEW:
			case TYPEOF:
			case CHECKED:
			case UNCHECKED:
			case PLUS:
			case MINUS:
			case LNOT:
			case BNOT:
			case STAR:
			case SIZEOF:
			{
				expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				local_variable_initializer_AST = currentAST.root;
				break;
			}
			case LBRACE:
			{
				array_initializer();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				local_variable_initializer_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = local_variable_initializer_AST;
	}
	
	public void constant_declarators() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_declarators_AST = null;
		
		try {      // for error handling
			constant_declarator();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						constant_declarator();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop150_breakloop;
					}
					
				}
_loop150_breakloop:				;
			}    // ( ... )*
			constant_declarators_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constant_declarators_AST;
	}
	
	public void constant_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_declarator_AST = null;
		AST id_AST = null;
		AST ce_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp137_AST = null;
			tmp137_AST = astFactory.create(LT(1));
			match(ASSIGN);
			constant_expression();
			if (0 == inputState.guessing)
			{
				ce_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				constant_declarator_AST = (AST)currentAST.root;
				constant_declarator_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.Declarator) astFactory.create(Declarator), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ce_AST));
						
				currentAST.root = constant_declarator_AST;
				if ( (null != constant_declarator_AST) && (null != constant_declarator_AST.getFirstChild()) )
					currentAST.child = constant_declarator_AST.getFirstChild();
				else
					currentAST.child = constant_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_51_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constant_declarator_AST;
	}
	
	public void statement_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_expression_AST = null;
		
		try {      // for error handling
			bool synPredMatched155 = false;
			if (((tokenSet_15_.member(LA(1))) && (tokenSet_17_.member(LA(2)))))
			{
				int _m155 = mark();
				synPredMatched155 = true;
				inputState.guessing++;
				try {
					{
						assignment();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched155 = false;
				}
				rewind(_m155);
				inputState.guessing--;
			}
			if ( synPredMatched155 )
			{
				assignment();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				statement_expression_AST = currentAST.root;
			}
			else if ((tokenSet_25_.member(LA(1))) && (tokenSet_52_.member(LA(2)))) {
				primary_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				statement_expression_AST = currentAST.root;
			}
			else if ((LA(1)==INC) && (tokenSet_15_.member(LA(2)))) {
				pre_increment_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				statement_expression_AST = currentAST.root;
			}
			else if ((LA(1)==DEC) && (tokenSet_15_.member(LA(2)))) {
				pre_decrement_expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				statement_expression_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = statement_expression_AST;
	}
	
	public void if_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST if_statement_AST = null;
		AST cond_AST = null;
		AST tr_AST = null;
		AST fl_AST = null;
		
		try {      // for error handling
			AST tmp138_AST = null;
			tmp138_AST = astFactory.create(LT(1));
			match(IF);
			AST tmp139_AST = null;
			tmp139_AST = astFactory.create(LT(1));
			match(LPAREN);
			boolean_expression();
			if (0 == inputState.guessing)
			{
				cond_AST = (AST)returnAST;
			}
			AST tmp140_AST = null;
			tmp140_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				tr_AST = (AST)returnAST;
			}
			{
				if ((LA(1)==ELSE) && (tokenSet_43_.member(LA(2))))
				{
					AST tmp141_AST = null;
					tmp141_AST = astFactory.create(LT(1));
					match(ELSE);
					embedded_statement();
					if (0 == inputState.guessing)
					{
						fl_AST = (AST)returnAST;
					}
				}
				else if ((tokenSet_47_.member(LA(1))) && (tokenSet_53_.member(LA(2)))) {
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
			if (0==inputState.guessing)
			{
				if_statement_AST = (AST)currentAST.root;
				if_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IfStmt) astFactory.create(IfStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), cond_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements,"true"), tr_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements,"false"), fl_AST));
						
				currentAST.root = if_statement_AST;
				if ( (null != if_statement_AST) && (null != if_statement_AST.getFirstChild()) )
					currentAST.child = if_statement_AST.getFirstChild();
				else
					currentAST.child = if_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = if_statement_AST;
	}
	
	public void switch_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switch_statement_AST = null;
		AST cond_AST = null;
		AST sb_AST = null;
		
		try {      // for error handling
			AST tmp142_AST = null;
			tmp142_AST = astFactory.create(LT(1));
			match(SWITCH);
			AST tmp143_AST = null;
			tmp143_AST = astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (0 == inputState.guessing)
			{
				cond_AST = (AST)returnAST;
			}
			AST tmp144_AST = null;
			tmp144_AST = astFactory.create(LT(1));
			match(RPAREN);
			switch_block();
			if (0 == inputState.guessing)
			{
				sb_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				switch_statement_AST = (AST)currentAST.root;
				switch_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.SwitchStmt) astFactory.create(SwitchStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), cond_AST), sb_AST);
						
				currentAST.root = switch_statement_AST;
				if ( (null != switch_statement_AST) && (null != switch_statement_AST.getFirstChild()) )
					currentAST.child = switch_statement_AST.getFirstChild();
				else
					currentAST.child = switch_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = switch_statement_AST;
	}
	
	public void switch_block() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switch_block_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			{
				switch ( LA(1) )
				{
				case CASE:
				case DEFAULT:
				{
					{ // ( ... )+
						int _cnt163=0;
						for (;;)
						{
							if ((LA(1)==CASE||LA(1)==DEFAULT))
							{
								switch_section();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
							}
							else
							{
								if (_cnt163 >= 1) { goto _loop163_breakloop; } else { throw new NoViableAltException(LT(1), getFilename());; }
							}
							
							_cnt163++;
						}
_loop163_breakloop:						;
					}    // ( ... )+
					break;
				}
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			match(RBRACE);
			switch_block_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = switch_block_AST;
	}
	
	public void switch_section() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switch_section_AST = null;
		AST sc_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			switch_cases();
			if (0 == inputState.guessing)
			{
				sc_AST = (AST)returnAST;
			}
			statement_list();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				switch_section_AST = (AST)currentAST.root;
				switch_section_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.SwitchSection) astFactory.create(SwitchSection)), sc_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = switch_section_AST;
				if ( (null != switch_section_AST) && (null != switch_section_AST.getFirstChild()) )
					currentAST.child = switch_section_AST.getFirstChild();
				else
					currentAST.child = switch_section_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_50_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = switch_section_AST;
	}
	
	public void switch_cases() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switch_cases_AST = null;
		
		try {      // for error handling
			{ // ( ... )+
				int _cnt167=0;
				for (;;)
				{
					if ((LA(1)==CASE||LA(1)==DEFAULT) && (tokenSet_54_.member(LA(2))))
					{
						switch_label();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						match(COLON);
					}
					else
					{
						if (_cnt167 >= 1) { goto _loop167_breakloop; } else { throw new NoViableAltException(LT(1), getFilename());; }
					}
					
					_cnt167++;
				}
_loop167_breakloop:				;
			}    // ( ... )+
			switch_cases_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_45_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = switch_cases_AST;
	}
	
	public void switch_label() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switch_label_AST = null;
		AST ce_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case CASE:
			{
				AST tmp148_AST = null;
				tmp148_AST = astFactory.create(LT(1));
				match(CASE);
				constant_expression();
				if (0 == inputState.guessing)
				{
					ce_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					switch_label_AST = (AST)currentAST.root;
					switch_label_AST = 
								(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"case")), ce_AST);
							
					currentAST.root = switch_label_AST;
					if ( (null != switch_label_AST) && (null != switch_label_AST.getFirstChild()) )
						currentAST.child = switch_label_AST.getFirstChild();
					else
						currentAST.child = switch_label_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case DEFAULT:
			{
				AST tmp149_AST = null;
				tmp149_AST = astFactory.create(LT(1));
				match(DEFAULT);
				if (0==inputState.guessing)
				{
					switch_label_AST = (AST)currentAST.root;
					switch_label_AST = 
								(AST) astFactory.make(switch_label_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"default")));
							
					currentAST.root = switch_label_AST;
					if ( (null != switch_label_AST) && (null != switch_label_AST.getFirstChild()) )
						currentAST.child = switch_label_AST.getFirstChild();
					else
						currentAST.child = switch_label_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_55_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = switch_label_AST;
	}
	
	public void while_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST while_statement_AST = null;
		AST tst_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp150_AST = null;
			tmp150_AST = astFactory.create(LT(1));
			match(WHILE);
			AST tmp151_AST = null;
			tmp151_AST = astFactory.create(LT(1));
			match(LPAREN);
			boolean_expression();
			if (0 == inputState.guessing)
			{
				tst_AST = (AST)returnAST;
			}
			AST tmp152_AST = null;
			tmp152_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				while_statement_AST = (AST)currentAST.root;
				while_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IterationStmt) astFactory.create(IterationStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"while"), tst_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = while_statement_AST;
				if ( (null != while_statement_AST) && (null != while_statement_AST.getFirstChild()) )
					currentAST.child = while_statement_AST.getFirstChild();
				else
					currentAST.child = while_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = while_statement_AST;
	}
	
	public void do_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST do_statement_AST = null;
		AST st_AST = null;
		AST tst_AST = null;
		
		try {      // for error handling
			AST tmp153_AST = null;
			tmp153_AST = astFactory.create(LT(1));
			match(DO);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			AST tmp154_AST = null;
			tmp154_AST = astFactory.create(LT(1));
			match(WHILE);
			AST tmp155_AST = null;
			tmp155_AST = astFactory.create(LT(1));
			match(LPAREN);
			boolean_expression();
			if (0 == inputState.guessing)
			{
				tst_AST = (AST)returnAST;
			}
			AST tmp156_AST = null;
			tmp156_AST = astFactory.create(LT(1));
			match(RPAREN);
			AST tmp157_AST = null;
			tmp157_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				do_statement_AST = (AST)currentAST.root;
				do_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IterationStmt) astFactory.create(IterationStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"do"), tst_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = do_statement_AST;
				if ( (null != do_statement_AST) && (null != do_statement_AST.getFirstChild()) )
					currentAST.child = do_statement_AST.getFirstChild();
				else
					currentAST.child = do_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = do_statement_AST;
	}
	
	public void for_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST for_statement_AST = null;
		AST ini_AST = null;
		AST tst_AST = null;
		AST inc_AST = null;
		AST stm_AST = null;
		
		try {      // for error handling
			AST tmp158_AST = null;
			tmp158_AST = astFactory.create(LT(1));
			match(FOR);
			AST tmp159_AST = null;
			tmp159_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					for_initializer();
					if (0 == inputState.guessing)
					{
						ini_AST = (AST)returnAST;
					}
					break;
				}
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp160_AST = null;
			tmp160_AST = astFactory.create(LT(1));
			match(SEMI);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					for_condition();
					if (0 == inputState.guessing)
					{
						tst_AST = (AST)returnAST;
					}
					break;
				}
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp161_AST = null;
			tmp161_AST = astFactory.create(LT(1));
			match(SEMI);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					for_iterator();
					if (0 == inputState.guessing)
					{
						inc_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp162_AST = null;
			tmp162_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				stm_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				for_statement_AST = (AST)currentAST.root;
				for_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IterationStmt) astFactory.create(IterationStmt)), (AST) astFactory.make((DDW.CSharp.Parse.InitStmt) astFactory.create(InitStmt), ini_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"for"), tst_AST), (AST) astFactory.make((DDW.CSharp.Parse.IncStmt) astFactory.create(IncStmt), inc_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), stm_AST));
						
				currentAST.root = for_statement_AST;
				if ( (null != for_statement_AST) && (null != for_statement_AST.getFirstChild()) )
					currentAST.child = for_statement_AST.getFirstChild();
				else
					currentAST.child = for_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = for_statement_AST;
	}
	
	public void foreach_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST foreach_statement_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST ex_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp163_AST = null;
			tmp163_AST = astFactory.create(LT(1));
			match(FOREACH);
			AST tmp164_AST = null;
			tmp164_AST = astFactory.create(LT(1));
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp165_AST = null;
			tmp165_AST = astFactory.create(LT(1));
			match(IN);
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			AST tmp166_AST = null;
			tmp166_AST = astFactory.create(LT(1));
			match(RPAREN);
			embedded_statement();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				foreach_statement_AST = (AST)currentAST.root;
				foreach_statement_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ForEachStmt) astFactory.create(ForEachStmt)), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"foreach"), ex_AST), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = foreach_statement_AST;
				if ( (null != foreach_statement_AST) && (null != foreach_statement_AST.getFirstChild()) )
					currentAST.child = foreach_statement_AST.getFirstChild();
				else
					currentAST.child = foreach_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = foreach_statement_AST;
	}
	
	public void for_initializer() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST for_initializer_AST = null;
		
		try {      // for error handling
			bool synPredMatched178 = false;
			if (((tokenSet_7_.member(LA(1))) && (LA(2)==IDENTIFIER||LA(2)==DOT||LA(2)==LBRACK)))
			{
				int _m178 = mark();
				synPredMatched178 = true;
				inputState.guessing++;
				try {
					{
						local_variable_declaration();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched178 = false;
				}
				rewind(_m178);
				inputState.guessing--;
			}
			if ( synPredMatched178 )
			{
				local_variable_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				for_initializer_AST = currentAST.root;
			}
			else if ((tokenSet_15_.member(LA(1))) && (tokenSet_56_.member(LA(2)))) {
				statement_expression_list();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				for_initializer_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = for_initializer_AST;
	}
	
	public void for_condition() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST for_condition_AST = null;
		
		try {      // for error handling
			boolean_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			for_condition_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = for_condition_AST;
	}
	
	public void for_iterator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST for_iterator_AST = null;
		
		try {      // for error handling
			statement_expression_list();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			for_iterator_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_19_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = for_iterator_AST;
	}
	
	public void statement_expression_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_expression_list_AST = null;
		
		try {      // for error handling
			annotated_statement_expression();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						annotated_statement_expression();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop183_breakloop;
					}
					
				}
_loop183_breakloop:				;
			}    // ( ... )*
			statement_expression_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_40_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = statement_expression_list_AST;
	}
	
	public void annotated_statement_expression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotated_statement_expression_AST = null;
		AST se_AST = null;
		
		try {      // for error handling
			statement_expression();
			if (0 == inputState.guessing)
			{
				se_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				annotated_statement_expression_AST = (AST)currentAST.root;
					annotated_statement_expression_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ExprStmt) astFactory.create(ExprStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), se_AST));	
				currentAST.root = annotated_statement_expression_AST;
				if ( (null != annotated_statement_expression_AST) && (null != annotated_statement_expression_AST.getFirstChild()) )
					currentAST.child = annotated_statement_expression_AST.getFirstChild();
				else
					currentAST.child = annotated_statement_expression_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_28_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = annotated_statement_expression_AST;
	}
	
	public void break_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST break_statement_AST = null;
		
		try {      // for error handling
			AST tmp168_AST = null;
			tmp168_AST = astFactory.create(LT(1));
			match(BREAK);
			AST tmp169_AST = null;
			tmp169_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				break_statement_AST = (AST)currentAST.root;
				break_statement_AST = (AST) astFactory.make((DDW.CSharp.Parse.BreakStmt) astFactory.create(BreakStmt));
				currentAST.root = break_statement_AST;
				if ( (null != break_statement_AST) && (null != break_statement_AST.getFirstChild()) )
					currentAST.child = break_statement_AST.getFirstChild();
				else
					currentAST.child = break_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = break_statement_AST;
	}
	
	public void continue_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continue_statement_AST = null;
		
		try {      // for error handling
			AST tmp170_AST = null;
			tmp170_AST = astFactory.create(LT(1));
			match(CONTINUE);
			AST tmp171_AST = null;
			tmp171_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				continue_statement_AST = (AST)currentAST.root;
				continue_statement_AST = (AST) astFactory.make((DDW.CSharp.Parse.ContinueStmt) astFactory.create(ContinueStmt));
				currentAST.root = continue_statement_AST;
				if ( (null != continue_statement_AST) && (null != continue_statement_AST.getFirstChild()) )
					currentAST.child = continue_statement_AST.getFirstChild();
				else
					currentAST.child = continue_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = continue_statement_AST;
	}
	
	public void goto_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST goto_statement_AST = null;
		AST id_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST ce_AST = null;
		IToken  mod2 = null;
		AST mod2_AST = null;
		
		try {      // for error handling
			if ((LA(1)==GOTO) && (LA(2)==IDENTIFIER))
			{
				AST tmp172_AST = null;
				tmp172_AST = astFactory.create(LT(1));
				match(GOTO);
				identifier();
				if (0 == inputState.guessing)
				{
					id_AST = (AST)returnAST;
				}
				AST tmp173_AST = null;
				tmp173_AST = astFactory.create(LT(1));
				match(SEMI);
				if (0==inputState.guessing)
				{
					goto_statement_AST = (AST)currentAST.root;
					goto_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.GotoStmt) astFactory.create(GotoStmt)), id_AST);
					currentAST.root = goto_statement_AST;
					if ( (null != goto_statement_AST) && (null != goto_statement_AST.getFirstChild()) )
						currentAST.child = goto_statement_AST.getFirstChild();
					else
						currentAST.child = goto_statement_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==GOTO) && (LA(2)==CASE)) {
				AST tmp174_AST = null;
				tmp174_AST = astFactory.create(LT(1));
				match(GOTO);
				mod = LT(1);
				mod_AST = astFactory.create(mod);
				match(CASE);
				constant_expression();
				if (0 == inputState.guessing)
				{
					ce_AST = (AST)returnAST;
				}
				AST tmp175_AST = null;
				tmp175_AST = astFactory.create(LT(1));
				match(SEMI);
				if (0==inputState.guessing)
				{
					goto_statement_AST = (AST)currentAST.root;
					goto_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.GotoStmt) astFactory.create(GotoStmt)), (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ce_AST));
					currentAST.root = goto_statement_AST;
					if ( (null != goto_statement_AST) && (null != goto_statement_AST.getFirstChild()) )
						currentAST.child = goto_statement_AST.getFirstChild();
					else
						currentAST.child = goto_statement_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==GOTO) && (LA(2)==DEFAULT)) {
				AST tmp176_AST = null;
				tmp176_AST = astFactory.create(LT(1));
				match(GOTO);
				mod2 = LT(1);
				mod2_AST = astFactory.create(mod2);
				match(DEFAULT);
				AST tmp177_AST = null;
				tmp177_AST = astFactory.create(LT(1));
				match(SEMI);
				if (0==inputState.guessing)
				{
					goto_statement_AST = (AST)currentAST.root;
					goto_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.GotoStmt) astFactory.create(GotoStmt)), (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod2_AST));
					currentAST.root = goto_statement_AST;
					if ( (null != goto_statement_AST) && (null != goto_statement_AST.getFirstChild()) )
						currentAST.child = goto_statement_AST.getFirstChild();
					else
						currentAST.child = goto_statement_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = goto_statement_AST;
	}
	
	public void return_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST return_statement_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp178_AST = null;
			tmp178_AST = astFactory.create(LT(1));
			match(RETURN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					expression();
					if (0 == inputState.guessing)
					{
						ex_AST = (AST)returnAST;
					}
					break;
				}
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp179_AST = null;
			tmp179_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				return_statement_AST = (AST)currentAST.root;
				return_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ReturnStmt) astFactory.create(ReturnStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ex_AST));
				currentAST.root = return_statement_AST;
				if ( (null != return_statement_AST) && (null != return_statement_AST.getFirstChild()) )
					currentAST.child = return_statement_AST.getFirstChild();
				else
					currentAST.child = return_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = return_statement_AST;
	}
	
	public void throw_statement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST throw_statement_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp180_AST = null;
			tmp180_AST = astFactory.create(LT(1));
			match(THROW);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					expression();
					if (0 == inputState.guessing)
					{
						ex_AST = (AST)returnAST;
					}
					break;
				}
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp181_AST = null;
			tmp181_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				throw_statement_AST = (AST)currentAST.root;
				throw_statement_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ThrowStmt) astFactory.create(ThrowStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ex_AST));
				currentAST.root = throw_statement_AST;
				if ( (null != throw_statement_AST) && (null != throw_statement_AST.getFirstChild()) )
					currentAST.child = throw_statement_AST.getFirstChild();
				else
					currentAST.child = throw_statement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = throw_statement_AST;
	}
	
	public void resource_acquisition() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST resource_acquisition_AST = null;
		
		try {      // for error handling
			bool synPredMatched200 = false;
			if (((tokenSet_7_.member(LA(1))) && (LA(2)==IDENTIFIER||LA(2)==DOT||LA(2)==LBRACK)))
			{
				int _m200 = mark();
				synPredMatched200 = true;
				inputState.guessing++;
				try {
					{
						local_variable_declaration();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched200 = false;
				}
				rewind(_m200);
				inputState.guessing--;
			}
			if ( synPredMatched200 )
			{
				local_variable_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				resource_acquisition_AST = currentAST.root;
			}
			else if ((tokenSet_15_.member(LA(1))) && (tokenSet_57_.member(LA(2)))) {
				expression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				resource_acquisition_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_19_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = resource_acquisition_AST;
	}
	
	public void catch_clauses() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST catch_clauses_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==CATCH))
					{
						catch_clause();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop205_breakloop;
					}
					
				}
_loop205_breakloop:				;
			}    // ( ... )*
			catch_clauses_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_58_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = catch_clauses_AST;
	}
	
	public void finally_clause() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST finally_clause_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp182_AST = null;
			tmp182_AST = astFactory.create(LT(1));
			match(FINALLY);
			block();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				finally_clause_AST = (AST)currentAST.root;
				finally_clause_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.FinallyStmt) astFactory.create(FinallyStmt)), (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = finally_clause_AST;
				if ( (null != finally_clause_AST) && (null != finally_clause_AST.getFirstChild()) )
					currentAST.child = finally_clause_AST.getFirstChild();
				else
					currentAST.child = finally_clause_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_47_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = finally_clause_AST;
	}
	
	public void catch_clause() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST catch_clause_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			AST tmp183_AST = null;
			tmp183_AST = astFactory.create(LT(1));
			match(CATCH);
			{
				switch ( LA(1) )
				{
				case LPAREN:
				{
					AST tmp184_AST = null;
					tmp184_AST = astFactory.create(LT(1));
					match(LPAREN);
					class_type();
					if (0 == inputState.guessing)
					{
						tp_AST = (AST)returnAST;
					}
					{
						switch ( LA(1) )
						{
						case IDENTIFIER:
						{
							identifier();
							if (0 == inputState.guessing)
							{
								id_AST = (AST)returnAST;
							}
							break;
						}
						case RPAREN:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						 }
					}
					AST tmp185_AST = null;
					tmp185_AST = astFactory.create(LT(1));
					match(RPAREN);
					break;
				}
				case LBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			block();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				catch_clause_AST = (AST)currentAST.root;
				catch_clause_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.CatchClause) astFactory.create(CatchClause)), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
				currentAST.root = catch_clause_AST;
				if ( (null != catch_clause_AST) && (null != catch_clause_AST.getFirstChild()) )
					currentAST.child = catch_clause_AST.getFirstChild();
				else
					currentAST.child = catch_clause_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_59_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = catch_clause_AST;
	}
	
	public void compilation_unit() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compilation_unit_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==USING))
					{
						using_directive();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop212_breakloop;
					}
					
				}
_loop212_breakloop:				;
			}    // ( ... )*
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==LBRACK) && (LA(2)==ASSEMBLY))
					{
						global_attributes();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop214_breakloop;
					}
					
				}
_loop214_breakloop:				;
			}    // ( ... )*
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_60_.member(LA(1))))
					{
						namespace_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop216_breakloop;
					}
					
				}
_loop216_breakloop:				;
			}    // ( ... )*
			if (0==inputState.guessing)
			{
				compilation_unit_AST = (AST)currentAST.root;
				compilation_unit_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.CompileUnit) astFactory.create(CompileUnit), compilation_unit_AST);
						
				currentAST.root = compilation_unit_AST;
				if ( (null != compilation_unit_AST) && (null != compilation_unit_AST.getFirstChild()) )
					currentAST.child = compilation_unit_AST.getFirstChild();
				else
					currentAST.child = compilation_unit_AST;
				currentAST.advanceChildToEnd();
			}
			compilation_unit_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_1_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = compilation_unit_AST;
	}
	
	public void using_directive() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST using_directive_AST = null;
		AST id_AST = null;
		AST nm_AST = null;
		AST nm2_AST = null;
		
		try {      // for error handling
			AST tmp186_AST = null;
			tmp186_AST = astFactory.create(LT(1));
			match(USING);
			{
				if ((LA(1)==IDENTIFIER) && (LA(2)==ASSIGN))
				{
					identifier();
					if (0 == inputState.guessing)
					{
						id_AST = (AST)returnAST;
					}
					AST tmp187_AST = null;
					tmp187_AST = astFactory.create(LT(1));
					match(ASSIGN);
					namespace_or_type_name();
					if (0 == inputState.guessing)
					{
						nm_AST = (AST)returnAST;
					}
					AST tmp188_AST = null;
					tmp188_AST = astFactory.create(LT(1));
					match(SEMI);
					if (0==inputState.guessing)
					{
						using_directive_AST = (AST)currentAST.root;
						using_directive_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.UsingNode) astFactory.create(UsingNode), (AST) astFactory.make((DDW.CSharp.Parse.Ident) astFactory.create(Ident,"alias"), id_AST), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent,"import"), nm_AST));
									
						currentAST.root = using_directive_AST;
						if ( (null != using_directive_AST) && (null != using_directive_AST.getFirstChild()) )
							currentAST.child = using_directive_AST.getFirstChild();
						else
							currentAST.child = using_directive_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else if ((LA(1)==IDENTIFIER) && (LA(2)==DOT||LA(2)==SEMI)) {
					namespace_name();
					if (0 == inputState.guessing)
					{
						nm2_AST = (AST)returnAST;
					}
					AST tmp189_AST = null;
					tmp189_AST = astFactory.create(LT(1));
					match(SEMI);
					if (0==inputState.guessing)
					{
						using_directive_AST = (AST)currentAST.root;
						using_directive_AST = 
										(AST) astFactory.make((DDW.CSharp.Parse.UsingNode) astFactory.create(UsingNode), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent,"import"), nm2_AST));
									
						currentAST.root = using_directive_AST;
						if ( (null != using_directive_AST) && (null != using_directive_AST.getFirstChild()) )
							currentAST.child = using_directive_AST.getFirstChild();
						else
							currentAST.child = using_directive_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_61_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = using_directive_AST;
	}
	
	public void global_attributes() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST global_attributes_AST = null;
		IToken  asm = null;
		AST asm_AST = null;
		AST al_AST = null;
		
		try {      // for error handling
			AST tmp190_AST = null;
			tmp190_AST = astFactory.create(LT(1));
			match(LBRACK);
			asm = LT(1);
			asm_AST = astFactory.create(asm);
			match(ASSEMBLY);
			AST tmp191_AST = null;
			tmp191_AST = astFactory.create(LT(1));
			match(COLON);
			attribute_list();
			if (0 == inputState.guessing)
			{
				al_AST = (AST)returnAST;
			}
			AST tmp192_AST = null;
			tmp192_AST = astFactory.create(LT(1));
			match(RBRACK);
			if (0==inputState.guessing)
			{
				global_attributes_AST = (AST)currentAST.root;
				global_attributes_AST = (AST) astFactory.make((DDW.CSharp.Parse.CustomAttributes) astFactory.create(CustomAttributes), (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), asm_AST), al_AST);
				currentAST.root = global_attributes_AST;
				if ( (null != global_attributes_AST) && (null != global_attributes_AST.getFirstChild()) )
					currentAST.child = global_attributes_AST.getFirstChild();
				else
					currentAST.child = global_attributes_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_62_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = global_attributes_AST;
	}
	
	public void namespace_member_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_member_declaration_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case NAMESPACE:
			{
				namespace_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				namespace_member_declaration_AST = currentAST.root;
				break;
			}
			case LBRACK:
			case NEW:
			case CLASS:
			case PUBLIC:
			case PROTECTED:
			case INTERNAL:
			case PRIVATE:
			case ABSTRACT:
			case SEALED:
			case STRUCT:
			case INTERFACE:
			case ENUM:
			case DELEGATE:
			{
				type_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				namespace_member_declaration_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_63_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = namespace_member_declaration_AST;
	}
	
	public void namespace_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_declaration_AST = null;
		AST qi_AST = null;
		AST ud_AST = null;
		AST bd_AST = null;
		
		try {      // for error handling
			AST tmp193_AST = null;
			tmp193_AST = astFactory.create(LT(1));
			match(NAMESPACE);
			qualified_identifier();
			if (0 == inputState.guessing)
			{
				qi_AST = (AST)returnAST;
			}
			match(LBRACE);
			using_directives();
			if (0 == inputState.guessing)
			{
				ud_AST = (AST)returnAST;
			}
			namespace_body();
			if (0 == inputState.guessing)
			{
				bd_AST = (AST)returnAST;
			}
			match(RBRACE);
			{
				switch ( LA(1) )
				{
				case SEMI:
				{
					match(SEMI);
					break;
				}
				case EOF:
				case LBRACK:
				case NEW:
				case RBRACE:
				case NAMESPACE:
				case CLASS:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				case STRUCT:
				case INTERFACE:
				case ENUM:
				case DELEGATE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				namespace_declaration_AST = (AST)currentAST.root;
				namespace_declaration_AST = 
							(AST) astFactory.make((DDW.CSharp.Parse.NamespaceNode) astFactory.create(NamespaceNode), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent), qi_AST), ud_AST, (AST) astFactory.make((DDW.CSharp.Parse.Types) astFactory.create(Types), bd_AST)); 
						
				currentAST.root = namespace_declaration_AST;
				if ( (null != namespace_declaration_AST) && (null != namespace_declaration_AST.getFirstChild()) )
					currentAST.child = namespace_declaration_AST.getFirstChild();
				else
					currentAST.child = namespace_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_63_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = namespace_declaration_AST;
	}
	
	public void qualified_identifier() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualified_identifier_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==DOT))
					{
						match(DOT);
						identifier();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop224_breakloop;
					}
					
				}
_loop224_breakloop:				;
			}    // ( ... )*
			qualified_identifier_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = qualified_identifier_AST;
	}
	
	public void using_directives() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST using_directives_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==USING))
					{
						using_directive();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop221_breakloop;
					}
					
				}
_loop221_breakloop:				;
			}    // ( ... )*
			using_directives_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_65_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = using_directives_AST;
	}
	
	public void namespace_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_body_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_60_.member(LA(1))))
					{
						namespace_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop227_breakloop;
					}
					
				}
_loop227_breakloop:				;
			}    // ( ... )*
			namespace_body_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_66_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = namespace_body_AST;
	}
	
	public void type_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_declaration_AST = null;
		
		try {      // for error handling
			{
				bool synPredMatched234 = false;
				if (((tokenSet_67_.member(LA(1))) && (tokenSet_68_.member(LA(2)))))
				{
					int _m234 = mark();
					synPredMatched234 = true;
					inputState.guessing++;
					try {
						{
							class_declaration();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched234 = false;
					}
					rewind(_m234);
					inputState.guessing--;
				}
				if ( synPredMatched234 )
				{
					class_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else {
					bool synPredMatched236 = false;
					if (((tokenSet_69_.member(LA(1))) && (tokenSet_70_.member(LA(2)))))
					{
						int _m236 = mark();
						synPredMatched236 = true;
						inputState.guessing++;
						try {
							{
								struct_declaration();
							}
						}
						catch (RecognitionException)
						{
							synPredMatched236 = false;
						}
						rewind(_m236);
						inputState.guessing--;
					}
					if ( synPredMatched236 )
					{
						struct_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else {
						bool synPredMatched238 = false;
						if (((tokenSet_71_.member(LA(1))) && (tokenSet_72_.member(LA(2)))))
						{
							int _m238 = mark();
							synPredMatched238 = true;
							inputState.guessing++;
							try {
								{
									interface_declaration();
								}
							}
							catch (RecognitionException)
							{
								synPredMatched238 = false;
							}
							rewind(_m238);
							inputState.guessing--;
						}
						if ( synPredMatched238 )
						{
							interface_declaration();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else {
							bool synPredMatched240 = false;
							if (((tokenSet_73_.member(LA(1))) && (tokenSet_74_.member(LA(2)))))
							{
								int _m240 = mark();
								synPredMatched240 = true;
								inputState.guessing++;
								try {
									{
										enum_declaration();
									}
								}
								catch (RecognitionException)
								{
									synPredMatched240 = false;
								}
								rewind(_m240);
								inputState.guessing--;
							}
							if ( synPredMatched240 )
							{
								enum_declaration();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
							}
							else {
								bool synPredMatched242 = false;
								if (((tokenSet_75_.member(LA(1))) && (tokenSet_76_.member(LA(2)))))
								{
									int _m242 = mark();
									synPredMatched242 = true;
									inputState.guessing++;
									try {
										{
											delegate_declaration();
											match(SEMI);
										}
									}
									catch (RecognitionException)
									{
										synPredMatched242 = false;
									}
									rewind(_m242);
									inputState.guessing--;
								}
								if ( synPredMatched242 )
								{
									delegate_declaration();
									if (0 == inputState.guessing)
									{
										astFactory.addASTChild(ref currentAST, returnAST);
									}
									match(SEMI);
								}
								else
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								}}}}
							}
							{
								switch ( LA(1) )
								{
								case SEMI:
								{
									match(SEMI);
									break;
								}
								case EOF:
								case IDENTIFIER:
								case OBJECT:
								case STRING:
								case BOOL:
								case DECIMAL:
								case SBYTE:
								case BYTE:
								case SHORT:
								case USHORT:
								case INT:
								case UINT:
								case LONG:
								case ULONG:
								case CHAR:
								case FLOAT:
								case DOUBLE:
								case LBRACK:
								case NEW:
								case VOID:
								case BNOT:
								case RBRACE:
								case CONST:
								case NAMESPACE:
								case CLASS:
								case PUBLIC:
								case PROTECTED:
								case INTERNAL:
								case PRIVATE:
								case ABSTRACT:
								case SEALED:
								case STATIC:
								case READONLY:
								case VOLATILE:
								case VIRTUAL:
								case OVERRIDE:
								case EXTERN:
								case EVENT:
								case IMPLICIT:
								case EXPLICIT:
								case STRUCT:
								case INTERFACE:
								case ENUM:
								case DELEGATE:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								 }
							}
							type_declaration_AST = currentAST.root;
						}
						catch (RecognitionException ex)
						{
							if (0 == inputState.guessing)
							{
								reportError(ex);
								recover(ex,tokenSet_77_);
							}
							else
							{
								throw ex;
							}
						}
						returnAST = type_declaration_AST;
					}
					
	public void class_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST bs_AST = null;
		AST bd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case CLASS:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			class_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp200_AST = null;
			tmp200_AST = astFactory.create(LT(1));
			match(CLASS);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case COLON:
				{
					class_base();
					if (0 == inputState.guessing)
					{
						bs_AST = (AST)returnAST;
					}
					break;
				}
				case LBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			class_body();
			if (0 == inputState.guessing)
			{
				bd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				class_declaration_AST = (AST)currentAST.root;
				class_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ClassNode) astFactory.create(ClassNode)), id_AST, atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.BaseTypes) astFactory.create(BaseTypes), bs_AST), (AST) astFactory.make((DDW.CSharp.Parse.Members) astFactory.create(Members), bd_AST)); 
						
				currentAST.root = class_declaration_AST;
				if ( (null != class_declaration_AST) && (null != class_declaration_AST.getFirstChild()) )
					currentAST.child = class_declaration_AST.getFirstChild();
				else
					currentAST.child = class_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = class_declaration_AST;
	}
	
	public void struct_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST bs_AST = null;
		AST bd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case STRUCT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			struct_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp201_AST = null;
			tmp201_AST = astFactory.create(LT(1));
			match(STRUCT);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case COLON:
				{
					struct_interfaces();
					if (0 == inputState.guessing)
					{
						bs_AST = (AST)returnAST;
					}
					break;
				}
				case LBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			struct_body();
			if (0 == inputState.guessing)
			{
				bd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				struct_declaration_AST = (AST)currentAST.root;
				struct_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.StructNode) astFactory.create(StructNode)), atr_AST, id_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.BaseTypes) astFactory.create(BaseTypes), bs_AST), (AST) astFactory.make((DDW.CSharp.Parse.Members) astFactory.create(Members), bd_AST)); 
						
				currentAST.root = struct_declaration_AST;
				if ( (null != struct_declaration_AST) && (null != struct_declaration_AST.getFirstChild()) )
					currentAST.child = struct_declaration_AST.getFirstChild();
				else
					currentAST.child = struct_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = struct_declaration_AST;
	}
	
	public void interface_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST bs_AST = null;
		AST bd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case INTERFACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			interface_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp202_AST = null;
			tmp202_AST = astFactory.create(LT(1));
			match(INTERFACE);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case COLON:
				{
					interface_base();
					if (0 == inputState.guessing)
					{
						bs_AST = (AST)returnAST;
					}
					break;
				}
				case LBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			interface_body();
			if (0 == inputState.guessing)
			{
				bd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				interface_declaration_AST = (AST)currentAST.root;
				interface_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.InterfaceNode) astFactory.create(InterfaceNode)), atr_AST, id_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.BaseTypes) astFactory.create(BaseTypes), bs_AST), (AST) astFactory.make((DDW.CSharp.Parse.Members) astFactory.create(Members), bd_AST)); 
						
				currentAST.root = interface_declaration_AST;
				if ( (null != interface_declaration_AST) && (null != interface_declaration_AST.getFirstChild()) )
					currentAST.child = interface_declaration_AST.getFirstChild();
				else
					currentAST.child = interface_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_declaration_AST;
	}
	
	public void enum_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enum_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST bs_AST = null;
		AST bd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ENUM:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			enum_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp203_AST = null;
			tmp203_AST = astFactory.create(LT(1));
			match(ENUM);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case COLON:
				{
					enum_base();
					if (0 == inputState.guessing)
					{
						bs_AST = (AST)returnAST;
					}
					break;
				}
				case LBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			enum_body();
			if (0 == inputState.guessing)
			{
				bd_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				enum_declaration_AST = (AST)currentAST.root;
				enum_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.EnumNode) astFactory.create(EnumNode)), atr_AST, id_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.BaseTypes) astFactory.create(BaseTypes), bs_AST), (AST) astFactory.make((DDW.CSharp.Parse.Members) astFactory.create(Members), bd_AST)); 
						
				currentAST.root = enum_declaration_AST;
				if ( (null != enum_declaration_AST) && (null != enum_declaration_AST.getFirstChild()) )
					currentAST.child = enum_declaration_AST.getFirstChild();
				else
					currentAST.child = enum_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = enum_declaration_AST;
	}
	
	public void delegate_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST delegate_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST pms_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case DELEGATE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			delegate_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp204_AST = null;
			tmp204_AST = astFactory.create(LT(1));
			match(DELEGATE);
			return_type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			match(RPAREN);
			if (0==inputState.guessing)
			{
				delegate_declaration_AST = (AST)currentAST.root;
				delegate_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.DelegateNode) astFactory.create(DelegateNode)), atr_AST, id_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), pms_AST); 
						
				currentAST.root = delegate_declaration_AST;
				if ( (null != delegate_declaration_AST) && (null != delegate_declaration_AST.getFirstChild()) )
					currentAST.child = delegate_declaration_AST.getFirstChild();
				else
					currentAST.child = delegate_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_3_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = delegate_declaration_AST;
	}
	
	public void attributes() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attributes_AST = null;
		
		try {      // for error handling
			{ // ( ... )+
				int _cnt497=0;
				for (;;)
				{
					if ((LA(1)==LBRACK))
					{
						attribute_section();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						if (_cnt497 >= 1) { goto _loop497_breakloop; } else { throw new NoViableAltException(LT(1), getFilename());; }
					}
					
					_cnt497++;
				}
_loop497_breakloop:				;
			}    // ( ... )+
			attributes_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_79_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attributes_AST;
	}
	
	public void class_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp207_AST = null;
						tmp207_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp207_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp208_AST = null;
						tmp208_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp208_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp209_AST = null;
						tmp209_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp209_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp210_AST = null;
						tmp210_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp210_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp211_AST = null;
						tmp211_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp211_AST);
						match(PRIVATE);
						break;
					}
					case ABSTRACT:
					{
						AST tmp212_AST = null;
						tmp212_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp212_AST);
						match(ABSTRACT);
						break;
					}
					case SEALED:
					{
						AST tmp213_AST = null;
						tmp213_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp213_AST);
						match(SEALED);
						break;
					}
					default:
					{
						goto _loop249_breakloop;
					}
					 }
				}
_loop249_breakloop:				;
			}    // ( ... )*
			class_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_80_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = class_modifiers_AST;
	}
	
	public void class_base() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_base_AST = null;
		
		try {      // for error handling
			match(COLON);
			{
				class_type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==COMMA))
						{
							AST tmp215_AST = null;
							tmp215_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp215_AST);
							match(COMMA);
							class_type();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop253_breakloop;
						}
						
					}
_loop253_breakloop:					;
				}    // ( ... )*
			}
			class_base_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = class_base_AST;
	}
	
	public void class_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_body_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_81_.member(LA(1))))
					{
						class_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop256_breakloop;
					}
					
				}
_loop256_breakloop:				;
			}    // ( ... )*
			match(RBRACE);
			class_body_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = class_body_AST;
	}
	
	public void class_member_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST class_member_declaration_AST = null;
		
		try {      // for error handling
			bool synPredMatched259 = false;
			if (((tokenSet_82_.member(LA(1))) && (tokenSet_83_.member(LA(2)))))
			{
				int _m259 = mark();
				synPredMatched259 = true;
				inputState.guessing++;
				try {
					{
						constant_declaration();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched259 = false;
				}
				rewind(_m259);
				inputState.guessing--;
			}
			if ( synPredMatched259 )
			{
				constant_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				class_member_declaration_AST = currentAST.root;
			}
			else {
				bool synPredMatched261 = false;
				if (((tokenSet_84_.member(LA(1))) && (tokenSet_85_.member(LA(2)))))
				{
					int _m261 = mark();
					synPredMatched261 = true;
					inputState.guessing++;
					try {
						{
							field_declaration();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched261 = false;
					}
					rewind(_m261);
					inputState.guessing--;
				}
				if ( synPredMatched261 )
				{
					field_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					class_member_declaration_AST = currentAST.root;
				}
				else {
					bool synPredMatched263 = false;
					if (((tokenSet_86_.member(LA(1))) && (tokenSet_87_.member(LA(2)))))
					{
						int _m263 = mark();
						synPredMatched263 = true;
						inputState.guessing++;
						try {
							{
								method_declaration();
							}
						}
						catch (RecognitionException)
						{
							synPredMatched263 = false;
						}
						rewind(_m263);
						inputState.guessing--;
					}
					if ( synPredMatched263 )
					{
						method_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						class_member_declaration_AST = currentAST.root;
					}
					else {
						bool synPredMatched265 = false;
						if (((tokenSet_88_.member(LA(1))) && (tokenSet_89_.member(LA(2)))))
						{
							int _m265 = mark();
							synPredMatched265 = true;
							inputState.guessing++;
							try {
								{
									property_declaration();
								}
							}
							catch (RecognitionException)
							{
								synPredMatched265 = false;
							}
							rewind(_m265);
							inputState.guessing--;
						}
						if ( synPredMatched265 )
						{
							property_declaration();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
							class_member_declaration_AST = currentAST.root;
						}
						else {
							bool synPredMatched267 = false;
							if (((tokenSet_90_.member(LA(1))) && (tokenSet_91_.member(LA(2)))))
							{
								int _m267 = mark();
								synPredMatched267 = true;
								inputState.guessing++;
								try {
									{
										event_declaration();
									}
								}
								catch (RecognitionException)
								{
									synPredMatched267 = false;
								}
								rewind(_m267);
								inputState.guessing--;
							}
							if ( synPredMatched267 )
							{
								event_declaration();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
								class_member_declaration_AST = currentAST.root;
							}
							else {
								bool synPredMatched269 = false;
								if (((tokenSet_92_.member(LA(1))) && (tokenSet_93_.member(LA(2)))))
								{
									int _m269 = mark();
									synPredMatched269 = true;
									inputState.guessing++;
									try {
										{
											indexer_declaration();
										}
									}
									catch (RecognitionException)
									{
										synPredMatched269 = false;
									}
									rewind(_m269);
									inputState.guessing--;
								}
								if ( synPredMatched269 )
								{
									indexer_declaration();
									if (0 == inputState.guessing)
									{
										astFactory.addASTChild(ref currentAST, returnAST);
									}
									class_member_declaration_AST = currentAST.root;
								}
								else {
									bool synPredMatched271 = false;
									if (((tokenSet_94_.member(LA(1))) && (tokenSet_95_.member(LA(2)))))
									{
										int _m271 = mark();
										synPredMatched271 = true;
										inputState.guessing++;
										try {
											{
												operator_declaration();
											}
										}
										catch (RecognitionException)
										{
											synPredMatched271 = false;
										}
										rewind(_m271);
										inputState.guessing--;
									}
									if ( synPredMatched271 )
									{
										operator_declaration();
										if (0 == inputState.guessing)
										{
											astFactory.addASTChild(ref currentAST, returnAST);
										}
										class_member_declaration_AST = currentAST.root;
									}
									else {
										bool synPredMatched273 = false;
										if (((tokenSet_96_.member(LA(1))) && (tokenSet_97_.member(LA(2)))))
										{
											int _m273 = mark();
											synPredMatched273 = true;
											inputState.guessing++;
											try {
												{
													constructor_declaration();
												}
											}
											catch (RecognitionException)
											{
												synPredMatched273 = false;
											}
											rewind(_m273);
											inputState.guessing--;
										}
										if ( synPredMatched273 )
										{
											constructor_declaration();
											if (0 == inputState.guessing)
											{
												astFactory.addASTChild(ref currentAST, returnAST);
											}
											class_member_declaration_AST = currentAST.root;
										}
										else {
											bool synPredMatched275 = false;
											if (((LA(1)==LBRACK||LA(1)==BNOT||LA(1)==EXTERN) && (tokenSet_98_.member(LA(2)))))
											{
												int _m275 = mark();
												synPredMatched275 = true;
												inputState.guessing++;
												try {
													{
														destructor_declaration();
													}
												}
												catch (RecognitionException)
												{
													synPredMatched275 = false;
												}
												rewind(_m275);
												inputState.guessing--;
											}
											if ( synPredMatched275 )
											{
												destructor_declaration();
												if (0 == inputState.guessing)
												{
													astFactory.addASTChild(ref currentAST, returnAST);
												}
												class_member_declaration_AST = currentAST.root;
											}
											else {
												bool synPredMatched277 = false;
												if (((LA(1)==LBRACK||LA(1)==STATIC||LA(1)==EXTERN) && (tokenSet_99_.member(LA(2)))))
												{
													int _m277 = mark();
													synPredMatched277 = true;
													inputState.guessing++;
													try {
														{
															static_constructor_declaration();
														}
													}
													catch (RecognitionException)
													{
														synPredMatched277 = false;
													}
													rewind(_m277);
													inputState.guessing--;
												}
												if ( synPredMatched277 )
												{
													static_constructor_declaration();
													if (0 == inputState.guessing)
													{
														astFactory.addASTChild(ref currentAST, returnAST);
													}
													class_member_declaration_AST = currentAST.root;
												}
												else if ((tokenSet_100_.member(LA(1))) && (tokenSet_101_.member(LA(2)))) {
													type_declaration();
													if (0 == inputState.guessing)
													{
														astFactory.addASTChild(ref currentAST, returnAST);
													}
													class_member_declaration_AST = currentAST.root;
												}
												else
												{
													throw new NoViableAltException(LT(1), getFilename());
												}
												}}}}}}}}}
											}
											catch (RecognitionException ex)
											{
												if (0 == inputState.guessing)
												{
													reportError(ex);
													recover(ex,tokenSet_102_);
												}
												else
												{
													throw ex;
												}
											}
											returnAST = class_member_declaration_AST;
										}
										
	public void constant_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST cd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case CONST:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			constant_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp218_AST = null;
			tmp218_AST = astFactory.create(LT(1));
			match(CONST);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			constant_declarators();
			if (0 == inputState.guessing)
			{
				cd_AST = (AST)returnAST;
			}
			AST tmp219_AST = null;
			tmp219_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				constant_declaration_AST = (AST)currentAST.root;
				constant_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ConstantNode) astFactory.create(ConstantNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), cd_AST); 
						
				currentAST.root = constant_declaration_AST;
				if ( (null != constant_declaration_AST) && (null != constant_declaration_AST.getFirstChild()) )
					currentAST.child = constant_declaration_AST.getFirstChild();
				else
					currentAST.child = constant_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constant_declaration_AST;
	}
	
	public void field_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST field_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST vd_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case STATIC:
				case READONLY:
				case VOLATILE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			field_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			variable_declarators();
			if (0 == inputState.guessing)
			{
				vd_AST = (AST)returnAST;
			}
			AST tmp220_AST = null;
			tmp220_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				field_declaration_AST = (AST)currentAST.root;
				field_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.FieldNode) astFactory.create(FieldNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), vd_AST); 
						
				currentAST.root = field_declaration_AST;
				if ( (null != field_declaration_AST) && (null != field_declaration_AST.getFirstChild()) )
					currentAST.child = field_declaration_AST.getFirstChild();
				else
					currentAST.child = field_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = field_declaration_AST;
	}
	
	public void method_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_declaration_AST = null;
		AST mh_AST = null;
		AST mb_AST = null;
		
		try {      // for error handling
			method_header();
			if (0 == inputState.guessing)
			{
				mh_AST = (AST)returnAST;
			}
			method_body();
			if (0 == inputState.guessing)
			{
				mb_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				method_declaration_AST = (AST)currentAST.root;
				method_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.MethodNode) astFactory.create(MethodNode)), mh_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), mb_AST)); 
						
				currentAST.root = method_declaration_AST;
				if ( (null != method_declaration_AST) && (null != method_declaration_AST.getFirstChild()) )
					currentAST.child = method_declaration_AST.getFirstChild();
				else
					currentAST.child = method_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = method_declaration_AST;
	}
	
	public void property_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST property_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST nm_AST = null;
		AST ad_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				case STATIC:
				case VIRTUAL:
				case OVERRIDE:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			property_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			type_name();
			if (0 == inputState.guessing)
			{
				nm_AST = (AST)returnAST;
			}
			AST tmp221_AST = null;
			tmp221_AST = astFactory.create(LT(1));
			match(LBRACE);
			accessor_declarations();
			if (0 == inputState.guessing)
			{
				ad_AST = (AST)returnAST;
			}
			AST tmp222_AST = null;
			tmp222_AST = astFactory.create(LT(1));
			match(RBRACE);
			if (0==inputState.guessing)
			{
				property_declaration_AST = (AST)currentAST.root;
				property_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.PropertyNode) astFactory.create(PropertyNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent), nm_AST), ad_AST);
						
				currentAST.root = property_declaration_AST;
				if ( (null != property_declaration_AST) && (null != property_declaration_AST.getFirstChild()) )
					currentAST.child = property_declaration_AST.getFirstChild();
				else
					currentAST.child = property_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = property_declaration_AST;
	}
	
	public void event_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST vd_AST = null;
		AST nm_AST = null;
		AST ead_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				case STATIC:
				case VIRTUAL:
				case OVERRIDE:
				case EXTERN:
				case EVENT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			event_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp223_AST = null;
			tmp223_AST = astFactory.create(LT(1));
			match(EVENT);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			{
				if ((LA(1)==IDENTIFIER) && (tokenSet_103_.member(LA(2))))
				{
					{ // ( ... )+
						int _cnt333=0;
						for (;;)
						{
							if ((LA(1)==IDENTIFIER))
							{
								variable_declarators();
								if (0 == inputState.guessing)
								{
									vd_AST = (AST)returnAST;
								}
							}
							else
							{
								if (_cnt333 >= 1) { goto _loop333_breakloop; } else { throw new NoViableAltException(LT(1), getFilename());; }
							}
							
							_cnt333++;
						}
_loop333_breakloop:						;
					}    // ( ... )+
					AST tmp224_AST = null;
					tmp224_AST = astFactory.create(LT(1));
					match(SEMI);
					if (0==inputState.guessing)
					{
						event_declaration_AST = (AST)currentAST.root;
						event_declaration_AST = 
										(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.EventNode) astFactory.create(EventNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), vd_AST);
									
						currentAST.root = event_declaration_AST;
						if ( (null != event_declaration_AST) && (null != event_declaration_AST.getFirstChild()) )
							currentAST.child = event_declaration_AST.getFirstChild();
						else
							currentAST.child = event_declaration_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else if ((LA(1)==IDENTIFIER) && (LA(2)==DOT||LA(2)==LBRACE)) {
					member_name();
					if (0 == inputState.guessing)
					{
						nm_AST = (AST)returnAST;
					}
					AST tmp225_AST = null;
					tmp225_AST = astFactory.create(LT(1));
					match(LBRACE);
					event_accessor_declarations();
					if (0 == inputState.guessing)
					{
						ead_AST = (AST)returnAST;
					}
					AST tmp226_AST = null;
					tmp226_AST = astFactory.create(LT(1));
					match(RBRACE);
					if (0==inputState.guessing)
					{
						event_declaration_AST = (AST)currentAST.root;
						event_declaration_AST = 
										(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.EventNode) astFactory.create(EventNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent), nm_AST), ead_AST);
									
						currentAST.root = event_declaration_AST;
						if ( (null != event_declaration_AST) && (null != event_declaration_AST.getFirstChild()) )
							currentAST.child = event_declaration_AST.getFirstChild();
						else
							currentAST.child = event_declaration_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_declaration_AST;
	}
	
	public void indexer_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexer_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST idl_AST = null;
		AST ad_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				case VIRTUAL:
				case OVERRIDE:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			indexer_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			indexer_declarator();
			if (0 == inputState.guessing)
			{
				idl_AST = (AST)returnAST;
			}
			AST tmp227_AST = null;
			tmp227_AST = astFactory.create(LT(1));
			match(LBRACE);
			accessor_declarations();
			if (0 == inputState.guessing)
			{
				ad_AST = (AST)returnAST;
			}
			AST tmp228_AST = null;
			tmp228_AST = astFactory.create(LT(1));
			match(RBRACE);
			if (0==inputState.guessing)
			{
				indexer_declaration_AST = (AST)currentAST.root;
				indexer_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IndexerNode) astFactory.create(IndexerNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), idl_AST, ad_AST);
						
				currentAST.root = indexer_declaration_AST;
				if ( (null != indexer_declaration_AST) && (null != indexer_declaration_AST.getFirstChild()) )
					currentAST.child = indexer_declaration_AST.getFirstChild();
				else
					currentAST.child = indexer_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = indexer_declaration_AST;
	}
	
	public void operator_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST operator_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST odl_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case PUBLIC:
				case STATIC:
				case EXTERN:
				case IMPLICIT:
				case EXPLICIT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			operator_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			operator_declarator();
			if (0 == inputState.guessing)
			{
				odl_AST = (AST)returnAST;
			}
			operator_body();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				operator_declaration_AST = (AST)currentAST.root;
				operator_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.OperatorNode) astFactory.create(OperatorNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), odl_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = operator_declaration_AST;
				if ( (null != operator_declaration_AST) && (null != operator_declaration_AST.getFirstChild()) )
					currentAST.child = operator_declaration_AST.getFirstChild();
				else
					currentAST.child = operator_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = operator_declaration_AST;
	}
	
	public void constructor_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST cdl_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			constructor_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			constructor_declarator();
			if (0 == inputState.guessing)
			{
				cdl_AST = (AST)returnAST;
			}
			constructor_body();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				constructor_declaration_AST = (AST)currentAST.root;
				constructor_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ConstructorNode) astFactory.create(ConstructorNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), cdl_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = constructor_declaration_AST;
				if ( (null != constructor_declaration_AST) && (null != constructor_declaration_AST.getFirstChild()) )
					currentAST.child = constructor_declaration_AST.getFirstChild();
				else
					currentAST.child = constructor_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_declaration_AST;
	}
	
	public void destructor_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST destructor_declaration_AST = null;
		AST atr_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case BNOT:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case EXTERN:
				{
					mod = LT(1);
					mod_AST = astFactory.create(mod);
					match(EXTERN);
					break;
				}
				case BNOT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp229_AST = null;
			tmp229_AST = astFactory.create(LT(1));
			match(BNOT);
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp230_AST = null;
			tmp230_AST = astFactory.create(LT(1));
			match(LPAREN);
			AST tmp231_AST = null;
			tmp231_AST = astFactory.create(LT(1));
			match(RPAREN);
			destructor_body();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				destructor_declaration_AST = (AST)currentAST.root;
				destructor_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.DestructorNode) astFactory.create(DestructorNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = destructor_declaration_AST;
				if ( (null != destructor_declaration_AST) && (null != destructor_declaration_AST.getFirstChild()) )
					currentAST.child = destructor_declaration_AST.getFirstChild();
				else
					currentAST.child = destructor_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = destructor_declaration_AST;
	}
	
	public void static_constructor_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST static_constructor_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST id_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case STATIC:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			static_constructor_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp232_AST = null;
			tmp232_AST = astFactory.create(LT(1));
			match(LPAREN);
			AST tmp233_AST = null;
			tmp233_AST = astFactory.create(LT(1));
			match(RPAREN);
			static_constructor_body();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				static_constructor_declaration_AST = (AST)currentAST.root;
				static_constructor_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.ConstructorNode) astFactory.create(ConstructorNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = static_constructor_declaration_AST;
				if ( (null != static_constructor_declaration_AST) && (null != static_constructor_declaration_AST.getFirstChild()) )
					currentAST.child = static_constructor_declaration_AST.getFirstChild();
				else
					currentAST.child = static_constructor_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = static_constructor_declaration_AST;
	}
	
	public void constant_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp234_AST = null;
						tmp234_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp234_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp235_AST = null;
						tmp235_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp235_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp236_AST = null;
						tmp236_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp236_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp237_AST = null;
						tmp237_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp237_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp238_AST = null;
						tmp238_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp238_AST);
						match(PRIVATE);
						break;
					}
					default:
					{
						goto _loop282_breakloop;
					}
					 }
				}
_loop282_breakloop:				;
			}    // ( ... )*
			constant_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_104_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constant_modifiers_AST;
	}
	
	public void field_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST field_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp239_AST = null;
						tmp239_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp239_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp240_AST = null;
						tmp240_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp240_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp241_AST = null;
						tmp241_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp241_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp242_AST = null;
						tmp242_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp242_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp243_AST = null;
						tmp243_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp243_AST);
						match(PRIVATE);
						break;
					}
					case STATIC:
					{
						AST tmp244_AST = null;
						tmp244_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp244_AST);
						match(STATIC);
						break;
					}
					case READONLY:
					{
						AST tmp245_AST = null;
						tmp245_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp245_AST);
						match(READONLY);
						break;
					}
					case VOLATILE:
					{
						AST tmp246_AST = null;
						tmp246_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp246_AST);
						match(VOLATILE);
						break;
					}
					default:
					{
						goto _loop287_breakloop;
					}
					 }
				}
_loop287_breakloop:				;
			}    // ( ... )*
			field_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_7_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = field_modifiers_AST;
	}
	
	public void variable_declarators() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_declarators_AST = null;
		
		try {      // for error handling
			variable_declarator();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						variable_declarator();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop290_breakloop;
					}
					
				}
_loop290_breakloop:				;
			}    // ( ... )*
			variable_declarators_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_105_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = variable_declarators_AST;
	}
	
	public void variable_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_declarator_AST = null;
		AST id_AST = null;
		AST id2_AST = null;
		AST vi_AST = null;
		
		try {      // for error handling
			if ((LA(1)==IDENTIFIER) && (LA(2)==IDENTIFIER||LA(2)==COMMA||LA(2)==SEMI))
			{
				identifier();
				if (0 == inputState.guessing)
				{
					id_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					variable_declarator_AST = (AST)currentAST.root;
					variable_declarator_AST = 
								(AST) astFactory.make((DDW.CSharp.Parse.Declarator) astFactory.create(Declarator), id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression)));
							
					currentAST.root = variable_declarator_AST;
					if ( (null != variable_declarator_AST) && (null != variable_declarator_AST.getFirstChild()) )
						currentAST.child = variable_declarator_AST.getFirstChild();
					else
						currentAST.child = variable_declarator_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==IDENTIFIER) && (LA(2)==ASSIGN)) {
				identifier();
				if (0 == inputState.guessing)
				{
					id2_AST = (AST)returnAST;
				}
				AST tmp248_AST = null;
				tmp248_AST = astFactory.create(LT(1));
				match(ASSIGN);
				variable_initializer();
				if (0 == inputState.guessing)
				{
					vi_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					variable_declarator_AST = (AST)currentAST.root;
					variable_declarator_AST = 
								(AST) astFactory.make((DDW.CSharp.Parse.Declarator) astFactory.create(Declarator), id2_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), vi_AST));
							
					currentAST.root = variable_declarator_AST;
					if ( (null != variable_declarator_AST) && (null != variable_declarator_AST.getFirstChild()) )
						currentAST.child = variable_declarator_AST.getFirstChild();
					else
						currentAST.child = variable_declarator_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_106_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = variable_declarator_AST;
	}
	
	public void variable_initializer() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_initializer_AST = null;
		AST ex_AST = null;
		AST ai_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case INTEGER_LITERAL:
			case HEXADECIMAL_INTEGER_LITERAL:
			case REAL_LITERAL:
			case CHARACTER_LITERAL:
			case STRING_LITERAL:
			case TRUE:
			case FALSE:
			case NULL:
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			case LPAREN:
			case THIS:
			case BASE:
			case INC:
			case DEC:
			case NEW:
			case TYPEOF:
			case CHECKED:
			case UNCHECKED:
			case PLUS:
			case MINUS:
			case LNOT:
			case BNOT:
			case STAR:
			case SIZEOF:
			{
				expression();
				if (0 == inputState.guessing)
				{
					ex_AST = (AST)returnAST;
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				variable_initializer_AST = currentAST.root;
				break;
			}
			case LBRACE:
			{
				array_initializer();
				if (0 == inputState.guessing)
				{
					ai_AST = (AST)returnAST;
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				variable_initializer_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_107_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = variable_initializer_AST;
	}
	
	public void method_header() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_header_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST ret_AST = null;
		AST nm_AST = null;
		AST pms_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				case VOID:
				case PUBLIC:
				case PROTECTED:
				case INTERNAL:
				case PRIVATE:
				case ABSTRACT:
				case SEALED:
				case STATIC:
				case VIRTUAL:
				case OVERRIDE:
				case EXTERN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			method_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			return_type();
			if (0 == inputState.guessing)
			{
				ret_AST = (AST)returnAST;
			}
			member_name();
			if (0 == inputState.guessing)
			{
				nm_AST = (AST)returnAST;
			}
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			match(RPAREN);
			if (0==inputState.guessing)
			{
				method_header_AST = (AST)currentAST.root;
				method_header_AST = 
							(AST) astFactory.make(method_header_AST, atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), ret_AST), (AST) astFactory.make((DDW.CSharp.Parse.QualIdent) astFactory.create(QualIdent), nm_AST), pms_AST); 
							
						
				currentAST.root = method_header_AST;
				if ( (null != method_header_AST) && (null != method_header_AST.getFirstChild()) )
					currentAST.child = method_header_AST.getFirstChild();
				else
					currentAST.child = method_header_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = method_header_AST;
	}
	
	public void method_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				method_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				method_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = method_body_AST;
	}
	
	public void method_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp252_AST = null;
						tmp252_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp252_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp253_AST = null;
						tmp253_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp253_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp254_AST = null;
						tmp254_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp254_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp255_AST = null;
						tmp255_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp255_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp256_AST = null;
						tmp256_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp256_AST);
						match(PRIVATE);
						break;
					}
					case STATIC:
					{
						AST tmp257_AST = null;
						tmp257_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp257_AST);
						match(STATIC);
						break;
					}
					case VIRTUAL:
					{
						AST tmp258_AST = null;
						tmp258_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp258_AST);
						match(VIRTUAL);
						break;
					}
					case SEALED:
					{
						AST tmp259_AST = null;
						tmp259_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp259_AST);
						match(SEALED);
						break;
					}
					case OVERRIDE:
					{
						AST tmp260_AST = null;
						tmp260_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp260_AST);
						match(OVERRIDE);
						break;
					}
					case ABSTRACT:
					{
						AST tmp261_AST = null;
						tmp261_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp261_AST);
						match(ABSTRACT);
						break;
					}
					case EXTERN:
					{
						AST tmp262_AST = null;
						tmp262_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp262_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop299_breakloop;
					}
					 }
				}
_loop299_breakloop:				;
			}    // ( ... )*
			method_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_109_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = method_modifiers_AST;
	}
	
	public void return_type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST return_type_AST = null;
		IToken  vd = null;
		AST vd_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IDENTIFIER:
			case OBJECT:
			case STRING:
			case BOOL:
			case DECIMAL:
			case SBYTE:
			case BYTE:
			case SHORT:
			case USHORT:
			case INT:
			case UINT:
			case LONG:
			case ULONG:
			case CHAR:
			case FLOAT:
			case DOUBLE:
			{
				type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				return_type_AST = currentAST.root;
				break;
			}
			case VOID:
			{
				vd = LT(1);
				vd_AST = astFactory.create(vd);
				match(VOID);
				if (0==inputState.guessing)
				{
					return_type_AST = (AST)currentAST.root;
					return_type_AST = (AST) astFactory.make((DDW.CSharp.Parse.BuiltInType) astFactory.create(BuiltInType,vd_AST.getText()));
					currentAST.root = return_type_AST;
					if ( (null != return_type_AST) && (null != return_type_AST.getFirstChild()) )
						currentAST.child = return_type_AST.getFirstChild();
					else
						currentAST.child = return_type_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_110_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = return_type_AST;
	}
	
	public void member_name() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST member_name_AST = null;
		
		try {      // for error handling
			type_name();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			member_name_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_111_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = member_name_AST;
	}
	
	public void formal_parameter_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formal_parameter_list_AST = null;
		
		try {      // for error handling
			method_parameter();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						method_parameter();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop305_breakloop;
					}
					
				}
_loop305_breakloop:				;
			}    // ( ... )*
			if (0==inputState.guessing)
			{
				formal_parameter_list_AST = (AST)currentAST.root;
				formal_parameter_list_AST = (AST) astFactory.make((DDW.CSharp.Parse.DeclArgs) astFactory.create(DeclArgs), formal_parameter_list_AST);
				currentAST.root = formal_parameter_list_AST;
				if ( (null != formal_parameter_list_AST) && (null != formal_parameter_list_AST.getFirstChild()) )
					currentAST.child = formal_parameter_list_AST.getFirstChild();
				else
					currentAST.child = formal_parameter_list_AST;
				currentAST.advanceChildToEnd();
			}
			formal_parameter_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_112_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = formal_parameter_list_AST;
	}
	
	public void method_parameter() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_parameter_AST = null;
		AST atr_AST = null;
		AST dir_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST patr_AST = null;
		IToken  pmod = null;
		AST pmod_AST = null;
		AST ptp_AST = null;
		AST pid_AST = null;
		
		try {      // for error handling
			bool synPredMatched310 = false;
			if (((tokenSet_113_.member(LA(1))) && (tokenSet_114_.member(LA(2)))))
			{
				int _m310 = mark();
				synPredMatched310 = true;
				inputState.guessing++;
				try {
					{
						{
							switch ( LA(1) )
							{
							case LBRACK:
							{
								attributes();
								break;
							}
							case IDENTIFIER:
							case OBJECT:
							case STRING:
							case BOOL:
							case DECIMAL:
							case SBYTE:
							case BYTE:
							case SHORT:
							case USHORT:
							case INT:
							case UINT:
							case LONG:
							case ULONG:
							case CHAR:
							case FLOAT:
							case DOUBLE:
							case REF:
							case OUT:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							 }
						}
						{
							switch ( LA(1) )
							{
							case REF:
							case OUT:
							{
								parameter_direction();
								break;
							}
							case IDENTIFIER:
							case OBJECT:
							case STRING:
							case BOOL:
							case DECIMAL:
							case SBYTE:
							case BYTE:
							case SHORT:
							case USHORT:
							case INT:
							case UINT:
							case LONG:
							case ULONG:
							case CHAR:
							case FLOAT:
							case DOUBLE:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							 }
						}
						type();
						identifier();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched310 = false;
				}
				rewind(_m310);
				inputState.guessing--;
			}
			if ( synPredMatched310 )
			{
				{
					switch ( LA(1) )
					{
					case LBRACK:
					{
						attributes();
						if (0 == inputState.guessing)
						{
							atr_AST = (AST)returnAST;
						}
						break;
					}
					case IDENTIFIER:
					case OBJECT:
					case STRING:
					case BOOL:
					case DECIMAL:
					case SBYTE:
					case BYTE:
					case SHORT:
					case USHORT:
					case INT:
					case UINT:
					case LONG:
					case ULONG:
					case CHAR:
					case FLOAT:
					case DOUBLE:
					case REF:
					case OUT:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				{
					switch ( LA(1) )
					{
					case REF:
					case OUT:
					{
						parameter_direction();
						if (0 == inputState.guessing)
						{
							dir_AST = (AST)returnAST;
						}
						break;
					}
					case IDENTIFIER:
					case OBJECT:
					case STRING:
					case BOOL:
					case DECIMAL:
					case SBYTE:
					case BYTE:
					case SHORT:
					case USHORT:
					case INT:
					case UINT:
					case LONG:
					case ULONG:
					case CHAR:
					case FLOAT:
					case DOUBLE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				type();
				if (0 == inputState.guessing)
				{
					tp_AST = (AST)returnAST;
				}
				identifier();
				if (0 == inputState.guessing)
				{
					id_AST = (AST)returnAST;
				}
				if (0==inputState.guessing)
				{
					method_parameter_AST = (AST)currentAST.root;
					method_parameter_AST = 
								(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg)), atr_AST, dir_AST, (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST); 
							
					currentAST.root = method_parameter_AST;
					if ( (null != method_parameter_AST) && (null != method_parameter_AST.getFirstChild()) )
						currentAST.child = method_parameter_AST.getFirstChild();
					else
						currentAST.child = method_parameter_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				bool synPredMatched315 = false;
				if (((LA(1)==LBRACK||LA(1)==PARAMS) && (tokenSet_115_.member(LA(2)))))
				{
					int _m315 = mark();
					synPredMatched315 = true;
					inputState.guessing++;
					try {
						{
							{
								switch ( LA(1) )
								{
								case LBRACK:
								{
									attributes();
									break;
								}
								case PARAMS:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								 }
							}
							match(PARAMS);
							array_type();
							identifier();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched315 = false;
					}
					rewind(_m315);
					inputState.guessing--;
				}
				if ( synPredMatched315 )
				{
					{
						switch ( LA(1) )
						{
						case LBRACK:
						{
							attributes();
							if (0 == inputState.guessing)
							{
								patr_AST = (AST)returnAST;
							}
							break;
						}
						case PARAMS:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						 }
					}
					pmod = LT(1);
					pmod_AST = astFactory.create(pmod);
					match(PARAMS);
					array_type();
					if (0 == inputState.guessing)
					{
						ptp_AST = (AST)returnAST;
					}
					identifier();
					if (0 == inputState.guessing)
					{
						pid_AST = (AST)returnAST;
					}
					if (0==inputState.guessing)
					{
						method_parameter_AST = (AST)currentAST.root;
						method_parameter_AST = 
									(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg)), patr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), pmod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), ptp_AST), pid_AST);
								
						currentAST.root = method_parameter_AST;
						if ( (null != method_parameter_AST) && (null != method_parameter_AST.getFirstChild()) )
							currentAST.child = method_parameter_AST.getFirstChild();
						else
							currentAST.child = method_parameter_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex)
			{
				if (0 == inputState.guessing)
				{
					reportError(ex);
					recover(ex,tokenSet_116_);
				}
				else
				{
					throw ex;
				}
			}
			returnAST = method_parameter_AST;
		}
		
	public void property_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST property_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp264_AST = null;
						tmp264_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp264_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp265_AST = null;
						tmp265_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp265_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp266_AST = null;
						tmp266_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp266_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp267_AST = null;
						tmp267_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp267_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp268_AST = null;
						tmp268_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp268_AST);
						match(PRIVATE);
						break;
					}
					case STATIC:
					{
						AST tmp269_AST = null;
						tmp269_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp269_AST);
						match(STATIC);
						break;
					}
					case VIRTUAL:
					{
						AST tmp270_AST = null;
						tmp270_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp270_AST);
						match(VIRTUAL);
						break;
					}
					case SEALED:
					{
						AST tmp271_AST = null;
						tmp271_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp271_AST);
						match(SEALED);
						break;
					}
					case OVERRIDE:
					{
						AST tmp272_AST = null;
						tmp272_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp272_AST);
						match(OVERRIDE);
						break;
					}
					case ABSTRACT:
					{
						AST tmp273_AST = null;
						tmp273_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp273_AST);
						match(ABSTRACT);
						break;
					}
					case EXTERN:
					{
						AST tmp274_AST = null;
						tmp274_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp274_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop322_breakloop;
					}
					 }
				}
_loop322_breakloop:				;
			}    // ( ... )*
			property_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_7_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = property_modifiers_AST;
	}
	
	public void accessor_declarations() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessor_declarations_AST = null;
		
		try {      // for error handling
			accessor_declaration();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case LBRACK:
				{
					accessor_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			accessor_declarations_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_66_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = accessor_declarations_AST;
	}
	
	public void accessor_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessor_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			accessor_modifier();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			accessor_body();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				accessor_declaration_AST = (AST)currentAST.root;
				accessor_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.AccessorNode) astFactory.create(AccessorNode,mod_AST.getText())), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = accessor_declaration_AST;
				if ( (null != accessor_declaration_AST) && (null != accessor_declaration_AST.getFirstChild()) )
					currentAST.child = accessor_declaration_AST.getFirstChild();
				else
					currentAST.child = accessor_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_117_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = accessor_declaration_AST;
	}
	
	public void accessor_modifier() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessor_modifier_AST = null;
		
		try {      // for error handling
			if (!( LT(1).getText()=="get" || LT(1).getText()=="set"))
			  throw new SemanticException(" LT(1).getText()==\"get\" || LT(1).getText()==\"set\"");
			identifier();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			accessor_modifier_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = accessor_modifier_AST;
	}
	
	public void accessor_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessor_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				accessor_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				accessor_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_117_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = accessor_body_AST;
	}
	
	public void event_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp276_AST = null;
						tmp276_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp276_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp277_AST = null;
						tmp277_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp277_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp278_AST = null;
						tmp278_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp278_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp279_AST = null;
						tmp279_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp279_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp280_AST = null;
						tmp280_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp280_AST);
						match(PRIVATE);
						break;
					}
					case STATIC:
					{
						AST tmp281_AST = null;
						tmp281_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp281_AST);
						match(STATIC);
						break;
					}
					case VIRTUAL:
					{
						AST tmp282_AST = null;
						tmp282_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp282_AST);
						match(VIRTUAL);
						break;
					}
					case SEALED:
					{
						AST tmp283_AST = null;
						tmp283_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp283_AST);
						match(SEALED);
						break;
					}
					case OVERRIDE:
					{
						AST tmp284_AST = null;
						tmp284_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp284_AST);
						match(OVERRIDE);
						break;
					}
					case ABSTRACT:
					{
						AST tmp285_AST = null;
						tmp285_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp285_AST);
						match(ABSTRACT);
						break;
					}
					case EXTERN:
					{
						AST tmp286_AST = null;
						tmp286_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp286_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop336_breakloop;
					}
					 }
				}
_loop336_breakloop:				;
			}    // ( ... )*
			event_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_118_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_modifiers_AST;
	}
	
	public void event_accessor_declarations() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_accessor_declarations_AST = null;
		
		try {      // for error handling
			event_accessor_declaration();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case LBRACK:
				{
					event_accessor_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			event_accessor_declarations_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_66_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_accessor_declarations_AST;
	}
	
	public void event_accessor_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_accessor_declaration_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		AST st_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			event_accessor_modifiers();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			event_accessor_block();
			if (0 == inputState.guessing)
			{
				st_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				event_accessor_declaration_AST = (AST)currentAST.root;
				event_accessor_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.AccessorNode) astFactory.create(AccessorNode,mod_AST.getText())), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.Statements) astFactory.create(Statements), st_AST));
						
				currentAST.root = event_accessor_declaration_AST;
				if ( (null != event_accessor_declaration_AST) && (null != event_accessor_declaration_AST.getFirstChild()) )
					currentAST.child = event_accessor_declaration_AST.getFirstChild();
				else
					currentAST.child = event_accessor_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_117_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_accessor_declaration_AST;
	}
	
	public void event_accessor_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_accessor_modifiers_AST = null;
		
		try {      // for error handling
			if (!( LT(1).getText()=="add" || LT(1).getText()=="remove"))
			  throw new SemanticException(" LT(1).getText()==\"add\" || LT(1).getText()==\"remove\"");
			identifier();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			event_accessor_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_accessor_modifiers_AST;
	}
	
	public void event_accessor_block() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST event_accessor_block_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				event_accessor_block_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				event_accessor_block_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_117_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = event_accessor_block_AST;
	}
	
	public void indexer_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexer_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp288_AST = null;
						tmp288_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp288_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp289_AST = null;
						tmp289_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp289_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp290_AST = null;
						tmp290_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp290_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp291_AST = null;
						tmp291_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp291_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp292_AST = null;
						tmp292_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp292_AST);
						match(PRIVATE);
						break;
					}
					case VIRTUAL:
					{
						AST tmp293_AST = null;
						tmp293_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp293_AST);
						match(VIRTUAL);
						break;
					}
					case SEALED:
					{
						AST tmp294_AST = null;
						tmp294_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp294_AST);
						match(SEALED);
						break;
					}
					case OVERRIDE:
					{
						AST tmp295_AST = null;
						tmp295_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp295_AST);
						match(OVERRIDE);
						break;
					}
					case ABSTRACT:
					{
						AST tmp296_AST = null;
						tmp296_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp296_AST);
						match(ABSTRACT);
						break;
					}
					case EXTERN:
					{
						AST tmp297_AST = null;
						tmp297_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp297_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop347_breakloop;
					}
					 }
				}
_loop347_breakloop:				;
			}    // ( ... )*
			indexer_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_7_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = indexer_modifiers_AST;
	}
	
	public void indexer_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexer_declarator_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST pms_AST = null;
		
		try {      // for error handling
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				{
					simple_name();
					if (0 == inputState.guessing)
					{
						id_AST = (AST)returnAST;
					}
					AST tmp298_AST = null;
					tmp298_AST = astFactory.create(LT(1));
					match(DOT);
					break;
				}
				case THIS:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp299_AST = null;
			tmp299_AST = astFactory.create(LT(1));
			match(THIS);
			AST tmp300_AST = null;
			tmp300_AST = astFactory.create(LT(1));
			match(LBRACK);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp301_AST = null;
			tmp301_AST = astFactory.create(LT(1));
			match(RBRACK);
			if (0==inputState.guessing)
			{
				indexer_declarator_AST = (AST)currentAST.root;
				indexer_declarator_AST = 
							(AST) astFactory.make(indexer_declarator_AST, (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST, pms_AST);
						
				currentAST.root = indexer_declarator_AST;
				if ( (null != indexer_declarator_AST) && (null != indexer_declarator_AST.getFirstChild()) )
					currentAST.child = indexer_declarator_AST.getFirstChild();
				else
					currentAST.child = indexer_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = indexer_declarator_AST;
	}
	
	public void operator_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST operator_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case PUBLIC:
					{
						AST tmp302_AST = null;
						tmp302_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp302_AST);
						match(PUBLIC);
						break;
					}
					case STATIC:
					{
						AST tmp303_AST = null;
						tmp303_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp303_AST);
						match(STATIC);
						break;
					}
					case EXTERN:
					{
						AST tmp304_AST = null;
						tmp304_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp304_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop355_breakloop;
					}
					 }
				}
_loop355_breakloop:				;
			}    // ( ... )*
			operator_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_119_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = operator_modifiers_AST;
	}
	
	public void operator_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST operator_declarator_AST = null;
		
		try {      // for error handling
			bool synPredMatched358 = false;
			if (((tokenSet_7_.member(LA(1))) && (LA(2)==DOT||LA(2)==LBRACK||LA(2)==OPERATOR)))
			{
				int _m358 = mark();
				synPredMatched358 = true;
				inputState.guessing++;
				try {
					{
						unary_operator_declarator();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched358 = false;
				}
				rewind(_m358);
				inputState.guessing--;
			}
			if ( synPredMatched358 )
			{
				unary_operator_declarator();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				operator_declarator_AST = currentAST.root;
			}
			else {
				bool synPredMatched360 = false;
				if (((tokenSet_7_.member(LA(1))) && (LA(2)==DOT||LA(2)==LBRACK||LA(2)==OPERATOR)))
				{
					int _m360 = mark();
					synPredMatched360 = true;
					inputState.guessing++;
					try {
						{
							binary_operator_declarator();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched360 = false;
					}
					rewind(_m360);
					inputState.guessing--;
				}
				if ( synPredMatched360 )
				{
					binary_operator_declarator();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					operator_declarator_AST = currentAST.root;
				}
				else {
					bool synPredMatched362 = false;
					if (((LA(1)==IMPLICIT||LA(1)==EXPLICIT)))
					{
						int _m362 = mark();
						synPredMatched362 = true;
						inputState.guessing++;
						try {
							{
								conversion_operator_declarator();
							}
						}
						catch (RecognitionException)
						{
							synPredMatched362 = false;
						}
						rewind(_m362);
						inputState.guessing--;
					}
					if ( synPredMatched362 )
					{
						conversion_operator_declarator();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						operator_declarator_AST = currentAST.root;
					}
					else
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}}
				}
				catch (RecognitionException ex)
				{
					if (0 == inputState.guessing)
					{
						reportError(ex);
						recover(ex,tokenSet_108_);
					}
					else
					{
						throw ex;
					}
				}
				returnAST = operator_declarator_AST;
			}
			
	public void operator_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST operator_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				operator_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				operator_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = operator_body_AST;
	}
	
	public void unary_operator_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unary_operator_declarator_AST = null;
		AST tp1_AST = null;
		AST uop_AST = null;
		AST tp2_AST = null;
		AST id_AST = null;
		
		try {      // for error handling
			type();
			if (0 == inputState.guessing)
			{
				tp1_AST = (AST)returnAST;
			}
			AST tmp306_AST = null;
			tmp306_AST = astFactory.create(LT(1));
			match(OPERATOR);
			overloadable_unary_operator();
			if (0 == inputState.guessing)
			{
				uop_AST = (AST)returnAST;
			}
			AST tmp307_AST = null;
			tmp307_AST = astFactory.create(LT(1));
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				tp2_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp308_AST = null;
			tmp308_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				unary_operator_declarator_AST = (AST)currentAST.root;
				unary_operator_declarator_AST = 
							(AST) astFactory.make(unary_operator_declarator_AST, (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp1_AST), (AST) astFactory.make((DDW.CSharp.Parse.Op) astFactory.create(Op,"unary"), uop_AST), (AST) astFactory.make((DDW.CSharp.Parse.DeclArgs) astFactory.create(DeclArgs), (AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp2_AST), id_AST)));
						
				currentAST.root = unary_operator_declarator_AST;
				if ( (null != unary_operator_declarator_AST) && (null != unary_operator_declarator_AST.getFirstChild()) )
					currentAST.child = unary_operator_declarator_AST.getFirstChild();
				else
					currentAST.child = unary_operator_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = unary_operator_declarator_AST;
	}
	
	public void binary_operator_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST binary_operator_declarator_AST = null;
		AST tp_AST = null;
		AST obo_AST = null;
		AST tp1_AST = null;
		AST id1_AST = null;
		AST tp2_AST = null;
		AST id2_AST = null;
		
		try {      // for error handling
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			AST tmp309_AST = null;
			tmp309_AST = astFactory.create(LT(1));
			match(OPERATOR);
			overloadable_binary_operator();
			if (0 == inputState.guessing)
			{
				obo_AST = (AST)returnAST;
			}
			AST tmp310_AST = null;
			tmp310_AST = astFactory.create(LT(1));
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				tp1_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id1_AST = (AST)returnAST;
			}
			AST tmp311_AST = null;
			tmp311_AST = astFactory.create(LT(1));
			match(COMMA);
			type();
			if (0 == inputState.guessing)
			{
				tp2_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id2_AST = (AST)returnAST;
			}
			AST tmp312_AST = null;
			tmp312_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				binary_operator_declarator_AST = (AST)currentAST.root;
				binary_operator_declarator_AST = 
							(AST) astFactory.make(binary_operator_declarator_AST, (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), (AST) astFactory.make((DDW.CSharp.Parse.Op) astFactory.create(Op,"binary"), obo_AST), (AST) astFactory.make((DDW.CSharp.Parse.DeclArgs) astFactory.create(DeclArgs), (AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp1_AST), id1_AST), (AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp2_AST), id2_AST)));
						
				currentAST.root = binary_operator_declarator_AST;
				if ( (null != binary_operator_declarator_AST) && (null != binary_operator_declarator_AST.getFirstChild()) )
					currentAST.child = binary_operator_declarator_AST.getFirstChild();
				else
					currentAST.child = binary_operator_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = binary_operator_declarator_AST;
	}
	
	public void conversion_operator_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conversion_operator_declarator_AST = null;
		AST cls_AST = null;
		AST tp_AST = null;
		AST atp_AST = null;
		AST id_AST = null;
		
		try {      // for error handling
			conversion_classification();
			if (0 == inputState.guessing)
			{
				cls_AST = (AST)returnAST;
			}
			AST tmp313_AST = null;
			tmp313_AST = astFactory.create(LT(1));
			match(OPERATOR);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			AST tmp314_AST = null;
			tmp314_AST = astFactory.create(LT(1));
			match(LPAREN);
			type();
			if (0 == inputState.guessing)
			{
				atp_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp315_AST = null;
			tmp315_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				conversion_operator_declarator_AST = (AST)currentAST.root;
				conversion_operator_declarator_AST = 
							(AST) astFactory.make(conversion_operator_declarator_AST, (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), (AST) astFactory.make((DDW.CSharp.Parse.Op) astFactory.create(Op,"conversion"), cls_AST), (AST) astFactory.make((DDW.CSharp.Parse.DeclArgs) astFactory.create(DeclArgs), (AST) astFactory.make((DDW.CSharp.Parse.DeclArg) astFactory.create(DeclArg), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), atp_AST), id_AST)));
						
				currentAST.root = conversion_operator_declarator_AST;
				if ( (null != conversion_operator_declarator_AST) && (null != conversion_operator_declarator_AST.getFirstChild()) )
					currentAST.child = conversion_operator_declarator_AST.getFirstChild();
				else
					currentAST.child = conversion_operator_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = conversion_operator_declarator_AST;
	}
	
	public void overloadable_unary_operator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST overloadable_unary_operator_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case PLUS:
			{
				AST tmp316_AST = null;
				tmp316_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp316_AST);
				match(PLUS);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case MINUS:
			{
				AST tmp317_AST = null;
				tmp317_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp317_AST);
				match(MINUS);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case LNOT:
			{
				AST tmp318_AST = null;
				tmp318_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp318_AST);
				match(LNOT);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case BNOT:
			{
				AST tmp319_AST = null;
				tmp319_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp319_AST);
				match(BNOT);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case STAR:
			{
				AST tmp320_AST = null;
				tmp320_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp320_AST);
				match(STAR);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case INC:
			{
				AST tmp321_AST = null;
				tmp321_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp321_AST);
				match(INC);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case DEC:
			{
				AST tmp322_AST = null;
				tmp322_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp322_AST);
				match(DEC);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp323_AST = null;
				tmp323_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp323_AST);
				match(TRUE);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp324_AST = null;
				tmp324_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp324_AST);
				match(FALSE);
				overloadable_unary_operator_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_120_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = overloadable_unary_operator_AST;
	}
	
	public void overloadable_binary_operator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST overloadable_binary_operator_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case PLUS:
			{
				AST tmp325_AST = null;
				tmp325_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp325_AST);
				match(PLUS);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case MINUS:
			{
				AST tmp326_AST = null;
				tmp326_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp326_AST);
				match(MINUS);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case STAR:
			{
				AST tmp327_AST = null;
				tmp327_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp327_AST);
				match(STAR);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case DIV:
			{
				AST tmp328_AST = null;
				tmp328_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp328_AST);
				match(DIV);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case MOD:
			{
				AST tmp329_AST = null;
				tmp329_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp329_AST);
				match(MOD);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case BAND:
			{
				AST tmp330_AST = null;
				tmp330_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp330_AST);
				match(BAND);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case BOR:
			{
				AST tmp331_AST = null;
				tmp331_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp331_AST);
				match(BOR);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case BXOR:
			{
				AST tmp332_AST = null;
				tmp332_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp332_AST);
				match(BXOR);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case SL:
			{
				AST tmp333_AST = null;
				tmp333_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp333_AST);
				match(SL);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case SR:
			{
				AST tmp334_AST = null;
				tmp334_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp334_AST);
				match(SR);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case EQUAL:
			{
				AST tmp335_AST = null;
				tmp335_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp335_AST);
				match(EQUAL);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case NOT_EQUAL:
			{
				AST tmp336_AST = null;
				tmp336_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp336_AST);
				match(NOT_EQUAL);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case GTHAN:
			{
				AST tmp337_AST = null;
				tmp337_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp337_AST);
				match(GTHAN);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case LTHAN:
			{
				AST tmp338_AST = null;
				tmp338_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp338_AST);
				match(LTHAN);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case GE:
			{
				AST tmp339_AST = null;
				tmp339_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp339_AST);
				match(GE);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			case LE:
			{
				AST tmp340_AST = null;
				tmp340_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp340_AST);
				match(LE);
				overloadable_binary_operator_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_120_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = overloadable_binary_operator_AST;
	}
	
	public void conversion_classification() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conversion_classification_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case IMPLICIT:
			{
				AST tmp341_AST = null;
				tmp341_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp341_AST);
				match(IMPLICIT);
				conversion_classification_AST = currentAST.root;
				break;
			}
			case EXPLICIT:
			{
				AST tmp342_AST = null;
				tmp342_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp342_AST);
				match(EXPLICIT);
				conversion_classification_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_121_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = conversion_classification_AST;
	}
	
	public void constructor_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case PUBLIC:
					{
						AST tmp343_AST = null;
						tmp343_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp343_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp344_AST = null;
						tmp344_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp344_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp345_AST = null;
						tmp345_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp345_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp346_AST = null;
						tmp346_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp346_AST);
						match(PRIVATE);
						break;
					}
					case EXTERN:
					{
						AST tmp347_AST = null;
						tmp347_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp347_AST);
						match(EXTERN);
						break;
					}
					default:
					{
						goto _loop374_breakloop;
					}
					 }
				}
_loop374_breakloop:				;
			}    // ( ... )*
			constructor_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_110_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_modifiers_AST;
	}
	
	public void constructor_declarator() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_declarator_AST = null;
		AST id_AST = null;
		AST pms_AST = null;
		AST ini_AST = null;
		
		try {      // for error handling
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp348_AST = null;
			tmp348_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp349_AST = null;
			tmp349_AST = astFactory.create(LT(1));
			match(RPAREN);
			{
				switch ( LA(1) )
				{
				case COLON:
				{
					constructor_initializer();
					if (0 == inputState.guessing)
					{
						ini_AST = (AST)returnAST;
					}
					break;
				}
				case LBRACE:
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				constructor_declarator_AST = (AST)currentAST.root;
				constructor_declarator_AST = 
							(AST) astFactory.make(constructor_declarator_AST, id_AST, pms_AST, ini_AST);
						
				currentAST.root = constructor_declarator_AST;
				if ( (null != constructor_declarator_AST) && (null != constructor_declarator_AST.getFirstChild()) )
					currentAST.child = constructor_declarator_AST.getFirstChild();
				else
					currentAST.child = constructor_declarator_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_declarator_AST;
	}
	
	public void constructor_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				constructor_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				constructor_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_body_AST;
	}
	
	public void constructor_initializer() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_initializer_AST = null;
		AST cim_AST = null;
		AST al_AST = null;
		
		try {      // for error handling
			AST tmp351_AST = null;
			tmp351_AST = astFactory.create(LT(1));
			match(COLON);
			constructor_init_modifiers();
			if (0 == inputState.guessing)
			{
				cim_AST = (AST)returnAST;
			}
			AST tmp352_AST = null;
			tmp352_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case REF:
				case OUT:
				case SIZEOF:
				{
					argument_list();
					if (0 == inputState.guessing)
					{
						al_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp353_AST = null;
			tmp353_AST = astFactory.create(LT(1));
			match(RPAREN);
			if (0==inputState.guessing)
			{
				constructor_initializer_AST = (AST)currentAST.root;
				constructor_initializer_AST = 
							(AST) astFactory.make(cim_AST, al_AST);
						
				currentAST.root = constructor_initializer_AST;
				if ( (null != constructor_initializer_AST) && (null != constructor_initializer_AST.getFirstChild()) )
					currentAST.child = constructor_initializer_AST.getFirstChild();
				else
					currentAST.child = constructor_initializer_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_108_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_initializer_AST;
	}
	
	public void constructor_init_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructor_init_modifiers_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case BASE:
			{
				AST tmp354_AST = null;
				tmp354_AST = astFactory.create(LT(1));
				match(BASE);
				if (0==inputState.guessing)
				{
					constructor_init_modifiers_AST = (AST)currentAST.root;
					constructor_init_modifiers_AST = (AST) astFactory.make((DDW.CSharp.Parse.BaseRefExpr) astFactory.create(BaseRefExpr));
					currentAST.root = constructor_init_modifiers_AST;
					if ( (null != constructor_init_modifiers_AST) && (null != constructor_init_modifiers_AST.getFirstChild()) )
						currentAST.child = constructor_init_modifiers_AST.getFirstChild();
					else
						currentAST.child = constructor_init_modifiers_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case THIS:
			{
				AST tmp355_AST = null;
				tmp355_AST = astFactory.create(LT(1));
				match(THIS);
				if (0==inputState.guessing)
				{
					constructor_init_modifiers_AST = (AST)currentAST.root;
					constructor_init_modifiers_AST = (AST) astFactory.make((DDW.CSharp.Parse.ThisRefExpr) astFactory.create(ThisRefExpr));
					currentAST.root = constructor_init_modifiers_AST;
					if ( (null != constructor_init_modifiers_AST) && (null != constructor_init_modifiers_AST.getFirstChild()) )
						currentAST.child = constructor_init_modifiers_AST.getFirstChild();
					else
						currentAST.child = constructor_init_modifiers_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_120_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = constructor_init_modifiers_AST;
	}
	
	public void static_constructor_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST static_constructor_modifiers_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case EXTERN:
				{
					AST tmp356_AST = null;
					tmp356_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp356_AST);
					match(EXTERN);
					break;
				}
				case STATIC:
				{
					AST tmp357_AST = null;
					tmp357_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp357_AST);
					match(STATIC);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case STATIC:
				{
					AST tmp358_AST = null;
					tmp358_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp358_AST);
					match(STATIC);
					break;
				}
				case EXTERN:
				{
					AST tmp359_AST = null;
					tmp359_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp359_AST);
					match(EXTERN);
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			static_constructor_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_110_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = static_constructor_modifiers_AST;
	}
	
	public void static_constructor_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST static_constructor_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				static_constructor_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				static_constructor_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = static_constructor_body_AST;
	}
	
	public void destructor_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST destructor_body_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case LBRACE:
			{
				block();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				destructor_body_AST = currentAST.root;
				break;
			}
			case SEMI:
			{
				match(SEMI);
				destructor_body_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_102_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = destructor_body_AST;
	}
	
	public void struct_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp362_AST = null;
						tmp362_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp362_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp363_AST = null;
						tmp363_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp363_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp364_AST = null;
						tmp364_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp364_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp365_AST = null;
						tmp365_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp365_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp366_AST = null;
						tmp366_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp366_AST);
						match(PRIVATE);
						break;
					}
					default:
					{
						goto _loop397_breakloop;
					}
					 }
				}
_loop397_breakloop:				;
			}    // ( ... )*
			struct_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_122_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = struct_modifiers_AST;
	}
	
	public void struct_interfaces() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_interfaces_AST = null;
		
		try {      // for error handling
			match(COLON);
			class_type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						class_type();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop400_breakloop;
					}
					
				}
_loop400_breakloop:				;
			}    // ( ... )*
			struct_interfaces_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = struct_interfaces_AST;
	}
	
	public void struct_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_body_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_123_.member(LA(1))))
					{
						struct_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop403_breakloop;
					}
					
				}
_loop403_breakloop:				;
			}    // ( ... )*
			match(RBRACE);
			struct_body_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = struct_body_AST;
	}
	
	public void struct_member_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST struct_member_declaration_AST = null;
		
		try {      // for error handling
			bool synPredMatched406 = false;
			if (((tokenSet_82_.member(LA(1))) && (tokenSet_83_.member(LA(2)))))
			{
				int _m406 = mark();
				synPredMatched406 = true;
				inputState.guessing++;
				try {
					{
						constant_declaration();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched406 = false;
				}
				rewind(_m406);
				inputState.guessing--;
			}
			if ( synPredMatched406 )
			{
				constant_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				struct_member_declaration_AST = currentAST.root;
			}
			else {
				bool synPredMatched408 = false;
				if (((tokenSet_84_.member(LA(1))) && (tokenSet_85_.member(LA(2)))))
				{
					int _m408 = mark();
					synPredMatched408 = true;
					inputState.guessing++;
					try {
						{
							field_declaration();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched408 = false;
					}
					rewind(_m408);
					inputState.guessing--;
				}
				if ( synPredMatched408 )
				{
					field_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					struct_member_declaration_AST = currentAST.root;
				}
				else {
					bool synPredMatched410 = false;
					if (((tokenSet_86_.member(LA(1))) && (tokenSet_87_.member(LA(2)))))
					{
						int _m410 = mark();
						synPredMatched410 = true;
						inputState.guessing++;
						try {
							{
								method_declaration();
							}
						}
						catch (RecognitionException)
						{
							synPredMatched410 = false;
						}
						rewind(_m410);
						inputState.guessing--;
					}
					if ( synPredMatched410 )
					{
						method_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						struct_member_declaration_AST = currentAST.root;
					}
					else {
						bool synPredMatched412 = false;
						if (((tokenSet_88_.member(LA(1))) && (tokenSet_89_.member(LA(2)))))
						{
							int _m412 = mark();
							synPredMatched412 = true;
							inputState.guessing++;
							try {
								{
									property_declaration();
								}
							}
							catch (RecognitionException)
							{
								synPredMatched412 = false;
							}
							rewind(_m412);
							inputState.guessing--;
						}
						if ( synPredMatched412 )
						{
							property_declaration();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
							struct_member_declaration_AST = currentAST.root;
						}
						else {
							bool synPredMatched414 = false;
							if (((tokenSet_90_.member(LA(1))) && (tokenSet_91_.member(LA(2)))))
							{
								int _m414 = mark();
								synPredMatched414 = true;
								inputState.guessing++;
								try {
									{
										event_declaration();
									}
								}
								catch (RecognitionException)
								{
									synPredMatched414 = false;
								}
								rewind(_m414);
								inputState.guessing--;
							}
							if ( synPredMatched414 )
							{
								event_declaration();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
								struct_member_declaration_AST = currentAST.root;
							}
							else {
								bool synPredMatched416 = false;
								if (((tokenSet_92_.member(LA(1))) && (tokenSet_93_.member(LA(2)))))
								{
									int _m416 = mark();
									synPredMatched416 = true;
									inputState.guessing++;
									try {
										{
											indexer_declaration();
										}
									}
									catch (RecognitionException)
									{
										synPredMatched416 = false;
									}
									rewind(_m416);
									inputState.guessing--;
								}
								if ( synPredMatched416 )
								{
									indexer_declaration();
									if (0 == inputState.guessing)
									{
										astFactory.addASTChild(ref currentAST, returnAST);
									}
									struct_member_declaration_AST = currentAST.root;
								}
								else {
									bool synPredMatched418 = false;
									if (((tokenSet_94_.member(LA(1))) && (tokenSet_95_.member(LA(2)))))
									{
										int _m418 = mark();
										synPredMatched418 = true;
										inputState.guessing++;
										try {
											{
												operator_declaration();
											}
										}
										catch (RecognitionException)
										{
											synPredMatched418 = false;
										}
										rewind(_m418);
										inputState.guessing--;
									}
									if ( synPredMatched418 )
									{
										operator_declaration();
										if (0 == inputState.guessing)
										{
											astFactory.addASTChild(ref currentAST, returnAST);
										}
										struct_member_declaration_AST = currentAST.root;
									}
									else {
										bool synPredMatched420 = false;
										if (((tokenSet_96_.member(LA(1))) && (tokenSet_97_.member(LA(2)))))
										{
											int _m420 = mark();
											synPredMatched420 = true;
											inputState.guessing++;
											try {
												{
													constructor_declaration();
												}
											}
											catch (RecognitionException)
											{
												synPredMatched420 = false;
											}
											rewind(_m420);
											inputState.guessing--;
										}
										if ( synPredMatched420 )
										{
											constructor_declaration();
											if (0 == inputState.guessing)
											{
												astFactory.addASTChild(ref currentAST, returnAST);
											}
											struct_member_declaration_AST = currentAST.root;
										}
										else {
											bool synPredMatched422 = false;
											if (((LA(1)==LBRACK||LA(1)==STATIC||LA(1)==EXTERN) && (tokenSet_99_.member(LA(2)))))
											{
												int _m422 = mark();
												synPredMatched422 = true;
												inputState.guessing++;
												try {
													{
														static_constructor_declaration();
													}
												}
												catch (RecognitionException)
												{
													synPredMatched422 = false;
												}
												rewind(_m422);
												inputState.guessing--;
											}
											if ( synPredMatched422 )
											{
												static_constructor_declaration();
												if (0 == inputState.guessing)
												{
													astFactory.addASTChild(ref currentAST, returnAST);
												}
												struct_member_declaration_AST = currentAST.root;
											}
											else if ((tokenSet_100_.member(LA(1))) && (tokenSet_101_.member(LA(2)))) {
												type_declaration();
												if (0 == inputState.guessing)
												{
													astFactory.addASTChild(ref currentAST, returnAST);
												}
												struct_member_declaration_AST = currentAST.root;
											}
											else
											{
												throw new NoViableAltException(LT(1), getFilename());
											}
											}}}}}}}}
										}
										catch (RecognitionException ex)
										{
											if (0 == inputState.guessing)
											{
												reportError(ex);
												recover(ex,tokenSet_124_);
											}
											else
											{
												throw ex;
											}
										}
										returnAST = struct_member_declaration_AST;
									}
									
	public void interface_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp371_AST = null;
						tmp371_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp371_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp372_AST = null;
						tmp372_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp372_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp373_AST = null;
						tmp373_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp373_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp374_AST = null;
						tmp374_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp374_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp375_AST = null;
						tmp375_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp375_AST);
						match(PRIVATE);
						break;
					}
					default:
					{
						goto _loop432_breakloop;
					}
					 }
				}
_loop432_breakloop:				;
			}    // ( ... )*
			interface_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_125_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_modifiers_AST;
	}
	
	public void interface_base() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_base_AST = null;
		
		try {      // for error handling
			match(COLON);
			class_type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						class_type();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop435_breakloop;
					}
					
				}
_loop435_breakloop:				;
			}    // ( ... )*
			interface_base_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_base_AST;
	}
	
	public void interface_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_body_AST = null;
		
		try {      // for error handling
			match(LBRACE);
			{    // ( ... )*
				for (;;)
				{
					if ((tokenSet_126_.member(LA(1))))
					{
						interface_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop438_breakloop;
					}
					
				}
_loop438_breakloop:				;
			}    // ( ... )*
			match(RBRACE);
			interface_body_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_body_AST;
	}
	
	public void interface_member_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_member_declaration_AST = null;
		
		try {      // for error handling
			bool synPredMatched441 = false;
			if (((tokenSet_127_.member(LA(1))) && (tokenSet_128_.member(LA(2)))))
			{
				int _m441 = mark();
				synPredMatched441 = true;
				inputState.guessing++;
				try {
					{
						interface_method_declaration();
					}
				}
				catch (RecognitionException)
				{
					synPredMatched441 = false;
				}
				rewind(_m441);
				inputState.guessing--;
			}
			if ( synPredMatched441 )
			{
				interface_method_declaration();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				interface_member_declaration_AST = currentAST.root;
			}
			else {
				bool synPredMatched443 = false;
				if (((tokenSet_129_.member(LA(1))) && (tokenSet_114_.member(LA(2)))))
				{
					int _m443 = mark();
					synPredMatched443 = true;
					inputState.guessing++;
					try {
						{
							interface_property_declaration();
						}
					}
					catch (RecognitionException)
					{
						synPredMatched443 = false;
					}
					rewind(_m443);
					inputState.guessing--;
				}
				if ( synPredMatched443 )
				{
					interface_property_declaration();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					interface_member_declaration_AST = currentAST.root;
				}
				else {
					bool synPredMatched445 = false;
					if (((LA(1)==LBRACK||LA(1)==NEW||LA(1)==EVENT) && (tokenSet_115_.member(LA(2)))))
					{
						int _m445 = mark();
						synPredMatched445 = true;
						inputState.guessing++;
						try {
							{
								interface_event_declaration();
							}
						}
						catch (RecognitionException)
						{
							synPredMatched445 = false;
						}
						rewind(_m445);
						inputState.guessing--;
					}
					if ( synPredMatched445 )
					{
						interface_event_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						interface_member_declaration_AST = currentAST.root;
					}
					else {
						bool synPredMatched447 = false;
						if (((tokenSet_129_.member(LA(1))) && (tokenSet_130_.member(LA(2)))))
						{
							int _m447 = mark();
							synPredMatched447 = true;
							inputState.guessing++;
							try {
								{
									interface_indexer_declaration();
								}
							}
							catch (RecognitionException)
							{
								synPredMatched447 = false;
							}
							rewind(_m447);
							inputState.guessing--;
						}
						if ( synPredMatched447 )
						{
							interface_indexer_declaration();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
							interface_member_declaration_AST = currentAST.root;
						}
						else
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}}}
					}
					catch (RecognitionException ex)
					{
						if (0 == inputState.guessing)
						{
							reportError(ex);
							recover(ex,tokenSet_131_);
						}
						else
						{
							throw ex;
						}
					}
					returnAST = interface_member_declaration_AST;
				}
				
	public void interface_method_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_method_declaration_AST = null;
		AST atr_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST ret_AST = null;
		AST id_AST = null;
		AST pms_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				case VOID:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case NEW:
				{
					mod = LT(1);
					mod_AST = astFactory.create(mod);
					match(NEW);
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case VOID:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			return_type();
			if (0 == inputState.guessing)
			{
				ret_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp380_AST = null;
			tmp380_AST = astFactory.create(LT(1));
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp381_AST = null;
			tmp381_AST = astFactory.create(LT(1));
			match(RPAREN);
			AST tmp382_AST = null;
			tmp382_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				interface_method_declaration_AST = (AST)currentAST.root;
				interface_method_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.MethodNode) astFactory.create(MethodNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), ret_AST), id_AST, pms_AST); 				
						
				currentAST.root = interface_method_declaration_AST;
				if ( (null != interface_method_declaration_AST) && (null != interface_method_declaration_AST.getFirstChild()) )
					currentAST.child = interface_method_declaration_AST.getFirstChild();
				else
					currentAST.child = interface_method_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_131_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_method_declaration_AST;
	}
	
	public void interface_property_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_property_declaration_AST = null;
		AST atr_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		AST ia_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case NEW:
				{
					mod = LT(1);
					mod_AST = astFactory.create(mod);
					match(NEW);
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp383_AST = null;
			tmp383_AST = astFactory.create(LT(1));
			match(LBRACE);
			interface_accessors();
			if (0 == inputState.guessing)
			{
				ia_AST = (AST)returnAST;
			}
			AST tmp384_AST = null;
			tmp384_AST = astFactory.create(LT(1));
			match(RBRACE);
			if (0==inputState.guessing)
			{
				interface_property_declaration_AST = (AST)currentAST.root;
				interface_property_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.PropertyNode) astFactory.create(PropertyNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST, ia_AST);
						
				currentAST.root = interface_property_declaration_AST;
				if ( (null != interface_property_declaration_AST) && (null != interface_property_declaration_AST.getFirstChild()) )
					currentAST.child = interface_property_declaration_AST.getFirstChild();
				else
					currentAST.child = interface_property_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_131_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_property_declaration_AST;
	}
	
	public void interface_event_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_event_declaration_AST = null;
		AST atr_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST id_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case NEW:
				case EVENT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case NEW:
				{
					mod = LT(1);
					mod_AST = astFactory.create(mod);
					match(NEW);
					break;
				}
				case EVENT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp385_AST = null;
			tmp385_AST = astFactory.create(LT(1));
			match(EVENT);
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			AST tmp386_AST = null;
			tmp386_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				interface_event_declaration_AST = (AST)currentAST.root;
				interface_event_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.EventNode) astFactory.create(EventNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), id_AST);
						
				currentAST.root = interface_event_declaration_AST;
				if ( (null != interface_event_declaration_AST) && (null != interface_event_declaration_AST.getFirstChild()) )
					currentAST.child = interface_event_declaration_AST.getFirstChild();
				else
					currentAST.child = interface_event_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_131_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_event_declaration_AST;
	}
	
	public void interface_indexer_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_indexer_declaration_AST = null;
		AST atr_AST = null;
		IToken  mod = null;
		AST mod_AST = null;
		AST tp_AST = null;
		AST pms_AST = null;
		AST ia_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case NEW:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			{
				switch ( LA(1) )
				{
				case NEW:
				{
					mod = LT(1);
					mod_AST = astFactory.create(mod);
					match(NEW);
					break;
				}
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			type();
			if (0 == inputState.guessing)
			{
				tp_AST = (AST)returnAST;
			}
			AST tmp387_AST = null;
			tmp387_AST = astFactory.create(LT(1));
			match(THIS);
			AST tmp388_AST = null;
			tmp388_AST = astFactory.create(LT(1));
			match(LBRACK);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LBRACK:
				case PARAMS:
				case REF:
				case OUT:
				{
					formal_parameter_list();
					if (0 == inputState.guessing)
					{
						pms_AST = (AST)returnAST;
					}
					break;
				}
				case RBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			AST tmp389_AST = null;
			tmp389_AST = astFactory.create(LT(1));
			match(RBRACK);
			AST tmp390_AST = null;
			tmp390_AST = astFactory.create(LT(1));
			match(LBRACE);
			interface_accessors();
			if (0 == inputState.guessing)
			{
				ia_AST = (AST)returnAST;
			}
			AST tmp391_AST = null;
			tmp391_AST = astFactory.create(LT(1));
			match(RBRACE);
			if (0==inputState.guessing)
			{
				interface_indexer_declaration_AST = (AST)currentAST.root;
				interface_indexer_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.IndexerNode) astFactory.create(IndexerNode)), atr_AST, (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), mod_AST), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tp_AST), pms_AST, ia_AST);
						
				currentAST.root = interface_indexer_declaration_AST;
				if ( (null != interface_indexer_declaration_AST) && (null != interface_indexer_declaration_AST.getFirstChild()) )
					currentAST.child = interface_indexer_declaration_AST.getFirstChild();
				else
					currentAST.child = interface_indexer_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_131_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_indexer_declaration_AST;
	}
	
	public void interface_accessors() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_accessors_AST = null;
		
		try {      // for error handling
			interface_accessor();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case LBRACK:
				{
					interface_accessor();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			interface_accessors_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_66_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_accessors_AST;
	}
	
	public void interface_accessor() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interface_accessor_AST = null;
		AST atr_AST = null;
		AST mod_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			accessor_modifier();
			if (0 == inputState.guessing)
			{
				mod_AST = (AST)returnAST;
			}
			AST tmp392_AST = null;
			tmp392_AST = astFactory.create(LT(1));
			match(SEMI);
			if (0==inputState.guessing)
			{
				interface_accessor_AST = (AST)currentAST.root;
				interface_accessor_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.AccessorNode) astFactory.create(AccessorNode,mod_AST.getText())), atr_AST);
						
				currentAST.root = interface_accessor_AST;
				if ( (null != interface_accessor_AST) && (null != interface_accessor_AST.getFirstChild()) )
					currentAST.child = interface_accessor_AST.getFirstChild();
				else
					currentAST.child = interface_accessor_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_117_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = interface_accessor_AST;
	}
	
	public void enum_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enum_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp393_AST = null;
						tmp393_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp393_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp394_AST = null;
						tmp394_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp394_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp395_AST = null;
						tmp395_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp395_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp396_AST = null;
						tmp396_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp396_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp397_AST = null;
						tmp397_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp397_AST);
						match(PRIVATE);
						break;
					}
					default:
					{
						goto _loop484_breakloop;
					}
					 }
				}
_loop484_breakloop:				;
			}    // ( ... )*
			enum_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_132_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = enum_modifiers_AST;
	}
	
	public void enum_base() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enum_base_AST = null;
		
		try {      // for error handling
			match(COLON);
			integral_type();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			enum_base_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_64_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = enum_base_AST;
	}
	
	public void enum_body() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enum_body_AST = null;
		
		try {      // for error handling
			bool synPredMatched475 = false;
			if (((LA(1)==LBRACE) && (LA(2)==IDENTIFIER||LA(2)==LBRACK||LA(2)==COMMA)))
			{
				int _m475 = mark();
				synPredMatched475 = true;
				inputState.guessing++;
				try {
					{
						match(LBRACE);
						{
							switch ( LA(1) )
							{
							case IDENTIFIER:
							case LBRACK:
							{
								enum_member_declaration();
								{    // ( ... )*
									for (;;)
									{
										if ((LA(1)==COMMA) && (LA(2)==IDENTIFIER||LA(2)==LBRACK))
										{
											match(COMMA);
											enum_member_declaration();
										}
										else
										{
											goto _loop474_breakloop;
										}
										
									}
_loop474_breakloop:									;
								}    // ( ... )*
								break;
							}
							case COMMA:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							 }
						}
						match(COMMA);
						match(RBRACE);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched475 = false;
				}
				rewind(_m475);
				inputState.guessing--;
			}
			if ( synPredMatched475 )
			{
				match(LBRACE);
				{
					switch ( LA(1) )
					{
					case IDENTIFIER:
					case LBRACK:
					{
						enum_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						{    // ( ... )*
							for (;;)
							{
								if ((LA(1)==COMMA) && (LA(2)==IDENTIFIER||LA(2)==LBRACK))
								{
									match(COMMA);
									enum_member_declaration();
									if (0 == inputState.guessing)
									{
										astFactory.addASTChild(ref currentAST, returnAST);
									}
								}
								else
								{
									goto _loop478_breakloop;
								}
								
							}
_loop478_breakloop:							;
						}    // ( ... )*
						break;
					}
					case COMMA:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				match(COMMA);
				match(RBRACE);
				enum_body_AST = currentAST.root;
			}
			else if ((LA(1)==LBRACE) && (LA(2)==IDENTIFIER||LA(2)==LBRACK||LA(2)==RBRACE)) {
				match(LBRACE);
				{
					switch ( LA(1) )
					{
					case IDENTIFIER:
					case LBRACK:
					{
						enum_member_declaration();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
						{    // ( ... )*
							for (;;)
							{
								if ((LA(1)==COMMA))
								{
									match(COMMA);
									enum_member_declaration();
									if (0 == inputState.guessing)
									{
										astFactory.addASTChild(ref currentAST, returnAST);
									}
								}
								else
								{
									goto _loop481_breakloop;
								}
								
							}
_loop481_breakloop:							;
						}    // ( ... )*
						break;
					}
					case RBRACE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				match(RBRACE);
				enum_body_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_78_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = enum_body_AST;
	}
	
	public void enum_member_declaration() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enum_member_declaration_AST = null;
		AST atr_AST = null;
		AST id_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			{
				switch ( LA(1) )
				{
				case LBRACK:
				{
					attributes();
					if (0 == inputState.guessing)
					{
						atr_AST = (AST)returnAST;
					}
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			identifier();
			if (0 == inputState.guessing)
			{
				id_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case ASSIGN:
				{
					match(ASSIGN);
					constant_expression();
					if (0 == inputState.guessing)
					{
						ex_AST = (AST)returnAST;
					}
					break;
				}
				case COMMA:
				case RBRACE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				enum_member_declaration_AST = (AST)currentAST.root;
				enum_member_declaration_AST = 
							(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.EnumMemberNode) astFactory.create(EnumMemberNode)), atr_AST, id_AST, (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression,"constant"), ex_AST)); 
						
				currentAST.root = enum_member_declaration_AST;
				if ( (null != enum_member_declaration_AST) && (null != enum_member_declaration_AST.getFirstChild()) )
					currentAST.child = enum_member_declaration_AST.getFirstChild();
				else
					currentAST.child = enum_member_declaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_133_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = enum_member_declaration_AST;
	}
	
	public void delegate_modifiers() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST delegate_modifiers_AST = null;
		
		try {      // for error handling
			{    // ( ... )*
				for (;;)
				{
					switch ( LA(1) )
					{
					case NEW:
					{
						AST tmp407_AST = null;
						tmp407_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp407_AST);
						match(NEW);
						break;
					}
					case PUBLIC:
					{
						AST tmp408_AST = null;
						tmp408_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp408_AST);
						match(PUBLIC);
						break;
					}
					case PROTECTED:
					{
						AST tmp409_AST = null;
						tmp409_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp409_AST);
						match(PROTECTED);
						break;
					}
					case INTERNAL:
					{
						AST tmp410_AST = null;
						tmp410_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp410_AST);
						match(INTERNAL);
						break;
					}
					case PRIVATE:
					{
						AST tmp411_AST = null;
						tmp411_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp411_AST);
						match(PRIVATE);
						break;
					}
					default:
					{
						goto _loop493_breakloop;
					}
					 }
				}
_loop493_breakloop:				;
			}    // ( ... )*
			delegate_modifiers_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_134_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = delegate_modifiers_AST;
	}
	
	public void attribute_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_list_AST = null;
		
		try {      // for error handling
			bool synPredMatched505 = false;
			if (((LA(1)==IDENTIFIER) && (LA(2)==DOT||LA(2)==COMMA||LA(2)==LPAREN)))
			{
				int _m505 = mark();
				synPredMatched505 = true;
				inputState.guessing++;
				try {
					{
						attribute();
						{    // ( ... )*
							for (;;)
							{
								if ((LA(1)==COMMA) && (LA(2)==IDENTIFIER))
								{
									match(COMMA);
									attribute();
								}
								else
								{
									goto _loop504_breakloop;
								}
								
							}
_loop504_breakloop:							;
						}    // ( ... )*
						match(COMMA);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched505 = false;
				}
				rewind(_m505);
				inputState.guessing--;
			}
			if ( synPredMatched505 )
			{
				attribute();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==COMMA) && (LA(2)==IDENTIFIER))
						{
							match(COMMA);
							attribute();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop507_breakloop;
						}
						
					}
_loop507_breakloop:					;
				}    // ( ... )*
				match(COMMA);
				attribute_list_AST = currentAST.root;
			}
			else if ((LA(1)==IDENTIFIER) && (tokenSet_135_.member(LA(2)))) {
				attribute();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==COMMA))
						{
							match(COMMA);
							attribute();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop509_breakloop;
						}
						
					}
_loop509_breakloop:					;
				}    // ( ... )*
				attribute_list_AST = currentAST.root;
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_24_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_list_AST;
	}
	
	public void attribute_section() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_section_AST = null;
		AST at_AST = null;
		AST atr_AST = null;
		
		try {      // for error handling
			AST tmp415_AST = null;
			tmp415_AST = astFactory.create(LT(1));
			match(LBRACK);
			{
				switch ( LA(1) )
				{
				case RETURN:
				case EVENT:
				case FIELD:
				case METHOD:
				case MODULE:
				case PARAM:
				case PROPERTY:
				case TYPE:
				{
					attribute_target();
					if (0 == inputState.guessing)
					{
						at_AST = (AST)returnAST;
					}
					AST tmp416_AST = null;
					tmp416_AST = astFactory.create(LT(1));
					match(COLON);
					break;
				}
				case IDENTIFIER:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			attribute_list();
			if (0 == inputState.guessing)
			{
				atr_AST = (AST)returnAST;
			}
			AST tmp417_AST = null;
			tmp417_AST = astFactory.create(LT(1));
			match(RBRACK);
			if (0==inputState.guessing)
			{
				attribute_section_AST = (AST)currentAST.root;
					attribute_section_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.CustomAttributes) astFactory.create(CustomAttributes)), (AST) astFactory.make((DDW.CSharp.Parse.ModifierAttributes) astFactory.create(ModifierAttributes), at_AST), atr_AST);
				currentAST.root = attribute_section_AST;
				if ( (null != attribute_section_AST) && (null != attribute_section_AST.getFirstChild()) )
					currentAST.child = attribute_section_AST.getFirstChild();
				else
					currentAST.child = attribute_section_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_136_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_section_AST;
	}
	
	public void attribute_target() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_target_AST = null;
		
		try {      // for error handling
			switch ( LA(1) )
			{
			case FIELD:
			{
				AST tmp418_AST = null;
				tmp418_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp418_AST);
				match(FIELD);
				attribute_target_AST = currentAST.root;
				break;
			}
			case EVENT:
			{
				AST tmp419_AST = null;
				tmp419_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp419_AST);
				match(EVENT);
				attribute_target_AST = currentAST.root;
				break;
			}
			case METHOD:
			{
				AST tmp420_AST = null;
				tmp420_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp420_AST);
				match(METHOD);
				attribute_target_AST = currentAST.root;
				break;
			}
			case MODULE:
			{
				AST tmp421_AST = null;
				tmp421_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp421_AST);
				match(MODULE);
				attribute_target_AST = currentAST.root;
				break;
			}
			case PARAM:
			{
				AST tmp422_AST = null;
				tmp422_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp422_AST);
				match(PARAM);
				attribute_target_AST = currentAST.root;
				break;
			}
			case PROPERTY:
			{
				AST tmp423_AST = null;
				tmp423_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp423_AST);
				match(PROPERTY);
				attribute_target_AST = currentAST.root;
				break;
			}
			case RETURN:
			{
				AST tmp424_AST = null;
				tmp424_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp424_AST);
				match(RETURN);
				attribute_target_AST = currentAST.root;
				break;
			}
			case TYPE:
			{
				AST tmp425_AST = null;
				tmp425_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp425_AST);
				match(TYPE);
				attribute_target_AST = currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_55_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_target_AST;
	}
	
	public void attribute() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_AST = null;
		AST tn_AST = null;
		AST aa_AST = null;
		
		try {      // for error handling
			type_name();
			if (0 == inputState.guessing)
			{
				tn_AST = (AST)returnAST;
			}
			{
				switch ( LA(1) )
				{
				case LPAREN:
				{
					attribute_arguments();
					if (0 == inputState.guessing)
					{
						aa_AST = (AST)returnAST;
					}
					break;
				}
				case COMMA:
				case RBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			if (0==inputState.guessing)
			{
				attribute_AST = (AST)currentAST.root;
				attribute_AST = (AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.CustomAttribute) astFactory.create(CustomAttribute)), (AST) astFactory.make((DDW.CSharp.Parse.TypeRef) astFactory.create(TypeRef), tn_AST), aa_AST); 
						
				currentAST.root = attribute_AST;
				if ( (null != attribute_AST) && (null != attribute_AST.getFirstChild()) )
					currentAST.child = attribute_AST.getFirstChild();
				else
					currentAST.child = attribute_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_137_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_AST;
	}
	
	public void attribute_arguments() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_arguments_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			{
				switch ( LA(1) )
				{
				case IDENTIFIER:
				case INTEGER_LITERAL:
				case HEXADECIMAL_INTEGER_LITERAL:
				case REAL_LITERAL:
				case CHARACTER_LITERAL:
				case STRING_LITERAL:
				case TRUE:
				case FALSE:
				case NULL:
				case OBJECT:
				case STRING:
				case BOOL:
				case DECIMAL:
				case SBYTE:
				case BYTE:
				case SHORT:
				case USHORT:
				case INT:
				case UINT:
				case LONG:
				case ULONG:
				case CHAR:
				case FLOAT:
				case DOUBLE:
				case LPAREN:
				case THIS:
				case BASE:
				case INC:
				case DEC:
				case NEW:
				case TYPEOF:
				case CHECKED:
				case UNCHECKED:
				case PLUS:
				case MINUS:
				case LNOT:
				case BNOT:
				case STAR:
				case SIZEOF:
				{
					attribute_argument_list();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			match(RPAREN);
			attribute_arguments_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_137_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_arguments_AST;
	}
	
	public void attribute_argument_list() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_argument_list_AST = null;
		
		try {      // for error handling
			attribute_argument();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						match(COMMA);
						attribute_argument();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop516_breakloop;
					}
					
				}
_loop516_breakloop:				;
			}    // ( ... )*
			if (0==inputState.guessing)
			{
				attribute_argument_list_AST = (AST)currentAST.root;
				attribute_argument_list_AST = (AST) astFactory.make((DDW.CSharp.Parse.Args) astFactory.create(Args), attribute_argument_list_AST);
				currentAST.root = attribute_argument_list_AST;
				if ( (null != attribute_argument_list_AST) && (null != attribute_argument_list_AST.getFirstChild()) )
					currentAST.child = attribute_argument_list_AST.getFirstChild();
				else
					currentAST.child = attribute_argument_list_AST;
				currentAST.advanceChildToEnd();
			}
			attribute_argument_list_AST = currentAST.root;
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_19_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_argument_list_AST;
	}
	
	public void attribute_argument() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_argument_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			expression();
			if (0 == inputState.guessing)
			{
				ex_AST = (AST)returnAST;
			}
			if (0==inputState.guessing)
			{
				attribute_argument_AST = (AST)currentAST.root;
				attribute_argument_AST = 
						(AST) astFactory.make((AST) astFactory.make((DDW.CSharp.Parse.Arg) astFactory.create(Arg)), (AST) astFactory.make((DDW.CSharp.Parse.Expression) astFactory.create(Expression), ex_AST)); 
					
				currentAST.root = attribute_argument_AST;
				if ( (null != attribute_argument_AST) && (null != attribute_argument_AST.getFirstChild()) )
					currentAST.child = attribute_argument_AST.getFirstChild();
				else
					currentAST.child = attribute_argument_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex)
		{
			if (0 == inputState.guessing)
			{
				reportError(ex);
				recover(ex,tokenSet_14_);
			}
			else
			{
				throw ex;
			}
		}
		returnAST = attribute_argument_AST;
	}
	
	private void initializeFactory()
	{
		if (astFactory == null)
		{
			astFactory = new ASTFactory();
		}
		initializeASTFactory( astFactory );
	}
	static public void initializeASTFactory( ASTFactory factory )
	{
		factory.setMaxNodeType(267);
		factory.setTokenTypeASTNodeType(CompileUnit, "DDW.CSharp.Parse.CompileUnit");
		factory.setTokenTypeASTNodeType(UsingNode, "DDW.CSharp.Parse.UsingNode");
		factory.setTokenTypeASTNodeType(NamespaceNode, "DDW.CSharp.Parse.NamespaceNode");
		factory.setTokenTypeASTNodeType(ClassNode, "DDW.CSharp.Parse.ClassNode");
		factory.setTokenTypeASTNodeType(InterfaceNode, "DDW.CSharp.Parse.InterfaceNode");
		factory.setTokenTypeASTNodeType(StructNode, "DDW.CSharp.Parse.StructNode");
		factory.setTokenTypeASTNodeType(EnumNode, "DDW.CSharp.Parse.EnumNode");
		factory.setTokenTypeASTNodeType(DelegateNode, "DDW.CSharp.Parse.DelegateNode");
		factory.setTokenTypeASTNodeType(BaseTypes, "DDW.CSharp.Parse.BaseTypes");
		factory.setTokenTypeASTNodeType(BooleanLiteral, "DDW.CSharp.Parse.BooleanLiteral");
		factory.setTokenTypeASTNodeType(IntegerLiteral, "DDW.CSharp.Parse.IntegerLiteral");
		factory.setTokenTypeASTNodeType(RealLiteral, "DDW.CSharp.Parse.RealLiteral");
		factory.setTokenTypeASTNodeType(CharLiteral, "DDW.CSharp.Parse.CharLiteral");
		factory.setTokenTypeASTNodeType(StringLiteral, "DDW.CSharp.Parse.StringLiteral");
		factory.setTokenTypeASTNodeType(NullLiteral, "DDW.CSharp.Parse.NullLiteral");
		factory.setTokenTypeASTNodeType(Types, "DDW.CSharp.Parse.Types");
		factory.setTokenTypeASTNodeType(Members, "DDW.CSharp.Parse.Members");
		factory.setTokenTypeASTNodeType(MethodNode, "DDW.CSharp.Parse.MethodNode");
		factory.setTokenTypeASTNodeType(FieldNode, "DDW.CSharp.Parse.FieldNode");
		factory.setTokenTypeASTNodeType(PropertyNode, "DDW.CSharp.Parse.PropertyNode");
		factory.setTokenTypeASTNodeType(EventNode, "DDW.CSharp.Parse.EventNode");
		factory.setTokenTypeASTNodeType(ConstantNode, "DDW.CSharp.Parse.ConstantNode");
		factory.setTokenTypeASTNodeType(IndexerNode, "DDW.CSharp.Parse.IndexerNode");
		factory.setTokenTypeASTNodeType(OperatorNode, "DDW.CSharp.Parse.OperatorNode");
		factory.setTokenTypeASTNodeType(ConstructorNode, "DDW.CSharp.Parse.ConstructorNode");
		factory.setTokenTypeASTNodeType(DestructorNode, "DDW.CSharp.Parse.DestructorNode");
		factory.setTokenTypeASTNodeType(AccessorNode, "DDW.CSharp.Parse.AccessorNode");
		factory.setTokenTypeASTNodeType(EnumMemberNode, "DDW.CSharp.Parse.EnumMemberNode");
		factory.setTokenTypeASTNodeType(Ident, "DDW.CSharp.Parse.Ident");
		factory.setTokenTypeASTNodeType(QualIdent, "DDW.CSharp.Parse.QualIdent");
		factory.setTokenTypeASTNodeType(TypeRef, "DDW.CSharp.Parse.TypeRef");
		factory.setTokenTypeASTNodeType(BuiltInType, "DDW.CSharp.Parse.BuiltInType");
		factory.setTokenTypeASTNodeType(Args, "DDW.CSharp.Parse.Args");
		factory.setTokenTypeASTNodeType(Arg, "DDW.CSharp.Parse.Arg");
		factory.setTokenTypeASTNodeType(DeclArgs, "DDW.CSharp.Parse.DeclArgs");
		factory.setTokenTypeASTNodeType(DeclArg, "DDW.CSharp.Parse.DeclArg");
		factory.setTokenTypeASTNodeType(ArgDirection, "DDW.CSharp.Parse.ArgDirection");
		factory.setTokenTypeASTNodeType(Statements, "DDW.CSharp.Parse.Statements");
		factory.setTokenTypeASTNodeType(ExprStmt, "DDW.CSharp.Parse.ExprStmt");
		factory.setTokenTypeASTNodeType(CommentNode, "DDW.CSharp.Parse.CommentNode");
		factory.setTokenTypeASTNodeType(TryCatchFinallyStmt, "DDW.CSharp.Parse.TryCatchFinallyStmt");
		factory.setTokenTypeASTNodeType(TryStmt, "DDW.CSharp.Parse.TryStmt");
		factory.setTokenTypeASTNodeType(CatchClause, "DDW.CSharp.Parse.CatchClause");
		factory.setTokenTypeASTNodeType(FinallyStmt, "DDW.CSharp.Parse.FinallyStmt");
		factory.setTokenTypeASTNodeType(IfStmt, "DDW.CSharp.Parse.IfStmt");
		factory.setTokenTypeASTNodeType(SwitchStmt, "DDW.CSharp.Parse.SwitchStmt");
		factory.setTokenTypeASTNodeType(SwitchSection, "DDW.CSharp.Parse.SwitchSection");
		factory.setTokenTypeASTNodeType(IterationStmt, "DDW.CSharp.Parse.IterationStmt");
		factory.setTokenTypeASTNodeType(InitStmt, "DDW.CSharp.Parse.InitStmt");
		factory.setTokenTypeASTNodeType(IncStmt, "DDW.CSharp.Parse.IncStmt");
		factory.setTokenTypeASTNodeType(ForEachStmt, "DDW.CSharp.Parse.ForEachStmt");
		factory.setTokenTypeASTNodeType(GotoStmt, "DDW.CSharp.Parse.GotoStmt");
		factory.setTokenTypeASTNodeType(ReturnStmt, "DDW.CSharp.Parse.ReturnStmt");
		factory.setTokenTypeASTNodeType(BreakStmt, "DDW.CSharp.Parse.BreakStmt");
		factory.setTokenTypeASTNodeType(ContinueStmt, "DDW.CSharp.Parse.ContinueStmt");
		factory.setTokenTypeASTNodeType(ThrowStmt, "DDW.CSharp.Parse.ThrowStmt");
		factory.setTokenTypeASTNodeType(CheckedStmt, "DDW.CSharp.Parse.CheckedStmt");
		factory.setTokenTypeASTNodeType(UncheckedStmt, "DDW.CSharp.Parse.UncheckedStmt");
		factory.setTokenTypeASTNodeType(LockStmt, "DDW.CSharp.Parse.LockStmt");
		factory.setTokenTypeASTNodeType(UsingStmt, "DDW.CSharp.Parse.UsingStmt");
		factory.setTokenTypeASTNodeType(LabeledStmt, "DDW.CSharp.Parse.LabeledStmt");
		factory.setTokenTypeASTNodeType(VariableDeclStmt, "DDW.CSharp.Parse.VariableDeclStmt");
		factory.setTokenTypeASTNodeType(ConstantDeclStmt, "DDW.CSharp.Parse.ConstantDeclStmt");
		factory.setTokenTypeASTNodeType(Expressions, "DDW.CSharp.Parse.Expressions");
		factory.setTokenTypeASTNodeType(Expression, "DDW.CSharp.Parse.Expression");
		factory.setTokenTypeASTNodeType(PrimaryExpression, "DDW.CSharp.Parse.PrimaryExpression");
		factory.setTokenTypeASTNodeType(SubExpr, "DDW.CSharp.Parse.SubExpr");
		factory.setTokenTypeASTNodeType(PrimitiveExpr, "DDW.CSharp.Parse.PrimitiveExpr");
		factory.setTokenTypeASTNodeType(CastExpr, "DDW.CSharp.Parse.CastExpr");
		factory.setTokenTypeASTNodeType(ThisRefExpr, "DDW.CSharp.Parse.ThisRefExpr");
		factory.setTokenTypeASTNodeType(BaseRefExpr, "DDW.CSharp.Parse.BaseRefExpr");
		factory.setTokenTypeASTNodeType(MemberAccessExpr, "DDW.CSharp.Parse.MemberAccessExpr");
		factory.setTokenTypeASTNodeType(AssignExpr, "DDW.CSharp.Parse.AssignExpr");
		factory.setTokenTypeASTNodeType(UnaryExpr, "DDW.CSharp.Parse.UnaryExpr");
		factory.setTokenTypeASTNodeType(BinaryExpr, "DDW.CSharp.Parse.BinaryExpr");
		factory.setTokenTypeASTNodeType(TernaryExpr, "DDW.CSharp.Parse.TernaryExpr");
		factory.setTokenTypeASTNodeType(ArrayCreateExpr, "DDW.CSharp.Parse.ArrayCreateExpr");
		factory.setTokenTypeASTNodeType(ObjectCreateExpr, "DDW.CSharp.Parse.ObjectCreateExpr");
		factory.setTokenTypeASTNodeType(TypeOfExpr, "DDW.CSharp.Parse.TypeOfExpr");
		factory.setTokenTypeASTNodeType(PostfixExpr, "DDW.CSharp.Parse.PostfixExpr");
		factory.setTokenTypeASTNodeType(CheckedExpr, "DDW.CSharp.Parse.CheckedExpr");
		factory.setTokenTypeASTNodeType(UncheckedExpr, "DDW.CSharp.Parse.UncheckedExpr");
		factory.setTokenTypeASTNodeType(InvokeExpr, "DDW.CSharp.Parse.InvokeExpr");
		factory.setTokenTypeASTNodeType(IndexerExpr, "DDW.CSharp.Parse.IndexerExpr");
		factory.setTokenTypeASTNodeType(ArrayRankExpr, "DDW.CSharp.Parse.ArrayRankExpr");
		factory.setTokenTypeASTNodeType(ArrayInitExpr, "DDW.CSharp.Parse.ArrayInitExpr");
		factory.setTokenTypeASTNodeType(Op, "DDW.CSharp.Parse.Op");
		factory.setTokenTypeASTNodeType(Declarator, "DDW.CSharp.Parse.Declarator");
		factory.setTokenTypeASTNodeType(CustomAttributes, "DDW.CSharp.Parse.CustomAttributes");
		factory.setTokenTypeASTNodeType(CustomAttribute, "DDW.CSharp.Parse.CustomAttribute");
		factory.setTokenTypeASTNodeType(ModifierAttributes, "DDW.CSharp.Parse.ModifierAttributes");
	}
	
	public static readonly string[] tokenNames_ = new string[] {
		@"""<0>""",
		@"""EOF""",
		@"""<2>""",
		@"""NULL_TREE_LOOKAHEAD""",
		@"""CompileUnit""",
		@"""UsingNode""",
		@"""NamespaceNode""",
		@"""ClassNode""",
		@"""InterfaceNode""",
		@"""StructNode""",
		@"""EnumNode""",
		@"""DelegateNode""",
		@"""BaseTypes""",
		@"""BooleanLiteral""",
		@"""IntegerLiteral""",
		@"""RealLiteral""",
		@"""CharLiteral""",
		@"""StringLiteral""",
		@"""NullLiteral""",
		@"""Types""",
		@"""Members""",
		@"""MethodNode""",
		@"""FieldNode""",
		@"""PropertyNode""",
		@"""EventNode""",
		@"""ConstantNode""",
		@"""IndexerNode""",
		@"""OperatorNode""",
		@"""ConstructorNode""",
		@"""DestructorNode""",
		@"""AccessorNode""",
		@"""EnumMemberNode""",
		@"""Ident""",
		@"""QualIdent""",
		@"""TypeRef""",
		@"""BuiltInType""",
		@"""Args""",
		@"""Arg""",
		@"""DeclArgs""",
		@"""DeclArg""",
		@"""ArgDirection""",
		@"""Statements""",
		@"""ExprStmt""",
		@"""CommentNode""",
		@"""TryCatchFinallyStmt""",
		@"""TryStmt""",
		@"""CatchClause""",
		@"""FinallyStmt""",
		@"""IfStmt""",
		@"""SwitchStmt""",
		@"""SwitchSection""",
		@"""IterationStmt""",
		@"""InitStmt""",
		@"""IncStmt""",
		@"""ForEachStmt""",
		@"""GotoStmt""",
		@"""ReturnStmt""",
		@"""BreakStmt""",
		@"""ContinueStmt""",
		@"""ThrowStmt""",
		@"""CheckedStmt""",
		@"""UncheckedStmt""",
		@"""LockStmt""",
		@"""UsingStmt""",
		@"""LabeledStmt""",
		@"""VariableDeclStmt""",
		@"""ConstantDeclStmt""",
		@"""Expressions""",
		@"""Expression""",
		@"""PrimaryExpression""",
		@"""SubExpr""",
		@"""PrimitiveExpr""",
		@"""CastExpr""",
		@"""ThisRefExpr""",
		@"""BaseRefExpr""",
		@"""MemberAccessExpr""",
		@"""AssignExpr""",
		@"""UnaryExpr""",
		@"""BinaryExpr""",
		@"""TernaryExpr""",
		@"""ArrayCreateExpr""",
		@"""ObjectCreateExpr""",
		@"""TypeOfExpr""",
		@"""PostfixExpr""",
		@"""CheckedExpr""",
		@"""UncheckedExpr""",
		@"""InvokeExpr""",
		@"""IndexerExpr""",
		@"""ArrayRankExpr""",
		@"""ArrayInitExpr""",
		@"""Op""",
		@"""Declarator""",
		@"""CustomAttributes""",
		@"""CustomAttribute""",
		@"""ModifierAttributes""",
		@"""IDENTIFIER""",
		@"""SINGLE_LINE_COMMENT""",
		@"""DELIMITED_COMMENT""",
		@"""INTEGER_LITERAL""",
		@"""HEXADECIMAL_INTEGER_LITERAL""",
		@"""REAL_LITERAL""",
		@"""CHARACTER_LITERAL""",
		@"""STRING_LITERAL""",
		@"""true""",
		@"""false""",
		@"""null""",
		@"""DOT""",
		@"""object""",
		@"""string""",
		@"""bool""",
		@"""decimal""",
		@"""sbyte""",
		@"""byte""",
		@"""short""",
		@"""ushort""",
		@"""int""",
		@"""uint""",
		@"""long""",
		@"""ulong""",
		@"""char""",
		@"""float""",
		@"""double""",
		@"""LBRACK""",
		@"""COMMA""",
		@"""RBRACK""",
		@"""LPAREN""",
		@"""RPAREN""",
		@"""this""",
		@"""base""",
		@"""INC""",
		@"""DEC""",
		@"""new""",
		@"""typeof""",
		@"""void""",
		@"""checked""",
		@"""unchecked""",
		@"""PLUS""",
		@"""MINUS""",
		@"""LNOT""",
		@"""BNOT""",
		@"""STAR""",
		@"""DIV""",
		@"""MOD""",
		@"""SL""",
		@"""SR""",
		@"""LTHAN""",
		@"""GTHAN""",
		@"""LE""",
		@"""GE""",
		@"""is""",
		@"""as""",
		@"""EQUAL""",
		@"""NOT_EQUAL""",
		@"""BAND""",
		@"""BXOR""",
		@"""BOR""",
		@"""LAND""",
		@"""LOR""",
		@"""QUESTION""",
		@"""COLON""",
		@"""ASSIGN""",
		@"""PLUS_ASN""",
		@"""MINUS_ASN""",
		@"""STAR_ASN""",
		@"""DIV_ASN""",
		@"""MOD_ASN""",
		@"""BAND_ASN""",
		@"""BOR_ASN""",
		@"""BXOR_ASN""",
		@"""SL_ASN""",
		@"""SR_ASN""",
		@"""LBRACE""",
		@"""RBRACE""",
		@"""SEMI""",
		@"""const""",
		@"""if""",
		@"""else""",
		@"""switch""",
		@"""case""",
		@"""default""",
		@"""while""",
		@"""do""",
		@"""for""",
		@"""foreach""",
		@"""in""",
		@"""break""",
		@"""continue""",
		@"""goto""",
		@"""return""",
		@"""throw""",
		@"""lock""",
		@"""using""",
		@"""try""",
		@"""catch""",
		@"""finally""",
		@"""namespace""",
		@"""class""",
		@"""public""",
		@"""protected""",
		@"""internal""",
		@"""private""",
		@"""abstract""",
		@"""sealed""",
		@"""static""",
		@"""readonly""",
		@"""VOLATILE""",
		@"""virtual""",
		@"""override""",
		@"""extern""",
		@"""params""",
		@"""ref""",
		@"""out""",
		@"""event""",
		@"""operator""",
		@"""implicit""",
		@"""explicit""",
		@"""struct""",
		@"""interface""",
		@"""enum""",
		@"""delegate""",
		@"""assembly""",
		@"""field""",
		@"""method""",
		@"""module""",
		@"""param""",
		@"""property""",
		@"""type""",
		@"""sizeof""",
		@"""stackalloc""",
		@"""fixed""",
		@"""unsafe""",
		@"""NEW_LINE""",
		@"""NEW_LINE_CHARACTER""",
		@"""NOT_NEW_LINE""",
		@"""WHITESPACE""",
		@"""UNICODE_ESCAPE_SEQUENCE""",
		@"""IDENTIFIER_START_CHARACTER""",
		@"""IDENTIFIER_PART_CHARACTER""",
		@"""NUMERIC_LITERAL""",
		@"""DECIMAL_DIGIT""",
		@"""INTEGER_TYPE_SUFFIX""",
		@"""HEX_DIGIT""",
		@"""EXPONENT_PART""",
		@"""SIGN""",
		@"""REAL_TYPE_SUFFIX""",
		@"""CHARACTER""",
		@"""SINGLE_CHARACTER""",
		@"""SIMPLE_ESCAPE_SEQUENCE""",
		@"""HEXADECIMAL_ESCAPE_SEQUENCE""",
		@"""REGULAR_STRING_LITERAL""",
		@"""REGULAR_STRING_LITERAL_CHARACTER""",
		@"""SINGLE_REGULAR_STRING_LITERAL_CHARACTER""",
		@"""VERBATIM_STRING_LITERAL""",
		@"""BSR""",
		@"""BSR_ASN""",
		@"""HASH""",
		@"""QUOTE""",
		@"""PP_NEW_LINE""",
		@"""PP_WHITESPACE""",
		@"""PP_DIRECTIVE""",
		@"""PP_DECLARATION""",
		@"""PP_REGION""",
		@"""PP_MESSAGE""",
		@"""CONDITIONAL_SYMBOL""",
		@"""PPT_DEFINE""",
		@"""PPT_UNDEF""",
		@"""PPT_REGION""",
		@"""PPT_END_REGION"""
	};
	
	private static long[] mk_tokenSet_0_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=-288225975957716992L;
		data[2]=72127962782102278L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_0_ = new BitSet(mk_tokenSet_0_());
	private static long[] mk_tokenSet_1_()
	{
		long[] data = { 2L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_1_ = new BitSet(mk_tokenSet_1_());
	private static long[] mk_tokenSet_2_()
	{
		long[] data = { 0L, 8935146060897058816L, 61572651152134L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_2_ = new BitSet(mk_tokenSet_2_());
	private static long[] mk_tokenSet_3_()
	{
		long[] data = { 0L, 0L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_3_ = new BitSet(mk_tokenSet_3_());
	private static long[] mk_tokenSet_4_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=-288230374004228096L;
		data[2]=61576945991680L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_4_ = new BitSet(mk_tokenSet_4_());
	private static long[] mk_tokenSet_5_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=-288225975957716992L;
		data[2]=70368744174342L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_5_ = new BitSet(mk_tokenSet_5_());
	private static long[] mk_tokenSet_6_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-576460750155939840L;
		data[2]=52780852969472L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_6_ = new BitSet(mk_tokenSet_6_());
	private static long[] mk_tokenSet_7_()
	{
		long[] data = { 0L, 288221582206173184L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_7_ = new BitSet(mk_tokenSet_7_());
	private static long[] mk_tokenSet_8_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-576456352109428736L;
		data[2]=52780852969472L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_8_ = new BitSet(mk_tokenSet_8_());
	private static long[] mk_tokenSet_9_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-288230374004228096L;
		data[2]=52780852969472L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_9_ = new BitSet(mk_tokenSet_9_());
	private static long[] mk_tokenSet_10_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=-576460750155939840L;
		data[2]=61576945991680L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_10_ = new BitSet(mk_tokenSet_10_());
	private static long[] mk_tokenSet_11_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-288230374004228096L;
		data[2]=61576945991680L;
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_11_ = new BitSet(mk_tokenSet_11_());
	private static long[] mk_tokenSet_12_()
	{
		long[] data = { 2L, 5476377149030006784L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_12_ = new BitSet(mk_tokenSet_12_());
	private static long[] mk_tokenSet_13_()
	{
		long[] data = { 0L, 288230376151711744L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_13_ = new BitSet(mk_tokenSet_13_());
	private static long[] mk_tokenSet_14_()
	{
		long[] data = { 0L, 5188146770730811392L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_14_ = new BitSet(mk_tokenSet_14_());
	private static long[] mk_tokenSet_15_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=8159L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_15_ = new BitSet(mk_tokenSet_15_());
	private static long[] mk_tokenSet_16_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-15032385536L;
		data[2]=52780853100511L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_16_ = new BitSet(mk_tokenSet_16_());
	private static long[] mk_tokenSet_17_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6341068290370043904L;
		data[2]=8791798063071L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_17_ = new BitSet(mk_tokenSet_17_());
	private static long[] mk_tokenSet_18_()
	{
		long[] data = { 0L, 6341068277485142016L, 52778705616896L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_18_ = new BitSet(mk_tokenSet_18_());
	private static long[] mk_tokenSet_19_()
	{
		long[] data = { 0L, 4611686018427387904L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_19_ = new BitSet(mk_tokenSet_19_());
	private static long[] mk_tokenSet_20_()
	{
		long[] data = { 0L, 8935146060897058816L, 70368744174342L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_20_ = new BitSet(mk_tokenSet_20_());
	private static long[] mk_tokenSet_21_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=-15032385536L;
		data[2]=-72057594037927937L;
		data[3]=34625945593L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_21_ = new BitSet(mk_tokenSet_21_());
	private static long[] mk_tokenSet_22_()
	{
		long[] data = { 0L, 6341068277485142016L, 61572651152128L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_22_ = new BitSet(mk_tokenSet_22_());
	private static long[] mk_tokenSet_23_()
	{
		long[] data = { 0L, 4398046511104L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_23_ = new BitSet(mk_tokenSet_23_());
	private static long[] mk_tokenSet_24_()
	{
		long[] data = { 0L, 1152921504606846976L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_24_ = new BitSet(mk_tokenSet_24_());
	private static long[] mk_tokenSet_25_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=217L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_25_ = new BitSet(mk_tokenSet_25_());
	private static long[] mk_tokenSet_26_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-15032385536L;
		data[2]=61572651155423L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_26_ = new BitSet(mk_tokenSet_26_());
	private static long[] mk_tokenSet_27_()
	{
		long[] data = { 0L, 0L, 7942L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_27_ = new BitSet(mk_tokenSet_27_());
	private static long[] mk_tokenSet_28_()
	{
		long[] data = { 0L, 5188146770730811392L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_28_ = new BitSet(mk_tokenSet_28_());
	private static long[] mk_tokenSet_29_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780853068544L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_29_ = new BitSet(mk_tokenSet_29_());
	private static long[] mk_tokenSet_30_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780853067776L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_30_ = new BitSet(mk_tokenSet_30_());
	private static long[] mk_tokenSet_31_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780852969472L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_31_ = new BitSet(mk_tokenSet_31_());
	private static long[] mk_tokenSet_32_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780844711936L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_32_ = new BitSet(mk_tokenSet_32_());
	private static long[] mk_tokenSet_33_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780819546112L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_33_ = new BitSet(mk_tokenSet_33_());
	private static long[] mk_tokenSet_34_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780785991680L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_34_ = new BitSet(mk_tokenSet_34_());
	private static long[] mk_tokenSet_35_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780718882816L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_35_ = new BitSet(mk_tokenSet_35_());
	private static long[] mk_tokenSet_36_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780584665088L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_36_ = new BitSet(mk_tokenSet_36_());
	private static long[] mk_tokenSet_37_()
	{
		long[] data = { 0L, 6341068277485142016L, 52780316229632L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_37_ = new BitSet(mk_tokenSet_37_());
	private static long[] mk_tokenSet_38_()
	{
		long[] data = { 0L, 6341068277485142016L, 52779779358720L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_38_ = new BitSet(mk_tokenSet_38_());
	private static long[] mk_tokenSet_39_()
	{
		long[] data = { 0L, 576460752303423488L, 52778705616896L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_39_ = new BitSet(mk_tokenSet_39_());
	private static long[] mk_tokenSet_40_()
	{
		long[] data = { 0L, 4611686018427387904L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_40_ = new BitSet(mk_tokenSet_40_());
	private static long[] mk_tokenSet_41_()
	{
		long[] data = { 0L, 288221582206173184L, 70368744177664L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_41_ = new BitSet(mk_tokenSet_41_());
	private static long[] mk_tokenSet_42_()
	{
		long[] data = { 0L, 576456356404396032L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_42_ = new BitSet(mk_tokenSet_42_());
	private static long[] mk_tokenSet_43_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-75813525758402593L;
		data[3]=34359738369L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_43_ = new BitSet(mk_tokenSet_43_());
	private static long[] mk_tokenSet_44_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6341068290370043904L;
		data[2]=-72339073309597729L;
		data[3]=34359738369L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_44_ = new BitSet(mk_tokenSet_44_());
	private static long[] mk_tokenSet_45_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-72347865107652641L;
		data[3]=34359738369L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_45_ = new BitSet(mk_tokenSet_45_());
	private static long[] mk_tokenSet_46_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6341068290370043904L;
		data[2]=43976170151903L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_46_ = new BitSet(mk_tokenSet_46_());
	private static long[] mk_tokenSet_47_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-72066390130941985L;
		data[3]=34359738369L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_47_ = new BitSet(mk_tokenSet_47_());
	private static long[] mk_tokenSet_48_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6341072688416555008L;
		data[2]=-72066390130941953L;
		data[3]=34625159159L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_48_ = new BitSet(mk_tokenSet_48_());
	private static long[] mk_tokenSet_49_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-75743157014224929L;
		data[3]=34359738369L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_49_ = new BitSet(mk_tokenSet_49_());
	private static long[] mk_tokenSet_50_()
	{
		long[] data = { 0L, 0L, 3395291906572288L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_50_ = new BitSet(mk_tokenSet_50_());
	private static long[] mk_tokenSet_51_()
	{
		long[] data = { 0L, 576460752303423488L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_51_ = new BitSet(mk_tokenSet_51_());
	private static long[] mk_tokenSet_52_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-1152921519639232512L;
		data[2]=35184372096991L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_52_ = new BitSet(mk_tokenSet_52_());
	private static long[] mk_tokenSet_53_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6341068290370043904L;
		data[2]=-72057596185403393L;
		data[3]=34625159159L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_53_ = new BitSet(mk_tokenSet_53_());
	private static long[] mk_tokenSet_54_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=2147491807L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_54_ = new BitSet(mk_tokenSet_54_());
	private static long[] mk_tokenSet_55_()
	{
		long[] data = { 0L, 0L, 2147483648L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_55_ = new BitSet(mk_tokenSet_55_());
	private static long[] mk_tokenSet_56_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-5764607538066620416L;
		data[2]=43976170151903L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_56_ = new BitSet(mk_tokenSet_56_());
	private static long[] mk_tokenSet_57_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-1729382271942656000L;
		data[2]=8793945538527L;
		data[3]=34359738368L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_57_ = new BitSet(mk_tokenSet_57_());
	private static long[] mk_tokenSet_58_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-72066390130941985L;
		data[3]=34359738373L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_58_ = new BitSet(mk_tokenSet_58_());
	private static long[] mk_tokenSet_59_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-6629303064568266752L;
		data[2]=-72066390130941985L;
		data[3]=34359738375L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_59_ = new BitSet(mk_tokenSet_59_());
	private static long[] mk_tokenSet_60_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=251660280L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_60_ = new BitSet(mk_tokenSet_60_());
	private static long[] mk_tokenSet_61_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=288230376151711744L;
		data[2]=-9223354444668731384L;
		data[3]=251660280L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_61_ = new BitSet(mk_tokenSet_61_());
	private static long[] mk_tokenSet_62_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=251660280L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_62_ = new BitSet(mk_tokenSet_62_());
	private static long[] mk_tokenSet_63_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=288230376151711744L;
		data[2]=17592186044424L;
		data[3]=251660280L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_63_ = new BitSet(mk_tokenSet_63_());
	private static long[] mk_tokenSet_64_()
	{
		long[] data = { 0L, 0L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_64_ = new BitSet(mk_tokenSet_64_());
	private static long[] mk_tokenSet_65_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=17592186044424L;
		data[3]=251660280L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_65_ = new BitSet(mk_tokenSet_65_());
	private static long[] mk_tokenSet_66_()
	{
		long[] data = { 0L, 0L, 17592186044416L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_66_ = new BitSet(mk_tokenSet_66_());
	private static long[] mk_tokenSet_67_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=2032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_67_ = new BitSet(mk_tokenSet_67_());
	private static long[] mk_tokenSet_68_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606846984L;
		data[3]=33823918064L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_68_ = new BitSet(mk_tokenSet_68_());
	private static long[] mk_tokenSet_69_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=16777696L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_69_ = new BitSet(mk_tokenSet_69_());
	private static long[] mk_tokenSet_70_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606846984L;
		data[3]=33840693728L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_70_ = new BitSet(mk_tokenSet_70_());
	private static long[] mk_tokenSet_71_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=33554912L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_71_ = new BitSet(mk_tokenSet_71_());
	private static long[] mk_tokenSet_72_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606846984L;
		data[3]=33857470944L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_72_ = new BitSet(mk_tokenSet_72_());
	private static long[] mk_tokenSet_73_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=67109344L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_73_ = new BitSet(mk_tokenSet_73_());
	private static long[] mk_tokenSet_74_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606846984L;
		data[3]=33891025376L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_74_ = new BitSet(mk_tokenSet_74_());
	private static long[] mk_tokenSet_75_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=134218208L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_75_ = new BitSet(mk_tokenSet_75_());
	private static long[] mk_tokenSet_76_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=1152921504606847016L;
		data[3]=33958134240L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_76_ = new BitSet(mk_tokenSet_76_());
	private static long[] mk_tokenSet_77_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=576451958357884928L;
		data[2]=87960930224168L;
		data[3]=265420792L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_77_ = new BitSet(mk_tokenSet_77_());
	private static long[] mk_tokenSet_78_()
	{
		long[] data = new long[8];
		data[0]=2L;
		data[1]=576451958357884928L;
		data[2]=123145302313000L;
		data[3]=265420792L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_78_ = new BitSet(mk_tokenSet_78_());
	private static long[] mk_tokenSet_79_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=70368744179752L;
		data[3]=266338288L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_79_ = new BitSet(mk_tokenSet_79_());
	private static long[] mk_tokenSet_80_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=16L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_80_ = new BitSet(mk_tokenSet_80_());
	private static long[] mk_tokenSet_81_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=70368744179752L;
		data[3]=265420784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_81_ = new BitSet(mk_tokenSet_81_());
	private static long[] mk_tokenSet_82_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=70368744177672L;
		data[3]=480L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_82_ = new BitSet(mk_tokenSet_82_());
	private static long[] mk_tokenSet_83_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=1152991873351024648L;
		data[3]=33823916512L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_83_ = new BitSet(mk_tokenSet_83_());
	private static long[] mk_tokenSet_84_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=8L;
		data[3]=14816L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_84_ = new BitSet(mk_tokenSet_84_());
	private static long[] mk_tokenSet_85_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606846984L;
		data[3]=33823930848L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_85_ = new BitSet(mk_tokenSet_85_());
	private static long[] mk_tokenSet_86_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=40L;
		data[3]=118752L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_86_ = new BitSet(mk_tokenSet_86_());
	private static long[] mk_tokenSet_87_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606847016L;
		data[3]=33824034784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_87_ = new BitSet(mk_tokenSet_87_());
	private static long[] mk_tokenSet_88_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=8L;
		data[3]=118752L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_88_ = new BitSet(mk_tokenSet_88_());
	private static long[] mk_tokenSet_89_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606846984L;
		data[3]=33824034784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_89_ = new BitSet(mk_tokenSet_89_());
	private static long[] mk_tokenSet_90_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=1167328L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_90_ = new BitSet(mk_tokenSet_90_());
	private static long[] mk_tokenSet_91_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=1152921504606846984L;
		data[3]=33824034784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_91_ = new BitSet(mk_tokenSet_91_());
	private static long[] mk_tokenSet_92_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=8L;
		data[3]=116704L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_92_ = new BitSet(mk_tokenSet_92_());
	private static long[] mk_tokenSet_93_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-8646915680450379776L;
		data[2]=1152921504606846984L;
		data[3]=33824032736L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_93_ = new BitSet(mk_tokenSet_93_());
	private static long[] mk_tokenSet_94_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=0L;
		data[3]=12650528L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_94_ = new BitSet(mk_tokenSet_94_());
	private static long[] mk_tokenSet_95_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606846976L;
		data[3]=33838663712L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_95_ = new BitSet(mk_tokenSet_95_());
	private static long[] mk_tokenSet_96_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230378299195392L;
		data[2]=0L;
		data[3]=66016L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_96_ = new BitSet(mk_tokenSet_96_());
	private static long[] mk_tokenSet_97_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2305843011361177600L;
		data[2]=1152921504606846976L;
		data[3]=33823982048L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_97_ = new BitSet(mk_tokenSet_97_());
	private static long[] mk_tokenSet_98_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606849024L;
		data[3]=33823916032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_98_ = new BitSet(mk_tokenSet_98_());
	private static long[] mk_tokenSet_99_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=2147483648L;
		data[2]=1152921504606846976L;
		data[3]=33823983616L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_99_ = new BitSet(mk_tokenSet_99_());
	private static long[] mk_tokenSet_100_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288230376151711744L;
		data[2]=8L;
		data[3]=251660272L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_100_ = new BitSet(mk_tokenSet_100_());
	private static long[] mk_tokenSet_101_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=1152921504606847016L;
		data[3]=34075576304L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_101_ = new BitSet(mk_tokenSet_101_());
	private static long[] mk_tokenSet_102_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=87960930224168L;
		data[3]=265420784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_102_ = new BitSet(mk_tokenSet_102_());
	private static long[] mk_tokenSet_103_()
	{
		long[] data = { 0L, 576460754450907136L, 35188667056128L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_103_ = new BitSet(mk_tokenSet_103_());
	private static long[] mk_tokenSet_104_()
	{
		long[] data = { 0L, 0L, 70368744177664L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_104_ = new BitSet(mk_tokenSet_104_());
	private static long[] mk_tokenSet_105_()
	{
		long[] data = { 0L, 2147483648L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_105_ = new BitSet(mk_tokenSet_105_());
	private static long[] mk_tokenSet_106_()
	{
		long[] data = { 0L, 576460754450907136L, 35184372088832L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_106_ = new BitSet(mk_tokenSet_106_());
	private static long[] mk_tokenSet_107_()
	{
		long[] data = { 0L, 576460754450907136L, 52776558133248L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_107_ = new BitSet(mk_tokenSet_107_());
	private static long[] mk_tokenSet_108_()
	{
		long[] data = { 0L, 0L, 43980465111040L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_108_ = new BitSet(mk_tokenSet_108_());
	private static long[] mk_tokenSet_109_()
	{
		long[] data = { 0L, 288221582206173184L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_109_ = new BitSet(mk_tokenSet_109_());
	private static long[] mk_tokenSet_110_()
	{
		long[] data = { 0L, 2147483648L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_110_ = new BitSet(mk_tokenSet_110_());
	private static long[] mk_tokenSet_111_()
	{
		long[] data = { 0L, 2305843009213693952L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_111_ = new BitSet(mk_tokenSet_111_());
	private static long[] mk_tokenSet_112_()
	{
		long[] data = { 0L, 5764607523034234880L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_112_ = new BitSet(mk_tokenSet_112_());
	private static long[] mk_tokenSet_113_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=0L;
		data[3]=786432L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_113_ = new BitSet(mk_tokenSet_113_());
	private static long[] mk_tokenSet_114_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606846976L;
		data[3]=33823916032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_114_ = new BitSet(mk_tokenSet_114_());
	private static long[] mk_tokenSet_115_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=1152921504606846976L;
		data[3]=33823916032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_115_ = new BitSet(mk_tokenSet_115_());
	private static long[] mk_tokenSet_116_()
	{
		long[] data = { 0L, 6341068275337658368L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_116_ = new BitSet(mk_tokenSet_116_());
	private static long[] mk_tokenSet_117_()
	{
		long[] data = { 0L, 288230378299195392L, 17592186044416L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_117_ = new BitSet(mk_tokenSet_117_());
	private static long[] mk_tokenSet_118_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=1048576L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_118_ = new BitSet(mk_tokenSet_118_());
	private static long[] mk_tokenSet_119_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=288221582206173184L;
		data[2]=0L;
		data[3]=12582912L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_119_ = new BitSet(mk_tokenSet_119_());
	private static long[] mk_tokenSet_120_()
	{
		long[] data = { 0L, 2305843009213693952L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_120_ = new BitSet(mk_tokenSet_120_());
	private static long[] mk_tokenSet_121_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=2097152L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_121_ = new BitSet(mk_tokenSet_121_());
	private static long[] mk_tokenSet_122_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=16777216L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_122_ = new BitSet(mk_tokenSet_122_());
	private static long[] mk_tokenSet_123_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=70368744177704L;
		data[3]=265420784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_123_ = new BitSet(mk_tokenSet_123_());
	private static long[] mk_tokenSet_124_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=87960930222120L;
		data[3]=265420784L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_124_ = new BitSet(mk_tokenSet_124_());
	private static long[] mk_tokenSet_125_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=33554432L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_125_ = new BitSet(mk_tokenSet_125_());
	private static long[] mk_tokenSet_126_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=40L;
		data[3]=1048576L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_126_ = new BitSet(mk_tokenSet_126_());
	private static long[] mk_tokenSet_127_()
	{
		long[] data = { 0L, 576451958357884928L, 40L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_127_ = new BitSet(mk_tokenSet_127_());
	private static long[] mk_tokenSet_128_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576456356404396032L;
		data[2]=1152921504606847008L;
		data[3]=33823916032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_128_ = new BitSet(mk_tokenSet_128_());
	private static long[] mk_tokenSet_129_()
	{
		long[] data = { 0L, 576451958357884928L, 8L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_129_ = new BitSet(mk_tokenSet_129_());
	private static long[] mk_tokenSet_130_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=-8646915680450379776L;
		data[2]=1152921504606846976L;
		data[3]=33823916032L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_130_ = new BitSet(mk_tokenSet_130_());
	private static long[] mk_tokenSet_131_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=17592186044456L;
		data[3]=1048576L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_131_ = new BitSet(mk_tokenSet_131_());
	private static long[] mk_tokenSet_132_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=67108864L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_132_ = new BitSet(mk_tokenSet_132_());
	private static long[] mk_tokenSet_133_()
	{
		long[] data = { 0L, 576460752303423488L, 17592186044416L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_133_ = new BitSet(mk_tokenSet_133_());
	private static long[] mk_tokenSet_134_()
	{
		long[] data = new long[8];
		for (int i = 0; i<=2; i++) { data[i]=0L; }
		data[3]=134217728L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_134_ = new BitSet(mk_tokenSet_134_());
	private static long[] mk_tokenSet_135_()
	{
		long[] data = { 0L, 4035229664170475520L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_135_ = new BitSet(mk_tokenSet_135_());
	private static long[] mk_tokenSet_136_()
	{
		long[] data = new long[8];
		data[0]=0L;
		data[1]=576451958357884928L;
		data[2]=70368744179752L;
		data[3]=266338288L;
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_136_ = new BitSet(mk_tokenSet_136_());
	private static long[] mk_tokenSet_137_()
	{
		long[] data = { 0L, 1729382256910270464L, 0L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_137_ = new BitSet(mk_tokenSet_137_());
	
}
}
