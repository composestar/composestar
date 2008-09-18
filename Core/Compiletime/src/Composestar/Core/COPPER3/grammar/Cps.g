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
 * (2008-09-17) michielh	Start on COPPER3. Input/Output filters now create
 				filter expressions.
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
	CONCERN_PARAMETERS;
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
	MATCHING_PART;
	SUBST_PART;
	LIST;
	MESSAGE_LIST;
	NAME;
	SIGN;
	TARGET;
	SELECTOR;
	
	SUPERIMPOSITION;
	LEGACY_SELECTOR;
	PREDICATE_SELECTOR;
	PROLOG_EXPR;
	FM_BINDINGS;
	BINDING;
	ANNOTATION_BINDINGS;
	CONSTRAINT;
	
	IMPLEMENTATION;
	FILENAME;
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
 * A generic fully qualified name. Each section is separated by a '.'. Each part is
 * individually available (including the dot). This way the FQN is available AS IS through
 * the text value but still available for modifications
 */
fqn
	: IDENTIFIER (PERIOD IDENTIFIER)*
	-> ^(FQN[$text] IDENTIFIER (PERIOD IDENTIFIER)*)
	;	
	
/**
 * Formal concern parameters. Similar to a class constructor.
 */	
concernParameters
	: LROUND IDENTIFIER COLON fqn (SEMICOLON IDENTIFIER COLON fqn)* RROUND
	-> ^(CONCERN_PARAMETERS ^(PARAM IDENTIFIER fqn)*)
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
	-> ^(PARAMS fmParamEntry+)
	;
	
fmParamEntry
	: singleFmParam | fmParamList
	;	
	
singleFmParam
	: QUESTION IDENTIFIER
	-> ^(FM_PARAM_SINGLE[$start] IDENTIFIER)
	;

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
	-> ^(INTERNAL fqnOrSingleFmParam  ^(NAMES IDENTIFIER+))
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
 * Note: currently paramaters are not supported in the init expression
 */
fmExternal
	: IDENTIFIER COLON type=fqnOrSingleFmParam (eq=EQUALS init=fqnOrSingleFmParam LROUND /* params */ RROUND)? SEMICOLON
	-> ^(EXTERNAL IDENTIFIER $type ^(INIT[$eq] $init /* params */)?)
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
	: IDENTIFIER COLON fqnOrSingleFmParam LROUND /* params */ RROUND SEMICOLON
	-> ^(CONDITION IDENTIFIER fqnOrSingleFmParam /* params */ )
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
	;

/**
 * Generic filter rules. Used by both input and outputfilters.
 */	
filter
	: name=IDENTIFIER COLON filterType filterParams? EQUALS filterElements
	-> ^(FILTER $name filterType filterParams? filterElements)
	;

/**
 * Filter type for the filter. This is a fully qualified name. Which can point
 * to either a primitive filter type or a filter module
 */
filterType
	: fqn
	;

/**
 * Filter parameters. Uses the canonical assignments. The assignments have an implicit
 * "filter." prefix. This should be handled by the tree walker.
 */
filterParams
	: (LROUND canonAssignment* RROUND)
	-> ^(PARAMS canonAssignment*)
	;

/**
 * Supports two types of filter elements (the legacy notation, and the canonical notation)
 */
filterElements
	: LCURLY! filterElement (filterElementOperator filterElement)* RCURLY!
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
	;
	
/**
 * A filter element in the canonical notation contains a matching expression
 * and zero or more assignments 
 */
canonFilterElement
	: LROUND matchingExpression RROUND ( LCURLY canonAssignment* RCURLY )?
	-> ^(FILTER_ELEMENT matchingExpression canonAssignment* )
	;
	
/**
 * The matching expression in the canonical notation
 */
matchingExpression
	: IDENTIFIER // ....
	;
	
canonAssignment
	: IDENTIFIER // ....
	;
	


/**
 * Operators that link the filter elements
 */	
filterElementOperator
	: COMMA
	-> 'cor'
	;	
	
/**
 * This rule contains a predicate in order to keep the grammar LL(1). The conditionExpression
 * and messagePatternSet can both start with an IDENTIFIER, the predicate will see if there's an
 * optional conditionExpression.
 */	
filterElement
	: (conditionExpression matchingOperator)=> conditionExpression matchingOperator messagePatternSet
	-> ^(FILTER_ELEMENT ^(EXPRESSION conditionExpression) ^(OPERATOR matchingOperator) messagePatternSet)
	| messagePatternSet
	-> ^(FILTER_ELEMENT messagePatternSet)
	;

// $<Condition Expression

/**
 * Condition expressions only contain boolean operators
 */
conditionExpression
	: andExpr ( OR^ conditionExpression )?
	;

andExpr
	: unaryExpr ( AND^ andExpr )?
	;
	
unaryExpr
	: (NOT^)? operandExpr
	;
	
operandExpr
	: LROUND! conditionExpression RROUND!
	| IDENTIFIER // literals (True, False) are resolved by the tree walker
	;		
// $> Condition Expression

matchingOperator
	: ENABLE | DISABLE
	;
	
/** 
 * Matching and optional Substitution
 * or only target.selector which is a signature matching.
 * A matchingMatchingPattern set creates two root nodes, a MatchingPart and optional SubstPart.
 * These nodes will be a child node of a FilterElement node
 */
messagePatternSet
	: matchingPart substitutionPart?
	-> ^(MATCHING_PART matchingPart) ^(SUBST_PART substitutionPart)?
	// single target.selector alternative dropped per 2007-10-05 (MichielH+Lodewijk)
	| targetSelector[1] 
	  {
	  	warning("Naked target.selector signature matching has been deprecated.", $start);
	  }
	-> ^(MATCHING_PART ^(SIGN targetSelector))
	;	
	
/**
 * The matching part can be a list of patterns, or a message list or a single pattern.
 */	
matchingPart
	: LCURLY matchingPattern (COMMA matchingPattern)* RCURLY
	-> ^(LIST matchingPattern+)
	| HASH LROUND matchingPattern (SEMICOLON matchingPattern)* RROUND
	-> ^(MESSAGE_LIST matchingPattern+)
	| matchingPattern
	;

/** 
 * Either name or signature matching.
 * Note that a 2nd notation for signature matching is defined in the messagePatternSet rule
 */
matchingPattern
	: (
		LSQUARE targetSelector[1] RSQUARE
	  // "target.selector" no longer available. support for this construction
	  // has been dropped before version 0.5
	  //|	DOUBLEQUOTE targetSelector DOUBLEQUOTE 
	  )
	  -> ^(NAME targetSelector)
	| (
		LANGLE targetSelector[1] RANGLE
	  )
	  -> ^(SIGN targetSelector)
	;
	
/**
 * Substitution part of the filter element. The second rule provides message list
 * support.
 */	
substitutionPart
	: targetSelector[0]
	| HASH  LROUND targetSelector[0] (SEMICOLON targetSelector[0])* RROUND
	-> ^(MESSAGE_LIST targetSelector+)
	;		
	
/**
 * The target is optional. However the target and selector contain similar constructions,
 * therefor a predicate is used to check if the target is present.
 */	
targetSelector [int allowParamList]
	: (target PERIOD)=> target PERIOD! selector[allowParamList]
	| selector[allowParamList]
	;

/**
 * The target part of the targeSelector. The target can also be a single filter module parameter.
 */		
target
	: identifierOrSingleFmParam
	-> ^(TARGET identifierOrSingleFmParam)
	| ASTERISK
	-> ^(TARGET ASTERISK)
	;

/**
 * The selector. The selector may contain both filter module parameter types.
 */
selector [int allowParamList]
	: IDENTIFIER
	-> ^(SELECTOR IDENTIFIER)
	| ASTERISK
	-> ^(SELECTOR ASTERISK)
	| singleFmParam
	-> ^(SELECTOR singleFmParam)
	| {allowParamList != 0}? fmParamList
	-> ^(SELECTOR fmParamList)
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
 * (michielh 2007-10-08) Initially the condital declaration did not support the parenthesis, but I've
 * added it for conformity.
 */	
conditionalSi
	: 'conditions' (IDENTIFIER COLON fqn  (LROUND /* params */ RROUND)? SEMICOLON)+
	-> ^(CONDITION IDENTIFIER fqn)+
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
	  -> ^(SELECTOR $name $leg)
	  | expr=selectorExprPredicate
	  -> ^(SELECTOR $name $expr)
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
	-> ^(PREDICATE_SELECTOR $id { adaptorCreate(adaptor, PROLOG_EXPR, inputToString($expr.start, $expr.stop)) } )
	;	

/**
 * A dirty hack to get the prolog expression, this will asume that prolog doesn't
 * use the } in any of it's expressions.
 */
allButRcurly
	: ~RCURLY*
	//-> ^(PrologExpr[$text])
	;		
	
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
	-> ^(FM_BINDINGS $sel fmBinding+)
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
	-> ^(BINDING $fmref ^(PARAMS param+)?)
	;	
	
/**
 * reference to a filtermodule
 * this can be a local reference (just IDENTIFIER) 
 * or a reference to a filter module in a external concern (fqn::IDENTIFIER)
 */
concernFmRef
	: fqn (DOUBLECOLON IDENTIFIER)?
	;		
	
/**
 * A filter module parameter, used in the fmBinding rule
 */
param
	: LCURLY fqn (COMMA fqn)* RCURLY
	-> ^(LIST fqn+)
	| fqn
	-> ^(PARAM fqn)
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
	-> ^(ANNOTATION_BINDINGS $sel fqn+)
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
	: oper=IDENTIFIER LROUND lhs=concernFmRef COMMA rhs=concernFmRef RROUND
	-> ^(CONSTRAINT $oper $lhs $rhs)
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
	| 'in' lang=IDENTIFIER 'by' cls=fqn 'as' DOUBLEQUOTE fn=filename DOUBLEQUOTE code=codeBlock
	-> ^(IMPLEMENTATION[$start] $lang $cls $fn $code)
	)
	;

/**
 * Creates a filename token.
 */
filename
	: (IDENTIFIER | '-')+ (PERIOD (PERIOD | IDENTIFIER | '-')+)?
	-> ^(FILENAME[$text])
	;	
	
/**
 * extractEmbeddedCode method will manually scan to the end of the codeblock
 */	
codeBlock
@init {String codeTxt = "";}
	: strt=LCURLY 
	  {codeTxt = extractEmbeddedCode(adaptor);}
	  RCURLY
	  -> ^(CODE_BLOCK[$strt, codeTxt])
	;	
// $>
		


// $<Tokens

AND		: '&'; 
OR		: '|';
NOT		: '!';

LROUND		: '(';
RROUND		: ')';
LCURLY		: '{';
RCURLY	 	: '}';
LSQUARE		: '[';
RSQUARE		: ']';
LANGLE		: '<';
RANGLE		: '>';

EQUALS		: '=';
COLON		: ':';
DOUBLECOLON	: '::';
SEMICOLON	: ';';

COMMA		: ',';
PERIOD		: '.';
ASTERISK	: '*';
HASH		: '#';
QUOTE		: '\'';
DOUBLEQUOTE	: '"';
QUESTION	: '?';
DOUBLEQUESTION	: '??';

// matching operators
ENABLE		: '=>';
DISABLE		: '~>';

// weaving operators
WEAVE		: '<-';

IDENTIFIER	: ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')*;

// Whitespace
WS 		:  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};
// Block comment
COMMENT		:   '/*' .* '*/' {$channel=HIDDEN;};
// Single line comment
LINE_COMMENT    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;} ;

// this token is needed to include garbage data in the tokenizer/lexer
// without it ANTLR will simply ingore all garbage
ALLTOKENS : '\u0000' .. '\uffff';

// $>
