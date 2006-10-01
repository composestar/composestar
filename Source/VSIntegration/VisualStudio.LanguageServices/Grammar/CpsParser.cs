// $ANTLR 2.7.6 (2005-12-22): "cps.g" -> "CpsParser.cs"$



namespace Composestar.StarLight.VisualStudio.LanguageServices
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
	
	public 	class CpsParser : antlr.LLkParser
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
		
		
  public bool sourceIncluded = false;        //source included in Cps file?
  public int startPos = 0;                      //starting position of embedded source (in bytes)
  public String sourceLang = null;              //source language
  public String sourceFile = null;              //source filename
  public int skipsize = 0;                      //how much to skip at the end when using an embedded implementation
		
		protected void initialize()
		{
			tokenNames = tokenNames_;
			initializeFactory();
		}
		
		
		protected CpsParser(TokenBuffer tokenBuf, int k) : base(tokenBuf, k)
		{
			initialize();
		}
		
		public CpsParser(TokenBuffer tokenBuf) : this(tokenBuf,1)
		{
		}
		
		protected CpsParser(TokenStream lexer, int k) : base(lexer,k)
		{
			initialize();
		}
		
		public CpsParser(TokenStream lexer) : this(lexer,1)
		{
		}
		
		public CpsParser(ParserSharedInputState state) : base(state,1)
		{
			initialize();
		}
		
	public void concern() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concern_AST = null;
		
		AST tmp1_AST = null;
		tmp1_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp1_AST);
		match(LITERAL_concern);
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp2_AST);
		match(NAME);
		{
			switch ( LA(1) )
			{
			case LPARENTHESIS:
			{
				match(LPARENTHESIS);
				formalParameters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(RPARENTHESIS);
				break;
			}
			case LITERAL_in:
			case LCURLY:
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
			case LITERAL_in:
			{
				match(LITERAL_in);
				@namespace();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		concernBlock();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		concern_AST = currentAST.root;
		returnAST = concern_AST;
	}
	
	public void formalParameters() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formalParameters_AST = null;
		
		formalParameterDef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==SEMICOLON))
				{
					match(SEMICOLON);
					formalParameterDef();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop6_breakloop;
				}
				
			}
_loop6_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			formalParameters_AST = (AST)currentAST.root;
			formalParameters_AST = (AST) astFactory.make(astFactory.create(FPMSET_,"formal parameters"), formalParameters_AST);
			currentAST.root = formalParameters_AST;
			if ( (null != formalParameters_AST) && (null != formalParameters_AST.getFirstChild()) )
				currentAST.child = formalParameters_AST.getFirstChild();
			else
				currentAST.child = formalParameters_AST;
			currentAST.advanceChildToEnd();
		}
		formalParameters_AST = currentAST.root;
		returnAST = formalParameters_AST;
	}
	
	public void @namespace() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespace_AST = null;
		
		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp7_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp9_AST = null;
					tmp9_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp9_AST);
					match(NAME);
				}
				else
				{
					goto _loop12_breakloop;
				}
				
			}
_loop12_breakloop:			;
		}    // ( ... )*
		namespace_AST = currentAST.root;
		returnAST = namespace_AST;
	}
	
	public void concernBlock() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concernBlock_AST = null;
		
		match(LCURLY);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==LITERAL_filtermodule))
				{
					filterModule();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop15_breakloop;
				}
				
			}
_loop15_breakloop:			;
		}    // ( ... )*
		{
			switch ( LA(1) )
			{
			case LITERAL_superimposition:
			{
				superImposition();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_implementation:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		concernEnd();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		concernBlock_AST = currentAST.root;
		returnAST = concernBlock_AST;
	}
	
	public void formalParameterDef() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formalParameterDef_AST = null;
		
		AST tmp11_AST = null;
		tmp11_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp11_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==COMMA))
				{
					match(COMMA);
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp13_AST);
					match(NAME);
				}
				else
				{
					goto _loop9_breakloop;
				}
				
			}
_loop9_breakloop:			;
		}    // ( ... )*
		match(COLON);
		type();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		if (0==inputState.guessing)
		{
			formalParameterDef_AST = (AST)currentAST.root;
			formalParameterDef_AST = (AST) astFactory.make(astFactory.create(FPMDEF_,"formal parameter definition"), formalParameterDef_AST);
			currentAST.root = formalParameterDef_AST;
			if ( (null != formalParameterDef_AST) && (null != formalParameterDef_AST.getFirstChild()) )
				currentAST.child = formalParameterDef_AST.getFirstChild();
			else
				currentAST.child = formalParameterDef_AST;
			currentAST.advanceChildToEnd();
		}
		formalParameterDef_AST = currentAST.root;
		returnAST = formalParameterDef_AST;
	}
	
	public void type() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		AST tmp15_AST = null;
		tmp15_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp15_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp17_AST);
					match(NAME);
				}
				else
				{
					goto _loop47_breakloop;
				}
				
			}
_loop47_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			type_AST = (AST)currentAST.root;
			type_AST = (AST) astFactory.make(astFactory.create(TYPE_,"type"), type_AST);
			currentAST.root = type_AST;
			if ( (null != type_AST) && (null != type_AST.getFirstChild()) )
				currentAST.child = type_AST.getFirstChild();
			else
				currentAST.child = type_AST;
			currentAST.advanceChildToEnd();
		}
		type_AST = currentAST.root;
		returnAST = type_AST;
	}
	
	public void filterModule() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModule_AST = null;
		
		AST tmp18_AST = null;
		tmp18_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp18_AST);
		match(LITERAL_filtermodule);
		AST tmp19_AST = null;
		tmp19_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp19_AST);
		match(NAME);
		{
			switch ( LA(1) )
			{
			case LPARENTHESIS:
			{
				filterModuleParameters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(LCURLY);
		{
			switch ( LA(1) )
			{
			case LITERAL_internals:
			{
				internals();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_externals:
			case LITERAL_conditions:
			case LITERAL_inputfilters:
			case LITERAL_outputfilters:
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
			case LITERAL_externals:
			{
				externals();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_conditions:
			case LITERAL_inputfilters:
			case LITERAL_outputfilters:
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
			case LITERAL_conditions:
			{
				conditions();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_inputfilters:
			case LITERAL_outputfilters:
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
			case LITERAL_inputfilters:
			{
				inputFilters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_outputfilters:
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
			case LITERAL_outputfilters:
			{
				outputFilters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(RCURLY);
		filterModule_AST = currentAST.root;
		returnAST = filterModule_AST;
	}
	
	public void superImposition() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST superImposition_AST = null;
		
		AST tmp22_AST = null;
		tmp22_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp22_AST);
		match(LITERAL_superimposition);
		superImpositionBlock();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		superImposition_AST = currentAST.root;
		returnAST = superImposition_AST;
	}
	
	public void concernEnd() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concernEnd_AST = null;
		
		switch ( LA(1) )
		{
		case LITERAL_implementation:
		{
			{
				implementation();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			concernEnd_AST = currentAST.root;
			break;
		}
		case RCURLY:
		{
			match(RCURLY);
			concernEnd_AST = currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		 }
		returnAST = concernEnd_AST;
	}
	
	public void implementation() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implementation_AST = null;
		
		AST tmp24_AST = null;
		tmp24_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp24_AST);
		match(LITERAL_implementation);
		implementationInner();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		implementation_AST = currentAST.root;
		returnAST = implementation_AST;
	}
	
	public void filterModuleParameters() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModuleParameters_AST = null;
		
		match(LPARENTHESIS);
		{
			switch ( LA(1) )
			{
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			{
				filterModuleParameterSet();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RPARENTHESIS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(RPARENTHESIS);
		if (0==inputState.guessing)
		{
			filterModuleParameters_AST = (AST)currentAST.root;
			filterModuleParameters_AST = (AST) astFactory.make(astFactory.create(FILTERMODULEPARAMETERS_,"filtermoduleparameters"), filterModuleParameters_AST);
			currentAST.root = filterModuleParameters_AST;
			if ( (null != filterModuleParameters_AST) && (null != filterModuleParameters_AST.getFirstChild()) )
				currentAST.child = filterModuleParameters_AST.getFirstChild();
			else
				currentAST.child = filterModuleParameters_AST;
			currentAST.advanceChildToEnd();
		}
		filterModuleParameters_AST = currentAST.root;
		returnAST = filterModuleParameters_AST;
	}
	
	public void internals() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST internals_AST = null;
		
		AST tmp27_AST = null;
		tmp27_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp27_AST);
		match(LITERAL_internals);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleInternal();
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
_loop39_breakloop:			;
		}    // ( ... )*
		internals_AST = currentAST.root;
		returnAST = internals_AST;
	}
	
	public void externals() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST externals_AST = null;
		
		AST tmp28_AST = null;
		tmp28_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp28_AST);
		match(LITERAL_externals);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleExternal();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop50_breakloop;
				}
				
			}
_loop50_breakloop:			;
		}    // ( ... )*
		externals_AST = currentAST.root;
		returnAST = externals_AST;
	}
	
	public void conditions() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditions_AST = null;
		
		AST tmp29_AST = null;
		tmp29_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp29_AST);
		match(LITERAL_conditions);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleCondition();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop101_breakloop;
				}
				
			}
_loop101_breakloop:			;
		}    // ( ... )*
		conditions_AST = currentAST.root;
		returnAST = conditions_AST;
	}
	
	public void inputFilters() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inputFilters_AST = null;
		
		AST tmp30_AST = null;
		tmp30_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp30_AST);
		match(LITERAL_inputfilters);
		generalFilterSet();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		inputFilters_AST = currentAST.root;
		returnAST = inputFilters_AST;
	}
	
	public void outputFilters() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST outputFilters_AST = null;
		
		AST tmp31_AST = null;
		tmp31_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp31_AST);
		match(LITERAL_outputfilters);
		generalFilterSet2();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		outputFilters_AST = currentAST.root;
		returnAST = outputFilters_AST;
	}
	
	public void filterModuleParameterSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModuleParameterSet_AST = null;
		
		{
			switch ( LA(1) )
			{
			case PARAMETER_NAME:
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp32_AST);
				match(PARAMETER_NAME);
				break;
			}
			case PARAMETERLIST_NAME:
			{
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp33_AST);
				match(PARAMETERLIST_NAME);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==COMMA))
				{
					match(COMMA);
					{
						switch ( LA(1) )
						{
						case PARAMETER_NAME:
						{
							AST tmp35_AST = null;
							tmp35_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp35_AST);
							match(PARAMETER_NAME);
							break;
						}
						case PARAMETERLIST_NAME:
						{
							AST tmp36_AST = null;
							tmp36_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp36_AST);
							match(PARAMETERLIST_NAME);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						 }
					}
				}
				else
				{
					goto _loop34_breakloop;
				}
				
			}
_loop34_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			filterModuleParameterSet_AST = (AST)currentAST.root;
			filterModuleParameterSet_AST = (AST) astFactory.make(astFactory.create(DECLAREDPARAMETER_,"declaredparameter"), filterModuleParameterSet_AST);
			currentAST.root = filterModuleParameterSet_AST;
			if ( (null != filterModuleParameterSet_AST) && (null != filterModuleParameterSet_AST.getFirstChild()) )
				currentAST.child = filterModuleParameterSet_AST.getFirstChild();
			else
				currentAST.child = filterModuleParameterSet_AST;
			currentAST.advanceChildToEnd();
		}
		filterModuleParameterSet_AST = currentAST.root;
		returnAST = filterModuleParameterSet_AST;
	}
	
	public void parameter() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameter_AST = null;
		
		AST tmp37_AST = null;
		tmp37_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp37_AST);
		match(PARAMETER_NAME);
		if (0==inputState.guessing)
		{
			parameter_AST = (AST)currentAST.root;
			parameter_AST = (AST) astFactory.make(astFactory.create(PARAMETER_,"parameter"), parameter_AST);
			currentAST.root = parameter_AST;
			if ( (null != parameter_AST) && (null != parameter_AST.getFirstChild()) )
				currentAST.child = parameter_AST.getFirstChild();
			else
				currentAST.child = parameter_AST;
			currentAST.advanceChildToEnd();
		}
		parameter_AST = currentAST.root;
		returnAST = parameter_AST;
	}
	
	public void parameterlist() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterlist_AST = null;
		
		AST tmp38_AST = null;
		tmp38_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp38_AST);
		match(PARAMETERLIST_NAME);
		if (0==inputState.guessing)
		{
			parameterlist_AST = (AST)currentAST.root;
			parameterlist_AST = (AST) astFactory.make(astFactory.create(PARAMETERLIST_,"parameterlist"), parameterlist_AST);
			currentAST.root = parameterlist_AST;
			if ( (null != parameterlist_AST) && (null != parameterlist_AST.getFirstChild()) )
				currentAST.child = parameterlist_AST.getFirstChild();
			else
				currentAST.child = parameterlist_AST;
			currentAST.advanceChildToEnd();
		}
		parameterlist_AST = currentAST.root;
		returnAST = parameterlist_AST;
	}
	
	public void singleInternal() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleInternal_AST = null;
		
		variableSet();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(COLON);
		{
			switch ( LA(1) )
			{
			case PARAMETER_NAME:
			{
				parameter();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case NAME:
			{
				type();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleInternal_AST = (AST)currentAST.root;
			singleInternal_AST = (AST) astFactory.make(astFactory.create(INTERNAL_,"internal"), singleInternal_AST);
			currentAST.root = singleInternal_AST;
			if ( (null != singleInternal_AST) && (null != singleInternal_AST.getFirstChild()) )
				currentAST.child = singleInternal_AST.getFirstChild();
			else
				currentAST.child = singleInternal_AST;
			currentAST.advanceChildToEnd();
		}
		singleInternal_AST = currentAST.root;
		returnAST = singleInternal_AST;
	}
	
	public void variableSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableSet_AST = null;
		
		AST tmp41_AST = null;
		tmp41_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp41_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==COMMA))
				{
					match(COMMA);
					AST tmp43_AST = null;
					tmp43_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp43_AST);
					match(NAME);
				}
				else
				{
					goto _loop44_breakloop;
				}
				
			}
_loop44_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			variableSet_AST = (AST)currentAST.root;
			variableSet_AST = (AST) astFactory.make(astFactory.create(VAR_,"variable"), variableSet_AST);
			currentAST.root = variableSet_AST;
			if ( (null != variableSet_AST) && (null != variableSet_AST.getFirstChild()) )
				currentAST.child = variableSet_AST.getFirstChild();
			else
				currentAST.child = variableSet_AST;
			currentAST.advanceChildToEnd();
		}
		variableSet_AST = currentAST.root;
		returnAST = variableSet_AST;
	}
	
	public void singleExternal() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleExternal_AST = null;
		
		variableSet();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(COLON);
		type();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{
			switch ( LA(1) )
			{
			case EQUALS:
			{
				externalReferance();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleExternal_AST = (AST)currentAST.root;
			singleExternal_AST = (AST) astFactory.make(astFactory.create(EXTERNAL_,"external"), singleExternal_AST);
			currentAST.root = singleExternal_AST;
			if ( (null != singleExternal_AST) && (null != singleExternal_AST.getFirstChild()) )
				currentAST.child = singleExternal_AST.getFirstChild();
			else
				currentAST.child = singleExternal_AST;
			currentAST.advanceChildToEnd();
		}
		singleExternal_AST = currentAST.root;
		returnAST = singleExternal_AST;
	}
	
	public void externalReferance() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST externalReferance_AST = null;
		
		match(EQUALS);
		concernReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(LPARENTHESIS);
		match(RPARENTHESIS);
		externalReferance_AST = currentAST.root;
		returnAST = externalReferance_AST;
	}
	
	public void concernReference() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concernReference_AST = null;
		
		fqn();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		concernReference_AST = currentAST.root;
		returnAST = concernReference_AST;
	}
	
	public void fqnDCNameCName() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fqnDCNameCName_AST = null;
		
		AST tmp49_AST = null;
		tmp49_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp49_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp51_AST = null;
					tmp51_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp51_AST);
					match(NAME);
				}
				else
				{
					goto _loop56_breakloop;
				}
				
			}
_loop56_breakloop:			;
		}    // ( ... )*
		match(DOUBLE_COLON);
		AST tmp53_AST = null;
		tmp53_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp53_AST);
		match(NAME);
		match(COLON);
		AST tmp55_AST = null;
		tmp55_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp55_AST);
		match(NAME);
		fqnDCNameCName_AST = currentAST.root;
		returnAST = fqnDCNameCName_AST;
	}
	
	public void fqnDCName() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fqnDCName_AST = null;
		
		AST tmp56_AST = null;
		tmp56_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp56_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp58_AST = null;
					tmp58_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp58_AST);
					match(NAME);
				}
				else
				{
					goto _loop59_breakloop;
				}
				
			}
_loop59_breakloop:			;
		}    // ( ... )*
		match(DOUBLE_COLON);
		AST tmp60_AST = null;
		tmp60_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp60_AST);
		match(NAME);
		fqnDCName_AST = currentAST.root;
		returnAST = fqnDCName_AST;
	}
	
	public void fqn() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fqn_AST = null;
		
		AST tmp61_AST = null;
		tmp61_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp61_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp63_AST = null;
					tmp63_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp63_AST);
					match(NAME);
				}
				else
				{
					goto _loop62_breakloop;
				}
				
			}
_loop62_breakloop:			;
		}    // ( ... )*
		fqn_AST = currentAST.root;
		returnAST = fqn_AST;
	}
	
	public void nameCName() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nameCName_AST = null;
		
		AST tmp64_AST = null;
		tmp64_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp64_AST);
		match(NAME);
		match(COLON);
		AST tmp66_AST = null;
		tmp66_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp66_AST);
		match(NAME);
		nameCName_AST = currentAST.root;
		returnAST = nameCName_AST;
	}
	
	public void fqnDCNameCStar() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fqnDCNameCStar_AST = null;
		
		AST tmp67_AST = null;
		tmp67_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp67_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==DOT))
				{
					match(DOT);
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp69_AST);
					match(NAME);
				}
				else
				{
					goto _loop66_breakloop;
				}
				
			}
_loop66_breakloop:			;
		}    // ( ... )*
		match(DOUBLE_COLON);
		AST tmp71_AST = null;
		tmp71_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp71_AST);
		match(NAME);
		match(COLON);
		AST tmp73_AST = null;
		tmp73_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp73_AST);
		match(STAR);
		fqnDCNameCStar_AST = currentAST.root;
		returnAST = fqnDCNameCStar_AST;
	}
	
	public void nameCStar() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nameCStar_AST = null;
		
		AST tmp74_AST = null;
		tmp74_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp74_AST);
		match(NAME);
		match(COLON);
		AST tmp76_AST = null;
		tmp76_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp76_AST);
		match(STAR);
		nameCStar_AST = currentAST.root;
		returnAST = nameCStar_AST;
	}
	
	public void concernElemReference() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concernElemReference_AST = null;
		
		{
			bool synPredMatched74 = false;
			if (((LA(1)==NAME)))
			{
				int _m74 = mark();
				synPredMatched74 = true;
				inputState.guessing++;
				try {
					{
						{    // ( ... )*
							for (;;)
							{
								switch ( LA(1) )
								{
								case NAME:
								{
									match(NAME);
									break;
								}
								case DOT:
								{
									match(DOT);
									break;
								}
								default:
								{
									goto _loop73_breakloop;
								}
								 }
							}
_loop73_breakloop:							;
						}    // ( ... )*
						match(DOUBLE_COLON);
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
				fqnDCName();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else if ((LA(1)==NAME)) {
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp77_AST);
				match(NAME);
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		concernElemReference_AST = currentAST.root;
		returnAST = concernElemReference_AST;
	}
	
	public void fmElemReference() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fmElemReference_AST = null;
		
		{
			bool synPredMatched80 = false;
			if (((LA(1)==NAME)))
			{
				int _m80 = mark();
				synPredMatched80 = true;
				inputState.guessing++;
				try {
					{
						{    // ( ... )*
							for (;;)
							{
								switch ( LA(1) )
								{
								case NAME:
								{
									match(NAME);
									break;
								}
								case DOT:
								{
									match(DOT);
									break;
								}
								default:
								{
									goto _loop79_breakloop;
								}
								 }
							}
_loop79_breakloop:							;
						}    // ( ... )*
						match(DOUBLE_COLON);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched80 = false;
				}
				rewind(_m80);
				inputState.guessing--;
			}
			if ( synPredMatched80 )
			{
				fqnDCNameCName();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else {
				bool synPredMatched84 = false;
				if (((LA(1)==NAME)))
				{
					int _m84 = mark();
					synPredMatched84 = true;
					inputState.guessing++;
					try {
						{
							{    // ( ... )*
								for (;;)
								{
									switch ( LA(1) )
									{
									case NAME:
									{
										match(NAME);
										break;
									}
									case DOT:
									{
										match(DOT);
										break;
									}
									default:
									{
										goto _loop83_breakloop;
									}
									 }
								}
_loop83_breakloop:								;
							}    // ( ... )*
							match(COLON);
						}
					}
					catch (RecognitionException)
					{
						synPredMatched84 = false;
					}
					rewind(_m84);
					inputState.guessing--;
				}
				if ( synPredMatched84 )
				{
					nameCName();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else if ((LA(1)==NAME)) {
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp78_AST);
					match(NAME);
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			fmElemReference_AST = currentAST.root;
			returnAST = fmElemReference_AST;
		}
		
	public void fmElemReferenceCond() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fmElemReferenceCond_AST = null;
		
		{
			bool synPredMatched90 = false;
			if (((LA(1)==NAME)))
			{
				int _m90 = mark();
				synPredMatched90 = true;
				inputState.guessing++;
				try {
					{
						{    // ( ... )*
							for (;;)
							{
								switch ( LA(1) )
								{
								case NAME:
								{
									match(NAME);
									break;
								}
								case COLON:
								{
									match(COLON);
									break;
								}
								default:
								{
									goto _loop89_breakloop;
								}
								 }
							}
_loop89_breakloop:							;
						}    // ( ... )*
						match(DOUBLE_COLON);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched90 = false;
				}
				rewind(_m90);
				inputState.guessing--;
			}
			if ( synPredMatched90 )
			{
				fqnDCNameCName();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else {
				bool synPredMatched92 = false;
				if (((LA(1)==NAME)))
				{
					int _m92 = mark();
					synPredMatched92 = true;
					inputState.guessing++;
					try {
						{
							match(NAME);
							match(COLON);
						}
					}
					catch (RecognitionException)
					{
						synPredMatched92 = false;
					}
					rewind(_m92);
					inputState.guessing--;
				}
				if ( synPredMatched92 )
				{
					nameCName();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else if ((LA(1)==NAME)) {
					AST tmp79_AST = null;
					tmp79_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp79_AST);
					match(NAME);
				}
				else
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			fmElemReferenceCond_AST = currentAST.root;
			returnAST = fmElemReferenceCond_AST;
		}
		
	public void fmElemReferenceStar() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fmElemReferenceStar_AST = null;
		
		{
			bool synPredMatched98 = false;
			if (((LA(1)==NAME)))
			{
				int _m98 = mark();
				synPredMatched98 = true;
				inputState.guessing++;
				try {
					{
						{    // ( ... )*
							for (;;)
							{
								switch ( LA(1) )
								{
								case NAME:
								{
									match(NAME);
									break;
								}
								case DOT:
								{
									match(DOT);
									break;
								}
								default:
								{
									goto _loop97_breakloop;
								}
								 }
							}
_loop97_breakloop:							;
						}    // ( ... )*
						match(DOUBLE_COLON);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched98 = false;
				}
				rewind(_m98);
				inputState.guessing--;
			}
			if ( synPredMatched98 )
			{
				fqnDCNameCStar();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else if ((LA(1)==NAME)) {
				nameCStar();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		fmElemReferenceStar_AST = currentAST.root;
		returnAST = fmElemReferenceStar_AST;
	}
	
	public void singleCondition() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleCondition_AST = null;
		
		AST tmp80_AST = null;
		tmp80_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp80_AST);
		match(NAME);
		match(COLON);
		concernReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(LPARENTHESIS);
		match(RPARENTHESIS);
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleCondition_AST = (AST)currentAST.root;
			singleCondition_AST = (AST) astFactory.make(astFactory.create(CONDITION_,"condition"), singleCondition_AST);
			currentAST.root = singleCondition_AST;
			if ( (null != singleCondition_AST) && (null != singleCondition_AST.getFirstChild()) )
				currentAST.child = singleCondition_AST.getFirstChild();
			else
				currentAST.child = singleCondition_AST;
			currentAST.advanceChildToEnd();
		}
		singleCondition_AST = currentAST.root;
		returnAST = singleCondition_AST;
	}
	
	public void generalFilterSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST generalFilterSet_AST = null;
		
		singleInputFilter();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==SEMICOLON))
				{
					AST tmp85_AST = null;
					tmp85_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp85_AST);
					match(SEMICOLON);
					singleInputFilter();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop106_breakloop;
				}
				
			}
_loop106_breakloop:			;
		}    // ( ... )*
		generalFilterSet_AST = currentAST.root;
		returnAST = generalFilterSet_AST;
	}
	
	public void singleInputFilter() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleInputFilter_AST = null;
		
		AST tmp86_AST = null;
		tmp86_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp86_AST);
		match(NAME);
		match(COLON);
		type();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{
			switch ( LA(1) )
			{
			case LPARENTHESIS:
			{
				match(LPARENTHESIS);
				actualParameters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(RPARENTHESIS);
				break;
			}
			case EQUALS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(EQUALS);
		match(LCURLY);
		{
			switch ( LA(1) )
			{
			case NAME:
			case LCURLY:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			case NOT:
			case HASH:
			case LSQUARE:
			case LANGLE:
			case SINGLEQUOTE:
			{
				filterElements();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(RCURLY);
		if (0==inputState.guessing)
		{
			singleInputFilter_AST = (AST)currentAST.root;
			singleInputFilter_AST = (AST) astFactory.make(astFactory.create(IFILTER_,"inputfilter"), singleInputFilter_AST);
			currentAST.root = singleInputFilter_AST;
			if ( (null != singleInputFilter_AST) && (null != singleInputFilter_AST.getFirstChild()) )
				currentAST.child = singleInputFilter_AST.getFirstChild();
			else
				currentAST.child = singleInputFilter_AST;
			currentAST.advanceChildToEnd();
		}
		singleInputFilter_AST = currentAST.root;
		returnAST = singleInputFilter_AST;
	}
	
	public void actualParameters() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST actualParameters_AST = null;
		
		AST tmp93_AST = null;
		tmp93_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp93_AST);
		match(NAME);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==COMMA))
				{
					match(COMMA);
					AST tmp95_AST = null;
					tmp95_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp95_AST);
					match(NAME);
				}
				else
				{
					goto _loop112_breakloop;
				}
				
			}
_loop112_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			actualParameters_AST = (AST)currentAST.root;
			actualParameters_AST = (AST) astFactory.make(astFactory.create(APS_,"actual parameters"), actualParameters_AST);
			currentAST.root = actualParameters_AST;
			if ( (null != actualParameters_AST) && (null != actualParameters_AST.getFirstChild()) )
				currentAST.child = actualParameters_AST.getFirstChild();
			else
				currentAST.child = actualParameters_AST;
			currentAST.advanceChildToEnd();
		}
		actualParameters_AST = currentAST.root;
		returnAST = actualParameters_AST;
	}
	
	public void filterElements() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterElements_AST = null;
		
		filterElement();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==COMMA))
				{
					AST tmp96_AST = null;
					tmp96_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp96_AST);
					match(COMMA);
					filterElement();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop115_breakloop;
				}
				
			}
_loop115_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			filterElements_AST = (AST)currentAST.root;
			filterElements_AST = (AST) astFactory.make(astFactory.create(FILTERSET_,"filterelements"), filterElements_AST);
			currentAST.root = filterElements_AST;
			if ( (null != filterElements_AST) && (null != filterElements_AST.getFirstChild()) )
				currentAST.child = filterElements_AST.getFirstChild();
			else
				currentAST.child = filterElements_AST;
			currentAST.advanceChildToEnd();
		}
		filterElements_AST = currentAST.root;
		returnAST = filterElements_AST;
	}
	
	public void filterElement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterElement_AST = null;
		
		{
			bool synPredMatched122 = false;
			if (((LA(1)==NAME||LA(1)==NOT)))
			{
				int _m122 = mark();
				synPredMatched122 = true;
				inputState.guessing++;
				try {
					{
						{    // ( ... )*
							for (;;)
							{
								if ((tokenSet_0_.member(LA(1))))
								{
									{
										match(tokenSet_0_);
									}
								}
								else
								{
									goto _loop121_breakloop;
								}
								
							}
_loop121_breakloop:							;
						}    // ( ... )*
						match(FILTER_OP);
					}
				}
				catch (RecognitionException)
				{
					synPredMatched122 = false;
				}
				rewind(_m122);
				inputState.guessing--;
			}
			if ( synPredMatched122 )
			{
				orExpr();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				AST tmp97_AST = null;
				tmp97_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp97_AST);
				match(FILTER_OP);
				messagePatternSet();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else if ((tokenSet_1_.member(LA(1)))) {
				messagePatternSet();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
			}
			else
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		if (0==inputState.guessing)
		{
			filterElement_AST = (AST)currentAST.root;
			filterElement_AST = (AST) astFactory.make(astFactory.create(FILTERELEM_,"filterelement"), filterElement_AST);
			currentAST.root = filterElement_AST;
			if ( (null != filterElement_AST) && (null != filterElement_AST.getFirstChild()) )
				currentAST.child = filterElement_AST.getFirstChild();
			else
				currentAST.child = filterElement_AST;
			currentAST.advanceChildToEnd();
		}
		filterElement_AST = currentAST.root;
		returnAST = filterElement_AST;
	}
	
	public void orExpr() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpr_AST = null;
		
		andExpr();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==OR))
				{
					AST tmp98_AST = null;
					tmp98_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp98_AST);
					match(OR);
					andExpr();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop125_breakloop;
				}
				
			}
_loop125_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			orExpr_AST = (AST)currentAST.root;
			orExpr_AST = (AST) astFactory.make(astFactory.create(OREXPR_,"orExpression"), orExpr_AST);
			currentAST.root = orExpr_AST;
			if ( (null != orExpr_AST) && (null != orExpr_AST.getFirstChild()) )
				currentAST.child = orExpr_AST.getFirstChild();
			else
				currentAST.child = orExpr_AST;
			currentAST.advanceChildToEnd();
		}
		orExpr_AST = currentAST.root;
		returnAST = orExpr_AST;
	}
	
	public void messagePatternSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST messagePatternSet_AST = null;
		
		{
			switch ( LA(1) )
			{
			case LCURLY:
			{
				match(LCURLY);
				messagePattern();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==COMMA))
						{
							AST tmp100_AST = null;
							tmp100_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp100_AST);
							match(COMMA);
							messagePattern();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop135_breakloop;
						}
						
					}
_loop135_breakloop:					;
				}    // ( ... )*
				match(RCURLY);
				break;
			}
			case NAME:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			case HASH:
			case LSQUARE:
			case LANGLE:
			case SINGLEQUOTE:
			{
				messagePattern();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
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
			messagePatternSet_AST = (AST)currentAST.root;
			messagePatternSet_AST = (AST) astFactory.make(astFactory.create(MPSET_,"messagePatternSet"), messagePatternSet_AST);
			currentAST.root = messagePatternSet_AST;
			if ( (null != messagePatternSet_AST) && (null != messagePatternSet_AST.getFirstChild()) )
				currentAST.child = messagePatternSet_AST.getFirstChild();
			else
				currentAST.child = messagePatternSet_AST;
			currentAST.advanceChildToEnd();
		}
		messagePatternSet_AST = currentAST.root;
		returnAST = messagePatternSet_AST;
	}
	
	public void andExpr() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpr_AST = null;
		
		notExpr();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==AND))
				{
					AST tmp102_AST = null;
					tmp102_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp102_AST);
					match(AND);
					notExpr();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop128_breakloop;
				}
				
			}
_loop128_breakloop:			;
		}    // ( ... )*
		if (0==inputState.guessing)
		{
			andExpr_AST = (AST)currentAST.root;
			andExpr_AST = (AST) astFactory.make(astFactory.create(ANDEXPR_,"andExpression"), andExpr_AST);
			currentAST.root = andExpr_AST;
			if ( (null != andExpr_AST) && (null != andExpr_AST.getFirstChild()) )
				currentAST.child = andExpr_AST.getFirstChild();
			else
				currentAST.child = andExpr_AST;
			currentAST.advanceChildToEnd();
		}
		andExpr_AST = currentAST.root;
		returnAST = andExpr_AST;
	}
	
	public void notExpr() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST notExpr_AST = null;
		
		{
			switch ( LA(1) )
			{
			case NOT:
			{
				AST tmp103_AST = null;
				tmp103_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp103_AST);
				match(NOT);
				break;
			}
			case NAME:
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
			AST tmp104_AST = null;
			tmp104_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp104_AST);
			match(NAME);
		}
		if (0==inputState.guessing)
		{
			notExpr_AST = (AST)currentAST.root;
			notExpr_AST = (AST) astFactory.make(astFactory.create(NOTEXPR_,"notExpression"), notExpr_AST);
			currentAST.root = notExpr_AST;
			if ( (null != notExpr_AST) && (null != notExpr_AST.getFirstChild()) )
				currentAST.child = notExpr_AST.getFirstChild();
			else
				currentAST.child = notExpr_AST;
			currentAST.advanceChildToEnd();
		}
		notExpr_AST = currentAST.root;
		returnAST = notExpr_AST;
	}
	
	public void messagePattern() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST messagePattern_AST = null;
		
		{
			matchingPart();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{
				switch ( LA(1) )
				{
				case NAME:
				case PARAMETER_NAME:
				case PARAMETERLIST_NAME:
				case STAR:
				case HASH:
				{
					substitutionPart();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case COMMA:
				case RCURLY:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
		}
		if (0==inputState.guessing)
		{
			messagePattern_AST = (AST)currentAST.root;
			messagePattern_AST = (AST) astFactory.make(astFactory.create(MP_,"messagePattern"), messagePattern_AST);
			currentAST.root = messagePattern_AST;
			if ( (null != messagePattern_AST) && (null != messagePattern_AST.getFirstChild()) )
				currentAST.child = messagePattern_AST.getFirstChild();
			else
				currentAST.child = messagePattern_AST;
			currentAST.advanceChildToEnd();
		}
		messagePattern_AST = currentAST.root;
		returnAST = messagePattern_AST;
	}
	
	public void matchingPart() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST matchingPart_AST = null;
		
		{
			switch ( LA(1) )
			{
			case HASH:
			{
				match(HASH);
				match(LPARENTHESIS);
				singleTargetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==SEMICOLON))
						{
							AST tmp107_AST = null;
							tmp107_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp107_AST);
							match(SEMICOLON);
							singleTargetSelector();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop142_breakloop;
						}
						
					}
_loop142_breakloop:					;
				}    // ( ... )*
				match(RPARENTHESIS);
				break;
			}
			case NAME:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			case LSQUARE:
			case LANGLE:
			case SINGLEQUOTE:
			{
				singleTargetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
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
			matchingPart_AST = (AST)currentAST.root;
			matchingPart_AST = (AST) astFactory.make(astFactory.create(MPART_,"matchingPart"), matchingPart_AST);
			currentAST.root = matchingPart_AST;
			if ( (null != matchingPart_AST) && (null != matchingPart_AST.getFirstChild()) )
				currentAST.child = matchingPart_AST.getFirstChild();
			else
				currentAST.child = matchingPart_AST;
			currentAST.advanceChildToEnd();
		}
		matchingPart_AST = currentAST.root;
		returnAST = matchingPart_AST;
	}
	
	public void substitutionPart() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST substitutionPart_AST = null;
		
		{
			switch ( LA(1) )
			{
			case HASH:
			{
				match(HASH);
				match(LPARENTHESIS);
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				{    // ( ... )*
					for (;;)
					{
						if ((LA(1)==SEMICOLON))
						{
							AST tmp111_AST = null;
							tmp111_AST = astFactory.create(LT(1));
							astFactory.addASTChild(ref currentAST, tmp111_AST);
							match(SEMICOLON);
							targetSelector();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop146_breakloop;
						}
						
					}
_loop146_breakloop:					;
				}    // ( ... )*
				match(RPARENTHESIS);
				break;
			}
			case NAME:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			{
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
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
			substitutionPart_AST = (AST)currentAST.root;
			substitutionPart_AST = (AST) astFactory.make(astFactory.create(SPART_,"substitutionPart"), substitutionPart_AST);
			currentAST.root = substitutionPart_AST;
			if ( (null != substitutionPart_AST) && (null != substitutionPart_AST.getFirstChild()) )
				currentAST.child = substitutionPart_AST.getFirstChild();
			else
				currentAST.child = substitutionPart_AST;
			currentAST.advanceChildToEnd();
		}
		substitutionPart_AST = currentAST.root;
		returnAST = substitutionPart_AST;
	}
	
	public void singleTargetSelector() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleTargetSelector_AST = null;
		
		{
			switch ( LA(1) )
			{
			case LSQUARE:
			{
				AST tmp113_AST = null;
				tmp113_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp113_AST);
				match(LSQUARE);
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(RSQUARE);
				break;
			}
			case LANGLE:
			{
				AST tmp115_AST = null;
				tmp115_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp115_AST);
				match(LANGLE);
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(RANGLE);
				break;
			}
			case SINGLEQUOTE:
			{
				AST tmp117_AST = null;
				tmp117_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp117_AST);
				match(SINGLEQUOTE);
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(SINGLEQUOTE);
				break;
			}
			case NAME:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			{
				targetSelector();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		singleTargetSelector_AST = currentAST.root;
		returnAST = singleTargetSelector_AST;
	}
	
	public void targetSelector() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST targetSelector_AST = null;
		
		bool synPredMatched151 = false;
		if (((LA(1)==NAME||LA(1)==STAR)))
		{
			int _m151 = mark();
			synPredMatched151 = true;
			inputState.guessing++;
			try {
				{
					target();
					match(DOT);
				}
			}
			catch (RecognitionException)
			{
				synPredMatched151 = false;
			}
			rewind(_m151);
			inputState.guessing--;
		}
		if ( synPredMatched151 )
		{
			target();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			match(DOT);
			{
				switch ( LA(1) )
				{
				case NAME:
				case STAR:
				{
					selector();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case PARAMETER_NAME:
				{
					parameter();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case PARAMETERLIST_NAME:
				{
					parameterlist();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			targetSelector_AST = currentAST.root;
		}
		else if ((tokenSet_2_.member(LA(1)))) {
			{
				switch ( LA(1) )
				{
				case NAME:
				case STAR:
				{
					selector();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case PARAMETER_NAME:
				{
					parameter();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				case PARAMETERLIST_NAME:
				{
					parameterlist();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				 }
			}
			targetSelector_AST = currentAST.root;
		}
		else
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = targetSelector_AST;
	}
	
	public void target() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST target_AST = null;
		
		{
			switch ( LA(1) )
			{
			case NAME:
			{
				AST tmp120_AST = null;
				tmp120_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp120_AST);
				match(NAME);
				break;
			}
			case STAR:
			{
				AST tmp121_AST = null;
				tmp121_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp121_AST);
				match(STAR);
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
			target_AST = (AST)currentAST.root;
			target_AST = (AST) astFactory.make(astFactory.create(TARGET_,"target"), target_AST);
			currentAST.root = target_AST;
			if ( (null != target_AST) && (null != target_AST.getFirstChild()) )
				currentAST.child = target_AST.getFirstChild();
			else
				currentAST.child = target_AST;
			currentAST.advanceChildToEnd();
		}
		target_AST = currentAST.root;
		returnAST = target_AST;
	}
	
	public void selector() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selector_AST = null;
		
		{
			switch ( LA(1) )
			{
			case NAME:
			{
				AST tmp122_AST = null;
				tmp122_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp122_AST);
				match(NAME);
				{
					switch ( LA(1) )
					{
					case LPARENTHESIS:
					{
						match(LPARENTHESIS);
						{
							switch ( LA(1) )
							{
							case NAME:
							{
								type();
								if (0 == inputState.guessing)
								{
									astFactory.addASTChild(ref currentAST, returnAST);
								}
								{    // ( ... )*
									for (;;)
									{
										if ((LA(1)==SEMICOLON))
										{
											match(SEMICOLON);
											type();
											if (0 == inputState.guessing)
											{
												astFactory.addASTChild(ref currentAST, returnAST);
											}
										}
										else
										{
											goto _loop161_breakloop;
										}
										
									}
_loop161_breakloop:									;
								}    // ( ... )*
								break;
							}
							case RPARENTHESIS:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							 }
						}
						match(RPARENTHESIS);
						break;
					}
					case NAME:
					case RPARENTHESIS:
					case SEMICOLON:
					case COMMA:
					case RCURLY:
					case PARAMETER_NAME:
					case PARAMETERLIST_NAME:
					case STAR:
					case HASH:
					case RSQUARE:
					case RANGLE:
					case SINGLEQUOTE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					 }
				}
				break;
			}
			case STAR:
			{
				AST tmp126_AST = null;
				tmp126_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp126_AST);
				match(STAR);
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
			selector_AST = (AST)currentAST.root;
			selector_AST = (AST) astFactory.make(astFactory.create(SELEC_,"selector"), selector_AST);
			currentAST.root = selector_AST;
			if ( (null != selector_AST) && (null != selector_AST.getFirstChild()) )
				currentAST.child = selector_AST.getFirstChild();
			else
				currentAST.child = selector_AST;
			currentAST.advanceChildToEnd();
		}
		selector_AST = currentAST.root;
		returnAST = selector_AST;
	}
	
	public void generalFilterSet2() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST generalFilterSet2_AST = null;
		
		singleOutputFilter();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==SEMICOLON))
				{
					AST tmp127_AST = null;
					tmp127_AST = astFactory.create(LT(1));
					astFactory.addASTChild(ref currentAST, tmp127_AST);
					match(SEMICOLON);
					singleOutputFilter();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop165_breakloop;
				}
				
			}
_loop165_breakloop:			;
		}    // ( ... )*
		generalFilterSet2_AST = currentAST.root;
		returnAST = generalFilterSet2_AST;
	}
	
	public void singleOutputFilter() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleOutputFilter_AST = null;
		
		AST tmp128_AST = null;
		tmp128_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp128_AST);
		match(NAME);
		match(COLON);
		type();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{
			switch ( LA(1) )
			{
			case LPARENTHESIS:
			{
				match(LPARENTHESIS);
				actualParameters();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				match(RPARENTHESIS);
				break;
			}
			case EQUALS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(EQUALS);
		match(LCURLY);
		{
			switch ( LA(1) )
			{
			case NAME:
			case LCURLY:
			case PARAMETER_NAME:
			case PARAMETERLIST_NAME:
			case STAR:
			case NOT:
			case HASH:
			case LSQUARE:
			case LANGLE:
			case SINGLEQUOTE:
			{
				filterElements();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(RCURLY);
		if (0==inputState.guessing)
		{
			singleOutputFilter_AST = (AST)currentAST.root;
			singleOutputFilter_AST = (AST) astFactory.make(astFactory.create(OFILTER_,"outputfilter"), singleOutputFilter_AST);
			currentAST.root = singleOutputFilter_AST;
			if ( (null != singleOutputFilter_AST) && (null != singleOutputFilter_AST.getFirstChild()) )
				currentAST.child = singleOutputFilter_AST.getFirstChild();
			else
				currentAST.child = singleOutputFilter_AST;
			currentAST.advanceChildToEnd();
		}
		singleOutputFilter_AST = currentAST.root;
		returnAST = singleOutputFilter_AST;
	}
	
	public void superImpositionBlock() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST superImpositionBlock_AST = null;
		
		match(LCURLY);
		superImpositionInner();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(RCURLY);
		superImpositionBlock_AST = currentAST.root;
		returnAST = superImpositionBlock_AST;
	}
	
	public void superImpositionInner() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST superImpositionInner_AST = null;
		
		{
			switch ( LA(1) )
			{
			case LITERAL_selectors:
			{
				selectorDef();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_filtermodules:
			case LITERAL_annotations:
			case LITERAL_constraints:
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
			case LITERAL_filtermodules:
			{
				filtermoduleBind();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_annotations:
			case LITERAL_constraints:
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
			case LITERAL_annotations:
			{
				annotationBind();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			case LITERAL_constraints:
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
			case LITERAL_constraints:
			{
				constraints();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		superImpositionInner_AST = currentAST.root;
		returnAST = superImpositionInner_AST;
	}
	
	public void selectorDef() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectorDef_AST = null;
		
		AST tmp137_AST = null;
		tmp137_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp137_AST);
		match(LITERAL_selectors);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleSelector();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop178_breakloop;
				}
				
			}
_loop178_breakloop:			;
		}    // ( ... )*
		selectorDef_AST = currentAST.root;
		returnAST = selectorDef_AST;
	}
	
	public void filtermoduleBind() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filtermoduleBind_AST = null;
		
		AST tmp138_AST = null;
		tmp138_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp138_AST);
		match(LITERAL_filtermodules);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleFmBind();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
				}
				else
				{
					goto _loop193_breakloop;
				}
				
			}
_loop193_breakloop:			;
		}    // ( ... )*
		filtermoduleBind_AST = currentAST.root;
		returnAST = filtermoduleBind_AST;
	}
	
	public void annotationBind() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotationBind_AST = null;
		
		AST tmp139_AST = null;
		tmp139_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp139_AST);
		match(LITERAL_annotations);
		{    // ( ... )*
			for (;;)
			{
				if ((LA(1)==NAME))
				{
					singleAnnotBind();
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
_loop212_breakloop:			;
		}    // ( ... )*
		annotationBind_AST = currentAST.root;
		returnAST = annotationBind_AST;
	}
	
	public void constraints() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constraints_AST = null;
		
		AST tmp140_AST = null;
		tmp140_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp140_AST);
		match(LITERAL_constraints);
		{    // ( ... )*
			for (;;)
			{
				if (((LA(1) >= LITERAL_presoft && LA(1) <= LITERAL_prehard)))
				{
					constraint();
					if (0 == inputState.guessing)
					{
						astFactory.addASTChild(ref currentAST, returnAST);
					}
					match(SEMICOLON);
				}
				else
				{
					goto _loop222_breakloop;
				}
				
			}
_loop222_breakloop:			;
		}    // ( ... )*
		constraints_AST = currentAST.root;
		returnAST = constraints_AST;
	}
	
	public void singleSelector() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleSelector_AST = null;
		
		AST tmp142_AST = null;
		tmp142_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp142_AST);
		match(NAME);
		match(EQUALS);
		match(LCURLY);
		{
			switch ( LA(1) )
			{
			case STAR:
			{
				{
					oldSelExpression();
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
								oldSelExpression();
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
_loop183_breakloop:						;
					}    // ( ... )*
					match(RCURLY);
				}
				break;
			}
			case NAME:
			{
				predSelExpression();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleSelector_AST = (AST)currentAST.root;
			singleSelector_AST = (AST) astFactory.make(astFactory.create(SELEC2_,"selector"), singleSelector_AST);
			currentAST.root = singleSelector_AST;
			if ( (null != singleSelector_AST) && (null != singleSelector_AST.getFirstChild()) )
				currentAST.child = singleSelector_AST.getFirstChild();
			else
				currentAST.child = singleSelector_AST;
			currentAST.advanceChildToEnd();
		}
		singleSelector_AST = currentAST.root;
		returnAST = singleSelector_AST;
	}
	
	public void oldSelExpression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST oldSelExpression_AST = null;
		
		match(STAR);
		{
			switch ( LA(1) )
			{
			case EQUALS:
			{
				AST tmp149_AST = null;
				tmp149_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp149_AST);
				match(EQUALS);
				break;
			}
			case COLON:
			{
				AST tmp150_AST = null;
				tmp150_AST = astFactory.create(LT(1));
				astFactory.addASTChild(ref currentAST, tmp150_AST);
				match(COLON);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		concernReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		if (0==inputState.guessing)
		{
			oldSelExpression_AST = (AST)currentAST.root;
			oldSelExpression_AST = (AST) astFactory.make(astFactory.create(SELEXP_,"selectorexpression"), oldSelExpression_AST);
			currentAST.root = oldSelExpression_AST;
			if ( (null != oldSelExpression_AST) && (null != oldSelExpression_AST.getFirstChild()) )
				currentAST.child = oldSelExpression_AST.getFirstChild();
			else
				currentAST.child = oldSelExpression_AST;
			currentAST.advanceChildToEnd();
		}
		oldSelExpression_AST = currentAST.root;
		returnAST = oldSelExpression_AST;
	}
	
	public void predSelExpression() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST predSelExpression_AST = null;
		
		AST tmp151_AST = null;
		tmp151_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp151_AST);
		match(NAME);
		AST tmp152_AST = null;
		tmp152_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp152_AST);
		match(PROLOG_EXPRESSION);
		predSelExpression_AST = currentAST.root;
		returnAST = predSelExpression_AST;
	}
	
	public void commonBindingPart() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonBindingPart_AST = null;
		
		selectorRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		weaveOperation();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		commonBindingPart_AST = currentAST.root;
		returnAST = commonBindingPart_AST;
	}
	
	public void selectorRef() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectorRef_AST = null;
		
		concernElemReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		selectorRef_AST = currentAST.root;
		returnAST = selectorRef_AST;
	}
	
	public void weaveOperation() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST weaveOperation_AST = null;
		
		match(ARROW_LEFT);
		weaveOperation_AST = currentAST.root;
		returnAST = weaveOperation_AST;
	}
	
	public void conditionRef() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionRef_AST = null;
		
		fmElemReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		conditionRef_AST = currentAST.root;
		returnAST = conditionRef_AST;
	}
	
	public void singleFmBind() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleFmBind_AST = null;
		
		commonBindingPart();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		filterModuleSet();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleFmBind_AST = (AST)currentAST.root;
			singleFmBind_AST = (AST) astFactory.make(astFactory.create(FM_,"filtermodule"), singleFmBind_AST);
			currentAST.root = singleFmBind_AST;
			if ( (null != singleFmBind_AST) && (null != singleFmBind_AST.getFirstChild()) )
				currentAST.child = singleFmBind_AST.getFirstChild();
			else
				currentAST.child = singleFmBind_AST;
			currentAST.advanceChildToEnd();
		}
		singleFmBind_AST = currentAST.root;
		returnAST = singleFmBind_AST;
	}
	
	public void filterModuleSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModuleSet_AST = null;
		
		{
			switch ( LA(1) )
			{
			case LCURLY:
			{
				match(LCURLY);
				filterModuleElement();
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
							filterModuleElement();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop198_breakloop;
						}
						
					}
_loop198_breakloop:					;
				}    // ( ... )*
				match(RCURLY);
				break;
			}
			case NAME:
			{
				filterModuleElement();
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
							filterModuleElement();
							if (0 == inputState.guessing)
							{
								astFactory.addASTChild(ref currentAST, returnAST);
							}
						}
						else
						{
							goto _loop200_breakloop;
						}
						
					}
_loop200_breakloop:					;
				}    // ( ... )*
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
			filterModuleSet_AST = (AST)currentAST.root;
			filterModuleSet_AST = (AST) astFactory.make(astFactory.create(FMSET_,"filtermodule set"), filterModuleSet_AST);
			currentAST.root = filterModuleSet_AST;
			if ( (null != filterModuleSet_AST) && (null != filterModuleSet_AST.getFirstChild()) )
				currentAST.child = filterModuleSet_AST.getFirstChild();
			else
				currentAST.child = filterModuleSet_AST;
			currentAST.advanceChildToEnd();
		}
		filterModuleSet_AST = currentAST.root;
		returnAST = filterModuleSet_AST;
	}
	
	public void filterModuleElement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModuleElement_AST = null;
		
		concernElemReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		{
			switch ( LA(1) )
			{
			case LPARENTHESIS:
			{
				fmBindingArguments();
				if (0 == inputState.guessing)
				{
					astFactory.addASTChild(ref currentAST, returnAST);
				}
				break;
			}
			case SEMICOLON:
			case COMMA:
			case RCURLY:
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
			filterModuleElement_AST = (AST)currentAST.root;
			filterModuleElement_AST = (AST) astFactory.make(astFactory.create(FMELEM_,"filtermodule element"), filterModuleElement_AST);
			currentAST.root = filterModuleElement_AST;
			if ( (null != filterModuleElement_AST) && (null != filterModuleElement_AST.getFirstChild()) )
				currentAST.child = filterModuleElement_AST.getFirstChild();
			else
				currentAST.child = filterModuleElement_AST;
			currentAST.advanceChildToEnd();
		}
		filterModuleElement_AST = currentAST.root;
		returnAST = filterModuleElement_AST;
	}
	
	public void fmBindingArguments() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fmBindingArguments_AST = null;
		
		match(LPARENTHESIS);
		{
			switch ( LA(1) )
			{
			case NAME:
			case LCURLY:
			{
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
							goto _loop206_breakloop;
						}
						
					}
_loop206_breakloop:					;
				}    // ( ... )*
				break;
			}
			case RPARENTHESIS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			 }
		}
		match(RPARENTHESIS);
		if (0==inputState.guessing)
		{
			fmBindingArguments_AST = (AST)currentAST.root;
			fmBindingArguments_AST = (AST) astFactory.make(astFactory.create(DECLAREDARGUMENT_,"declaredargument"), fmBindingArguments_AST);
			currentAST.root = fmBindingArguments_AST;
			if ( (null != fmBindingArguments_AST) && (null != fmBindingArguments_AST.getFirstChild()) )
				currentAST.child = fmBindingArguments_AST.getFirstChild();
			else
				currentAST.child = fmBindingArguments_AST;
			currentAST.advanceChildToEnd();
		}
		fmBindingArguments_AST = currentAST.root;
		returnAST = fmBindingArguments_AST;
	}
	
	public void argument() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argument_AST = null;
		
		switch ( LA(1) )
		{
		case NAME:
		{
			fqn();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			if (0==inputState.guessing)
			{
				argument_AST = (AST)currentAST.root;
				argument_AST = (AST) astFactory.make(astFactory.create(ARGUMENT_,"argument"), argument_AST);
				currentAST.root = argument_AST;
				if ( (null != argument_AST) && (null != argument_AST.getFirstChild()) )
					currentAST.child = argument_AST.getFirstChild();
				else
					currentAST.child = argument_AST;
				currentAST.advanceChildToEnd();
			}
			argument_AST = currentAST.root;
			break;
		}
		case LCURLY:
		{
			match(LCURLY);
			fqn();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			{    // ( ... )*
				for (;;)
				{
					if ((LA(1)==COMMA))
					{
						AST tmp163_AST = null;
						tmp163_AST = astFactory.create(LT(1));
						astFactory.addASTChild(ref currentAST, tmp163_AST);
						match(COMMA);
						fqn();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop209_breakloop;
					}
					
				}
_loop209_breakloop:				;
			}    // ( ... )*
			match(RCURLY);
			if (0==inputState.guessing)
			{
				argument_AST = (AST)currentAST.root;
				argument_AST = (AST) astFactory.make(astFactory.create(ARGUMENT_,"argument"), argument_AST);
				currentAST.root = argument_AST;
				if ( (null != argument_AST) && (null != argument_AST.getFirstChild()) )
					currentAST.child = argument_AST.getFirstChild();
				else
					currentAST.child = argument_AST;
				currentAST.advanceChildToEnd();
			}
			argument_AST = currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		 }
		returnAST = argument_AST;
	}
	
	public void singleAnnotBind() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleAnnotBind_AST = null;
		
		selectorRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(ARROW_LEFT);
		annotationSet();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(SEMICOLON);
		if (0==inputState.guessing)
		{
			singleAnnotBind_AST = (AST)currentAST.root;
			singleAnnotBind_AST = (AST) astFactory.make(astFactory.create(ANNOT_,"annotation"), singleAnnotBind_AST);
			currentAST.root = singleAnnotBind_AST;
			if ( (null != singleAnnotBind_AST) && (null != singleAnnotBind_AST.getFirstChild()) )
				currentAST.child = singleAnnotBind_AST.getFirstChild();
			else
				currentAST.child = singleAnnotBind_AST;
			currentAST.advanceChildToEnd();
		}
		singleAnnotBind_AST = currentAST.root;
		returnAST = singleAnnotBind_AST;
	}
	
	public void annotationSet() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotationSet_AST = null;
		
		switch ( LA(1) )
		{
		case LCURLY:
		{
			match(LCURLY);
			annotationElement();
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
						annotationElement();
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
			match(RCURLY);
			annotationSet_AST = currentAST.root;
			break;
		}
		case NAME:
		{
			annotationElement();
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
						annotationElement();
						if (0 == inputState.guessing)
						{
							astFactory.addASTChild(ref currentAST, returnAST);
						}
					}
					else
					{
						goto _loop218_breakloop;
					}
					
				}
_loop218_breakloop:				;
			}    // ( ... )*
			if (0==inputState.guessing)
			{
				annotationSet_AST = (AST)currentAST.root;
				annotationSet_AST = (AST) astFactory.make(astFactory.create(ANNOTSET_,"annotation_set"), annotationSet_AST);
				currentAST.root = annotationSet_AST;
				if ( (null != annotationSet_AST) && (null != annotationSet_AST.getFirstChild()) )
					currentAST.child = annotationSet_AST.getFirstChild();
				else
					currentAST.child = annotationSet_AST;
				currentAST.advanceChildToEnd();
			}
			annotationSet_AST = currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		 }
		returnAST = annotationSet_AST;
	}
	
	public void annotationElement() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotationElement_AST = null;
		
		concernReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		if (0==inputState.guessing)
		{
			annotationElement_AST = (AST)currentAST.root;
			annotationElement_AST = (AST) astFactory.make(astFactory.create(ANNOTELEM_,"annotation element"), annotationElement_AST);
			currentAST.root = annotationElement_AST;
			if ( (null != annotationElement_AST) && (null != annotationElement_AST.getFirstChild()) )
				currentAST.child = annotationElement_AST.getFirstChild();
			else
				currentAST.child = annotationElement_AST;
			currentAST.advanceChildToEnd();
		}
		annotationElement_AST = currentAST.root;
		returnAST = annotationElement_AST;
	}
	
	public void constraint() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constraint_AST = null;
		
		switch ( LA(1) )
		{
		case LITERAL_presoft:
		{
			preSoftConstraint();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			constraint_AST = currentAST.root;
			break;
		}
		case LITERAL_pre:
		{
			preConstraint();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			constraint_AST = currentAST.root;
			break;
		}
		case LITERAL_prehard:
		{
			preHardConstraint();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			constraint_AST = currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		 }
		returnAST = constraint_AST;
	}
	
	public void preSoftConstraint() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST preSoftConstraint_AST = null;
		
		AST tmp171_AST = null;
		tmp171_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp171_AST);
		match(LITERAL_presoft);
		match(LPARENTHESIS);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		AST tmp173_AST = null;
		tmp173_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp173_AST);
		match(COMMA);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(RPARENTHESIS);
		preSoftConstraint_AST = currentAST.root;
		returnAST = preSoftConstraint_AST;
	}
	
	public void preConstraint() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST preConstraint_AST = null;
		
		AST tmp175_AST = null;
		tmp175_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp175_AST);
		match(LITERAL_pre);
		match(LPARENTHESIS);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		AST tmp177_AST = null;
		tmp177_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp177_AST);
		match(COMMA);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(RPARENTHESIS);
		preConstraint_AST = currentAST.root;
		returnAST = preConstraint_AST;
	}
	
	public void preHardConstraint() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST preHardConstraint_AST = null;
		
		AST tmp179_AST = null;
		tmp179_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(ref currentAST, tmp179_AST);
		match(LITERAL_prehard);
		match(LPARENTHESIS);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		AST tmp181_AST = null;
		tmp181_AST = astFactory.create(LT(1));
		astFactory.addASTChild(ref currentAST, tmp181_AST);
		match(COMMA);
		filterModuleRef();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		match(RPARENTHESIS);
		preHardConstraint_AST = currentAST.root;
		returnAST = preHardConstraint_AST;
	}
	
	public void filterModuleRef() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterModuleRef_AST = null;
		
		concernElemReference();
		if (0 == inputState.guessing)
		{
			astFactory.addASTChild(ref currentAST, returnAST);
		}
		filterModuleRef_AST = currentAST.root;
		returnAST = filterModuleRef_AST;
	}
	
	public void implementationInner() //throws RecognitionException, TokenStreamException
{
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implementationInner_AST = null;
		IToken  l2 = null;
		AST l2_AST = null;
		IToken  f2 = null;
		AST f2_AST = null;
		IToken  lcurly = null;
		AST lcurly_AST = null;
		
		switch ( LA(1) )
		{
		case LITERAL_in:
		{
			match(LITERAL_in);
			l2 = LT(1);
			l2_AST = astFactory.create(l2);
			astFactory.addASTChild(ref currentAST, l2_AST);
			match(NAME);
			match(LITERAL_by);
			fqn();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			match(LITERAL_as);
			f2 = LT(1);
			f2_AST = astFactory.create(f2);
			astFactory.addASTChild(ref currentAST, f2_AST);
			match(FILENAME);
			lcurly = LT(1);
			lcurly_AST = astFactory.create(lcurly);
			match(LCURLY);
			if (0==inputState.guessing)
			{
				sourceLang=l2.getText(); sourceFile=f2.getText(); sourceIncluded=true;
			}
			implementationInner_AST = currentAST.root;
			break;
		}
		case LITERAL_by:
		{
			match(LITERAL_by);
			fqn();
			if (0 == inputState.guessing)
			{
				astFactory.addASTChild(ref currentAST, returnAST);
			}
			AST tmp187_AST = null;
			tmp187_AST = astFactory.create(LT(1));
			astFactory.addASTChild(ref currentAST, tmp187_AST);
			match(SEMICOLON);
			implementationInner_AST = currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		 }
		returnAST = implementationInner_AST;
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
		factory.setMaxNodeType(107);
	}
	
	public static readonly string[] tokenNames_ = new string[] {
		@"""<0>""",
		@"""EOF""",
		@"""<2>""",
		@"""NULL_TREE_LOOKAHEAD""",
		@"""ANDEXPR_""",
		@"""ANNOTELEM_""",
		@"""ANNOTSET_""",
		@"""ANNOT_""",
		@"""ARGUMENT_""",
		@"""DECLAREDARGUMENT_""",
		@"""DECLAREDPARAMETER_""",
		@"""FPMSET_""",
		@"""FPMDEF_""",
		@"""FPMSTYPE_""",
		@"""FPMDEFTYPE_""",
		@"""CONDITION_""",
		@"""CONDNAMESET_""",
		@"""CONDNAME_""",
		@"""EXTERNAL_""",
		@"""FILTERELEM_""",
		@"""FILTERMODULEPARAMETERS_""",
		@"""FILTERSET_""",
		@"""FMELEM_""",
		@"""FMSET_""",
		@"""FM_""",
		@"""IFILTER_""",
		@"""INTERNAL_""",
		@"""METHOD2_""",
		@"""METHODNAMESET_""",
		@"""METHODNAME_""",
		@"""METHOD_""",
		@"""NOTEXPR_""",
		@"""MPSET_""",
		@"""MP_""",
		@"""MPART_""",
		@"""OCL_""",
		@"""OFILTER_""",
		@"""OREXPR_""",
		@"""PARAMETER_""",
		@"""PARAMETERLIST_""",
		@"""SELEC2_""",
		@"""SELEC_""",
		@"""SELEXP_""",
		@"""SOURCE_""",
		@"""SPART_""",
		@"""TSSET_""",
		@"""TARGET_""",
		@"""TYPELIST_""",
		@"""TYPE_""",
		@"""VAR_""",
		@"""APS_""",
		@"""concern""",
		@"""NAME""",
		@"""LPARENTHESIS""",
		@"""RPARENTHESIS""",
		@"""in""",
		@"""SEMICOLON""",
		@"""COMMA""",
		@"""COLON""",
		@"""DOT""",
		@"""LCURLY""",
		@"""RCURLY""",
		@"""filtermodule""",
		@"""PARAMETER_NAME""",
		@"""PARAMETERLIST_NAME""",
		@"""internals""",
		@"""externals""",
		@"""EQUALS""",
		@"""DOUBLE_COLON""",
		@"""STAR""",
		@"""conditions""",
		@"""inputfilters""",
		@"""FILTER_OP""",
		@"""OR""",
		@"""AND""",
		@"""NOT""",
		@"""HASH""",
		@"""LSQUARE""",
		@"""RSQUARE""",
		@"""LANGLE""",
		@"""RANGLE""",
		@"""SINGLEQUOTE""",
		@"""outputfilters""",
		@"""superimposition""",
		@"""selectors""",
		@"""PROLOG_EXPRESSION""",
		@"""ARROW_LEFT""",
		@"""filtermodules""",
		@"""annotations""",
		@"""constraints""",
		@"""presoft""",
		@"""pre""",
		@"""prehard""",
		@"""implementation""",
		@"""by""",
		@"""as""",
		@"""FILENAME""",
		@"""QUESTIONMARK""",
		@"""DIGIT""",
		@"""FILE_SPECIAL""",
		@"""LETTER""",
		@"""NEWLINE""",
		@"""SPECIAL""",
		@"""QUOTE""",
		@"""COMMENTITEMS""",
		@"""COMMENT""",
		@"""WS""",
		@"""PROLOG_SUB_EXPRESSION"""
	};
	
	private static long[] mk_tokenSet_0_()
	{
		long[] data = new long[8];
		data[0]=-16L;
		data[1]=17592186044159L;
		for (int i = 2; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static readonly BitSet tokenSet_0_ = new BitSet(mk_tokenSet_0_());
	private static long[] mk_tokenSet_1_()
	{
		long[] data = { -8065946932620558336L, 176161L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_1_ = new BitSet(mk_tokenSet_1_());
	private static long[] mk_tokenSet_2_()
	{
		long[] data = { -9218868437227405312L, 33L, 0L, 0L};
		return data;
	}
	public static readonly BitSet tokenSet_2_ = new BitSet(mk_tokenSet_2_());
	
}
}
