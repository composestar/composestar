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

import java.util.Collection;
import java.util.ArrayList;
}

concern returns [CpsConcern c]
// throws CpsSemanticException
@init {
	ns = new ArrayList<String>();
}
	: ^(strt=CONCERN
		name=IDENTIFIER
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

methodReference[FilterModule fm] returns [MethodReference mref]
// throws CpsSemanticException
@init {
	Tree errTok = (Tree) input.LT(1);
	// FIXME: should be a language construct
	JoinPointContextArgument jpca = JoinPointContextArgument.NONE;
	// FIXME: handle "inner" references 
}
	: lst=fqnAsList
		{
			try {
				if (lst.size() < 2)
				{
					throw new CpsSemanticException(String.format("Invalid method reference: \%s", lst.toString()), 
						input, errTok);
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
	: ^(OPERATOR seq=SEMICOLON 
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
			filter.setOwner(fm); // done so that the correct FQN is produced in repository.add(..)
			filter.setType(ft);
			repository.add(filter);
		}
		(^(PARAMS
			(asgn=canonAssign[fm]
				{
					try {
						try {
							filter.addArgument(asgn);
						}
						catch (IllegalArgumentException iae)
						{
							throw new CpsSemanticException(iae.getMessage(), input, name);
						}
					}
					catch (RecognitionException re) {
						reportError(re);
						recover(input,re);
					}
				}
			)*
		))?
		expr=filterElementExpression[fm]
		{
			filter.setElementExpression(expr);
		}
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
	
filterElementExpression [FilterModule fm] returns [FilterElementExpression expr]
// throws CpsSemanticException
	: op=filterElementOperator lhs=filterElementExpression[fm] rhs=filterElementExpression[fm]
		{
			op.setLHS(lhs);
			op.setRHS(rhs);
			expr = op;
		}
	| elm=filterElement[fm] {expr = elm;}
	;
	
filterElementOperator returns [BinaryFilterElementOperator op]
	: ^(OPERATOR cor='cor' 
		{
			op = new CORFilterElmOper();
			setLocInfo(op, $cor);
			repository.add(op);
		}
	)
	;	

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filterElement [FilterModule fm] returns [FilterElement fe]
// throws CpsSemanticException
	: ^(strt=FILTER_ELEMENT 
		{
			fe = new FilterElementImpl();
			setLocInfo(fe, $strt);
			repository.add(fe);
		}
		expr=matchingExpression[fm] 
		{
			fe.setMatchingExpression(expr);
		}
		(asgn=canonAssign[fm]
			{
				fe.addAssignment(asgn);
			}
		)*
	)
	;
	
canonAssign [FilterModule fm] returns [CanonAssignment asgn]
	: ^(strt=EQUALS ^(OPERAND lhs=assignLhs[fm]) ^(OPERAND rhs=assignRhs[fm])
		{
			asgn = new CanonAssignmentImpl();
			setLocInfo(asgn, strt);
			repository.add(asgn);
			asgn.setProperty(lhs);
			asgn.setValue(rhs);
		}
	)
	;
	
assignLhs [FilterModule fm] returns [CanonProperty prop]
	: p1=IDENTIFIER p2=IDENTIFIER?
	{
		
	}
	;
	
assignRhs [FilterModule fm] returns [CpsVariable val]
	: fqn | singleFmParam | LITERAL
	;

// $<Condition Expression

matchingExpression [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=OR lhs=matchingExpression[fm] rhs=matchingExpression[fm] 
			{
				ex = new OrMEOper();
				setLocInfo(ex, $frst);
				repository.add(ex);
				((BinaryMEOperator) ex).setLHS(lhs);
				((BinaryMEOperator) ex).setRHS(rhs);
			}
		)
	| andex=andExpr[fm] {ex = andex;}
	;

andExpr [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=AND lhs=matchingExpression[fm] rhs=matchingExpression[fm] 
			{
				ex = new AndMEOper();
				setLocInfo(ex, $frst);
				repository.add(ex);
				((BinaryMEOperator) ex).setLHS(lhs);
				((BinaryMEOperator) ex).setRHS(rhs);
			}
		)
	| unex=unaryExpr[fm] {ex = unex;}
	;
	
unaryExpr [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=NOT oper=matchingExpression[fm]
			{
			ex = new NotMEOper();
			setLocInfo(ex, $frst);
			((UnaryMEOperator) ex).setOperand(oper);
			}
		)
	| oex=operandExpr[fm] {ex = oex;}
	;
	
operandExpr [FilterModule fm] returns [MatchingExpression ex]
	: name=IDENTIFIER
		{
			try {
				String oper = $name.text;
				if ("true".equalsIgnoreCase(oper))
				{
					ex = new MELiteralImpl(true);
				}
				else if ("false".equalsIgnoreCase(oper))
				{
					ex = new MELiteralImpl(false);
				}
				else {
					Condition cond = fm.getCondition(oper);
					if (cond == null)
					{
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a condition with the name \"\%s\"", 
							fm.getFullyQualifiedName(), oper), input, $name);
					}
					else {
						ex = new MEConditionImpl();
						((MEConditionImpl) ex).setCondition(cond);
					}
				}
				setLocInfo(ex, $name);
				repository.add(ex);
			}
			catch (RecognitionException re) {
				reportError(re);
				recover(input,re);
			} 
		}
	| cmp=compareStatement[fm] { ex = cmp; }
	;
	
compareStatement [FilterModule fm] returns [MECompareStatement cmp]
	: ^(strt=CMPSTMT oper=cmpOperator ^(OPERAND lhs=assignLhs[fm]) ^(OPERAND rhs=cmpRhs[fm])
	{
		cmp = oper;
		cmp.setLHS(lhs);
		cmp.setRHS(rhs);
	}
	)
	;
	
cmpOperator returns [MECompareStatement cmp]
	: ^(OPERATOR {	Tree ftok = (Tree) input.LT(1); }
	( '=='	{ cmp = new InstanceMatching(); } 
	| '$=' 	{ cmp = new SignatureMatching(); }
	| '~='  { cmp = new CompatibilityMatching(); }
	| '@='	{ cmp = new AnnotationMatching(); }
	)
	{
		if (cmp != null)
		{
			setLocInfo(cmp, ftok);
			repository.add(cmp);
		}
		else {
			throw new CpsSemanticException(String.format("Unknown compare operator \"\%s\"", 
				ftok.getText()), input, ftok);
		}
	}
	)
	; 

cmpRhs [FilterModule fm] returns [Collection<CpsVariable> res = new ArrayList<CpsVariable>();]
@init{
	Tree errTok = (Tree) input.LT(1); 
}
	: qn=fqnAsList
		{
			if (qn.size() == 0)
			{
				// fixme: error
			}
			
			// fixme: check for message.*, filter.*, target, selector
			
			if (qn.size() == 1)
			FilterModuleVariable fmVar = fm.getVariable(qn);
			if (fmVar == null)
			{
				// qualified name, resolve to a type reference
				res.add(new CpsTypeProgramElementImpl(references.getTypeReference(qn)));
			}
			else if (fmVar instanceof Condition)
			{
				res.add((Condition) fmVar);
			}
			else if (fmVar instanceof Internal)
			{
				res.add((Internal) fmVar);
			}
			else if (fmVar instanceof External)
			{
				res.add((External) fmVar);
			}
			else {
				// fixme: error
			}
		}
	| ^(FM_PARAM_SINGLE IDENTIFIER)
	| ^(FM_PARAM_LIST IDENTIFIER)
	| l=LITERAL 
		{
			String lvalue = $l.text;
			if (lvalue.length() >= 2)
			{
				lvalue = unescapeLiteral(lvalue.substring(1, lvalue.length()-2));
			}
			else {
				lvalue = "";
			}
			res.add(new CpsLiteralImpl(lvalue)); 
		}
	;

// $> Condition Expression

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
		
