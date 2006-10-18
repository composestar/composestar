// $ANTLR 2.7.6 (2005-12-22): "cps.g" -> "CpsLexer.cs"$



namespace Composestar.StarLight.CpsParser
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
	
	public 	class CpsLexer : antlr.CharScanner	, TokenStream
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
		public const int LITERAL_concern = 51;
		public const int NAME = 52;
		public const int LPARENTHESIS = 53;
		public const int RPARENTHESIS = 54;
		public const int LITERAL_in = 55;
		public const int SEMICOLON = 56;
		public const int COMMA = 57;
		public const int COLON = 58;
		public const int DOT = 59;
		public const int LCURLY = 60;
		public const int RCURLY = 61;
		public const int LITERAL_filtermodule = 62;
		public const int PARAMETER_NAME = 63;
		public const int PARAMETERLIST_NAME = 64;
		public const int LITERAL_internals = 65;
		public const int LITERAL_externals = 66;
		public const int EQUALS = 67;
		public const int DOUBLE_COLON = 68;
		public const int STAR = 69;
		public const int LITERAL_conditions = 70;
		public const int LITERAL_inputfilters = 71;
		public const int FILTER_OP = 72;
		public const int OR = 73;
		public const int AND = 74;
		public const int NOT = 75;
		public const int HASH = 76;
		public const int LSQUARE = 77;
		public const int RSQUARE = 78;
		public const int LANGLE = 79;
		public const int RANGLE = 80;
		public const int SINGLEQUOTE = 81;
		public const int LITERAL_outputfilters = 82;
		public const int LITERAL_superimposition = 83;
		public const int LITERAL_selectors = 84;
		public const int PROLOG_EXPRESSION = 85;
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
		public const int COMMENTITEMS = 104;
		public const int COMMENT = 105;
		public const int WS = 106;
		public const int PROLOG_SUB_EXPRESSION = 107;
		
		public CpsLexer(Stream ins) : this(new ByteBuffer(ins))
		{
		}
		
		public CpsLexer(TextReader r) : this(new CharBuffer(r))
		{
		}
		
		public CpsLexer(InputBuffer ib)		 : this(new LexerSharedInputState(ib))
		{
		}
		
		public CpsLexer(LexerSharedInputState state) : base(state)
		{
			initialize();
		}

        public override void tab()
        {
            int t = 4;
            int c = getColumn();
            int nc = (((c - 1) / t) + 1) * t + 1;
            setColumn(nc);
        }

		private void initialize()
		{
			caseSensitiveLiterals = true;
			setCaseSensitive(true);
			literals = new Hashtable(100, (float) 0.4, null, Comparer.Default);
			literals.Add("internals", 65);
			literals.Add("selectors", 84);
			literals.Add("constraints", 89);
			literals.Add("superimposition", 83);
			literals.Add("inputfilters", 71);
			literals.Add("conditions", 70);
			literals.Add("outputfilters", 82);
			literals.Add("pre", 91);
			literals.Add("in", 55);
			literals.Add("externals", 66);
			literals.Add("prehard", 92);
			literals.Add("annotations", 88);
			literals.Add("filtermodules", 87);
			literals.Add("presoft", 90);
			literals.Add("implementation", 93);
			literals.Add("concern", 51);
			literals.Add("filtermodule", 62);
			literals.Add("by", 94);
			literals.Add("as", 95);
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
						case '&':
						{
							mAND(true);
							theRetToken = returnToken_;
							break;
						}
						case ',':
						{
							mCOMMA(true);
							theRetToken = returnToken_;
							break;
						}
						case '.':
						{
							mDOT(true);
							theRetToken = returnToken_;
							break;
						}
						case '#':
						{
							mHASH(true);
							theRetToken = returnToken_;
							break;
						}
						case '{':
						{
							mLCURLY(true);
							theRetToken = returnToken_;
							break;
						}
						case '(':
						{
							mLPARENTHESIS(true);
							theRetToken = returnToken_;
							break;
						}
						case '[':
						{
							mLSQUARE(true);
							theRetToken = returnToken_;
							break;
						}
						case '!':
						{
							mNOT(true);
							theRetToken = returnToken_;
							break;
						}
						case '>':
						{
							mRANGLE(true);
							theRetToken = returnToken_;
							break;
						}
						case '}':
						{
							mRCURLY(true);
							theRetToken = returnToken_;
							break;
						}
						case ')':
						{
							mRPARENTHESIS(true);
							theRetToken = returnToken_;
							break;
						}
						case ']':
						{
							mRSQUARE(true);
							theRetToken = returnToken_;
							break;
						}
						case ';':
						{
							mSEMICOLON(true);
							theRetToken = returnToken_;
							break;
						}
						case '*':
						{
							mSTAR(true);
							theRetToken = returnToken_;
							break;
						}
						case '\'':
						{
							mSINGLEQUOTE(true);
							theRetToken = returnToken_;
							break;
						}
						case '/':
						{
							mCOMMENT(true);
							theRetToken = returnToken_;
							break;
						}
						case '"':
						{
							mFILENAME(true);
							theRetToken = returnToken_;
							break;
						}
						case 'A':  case 'B':  case 'C':  case 'D':
						case 'E':  case 'F':  case 'G':  case 'H':
						case 'I':  case 'J':  case 'K':  case 'L':
						case 'M':  case 'N':  case 'O':  case 'P':
						case 'Q':  case 'R':  case 'S':  case 'T':
						case 'U':  case 'V':  case 'W':  case 'X':
						case 'Y':  case 'Z':  case '_':  case 'a':
						case 'b':  case 'c':  case 'd':  case 'e':
						case 'f':  case 'g':  case 'h':  case 'i':
						case 'j':  case 'k':  case 'l':  case 'm':
						case 'n':  case 'o':  case 'p':  case 'q':
						case 'r':  case 's':  case 't':  case 'u':
						case 'v':  case 'w':  case 'x':  case 'y':
						case 'z':
						{
							mNAME(true);
							theRetToken = returnToken_;
							break;
						}
						case '\t':  case '\n':  case '\u000c':  case '\r':
						case ' ':
						{
							mWS(false);
							theRetToken = returnToken_;
							break;
						}
						default:
							if ((cached_LA1=='<') && (cached_LA2=='-'))
							{
								mARROW_LEFT(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1==':') && (cached_LA2==':')) {
								mDOUBLE_COLON(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='='||cached_LA1=='~') && (cached_LA2=='>')) {
								mFILTER_OP(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='?') && (cached_LA2=='?')) {
								mPARAMETERLIST_NAME(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='?') && (tokenSet_0_.member(cached_LA2))) {
								mPARAMETER_NAME(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='|') && ((cached_LA2 >= '\u0000' && cached_LA2 <= '\u007f'))) {
								mPROLOG_EXPRESSION(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1==':') && (true)) {
								mCOLON(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='=') && (true)) {
								mEQUALS(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='<') && (true)) {
								mLANGLE(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='|') && (true)) {
								mOR(true);
								theRetToken = returnToken_;
							}
							else if ((cached_LA1=='?') && (true)) {
								mQUESTIONMARK(true);
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
						_ttype = testLiteralsTable(_ttype);
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
		
	public void mAND(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = AND;
		
		match('&');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mARROW_LEFT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = ARROW_LEFT;
		
		match("<-");
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
	
	public void mDOT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DOT;
		
		match('.');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mDOUBLE_COLON(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DOUBLE_COLON;
		
		match("::");
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mEQUALS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = EQUALS;
		
		match('=');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mFILTER_OP(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = FILTER_OP;
		
		switch ( cached_LA1 )
		{
		case '=':
		{
			match("=>");
			break;
		}
		case '~':
		{
			match("~>");
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
	
	public void mHASH(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = HASH;
		
		match('#');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLANGLE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LANGLE;
		
		match('<');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLCURLY(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LCURLY;
		
		match('{');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLPARENTHESIS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LPARENTHESIS;
		
		match('(');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mLSQUARE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LSQUARE;
		
		match('[');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mNOT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NOT;
		
		match('!');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mOR(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = OR;
		
		match('|');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRANGLE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RANGLE;
		
		match('>');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRCURLY(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RCURLY;
		
		match('}');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRPARENTHESIS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RPARENTHESIS;
		
		match(')');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mRSQUARE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = RSQUARE;
		
		match(']');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mSEMICOLON(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SEMICOLON;
		
		match(';');
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
	
	public void mSINGLEQUOTE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SINGLEQUOTE;
		
		match('\'');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mQUESTIONMARK(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = QUESTIONMARK;
		
		match('?');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mDIGIT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = DIGIT;
		
		matchRange('0','9');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mFILE_SPECIAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = FILE_SPECIAL;
		
		switch ( cached_LA1 )
		{
		case '\\':
		{
			match('\\');
			break;
		}
		case '/':
		{
			match('/');
			break;
		}
		case ':':
		{
			int _saveIndex = 0;
			_saveIndex = text.Length;
			mCOLON(false);
			text.Length = _saveIndex;
			break;
		}
		case ' ':
		{
			match(' ');
			break;
		}
		case '.':
		{
			mDOT(false);
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
	
	protected void mLETTER(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = LETTER;
		
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
	
	protected void mNEWLINE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NEWLINE;
		
		{
			bool synPredMatched259 = false;
			if (((cached_LA1=='\r') && (cached_LA2=='\n')))
			{
				int _m259 = mark();
				synPredMatched259 = true;
				inputState.guessing++;
				try {
					{
						match("\r\n");
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
				match("\r\n");
			}
			else if ((cached_LA1=='\r') && (true)) {
				match('\r');
			}
			else if ((cached_LA1=='\n')) {
				match('\n');
			}
			else
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			
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
	
	protected void mSPECIAL(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = SPECIAL;
		
		match('_');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mQUOTE(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = QUOTE;
		
		match('"');
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mCOMMENTITEMS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = COMMENTITEMS;
		
		bool synPredMatched264 = false;
		if (((cached_LA1=='*') && (cached_LA2=='/')))
		{
			int _m264 = mark();
			synPredMatched264 = true;
			inputState.guessing++;
			try {
				{
					match("*/");
				}
			}
			catch (RecognitionException)
			{
				synPredMatched264 = false;
			}
			rewind(_m264);
			inputState.guessing--;
		}
		if ( synPredMatched264 )
		{
			match("*/");
		}
		else if ((tokenSet_1_.member(cached_LA1)) && ((cached_LA2 >= '\u0000' && cached_LA2 <= '\u007f'))) {
			{
				{
					match(tokenSet_1_);
				}
			}
			mCOMMENTITEMS(false);
		}
		else if ((cached_LA1=='\n'||cached_LA1=='\r')) {
			mNEWLINE(false);
			mCOMMENTITEMS(false);
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
	
	public void mCOMMENT(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = COMMENT;
		
		{
			if ((cached_LA1=='/') && (cached_LA2=='/'))
			{
				match("//");
				{    // ( ... )*
					for (;;)
					{
						if ((tokenSet_1_.member(cached_LA1)))
						{
							{
								match(tokenSet_1_);
							}
						}
						else
						{
							goto _loop271_breakloop;
						}
						
					}
_loop271_breakloop:					;
				}    // ( ... )*
			}
			else if ((cached_LA1=='/') && (cached_LA2=='*')) {
				match("/*");
				mCOMMENTITEMS(false);
			}
			else
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
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
	
	public void mFILENAME(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = FILENAME;
		
		bool synPredMatched276 = false;
		if (((cached_LA1=='"') && (tokenSet_2_.member(cached_LA2))))
		{
			int _m276 = mark();
			synPredMatched276 = true;
			inputState.guessing++;
			try {
				{
					mQUOTE(false);
					{ // ( ... )+
						int _cnt275=0;
						for (;;)
						{
							switch ( cached_LA1 )
							{
							case 'A':  case 'B':  case 'C':  case 'D':
							case 'E':  case 'F':  case 'G':  case 'H':
							case 'I':  case 'J':  case 'K':  case 'L':
							case 'M':  case 'N':  case 'O':  case 'P':
							case 'Q':  case 'R':  case 'S':  case 'T':
							case 'U':  case 'V':  case 'W':  case 'X':
							case 'Y':  case 'Z':  case 'a':  case 'b':
							case 'c':  case 'd':  case 'e':  case 'f':
							case 'g':  case 'h':  case 'i':  case 'j':
							case 'k':  case 'l':  case 'm':  case 'n':
							case 'o':  case 'p':  case 'q':  case 'r':
							case 's':  case 't':  case 'u':  case 'v':
							case 'w':  case 'x':  case 'y':  case 'z':
							{
								mLETTER(false);
								break;
							}
							case '0':  case '1':  case '2':  case '3':
							case '4':  case '5':  case '6':  case '7':
							case '8':  case '9':
							{
								mDIGIT(false);
								break;
							}
							case '_':
							{
								mSPECIAL(false);
								break;
							}
							case ' ':  case '.':  case '/':  case ':':
							case '\\':
							{
								mFILE_SPECIAL(false);
								break;
							}
							default:
							{
								if (_cnt275 >= 1) { goto _loop275_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
							}
							break; }
							_cnt275++;
						}
_loop275_breakloop:						;
					}    // ( ... )+
					mQUOTE(false);
				}
			}
			catch (RecognitionException)
			{
				synPredMatched276 = false;
			}
			rewind(_m276);
			inputState.guessing--;
		}
		if ( synPredMatched276 )
		{
			int _saveIndex = 0;
			_saveIndex = text.Length;
			mQUOTE(false);
			text.Length = _saveIndex;
			{ // ( ... )+
				int _cnt278=0;
				for (;;)
				{
					switch ( cached_LA1 )
					{
					case 'A':  case 'B':  case 'C':  case 'D':
					case 'E':  case 'F':  case 'G':  case 'H':
					case 'I':  case 'J':  case 'K':  case 'L':
					case 'M':  case 'N':  case 'O':  case 'P':
					case 'Q':  case 'R':  case 'S':  case 'T':
					case 'U':  case 'V':  case 'W':  case 'X':
					case 'Y':  case 'Z':  case 'a':  case 'b':
					case 'c':  case 'd':  case 'e':  case 'f':
					case 'g':  case 'h':  case 'i':  case 'j':
					case 'k':  case 'l':  case 'm':  case 'n':
					case 'o':  case 'p':  case 'q':  case 'r':
					case 's':  case 't':  case 'u':  case 'v':
					case 'w':  case 'x':  case 'y':  case 'z':
					{
						mLETTER(false);
						break;
					}
					case '0':  case '1':  case '2':  case '3':
					case '4':  case '5':  case '6':  case '7':
					case '8':  case '9':
					{
						mDIGIT(false);
						break;
					}
					case '_':
					{
						mSPECIAL(false);
						break;
					}
					case ' ':  case '.':  case '/':  case ':':
					case '\\':
					{
						mFILE_SPECIAL(false);
						break;
					}
					default:
					{
						if (_cnt278 >= 1) { goto _loop278_breakloop; } else { throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());; }
					}
					break; }
					_cnt278++;
				}
_loop278_breakloop:				;
			}    // ( ... )+
			_saveIndex = text.Length;
			mQUOTE(false);
			text.Length = _saveIndex;
		}
		else if ((cached_LA1=='"') && (true)) {
			mQUOTE(false);
			if (0==inputState.guessing)
			{
				_ttype = QUOTE;
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
	
	public void mNAME(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = NAME;
		
		{
			switch ( cached_LA1 )
			{
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':  case 'a':  case 'b':
			case 'c':  case 'd':  case 'e':  case 'f':
			case 'g':  case 'h':  case 'i':  case 'j':
			case 'k':  case 'l':  case 'm':  case 'n':
			case 'o':  case 'p':  case 'q':  case 'r':
			case 's':  case 't':  case 'u':  case 'v':
			case 'w':  case 'x':  case 'y':  case 'z':
			{
				mLETTER(false);
				break;
			}
			case '_':
			{
				mSPECIAL(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
			}
			 }
		}
		{    // ( ... )*
			for (;;)
			{
				switch ( cached_LA1 )
				{
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case 'a':  case 'b':
				case 'c':  case 'd':  case 'e':  case 'f':
				case 'g':  case 'h':  case 'i':  case 'j':
				case 'k':  case 'l':  case 'm':  case 'n':
				case 'o':  case 'p':  case 'q':  case 'r':
				case 's':  case 't':  case 'u':  case 'v':
				case 'w':  case 'x':  case 'y':  case 'z':
				{
					mLETTER(false);
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mDIGIT(false);
					break;
				}
				case '_':
				{
					mSPECIAL(false);
					break;
				}
				default:
				{
					goto _loop282_breakloop;
				}
				 }
			}
_loop282_breakloop:			;
		}    // ( ... )*
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mPARAMETERLIST_NAME(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PARAMETERLIST_NAME;
		
		mQUESTIONMARK(false);
		mQUESTIONMARK(false);
		mNAME(false);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mPARAMETER_NAME(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PARAMETER_NAME;
		
		mQUESTIONMARK(false);
		mNAME(false);
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	public void mWS(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = WS;
		
		switch ( cached_LA1 )
		{
		case '\n':  case '\r':
		{
			mNEWLINE(false);
			if (0==inputState.guessing)
			{
				/*newline();*/ _ttype = Token.SKIP;
			}
			break;
		}
		case '\t':  case '\u000c':  case ' ':
		{
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
					match('\t');
					break;
				}
				case '\u000c':
				{
					match('\f');
					break;
				}
				default:
				{
					throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());
				}
				 }
			}
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
	
	public void mPROLOG_EXPRESSION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PROLOG_EXPRESSION;
		
		match('|');
		{    // ( ... )*
			for (;;)
			{
				if ((tokenSet_3_.member(cached_LA1)))
				{
					mPROLOG_SUB_EXPRESSION(false);
				}
				else
				{
					goto _loop291_breakloop;
				}
				
			}
_loop291_breakloop:			;
		}    // ( ... )*
		int _saveIndex = 0;
		_saveIndex = text.Length;
		mRCURLY(false);
		text.Length = _saveIndex;
		if (_createToken && (null == _token) && (_ttype != Token.SKIP))
		{
			_token = makeToken(_ttype);
			_token.setText(text.ToString(_begin, text.Length-_begin));
		}
		returnToken_ = _token;
	}
	
	protected void mPROLOG_SUB_EXPRESSION(bool _createToken) //throws RecognitionException, CharStreamException, TokenStreamException
{
		int _ttype; IToken _token=null; int _begin=text.Length;
		_ttype = PROLOG_SUB_EXPRESSION;
		
		switch ( cached_LA1 )
		{
		case '\u0000':  case '\u0001':  case '\u0002':  case '\u0003':
		case '\u0004':  case '\u0005':  case '\u0006':  case '\u0007':
		case '\u0008':  case '\t':  case '\u000b':  case '\u000c':
		case '\u000e':  case '\u000f':  case '\u0010':  case '\u0011':
		case '\u0012':  case '\u0013':  case '\u0014':  case '\u0015':
		case '\u0016':  case '\u0017':  case '\u0018':  case '\u0019':
		case '\u001a':  case '\u001b':  case '\u001c':  case '\u001d':
		case '\u001e':  case '\u001f':  case ' ':  case '!':
		case '"':  case '#':  case '$':  case '%':
		case '&':  case '\'':  case '(':  case ')':
		case '*':  case '+':  case ',':  case '-':
		case '.':  case '/':  case '0':  case '1':
		case '2':  case '3':  case '4':  case '5':
		case '6':  case '7':  case '8':  case '9':
		case ':':  case ';':  case '<':  case '=':
		case '>':  case '?':  case '@':  case 'A':
		case 'B':  case 'C':  case 'D':  case 'E':
		case 'F':  case 'G':  case 'H':  case 'I':
		case 'J':  case 'K':  case 'L':  case 'M':
		case 'N':  case 'O':  case 'P':  case 'Q':
		case 'R':  case 'S':  case 'T':  case 'U':
		case 'V':  case 'W':  case 'X':  case 'Y':
		case 'Z':  case '[':  case '\\':  case ']':
		case '^':  case '_':  case '`':  case 'a':
		case 'b':  case 'c':  case 'd':  case 'e':
		case 'f':  case 'g':  case 'h':  case 'i':
		case 'j':  case 'k':  case 'l':  case 'm':
		case 'n':  case 'o':  case 'p':  case 'q':
		case 'r':  case 's':  case 't':  case 'u':
		case 'v':  case 'w':  case 'x':  case 'y':
		case 'z':  case '|':  case '~':  case '\u007f':
		{
			{
				{
					match(tokenSet_4_);
				}
			}
			break;
		}
		case '{':
		{
			{
				mLCURLY(false);
				{    // ( ... )*
					for (;;)
					{
						if ((tokenSet_3_.member(cached_LA1)))
						{
							mPROLOG_SUB_EXPRESSION(false);
						}
						else
						{
							goto _loop297_breakloop;
						}
						
					}
_loop297_breakloop:					;
				}    // ( ... )*
				mRCURLY(false);
			}
			break;
		}
		case '\n':  case '\r':
		{
			mNEWLINE(false);
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
	
	
	private static long[] mk_tokenSet_0_()
	{
		long[] data = { 0L, 576460745995190270L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_0_ = new BitSet(mk_tokenSet_0_());
	private static long[] mk_tokenSet_1_()
	{
		long[] data = { -9217L, -1L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_1_ = new BitSet(mk_tokenSet_1_());
	private static long[] mk_tokenSet_2_()
	{
		long[] data = { 576390387854213120L, 576460746263625726L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_2_ = new BitSet(mk_tokenSet_2_());
	private static long[] mk_tokenSet_3_()
	{
		long[] data = { -1L, -2305843009213693953L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_3_ = new BitSet(mk_tokenSet_3_());
	private static long[] mk_tokenSet_4_()
	{
		long[] data = { -9217L, -2882303761517117441L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_4_ = new BitSet(mk_tokenSet_4_());
	
}
}
