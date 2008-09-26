//
// @Warning@
//

/*
 * Parser (Tree generator) and Lexer specification for the Compose* language
 * $Id$
 *
 * Grammar Changes:
 * (2007-10-05) michielh	Dropped single target.selector which implies
 *				signature matching (too confusing).
 * (2007-10-08) michielh	Added the filter module parameter
 * (2007-10-09) michielh	Renamed magic tokens to prevent naming collision 
 *				with types.
 * (2007-10-10) michielh	Forgot filter element operators. Added filter 
 *				type arguments. MatchingPart is now either a
 *				list, message list or single entry. No longer
 *				a list of message lists. FM Param list not
 *				allowed in selector of subst parts.
 * (2007-10-11) michielh	Added legacy selector operator to AST (=, :).
 *				Added PARAM token to a single FM parameter in
 *				the params rule, needed for walker.
 * (2007-10-12) michielh	CodeBlock now included in the tree. Allowed naked
 *				target.selector sign matching, but generate a 
 *				warning.
 * (2007-10-15) michielh	Constraints are no longer hardcoded
 * (2008-02-25) michielh	Added required check for ending } and EOF
 * (2008-05-29) michielh	Made the grammar even more target neutral
 * (2008-09-17) michielh	COPPER3, various changes to the grammer to better
 *				suit the new repository model and canonical filter notation.
 */
grammar Cps;

options {
	k = 1;
	output = AST;
	superClass = CpsParserBase;
	language = @TargetLanguage@;
}

// Magic tokes used for tree construction
tokens {
	CONCERN;	
	FQN;
	PARAM;
	IN;
	
	FILTER_MODULE;
	PARAMS;
	FM_PARAM_SINGLE;
	FM_PARAM_LIST;	
	INTERNAL;
	NAMES;
	EXTERNAL;
	INIT;
	CONDITION;
	INPUT_FILTERS;
	OUTPUT_FILTERS;	
	FILTER;
	FILTER_ELEMENT;
	EXPRESSION;
	OPERATOR;
	OPERAND;
	CMPSTMT;
	JPCA;
	
	SUPERIMPOSITION;
	SELECTOR;
	LEGACY_SELECTOR;
	PREDICATE_SELECTOR;
	PROLOG_EXPR;
	FM_BINDINGS;
	BINDING;
	ANNOTATION_BINDINGS;
	CONSTRAINT;
	LIST;
	
	IMPLEMENTATION;
	CODE_BLOCK;
}

@header {
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 */
 
@Java@package Composestar.Core.COPPER3;
}

@lexer::header {
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 */
 
@Java@package Composestar.Core.COPPER3;
}

// Namespace for C# targets
@lexer::namespace {Composestar.StarLight.CpsParser}
@parser::namespace {Composestar.StarLight.CpsParser}


/**
 * Start of a CPS source file. It will always start with a concern declaration.
 * This rule contains a partial hack for the implementation part. The implementation
 * rule will eat all remaining tokens therefore a concern will either end with an RCURCLY
 * or an implementation rule.
 */
concern
	: 'concern' IDENTIFIER concernParameters? (inToken='in' fqn)? LCURLY filtermodule* superimposition? implementation? RCURLY EOF
	-> ^(CONCERN[$start] IDENTIFIER concernParameters? ^(IN[$inToken] fqn)? filtermodule* superimposition? implementation?)
	;

/**
 * A generic fully qualified name.
 */
fqn
	: IDENTIFIER (PERIOD IDENTIFIER)*
	-> ^(FQN[$start] IDENTIFIER+)
	;	
	
/**
 * Formal concern parameters. Similar to a class constructor.
 */	
concernParameters
	: LROUND IDENTIFIER COLON fqn (SEMICOLON IDENTIFIER COLON fqn)* RROUND
	{
		warning("Concern parameters are deprecated.", $start);
	}
	-> // delete all tokens
	;	
	
// $<Filter Module

/**
 * A filter module. A concern can contain multiple filtermodule declarations. 
 * The filtermodule will contain a list of internals/externals/conditions and an inputfilter
 * and outputfilter child node. The internals/externals/conditions are not grouped
 * under a specific node but directly available from this node. These FM elements can be
 * identified by their node type.
 */
filtermodule
	: 'filtermodule' IDENTIFIER filtermoduleParameters? LCURLY 
		internals? externals? conditions? inputfilters? outputfilters? 
		RCURLY
	-> ^(FILTER_MODULE[$start] IDENTIFIER filtermoduleParameters? internals? externals? conditions? inputfilters? outputfilters?)
	;
	
/**
 * Filter module parameters. See the thesis of Dirk Doornenbal for datails.
 */	
filtermoduleParameters
	: LROUND fmParamEntry (COMMA fmParamEntry)* RROUND
	-> ^(PARAMS[$start] fmParamEntry+)
	;
	
/**
 * The two forms of filter module parameters, either a single item or a list
 */
fmParamEntry
	: singleFmParam | fmParamList
	;	
	
/**
 * A parameter with should contain only a single value. The FM_PARAM_SINGLE 
 * should have as text value the complete name of the parameter, including the
 * '?' prefix.
 */	
singleFmParam
	: QUESTION IDENTIFIER
	-> ^(FM_PARAM_SINGLE[$start] IDENTIFIER)
	;

/**
 * A parameter with should contain zero or more values. The FM_PARAM_LIST 
 * should have as text value the complete name of the parameter, including the
 * '??' prefix.
 */	
fmParamList
	: DOUBLEQUESTION IDENTIFIER
	-> ^(FM_PARAM_LIST[$start] IDENTIFIER)
	;		

/**
 * Shorthand for either a fqn or singleFmParam. This constructions is often used.
 */
fqnOrSingleFmParam
	: fqn | singleFmParam
	;
	
/**
 * Shorthand for an identifier or single parameter. 
 */
identifierOrSingleFmParam
	: IDENTIFIER | singleFmParam
	;	

/**
 * List of internals. No root node is created. Child elements can be identifier by
 * their corresponding root node.
 */
internals
	: 'internals'! fmInternal*
	;
	
/**
 * An internal can contain a list of names and a single type identifier. The
 * type is placed first because it will be easier to use in the walker
 */	
fmInternal
	: IDENTIFIER (COMMA IDENTIFIER)* COLON fqnOrSingleFmParam SEMICOLON
	-> ^(INTERNAL[$start] fqnOrSingleFmParam  ^(NAMES IDENTIFIER+))
	;

/**
 * List of externals. No root node is created.
 */
externals
	: 'externals'! fmExternal*
	;

/**
 * An external can contain an optional initialization expression.
 * Without an initialization expression the external should be considered
 * as an static object reference rather than an instance.
 */
fmExternal
	: IDENTIFIER COLON type=fqnOrSingleFmParam (eq=EQUALS init=fqnOrSingleFmParam joinPointContext?)? SEMICOLON
	-> ^(EXTERNAL[$start] IDENTIFIER $type ^(INIT[$eq] $init joinPointContext?)?)
	;	
	
/**
 * The desired join point context that the refered to method desires.
 * When the join point context is defined the method has to have that argument type
 * in its signature. If it's completely omitted it will be considered as a "don't care"
 * The 
 */
joinPointContext
	: LROUND 
	( 'full'
	-> ^(JPCA[$start] 'full')
	| 'partial'
	-> ^(JPCA[$start] 'partial')
	| ASTERISK
	-> // don't care
	| // none
	-> ^(JPCA[$start])
	) RROUND
	;

/**
 * List of conditions. No root node is created.
 */
conditions
	: 'conditions'! condition*
	;

/**
 * Note: currently paramaters are not supported in the expression
 */	
condition
	: IDENTIFIER COLON fqnOrSingleFmParam joinPointContext? SEMICOLON
	-> ^(CONDITION[$start] IDENTIFIER fqnOrSingleFmParam joinPointContext? )
	;	
	
/**
 * Creates a node with child elements containing filters and filter operations.
 * Inputfilters and outputfilter chains do not contain a closing semicolon 
 * (confusing with a filter operator)
 */	
inputfilters
	: 'inputfilters' filterExpression
	-> ^(INPUT_FILTERS[$start] filterExpression)
	;
	
outputfilters
	: 'outputfilters' filterExpression
	-> ^(OUTPUT_FILTERS[$start] filterExpression)
	;
	
/**
 * The input and output filters are now created as a filter expression. This
 * constructs a tree of operators and filters
 */
filterExpression
	: filter ( filterOperator^ filterExpression )?
	;	

// $<Filter

/**
 * Currerently there is only one filter operator, the semicolon.
 */
filterOperator
	: SEMICOLON
	-> ^(OPERATOR[$start] SEMICOLON)
	;

/**
 * Generic filter rules. Used by both input and outputfilters.
 */	
filter
	: name=IDENTIFIER COLON filterType filterParams? EQUALS filterElements
	-> ^(FILTER[$start] $name filterType filterParams? filterElements)
	;

/**
 * Filter type for the filter. This is a fully qualified name. Which can point
 * to either a primitive filter type or a filter module
 */
filterType
	: fqn
	;

/**
 * Filter parameters. Uses the canonical assignments. 
 */
filterParams
	: (LROUND filterParam* RROUND)
	-> ^(PARAMS[$start] filterParam*)
	;

filterParam
	: IDENTIFIER EQUALS canonAssignRhs SEMICOLON
	-> ^(EQUALS[$start] ^(OPERAND IDENTIFIER["filter"] IDENTIFIER) ^(OPERAND canonAssignRhs))
	;

/**
 * Supports two types of filter elements (the legacy notation, and the canonical notation)
 */
filterElements
	: LCURLY! legacyFilterElement RCURLY!
	| canonFilterElementExpression 
	;
	
/**
 * The filter element expression in the canonical form
 */
canonFilterElementExpression
	: canonFilterElement ( canonFilterElementOperator^ canonFilterElementExpression )?
	;
	
/**
 * The filter element operators as used in the canonical notation
 */
canonFilterElementOperator
	: 'cor'
	-> ^(OPERATOR[$start] 'cor')
	;
	
/**
 * A filter element in the canonical notation contains a matching expression
 * and zero or more assignments 
 */
canonFilterElement
	: LROUND matchingExpression RROUND ( LCURLY canonAssignment* RCURLY )?
	-> ^(FILTER_ELEMENT[$start] ^(EXPRESSION matchingExpression) canonAssignment* )
	;
	
/**
 * The matching expression in the canonical notation
 */
matchingExpression
	: meAndExpr ( OR^ matchingExpression )?
	;
	
meAndExpr
	: meUnaryExpr ( AND^ meAndExpr )?
	;
	
meUnaryExpr
	: (NOT^)? meCompoundExpr
	;
	
/**
 * Either a simple identifier for 'true' , 'false' or a condition reference, or a
 * compare statement or a compound expression
 */	
meCompoundExpr
	: meCmpLhs meCmpOpr meCmpRhs 
	-> ^(CMPSTMT[$start] ^(OPERATOR meCmpOpr) ^(OPERAND meCmpLhs) ^(OPERAND meCmpRhs))
	| LROUND! matchingExpression RROUND!
	| IDENTIFIER // 'true', 'false', or condition
	;
	
/**
 * The left hand side of a compare statement
 */	
meCmpLhs
	: m='message' PERIOD IDENTIFIER
	-> IDENTIFIER[m] IDENTIFIER
	| t='target'
	-> IDENTIFIER[t]
	| s='selector'
	-> IDENTIFIER[s]
	;		
	
/**
 * The accepted compare operators
 */
meCmpOpr
	: '==' | '$=' | '~=' | '@='
	;

/**
 * The right hand side of the compare statement. This can be a single
 * entry, a paramlist, or a list of single entries
 */
meCmpRhs
	: fmParamList
	| LCURLY meCmpRhsSingle (COMMA meCmpRhsSingle)* RCURLY
	-> ^(LIST[$start] meCmpRhsSingle+)
	| meCmpRhsSingle
	;
	
/** 
 * A single entry on the right hand side
 */
meCmpRhsSingle
	: fqn | singleFmParam | LITERAL
	;
	
/**
 * An assignment part. The filter arguments contains a similar construction
 */	
canonAssignment
	: canonAssingLhs EQUALS canonAssignRhs SEMICOLON
	-> ^(EQUALS[$start] ^(OPERAND canonAssingLhs) ^(OPERAND canonAssignRhs))
	;
	
/**
 * The left hand side of the assignment. This is almost the same as for the
 * left hand side of the compare statement, except that the filter structure
 * can also be given assignments
 */
canonAssingLhs
	: meCmpLhs
	| f='filter' PERIOD IDENTIFIER
	-> IDENTIFIER[f] IDENTIFIER
	;

/**
 * The right hand side of the assignment
 */
canonAssignRhs
	: fqn | singleFmParam | LITERAL
	;

/**
 * (Legacy notation) Converts the legacy filter element notation to
 * the canonical notation. This does not do a 100% safe conversion. The
 * assignment part needs some extra attention. 
 */		
legacyFilterElement
	: filterElement (filterElementOperator^ legacyFilterElement)?
	;

/**
 * Operators that link the filter elements. (Legacy notation) 
 */	
filterElementOperator
	: COMMA
	-> ^(OPERATOR[$start] 'cor')
	;
	
/**
 * This rule contains a predicate in order to keep the grammar LL(1). The conditionExpression
 * and messagePatternSet can both start with an IDENTIFIER, the predicate will see if there's an
 * optional conditionExpression. (Legacy notation)
 */	
filterElement
	: (conditionExpression (ENABLE | DISABLE) )=> feWithCond substitutionPart?
	-> ^(FILTER_ELEMENT feWithCond substitutionPart?)
	| matchingPart substitutionPart?
	-> ^(FILTER_ELEMENT matchingPart substitutionPart?)
	;
	
/**
 * A filter element with a condition construction
 */	
feWithCond
	: conditionExpression feOperMatchPart
	-> ^(EXPRESSION ^(AND conditionExpression feOperMatchPart))
	;

/**
 * The matching operator
 */
feOperMatchPart
	: ENABLE matchingPart
	-> matchingPart
	| DISABLE matchingPart
	-> ^(NOT matchingPart)
	;

// $<Condition Expression

/**
 * Condition expressions only contain boolean operators (Legacy notation)
 */
conditionExpression
	: andExpr ( OR^ conditionExpression )?
	;

/**
 * (Legacy notation)
 */
andExpr
	: unaryExpr ( AND^ andExpr )?
	;

/**
 * (Legacy notation)
 */	
unaryExpr
	: (NOT^)? operandExpr
	;

/**
 * (Legacy notation)
 */	
operandExpr
	: LROUND! conditionExpression RROUND!
	| IDENTIFIER // literals (True, False) are resolved by the tree walker
	;		
// $> Condition Expression
	
/**
 * The matching part can be a list of patterns, or a message list or a single pattern.
 * (Legacy notation)
 */	
matchingPart
	: LCURLY! matchingPatternList RCURLY!
	/* 
	// no longer supported in the new repository (and never supported in FIRE/INLINE)
	| HASH LROUND matchingPattern (SEMICOLON matchingPattern)* RROUND
	-> ^(MESSAGE_LIST matchingPattern+)
	*/
	| matchingPattern
	;
	
/**
 * Convert the matching pattern to a sequence of OR matchings (Legacy notation)
 */
matchingPatternList
	: matchingPattern (COMMA matchingPatternList
	-> ^(OR matchingPattern matchingPatternList)
	|
	-> matchingPattern
	)
	;

/**
 * Even though the original syntax did not always allow a filter module
 * parameter list it is accepted in the canonical notation (Legacy notation)
 */
identifierOrFmParam
	: IDENTIFIER
	-> ^(FQN[$start] IDENTIFIER) // model is as an FQN 
	| singleFmParam | fmParamList
	;
	
/**
 * Converts an identifier to a literal
 */
literalOrFmParam
	: v=IDENTIFIER
	-> LITERAL[$v] 
	| singleFmParam | fmParamList
	;

/** 
 * Either name or signature matching. Converted to a canonical compare
 * statement (Legacy notation).
 */
matchingPattern
	: (LSQUARE (n1=identifierOrFmParam 
			(PERIOD 
				(n2=literalOrFmParam // foo.bar
				-> ^(AND
						^(CMPSTMT[$start] ^(OPERATOR '==') ^(OPERAND IDENTIFIER["target"]) ^(OPERAND $n1))
						^(CMPSTMT[$start] ^(OPERATOR '==') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND $n2))
					)
				| ASTERISK // foo.*
				-> ^(CMPSTMT[$start] ^(OPERATOR '==') ^(OPERAND IDENTIFIER["target"]) ^(OPERAND $n1))
				)
			| // bar
			-> ^(CMPSTMT[$start] ^(OPERATOR '==') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND LITERAL[$n1.text]))
			)
		| ASTERISK PERIOD n3=literalOrFmParam // *.bar
		-> ^(CMPSTMT[$start] ^(OPERATOR '==') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND $n3))
		) RSQUARE)
	| (LANGLE 
		(s1=identifierOrFmParam 
			(PERIOD 
				(s2=literalOrFmParam // foo.bar
				-> ^(AND
						^(CMPSTMT[$start] ^(OPERATOR '$=') ^(OPERAND IDENTIFIER["target"]) ^(OPERAND $s1))
						^(CMPSTMT[$start] ^(OPERATOR '$=') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND $s2))
					)
				| ASTERISK // foo.*
				-> ^(CMPSTMT[$start] ^(OPERATOR '$=') ^(OPERAND IDENTIFIER["target"]) ^(OPERAND $s1))
				)
			| // bar
			-> ^(CMPSTMT[$start] ^(OPERATOR '$=') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND LITERAL[$s1.text]))
			)
		| ASTERISK PERIOD s3=literalOrFmParam // *.bar
		-> ^(CMPSTMT[$start] ^(OPERATOR '$=') ^(OPERAND IDENTIFIER["selector"]) ^(OPERAND $s3))
		) RANGLE)
	;
	
/**
 * Substitution part of the filter element. (Legacy notation) Note that all assignments
 * have the variable prefix 'legacy', this should eventually be converted to 'message' or
 * 'filter', but it depends on the filter type.
 */	
substitutionPart
	: n1=identifierOrSingleFmParam 
		(PERIOD 
			(n2=literalOrFmParam // foo.bar
			-> ^(AND
					^(EQUALS[$start] ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["target"]) ^(OPERAND $n1))
					^(EQUALS[$start] ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["selector"]) ^(OPERAND $n2))
				)
			| ASTERISK // foo.*
			-> ^(EQUALS[$start] ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["target"]) ^(OPERAND $n1))
				^(EQUALS ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["selector"]) ^(OPERAND IDENTIFIER["selector"]))
			)
		| // bar
		-> ^(EQUALS[$start] ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["selector"]) ^(OPERAND LITERAL[$n1.text]))
			^(EQUALS ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["target"]) ^(OPERAND IDENTIFIER["target"]))
		)
	| ASTERISK PERIOD n3=literalOrFmParam // *.bar
	-> ^(EQUALS[$start] ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["selector"]) ^(OPERAND $n3))
		^(EQUALS ^(OPERAND IDENTIFIER["legacy"] IDENTIFIER["target"]) ^(OPERAND IDENTIFIER["target"]))
	;

// $> Filter


// $> Filter Module
	
// $<Superimposition

/**
 * Superimposition block. This node will contain a collection of specific childnodes rather than grouping
 * nodes. So it will be simply a list of conditions, selectors, etc.
 */
superimposition
	: 'superimposition' LCURLY conditionalSi? selectors? filtermodules? annotations? constraints? RCURLY
	-> ^(SUPERIMPOSITION[$start] conditionalSi? selectors? filtermodules? annotations? constraints?)
	;

/**
 * A conditional superimposition declaration. 
 */	
conditionalSi
	: 'conditions' (IDENTIFIER COLON fqn joinPointContext? SEMICOLON)+
	-> ^(CONDITION[$start] IDENTIFIER fqn joinPointContext?)+
	;
	
selectors
	: 'selectors'! (selectorSi SEMICOLON!)+
	;

/**
 * There are two form of selectors, legacy selectors or using prolog predicates.
 */	
selectorSi
	: name=IDENTIFIER EQUALS LCURLY 
	( leg=selectorExprLegacy
	-> ^(SELECTOR[$start] $name $leg)
	| expr=selectorExprPredicate
	-> ^(SELECTOR[$start] $name $expr)
	)
	;
	
/**
 * Legacy selector language. Always starts with a star
 */	
selectorExprLegacy
	: ASTERISK (EQUALS | COLON) fqn RCURLY
	-> ^(LEGACY_SELECTOR[$start] EQUALS? COLON? fqn)
	;
	
/**
 * Prolog predicate selector. The prolog expression is not parsed, all tokens until the
 * ending RCURLY are accepted and after that a new token is created that contains the expression
 * AS IS.
 */	
selectorExprPredicate
	: id=IDENTIFIER '|' expr=allButRcurly RCURLY
	-> ^(PREDICATE_SELECTOR[$start] $id { adaptorCreate(adaptor, PROLOG_EXPR, inputToString($expr.start, $expr.stop)) } )
	;	

/**
 * A dirty hack to get the prolog expression, this will asume that prolog doesn't
 * use the } in any of it's expressions.
 */
allButRcurly
	: ~RCURLY*
	//-> ^(PrologExpr[$text])
	;		
	
/**
 * Filter module bindings
 */
filtermodules
	: 'filtermodules'! (filtermoduleSi SEMICOLON!)+
	;
	
/**
 * Filter module binding. It uses a subrule for the conditional superimposition.
 * After the weave operator (see condBinding) there is a list of filter modules
 * references. The curly braces are optional.
 */
filtermoduleSi
	: sel=condBinding
	( LCURLY fmBinding (COMMA fmBinding)* RCURLY
	| fmBinding (COMMA fmBinding)*
	)
	-> ^(FM_BINDINGS[$start] $sel fmBinding+)
	;

/** 
 * Optional conditional binding of filter modules. Uses a predicate to check if
 * there is a condition. The ENABLE token is only used for form, it does not imply
 * the ENABLE operator functionality
 */
condBinding
	: (IDENTIFIER ENABLE)=> cond=IDENTIFIER ENABLE sel=IDENTIFIER WEAVE
	-> $sel ^(CONDITION $cond)
	| sel=IDENTIFIER WEAVE
	-> $sel
	;	
	
/** 
 * A single filter module binding. Contains optional filter module parameters.
 */
fmBinding
	: fmref=concernFmRef (LROUND param (COMMA param)* RROUND)?
	-> ^(BINDING[$start] $fmref ^(PARAMS param+)?)
	;	
	
/**
 * reference to a filtermodule
 * this can be a local reference (just IDENTIFIER) 
 * or a reference to a filter module in a external concern (fqn::IDENTIFIER)
 * The double colon is the old notation, FQN is prefered.
 */
concernFmRef
	: qn=fqn (dc=DOUBLECOLON IDENTIFIER
	{
		warning("The namespace.concern::filterModule is deprectated. Use namespace.concern.filterModule.", $start);
	}
	-> ^($qn IDENTIFIER)
	|
	-> $qn
	)
	;		
	
/**
 * A filter module parameter, used in the fmBinding rule
 */
param
	: LCURLY paramValue (COMMA paramValue)* RCURLY
	-> ^(LIST[$start] paramValue+)
	| paramValue
	;
	
/**
 * A single parameter value
 */
paramValue
	: fqn 
	-> ^(PARAM[$start] fqn)
	| LITERAL
	-> ^(PARAM[$start] LITERAL)
	;	
	
/**
 * Annotation binding, a bit like filter module binding, except that it uses annotation classes	
 */
annotations
	: 'annotations'! (annotationSi SEMICOLON!)+
	;
	
/**
 * A list of annotation classes bound to a selector
 */	
annotationSi
	: sel=IDENTIFIER WEAVE
	( LCURLY fqn (COMMA fqn)* RCURLY
	| fqn (COMMA fqn)*
	)
	-> ^(ANNOTATION_BINDINGS[$start] $sel fqn+)
	;	
	
/**
 * filter module orderning constraints	
 */
constraints
	: 'constraints'! (constraint SEMICOLON!)+
	;

/**
 * The available orderning constrains. Currently only a pre constraint.
 */	
constraint
	: IDENTIFIER LROUND concernFmRef (COMMA concernFmRef)* RROUND
	-> ^(CONSTRAINT[$start] IDENTIFIER concernFmRef+)
	;			

// $>
	
// $<Implementation

/**
 * The implementation section. This contains a hack to copy with the variable 
 * content of the code implementation. The code block is not added as a token element.
 * (MichielH+Lodewijk 2007-10-08) "implementation by class" has been dropped, it did not 
 * provide any functionality
 */
implementation
	: 'implementation' 
	( 'by' asm=fqn SEMICOLON
	-> ^(IMPLEMENTATION[$start] $asm)
	| 'in' lang=IDENTIFIER 'by' cls=fqn 'as' fn=LITERAL code=codeBlock
	-> ^(IMPLEMENTATION[$start] $lang $cls $fn $code)
	)
	;
	
/**
 * extractEmbeddedCode method will manually scan to the end of the codeblock
 */	
codeBlock
@init {String codeTxt = "";}
	: al=HEREDOC
	-> ^(CODE_BLOCK[al])
	| strt=LCURLY 
	{codeTxt = extractEmbeddedCode(adaptor);}
	RCURLY
	-> ^(CODE_BLOCK[$strt, codeTxt])
	;
// $>
		


// $<Tokens

AND				: '&'; 
OR				: '|';
NOT				: '!';

LROUND			: '(';
RROUND			: ')';
LCURLY			: '{';
RCURLY	 		: '}';
LSQUARE			: '[';
RSQUARE			: ']';
LANGLE			: '<';
RANGLE			: '>';

EQUALS			: '=';
COLON			: ':';
DOUBLECOLON		: '::';
SEMICOLON		: ';';

COMMA			: ',';
PERIOD			: '.';
ASTERISK		: '*';
HASH			: '#';
QUOTE			: '\'';
DOUBLEQUOTE		: '"';
QUESTION		: '?';
DOUBLEQUESTION	: '??';

// matching operators
ENABLE			: '=>';
DISABLE			: '~>';

// weaving operators
WEAVE			: '<-';

IDENTIFIER		: ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*;

// string literal
LITERAL 				: ('"' LIT_ALT1  '"') | ('\'' LIT_ALT2 '\'');
fragment LIT_ALT1 		: LIT_INTERNAL ('\'' LIT_INTERNAL)*;
fragment LIT_ALT2 		: LIT_INTERNAL ('"' LIT_INTERNAL)* ;
fragment LIT_INTERNAL 	: (LIT_ESC | '\t' | '\r' | '\n' | ~('\u0000'..'\u001f' | '\\' | '"' | '\'' ))*;
fragment LIT_ESC 		: '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\');

// not really heredoc, but almost
HEREDOC : '<<ASIS' {int mystart = getCharIndex();} (' '|'\n'|'\r') .* ('\n'|'\r') { setText(input.substring(mystart, getCharIndex())); } 'ASIS;';

// Whitespace
WS : (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};
// Block comment
COMMENT	: '/*' .* '*/' {$channel=HIDDEN;};
// Single line comment
LINE_COMMENT : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;} ;

// this token is needed to include garbage data in the tokenizer/lexer
// without it ANTLR will simply ingore all garbage
ALLTOKENS : '\u0000' .. '\uffff';

// $>
