grammar Cps;

options {
	k=1;
	output=AST;
	//language=CSharp;
}

tokens {
	// keywords
	Concern;	
	Fqn;
	ConcernParameters;
	Param;
	In;
	
	FilterModule;
	Internal;
	Names;
	External;
	Init;
	Condition;
	InputFilters;
	OutputFilters;	
	Filter;
	FilterElement;
	Expression;
	Operator;
	MatchingPart;
	SubstPart;
	List;
	MessageList;
	Name;
	Sign;
	Target;
	Selector;
	
	Superimposition;
	LegacySelector;
	PredicateSelector;
	PrologExpr;
	FMBindings;
	Params;
	Binding;
	AnnotationBindings;
	Constraint;
	Pre;
	
	Implementation;
	Filename;
	CodeBlock;
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
 *
 * \$Id$
 */
 
 package Composestar.Core.COPPER2;
}

@members{
	/**
	 * Stream location in the CPS file where the embedded source starts
	 */
	protected int embeddedSourceLoc;
	public int getEmbeddedSourceLoc() { return embeddedSourceLoc; }
}

concern
	: 'concern' IDENTIFIER concernParameters? ('in' fqn)? LCURLY filtermodule* superimposition? (implementation | RCURLY)
	-> ^(Concern IDENTIFIER concernParameters? ^(In fqn)? filtermodule* superimposition? implementation?)
	;

fqn
	: IDENTIFIER (PERIOD IDENTIFIER)*
	-> ^(Fqn IDENTIFIER (PERIOD IDENTIFIER)*)
	;	
	
concernParameters
	: LROUND IDENTIFIER COLON fqn (COMMA IDENTIFIER COLON fqn)* RROUND
	-> ^(ConcernParameters ^(Param IDENTIFIER fqn)*)
	;	
	
// $<Filter Module

filtermodule
	: 'filtermodule' IDENTIFIER LCURLY internals? externals? conditions? inputfilters? outputfilters? RCURLY
	-> ^(FilterModule internals? externals? conditions? inputfilters? outputfilters?)
	;

internals
	: 'internals'! internal+
	;
	
internal
	: IDENTIFIER (COMMA IDENTIFIER)* COLON fqn SEMICOLON
	-> ^(Internal ^(Names IDENTIFIER+) fqn)
	;

externals
	: 'externals'! external+
	;

external
	: IDENTIFIER COLON type=fqn (EQUALS init=fqn LROUND /* params */ RROUND)? SEMICOLON
	-> ^(External IDENTIFIER $type ^(Init $init /* params */))
	;	

conditions
	: 'conditions'! condition+
	;
	
condition
	: IDENTIFIER COLON fqn LROUND /* params */ RROUND SEMICOLON
	-> ^(Condition IDENTIFIER fqn /* params */ )
	;	
	
inputfilters
	: 'inputfilters' filter (filterOperator filter)*
	-> ^(InputFilters filter (filterOperator filter)*)
	;
	
outputfilters
	: 'outputfilters' filter (filterOperator filter)*
	-> ^(OutputFilters filter (filterOperator filter)*)
	;

// $<Filter

filterOperator
	: SEMICOLON
	;
	
filter
	: name=IDENTIFIER COLON type=IDENTIFIER EQUALS LCURLY filterElement (COMMA filterElement)* RCURLY
	-> ^(Filter $name $type filterElement+)
	;
	
filterElement
	: (conditionExpression matchingOperator)=> conditionExpression matchingOperator messagePatternSet
	-> ^(FilterElement ^(Expression conditionExpression) ^(Operator matchingOperator) messagePatternSet)
	| messagePatternSet
	-> ^(FilterElement messagePatternSet)
	;

// $<Condition Expression	
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
	| IDENTIFIER // literals are resolved by the walker
	;		
// $> Condition Expression

matchingOperator
	: ENABLE | DISABLE
	;
	
// Matching and optional Substitution
// or only target.selector which is a signature matching
messagePatternSet
	: matchingPart substitutionPart?
	-> ^(MatchingPart matchingPart) ^(SubstPart substitutionPart)?
	// only target.selector alternative dropped per 2007-10-05 (MichielH+Lodewijk)
	//| targetSelector
	//-> ^(MatchingPart ^(Sign targetSelector))
	;	
	
matchingPart
	: LCURLY matchingPatternList (COMMA matchingPatternList)* RCURLY
	-> ^(List matchingPatternList+)
	| matchingPatternList
	;

matchingPatternList 
	: HASH LROUND matchingPattern (COMMA matchingPattern)* RROUND
	-> ^(MessageList matchingPattern+)
	| matchingPattern
	;

// Either name or signature matching.
// Note that a 2nd notation for signature matching is defined in the messagePatternSet rule
matchingPattern
	: (
		LSQUARE targetSelector RSQUARE
	  //|	DOUBLEQUOTE targetSelector DOUBLEQUOTE // "target.selector" no longer available
	  )
	  -> ^(Name targetSelector)
	| (
		LANGLE targetSelector RANGLE
	  )
	  -> ^(Sign targetSelector)
	;
	
substitutionPart
	: HASH  LROUND targetSelector (COMMA targetSelector)* RROUND
	-> ^(MessageList targetSelector+)
	| targetSelector
	;		
	
targetSelector
	: (target PERIOD)=> target PERIOD! selector
	| selector
	;
	
target
	: IDENTIFIER
	-> ^(Target IDENTIFIER)
	| ASTERISK
	-> ^(Target ASTERISK)
	;

selector
	: IDENTIFIER
	-> ^(Selector IDENTIFIER)
	| ASTERISK
	-> ^(Selector ASTERISK)
	;

// $> Filter


// $> Filter Module
	
// $<Superimposition

superimposition
	: 'superimposition' LCURLY conditionalSi? selectors? filtermodules? annotations? constraints? RCURLY
	-> ^(Superimposition conditionalSi? selectors? filtermodules? annotations? constraints?)
	;
	
conditionalSi
	: 'conditions' (IDENTIFIER COLON fqn SEMICOLON)+
	-> ^(Condition IDENTIFIER fqn)+
	;
	
selectors
	: 'selectors'! (selectorSi SEMICOLON!)+
	;
	
selectorSi
	: name=IDENTIFIER EQUALS LCURLY 
	  ( leg=selectorExprLegacy
	  -> ^(Selector $name $leg)
	  | expr=selectorExprPredicate
	  -> ^(Selector $name $expr)
	  )
	;
	
selectorExprLegacy
	: ASTERISK (EQUALS | COLON) fqn RCURLY
	-> ^(LegacySelector fqn)
	;
	
selectorExprPredicate
	: id=IDENTIFIER '|' expr=allButRcurly RCURLY
	-> ^(PredicateSelector $id { adaptor.create(PrologExpr, input.toString($expr.start, $expr.stop)) } )
	;	

// A dirty hack to get the prolog expression, this will asume that prolog doesn't
// use the } in any of it's expressions.
allButRcurly
	: ~RCURLY*
	//-> ^(PrologExpr[$text])
	;		
	
filtermodules
	: 'filtermodules'! (filtermoduleSi SEMICOLON!)+
	;
	
// sel <- FM1, FM2, FM3	
filtermoduleSi
	: sel=condBinding
	( LCURLY fmBinding (COMMA fmBinding)* RCURLY
	| fmBinding (COMMA fmBinding)*
	)
	-> ^(FMBindings $sel fmBinding+)
	;

// optional conditional binding of filter modules
condBinding
	: (IDENTIFIER ENABLE)=> cond=IDENTIFIER ENABLE sel=IDENTIFIER WEAVE
	-> $sel ^(Condition $cond)
	| sel=IDENTIFIER WEAVE
	-> $sel
	;	
	
// A single filter module binding	
fmBinding
	: ref=concernFmRef (LROUND param (COMMA param) RROUND)?
	-> ^(Binding $ref ^(Params param+)?)
	;	
	
// reference to a filtermodule
// this can be a local reference (just IDENTIFIER) 
// or a reference to a filter module in a external concern (fqn::IDENTIFIER)
concernFmRef
	: fqn (DOUBLECOLON IDENTIFIER)?
	;		
	
// A filter module parameter
param
	: IDENTIFIER // | STRING ???
	;	
	
// annotation binding, a bit like filter module binding, except that it uses annotation classes	
annotations
	: 'annotations'! (annotationSi SEMICOLON!)+
	;
	
annotationSi
	: sel=IDENTIFIER WEAVE
	( LCURLY fqn (COMMA fqn)* RCURLY
	| fqn (COMMA fqn)*
	)
	-> ^(AnnotationBindings $sel fqn+)
	;	
	
// filter module orderning constraints	
constraints
	: 'constraints'! (constraint SEMICOLON!)+
	;
	
constraint
	: preConstraint
	-> ^(Constraint preConstraint)
	;
	
preConstraint
	: 'pre' LROUND lhs=concernFmRef COMMA rhs=concernFmRef  RROUND
	-> ^(Pre $lhs $rhs)
	;				

// $>
	
// $<Implementation

implementation
	: 'implementation' 'in' lang=IDENTIFIER 'by' cls=IDENTIFIER 'as' DOUBLEQUOTE fn=filename DOUBLEQUOTE code=codeBlock
	-> ^(Implementation $lang $cls $fn)
	;

filename
	: (IDENTIFIER | '-')+ (PERIOD (PERIOD | IDENTIFIER | '-')+)?
	-> ^(Filename[$text])
	;	
	
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
DOUBLEQUOTE	: '"';

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
