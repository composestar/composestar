/*
 * Tree Walker. uses the output of Cps.g
 * $Id$
 *
 * Changes:
 */
tree grammar CpsTreeWalker;

options {
	tokenVocab = Cps;
	ASTLabelType = CommonTree;
	language = Java;
	superClass = CpsTreeWalkerBase;	
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

import Composestar.Core.CpsProgramRepository.CpsConcern.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.Exception.*;

import java.util.Vector;
}

concern returns [CpsConcern c = new CpsConcern();]
// throws CpsSemanticException
	: ^(strt=CONCERN {setLocInfo(c, strt);}
		name=IDENTIFIER {c.setName($name.text);}
		(concernParameters[c])?
		(^(IN ns=fqn {c.setNamespace(ns);}))?
		(filtermodule[c])*
		(superimposition[c])?
		(implementation[c])?
	  )
	;
	
fqn returns [String res]
	: ^(frst=FQN {res = $frst.text;} IDENTIFIER (PERIOD IDENTIFIER)* )
	;

fqnAsList returns [List<String> l = new ArrayList<String>();]
	: ^(FQN first=IDENTIFIER { l.add($first.text); } (PERIOD nxt=IDENTIFIER { l.add($nxt.text); })*)
	;
	
concernParameters [CpsConcern c]
	: ^(CONCERN_PARAMETERS 
		(^(PARAM name=IDENTIFIER type=fqnAsList
			{
			// FIXME needs to be tested
			LabeledConcernReference ref = new LabeledConcernReference($name.text, type);
			setLocInfo(ref, $name);
			c.addParameter(ref);
			}
		))*
	  )
	;

// $<Filter Modules

filtermodule [CpsConcern c] returns [FilterModuleAST fm = new FilterModuleAST()]
// throws CpsSemanticException
	: ^(strt=FILTER_MODULE 
		name=IDENTIFIER
		{
			fm.setName($name.text);
			setLocInfo(fm, strt);
			if (c.getFilterModuleAST(fm.getName()) != null)
			{
				throw new CpsSemanticException(String.format("Duplicate filter module name \%s in concern \%s", 
					fm.getName(), c.getQualifiedName()), fm);
			}
			fm.setParent(c);
			c.addFilterModuleAST(fm);
		}
		(filtermoduleParameters[fm])? 
		(internal[fm])* 
		(external[fm])* 
		(condition[fm])* 
		(inputfilters[fm])? 
		(outputfilters[fm])?
	  )
	;
	
filtermoduleParameters [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(PARAMS (prm=fmParamEntry
	  {
	  	if (!fm.parameterExists(prm))
	  	{
	  		prm.setParent(fm);
	  		fm.addParameter(prm);
	  	}
	  	else {
	  		throw new CpsSemanticException(String.format("Parameter \%s not unique within filtermodule \%s", prm.getName(), fm.getQualifiedName()), prm);
	  	}
	  }
	  )+)
	;
	
fmParamEntry returns [FilterModuleParameterAST param = new FilterModuleParameterAST()]
	: sn=singleFmParam 
	  {//FIXME needs to be tested
		param.setName($sn.text); 
		setLocInfo(param, $sn.start);
	  }
	| ln=fmParamList 
	  {
	  	param.setName($ln.text); 
		setLocInfo(param, $ln.start);
	  }
	;	
	
singleFmParam
	: ^(FM_PARAM_SINGLE IDENTIFIER)
	;

fmParamList
	: ^(FM_PARAM_LIST IDENTIFIER)
	;		

fqnOrSingleFmParam
	: fqn | singleFmParam
	;
	
/**
 * Returns a concern reference instance.
 * Used by: internal, external
 */	
concernReference returns [ConcernReference ref]
@init {
	List<String> tp = new ArrayList<String>();
}
	: // this mimics the fqnAsList
	  ^(FQN first=IDENTIFIER {tp.add($first.text);} (PERIOD nxt=IDENTIFIER {tp.add($nxt.text);})*
	  {
	  	ref = new ConcernReference(tp);
	  	setLocInfo(ref, $first);
	  }
	  )
	;	
	
identifierOrSingleFmParam
	: IDENTIFIER | singleFmParam
	;	

internal [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(INTERNAL (ref=concernReference | prm=singleFmParam) ^(NAMES (
		name=IDENTIFIER
		{
			InternalAST internal;
			if (ref != null)
			{
				internal = new InternalAST();
				internal.setType(ref);			
			}
			else if (prm != null)
			{
				internal = new ParameterizedInternalAST();
				((ParameterizedInternalAST) internal).setParameter($prm.text);
			}
			else {
				throw new CpsSemanticException(String.format("Internal \%s is not an qualified name or parameter", 
					$name.text), sourceFile, name);
			}
			internal.setName($name.text);
			internal.setParent(fm);
			setLocInfo(internal, $name);
			if (!fm.addInternal(internal))
			{
				throw new CpsSemanticException(String.format("Internal name \%s is not unqiue in filter module \%s", 
					internal.getName(), fm.getQualifiedName()), internal);
			}
		}
	  )+))
	;

externalConcernReference returns [ExternalConcernReference ecr]
// throws CpsSemanticException
@init {
	List<String> lst = new ArrayList<String>();
}
	: ^(FQN first=IDENTIFIER {lst.add($first.text);} (PERIOD nxt=IDENTIFIER {lst.add($nxt.text);})*
	  {
		if (lst.size() < 2)
		{
			throw new CpsSemanticException(String.format("Invalid external initializer: \%s", lst.toString()), sourceFile, first);
		}
		// the fqn is: ns.type.method
		// concern reference should point to: ns.type
		// initial target should be: ns.type
		// initial selector should me: method
		ecr = new ExternalConcernReference(lst.subList(0, lst.size()-1));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lst.size()-1; i++)
		{
			if (sb.length() > 0) sb.append(".");
			sb.append(lst.get(i));
		}
		ecr.setInitSelector(lst.get(lst.size()-1));
		ecr.setInitTarget(sb.toString());
		setLocInfo(ecr, $first);
	  }
	  )
	| prm=singleFmParam {if (prm != null) throw new CpsSemanticException(String.format("Filter Module Parameters are not supported in the external initializer"), sourceFile, $prm.start);}
	/* params */
	;

external [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(EXTERNAL name=IDENTIFIER (ref=concernReference | prm=singleFmParam) (^(INIT init=externalConcernReference))?
	  {
	  	External external = new External();
	  	if (ref != null)
		{
			external.setType(ref);			
		}
		else if (prm != null)
		{
			// apperently there is no special External instance of parameterized externals!?
			throw new CpsSemanticException(String.format("Parameterized externals are currently not supported"), 
				sourceFile, name);
		}
		else {
			throw new CpsSemanticException(String.format("External \%s is not an qualified name or parameter", 
				$name.text), sourceFile, name);
		}
		external.setName($name.text);
		external.setParent(fm);
		if (init != null)
		{
			external.setShortinit(init);
		}
		setLocInfo(external, $name);
		if (!fm.addExternal(external))
		{
			throw new CpsSemanticException(String.format("External name \%s is not unqiue in filter module \%s", 
				external.getName(), fm.getQualifiedName()), external);
		}
	  }
	  )
	;	

condition [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(CONDITION name=IDENTIFIER
	  {
	  	Condition cond = new Condition();
	  	cond.setName($name.text);
		cond.setParent(fm);
		setLocInfo(cond, $name);
	  } 
	  ( ^(FQN first=IDENTIFIER 
	    {
	    	List<String> lst = new ArrayList<String>();
	    	lst.add($first.text);
	    } 
		(PERIOD nxt=IDENTIFIER {lst.add($nxt.text);})*
	    {
	    	if (lst.size() < 2)
		{
			throw new CpsSemanticException(String.format("Invalid conditionalSi method: \%s", lst.toString()), sourceFile, first);
		}
		String tar = lst.get(0);
		String sel = lst.get(lst.size()-1);
		if (tar.equals(Target.INNER) && (lst.size() == 2))
		{
			// inner object
			DeclaredObjectReference dor = new DeclaredObjectReference();
			dor.setName(Target.INNER);
			setLocInfo(dor, $first);
			cond.setShortref(dor);
		}
		else if ((lst.size() == 2) && (fm.isExternal(tar) || fm.isInternal(tar)))
		{
			// internal or external
			DeclaredObjectReference dor = new DeclaredObjectReference();
			dor.setName(tar);
			dor.setFilterModule(fm.getName());
			CpsConcern conc = (CpsConcern) fm.getParent();
			dor.setConcern(conc.getName());
			Vector ns = new Vector();
			String[] ns_ = conc.getNamespace().split("\\.");
			for (String s : ns_) ns.add(s);
			dor.setPackage(ns);
			setLocInfo(dor, $first);
			cond.setShortref(dor);
		}
		else 
		{
			// static method call
			ConcernReference ref = new ConcernReference(lst.subList(0, lst.size()-1));
		  	setLocInfo(ref, $first);
		  	cond.setShortref(ref);
		}
		cond.addDynObject("selector", sel);
	    }
	    )
	  | prm=singleFmParam 
	    {if (prm != null) throw new CpsSemanticException(String.format("Filter Module Parameters are not supported in conditionals"), sourceFile, $prm.start);}
	  )
	  /* params */ )
	;	
	
inputfilters [FilterModuleAST fm]
	: ^(INPUT_FILTERS lhs=filter[fm] (op=filterOperator rhs=filter[fm])*)
	;
	
outputfilters [FilterModuleAST fm]
	: ^(OUTPUT_FILTERS lhs=filter[fm] (op=filterOperator rhs=filter[fm])*)
	;

// $<Filter


filterOperator
	: SEMICOLON
	;

filter [FilterModuleAST fm] returns [FilterAST filter]
	: ^(FILTER IDENTIFIER identifierOrSingleFmParam filterElement+)
	;
	
filterElement
	: ^(FILTER_ELEMENT (^(EXPRESSION conditionExpression))? (^(OPERATOR matchingOperator))? messagePatternSet)
	;

// $<Condition Expression

conditionExpression
	: ^(OR conditionExpression conditionExpression )
	| andExpr
	;

andExpr
	: ^(AND conditionExpression conditionExpression )
	| unaryExpr
	;
	
unaryExpr
	: ^(NOT conditionExpression)
	| operandExpr
	;
	
operandExpr
	: IDENTIFIER // literals (True, False) are resolved by the tree walker
	;

// $> Condition Expression

matchingOperator
	: ENABLE | DISABLE
	;
	
messagePatternSet
	: ^(MATCHING_PART matchingPart) (^(SUBST_PART substitutionPart))?
	;	
	
matchingPart
	: ^(LIST matchingPatternList+)
	| matchingPatternList
	;

matchingPatternList 
	: ^(MESSAGE_LIST matchingPattern+)
	| matchingPattern
	;

matchingPattern
	: ^(NAME targetSelector)
	| ^(SIGN targetSelector)
	;
	
substitutionPart
	: targetSelector
	| ^(MESSAGE_LIST targetSelector+)
	;		
	
targetSelector
	: target selector
	| selector
	;

target
	: ^(TARGET identifierOrSingleFmParam)
	| ^(TARGET ASTERISK)
	;

selector
	: ^(SELECTOR IDENTIFIER)
	| ^(SELECTOR ASTERISK)
	| ^(SELECTOR fmParamEntry)
	;

// $> Filter


// $> Filter Module
	
// $<Superimposition

superimposition [CpsConcern c]
	: ^(SUPERIMPOSITION conditionalSi* selectorSi* filtermoduleSi* annotationSi* constraint*)
	;

conditionalSi
	: ^(CONDITION IDENTIFIER fqn)
	;
	
selectorSi
	: ^(SELECTOR IDENTIFIER selectorExprLegacy)
	| ^(SELECTOR IDENTIFIER selectorExprPredicate)
	;

selectorExprLegacy
	: ^(LEGACY_SELECTOR fqn)
	;
	
selectorExprPredicate
	: ^(PREDICATE_SELECTOR IDENTIFIER PROLOG_EXPR )
	;		
	
filtermoduleSi
	: ^(FM_BINDINGS IDENTIFIER (^(CONDITION IDENTIFIER))? fmBinding+)
	;
	
fmBinding
	: ^(BINDING concernFmRef (^(PARAMS param+))?)
	;	

concernFmRef
	: fqn (DOUBLECOLON IDENTIFIER)?
	;		
	
param
	: ^(LIST fqn+)
	| fqn
	;		

annotationSi
	: ^(ANNOTATION_BINDINGS IDENTIFIER fqn+)
	;	
	
constraint
	: ^(CONSTRAINT preConstraint)
	;

preConstraint
	: ^(PRE concernFmRef concernFmRef)
	;				

// $>
	
// $<Implementation

implementation [CpsConcern c]
	: ^(IMPLEMENTATION IDENTIFIER IDENTIFIER FILENAME)
	;
		
// $>
		
