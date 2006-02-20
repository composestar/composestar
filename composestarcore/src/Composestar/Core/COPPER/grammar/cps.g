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
 * $Id: cps.g,v 1.1 2006/02/16 23:03:49 pascal_durr Exp $
 */

/**
 * Parser / lexer class for .cps files
 */
package Composestar.Core.COPPER;

}

class CpsParser extends Parser;
options {
        buildAST = true;
        exportVocab = Cps;
        k = 2;
	ASTLabelType = "CpsAST";  
	defaultErrorHandler = false;
}

tokens {                                //extra tokens used in constructing the tree
        ANDEXPR_;
        ANNOTELEM_;
        ANNOTSET_;
        ANNOT_;
        FPMSET_;
        FPMDEF_;
        FPMSTYPE_;
        FPMDEFTYPE_;
        CONDITION_;
        CONDNAMESET_;
        CONDNAME_;
        EXTERNAL_;
        FILTERELEM_;
        FILTERSET_;
        FMELEM_;
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
        OCL_;
        OFILTER_;
        OREXPR_;
        SELEC2_;
        SELEC_;
        SELEXP_;
        SOURCE_;
        TARGET_;
        TYPELIST_;
        TYPE_;
        VAR_;
        APS_;
}
{
  public boolean sourceIncluded = false;        //source included in Cps file?
  public int startPos = 0;                      //starting position of embedded source (in bytes)
  public String sourceLang = null;              //source language
  public String sourceFile = null;              //source filename
  public int skipsize = 0;                      //how much to skip at the end when using an embedded implementation
}

concern : "concern"^ NAME (LPARENTHESIS! formalParameters RPARENTHESIS!)? ("in"! namespace)? concernBlock;

  formalParameters : formalParameterDef (SEMICOLON! formalParameterDef)*
  { #formalParameters = #([FPMSET_, "formal parameters"], #formalParameters);} ;

    formalParameterDef : NAME (COMMA! NAME)* COLON!type
    { #formalParameterDef = #([FPMDEF_, "formal parameter definition"], #formalParameterDef);} ;

  namespace : NAME (DOT! NAME)*;

  concernBlock : LCURLY! (filterModule)* (superImposition)? concernEnd;   																																	

//don't bother parsing the closing end / curly if implementation is used
//(because of possible embedded source code, which stops the parsing at that point)
  concernEnd : (implementation) => (implementation)
                    | RCURLY! (SEMICOLON!)?;

  //////////////////////////////////////////////////////////////////////////
  filterModule : "filtermodule"^ NAME filterModuleBlock ;

  filterModuleBlock : LCURLY! (internals)? (externals)? (conditions)? (methodDeclarations)? (inputFilters)? (outputFilters)? RCURLY! (SEMICOLON!)?;

    /*---------------------------------------------------------------------------*/
    internals : "internals"^ (singleInternal)* ;

      singleInternal : variableSet COLON! type SEMICOLON!
      { #singleInternal = #([INTERNAL_, "internal"], #singleInternal);} ;

        variableSet : NAME (COMMA! NAME)*
        { #variableSet = #([VAR_, "variable"], #variableSet);} ;

        type : NAME (DOT! NAME)*
        { #type = #([TYPE_, "type"], #type);} ;

    /*---------------------------------------------------------------------------*/
    externals : "externals"^ (singleExternal)* ;

      singleExternal : variableSet COLON!type (externalReferance)? SEMICOLON!
      { #singleExternal = #([EXTERNAL_, "external"], #singleExternal);} ;
      
      externalReferance : (((~(DOUBLE_COLON|COMMA|SEMICOLON))* DOUBLE_COLON) => EQUALS! fmElemReference //add the equals to differentiate between the alternatives
           | EQUALS! concernReference LPARENTHESIS! RPARENTHESIS!);

        //stuff that is reused in the definitions of references
        fqnDCNameCName 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME COLON! NAME;       //n.n.n.n::n:n
        fqnDCName 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME;                   //n.n.n::n
        fqn 		: NAME (DOT! NAME)*;                                      //n.n.n Fully Qualified Name
        nameCName 	: NAME COLON! NAME;                                       //n:n 
        fqnDCNameCStar 	: NAME (DOT! NAME)* DOUBLE_COLON! NAME COLON! STAR;       //n.n.n.n::n:*
        nameCStar 	: NAME COLON! STAR;                                       //n:*


        //real definitions
        concernReference : fqn;                 																	//n.n.n
        concernElemReference : (((NAME|DOT)* DOUBLE_COLON) => fqnDCName           //n.n.n.n::n
                                | NAME);                                          //n

        fmElemReference : (((NAME|DOT)* DOUBLE_COLON) => fqnDCNameCName           //n.n.n.n::n:n
                          |((NAME|DOT)* COLON) => nameCName                      	//n:n
                           | NAME);                                               //n  //fixme: don't allow this for bindings

        fmElemReferenceCond : (((NAME|COLON)* DOUBLE_COLON) => fqnDCNameCName     //n.n.n.n::n:n
                            |  nameCName                      										//n:n
                            | 	NAME);                                            //n  //fixme: don't allow this for bindings
                            
        fmElemReferenceStar : (((NAME|DOT)* DOUBLE_COLON) => fqnDCNameCStar       //n.n.n.n::n:*
                               | nameCStar);                     									//n:*
                               
    /*---------------------------------------------------------------------------*/
    conditions : "conditions"^ (singleCondition)* ;

      singleCondition:(((~(DOUBLE_COLON|COMMA|SEMICOLON))* DOUBLE_COLON) => NAME COLON! fmElemReference SEMICOLON //add the SEMICOLON!to differentiate between the alternatives
      		      | NAME COLON! concernReference LPARENTHESIS! RPARENTHESIS! SEMICOLON!)
      { #singleCondition = #([CONDITION_, "condition"], #singleCondition);} ;


    /*---------------------------------------------------------------------------*/
    methodDeclarations : "methods"^ (singleMethodDeclaration)* ;

      singleMethodDeclaration : NAME LPARENTHESIS! (formalParametersType)? RPARENTHESIS! (COLON!type)? SEMICOLON!
      { #singleMethodDeclaration = #([METHOD_, "method"], #singleMethodDeclaration);} ;

        formalParametersType : formalParameterDefType (SEMICOLON!formalParameterDefType)*
        { #formalParametersType = #([FPMSTYPE_, "formal parameters (only type)"], #formalParametersType);} ;

          formalParameterDefType : (((~(COLON|RPARENTHESIS|COMMA|SEMICOLON))* COLON) => NAME (COMMA! NAME)* COLON!type
                                 | type)
          { #formalParameterDefType = #([FPMDEFTYPE_, "formal parameter definition (only type)"], #formalParameterDefType);} ;


    /*---------------------------------------------------------------------------*/
    inputFilters : "inputfilters"^ (generalFilterSet)* ;

      generalFilterSet : singleInputFilter (SEMICOLON singleInputFilter)*;

        singleInputFilter : NAME COLON! type (LPARENTHESIS! actualParameters RPARENTHESIS!)? EQUALS! LCURLY! (filterElements)? RCURLY!
        { #singleInputFilter = #([IFILTER_, "inputfilter"], #singleInputFilter );} ;

          actualParameters : NAME (COMMA! NAME)*
  	  { #actualParameters = #([APS_, "actual parameters"], #actualParameters);} ;

    	  filterElements : filterElement (COMMA filterElement)*
          { #filterElements = #([FILTERSET_, "filterelements"], #filterElements);} ;

            filterElement : (((~(FILTER_OP|SEMICOLON))* FILTER_OP) => orExpr FILTER_OP messagePatternSet
                          | messagePatternSet)
                          { #filterElement = #([FILTERELEM_, "filterelement"], #filterElement);} ;

              orExpr : andExpr (OR andExpr)*
                     { #orExpr = #([OREXPR_, "orExpression"], #orExpr);};

                andExpr : notExpr (AND notExpr)*
                        { #andExpr = #([ANDEXPR_, "andExpression"], #andExpr);};

                  //changed it from fmElemReferenceCond to NAME, since reuse must be done in the conditions block
                  notExpr : (NOT)? ( NAME )
                          { #notExpr = #([NOTEXPR_, "notExpression"], #notExpr);};

              messagePatternSet : (LCURLY! messagePattern (COMMA messagePattern)* RCURLY!
                                | messagePattern)
                                { #messagePatternSet = #([MPSET_, "messagePatternSet"], #messagePatternSet);} ;

                messagePattern : (LSQUARE targetSelector RSQUARE! (targetSelector)? // name
                     	         | LANGLE targetSelector RANGLE! (targetSelector)? // signature
                               | SINGLEQUOTE targetSelector SINGLEQUOTE! (targetSelector)? //name
                               | targetSelector)
                               { #messagePattern = #([MP_, "messagePattern"], #messagePattern);} ;

                  targetSelector : target DOT! selector      //extra rule to help parse target.selector correctly
                                 | selector;

                    target : (NAME                      //removed inner
                           | STAR)
                           { #target = #([TARGET_, "target"], #target);} ;

                    selector : (NAME (LPARENTHESIS! (type (SEMICOLON!type)*)? RPARENTHESIS!)?
                             | STAR)
                             { #selector = #([SELEC_, "selector"], #selector);} ;


    /*---------------------------------------------------------------------------*/
    outputFilters : "outputfilters"^ (generalFilterSet2)* ;

      generalFilterSet2 : singleOutputFilter (SEMICOLON singleOutputFilter)* ;     //exactly the same definitons, but we use it to separate in- and outputfilters

        singleOutputFilter : NAME COLON! type (LPARENTHESIS! actualParameters RPARENTHESIS!)? (EQUALS! LCURLY! (filterElements)? RCURLY!)?
        { #singleOutputFilter = #([OFILTER_, "outputfilter"], #singleOutputFilter);} ;


  //////////////////////////////////////////////////////////////////////////
  superImposition : "superimposition"^ superImpositionBlock (SEMICOLON!)?;

    superImpositionBlock : LCURLY! superImpositionInner RCURLY! ("superimposition"!)?;

      superImpositionInner : (selectorDef)? (conditionBind)? (methodBind)? (filtermoduleBind)? (annotationBind)? (constraints)?;


    /*---------------------------------------------------------------------------*/
    selectorDef : "selectors"^ (singleSelector)* ;

       singleSelector :  NAME EQUALS! LCURLY! ((oldSelExpression (COMMA! oldSelExpression)* RCURLY!) | predSelExpression) SEMICOLON!
                     { #singleSelector = #([SELEC2_, "selector"], #singleSelector);} ;

        oldSelExpression: STAR! (EQUALS | COLON) concernReference
        { #oldSelExpression = #([SELEXP_, "selectorexpression"], #oldSelExpression);} ;
        

	   predSelExpression : NAME PROLOG_EXPRESSION;
	   

    /*---------------------------------------------------------------------------*/
    commonBindingPart : (event)? (bindCondition)? selectorRef weaveOperation;

      event : "on"^ LPARENTHESIS! (instanceCreated | applicationStart | methodCalled) RPARENTHESIS!;

        instanceCreated : "instancecreated"^;

        applicationStart : "applicationstart"^;

        methodCalled : "methodcalled"^ COLON! concernReference (LPARENTHESIS! (formalParametersType)? RPARENTHESIS!)? (COLON! type)?; 

      	bindCondition : "if"^ LPARENTHESIS! conditionRef RPARENTHESIS!;

      	conditionRef : fmElemReference;
  
      	selectorRef : concernElemReference;

      	weaveOperation : ARROW_LEFT!
                       | ARROW_RIGHT!;

    /*---------------------------------------------------------------------------*/
    conditionBind : "conditions"^ (singleConditionBind)* ;

      singleConditionBind : commonBindingPart conditionNameSet SEMICOLON!
                          { #singleConditionBind = #([CONDITION_, "condition"], #singleConditionBind); } ;

        conditionNameSet : (LCURLY! conditionName (COMMA! conditionName)* RCURLY!
                           | conditionName (COMMA! conditionName)*)
                           { #conditionNameSet = #([CONDNAMESET_, "condition name set"], #conditionNameSet);} ;

          conditionName : (((~(STAR|SEMICOLON))* STAR) => fmElemReferenceStar
                          | fmElemReference)             //condition_name_id
                          { #conditionName = #([CONDNAME_, "condition name"], #conditionName);} ;

    /*---------------------------------------------------------------------------*/
    methodBind : "methods"^ (singleMethodBind )* ;

      singleMethodBind : commonBindingPart methodNameSet SEMICOLON!
                       { #singleMethodBind = #([METHOD2_, "method"], #singleMethodBind);} ;

        methodNameSet : ( LCURLY! methodName (COMMA methodName)* RCURLY!
                        | methodName (COMMA methodName)*)
                        { #methodNameSet = #([METHODNAMESET_, "method name set"], #methodNameSet);} ;

          methodName : (((~(STAR|SEMICOLON))* STAR) => fmElemReferenceStar
                       | fmElemReference (LPARENTHESIS! (typeList)? RPARENTHESIS!)?)     //method_name_id
                       { #methodName = #([METHODNAME_, "method name"], #methodName);} ;

            typeList : type (COMMA! type)*              //or instead of COMMA!: SEMICOLON!??
                       { #typeList = #([TYPELIST_, "typelist"], #typeList);} ;


    /*---------------------------------------------------------------------------*/
    filtermoduleBind : "filtermodules"^ (singleFmBind)* ;

      singleFmBind : commonBindingPart filterModuleSet SEMICOLON!
                   { #singleFmBind = #([FM_, "filtermodule"], #singleFmBind);} ;

        filterModuleSet : ( LCURLY! filterModuleElement (COMMA! filterModuleElement)* RCURLY!
                          | filterModuleElement (COMMA! filterModuleElement)*)
                          { #filterModuleSet = #([FMSET_, "filtermodule set"], #filterModuleSet);} ;

          filterModuleElement : concernElemReference
          { #filterModuleElement = #([FMELEM_, "filtermodule element"], #filterModuleElement);} ;

    /*---------------------------------------------------------------------------*/
	annotationBind : "annotations"^ (singleAnnotBind)* ;
	
      singleAnnotBind : selectorRef ARROW_LEFT! annotationSet SEMICOLON!
                   { #singleAnnotBind = #([ANNOT_, "annotation"], #singleAnnotBind);} ;

        annotationSet : (((~(LCURLY|SEMICOLON))* LCURLY) => LCURLY! annotationElement (COMMA! annotationElement)* RCURLY!
                        | annotationElement (COMMA! annotationElement)*)
                          { #annotationSet = #([ANNOTSET_, "annotation_set"], #annotationSet);} ;

          annotationElement : concernReference
          { #annotationElement = #([ANNOTELEM_, "annotation element"], #annotationElement);} ;

    /*---------------------------------------------------------------------------*/
    constraints : "constraints"^ (constraint SEMICOLON!)*; 
      
      constraint : preSoftConstraint 
                   | preConstraint 
                   | preHardConstraint;

        preSoftConstraint : "presoft"^ LPARENTHESIS! filterModuleRef COMMA filterModuleRef RPARENTHESIS!;

            filterModuleRef : concernElemReference;

        preConstraint : "pre"^ LPARENTHESIS! filterModuleRef COMMA filterModuleRef RPARENTHESIS!;

        preHardConstraint : "prehard"^ LPARENTHESIS! filterModuleRef COMMA filterModuleRef RPARENTHESIS!;


  //////////////////////////////////////////////////////////////////////////
  implementation : "implementation"^ implementationInner;

	implementationInner : "in"! l2:NAME "by"! fqn "as"! f2:FILENAME lcurly:LCURLY! { sourceLang=l2.getText(); sourceFile=f2.getText(); sourceIncluded=true; if(lcurly != null) startPos=((PosToken)lcurly).getBytePos() + 4;}
                      	| "by"! fqn SEMICOLON;   //assemblyname


///////////////////////////////////////////////////////////////////////////
// Lexer for Cps files
///////////////////////////////////////////////////////////////////////////

class CpsLexer extends Lexer;
options {
        k = 2;
        exportVocab = Cps;
        charVocabulary = '\3'..'\377'; // just handle ASCII not Unicode
}

AND                     : '&';
ARROW_LEFT              : "<-" ;
ARROW_RIGHT             : "->" ;
COLON                   : ':' ;
COMMA                   : ',' ;
DOT                     : '.' ;
DOUBLE_COLON            : "::" ;
EQUALS                  : '=' ;
FILTER_OP               : "=>" | "~>" ;
LANGLE                  : '<' ;
LCURLY                  : '{' ;
LPARENTHESIS            : '(';
LSQUARE                 : '[' ;
NOT                     : '!';
OR                      : '|';
RANGLE                  : '>' ;         //right angle bracket
RCURLY                  : '}' ;
RPARENTHESIS            : ')';
RSQUARE                 : ']' ;
SEMICOLON               : ';' ;
STAR                    : '*' ;
SINGLEQUOTE             : '\'' ;

protected DIGIT         : '0'..'9' ;
protected FILE_SPECIAL  : '\\' | '/' | COLON!| ' ' | DOT;     //special items only allowed in filenames
protected LETTER        : 'a'..'z'|'A'..'Z' ;
protected NEWLINE       : (("\r\n") => "\r\n"           //DOS
                          | '\r'                        //Macintosh
                          | '\n'){newline();};          //Unix

protected SPECIAL       : '_';
protected QUOTE         : '"';

protected COMMENTITEMS  : NEWLINE COMMENTITEMS
				  | ("*/") => "*/"
				  | (~ ('\n'|'\r')) COMMENTITEMS;

COMMENT                 : ("//" (~('\n'|'\r'))*
			        | "/*" COMMENTITEMS) { $setType(Token.SKIP); };

FILENAME                : (QUOTE (LETTER | DIGIT | SPECIAL | FILE_SPECIAL)+ QUOTE) => QUOTE! (LETTER | DIGIT | SPECIAL | FILE_SPECIAL)+ QUOTE! //fixme: also allow paths?
                          | QUOTE {$setType(QUOTE);};

NAME                    : (LETTER | SPECIAL) (LETTER | DIGIT | SPECIAL)*;

// NEWLINE and WS.... you can combine those
WS                      : (NEWLINE) => NEWLINE { /*newline();*/ $setType(Token.SKIP);}
                          | (' ' | '\t' | '\f') { $setType(Token.SKIP); } ;

PROLOG_EXPRESSION       : 
  '|' 
    (
      options { greedy=false; } : // Have to turn off greediness, otherwise the rest of the input would match
      NEWLINE
      | .                    // Match everything (ungreedy) until the '}'
    ) *
  RCURLY!;

