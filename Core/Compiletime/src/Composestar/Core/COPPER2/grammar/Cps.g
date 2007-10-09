/*
 * Parser (Tree generator) and Lexer specification for the Compose* language
 * $Id$
 *
 * Changes:
 * (2007-10-05) michielh	Dropped single target.selector which implies
 *				signature matching (too confusing).
 * (2007-10-08) michielh	Added the filter module parameter
 * (2007-10-09) michielh	Renamed magic tokens to prevent naming collision 
 *				with types.
 */
grammar Cps;

options {
	k = 1;
	output = AST;
	language = Java;
	superClass = CpsParserBase;
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
	PRE;
	
	IMPLEMENTATION;
	FILENAME;
	CODE_BLOCK;
}

@header {
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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
 
package Composestar.Core.COPPER2;
}

@lexer::header {
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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
 
package Composestar.Core.COPPER2;
}

/**
 * Start of a CPS source file. It will always start with a concern declaration.
 * This rule contains a partial hack for the implementation part. The implementation
 * rule will eat all remaining tokens therefore a concern will either end with an RCURCLY
 * or an implementation rule.
 */
concern
	: 'concern' IDENTIFIER concernParameters? (in='in' fqn)? LCURLY filtermodule* superimposition? (implementation | RCURLY)
	-> ^(CONCERN[$start] IDENTIFIER concernParameters? ^(IN[$in] fqn)? filtermodule* superimposition? implementation?)
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
	: LROUND IDENTIFIER COLON fqn (COMMA IDENTIFIER COLON fqn)* RROUND
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
	: 'internals'! internal+
	;
	
/**
 * An internal can contain a list of names and a single type identifier. The
 * type is placed first because it will be easier to use in the walker
 */	
internal
	: IDENTIFIER (COMMA IDENTIFIER)* COLON fqnOrSingleFmParam SEMICOLON
	-> ^(INTERNAL fqnOrSingleFmParam  ^(NAMES IDENTIFIER+))
	;

/**
 * List of externals. No root node is created.
 */
externals
	: 'externals'! external+
	;

/**
 * An external can contain an optional initialization expression.
 * Without an initialization expression the external should be considered
 * as an static object reference rather than an instance.
 * Note: currently paramaters are not supported in the init expression
 */
external
	: IDENTIFIER COLON type=fqnOrSingleFmParam (eq=EQUALS init=fqnOrSingleFmParam LROUND /* params */ RROUND)? SEMICOLON
	-> ^(EXTERNAL IDENTIFIER $type ^(INIT[$eq] $init /* params */))
	;	

/**
 * List of conditions. No root node is created.
 */
conditions
	: 'conditions'! condition+
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
	: 'inputfilters' filter (filterOperator filter)*
	-> ^(INPUT_FILTERS[$start] filter (filterOperator filter)*)
	;
	
outputfilters
	: 'outputfilters' filter (filterOperator filter)*
	-> ^(OUTPUT_FILTERS[$start] filter (filterOperator filter)*)
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
	: name=IDENTIFIER COLON type=identifierOrSingleFmParam EQUALS LCURLY filterElement (COMMA filterElement)* RCURLY
	-> ^(FILTER $name $type filterElement+)
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
	//| targetSelector
	//-> ^(MatchingPart ^(Sign targetSelector))
	;	
	
/**
 * The matching part can be a list of patterns
 */	
matchingPart
	: LCURLY matchingPatternList (COMMA matchingPatternList)* RCURLY
	-> ^(LIST matchingPatternList+)
	| matchingPatternList
	;

/**
 * This rule provides message list support: #( pattern, pattern, ...)
 * To remove message list support in parsing simply change the matchingPart
 * rule to use matchingPattern instead of matchingPatternList
 */
matchingPatternList 
	: HASH LROUND matchingPattern (COMMA matchingPattern)* RROUND
	-> ^(MESSAGE_LIST matchingPattern+)
	| matchingPattern
	;

/** 
 * Either name or signature matching.
 * Note that a 2nd notation for signature matching is defined in the messagePatternSet rule
 */
matchingPattern
	: (
		LSQUARE targetSelector RSQUARE
	  // "target.selector" no longer available. support for this construction
	  // has been dropped before version 0.5
	  //|	DOUBLEQUOTE targetSelector DOUBLEQUOTE 
	  )
	  -> ^(NAME targetSelector)
	| (
		LANGLE targetSelector RANGLE
	  )
	  -> ^(SIGN targetSelector)
	;
	
/**
 * Substitution part of the filter element. The second rule provides message list
 * support.
 */	
substitutionPart
	: targetSelector
	| HASH  LROUND targetSelector (COMMA targetSelector)* RROUND
	-> ^(MESSAGE_LIST targetSelector+)
	;		
	
/**
 * The target is optional. However the target and selector contain similar constructions,
 * therefor a predicate is used to check if the target is present.
 */	
targetSelector
	: (target PERIOD)=> target PERIOD! selector
	| selector
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
selector
	: IDENTIFIER
	-> ^(SELECTOR IDENTIFIER)
	| ASTERISK
	-> ^(SELECTOR ASTERISK)
	| fmParamEntry
	-> ^(SELECTOR fmParamEntry)
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
	-> ^(LEGACY_SELECTOR[$start] fqn)
	;
	
/**
 * Prolog predicate selector. The prolog expression is not parsed, all tokens until the
 * ending RCURLY are accepted and after that a new token is created that contains the expression
 * AS IS.
 */	
selectorExprPredicate
	: id=IDENTIFIER '|' expr=allButRcurly RCURLY
	-> ^(PREDICATE_SELECTOR $id { adaptor.create(PROLOG_EXPR, input.toString($expr.start, $expr.stop)) } )
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
	: ref=concernFmRef (LROUND param (COMMA param)* RROUND)?
	-> ^(BINDING $ref ^(PARAMS param+)?)
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
	: preConstraint
	-> ^(CONSTRAINT preConstraint)
	;

/**
 * The pre(lhs, rhs)  filter module ordering constraint
 */	
preConstraint
	: 'pre' LROUND lhs=concernFmRef COMMA rhs=concernFmRef  RROUND
	-> ^(PRE[$start] $lhs $rhs)
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
	: 'implementation' 'in' lang=IDENTIFIER 'by' cls=IDENTIFIER 'as' DOUBLEQUOTE fn=filename DOUBLEQUOTE code=codeBlock
	-> ^(IMPLEMENTATION[$start] $lang $cls $fn)
	;

/**
 * Creates a filename token.
 */
filename
	: (IDENTIFIER | '-')+ (PERIOD (PERIOD | IDENTIFIER | '-')+)?
	-> ^(FILENAME[$text])
	;	
	
/**
 * The code block will eat everything after the left curly. The EMBEX module will eventually extract
 * the content.
 */	
codeBlock
	: LCURLY! .*
	{ embeddedSourceLoc = $start.getTokenIndex(); }
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

// token fragment, just for ease of use
fragment DIGIT	: '0' .. '9';
fragment LETTER	: 'a' .. 'z' | 'A' .. 'Z';
fragment SPECIAL: '_';

IDENTIFIER	: (LETTER | SPECIAL) (LETTER | SPECIAL | DIGIT)*;

// Whitespace
WS 		:  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};
// Block comment
COMMENT		:   '/*' .* '*/' {$channel=HIDDEN;};
// Single line comment
LINE_COMMENT    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;} ;

// $>
