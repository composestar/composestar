header
{
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
}
{
import java.io.*;

import antlr.CommonAST;
import antlr.DumpASTVisitor;
}class GnuCParser extends Parser;

options {
	k= 2;
	exportVocab= GnuC;
	buildAST= true;
	ASTLabelType= "TNode";
	codeGenMakeSwitchThreshold= 2;
	codeGenBitsetTestThreshold= 3;
	importVocab=STDC;
}

{
    // Suppport C++-style single-line comments?
    public static boolean CPPComments = true;
    
    // access to symbol table
    public CSymbolTable symbolTable = new CSymbolTable();

    // source for names to unnamed scopes
    protected int unnamedScopeCounter = 0;
    
    private String filename = "";
    
    public void setFilename(String name)
    {
    	this.filename = name;
    }
    
    public String getFilename()
    {
    	return this.filename;
    }
    
    // Required for our weaver
    protected boolean checksymbols = true;
    
    public boolean checksSymbols()
    {
    	return checksymbols;
    }
    
    public void activateSymbolCheck(boolean check)
    {
    	checksymbols = check;
    }

    public boolean isTypedefName(String name) {
    	if(!checksymbols) return true;
      boolean returnValue = false;
      TNode node = symbolTable.lookupNameInCurrentScope(name);
      for (; node != null; node = (TNode) node.getNextSibling() ) {
        if(node.getType() == LITERAL_typedef) {
            returnValue = true;
            break;
        }
      }
      return returnValue;
    }

	private void runPatchLeftCurly(TNode node)
    {
            if(node.getType() == 7)
                node.setText("{");

            if(node.getNumberOfChildren()>0)
            {
                runPatchLeftCurly((TNode) node.getFirstChild());
            }
            if(node.getNextSibling() != null)
            {
                runPatchLeftCurly((TNode) node.getNextSibling());
            }
    }

    public String getAScopeName() {
      return "" + (unnamedScopeCounter++);
    }

    public void pushScope(String scopeName) {
      symbolTable.pushScope(scopeName);
    }

    public void popScope() {
      symbolTable.popScope();
    }

        int traceDepth = 0;
        public void reportError(RecognitionException ex) {
          try {
            String text = "ANTLR Parsing Error: ";
        	text+="file "+this.getFilename()+" "+ex.getMessage();
            System.err.println(text + " token name:" + tokenNames[LA(1)]);
            ex.printStackTrace(System.err);
          }
	  catch (TokenStreamException e) {
            System.err.println("ANTLR Parsing Error: "+ex);
            ex.printStackTrace(System.err);              
          }
        }
        public void reportError(String s) {
            System.err.println("ANTLR Parsing Error from String: " + s);
        }
        public void reportWarning(String s) {
            System.err.println("ANTLR Parsing Warning from String: " + s);
        }
        public void match(int t) throws MismatchedTokenException {
          boolean debugging = false;
          
          if ( debugging ) {
           for (int x=0; x<traceDepth; x++) System.out.print(" ");
           try {
            System.out.println("Match("+tokenNames[t]+") with LA(1)="+
                tokenNames[LA(1)] + ((inputState.guessing>0)?" [inputState.guessing "+ inputState.guessing + "]":""));
           }
           catch (TokenStreamException e) {
            System.out.println("Match("+tokenNames[t]+") " + ((inputState.guessing>0)?" [inputState.guessing "+ inputState.guessing + "]":""));

           }
    
          }
          try {
            if ( LA(1)!=t ) {
                if ( debugging ){
                    for (int x=0; x<traceDepth; x++) System.out.print(" ");
                    System.out.println("token mismatch: "+tokenNames[LA(1)]
                                + "!="+tokenNames[t]);
                }
	        throw new MismatchedTokenException(tokenNames, LT(1), t, false, getFilename());

            } else {
                // mark token as consumed -- fetch next token deferred until LA/LT
                consume();
            }
          }
          catch (TokenStreamException e) {
          }
    
        }
        public void traceIn(String rname) {
          traceDepth += 1;
          for (int x=0; x<traceDepth; x++) System.out.print(" ");
          try {
            System.out.println("> "+rname+"; LA(1)==("+ tokenNames[LT(1).getType()] 
                + ") " + LT(1).getText() + " [inputState.guessing "+ inputState.guessing + "]");
          }
          catch (TokenStreamException e) {
          }
        }
        public void traceOut(String rname) {
          for (int x=0; x<traceDepth; x++) System.out.print(" ");
          try {
            System.out.println("< "+rname+"; LA(1)==("+ tokenNames[LT(1).getType()] 
                + ") "+LT(1).getText() + " [inputState.guessing "+ inputState.guessing + "]");
          }
          catch (TokenStreamException e) {
          }
          traceDepth -= 1;
        }
    
}
translationUnit :( externalList )?       /* Empty source files are allowed.  */
        ;

asm_expr :"asm"^ 
                ("volatile")? LCURLY expr RCURLY ( SEMI )+
        ;

idList :ID ( options{warnWhenFollowAmbig=false;}: COMMA ID )*
        ;

externalDef :( "typedef" | declaration )=> declaration
        |       functionDef
        |       typelessDeclaration
        |       asm_expr
        |       SEMI
        ;

functionDef { String declName; }
:( (functionDeclSpecifiers)=> ds:functionDeclSpecifiers
                |  //epsilon
                )
                declName = d:declarator[true]
                ( declaration )* (VARARGS)? ( SEMI! )*
                ( traceDef )?
                compoundStatement[declName] { ## = #( #[NFunctionDef], ## );}
        ;

traceDef :// __trace__(( in(a), out(b)))
        "__trace__"^ LPAREN LPAREN
        (
        	ID
        	(LPAREN 
        	(idList|StringLiteral)
        	RPAREN)?
        )*
        RPAREN RPAREN
        ;

annotation :// __ANOTATIONNAME__(  )
		"__"^ ID "__" LPAREN (stringConst ( COMMA stringConst)* )? RPAREN;

typelessDeclaration { AST typeMissing = #[NTypeMissing]; }
:initDeclList[typeMissing] SEMI          { ## = #( #[NTypeMissing], ##); }
        ;

initializer :( ( ( (initializerElementLabel)=> initializerElementLabel )?
                ( assignExpr | lcurlyInitializer )  { ## = #( #[NInitializer], ## ); }
              )
              | lcurlyInitializer
              )
        ;

initializerElementLabel :(   ( LBRACKET ((constExpr VARARGS)=> rangeExpr | constExpr) RBRACKET (ASSIGN)? )
                | ID COLON
                | DOT ID ASSIGN
            )
                                    { ## = #( #[NInitializerElementLabel], ##) ; }
        ;

lcurlyInitializer :LCURLY^ (initializerList ( COMMA! )? )? RCURLY
                            { ##.setType( NLcurlyInitializer ); }
        ;

initializerList :initializer ( options{warnWhenFollowAmbig=false;}:COMMA! initializer )*
        ;

declarator[boolean isFunctionDefinition] returns [String declName]{ declName = ""; }
:( pointerGroup )?               

                ( id:ID                         { declName = id.getText(); }
                | LPAREN declName = declarator[false] RPAREN
                )

                ( declaratorParamaterList[isFunctionDefinition, declName]
                | LBRACKET ( expr )? RBRACKET
                )*
                                                { ## = #( #[NDeclarator], ## ); }
        ;

declaratorParamaterList[boolean isFunctionDefinition, String declName] :LPAREN^
                                                { 
                                                    if (isFunctionDefinition) {
                                                    	if(checksymbols) pushScope(declName);
                                                    }
                                                    else {
                                                        if(checksymbols) pushScope("!"+declName); 
                                                    }
                                                }
                (                           
                        (declSpecifiers)=> parameterTypeList
                        | (idList)?
                )
                                                {
                                                	if(checksymbols) popScope();
                                                }    
                ( COMMA! )?
                RPAREN       
                                                { ##.setType(NParameterTypeList);                                             
                                                }      
        ;

parameterTypeList :parameterDeclaration 
                (   options {
                            warnWhenFollowAmbig = false;
                        } : 
                  ( COMMA | SEMI )  
                  parameterDeclaration
                )*
                ( ( COMMA | SEMI ) 
                  VARARGS
                )?
        ;

declarationList :(               options {   // this loop properly aborts when
                                            // it finds a non-typedefName ID MBZ
                                            warnWhenFollowAmbig = false;
                                        } :
    
                localLabelDeclaration
                |  ( declarationPredictor )=> declaration
                )+
        ;

localLabelDeclaration :( //GNU note:  any __label__ declarations must come before regular declarations.
                "__label__"^ ID (options{warnWhenFollowAmbig=false;}: COMMA! ID)* ( COMMA! )? ( SEMI! )+
                )
        ;

declaration { AST ds1 = null; }
:ds:declSpecifiers       { ds1 = astFactory.dupList(#ds); }
                (                       
                    initDeclList[ds1]
                )?
                ( SEMI )+
                                        { ## = #( #[NDeclaration], ##); }
                
        ;

functionStorageClassSpecifier :"extern"
        |       "static"
        |       "inline"
        ;

typeSpecifier[int specCount] returns [int retSpecCount]{ retSpecCount = specCount + 1; }
:(       "void" 
        |       "char"
        |       "short"
        |       "int"
        |       "long"
        |       "float"
        |       "double"
        |       "signed"
        |       "unsigned"
        |       structOrUnionSpecifier  ( options{warnWhenFollowAmbig=false;}: attributeDecl )*
        |       enumSpecifier
        |       { specCount==0 }? typedefName
        |       "typeof"^ LPAREN ( ( typeName )=> typeName | expr ) RPAREN
        |       "__complex"
        )
        ;

structOrUnionSpecifier { String scopeName; }
:sou:structOrUnion!
                ( ( ID LCURLY )=> i:ID l:LCURLY
                                            {
                                            scopeName = #sou.getText() + " " + #i.getText();
                                            #l.setText(scopeName);
                                            if(checksymbols) pushScope(scopeName);
                                            }
                        ( structDeclarationList )?
                                            { if(checksymbols) popScope(); }
                        RCURLY
                |   l1:LCURLY
                                            {
                                            scopeName = getAScopeName();
                                            #l1.setText(scopeName);
                                            if(checksymbols) pushScope(scopeName);
                                            }
                    ( structDeclarationList )?
                                            { if(checksymbols) popScope(); }
                    RCURLY
                | ID
                )
                                            {
                                            ## = #( #sou, ## );
                                            }
        ;

structDeclaration :specifierQualifierList structDeclaratorList ( COMMA! )? ( SEMI! )+
        ;

structDeclaratorList :structDeclarator ( options{warnWhenFollowAmbig=false;}: COMMA! structDeclarator )*
        ;

structDeclarator :( declarator[false] )?
                ( COLON constExpr )?
                ( attributeDecl )*
                                    { ## = #( #[NStructDeclarator], ##); }
        ;

enumSpecifier :"enum"^
                ( ( ID LCURLY )=> i:ID LCURLY enumList[i.getText()] RCURLY
                | LCURLY enumList["anonymous"] RCURLY
                | ID
                )
        ;

enumList[String enumName] :enumerator[enumName] ( options{warnWhenFollowAmbig=false;}: COMMA! enumerator[enumName] )* ( COMMA! )?
        ;

initDeclList[AST declarationSpecifiers] :initDecl[declarationSpecifiers] 
                ( options{warnWhenFollowAmbig=false;}: COMMA! initDecl[declarationSpecifiers] )*
                ( COMMA! )?
        ;

initDecl[AST declarationSpecifiers] { String declName = ""; }
:declName = d:declarator[false]
                                        {   AST ds1, d1;
                                            ds1 = astFactory.dupList(declarationSpecifiers);
                                            d1 = astFactory.dupList(#d);
                                            symbolTable.add(declName, #(null, ds1, d1) );
                                        }
                ( attributeDecl )*
                ( ASSIGN initializer
                | COLON expr
                )?
                                        { ## = #( #[NInitDecl], ## ); }
        ;

attributeDecl :"__attribute"^ LPAREN LPAREN attributeList RPAREN RPAREN
                | "asm"^ LPAREN stringConst RPAREN { ##.setType( NAsmAttribute ); }
        ;

attributeList :attribute ( options{warnWhenFollowAmbig=false;}: COMMA attribute)*  ( COMMA )?
        ;

attribute :( ~(LPAREN | RPAREN | COMMA)
                |  LPAREN attributeList RPAREN
                )*
        ;

compoundStatement[String scopeName] :LCURLY^

                            {
                                if(checksymbols) pushScope(scopeName);
                            }
                (       //this ambiguity is ok, declarationList and nestedFunctionDef end properly
                        options {
                            warnWhenFollowAmbig = false;
                        } :
                    ( "typedef" | "__label__" | declaration )=> declarationList
                    | (nestedFunctionDef)=> nestedFunctionDef
                )*
                ( statementList )?
                            { if(checksymbols) popScope(); }
                RCURLY
                            { ##.setType( NCompoundStatement ); ##.setAttribute( "scopeName", scopeName ); }
        ;

nestedFunctionDef { String declName; }
:( "auto" )? //only for nested functions
                ( (functionDeclSpecifiers)=> ds:functionDeclSpecifiers
                )?
                declName = d:declarator[false]
                            {
                            AST d2, ds2;
                            d2 = astFactory.dupList(#d);
                            ds2 = astFactory.dupList(#ds);
                            symbolTable.add(declName, #(null, ds2, d2));
                            if(checksymbols) pushScope(declName);
                            }
                ( declaration )*
                            { if(checksymbols) popScope(); }
                compoundStatement[declName]
                            { ## = #( #[NFunctionDef], ## );}
        ;

statementList :( statement )+
        ;

statement :SEMI                    // Empty statements
        
        |       compoundStatement[getAScopeName()] // Group of statements

        |       (annotation)* expr SEMI!               { ## = #( #[NStatementExpr], ## );} // Expressions

// Iteration statements:

        |       "while"^ LPAREN! expr RPAREN! statement
        |       "do"^ statement "while"! LPAREN! expr RPAREN! SEMI!
        |!       "for"
                LPAREN ( e1:expr )? SEMI ( e2:expr )? SEMI ( e3:expr )? RPAREN
                s:statement
                                    {
                                        if ( #e1 == null) { #e1 = #[ NEmptyExpression ]; }
                                        if ( #e2 == null) { #e2 = #[ NEmptyExpression ]; }
                                        if ( #e3 == null) { #e3 = #[ NEmptyExpression ]; }
                                        ## = #( #[LITERAL_for, "for"], #e1, #e2, #e3, #s );
                                    }


// Jump statements:

        |       "goto"^ expr SEMI!
        |       "continue" SEMI!
        |       "break" SEMI!
        |       "return"^ ( expr )? SEMI!


        |       ID COLON! (options {warnWhenFollowAmbig=false;}: statement)?  { ## = #( #[NLabel], ## ); }
// GNU allows range expressions in case statements
        |       "case"^ ((constExpr VARARGS)=> rangeExpr | constExpr) COLON! ( options{warnWhenFollowAmbig=false;}:statement )?
        |       "default"^ COLON! ( options{warnWhenFollowAmbig=false;}: statement )?

// Selection statements:

        |       "if"^
                 LPAREN! expr RPAREN! statement  
                ( //standard if-else ambiguity
                        options {
                            warnWhenFollowAmbig = false;
                        } :
                "else" statement )?
        |       "switch"^ LPAREN! expr RPAREN! statement
        ;

conditionalExpr :logicalOrExpr
                ( QUESTION^ (expr)? COLON conditionalExpr )?
        ;

rangeExpr :constExpr VARARGS constExpr
                                { ## = #(#[NRangeExpr], ##); }
        ;

castExpr :( LPAREN typeName RPAREN ) => //( LPAREN | unaryExpr | lcurlyInitializer) =>
                LPAREN^ typeName RPAREN ( castExpr | lcurlyInitializer )
                            { ##.setType(NCast); }

        |       unaryExpr
        ;

nonemptyAbstractDeclarator :(
                pointerGroup
                (   (LPAREN  
                    (   nonemptyAbstractDeclarator
                        | parameterTypeList
                    )?
                    ( COMMA! )?
                    RPAREN)
                | (LBRACKET (expr)? RBRACKET)
                )*

            |   (   (LPAREN  
                    (   nonemptyAbstractDeclarator
                        | parameterTypeList
                    )?
                    ( COMMA! )?
                    RPAREN)
                | (LBRACKET (expr)? RBRACKET)
                )+
            )
                            {   ## = #( #[NNonemptyAbstractDeclarator], ## ); }
                                
        ;

unaryExpr :postfixExpr
        |       INC^ castExpr
        |       DEC^ castExpr
        |       u:unaryOperator castExpr { ## = #( #[NUnaryExpr], ## ); }

        |       "sizeof"^
                ( ( LPAREN typeName )=> LPAREN typeName RPAREN
                | unaryExpr
                )
        |       "__alignof"^
                ( ( LPAREN typeName )=> LPAREN typeName RPAREN
                | unaryExpr
                )       
        |       gnuAsmExpr
        ;

unaryOperator :BAND
        |       STAR
        |       PLUS
        |       MINUS
        |       BNOT    //also stands for complex conjugation
        |       LNOT
        |       LAND    //for label dereference (&&label)
        |       "__real"
        |       "__imag"
        ;

gnuAsmExpr :"asm"^ ("volatile")? 
                LPAREN stringConst
                ( options { warnWhenFollowAmbig = false; }:
                  COLON (strOptExprPair ( COMMA strOptExprPair)* )?
                  ( options { warnWhenFollowAmbig = false; }:
                    COLON (strOptExprPair ( COMMA strOptExprPair)* )?
                  )?
                )?
                ( COLON stringConst ( COMMA stringConst)* )?
                RPAREN
                                { ##.setType(NGnuAsmExpr); }
        ;

strOptExprPair :stringConst ( LPAREN expr RPAREN )?
        ;

primaryExpr :ID
        |       Number
        |       charConst
        |       stringConst
// JTC:
// ID should catch the enumerator
// leaving it in gives ambiguous err
//      | enumerator
        |       (LPAREN LCURLY) => LPAREN^ compoundStatement[getAScopeName()] RPAREN
        |       LPAREN^ expr RPAREN        { ##.setType(NExpressionGroup); }
        ;

// inherited from grammar StdCParser
externalList :( externalDef )+
        ;

// inherited from grammar StdCParser
declSpecifiers { int specCount=0; }
:(               options { // this loop properly aborts when
                                          //  it finds a non-typedefName ID MBZ
                                          warnWhenFollowAmbig = false;
                                        } :
                  s:storageClassSpecifier
                | typeQualifier
                | ( "struct" | "union" | "enum" | typeSpecifier[specCount] )=>
                        specCount = typeSpecifier[specCount]
                )+
        ;

// inherited from grammar StdCParser
storageClassSpecifier :"auto"                  
        |       "register"              
        |       "typedef"               
        |       functionStorageClassSpecifier
        ;

// inherited from grammar StdCParser
typeQualifier :"const"
        |       "volatile"
        ;

// inherited from grammar StdCParser
typedefName :{ isTypedefName ( LT(1).getText() ) }?
                i:ID                    { ## = #(#[NTypedefName], #i); }
        ;

// inherited from grammar StdCParser
structOrUnion :"struct"
        |       "union"
        ;

// inherited from grammar StdCParser
structDeclarationList :( structDeclaration )+
        ;

// inherited from grammar StdCParser
specifierQualifierList { int specCount = 0; }
:(               options {   // this loop properly aborts when
                                            // it finds a non-typedefName ID MBZ
                                            warnWhenFollowAmbig = false;
                                        } :
                ( "struct" | "union" | "enum" | typeSpecifier[specCount]  )=>
                        specCount = typeSpecifier[specCount]
                | typeQualifier
                )+
        ;

// inherited from grammar StdCParser
enumerator[String enumName] :i:ID                { symbolTable.add(  i.getText(),
                                                        #(   null,
                                                            #[LITERAL_enum, "enum"],
                                                            #[ ID, enumName]
                                                         )
                                                     );
                                    }
                (ASSIGN constExpr)?
        ;

// inherited from grammar StdCParser
pointerGroup :( STAR ( typeQualifier )* )+    { ## = #( #[NPointerGroup], ##); }
        ;

// inherited from grammar StdCParser
parameterDeclaration { String declName; }
:ds:declSpecifiers
                ( ( declarator[false] )=> declName = d:declarator[false]
                            {
                            AST d2, ds2;
                            d2 = astFactory.dupList(#d);
                            ds2 = astFactory.dupList(#ds);
                            symbolTable.add(declName, #(null, ds2, d2));
							}
                | nonemptyAbstractDeclarator
                )?
                            {
                            ## = #( #[NParameterDeclaration], ## );
                            }
        ;

// inherited from grammar StdCParser
functionDeclSpecifiers { int specCount = 0; }
:(               options {   // this loop properly aborts when
                                            // it finds a non-typedefName ID MBZ
                                            warnWhenFollowAmbig = false;
                                        } :
                  functionStorageClassSpecifier
                | typeQualifier
                | ( "struct" | "union" | "enum" | typeSpecifier[specCount] )=>
                        specCount = typeSpecifier[specCount]
                )+
        ;

// inherited from grammar StdCParser
declarationPredictor :(options {      //only want to look at declaration if I don't see typedef
                    warnWhenFollowAmbig = false;
                }:
                "typedef"
                | declaration
                )
        ;

// inherited from grammar StdCParser
expr :assignExpr (options {
                                /* MBZ:
                                    COMMA is ambiguous between comma expressions and
                                    argument lists.  argExprList should get priority,
                                    and it does by being deeper in the expr rule tree
                                    and using (COMMA assignExpr)*
                                */
                                warnWhenFollowAmbig = false;
                            } :
                            c:COMMA^ { #c.setType(NCommaExpr); } assignExpr         
                            )*
        ;

// inherited from grammar StdCParser
assignExpr :conditionalExpr ( a:assignOperator! assignExpr { ## = #( #a, ## );} )?
        ;

// inherited from grammar StdCParser
assignOperator :ASSIGN
        |       DIV_ASSIGN
        |       PLUS_ASSIGN
        |       MINUS_ASSIGN
        |       STAR_ASSIGN
        |       MOD_ASSIGN
        |       RSHIFT_ASSIGN
        |       LSHIFT_ASSIGN
        |       BAND_ASSIGN
        |       BOR_ASSIGN
        |       BXOR_ASSIGN
        ;

// inherited from grammar StdCParser
constExpr :conditionalExpr
        ;

// inherited from grammar StdCParser
logicalOrExpr :logicalAndExpr ( LOR^ logicalAndExpr )*
        ;

// inherited from grammar StdCParser
logicalAndExpr :inclusiveOrExpr ( LAND^ inclusiveOrExpr )*
        ;

// inherited from grammar StdCParser
inclusiveOrExpr :exclusiveOrExpr ( BOR^ exclusiveOrExpr )*
        ;

// inherited from grammar StdCParser
exclusiveOrExpr :bitAndExpr ( BXOR^ bitAndExpr )*
        ;

// inherited from grammar StdCParser
bitAndExpr :equalityExpr ( BAND^ equalityExpr )*
        ;

// inherited from grammar StdCParser
equalityExpr :relationalExpr
                ( ( EQUAL^ | NOT_EQUAL^ ) relationalExpr )*
        ;

// inherited from grammar StdCParser
relationalExpr :shiftExpr
                ( ( LT^ | LTE^ | GT^ | GTE^ ) shiftExpr )*
        ;

// inherited from grammar StdCParser
shiftExpr :additiveExpr
                ( ( LSHIFT^ | RSHIFT^ ) additiveExpr )*
        ;

// inherited from grammar StdCParser
additiveExpr :multExpr
                ( ( PLUS^ | MINUS^ ) multExpr )*
        ;

// inherited from grammar StdCParser
multExpr :castExpr
                ( ( STAR^ | DIV^ | MOD^ ) castExpr )*
        ;

// inherited from grammar StdCParser
typeName :specifierQualifierList (nonemptyAbstractDeclarator)?
        ;

// inherited from grammar StdCParser
postfixExpr :primaryExpr
                ( 
                postfixSuffix                   {## = #( #[NPostfixExpr], ## );} 
                )?
        ;

// inherited from grammar StdCParser
postfixSuffix :( PTR ID
                | DOT ID
                | functionCall
                | LBRACKET expr RBRACKET
                | INC
                | DEC
                )+
        ;

// inherited from grammar StdCParser
functionCall :LPAREN^ (a:argExprList)? RPAREN
                        {
                        ##.setType( NFunctionCallArgs );
                        }
        ;

// inherited from grammar StdCParser
argExprList :assignExpr ( COMMA! assignExpr )*
        ;

// inherited from grammar StdCParser
protected charConst :CharLiteral
        ;

// inherited from grammar StdCParser
protected stringConst :(StringLiteral)+                { ## = #(#[NStringSeq], ##); }
        ;

// inherited from grammar StdCParser
protected intConst :IntOctalConst
        |       LongOctalConst
        |       UnsignedOctalConst
        |       IntIntConst
        |       LongIntConst
        |       UnsignedIntConst
        |       IntHexConst
        |       LongHexConst
        |       UnsignedHexConst
        ;

// inherited from grammar StdCParser
protected floatConst :FloatDoubleConst
        |       DoubleDoubleConst
        |       LongDoubleConst
        ;

// inherited from grammar StdCParser
dummy :NTypedefName
        |       NInitDecl
        |       NDeclarator
        |       NStructDeclarator
        |       NDeclaration
        |       NCast
        |       NPointerGroup
        |       NExpressionGroup
        |       NFunctionCallArgs
        |       NNonemptyAbstractDeclarator
        |       NInitializer
        |       NStatementExpr
        |       NEmptyExpression
        |       NParameterTypeList
        |       NFunctionDef
        |       NCompoundStatement
        |       NParameterDeclaration
        |       NCommaExpr
        |       NUnaryExpr
        |       NLabel
        |       NPostfixExpr
        |       NRangeExpr
        |       NStringSeq
        |       NInitializerElementLabel
        |       NLcurlyInitializer
        |       NAsmAttribute
        |       NGnuAsmExpr
        |       NTypeMissing
        ;

{
        //import CToken;
        import java.io.*;
        //import LineObject;
        import antlr.*;
}class GnuCLexer extends Lexer;

options {
	k= 3;
	importVocab= GnuC;
	testLiterals= false;
}

tokens {
        LITERAL___extension__ = "__extension__";
}
{
  public void initialize(String src)
  {
    setOriginalSource(src);
    initialize();
  }

  public void initialize() 
  {
    literals.put(new ANTLRHashString("__alignof__", this), new Integer(LITERAL___alignof));
    literals.put(new ANTLRHashString("__asm", this), new Integer(LITERAL_asm));
    literals.put(new ANTLRHashString("__asm__", this), new Integer(LITERAL_asm));
    literals.put(new ANTLRHashString("__attribute__", this), new Integer(LITERAL___attribute));
    literals.put(new ANTLRHashString("__complex__", this), new Integer(LITERAL___complex));
    literals.put(new ANTLRHashString("__const", this), new Integer(LITERAL_const));
    literals.put(new ANTLRHashString("__const__", this), new Integer(LITERAL_const));
    literals.put(new ANTLRHashString("__imag__", this), new Integer(LITERAL___imag));
    literals.put(new ANTLRHashString("__inline", this), new Integer(LITERAL_inline));
    literals.put(new ANTLRHashString("__inline__", this), new Integer(LITERAL_inline));
    literals.put(new ANTLRHashString("__real__", this), new Integer(LITERAL___real));
    literals.put(new ANTLRHashString("__signed", this), new Integer(LITERAL_signed));
    literals.put(new ANTLRHashString("__signed__", this), new Integer(LITERAL_signed));
    literals.put(new ANTLRHashString("__typeof", this), new Integer(LITERAL_typeof));
    literals.put(new ANTLRHashString("__typeof__", this), new Integer(LITERAL_typeof));
    literals.put(new ANTLRHashString("__volatile", this), new Integer(LITERAL_volatile));
    literals.put(new ANTLRHashString("__volatile__", this), new Integer(LITERAL_volatile));
	//literals.put(new ANTLRHashString("__extension__", this), new Integer(LITERAL_extension));
  }


  LineObject lineObject = new LineObject();
  String originalSource = "";
  PreprocessorInfoChannel preprocessorInfoChannel = new PreprocessorInfoChannel();
  int tokenNumber = 0;
  boolean countingTokens = true;
  int deferredLineCount = 0;

  public void setCountingTokens(boolean ct) 
  {
    countingTokens = ct;
    if ( countingTokens ) {
      tokenNumber = 0;
    }
    else {
      tokenNumber = 1;
    }
  }

  public void setOriginalSource(String src) 
  {
    originalSource = src;
    lineObject.setSource(src);
  }
  public void setSource(String src) 
  {
    lineObject.setSource(src);
  }
  
  public PreprocessorInfoChannel getPreprocessorInfoChannel() 
  {
    return preprocessorInfoChannel;
  }

  public void setPreprocessingDirective(String pre)
  {
    //System.out.println("Prepoc: " + pre);
    preprocessorInfoChannel.addLineForTokenNumber( pre, new Integer(tokenNumber) );
  }
  
  protected Token makeToken(int t)
  {
    if ( t != Token.SKIP && countingTokens) {
        tokenNumber++;
    }
    CToken tok = (CToken) super.makeToken(t);
    tok.setLine(lineObject.line);
    tok.setSource(lineObject.source);
    tok.setTokenNumber(tokenNumber);

    lineObject.line += deferredLineCount;
    deferredLineCount = 0;
    //System.out.println(lineObject.toString());
    return tok;
  }

    public void deferredNewline() { 
        deferredLineCount++;
    }

    public void newline() { 
        lineObject.newline();
    }






}
Whitespace :( ( ' ' | '\t' | '\014')
                | "\r\n"                { newline(); }
                | ( '\n' | '\r' )       { newline();    }
                )                       { _ttype = Token.SKIP;  }
        ;

protected Escape :'\\'
                ( options{warnWhenFollowAmbig=false;}: 
                  ~('0'..'7' | 'x')
                | ('0'..'3') ( options{warnWhenFollowAmbig=false;}: Digit )*
                | ('4'..'7') ( options{warnWhenFollowAmbig=false;}: Digit )*
                | 'x' ( options{warnWhenFollowAmbig=false;}: Digit | 'a'..'f' | 'A'..'F' )+
                )
        ;

protected IntSuffix :'L'
            | 'l'
            | 'U'
            | 'u'
            | 'I'
            | 'i'
            | 'J'
            | 'j'
        ;

protected NumberSuffix :IntSuffix
            | 'F'
            | 'f'
        ;

Number :( ( Digit )+ ( '.' | 'e' | 'E' ) )=> ( Digit )+
                ( '.' ( Digit )* ( Exponent )?
                | Exponent
                ) 
                ( NumberSuffix
                )*

        |       ( "..." )=> "..."       { _ttype = VARARGS;     }

        |       '.'                     { _ttype = DOT; }
                ( ( Digit )+ ( Exponent )?
                                        { _ttype = Number;   }
                    ( NumberSuffix
                    )*
                )?

        |       '0' ( '0'..'7' )*       
                ( NumberSuffix
                )*

        |       '1'..'9' ( Digit )*     
                ( NumberSuffix
                )*

        |       '0' ( 'x' | 'X' ) ( 'a'..'f' | 'A'..'F' | Digit )+
                ( IntSuffix
                )*
        ;

IDMEAT :i:ID                {
                                        
                                        if ( i.getType() == LITERAL___extension__ ) {
                                                $setType(Token.SKIP);
                                        }
                                        else {
                                                $setType(i.getType());
                                        }
                                        
                                    }
        ;

protected ID 
options {
	testLiterals= true;
}
:( 'a'..'z' | 'A'..'Z' | '_' | '$')
                ( 'a'..'z' | 'A'..'Z' | '_' | '$' | '0'..'9' )*
        ;

WideCharLiteral :'L' CharLiteral
                                { $setType(CharLiteral); }
        ;

WideStringLiteral :'L' StringLiteral
                                { $setType(StringLiteral); }
        ;

StringLiteral :'"'
                ( ('\\' ~('\n'))=> Escape
                | ( '\r'        { newline(); }
                  | '\n'        {
                                newline();
                                }
                  | '\\' '\n'   {
                                newline();
                                }
                  )
                | ~( '"' | '\r' | '\n' | '\\' )
                )*
                '"'
        ;

// inherited from grammar StdCLexer
protected Vocabulary :'\3'..'\377'
        ;

// inherited from grammar StdCLexer
ASSIGN :'=' ;

// inherited from grammar StdCLexer
COLON :':' ;

// inherited from grammar StdCLexer
COMMA :',' ;

// inherited from grammar StdCLexer
QUESTION :'?' ;

// inherited from grammar StdCLexer
SEMI :';' ;

// inherited from grammar StdCLexer
PTR :"->" ;

// inherited from grammar StdCLexer
protected DOT :;

// inherited from grammar StdCLexer
protected VARARGS :;

// inherited from grammar StdCLexer
LPAREN :'(' ;

// inherited from grammar StdCLexer
RPAREN :')' ;

// inherited from grammar StdCLexer
LBRACKET :'[' ;

// inherited from grammar StdCLexer
RBRACKET :']' ;

// inherited from grammar StdCLexer
LCURLY :'{' ;

// inherited from grammar StdCLexer
RCURLY :'}' ;

// inherited from grammar StdCLexer
EQUAL :"==" ;

// inherited from grammar StdCLexer
NOT_EQUAL :"!=" ;

// inherited from grammar StdCLexer
LTE :"<=" ;

// inherited from grammar StdCLexer
LT :"<" ;

// inherited from grammar StdCLexer
GTE :">=" ;

// inherited from grammar StdCLexer
GT :">" ;

// inherited from grammar StdCLexer
DIV :'/' ;

// inherited from grammar StdCLexer
DIV_ASSIGN :"/=" ;

// inherited from grammar StdCLexer
PLUS :'+' ;

// inherited from grammar StdCLexer
PLUS_ASSIGN :"+=" ;

// inherited from grammar StdCLexer
INC :"++" ;

// inherited from grammar StdCLexer
MINUS :'-' ;

// inherited from grammar StdCLexer
MINUS_ASSIGN :"-=" ;

// inherited from grammar StdCLexer
DEC :"--" ;

// inherited from grammar StdCLexer
STAR :'*' ;

// inherited from grammar StdCLexer
STAR_ASSIGN :"*=" ;

// inherited from grammar StdCLexer
MOD :'%' ;

// inherited from grammar StdCLexer
MOD_ASSIGN :"%=" ;

// inherited from grammar StdCLexer
RSHIFT :">>" ;

// inherited from grammar StdCLexer
RSHIFT_ASSIGN :">>=" ;

// inherited from grammar StdCLexer
LSHIFT :"<<" ;

// inherited from grammar StdCLexer
LSHIFT_ASSIGN :"<<=" ;

// inherited from grammar StdCLexer
LAND :"&&" ;

// inherited from grammar StdCLexer
LNOT :'!' ;

// inherited from grammar StdCLexer
LOR :"||" ;

// inherited from grammar StdCLexer
BAND :'&' ;

// inherited from grammar StdCLexer
BAND_ASSIGN :"&=" ;

// inherited from grammar StdCLexer
BNOT :'~' ;

// inherited from grammar StdCLexer
BOR :'|' ;

// inherited from grammar StdCLexer
BOR_ASSIGN :"|=" ;

// inherited from grammar StdCLexer
BXOR :'^' ;

// inherited from grammar StdCLexer
BXOR_ASSIGN :"^=" ;

// inherited from grammar StdCLexer
Comment :"/*"
                ( { LA(2) != '/' }? '*'
                | "\r\n"                { deferredNewline(); }
                | ( '\r' | '\n' )       { deferredNewline();    }
                | ~( '*'| '\r' | '\n' )
                )*
                "*/" {
                        setPreprocessingDirective(getText());
                        _ttype = Token.SKIP;  
                }
        ;

// inherited from grammar StdCLexer
CPPComment :"//" ( ~('\n') )* 
                        {
                        setPreprocessingDirective(getText());
                        _ttype = Token.SKIP;
                        }
        ;

// inherited from grammar StdCLexer
PREPROC_DIRECTIVE 
options {
	paraphrase= "a line directive";
}
:'#'
        ( ( "line" || (( ' ' | '\t' | '\014')+ '0'..'9')) => LineDirective      
            | (~'\n')*                                  { setPreprocessingDirective(getText()); }
        )
                {  
                    _ttype = Token.SKIP;
                }
        ;

// inherited from grammar StdCLexer
protected Space :( ' ' | '\t' | '\014')
        ;

// inherited from grammar StdCLexer
protected LineDirective {
        boolean oldCountingTokens = countingTokens;
        countingTokens = false;
}
:{
                        lineObject = new LineObject();
                        deferredLineCount = 0;
                }
        ("line")?  //this would be for if the directive started "#line", but not there for GNU directives
        (Space)+
        n:Number { lineObject.setLine(Integer.parseInt(n.getText())); } 
        (Space)+
        (       fn:StringLiteral {  try { 
                                          lineObject.setSource(fn.getText().substring(1,fn.getText().length()-1)); 
                                    } 
                                    catch (StringIndexOutOfBoundsException e) { /*not possible*/ } 
                                 }
                | fi:ID { lineObject.setSource(fi.getText()); }
        )?
        (Space)*
        ("1"            { lineObject.setEnteringFile(true); } )?
        (Space)*
        ("2"            { lineObject.setReturningToFile(true); } )?
        (Space)*
        ("3"            { lineObject.setSystemHeader(true); } )?
        (Space)*
        ("4"            { lineObject.setTreatAsC(true); } )?
        (~('\r' | '\n'))*
        ("\r\n" | "\r" | "\n")
                {
                        preprocessorInfoChannel.addLineForTokenNumber(new LineObject(lineObject), new Integer(tokenNumber));
                        countingTokens = oldCountingTokens;
                }
        ;

// inherited from grammar StdCLexer
CharLiteral :'\'' ( Escape | ~( '\'' ) ) '\''
        ;

// inherited from grammar StdCLexer
protected BadStringLiteral :// Imaginary token.
        ;

// inherited from grammar StdCLexer
protected Digit :'0'..'9'
        ;

// inherited from grammar StdCLexer
protected LongSuffix :'l'
        |       'L'
        ;

// inherited from grammar StdCLexer
protected UnsignedSuffix :'u'
        |       'U'
        ;

// inherited from grammar StdCLexer
protected FloatSuffix :'f'
        |       'F'
        ;

// inherited from grammar StdCLexer
protected Exponent :( 'e' | 'E' ) ( '+' | '-' )? ( Digit )+
        ;

// inherited from grammar StdCLexer
protected DoubleDoubleConst :;

// inherited from grammar StdCLexer
protected FloatDoubleConst :;

// inherited from grammar StdCLexer
protected LongDoubleConst :;

// inherited from grammar StdCLexer
protected IntOctalConst :;

// inherited from grammar StdCLexer
protected LongOctalConst :;

// inherited from grammar StdCLexer
protected UnsignedOctalConst :;

// inherited from grammar StdCLexer
protected IntIntConst :;

// inherited from grammar StdCLexer
protected LongIntConst :;

// inherited from grammar StdCLexer
protected UnsignedIntConst :;

// inherited from grammar StdCLexer
protected IntHexConst :;

// inherited from grammar StdCLexer
protected LongHexConst :;

// inherited from grammar StdCLexer
protected UnsignedHexConst :;


