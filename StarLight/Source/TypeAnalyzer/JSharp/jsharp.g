///////////////////////////////////////////////////////////////////////////
// JSharp parser (target language is CSharp)
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
 
using StringBuilder = System.Text.StringBuilder;
}
 
options {
	language  = CSharp;
	namespace = "Composestar.StarLight.TypeAnalyzer.JSharp";
}

class JSharpParser extends Parser;
options {
	k = 2;
	buildAST = true;
	exportVocab = JSharp;
	codeGenMakeSwitchThreshold = 2;
	codeGenBitsetTestThreshold = 3;
	defaultErrorHandler = false;
}

tokens {
	COMPILATION_UNIT;
	ATTRIBUTES; MODIFIERS; 
	QNAME;
	INSTANCEOF = "instanceof";
	BLOCK; CLASS_BODY; SLIST; CTOR_DEF; METHOD_DEF; VARIABLE_DEF;
	INSTANCE_INIT; STATIC_INIT; TYPE; CLASS_DEF; INTERFACE_DEF;
	PACKAGE_DEF; ARRAY_OF; EXTENDS_CLAUSE; IMPLEMENTS_CLAUSE;
	PARAMETERS; PARAMETER_DEF; LABELED_STAT; TYPECAST; INDEX_OP;
	POST_INC; POST_DEC; METHOD_CALL; 
	EXPR; ARRAY_INIT;
	IMPORT; UNARY_MINUS; UNARY_PLUS; 
	CASE_GROUP; CASE = "case"; 
	ELIST; FOR_INIT; FOR_CONDITION;
	FOR_ITERATOR; EMPTY_STAT; FINAL="final"; ABSTRACT="abstract";
	STRICTFP="strictfp"; 
	SUPER_CTOR_CALL; CTOR_CALL; 
	ATTR_START; ATTR; ATTR_NAME; ATTR_ARGUMENT;
	TRY = "try"; CATCH = "catch"; FINALLY = "finally";
}

{
	public AST dup(AST ast)
	{
		return getASTFactory().dupTree(ast);
	}
}

//////////////////////////////// COMPILATION UNIT ////////////////////////////////

// Compilation Unit: in Java this is a single file.
compilationUnit
	:	( packageDefinition )?
		( importDefinition )*
		( a:attributes! typeDefinition[#a] )*		
		EOF!
		{ ## = #([COMPILATION_UNIT,"COMPILATION_UNIT"], ##); }
	;
	
// Package statement: "package" followed by an identifier.
packageDefinition!
	options {defaultErrorHandler = true;}
	:	"package" q:qname SEMI!
		{ ## = #([PACKAGE_DEF, "PACKAGE_DEF"], q); }
	;

// Import statement: import followed by a package or class name.
importDefinition!
	options {defaultErrorHandler = true;}
	:	"import" q:qname SEMI!
		{ ## = #([IMPORT, "IMPORT"], q); }
	;

// A type definition in a file is either a class or interface definition.
typeDefinition[AST atts]
	options {defaultErrorHandler = true;}
	:	mods:modifiers!
		( classDefinition[#atts,#mods]
		| interfaceDefinition[#atts,#mods]
		)
	|	SEMI!
	;

// A declaration is the creation of a reference or primitive-type variable.
// Create a separate Type/Var tree for each var in the var list.
declaration!
	:	m:modifiers t:typeName[false] v:variableDefinitions[#m,#t]
		{ ## = #v; }
	;

// A type name has possible brackets (which would make it an array type).
typeName[bool addNode]
	:	classTypeName[addNode]
	|	primitiveTypeName[addNode]
	;

// A class type specification is a class type with possible brackets 
// afterwards (which would make it an array type).
classTypeName[bool addNode]
	:	qname
		(	options { greedy=true; }:
			LBRACK! RBRACK!
			{ ## = #([ARRAY_OF,"ARRAY_OF"], ##); }
		)*
		{ if (addNode) ## = #([TYPE,"TYPE"], ##); }
	;

// A builtin type specification is a builtin type with possible brackets
// afterwards (which would make it an array type).
primitiveTypeName[bool addNode]
	:	(	"void"
		|	"boolean"
		|	"byte"
		|	"ubyte"
		|	"char"
		|	"short"
		|	"int"
		|	"long"
		|	"float"
		|	"double"
		)
		(	options { greedy=true; }:
			LBRACK! RBRACK!
			{ ## = #([ARRAY_OF,"ARRAY_OF"], ##); }
		)*
		{ if (addNode) ## = #([TYPE,"TYPE"], ##); }
	;

// A (possibly-qualified) java identifier.  
qname!
	{ StringBuilder sb = new StringBuilder(); }
	:	i:IDENT { sb.Append(i.getText()); }
		(	options { greedy=true; }:
			DOT j:IDENT
			{ sb.Append('.').Append(j.getText()); }
		)*
		(	options { greedy=true; }:
			DOT STAR
			{ sb.Append(".*"); }
		)?
		{ ## = #[QNAME,sb.ToString()]; }
	;
	
//////////////////////////////// ATTRIBUTES ////////////////////////////////

attributes
	:	(attribute_section)*
		{ ## = #([ATTRIBUTES,"ATTRIBUTES"], ##); }
	;

attribute_section
	:	ATTR_START! (attribute) ML_COMMENT_END!
	;

attribute!
	:	name:attributeName arguments:attributeArguments
		{ ## = #([ATTR,"ATTR"],name,arguments); }
	;

attributeName!
	:	id:qname  
		{ ## = #([ATTR_NAME,"ATTR_NAME"], id); }
	;
	
attributeArguments
	:	LPAREN! (positionalArgumentList)? RPAREN!
	;

positionalArgumentList
	:	positionalArgument (COMMA! positionalArgument)* 
	;

positionalArgument
	:	attributeArgumentExpression
	;
	
attributeArgumentExpression!
	: 	c:STRING_LITERAL
		{ #attributeArgumentExpression = #([ATTR_ARGUMENT, "ATTR_ARGUMENT"], c);}
	;

//////////////////////////////// MODIFIERS ////////////////////////////////

// A list of zero or more modifiers.  We could have used (modifier)* in
// place of a call to modifiers, but I thought it was a good idea to keep
// this rule separate so they can easily be collected in a List if
// someone so desires
modifiers
	:	( modifier )*
		{ ## = #([MODIFIERS, "MODIFIERS"], ##); }
	;

// modifiers for Java classes, interfaces, class/instance vars and methods
modifier
	:	"private"
	|	"public"
	|	"protected"
	|	"static"
	|	"transient"
	|	"final"
	|	"abstract"
	|	"native"
	|	"threadsafe"
	|	"synchronized"
	|	"const"			// reserved word, but not valid
	|	"volatile"
	|	"strictfp"
	;
	
//////////////////////////////// TYPE DEFINITION ////////////////////////////////

classDefinition![AST atts, AST mods]
	{ int endPos; }
	:	"class" n:IDENT s:superClassClause i:implementsClause endPos=b:classBody
		{
			## = #([CLASS_DEF,"CLASS_DEF",PosAST.ClassName],atts,mods,n,s,i,b);
			((PosAST)##).setEndPos(endPos);
		}
	;

// Definition of a Java interface
interfaceDefinition![AST atts, AST mods]
	{ int endPos; }
	:	"interface" n:IDENT e:interfaceExtends endPos=b:classBody
		{
			## = #([INTERFACE_DEF,"INTERFACE_DEF",PosAST.ClassName],atts,mods,n,e,b); 
			((PosAST)##).setEndPos(endPos);
		}
	;

superClassClause!
	:	( "extends" n:qname )?
		{ ## = #([EXTENDS_CLAUSE,"EXTENDS_CLAUSE"],n); }
	;

// An interface can extend several other interfaces...
interfaceExtends
	:	("extends"! qname (COMMA! qname)*)?
		{ ## = #([EXTENDS_CLAUSE,"EXTENDS_CLAUSE"], ##); }
	;

// A class can implement several interfaces...
implementsClause
	:	("implements"! qname (COMMA! qname)*)?
		{ ## = #([IMPLEMENTS_CLAUSE,"IMPLEMENTS_CLAUSE"], ##); }
	;

// This is the body of a class.  You can have classMembers and extra semicolons.
classBody returns [int endPos = -1]
	:	LCURLY! (classMember | SEMI!)* end:RCURLY!
		{ ## = #([CLASS_BODY], ##); endPos = ((PosToken)end).getPosition(); }
	;

//////////////////////////////// TYPE BODY ////////////////////////////////

// Now the various things that can be defined inside a class or interface...
// Note that not all of these are really valid in an interface (constructors,
// for example), and if this grammar were used for a compiler there would
// need to be some semantic checks.
classMember!
	:	atts:attributes mods:modifiers
		(	h:constructorHead s:constructorBody   // constructor
			{ ## = #([CTOR_DEF,"CTOR_DEF"], mods, h, s); }

		|	cd:classDefinition[#atts,#mods]       // inner class
			{ ## = #cd; }

		|	id:interfaceDefinition[#atts,#mods]   // inner interface
			{ ## = #id; }

		|	t:typeName[false]  // method or variable declaration(s)
			(	IDENT
				LPAREN! param:parameterDeclarationList RPAREN!
				rt:arrayOf[#t]
				(tc:throwsClause)?
				(s2:compoundStatement | SEMI)
				{ ## = #([METHOD_DEF,"METHOD_DEF"], atts, mods, #([TYPE,"TYPE"],rt), IDENT, param, tc, s2); }
			|	v:variableDefinitions[#mods,#t] SEMI
				{ ## = #v; }
			)
		)

	// "static { ... }" class initializer
	|	"static" s3:compoundStatement
		{ ## = #([STATIC_INIT,"STATIC_INIT"], s3); }

	// "{ ... }" instance initializer
	|	s4:compoundStatement
		{ ## = #([INSTANCE_INIT,"INSTANCE_INIT"], s4); }
	;

// This is the header of a method.  It includes the name and parameters
// for the method.
//   This also watches for a list of exception classes in a "throws" clause.
constructorHead
	:	IDENT
		LPAREN! parameterDeclarationList RPAREN!
		(throwsClause)?
	;

constructorBody
	:	LCURLY!
		(	options { greedy=true; }: 
			explicitConstructorInvocation
		)?
		(statement)*
        RCURLY!
        { ## = #([SLIST,"SLIST"], ##); }
	;

// Catch obvious constructor calls, but not the expr.super(...) calls
explicitConstructorInvocation
    :   "this"! LPAREN! argList RPAREN! SEMI!
		{ ## = #([CTOR_CALL,"CTOR_CALL"], ##); }
    |   "super"! LPAREN! argList RPAREN! SEMI!
		{ ## = #([SUPER_CTOR_CALL,"SUPER_CTOR_CALL"], ##); }
    ;

variableDefinitions[AST mods, AST atts]
	:	variableDeclarator[dup(mods),dup(atts)]
		(	COMMA!
			variableDeclarator[dup(mods),dup(atts)]
		)*
	;

// Declaration of a class/instance/local variable.  
// It can also include possible initialization.
variableDeclarator![AST mods, AST typ]
	:	id:IDENT d:arrayOf[typ] v:varInitializer
		{ ## = #([VARIABLE_DEF,"VARIABLE_DEF"], mods, #([TYPE,"TYPE"],d), id, v);}
	;

arrayOf[AST typ]
	:	{ ## = typ; }
		(	LBRACK! RBRACK!
			{ ## = #([ARRAY_OF,"ARRAY_OF"], ##); }
		)*
	;

varInitializer
	:	( ASSIGN^ initializer )?
	;

// This is an initializer used to set up an array.
arrayInitializer
	:	lc:LCURLY^ {#lc.setType(ARRAY_INIT);}
		(	initializer
			(
				// CONFLICT: does a COMMA after an initializer start a new
				//           initializer or start the option ',' at end?
				//           ANTLR generates proper code by matching
				//			 the comma as soon as possible.
				options { warnWhenFollowAmbig = false; }:
				COMMA! initializer
			)*
			(COMMA!)?
		)?
		RCURLY!
	;


// The two "things" that can initialize an array element are an expression
// and another (nested) array initializer.
initializer
	:	expression
	|	arrayInitializer
	;

// This is a list of exception classes that the method is declared to throw
throwsClause
	:	"throws"^ qname ( COMMA! qname )*
	;

// A list of formal parameters
parameterDeclarationList
	:	( parameterDeclaration ( COMMA! parameterDeclaration )* )?
		{ ## = #([PARAMETERS,"PARAMETERS"], ##); }
	;

// A formal parameter.
parameterDeclaration!
	:	pm:parameterModifier t:typeName[false] id:IDENT pd:arrayOf[#t]
		{ ## = #([PARAMETER_DEF,"PARAMETER_DEF"], pm, #([TYPE,"TYPE"],pd), id); }
	;

parameterModifier!
	:	(f:"final")?
		{ ## = #([MODIFIERS,"MODIFIERS"], f); }
	;

// Compound statement.  This is used in many contexts:
//   Inside a class definition prefixed with "static":
//      it is a class initializer
//   Inside a class definition without "static":
//      it is an instance initializer
//   As the body of a method
//   As a completely indepdent braced block of code inside a method
//      it starts a new scope for variable definitions

compoundStatement
	:	LCURLY! (statement)* RCURLY!
        { ## = #([SLIST,"SLIST"], ##); }
	;

statement
	// A list of statements in curly braces -- start a new scope!
	:	compoundStatement

	// declarations are ambiguous with "ID DOT" relative to expression
	// statements.  Must backtrack to be sure.  Could use a semantic
	// predicate to test symbol table to see what the type was coming
	// up, but that's pretty hard without a symbol table ;)
	|	(declaration)=> declaration SEMI!

	// An expression statement.  This could be a method call,
	// assignment statement, or any other expression evaluated for
	// side-effects.
	|	expression SEMI!

	// class definition
	|	m:modifiers! classDefinition[null,#m]

	// Attach a label to the front of a statement
	|	IDENT COLON! statement
		{ ## = #([LABELED_STAT,"LABELED_STAT"], ##); }

	// If-else statement
	|	"if"^ LPAREN! expression RPAREN! statement
		(
			// CONFLICT: the old "dangling-else" problem...
			//           ANTLR generates proper code matching
			//			 as soon as possible.  Hush warning.
			options { warnWhenFollowAmbig = false; }:
			"else"! statement
		)?

	// For statement
	|	"for"^
			LPAREN!
				forInit SEMI!   // initializer
				forCond	SEMI!   // condition test
				forIter         // updater
			RPAREN!
			statement                     // statement to loop over

	// While statement
	|	"while"^ LPAREN! expression RPAREN! statement

	// do-while statement
	|	"do"^ statement "while"! LPAREN! expression RPAREN! SEMI!

	// get out of a loop (or switch)
	|	"break"^ (IDENT)? SEMI!

	// do next iteration of a loop
	|	"continue"^ (IDENT)? SEMI!

	// Return an expression
	|	"return"^ (expression)? SEMI!

	// switch/case statement
	|	"switch"^ LPAREN! expression RPAREN! LCURLY! (casesGroup)* RCURLY!

	// exception try-catch block
	|	tryBlock

	// throw an exception
	|	"throw"^ expression SEMI!

	// synchronize a statement
	|	"synchronized"^ LPAREN! expression RPAREN! compoundStatement

	// empty statement
	|	SEMI! { ##.setType(EMPTY_STAT); }
	;

casesGroup
	:	(	// CONFLICT: to which case group do the statements bind?
			//           ANTLR generates proper code: it groups the
			//           many "case"/"default" labels together then
			//           follows them with the statements
			options { greedy=true; }:
			aCase
		)+
		caseSList
		{ ## = #([CASE_GROUP, "CASE_GROUP"], ##); }
	;

aCase
	:	(CASE^ expression | "default") COLON!
	;

caseSList
	:	(statement)*
		{ ## = #([SLIST,"SLIST"], ##);}
	;

// The initializer for a for loop
forInit
	:	(	(declaration)=> declaration	// if it looks like a declaration, it is			
		|	expressionList				// otherwise it could be an expression list...
		)?
		{ ## = #([FOR_INIT,"FOR_INIT"], ##);}
	;

forCond
	:	(expression)?
		{ ## = #([FOR_CONDITION,"FOR_CONDITION"], ##);}
	;

forIter
	:	(expressionList)?
		{ ## = #([FOR_ITERATOR,"FOR_ITERATOR"], ##);}
	;

// an exception handler try/catch block
tryBlock
	:	TRY^ compoundStatement
		(handler)*
		(finallyClause)?
	;

finallyClause
	:	FINALLY^ compoundStatement
	;

// an exception handler
handler
	:	CATCH^ LPAREN! parameterDeclaration RPAREN! compoundStatement
	;


//////////////////////////////// EXPRESSIONS ////////////////////////////////

// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The operators in java have the following precedences:
//    lowest  (13)  = *= /= %= += -= <<= >>= >>>= &= ^= |=
//            (12)  ?:
//            (11)  ||
//            (10)  &&
//            ( 9)  |
//            ( 8)  ^
//            ( 7)  &
//            ( 6)  == !=
//            ( 5)  < <= > >=
//            ( 4)  << >>
//            ( 3)  +(binary) -(binary)
//            ( 2)  * / %
//            ( 1)  ++ -- +(unary) -(unary)  ~  !  (type)
//                  []   () (method call)  . (dot -- identifier qualification)
//                  new   ()  (explicit parenthesis)
//
// the last two are not usually on a precedence chart; I put them in
// to point out that new has a higher precedence than '.', so you
// can validy use new Frame().show().
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
// is usually very straightfoward.

// the mother of all expressions
expression
	:	assignmentExpression
		{ ## = #([EXPR,"EXPR"],##); }
	;

// This is a list of expressions.
expressionList
	:	expression (COMMA! expression)*
		{ ## = #([ELIST,"ELIST"], ##); }
	;

// assignment expression (level 13)
assignmentExpression
	:	conditionalExpression
		(	(	ASSIGN^
            |   PLUS_ASSIGN^
            |   MINUS_ASSIGN^
            |   STAR_ASSIGN^
            |   DIV_ASSIGN^
            |   MOD_ASSIGN^
            |   SR_ASSIGN^
            |   BSR_ASSIGN^
            |   SL_ASSIGN^
            |   BAND_ASSIGN^
            |   BXOR_ASSIGN^
            |   BOR_ASSIGN^
            )
			assignmentExpression
		)?
	;

// conditional expression (level 12)
conditionalExpression
	:	logicalOrExpression
		( QUESTION^ assignmentExpression COLON! conditionalExpression )?
	;

// logical or (||)  (level 11)
logicalOrExpression
	:	logicalAndExpression (LOR^ logicalAndExpression)*
	;

// logical and (&&)  (level 10)
logicalAndExpression
	:	inclusiveOrExpression (LAND^ inclusiveOrExpression)*
	;

// bitwise or non-short-circuiting or (|)  (level 9)
inclusiveOrExpression
	:	exclusiveOrExpression (BOR^ exclusiveOrExpression)*
	;

// exclusive or (^)  (level 8)
exclusiveOrExpression
	:	andExpression (BXOR^ andExpression)*
	;

// bitwise or non-short-circuiting and (&)  (level 7)
andExpression
	:	equalityExpression (BAND^ equalityExpression)*
	;

// equality/inequality (==/!=) (level 6)
equalityExpression
	:	relationalExpression ((NOT_EQUAL^ | EQUAL^) relationalExpression)*
	;

// boolean relational expressions (level 5)
relationalExpression
	:	shiftExpression
		(	(	(	LTH^
				|	GT^
				|	LE^
				|	GE^
				)
				shiftExpression
			)*
		|	INSTANCEOF^ typeName[true]
		)
	;

// bit shift expressions (level 4)
shiftExpression
	:	additiveExpression ((SL^ | SR^ | BSR^) additiveExpression)*
	;

// binary addition/subtraction (level 3)
additiveExpression
	:	multiplicativeExpression ((PLUS^ | MINUS^) multiplicativeExpression)*
	;

// multiplication/division/modulo (level 2)
multiplicativeExpression
	:	unaryExpression ((STAR^ | DIV^ | MOD^ ) unaryExpression)*
	;

unaryExpression
	:	INC^ unaryExpression
	|	DEC^ unaryExpression
	|	MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
	|	PLUS^  {#PLUS.setType(UNARY_PLUS);} unaryExpression
	|	unaryExpressionNotPlusMinus
	;

unaryExpressionNotPlusMinus
	:	BNOT^ unaryExpression
	|	LNOT^ unaryExpression

		// use predicate to skip cases like (int.class)
    |   (LPAREN primitiveTypeName[false] RPAREN)=>
        LPAREN! primitiveTypeName[true] RPAREN! unaryExpression
        { ## = #([TYPECAST,"TYPECAST"], ##); }

        // Have to backtrack to see if operator follows.  If no operator
        // follows, it's a typecast.  No semantic checking needed to parse.
        // if it _looks_ like a cast, it _is_ a cast; else it's a "(expr)"
    |	(LPAREN classTypeName[false] RPAREN unaryExpressionNotPlusMinus)=>
        LPAREN! classTypeName[true] RPAREN! unaryExpressionNotPlusMinus
        { ## = #([TYPECAST,"TYPECAST"], ##); }

    |	postfixExpression
	;

// qualified names, array expressions, method invocation, post inc/dec
postfixExpression
	:
    /*
    "this"! lp1:LPAREN^ argList RPAREN!
		{#lp1.setType(CTOR_CALL);}

    |   "super"! lp2:LPAREN^ argList RPAREN!
		{#lp2.setType(SUPER_CTOR_CALL);}
    |
    */
        primaryExpression
		(
            /*
            options {
				// the use of postfixExpression in SUPER_CTOR_CALL adds DOT
				// to the lookahead set, and gives loads of false non-det
				// warnings.
				// shut them off.
				generateAmbigWarnings=false;
			}
		:	*/
            DOT^ IDENT
			(	lp:LPAREN^ {#lp.setType(METHOD_CALL);}
				argList
				RPAREN!
			)?
		|	DOT^ "this"

		|	DOT^ "super"
            (   // (new Outer()).super()  (create enclosing instance)
                lp3:LPAREN^ argList RPAREN!
                {#lp3.setType(SUPER_CTOR_CALL);}
			|   DOT^ IDENT
                (	lps:LPAREN^ {#lps.setType(METHOD_CALL);}
                    argList
                    RPAREN!
                )?
            )
		|	DOT^ newExpression
		|	lb:LBRACK^ {#lb.setType(INDEX_OP);} expression RBRACK!
		)*

		(   // possibly add on a post-increment or post-decrement.
            // allows INC/DEC on too much, but semantics can check
			inc:INC^ { #inc.setType(POST_INC); }
	 	|	dec:DEC^ { #dec.setType(POST_DEC); }
		)?
 	;

// the basic element of an expression
primaryExpression
	:	identPrimary ( options { greedy=true; }: DOT^ "class" )?
    |   constant
	|	"true"
	|	"false"
	|	"null"
    |   newExpression
	|	"this"
	|	"super"
	|	LPAREN! assignmentExpression RPAREN!		
	|	primitiveTypeName[false] DOT^ "class"
	;

// Match a, a.b.c refs, a.b.c(...) refs, a.b.c[], a.b.c[].class,
// and a.b.c.class refs.  Also this(...) and super(...).  Match
// this or super.
identPrimary
	:	IDENT
		(
			options {
				// .ident could match here or in postfixExpression.
				// We do want to match here.  Turn off warning.
				greedy=true;
			}
		:	DOT^ IDENT
		)*
		(
			// ARRAY_OF here conflicts with INDEX_OP in
			// postfixExpression on LBRACK RBRACK.
			// We want to match [] here, so greedy.  This overcomes
			// limitation of linear approximate lookahead.
			options { greedy=true; }:
			( lp:LPAREN^ {#lp.setType(METHOD_CALL);} argList RPAREN! )
		|	(	options { greedy=true; }:
				lbc:LBRACK^ {#lbc.setType(ARRAY_OF);} RBRACK!
			)+
		)?
	;

/** object instantiation.
 *  Trees are built as illustrated by the following input/tree pairs:
 *
 *  new T()
 *
 *  new
 *   |
 *   T --  ELIST
 *           |
 *          arg1 -- arg2 -- .. -- argn
 *
 *  new int[]
 *
 *  new
 *   |
 *  int -- ARRAY_OF
 *
 *  new int[] {1,2}
 *
 *  new
 *   |
 *  int -- ARRAY_OF -- ARRAY_INIT
 *                                  |
 *                                EXPR -- EXPR
 *                                  |      |
 *                                  1      2
 *
 *  new int[3]
 *  new
 *   |
 *  int -- ARRAY_OF
 *                |
 *              EXPR
 *                |
 *                3
 *
 *  new int[1][2]
 *
 *  new
 *   |
 *  int -- ARRAY_OF
 *               |
 *         ARRAY_OF -- EXPR
 *               |              |
 *             EXPR             1
 *               |
 *               2
 *
 */
newExpression
	{ int endPos; }
	:	"new"^ typeName[true]
		(	LPAREN! argList RPAREN! (endPos=classBody)?
		|	newArrayDeclarator (arrayInitializer)?
		)
	;

argList
	:	(	expressionList
		|	/*nothing*/
			{ ## = #[ELIST,"ELIST"]; }
		)
	;

newArrayDeclarator
	:	(
			// CONFLICT:
			// newExpression is a primaryExpression which can be
			// followed by an array index reference.  This is ok,
			// as the generated code will stay in this loop as
			// long as it sees an LBRACK (proper behavior)
			options { warnWhenFollowAmbig = false; } :
			lb:LBRACK^ {#lb.setType(ARRAY_OF);}
				(expression)?
			RBRACK!
		)+
	;

constant
	:	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	|	NUM_LONG
	|	NUM_DOUBLE
	;

//----------------------------------------------------------------------------
// The JSharp lexer
//----------------------------------------------------------------------------
class JSharpLexer extends Lexer;

options {
	exportVocab = JSharp;
	testLiterals = false;
	k = 4;
	charVocabulary='\u0003'..'\uFFFF';
	
	// Cannot do Unicode without inlining some bitset tests.
	// I need to make ANTLR generate smaller bitsets; see bottom of JavaLexer.java
	codeGenBitsetTestThreshold = 20;
}

// OPERATORS
QUESTION		:	'?'		;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
LCURLY			:	'{'		;
RCURLY			:	'}'		;
COLON			:	':'		;
COMMA			:	','		;
//DOT			:	'.'		;
ASSIGN			:	'='		;
EQUAL			:	"=="	;
LNOT			:	'!'		;
BNOT			:	'~'		;
NOT_EQUAL		:	"!="	;
DIV				:	'/'		;
DIV_ASSIGN		:	"/="	;
PLUS			:	'+'		;
PLUS_ASSIGN		:	"+="	;
INC				:	"++"	;
MINUS			:	'-'		;
MINUS_ASSIGN	:	"-="	;
DEC				:	"--"	;
STAR			:	'*'		;
STAR_ASSIGN		:	"*="	;
MOD				:	'%'		;
MOD_ASSIGN		:	"%="	;
SR				:	">>"	;
SR_ASSIGN		:	">>="	;
BSR				:	">>>"	;
BSR_ASSIGN		:	">>>="	;	// requires k=4
GE				:	">="	;
GT				:	">"		;
SL				:	"<<"	;
SL_ASSIGN		:	"<<="	;
LE				:	"<="	;
LTH				:	'<'		;
BXOR			:	'^'		;
BXOR_ASSIGN		:	"^="	;
BOR				:	'|'		;
BOR_ASSIGN		:	"|="	;
LOR				:	"||"	;
BAND			:	'&'		;
BAND_ASSIGN		:	"&="	;
LAND			:	"&&"	;
SEMI			:	';'		;

// Whitespace -- ignored
protected NEWLINE
	:	(	("\r\n")=> "\r\n"	//DOS
		|	'\r'				//Macintosh
		|	'\n'				//Unix
		)
		{ newline(); }
	;

WS	:	(	' '
		|	'\t'
		|	'\f'
		|	NEWLINE
		)+
		{ $setType(Token.SKIP); }
	;

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{ $setType(Token.SKIP); newline(); }
	;

// Multi-line comments

ML_COMMENT_START
	:	"/*"
	;
	
ML_COMMENT_END
	:	"*/"
	;

ML_COMMENT
	:	ML_COMMENT_START
		(	("* @attribute")=> "* @attribute"
			{ $setType(ATTR_START); }
		|	(	{ LA(2) != '/' }? '*'	// '*' if not followed by '/' 
			|	NEWLINE					// newlines
			|	~('*'|'\n'|'\r')		// everything else
			)*
			ML_COMMENT_END
			{ $setType(Token.SKIP); }
		)
	;

//pre-processing directives
PREPRO_DIRECTIVES
	:	"#" (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{ $setType(Token.SKIP); newline(); } //skip directives
	;

// character literals
CHAR_LITERAL
	:	'\'' ( ESC | ~('\''|'\n'|'\r'|'\\') ) '\''
	;

// string literals
STRING_LITERAL
	:	'"' (ESC|~('"'|'\\'|'\n'|'\r'))* '"'
	;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)
	;

// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;

// a dummy rule to force vocabulary to be all characters (except special
//   ones that ANTLR uses internally (0 to 2)
protected
VOCAB
	:	'\3'..'\377'
	;

// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
IDENT
	options { testLiterals=true; }
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
	;

// a numeric literal
NUM_INT
	{ bool isDecimal = false; IToken t = null; }
	:	'.' { $setType(DOT); }
		(	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX { t = f1; })?
			{
				if (t != null && t.getText().ToUpper().IndexOf('F') >= 0) {
					$setType(NUM_FLOAT);
				}
				else {
					$setType(NUM_DOUBLE); // assume double
				}
			}
		)?
	|	(	'0' { isDecimal = true; } // special case for just '0'
			(	('x'|'X')
				(	// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options { warnWhenFollowAmbig=false; }:
					HEX_DIGIT
				)+
			|	(('0'..'9')+ ('.'|EXPONENT|FLOAT_SUFFIX)) => ('0'..'9')+ //float or double with leading zero
			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  { isDecimal = true; }	// non-zero decimal
		)
		(	('l'|'L') { _ttype = NUM_LONG; }

		// only check to see if it's a float if looks like decimal so far
		|	{isDecimal}?
			(	'.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
			|	EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
			|	f4:FLOAT_SUFFIX {t=f4;}
			)
			{
				if (t != null && t.getText().ToUpper().IndexOf('F') >= 0) {
					_ttype = NUM_FLOAT;
				}
				else {
					_ttype = NUM_DOUBLE; // assume double
				}
			}
		)?
	;

// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e'|'E') ('+'|'-')? ('0'..'9')+
	;

protected
FLOAT_SUFFIX
	:	'f'|'F'|'d'|'D'
	;

