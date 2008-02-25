/*
 * Tree Walker. uses the output of Cps.g
 *
 * Important note concerning throwing CpsSemanticException :
 * ALWAYS include a catch, report, recover in the same code scope.
 * this way multiple errors can be reported at a single parse 
 * iteration.
 *
 * $Id$
 *
 * Grammar Changes:
 * (2007-10-12) michielh	Added source code extraction from the
 *				implementation block. Added graceful
 *				error recovery.
 * (2007-10-15) michielh	Added constraint processing.
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
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.*;
import Composestar.Core.CpsProgramRepository.Filters.*;
import Composestar.Core.Exception.*;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;

import java.util.Vector;
}

concern returns [CpsConcern c = new CpsConcern();]
// throws CpsSemanticException
	: ^(strt=CONCERN {setLocInfo(c, strt);}
		name=IDENTIFIER {c.setName($name.text);}
		(concernParameters[c])?
		(^(IN ns=fqnAsList {c.setNamespace(ns);}))?
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
		try {
			fm.setName($name.text);
			setLocInfo(fm, strt);
			if (c.getFilterModuleAST(fm.getName()) != null)
			{
				throw new CpsSemanticException(String.format("Duplicate filter module name \"\%s\" in concern: \%s", 
					fm.getName(), c.getQualifiedName()), input, $name);
			}
			fm.setParent(c);
			c.addFilterModuleAST(fm);
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
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
	: ^(PARAMS ({Tree errTok = (Tree) input.LT(1);} prm=fmParamEntry
	  {
	  try {
	  	if (!fm.parameterExists(prm))
	  	{
	  		prm.setParent(fm);
	  		fm.addParameter(prm);
	  	}
	  	else {
	  		throw new CpsSemanticException(String.format("Parameter \"\%s\" not unique within filtermodule: \%s", 
	  			prm.getName(), fm.getQualifiedName()), input, errTok);
	  	}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )+)
	;
	
fmParamEntry returns [FilterModuleParameterAST param = new FilterModuleParameterAST()]
	: sn=singleFmParam 
	  {
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
		try {
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
				throw new CpsSemanticException(String.format("Internal \"\%s\" is not an qualified name or parameter", 
					$name.text), input, name);
			}
			internal.setName($name.text);
			internal.setParent(fm);
			setLocInfo(internal, $name);
			if (!fm.addInternal(internal))
			{
				throw new CpsSemanticException(String.format("Internal name \"\%s\" is not unqiue in filter module: \%s", 
					internal.getName(), fm.getQualifiedName()), input, name);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
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
	  try {
		if (lst.size() < 2)
		{
			throw new CpsSemanticException(String.format("Invalid external initializer: \%s", lst.toString()), 
				input, first);
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
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	| prm=singleFmParam 
	  {
	  try {
		throw new CpsSemanticException(String.format("Filter Module Parameters are not supported in the external initializer"), input, $prm.start);
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	/* params */
	;

external [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(EXTERNAL name=IDENTIFIER (ref=concernReference | prm=singleFmParam) (^(INIT init=externalConcernReference))?
	  {
	  try {
	  	External external = new External();
	  	if (ref != null)
		{
			external.setType(ref);			
		}
		else if (prm != null)
		{
			// apperently there is no special External instance of parameterized externals!?
			throw new CpsSemanticException(String.format("Parameterized externals are currently not supported"), 
				input, name);
		}
		else {
			throw new CpsSemanticException(String.format("External \"\%s\" is not an qualified name or parameter", 
				$name.text), input, name);
		}
		external.setName($name.text);
		external.setParent(fm);
		if (init != null)
		{
			external.setShortinit(init);
		}
		else {
		  // C*/C uses no initializer
			//throw new CpsSemanticException(String.format("Static object references are not implemented. An initialization expression is required."), 
			//	input, name);
		}
		setLocInfo(external, $name);
		if (!fm.addExternal(external))
		{
			throw new CpsSemanticException(String.format("External name \"\%s\" is not unqiue in filter module: \%s", 
				external.getName(), fm.getQualifiedName()), input, name);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
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
		fm.addCondition(cond);
		setLocInfo(cond, $name);
	  } 
	  ( ^(FQN first=IDENTIFIER 
	    {
	    	List<String> lst = new ArrayList<String>();
	    	lst.add($first.text);
	    } 
		(PERIOD nxt=IDENTIFIER {lst.add($nxt.text);})*
	    {
	    try {
	    	if (lst.size() < 2)
		{
			throw new CpsSemanticException(String.format("Invalid conditional method: \%s", lst.toString()), 
				input, first);
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
			dor.setPackage(conc.getNamespace());
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
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	    )
	  | prm=singleFmParam 
	    {
	    try {
	    	throw new CpsSemanticException(String.format("Filter Module Parameters are not supported in conditionals"), input, $prm.start);
	    }
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	  )
	  /* params */ )
	;	
	
inputfilters [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(INPUT_FILTERS ({Tree errTok = (Tree) input.LT(1);} 
	  lhs=filter[fm]
	  {
	  try {
	  	if (!fm.addInputFilter(lhs))
		{
			throw new CpsSemanticException(String.format("Inputfilter name \"\%s\" is not unqiue in filter module: \%s", 
				lhs.getName(), fm.getQualifiedName()), input, errTok);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	  (op=filterOperator {Tree errTok = (Tree) input.LT(1);} rhs=filter[fm]
	    {
	    try {
	  	lhs.setRightOperator(op);
	  	op.setParent(lhs);
	  	op.setRightArgument(rhs);
		if (!fm.addInputFilter(rhs))
		{
			throw new CpsSemanticException(String.format("Inputfilter name \"\%s\" is not unqiue in filter module: \%s", 
				rhs.getName(), fm.getQualifiedName()), input, errTok);
		}
		lhs = rhs;
	    }
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	  )*
	  {
		FilterCompOper voidop = new VoidFilterCompOper();
		lhs.setRightOperator(voidop);
		voidop.setParent(lhs);		
	  }
	  )
	;
	
outputfilters [FilterModuleAST fm]
// throws CpsSemanticException
	: ^(OUTPUT_FILTERS ({Tree errTok = (Tree) input.LT(1);} 
	  lhs=filter[fm] 
	  {
	  try {
	  	if (!fm.addOutputFilter(lhs))
		{
			throw new CpsSemanticException(String.format("Outputfilter name \"\%s\" is not unqiue in filter module: \%s", 
				lhs.getName(), fm.getQualifiedName()), input, errTok);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	  (op=filterOperator {Tree errTok = (Tree) input.LT(1);} rhs=filter[fm]
	    {
	    try {
	  	lhs.setRightOperator(op);
	  	op.setParent(lhs);
	  	op.setRightArgument(rhs);
	 	if (!fm.addOutputFilter(rhs))
		{
			throw new CpsSemanticException(String.format("Outputfilter name \"\%s\" is not unqiue in filter module: \%s", 
				rhs.getName(), fm.getQualifiedName()), input, errTok);
		}
	  	lhs = rhs;
	    }
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	  )*
	  {
		FilterCompOper voidop = new VoidFilterCompOper();
		lhs.setRightOperator(voidop);
		voidop.setParent(lhs);		
	  }
	  )
	;

// $<Filter

filterOperator returns [FilterCompOper op]
	: seq=SEMICOLON 
	  {
	  	op = new SEQfilterCompOper();
	  	setLocInfo(op, $seq);
	  }
	;

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filter [FilterModuleAST fm] returns [FilterAST filter = new FilterAST();]
// throws CpsSemanticException
	: ^(frst=FILTER name=IDENTIFIER ft=filterType 
	  {
	  	setLocInfo(filter, $frst);
	  	filter.setName($name.text);
	  	filter.setParent(fm);
	  	filter.setFilterType(ft);
	  }
	  (^(PARAMS (prm=identifierOrSingleFmParam
	    {
	  	// fm params
	  	filter.addParameter($prm.text);
	    }
	  )+))?
	  lhs=filterElement[fm] 
	  {
	  	lhs.setParent(filter);
	  	filter.addFilterElement(lhs);
	  }
	  (op=filterElementOperator rhs=filterElement[fm]
	    {
		lhs.setRightOperator(op);
		op.setParent(lhs);
		op.setRightArgument(rhs);
		rhs.setParent(filter);
		filter.addFilterElement(rhs);
		lhs=rhs;
	    }
	  )*
	  {
	  	VoidFilterElementCompOper vop = new VoidFilterElementCompOper();
	  	lhs.setRightOperator(vop);
	  	vop.setParent(lhs);
	  }
	  )
	;
	
filterType returns [FilterType ft]
// throws CpsSemanticException
	: name=IDENTIFIER
	  {
	  try {
	  	String ftName = $name.text;
	  	ft = filterTypes.getFilterType(ftName);
	  	if ((ft == null) && (filterFactory.allowLegacyCustomFilters()))
		{
			logger.info(String.format("Creating legacy custom filter with name: \%s", ftName));
			try {
				ft = filterFactory.createLegacyCustomFilterType(ftName);
			}
			catch (UnsupportedFilterTypeException e)
			{
				logger.info(String.format("Error creating custom filter type: \%s", ftName));
			}
		}
		if (ft == null)
		{
			throw new CpsSemanticException(String.format("Undefined filter type: \%s", ftName), input, name);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	| prm=singleFmParam
	  {
	  try {
	  	throw new CpsSemanticException(String.format("Filter Module Parameters are not supported as filter type"), input, $prm.start);
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	;	
	
filterElementOperator returns [FilterElementCompOper op]
	: or=COMMA 
	  {
	  	op = new CORfilterElementCompOper();
	  	setLocInfo(op, $or);
	  }
	;	

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filterElement [FilterModuleAST fm] returns [FilterElementAST fe = new FilterElementAST();]
// throws CpsSemanticException
	: ^(frst=FILTER_ELEMENT 
	  {
	  	setLocInfo(fe, $frst);
	  }
	  (^(EXPRESSION ex=conditionExpression[fm]))? 
	  (^(OPERATOR op=matchingOperator))?
	  {
	  	if (ex == null)
	  	{
	  		ex = new True();
	  		setLocInfo(ex, $frst);
	  	}
	  	ex.setParent(fe);
	  	fe.setConditionPart(ex);
	  	
	  	if (op == null)
	  	{
	  		op = new EnableOperator();
	  		setLocInfo(op, $frst);
	  	}
	  	op.setParent(fe);
	  	fe.setEnableOperatorType(op);
	  }
	  mp=messagePatternSet[fm]
	  {
	  	mp.setParent(fe);
	  	fe.setMatchingPattern(mp);
	  }
	  )
	;

// $<Condition Expression

conditionExpression [FilterModuleAST fm] returns [ConditionExpression ex]
	: ^(frst=OR lhs=conditionExpression[fm] rhs=conditionExpression[fm] 
	    {
	    	ex = new Or();
	    	setLocInfo(ex, $frst);
	    	lhs.setParent(ex);
	    	rhs.setParent(ex);
	    	((BinaryOperator) ex).setLeft(lhs);
	    	((BinaryOperator) ex).setRight(rhs);
	    }
	  )
	| andex=andExpr[fm] {ex = andex;}
	;

andExpr [FilterModuleAST fm] returns [ConditionExpression ex]
	: ^(frst=AND lhs=conditionExpression[fm] rhs=conditionExpression[fm] 
	    {
	    	ex = new And();
	    	setLocInfo(ex, $frst);
	    	lhs.setParent(ex);
	    	rhs.setParent(ex);
	    	((BinaryOperator) ex).setLeft(lhs);
	    	((BinaryOperator) ex).setRight(rhs);
	    }
	  )
	| unex=unaryExpr[fm] {ex = unex;}
	;
	
unaryExpr [FilterModuleAST fm] returns [ConditionExpression ex]
	: ^(frst=NOT oper=conditionExpression[fm]
	    {
	  	ex = new Not();
	  	setLocInfo(ex, $frst);
	  	oper.setParent(ex);
	  	((Not) ex).setOperand(oper);
	    }
	  )
	| oex=operandExpr[fm] {ex = oex;}
	;
	
operandExpr [FilterModuleAST fm] returns [ConditionExpression ex]
	: name=IDENTIFIER // literals (True, False) are resolved by the tree walker
	  {
	  	String oper = $name.text;
	  	if ("true".equalsIgnoreCase(oper))
	  	{
	  		ex = new True();
	  	}
	  	else if ("false".equalsIgnoreCase(oper))
	  	{
	  		ex = new False();
	  	}
	  	else {
	  		ex = new ConditionVariable();
	  		ConditionReference cref = new ConditionReference();
	  		cref.setName(oper);
	  		cref.setFilterModule(fm.getName());
	  		CpsConcern cpsc = (CpsConcern) fm.getParent();
	  		cref.setConcern(cpsc.getName());
	  		cref.setPackage(cpsc.getNamespace());
	  		setLocInfo(cref, name);
	  		((ConditionVariable) ex).setCondition(cref);
	  	}
	  	setLocInfo(ex, $name);
	  }
	;

// $> Condition Expression

matchingOperator returns [EnableOperatorType op]
	: en=ENABLE {op = new EnableOperator(); setLocInfo(op, $en);}
	| di=DISABLE {op = new DisableOperator(); setLocInfo(op, $di);}
	;
	
messagePatternSet [FilterModuleAST fm] returns [MatchingPatternAST mp = new MatchingPatternAST();]
// throws CpsSemanticException
	: ^(frst=MATCHING_PART matchingPart[mp,fm]) (^(SUBST_PART substitutionPart[mp,fm]))?
	  {
	  	setLocInfo(mp, $frst);
	  }
	;	
	
matchingPart [MatchingPatternAST mp, FilterModuleAST fm]
// throws CpsSemanticException
	: ^(LIST (m1=matchingPattern[fm]
	    {
	    	m1.setParent(mp);
	    	mp.addMatchingPart(m1);
	    }
	  )+)
	| ^(MESSAGE_LIST 
	    (m2=matchingPattern[fm]
	      {
	      	m2.setParent(mp);
	      	mp.addMatchingPart(m2);
	      }
	    )+
	    {
	    	mp.setIsMessageList(true);
	    }
	  )
	| m3=matchingPattern[fm]
	  {
	  	m3.setParent(mp);
	  	mp.addMatchingPart(m3);
	  }
	;

matchingPattern [FilterModuleAST fm] returns [MatchingPartAST mp = new MatchingPartAST()]
// throws CpsSemanticException
	: ^(nm=NAME 
	  {
	  	MatchingType mt = new NameMatchingType();
	  	setLocInfo(mt, $nm);
	  	mt.setParent(mp);
	  	mp.setMatchType(mt);
	  	setLocInfo(mp, $nm);
	  }
	    targetSelector[mp,fm]
	  )
	| ^(sm=SIGN 
	  {
	  	MatchingType mt = new SignatureMatchingType();
	  	setLocInfo(mt, $sm);
	  	mt.setParent(mp);
	  	mp.setMatchType(mt);
	  	setLocInfo(mp, $sm);
	  }
	    targetSelector[mp,fm]
	  )
	;
	
substitutionPart [MatchingPatternAST mp, FilterModuleAST fm]
// throws CpsSemanticException
@init {
	SubstitutionPartAST sp;
}
	: {sp = new SubstitutionPartAST();}
	  ts=targetSelector[sp,fm]
	  {
	  try {
	  	if (mp.getIsMessageList())
	  	{
	  		throw new CpsSemanticException("Substitution part can not be a message list", input, $start);
	  	}
	  	setLocInfo(sp, $start);
	  	sp.setParent(mp);
		mp.addSubstitutionPart(sp);
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	| ^(strt=MESSAGE_LIST 
	    ({sp = new SubstitutionPartAST();}
	      targetSelector[sp,fm]
		{
			setLocInfo(sp, $start);
			sp.setParent(mp);
	      		mp.addSubstitutionPart(sp);
		}
	    )+
	  {
	  try {
	  	if (!mp.getIsMessageList())
	  	{
	  		throw new CpsSemanticException("Substitution part must also be a message list", input, strt);
	  	}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	;		
	
targetSelector [AbstractPatternAST ap, FilterModuleAST fm]
	: t=target[fm] s=selector
	  {
	  	t.setParent(ap);
	  	ap.setTarget(t);
	  	s.setParent(ap);
	  	ap.setSelector(s);
	  }
	| ss=selector
	  {
	  	Target tt = new Target();
	  	tt.setName("*");
	  	tt.setParent(ap);
	  	ap.setTarget(tt);
	  	
	  	ss.setParent(ap);
	  	ap.setSelector(ss);
	  }
	;

target [FilterModuleAST fm] returns [Target t = new Target();]
// throws CpsSemanticException
	: ^(TARGET ident=IDENTIFIER
	    {
	    	setLocInfo(t, $ident);
	    	if (Target.INNER.equals($ident.text))
	    	{
	    		t.setName(Target.INNER);
	    	}
	    	else {
	    		t.setName($ident.text);
	    		DeclaredObjectReference dor = new DeclaredObjectReference();
	    		dor.setName($ident.text);
	    		dor.setFilterModule(fm.getName());
	    		CpsConcern cpsc = (CpsConcern) fm.getParent();
	    		dor.setConcern(cpsc.getName());
	    		dor.setPackage(cpsc.getNamespace());
	    		t.setRef(dor);
	    	}
	    }
	  )
	| ^(TARGET p=singleFmParam
	    {
	    try {
	    	throw new CpsSemanticException("Filter module parameters have not been implemented for targets", 
	    		input, $p.start);
	    }
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	  )
	| ^(TARGET a=ASTERISK {t.setName($a.text); setLocInfo(t, $a); })
	;

selector returns [MessageSelectorAST s]
	: ^(SELECTOR ident=IDENTIFIER
	    {
	    	s = new MessageSelectorAST();
	    	s.setName($ident.text); 
	    	setLocInfo(s, $ident);
	    }
	  )
	| ^(SELECTOR a=ASTERISK
	    {
	    	s = new MessageSelectorAST();
	    	s.setName($a.text); 
	    	setLocInfo(s, $a);
	    }
	  )
	| ^(SELECTOR sp=singleFmParam
	    {
		s = new ParameterizedMessageSelectorAST();
		s.setName($sp.text); 
	    	setLocInfo(s, $sp.start);
	    }
	  )
	| ^(SELECTOR pl=fmParamList
	    {
		s = new ParameterizedMessageSelectorAST();
		s.setName($pl.text); 
		((ParameterizedMessageSelectorAST) s).setList(true);
	    	setLocInfo(s, $pl.start);
	    }
	  )
	;

// $> Filter


// $> Filter Module
	
// $<Superimposition

superimposition [CpsConcern c]
// throws CpsSemanticException
	: ^(strt=SUPERIMPOSITION 
	  {
	  	SuperImposition si = new SuperImposition();
	  	setLocInfo(si, $strt);
	  	si.setParent(c);
	  	c.setSuperImposition(si);
	  	
	  	// default selector
	  	SelectorDefinition defs = new SelectorDefinition();
	  	defs.setName("self");
	  	defs.setParent(si);
	  	setLocInfo(defs, $strt);
	  	
	  	SelClass selc = new SelClass();
	  	setLocInfo(selc, strt);
	  	Vector v = new Vector(c.getNamespace());
	  	v.add(c.getName());
	  	ConcernReference cref = new ConcernReference(v);	  	
	  	setLocInfo(cref, strt);
	  	selc.setClass(cref);
	  	selc.setClassName(c.getName());
	  	selc.setParent(defs);
	  	defs.addSelExpression(selc);
	  	
	  	si.addSelectorDefinition(defs);
	  }
	  conditionalSi[si]* selectorSi[si]* filtermoduleSi[si]* annotationSi[si]* constraint[si]*
	  )
	;

conditionalSi [SuperImposition si]
// throws CpsSemanticException
	: ^(strt=CONDITION name=IDENTIFIER expr=fqnAsList
	  {
	  try {
		Condition cond = new Condition();
		setLocInfo(cond, $strt);
		cond.setName($name.text);
		ConcernReference ref = new ConcernReference(expr.subList(0, expr.size()-1));
		setLocInfo(ref, $strt.getChild(1)); // should be FQN token
		cond.setShortref(ref);
		cond.addDynObject("selector", expr.get(expr.size()-1));
		
		cond.setParent(si);
		if (!si.addFilterModuleCondition(cond))
		{
			throw new CpsSemanticException(String.format("Condition name \"\%s\" is not unqiue within superimposition for: \%s",
				cond.getName(), si.getQualifiedName()), input, $strt);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	;
	
selectorSi [SuperImposition si]
// throws CpsSemanticException
	: ^(SELECTOR nm1=IDENTIFIER sel=selectorExprLegacy
	  {
	  try {
	  	SelectorDefinition defs = new SelectorDefinition();
	  	defs.setName($nm1.text);
	  	defs.setParent(si);
	  	setLocInfo(defs, $nm1);
	  	sel.setParent(defs);
	  	defs.addSelExpression(sel);
	  		
	  	if (!si.addSelectorDefinition(defs))
	  	{
	  		throw new CpsSemanticException(String.format("Selector name \"\%s\" is not unqiue within superimposition for: \%s",
				defs.getName(), si.getQualifiedName()), input, nm1);
	  	}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	| ^(SELECTOR nm2=IDENTIFIER ^(PREDICATE_SELECTOR var=IDENTIFIER expr=PROLOG_EXPR)
	  {
	  try {
	  	SelectorDefinition defs = new SelectorDefinition();
	  	defs.setName($nm2.text);
	  	defs.setParent(si);
	  	setLocInfo(defs, $nm2);
	  	PredicateSelector ps = new PredicateSelector($var.text, $expr.text);
	  	ps.setParent(defs);
	  	setLocInfo(ps, $var);
	  	defs.addSelExpression(ps);
	  		
	  	if (!si.addSelectorDefinition(defs))
	  	{
	  		throw new CpsSemanticException(String.format("Selector name \"\%s\" is not unqiue within superimposition for: \%s",
				defs.getName(), si.getQualifiedName()), input, nm2);
	  	}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	;

selectorExprLegacy returns [SimpleSelExpression sse]
	: ^(LEGACY_SELECTOR (eq=EQUALS | co=COLON) expr=fqnAsList
	  {
	  	if (eq != null)
	  	{
	  		SelClass sc = new SelClass();
	  		setLocInfo(sc, $eq);
	  		ConcernReference cref = new ConcernReference(expr);	  	
		  	setLocInfo(cref, $eq);
		  	sc.setClass(cref);
		  	sc.setClassName(expr.get(expr.size()-1));
	  		sse = sc;
	  	}
	  	else if (co != null)
	  	{
	  		SelClassAndSubClasses scsc = new SelClassAndSubClasses();
	  		setLocInfo(scsc, $co);
	  		ConcernReference cref = new ConcernReference(expr);	  	
		  	setLocInfo(cref, $co);
		  	scsc.setClass(cref);
		  	scsc.setClassName(expr.get(expr.size()-1));
	  		sse = scsc;
	  	}	  	
	  }
	  )
	;
	
selectorRef [SuperImposition si] returns [SelectorReference sref = new SelectorReference()]
	: sel=IDENTIFIER
	  {
	  	setLocInfo(sref, $sel);
	  	sref.setName($sel.text);
    		CpsConcern cpsc = (CpsConcern) si.getParent();
    		sref.setConcern(cpsc.getName());
    		sref.setPackage(cpsc.getNamespace());
	  }
	;
	
filtermoduleSi [SuperImposition si]
// throws CpsSemanticException
	: ^(strt=FM_BINDINGS sel=selectorRef[si] 
	  {
	  	FilterModuleBinding fmb = new FilterModuleBinding();
	  	setLocInfo(fmb, strt);
	  	fmb.setParent(si);
	  	fmb.setSelector(sel);
	  	si.addFilterModuleBinding(fmb);	  	
	  }
	  (^(CONDITION cond=IDENTIFIER
	    {
	    try {
	    	Condition condition = si.getFilterModuleCondition($cond.text);
	    	if (condition == null)
		{
			throw new CpsSemanticException(String.format("No condition with the name \"\%s\" in: \%s",
			$cond.text, si.getQualifiedName()), input, cond);
		}
	    	fmb.setFilterModuleCondition(condition);
	    }
	    catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	    }
	    }
	  ))? 
	  (bnd=fmBinding[si]
	    {
	    	fmb.addFilterModule(bnd);
        }
	  )+)
	;
	
fmBinding [SuperImposition si] returns [FilterModuleReference fmr = new FilterModuleReference();]
// throws CpsSemanticException
	: ^(strt=BINDING cref=fqnAsList (DOUBLECOLON fmName=IDENTIFIER)? 
	  {
	  try {
	  	setLocInfo(fmr, $strt);
	  	// if elm is set it's the filter module, otherwise fqn can only be
	  	// a single element
	  	if ((fmName == null) && cref.size() > 1)
	  	{
	  		StringBuffer fqns = new StringBuffer();
	  		for (String elm : cref)
	  		{
	  			if (fqns.length() > 0)
	  			{
	  				fqns.append(".");
	  			}
	  			fqns.append(elm);
	  		}
	  		throw new CpsSemanticException(String.format("\"\%s\" is not a valid filter module reference", 
	  			fqns.toString()), input, strt);
	  	}
	  	String fm;
	  	if (fmName == null)
	  	{
	  		fm = cref.get(0);
	  		CpsConcern cpsc = (CpsConcern) si.getParent();
	  		cref = new ArrayList<String>();
	  		cref.addAll(cpsc.getNamespace());
	  		cref.add(cpsc.getName());
	  	}
	  	else {
	  		fm = $fmName.text;
	  	}
	  	fmr.setName(fm);
	  	fmr.setConcern(cref.get(cref.size() - 1));
	  	fmr.setPackage(cref.subList(0, cref.size() - 1));
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  (^(PARAMS (prm=param[si]
	    {
	    	fmr.addArg(prm);
	    }
	  )+))?)
	;	
	
param [SuperImposition si] returns [FilterModuleParameter fmp = new FilterModuleParameter();]
@init {
	Vector v = new Vector();
}
	: ^(strt=LIST (lp=fqn
	    {
	    	SelectorDefinition seldef = si.getSelectorDefinitionByName(lp);
	    	if (seldef != null)
	    	{
	    		v.add(new SelectorFilterModuleParameterValue(seldef));
	    	}
	    	else {
	    		v.add(new LiteralFilterModuleParameterValue(lp));
	    	}
	    }
	  )+
	  {
	  	setLocInfo(fmp, $strt);
	  	fmp.setValue(v);
	  }
	  )
	| ^(strt=PARAM sp=fqn
	  {
	  	setLocInfo(fmp, $strt);
	  	SelectorDefinition seldef = si.getSelectorDefinitionByName(sp);
	    if (seldef != null)
	    {
	    	v.add(new SelectorFilterModuleParameterValue(seldef));
	    }
	    else {
	    	v.add(new LiteralFilterModuleParameterValue(sp));
	    }
	  	fmp.setValue(v);
	  }
	  )
	;		

annotationSi [SuperImposition si]
	: ^(strt=ANNOTATION_BINDINGS sel=selectorRef[si]
	  {
	  	AnnotationBinding ab = new AnnotationBinding();
	  	setLocInfo(ab, $strt);
	  	ab.setSelector(sel);
	  	ab.setParent(si);
	  	si.addAnnotationBinding(ab);
	  }
	  (at=concernReference
	  {
		ab.addAnnotation(at);  	
	  }
	  )+)
	;
	

concernFmRef [SuperImposition si] returns [String res]
// throws CpsSemanticException	
	: {Tree errTok = (Tree) input.LT(1);}
	  fr=fqn (DOUBLECOLON id=IDENTIFIER)?
	  {
	  try {
	  	if (fr.indexOf(".") > -1 && id == null)
	  	{
	  		throw new CpsSemanticException(String.format("\"\%s\" must refer to a local or external filter module", 
	  			fr), input, errTok);
	  	}
	  	// note keep the DOUBLECOLON -> . in sync with the naming convention
	  	if (id == null)
	  	{
	  		// local concern
	  		CpsConcern cpsc = (CpsConcern) si.getParent();
	  		res = cpsc.getQualifiedName() + "." + fr;
	  	}
	  	else {
	  		// external reference
	  		res = fr + "." + $id.text;
	  	}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	;				

constraint [SuperImposition si]
// throws CpsSemanticException
	: ^(CONSTRAINT opr=IDENTIFIER lhs=concernFmRef[si] rhs=concernFmRef[si]
	  {
	  try {
	  	if (!"pre".equalsIgnoreCase($opr.text))
	  	{
	  		throw new CpsSemanticException(String.format("Unknown filter module constraint type \"\%s\"", 
	  			$opr.text), input, opr);
	  	}
	  			
	  	// TODO: See part 2 of: http://www.mail-archive.com/composestar-developers@lists.sourceforge.net/msg00083.html
	  	
	  	SyntacticOrderingConstraint constraint = orderingconstraints.get(lhs);
		if (constraint == null)
		{
			constraint = new SyntacticOrderingConstraint(lhs);
			constraint.addRightFilterModule(rhs);
			orderingconstraints.put(lhs, constraint);
		}
		else
		{
			constraint.addRightFilterModule(rhs);
		}
	  }
	  catch (RecognitionException re) {
		reportError(re);
		recover(input,re);
	  }
	  }
	  )
	;				

// $>
	
// $<Implementation

implementation [CpsConcern c]
	: ^(astr=IMPLEMENTATION asm=fqn
	  {
	  	CompiledImplementation compimp = new CompiledImplementation();
	  	setLocInfo(compimp, $astr);
	  	compimp.setClassName(asm);
	  	c.setImplementation(compimp);	  	
	  }
	  )
	| ^(strt=IMPLEMENTATION lang=IDENTIFIER cls=fqn fn=FILENAME code=CODE_BLOCK
	  {
	  	Source src = new Source();
	  	setLocInfo(src, $strt);
		src.setLanguage($lang.text);
		src.setClassName(cls);
		src.setSourceFile($fn.text);
		src.setSource($code.text);
		c.setImplementation(src);
	  }
	  )
	;
		
// $>
		
