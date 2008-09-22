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
 * (2008-05-09) michielh	Added constraint processing for FILTH2
 * (2008-09-17) michielh	COPPER3, various changes to the grammer to better
 *				suit the new repository model and canonical filter notation.
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
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 */
 
package Composestar.Core.COPPER3;

import Composestar.Core.CpsRepository2.*;
import Composestar.Core.CpsRepository2.FilterElements.*;
import Composestar.Core.CpsRepository2.FilterModules.*;
import Composestar.Core.CpsRepository2.Filters.*;
import Composestar.Core.CpsRepository2.FMParams.*;
import Composestar.Core.CpsRepository2.References.*;
import Composestar.Core.CpsRepository2.SuperImposition.*;
import Composestar.Core.CpsRepository2.TypeSystem.*;

import Composestar.Core.CpsRepository2Impl.*;
import Composestar.Core.CpsRepository2Impl.FilterElements.*;
import Composestar.Core.CpsRepository2Impl.FilterModules.*;
import Composestar.Core.CpsRepository2Impl.Filters.*;
import Composestar.Core.CpsRepository2Impl.FMParams.*;
import Composestar.Core.CpsRepository2Impl.References.*;
import Composestar.Core.CpsRepository2Impl.SuperImposition.*;
import Composestar.Core.CpsRepository2Impl.TypeSystem.*;

import Composestar.Core.Exception.*;

import java.util.Vector;
}

concern returns [CpsConcern c]
// throws CpsSemanticException
@init {
	ns = new ArrayList<String>();
}
	: ^(strt=CONCERN
		name=IDENTIFIER
		(concernParameters)?
		(^(IN ns=fqnAsList))?
		{
			c = new CpsConcernImpl($name.text, ns);
			setLocInfo(c, strt);
			repository.add(c);
		}
		(filtermodule[c])*
		(superimposition[c])?
		(implementation[c])?
		)
	;
	
fqn returns [String res]
@init {
	StringBuffer sb = new StringBuffer();
}
	: ^(FQN (id=IDENTIFIER
		{
			if (sb.length() > 0) sb.append('.');
			sb.append($id.text);
		}
		)+
		{
			res = sb.toString();
		})
	;

fqnAsList returns [List<String> l = new ArrayList<String>();]
	: ^(FQN (idf=IDENTIFIER {l.add($idf.text);} )+)
	;
	
concernParameters
// obsolete
	: ^(CONCERN_PARAMETERS 
		(^(PARAM name=IDENTIFIER type=fqnAsList))*
		)
	;

// $<Filter Modules

filtermodule [CpsConcern c] returns [FilterModule fm]
// throws CpsSemanticException
	: ^(strt=FILTER_MODULE 
		name=IDENTIFIER
		{
			try {
				fm = new FilterModuleImpl($name.text);
				setLocInfo(fm, strt);
				if (!c.addFilterModule(fm))
				{
					throw new CpsSemanticException(String.format("Duplicate filter module name \"\%s\" in concern: \%s", 
						fm.getName(), c.getFullyQualifiedName()), input, $name);
				}
				repository.add(fm);
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
	
filtermoduleParameters [FilterModule fm]
// throws CpsSemanticException
	: ^(PARAMS ({Tree errTok = (Tree) input.LT(1);} prm=fmParamEntry
		{
			try {
				if (fm.addParameter(prm))
				{
					repository.add(prm);
				}
				else {
					throw new CpsSemanticException(String.format("Parameter \"\%s\" not unique within filtermodule: \%s", 
						prm.getName(), fm.getFullyQualifiedName()), input, errTok);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		)+)
	;
	
fmParamEntry returns [FMParameter param]
	: sn=singleFmParam 
		{
			param = new FMParameterImpl($sn.text); 
			setLocInfo(param, $sn.start);
		}
	| ln=fmParamList 
		{
			param = new FMParameterImpl($ln.text);
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
// FIXME remove
concernReference returns [Object ref]
	:
		^(FQN first=IDENTIFIER (PERIOD nxt=IDENTIFIER)*)
	;	
	
identifierOrSingleFmParam
	: IDENTIFIER | singleFmParam
	;	

internal [FilterModule fm]
// throws CpsSemanticException
	: ^(INTERNAL (ref=fqn | prm=singleFmParam) ^(NAMES (
		name=IDENTIFIER
		{
			try {
				Internal internal = new InternalImpl($name.text);
				TypeReference tr = null;
				if (ref != null)
				{
					tr = references.getTypeReference(ref);			
				}
				else if (prm != null)
				{
					// FIXME: create parameterized type ref
					FMParameter fmp = fm.getParameter($prm.text);
					if (fmp == null) {
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
							fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
					}
				}
				else {
					throw new CpsSemanticException(String.format("Internal \"\%s\" is not a fully qualified name or parameter", 
						$name.text), input, name);
				}
				internal.setTypeReference(tr);
				setLocInfo(internal, $name);
				if (!fm.addVariable(internal))
				{
					throw new CpsSemanticException(String.format("Internal name \"\%s\" is not unqiue in filter module: \%s", 
						internal.getName(), fm.getFullyQualifiedName()), input, name);
				}
				repository.add(internal);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		)+))
	;

// FIXME remove
externalConcernReference returns [Object ecr]
// throws CpsSemanticException
@init {
	List<String> lst = new ArrayList<String>();
}
	: ^(FQN first=IDENTIFIER (PERIOD nxt=IDENTIFIER)*)
	| prm=singleFmParam
	/* params */
	;
	
methodReference[FilterModule fm] returns [MethodReference mref]
// throws CpsSemanticException
@init {
	List<String> lst = new ArrayList<String>();
	// FIXME: should be a language construct
	JoinPointContextArgument jpca = JoinPointContextArgument.NONE;
	// FIXME: handle "inner" references 
}
	: ^(FQN first=IDENTIFIER {lst.add($first.text);} (PERIOD nxt=IDENTIFIER {lst.add($nxt.text);})*
		{
			try {
				if (lst.size() < 2)
				{
					throw new CpsSemanticException(String.format("Invalid method reference: \%s", lst.toString()), 
						input, first);
				}
				if (lst.size() == 2)
				{
					FilterModuleVariable fmvar = fm.getVariable(lst.get(0));
					if (fmvar instanceof CpsObject)
					{
						CpsObject ctx = (CpsObject) fmvar;
						mref = references.getInstanceMethodReference(lst.get(1), ctx, jpca);
					}
				}
				if (mref == null)
				{
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < lst.size()-1; i++)
					{
						if (sb.length() > 0)
						{
							sb.append('.');
						}
						sb.append(lst.get(i));			
					}
					mref = references.getMethodReference(lst.get(lst.size()-1), sb.toString(), jpca);
				}
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
				// FIXME: parameterized method reference
				FMParameter fmp = fm.getParameter($prm.text);
				if (fmp == null) {
					throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
						fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
	/* params */
	;

external [FilterModule fm]
// throws CpsSemanticException
	: ^(EXTERNAL name=IDENTIFIER (ref=fqn | prm=singleFmParam) (^(INIT init=methodReference[fm]))?
		{
			try {
				External external = new ExternalImpl($name.text);
				TypeReference tr = null;
				if (ref != null)
				{
					tr = references.getTypeReference(ref);			
				}
				else if (prm != null)
				{
					// FIXME: create parameterized type ref
					FMParameter fmp = fm.getParameter($prm.text);
					if (fmp == null) {
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
							fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
					}
				}
				else {
					throw new CpsSemanticException(String.format("External \"\%s\" is not a fully qualified name or parameter", 
						$name.text), input, name);
				}			
				if (init != null)
				{
					external.setMethodReference(init);
				}
				setLocInfo(external, $name);
				if (!fm.addVariable(external))
				{
					throw new CpsSemanticException(String.format("External name \"\%s\" is not unqiue in filter module: \%s", 
						external.getName(), fm.getFullyQualifiedName()), input, name);
				}
				repository.add(external);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		)
	;	

condition [FilterModule fm]
// throws CpsSemanticException
	: ^(CONDITION name=IDENTIFIER mref=methodReference[fm]
		{
			try {
				Condition cond = new ConditionImpl($name.text);
				cond.setMethodReference(mref);
				setLocInfo(cond, $name);
				if (!fm.addVariable(cond))
				{
					throw new CpsSemanticException(String.format("Condition name \"\%s\" is not unqiue in filter module: \%s", 
						cond.getName(), fm.getFullyQualifiedName()), input, name);
				}
				repository.add(cond);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		)
	;	
	
filterExpression [FilterModule fm] returns [FilterExpression expr]
// throws CpsSemanticException
	: op=filterOperator lhs=filterExpression[fm] rhs=filterExpression[fm]
		{
			op.setLHS(lhs);
			op.setRHS(rhs);
			expr = op;
		}
	| flt=filter[fm] {expr = flt;}
	;

inputfilters [FilterModule fm]
// throws CpsSemanticException
	: ^(INPUT_FILTERS expr=filterExpression[fm]
		{
			fm.setInputFilterExpression(expr);
		}
		)
	;
	
outputfilters [FilterModule fm]
// throws CpsSemanticException
	: ^(OUTPUT_FILTERS expr=filterExpression[fm]
		{
			fm.setOutputFilterExpression(expr);
		}
		)
	;

// $<Filter

filterOperator returns [BinaryFilterOperator op]
	: ^(seq=SEMICOLON 
		{
			op = new SequentialFilterOper();
			setLocInfo(op, $seq);
			repository.add(op);
		}
		)
	;

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filter [FilterModule fm] returns [Filter filter]
// throws CpsSemanticException
	: ^(frst=FILTER name=IDENTIFIER ft=filterType 
		{
			try {
				if (!fm.isUniqueMemberName($name.text)) {
					throw new CpsSemanticException(String.format("Filter name \"\%s\" is not unqiue in filter module: \%s", 
					$name.text, fm.getFullyQualifiedName()), input, name);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
			filter = new FilterImpl($name.text);
			setLocInfo(filter, $frst);
			filter.setOwner(fm); // done so that the correct FQN is produced in repository.add(.._
			filter.setType(ft);
			repository.add(filter);
		}
		// TODO: process assingments
		// FIXME: process filter element expression
		)
	;
	
filterType returns [FilterType ft]
// throws CpsSemanticException
@init {
	Tree errTok = (Tree) input.LT(1);
}
	: ftName=fqn
		{
			try {
				// TODO: handle filter module as filter type
				ft = filterTypes.getFilterType(ftName);
				if ((ft == null) && (filterFactory.allowLegacyCustomFilters()))
				{
					// TODO: do something with custom filters
					/*
					logger.info(String.format("Creating legacy custom filter with name: \%s", ftName));
					try {
						ft = filterFactory.createLegacyCustomFilterType(ftName);
					}
					catch (UnsupportedFilterTypeException e)
					{
						logger.info(String.format("Error creating custom filter type: \%s", ftName));
					}
					*/
				}
				if (ft == null)
				{
					throw new CpsSemanticException(String.format("Undefined filter type: \%s", ftName), input, errTok);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
	;	
	
filterElementOperator returns [BinaryFilterElementOperator op]
	: or=COMMA 
		{
			op = new CORfilterElementCompOper();
			setLocInfo(op, $or);
		}
	;	

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filterElement [FilterModule fm] returns [FilterElement fe]
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

conditionExpression [FilterModule fm] returns [ConditionExpression ex]
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

andExpr [FilterModule fm] returns [ConditionExpression ex]
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
	
unaryExpr [FilterModule fm] returns [ConditionExpression ex]
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
	
operandExpr [FilterModule fm] returns [ConditionExpression ex]
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
	
messagePatternSet [FilterModule fm] returns [MatchingPatternAST mp = new MatchingPatternAST();]
// throws CpsSemanticException
	: ^(frst=MATCHING_PART matchingPart[mp,fm]) (^(SUBST_PART substitutionPart[mp,fm]))?
		{
			setLocInfo(mp, $frst);
		}
	;	
	
matchingPart [MatchingPattern mp, FilterModule fm]
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

matchingPattern [FilterModule fm] returns [MatchingPartAST mp = new MatchingPartAST()]
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
			SuperImposition si = new SuperImpositionImpl();
			setLocInfo(si, $strt);
			c.setSuperImposition(si);
			repository.add(si);
			
			// default selector
			Selector selfSel = new LegacySelector("self", c.getFullyQualifiedName(), false);
			repository.add(selfSel);
			si.addSelector(selfSel);
		}
		conditionalSi[si]* selectorSi[si]* filtermoduleSi[si]* annotationSi[si]* constraint[si]*
		)
	;

conditionalSi [SuperImposition si]
// throws CpsSemanticException
	: ^(strt=CONDITION name=IDENTIFIER expr=fqnAsList
		{
			try {
				SICondition cond = new SIConditionImpl($name.text);
				setLocInfo(cond, $strt);
				MethodReference mref;
				cond.setMethodReference(mref);			
				if (!si.addCondition(cond))
				{
					throw new CpsSemanticException(String.format("Condition name \"\%s\" is not unqiue within superimposition for: \%s",
						cond.getName(), si.getFullyQualifiedName()), input, $strt);
				}
				repository.add(cond);
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
	: ^(SELECTOR nm1=IDENTIFIER ^(LEGACY_SELECTOR (eq=EQUALS | co=COLON) expr1=fqn)
		{
			try {
				Selector sel = new LegacySelector($nm1.text, expr1, co != null);
				setLocInfo(sel, $nm1);
				if (!si.addSelector(sel))
				{
					throw new CpsSemanticException(String.format("Selector name \"\%s\" is not unqiue within superimposition for: \%s",
						sel.getName(), si.getFullyQualifiedName()), input, nm1);
				}
				repository.add(sel);
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
				Selector sel = new PredicateSelector($nm2.text, $var.text, $expr.text);
				setLocInfo(sel, $nm2);
				if (!si.addSelector(sel))
				{
					throw new CpsSemanticException(String.format("Selector name \"\%s\" is not unqiue within superimposition for: \%s",
						sel.getName(), si.getFullyQualifiedName()), input, nm2);
				}
				repository.add(sel);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		)
	;
	
filtermoduleSi [SuperImposition si]
// throws CpsSemanticException
@init {
	Selector sel = null;
	SICondition cond = null;
}
	: ^(strt=FM_BINDINGS selName=IDENTIFIER 
		{
			try {
				sel = si.getSelector($selName.text);
				if (sel == null)
				{
					throw new CpsSemanticException(String.format("No selector with the name \"\%s\" in: \%s",
						$selName.text, si.getFullyQualifiedName()), input, selName);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		(^(CONDITION condName=IDENTIFIER
			{
				try {
					cond = si.getCondition($condName.text);
					if (cond == null)
					{
						throw new CpsSemanticException(String.format("No condition with the name \"\%s\" in: \%s",
							$condName.text, si.getFullyQualifiedName()), input, condName);
					}
				}
				catch (RecognitionException re) {
					reportError(re);
					recover(input,re);
				}
			}
		))? 
		(bnd=fmBinding[si]
		{
			bnd.setSelector(sel);
			si.addFilterModuleBinding(bnd);
			repository.add(bnd);
			if (cond != null)
			{
				FilterModuleConstraint fmc = new FilterModuleConstraintImpl("cond");
				setLocInfo(fmc, strt);
				List<ConstraintValue> args = new ArrayList<ConstraintValue>();
				ConstraintValue cv = new ConditionConstraintValueImpl(cond);
				repository.add(cv);
				args.add(cv);
				cv = new FilterModuleConstraintValueImpl(bnd.getFilterModuleReference());
				repository.add(cv);
				args.add(cv);
				fmc.setArguments(args);
				si.addFilterModuleConstraint(fmc);
				repository.add(fmc);
			}
		}
		)+)
	;
	
fmBinding [SuperImposition si] returns [FilterModuleBinding fmb = new FilterModuleBindingImpl()]
// throws CpsSemanticException
@init {
	if (si.getOwner() == null || !(si.getOwner() instanceof CpsConcern)) 
	{
		throw new CpsSemanticException("Superimposition is not owned by a CpsConcern", input);
	}
	CpsConcern concern = (CpsConcern) si.getOwner();
}
	: ^(strt=BINDING fmr=concernFmRef[si] 
		{
			try {
				setLocInfo(fmb, $strt);
				fmb.setFilterModuleReference(fmr);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
		}
		(^(PARAMS
			{
				List<FMParameterValue> args = new ArrayList<FMParameterValue>();
			} 
				(prm=param[si]
				{
					args.add(prm);
				}
				)+
			{
				fmb.setParameterValues(args);
			}
			)
		)?)
	;	
	
// TODO: !!!
param [SuperImposition si] returns [FMParameterValue fmp]
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
	: ^(strt=ANNOTATION_BINDINGS sel=IDENTIFIER
		{
			AnnotationBinding ab = new AnnotationBindingImpl();
			setLocInfo(ab, $strt);
			try {
				Selector selector = si.getSelector($sel.text);
				if (selector == null)
				{
					throw new CpsSemanticException(String.format("No selector with the name \"\%s\" in: \%s",
						$sel.text, si.getFullyQualifiedName()), input, sel);
				}
				ab.setSelector(selector);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			}
			si.addAnnotationBinding(ab);
			repository.add(ab);
		}
		(at=fqn
		{
			ab.addAnnotation(references.getTypeReference(at));		
		}
		)+)
	;
	

concernFmRef [SuperImposition si] returns [FilterModuleReference res]
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
					CpsConcern cpsc = (CpsConcern) si.getOwner();
					res = cpsc.getFilterModule(fr);
					if (res == null)
					{
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a filter module with the name \"\%s\"", 
							cpsc.getFullyQualifiedName(), fr), input, errTok);
					}
				}
				else {
					// external reference
					res = references.getFilterModuleReference(fr + "." + $id.text);
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
	: ^(CONSTRAINT opr=IDENTIFIER 
		{
			FilterModuleConstraint fmc = new FilterModuleConstraintImpl($opr.text);
			setLocInfo(fmc, opr);
			si.addFilterModuleConstraint(fmc);
			repository.add(fmc);
			List<FilterModuleConstraintValue> args = new ArrayList<FilterModuleConstraintValue>();
		}
		(lhs=concernFmRef[si]
			{
				// ...
			}
		)+
		{
			try {
				try {
					fmc.setArguments(args);
				}
				catch (IllegalArgumentException e) {
					throw new CpsSemanticException(IllegalArgumentException.getMessage(), input, opr);
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

// TODO
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
		
