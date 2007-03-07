//$W
///////////////////////////////////////////////////////////////////////////
// Parser for Cps files
///////////////////////////////////////////////////////////////////////////

header {
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

$J:package Composestar.Core.COPPER;
$C:using StringBuffer = System.Text.StringBuilder;
}
options {
$C:	language  = CSharp;
$C:	namespace = "Composestar.StarLight.CpsParser";
}

class CpsParser extends Parser;
options {
	k = 1;
	buildAST = true;
	exportVocab = Cps;
	defaultErrorHandler = false;
$J:	ASTLabelType = "CpsAST";
}

//extra tokens used in constructing the tree
tokens {
	ANDEXPR_;
	ANNOTELEM_;
	ANNOTSET_;
	ANNOT_;
	ARGUMENT_;
	DECLAREDARGUMENT_;
	DECLAREDPARAMETER_;
	FPMSET_;
	FPMDEF_;
	FPMSTYPE_;
	FPMDEFTYPE_;
	CONDITION_;
	CONDNAMESET_;
	CONDNAME_;
	EXTERNAL_;
	FILTERELEM_;
	FILTERMODULEPARAMETERS_;
	FILTERSET_;
	FMELEM_;
	FMCONDBIND_;
	FMSET_;
	FM_;
	IFILTER_;
	INTERNAL_;
	METHOD2_;
	METHODNAMESET_;
	METHODNAME_;
	METHOD_;
	NOTEXPR_;
	MPSET_;
	MP_;
	MPART_;
	OCL_;
	OFILTER_;
	OREXPR_;
	PARAMETER_;
	PARAMETERLIST_;
	SELEC2_;
	SELEC_;
	SELEXP_;
	SOURCE_;
	SPART_;
	TSSET_;
	TARGET_;
	TYPELIST_;
	TYPE_;
	VAR_;
	APS_;
	PROLOG_EXPRESSION;
}
{
	public String sourceLang = null;              //source language
	public String sourceFile = null;              //source filename
	public int startPos = -1;                     //starting position of embedded source (in bytes)

$J:	private void addEmbeddedSource(Token lang, Token fn, Token start)
$C:	private void addEmbeddedSource(IToken lang, IToken fn, IToken start)
	{
		sourceLang = lang.getText();
		sourceFile = fn.getText();
		startPos = ((PosToken)start).getBytePos() + 1;
	}
}

concern : "concern"^ NAME (LPARENTHESIS! formalParameters RPARENTHESIS!)? ("in"! ns)? concernBlock;

  formalParameters : formalParameterDef (SEMICOLON! formalParameterDef)*
  { #formalParameters = #([FPMSET_, "formal parameters"], #formalParameters);} ;

    formalParameterDef : NAME (COMMA! NAME)* COLON!type
    { #formalParameterDef = #([FPMDEF_, "formal parameter definition"], #formalParameterDef);} ;

  ns : NAME (DOT NAME)*;

  concernBlock : LCURLY! (filterModule)* (superImposition)? concernEnd;

//don't bother parsing the closing end / curly if implementation is used
//(because of possible embedded source code, which stops the parsing at that point)
  concernEnd : ("implementation")=> implementation
                    | RCURLY!;

  //////////////////////////////////////////////////////////////////////////
  filterModule : "filtermodule"^ NAME (filterModuleParameters)? LCURLY! (internals)? (externals)? (conditions)? (inputFilters)? (outputFilters)? RCURLY!;
  
  filterModuleParameters : LPARENTHESIS! (filterModuleParameterSet)? RPARENTHESIS!
  {#filterModuleParameters = #([FILTERMODULEPARAMETERS_, "filtermoduleparameters"], #filterModuleParameters);} ;
  filterModuleParameterSet : (PARAMETER_NAME  | PARAMETERLIST_NAME) (COMMA! (PARAMETER_NAME | PARAMETERLIST_NAME))*
   {#filterModuleParameterSet = #([DECLAREDPARAMETER_, "declaredparameter"], #filterModuleParameterSet);} ;
    
  parameter : PARAMETER_NAME 
  {#parameter = #([PARAMETER_, "parameter"], #parameter);} ;
  
  parameterlist : PARAMETERLIST_NAME 
  {#parameterlist = #([PARAMETERLIST_, "parameterlist"], #parameterlist);} ;

    /*---------------------------------------------------------------------------*/
    internals : "internals"^ (singleInternal)* ;

      singleInternal : variableSet COLON! (parameter|type) SEMICOLON!
      { #singleInternal = #([INTERNAL_, "internal"], #singleInternal);} ;

        variableSet : NAME (COMMA! NAME)*
        { #variableSet = #([VAR_, "variable"], #variableSet);} ;

        type : NAME (DOT! NAME)*
        { #type = #([TYPE_, "type"], #type);} ;

    /*---------------------------------------------------------------------------*/
    externals : "externals"^ (singleExternal)* ;

      singleExternal : variableSet COLON!type (externalReferance)? SEMICOLON!
      { #singleExternal = #([EXTERNAL_, "external"], #singleExternal);} ;
      
      externalReferance : EQUALS! concernReference LPARENTHESIS! RPARENTHESIS!;

        //stuff that is reused in the definitions of references
        fqnDCNameCName 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME COLON! NAME;       //n.n.n.n::n:n
        fqnDCName 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME;                   //n.n.n::n
        fqn 		: NAME (DOT! NAME)*;                                      //n.n.n Fully Qualified Name
        nameCName 	: NAME COLON! NAME;                                       //n:n 
        fqnDCNameCStar 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME COLON! STAR;       //n.n.n.n::n:*
        nameCStar 	: NAME COLON! STAR;                                       //n:*


        //real definitions
        concernReference : fqn;                 																	//n.n.n
        concernElemReference : (((NAME|DOT)* DOUBLE_COLON)=> fqnDCName           //n.n.n.n::n
                                | NAME);                                          //n

        fmElemReference : (((NAME|DOT)* DOUBLE_COLON)=> fqnDCNameCName           //n.n.n.n::n:n
                          |((NAME|DOT)* COLON)=> nameCName                       //n:n
                           | NAME);                                               //n  //fixme: don't allow this for bindings

        fmElemReferenceCond : (((NAME|COLON)* DOUBLE_COLON)=> fqnDCNameCName     //n.n.n.n::n:n
                            |  (NAME COLON)=> nameCName                          //n:n
                            | 	NAME);                                            //n  //fixme: don't allow this for bindings
                            
        fmElemReferenceStar : (((NAME|DOT)* DOUBLE_COLON)=> fqnDCNameCStar       //n.n.n.n::n:*
                               | nameCStar);                     									//n:*
                               
    /*---------------------------------------------------------------------------*/
    conditions : "conditions"^ (singleCondition)* ;

      singleCondition: NAME COLON! concernReference LPARENTHESIS! RPARENTHESIS! SEMICOLON!
      { #singleCondition = #([CONDITION_, "condition"], #singleCondition);} ;


    /*---------------------------------------------------------------------------*/
    inputFilters : "inputfilters"^ generalFilterSet;

      generalFilterSet : singleInputFilter (SEMICOLON singleInputFilter)*;

        singleInputFilter : NAME COLON! type (LPARENTHESIS! actualParameters RPARENTHESIS!)? EQUALS! LCURLY! filterElements RCURLY!
        { #singleInputFilter = #([IFILTER_, "inputfilter"], #singleInputFilter );} ;

          actualParameters : NAME (COMMA! NAME)*
  	  { #actualParameters = #([APS_, "actual parameters"], #actualParameters);} ;

    	  filterElements : filterElement (COMMA filterElement)*
          { #filterElements = #([FILTERSET_, "filterelements"], #filterElements);} ;

            //filterElement : (((~(FILTER_OP|SEMICOLON))* FILTER_OP)=> orExpr FILTER_OP messagePatternSet // overuse of SEMICOLON?
            filterElement : (((~(FILTER_OP))* FILTER_OP)=> orExpr FILTER_OP messagePatternSet // overuse of SEMICOLON?
                          | messagePatternSet)
                          { #filterElement = #([FILTERELEM_, "filterelement"], #filterElement);} ;

              orExpr : andExpr (OR^ orExpr)?;
                     
                andExpr : unaryExpr (AND^ andExpr)?;
                       
                  unaryExpr : (NOT^)? operandExpr;
                         
                    operandExpr : LPARENTHESIS! orExpr RPARENTHESIS!
                                | NAME // name is either a literal or identifier
                                ;

              messagePatternSet : messagePattern
                                { #messagePatternSet = #([MPSET_, "messagePatternSet"], #messagePatternSet);} ;

                messagePattern : ( matchingPart (substitutionPart)? )
                                 { #messagePattern = #([MP_, "messagePattern"], #messagePattern);} ;
                
                 matchingPart : (
                 				  // matching list
                 				  LCURLY! singleTargetSelector (COMMA! singleTargetSelector)* RCURLY!
                 				  // matching sequence for multiple message
                                | HASH LPARENTHESIS! singleTargetSelector (SEMICOLON! singleTargetSelector)* RPARENTHESIS!
                                  // normal
                                | singleTargetSelector
                                )
                                { #matchingPart = #([MPART_, "matchingPart"], #matchingPart);}
                                ;
                
                 substitutionPart : ( 
                                      HASH LPARENTHESIS! targetSelector (SEMICOLON! targetSelector)* RPARENTHESIS!
                                    | targetSelector
                                    )
                                    { #substitutionPart = #([SPART_, "substitutionPart"], #substitutionPart);}
                                    ;
                 
                 
                   singleTargetSelector : (
                                          LSQUARE targetSelector RSQUARE!          // name
                                        | LANGLE targetSelector RANGLE!            // signature
                                        | targetSelector
                                        ) ;
                  
                   targetSelector : (target DOT)=> target DOT! (selector|parameter|parameterlist)      //extra rule to help parse target.selector correctly
                                  | (selector|parameter|parameterlist);

                    target : (NAME                      //removed inner
                           | STAR)
                           { #target = #([TARGET_, "target"], #target);} ;

                    selector : (NAME (LPARENTHESIS! (type (SEMICOLON!type)*)? RPARENTHESIS!)?
                             | STAR)
                             { #selector = #([SELEC_, "selector"], #selector);} ;


    /*---------------------------------------------------------------------------*/
    outputFilters : "outputfilters"^ generalFilterSet2;

      generalFilterSet2 : singleOutputFilter (SEMICOLON singleOutputFilter)* ;     //exactly the same definitons, but we use it to separate in- and outputfilters

        singleOutputFilter : NAME COLON! type (LPARENTHESIS! actualParameters RPARENTHESIS!)? EQUALS! LCURLY! filterElements RCURLY!
        { #singleOutputFilter = #([OFILTER_, "outputfilter"], #singleOutputFilter);} ;
        
   //////////////////////////////////////////////////////////////////////////
  superImposition : "superimposition"^ superImpositionBlock;

    superImpositionBlock : LCURLY! superImpositionInner RCURLY!;

      superImpositionInner : (conditionDef)? (selectorDef)? (filtermoduleBind)? (annotationBind)? (constraints)?;

    /*---------------------------------------------------------------------------*/
    conditionDef : "conditions"^ (singleConditionDef)* ;

      singleConditionDef: NAME COLON! concernReference SEMICOLON!
      { #singleConditionDef = #([CONDITION_, "condition"], #singleConditionDef);} ;


    /*---------------------------------------------------------------------------*/
    selectorDef : "selectors"^ (singleSelector)* ;

       singleSelector :  NAME EQUALS! LCURLY! ((oldSelExpression (COMMA! oldSelExpression)* RCURLY!) | predSelExpression) SEMICOLON!
                     { #singleSelector = #([SELEC2_, "selector"], #singleSelector);} ;

        oldSelExpression: STAR! (EQUALS | COLON) concernReference
        { #oldSelExpression = #([SELEXP_, "selectorexpression"], #oldSelExpression);} ;
        

       predSelExpression : NAME OR! prologExpression RCURLY!;
      
         prologExpression 
         { StringBuffer sb = new StringBuffer(); }
         :   (   x:~(RCURLY)
                 $J:{ sb.append(x.getText()); }
                 $C:{ sb.Append(x.getText()); }
             )*
             $J:{ #prologExpression = #([PROLOG_EXPRESSION, sb.toString()]); }
             $C:{ #prologExpression = #([PROLOG_EXPRESSION, sb.ToString()]); }
         ;

    /*---------------------------------------------------------------------------*/
    commonBindingPart : (NAME FILTER_OP) => fmCondBind selectorRef weaveOperation
    	| selectorRef weaveOperation;
    
    	fmCondBind : NAME FILTER_OP
    	    { #fmCondBind = #([FMCONDBIND_, "condition"], #fmCondBind);};
    
       	conditionRef : fmElemReference;
  
      	selectorRef : concernElemReference;

      	weaveOperation : ARROW_LEFT!;

    /*---------------------------------------------------------------------------*/
    filtermoduleBind : "filtermodules"^ (singleFmBind)* ;

      singleFmBind : commonBindingPart filterModuleSet SEMICOLON!
                   { #singleFmBind = #([FM_, "filtermodule"], #singleFmBind);} ;

        filterModuleSet : ( LCURLY! filterModuleElement (COMMA! filterModuleElement)* RCURLY!
                          | filterModuleElement (COMMA! filterModuleElement)*
                          )
                          { #filterModuleSet = #([FMSET_, "filtermodule set"], #filterModuleSet);} ;

          filterModuleElement : concernElemReference (fmBindingArguments)?
          					 { #filterModuleElement = #([FMELEM_, "filtermodule element"], #filterModuleElement);} ;
          fmBindingArguments : LPARENTHESIS! (argument (COMMA! argument)*)? RPARENTHESIS!
          				     { #fmBindingArguments = #([DECLAREDARGUMENT_, "declaredargument"], #fmBindingArguments);};
          argument : fqn
                     { #argument = #([ARGUMENT_, "argument"], #argument);}
                   | LCURLY! fqn (COMMA! fqn)* RCURLY!
                     { #argument = #([ARGUMENT_, "argument"], #argument);};

    /*---------------------------------------------------------------------------*/
    annotationBind : "annotations"^ (singleAnnotBind)* ;

      singleAnnotBind : selectorRef ARROW_LEFT! annotationSet SEMICOLON!
                   { #singleAnnotBind = #([ANNOT_, "annotation"], #singleAnnotBind);} ;

      annotationSet : LCURLY! annotationElement (COMMA! annotationElement)* RCURLY!
                        | annotationElement (COMMA! annotationElement)*
                          { #annotationSet = #([ANNOTSET_, "annotation_set"], #annotationSet);} ;

          annotationElement : concernReference
          { #annotationElement = #([ANNOTELEM_, "annotation element"], #annotationElement);} ;

    /*---------------------------------------------------------------------------*/
    constraints : "constraints"^ (constraint SEMICOLON!)*; 
      
      constraint : preConstraint;

            filterModuleRef : concernElemReference;

        preConstraint : "pre"^ LPARENTHESIS! filterModuleRef COMMA filterModuleRef RPARENTHESIS!;

  //////////////////////////////////////////////////////////////////////////
  implementation : "implementation"^ implementationInner;

    implementationInner : "in"! lang:NAME "by"! tn:fqn "as"! fn:FILENAME start:LCURLY!
                          { addEmbeddedSource(lang, fn, start); }
                        | "by"! asm:fqn SEMICOLON
                        ; 

///////////////////////////////////////////////////////////////////////////
// Lexer for Cps files
///////////////////////////////////////////////////////////////////////////

class CpsLexer extends Lexer;
options {
        k = 2;
        exportVocab = Cps;
}

AND                     : '&'  ;
ARROW_LEFT              : "<-" ;
COLON                   : ':'  ;
COMMA                   : ','  ;
DOT                     : '.'  ;
DOUBLE_COLON            : "::" ;
EQUALS                  : '='  ;
FILTER_OP               : "=>" | "~>" ;
HASH                    : '#' ;
LANGLE                  : '<' ;
LCURLY                  : '{' ;
LPARENTHESIS            : '(' ;
LSQUARE                 : '[' ;
NOT                     : '!' ;
OR                      : '|' ;
RANGLE                  : '>' ;
RCURLY                  : '}' ;
RPARENTHESIS            : ')' ;
RSQUARE                 : ']' ;
SEMICOLON               : ';' ;
STAR                    : '*' ;
QUESTIONMARK            : '?' ;

protected DIGIT         : '0'..'9' ;
protected FILE_SPECIAL  : '\\' | '/' | COLON!| ' ' | DOT;     //special items only allowed in filenames
protected LETTER        : 'a'..'z'|'A'..'Z' ;
protected NEWLINE       : (("\r\n")=> "\r\n"           //DOS
                          | '\r'                        //Macintosh
                          | '\n'                        //Unix
                          ) {newline();};

protected SPECIAL       : '_';
protected QUOTE         : '"';
protected SINGLEQUOTE   : '\'';

// used for prolog expressions
PROLOG_STRING           : SINGLEQUOTE ( ~('\'') )* SINGLEQUOTE;

protected COMMENTITEMS  : NEWLINE COMMENTITEMS
                        | ("*/")=> "*/"
                        | (~ ('\n'|'\r')) COMMENTITEMS;

COMMENT                 : ("//" (~('\n'|'\r'))*
                        | "/*" COMMENTITEMS) { $setType(Token.SKIP); };

FILENAME                : (QUOTE (LETTER | DIGIT | SPECIAL | FILE_SPECIAL)+ QUOTE)=> QUOTE! (LETTER | DIGIT | SPECIAL | FILE_SPECIAL)+ QUOTE! //fixme: also allow paths?
                          | QUOTE {$setType(QUOTE);};

NAME                    : (LETTER | SPECIAL) (LETTER | DIGIT | SPECIAL)*;

PARAMETERLIST_NAME      : QUESTIONMARK QUESTIONMARK NAME;

PARAMETER_NAME          : QUESTIONMARK NAME;

// NEWLINE and WS.... you can combine those
WS                      : (NEWLINE)=> NEWLINE { /*newline();*/ $setType(Token.SKIP);}
                          | (' ' | '\t' | '\f') { $setType(Token.SKIP); } ;
