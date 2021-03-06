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
import Composestar.Core.CpsRepository2.Meta.*;
import Composestar.Core.CpsRepository2.References.*;
import Composestar.Core.CpsRepository2.SISpec.*;
import Composestar.Core.CpsRepository2.SISpec.Constraints.*;
import Composestar.Core.CpsRepository2.TypeSystem.*;

import Composestar.Core.CpsRepository2Impl.*;
import Composestar.Core.CpsRepository2Impl.FilterElements.*;
import Composestar.Core.CpsRepository2Impl.FilterModules.*;
import Composestar.Core.CpsRepository2Impl.Filters.*;
import Composestar.Core.CpsRepository2Impl.FMParams.*;
import Composestar.Core.CpsRepository2Impl.References.*;
import Composestar.Core.CpsRepository2Impl.SISpec.*;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.*;
import Composestar.Core.CpsRepository2Impl.TypeSystem.*;

import Composestar.Core.EMBEX.EmbeddedSource;

import Composestar.Core.Exception.*;

import java.util.Collection;
import java.util.ArrayList;
}

@members {
	protected ArrayList<String> currentFilterNames = new ArrayList<String>();
	
	protected CanonProperty createProperty(String prefixStr, String varname, FilterType filterType)
	{
		PropertyPrefix prefix = null;
		if (prefixStr == null)
		{
			if (PropertyNames.INNER.equals(varname)) 
			{
				prefix = PropertyPrefix.NONE;
			}
			else {
				prefix = PropertyPrefix.MESSAGE;
			}
		}
		else {
			if ("legacy".equals(prefixStr)) 
			{
				if (filterType == null)
				{
					throw new IllegalArgumentException("Cannot process 'legacy' prefix without a filter type");
				}
				if (FilterTypeNames.DISPATCH.equalsIgnoreCase(filterType.getFilterName())
					|| FilterTypeNames.SEND.equalsIgnoreCase(filterType.getFilterName())
					|| FilterTypeNames.SUBSTITUTION.equalsIgnoreCase(filterType.getFilterName()))
				{
					prefix = PropertyPrefix.MESSAGE;
				}
				else if (FilterTypeNames.BEFORE.equalsIgnoreCase(filterType.getFilterName())
					|| FilterTypeNames.AFTER.equalsIgnoreCase(filterType.getFilterName())
					|| FilterTypeNames.META.equalsIgnoreCase(filterType.getFilterName())
					|| FilterTypeNames.ERROR.equalsIgnoreCase(filterType.getFilterName()) // actually doesn't use it
				)
				{
					prefix = PropertyPrefix.FILTER;
				}
				else {
					prefix = PropertyPrefix.FILTER;
					logger.warn(String.format("Unable to convert legacy substitution structure for unknown filter \%s. Defaulting to filter.\%s . Switch to the canonical notation.",
						filterType.getFilterName(), varname));
				}
			}
			else {
				prefix = PropertyPrefix.fromString(prefixStr);
			}
		}
		return new CanonPropertyImpl(prefix, varname);		
	}
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
			QualifiedRepositoryEntity test = repository.get(c.getFullyQualifiedName());
			if (test!=null)
			{
				throw new CpsSemanticException(String.format("Duplicate concern \"\%s\". Previously declared in \"\%s\".", 
						c.getFullyQualifiedName(), test.getSourceInformation()), input, $name);
			}
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
				//recover(input,re);
			}
			currentFilterNames.clear();
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
				//recover(input,re);
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
					references.addReferenceUser(tr, internal, true);
				}
				else if (prm != null)
				{
					FMParameter fmp = fm.getParameter($prm.text);
					if (fmp == null) {
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
							fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
					}
					tr = new ParameterizedTypeReference(fmp);
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
				//recover(input,re);
			}
		}
		)+))
	;

joinPointContext returns [JoinPointContextArgument jpca]
	: ^(JPCA 'full' {jpca = JoinPointContextArgument.FULL;})
	| ^(JPCA 'partial' {jpca = JoinPointContextArgument.PARTIAL;})
	| ^(JPCA NONE {jpca = JoinPointContextArgument.NONE;})
	;

methodReference[FilterModule fm] returns [MethodReference mref]
// throws CpsSemanticException
@init {
	Tree errTok = (Tree) input.LT(1);
	JoinPointContextArgument jpca = JoinPointContextArgument.NONE; 
}
	: lst=fqnAsList (jpca1=joinPointContext {jpca = jpca1;})?
		{
			try {
				if (lst.size() < 2)
				{
					throw new CpsSemanticException(String.format("Invalid method reference: \%s", lst.toString()), 
						input, errTok);
				}
				if (lst.size() == 2)
				{
					if (PropertyNames.INNER.equals(lst.get(0)))
					{
						CpsObject innerObj = new CpsObjectImpl(new InnerTypeReference(), CpsObjectImpl.CpsObjectType.INNER);
						mref = references.getInstanceMethodReference(lst.get(1), innerObj, jpca);
						repository.add(innerObj);
					}
					else {
						FilterModuleVariable fmvar = fm.getVariable(lst.get(0));
						if (fmvar instanceof CpsObject)
						{
							CpsObject ctx = (CpsObject) fmvar;
							mref = references.getInstanceMethodReference(lst.get(1), ctx, jpca);
						}
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
				//recover(input,re);
			}
		}
	| prm=singleFmParam (jpca2=joinPointContext {jpca = jpca2;})?
		{
			try {
				FMParameter fmp = fm.getParameter($prm.text);
				if (fmp == null) {
					throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
						fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
				}
				mref = new ParameterizedMethodReference(fmp, jpca);
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
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
					references.addReferenceUser(tr, external, true);
				}
				else if (prm != null)
				{
					FMParameter fmp = fm.getParameter($prm.text);
					if (fmp == null) {
						throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
							fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
					}
					tr = new ParameterizedTypeReference(fmp);
				}
				else {
					throw new CpsSemanticException(String.format("External \"\%s\" is not a fully qualified name or parameter", 
						$name.text), input, name);
				}
				external.setTypeReference(tr);			
				if (init != null)
				{
					external.setMethodReference(init);
					if (!(init instanceof Parameterized))
					{
						references.addReferenceUser(init, external, false);
					}
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
				//recover(input,re);
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
				if (mref != null) 
				{
					cond.setMethodReference(mref);
					if (!(mref instanceof Parameterized))
					{
						references.addReferenceUser(mref, cond, true);
					}
				}
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
				//recover(input,re);
			}
		}
		)
	;	
	
filterExpression [FilterModule fm] returns [FilterExpression expr]
// throws CpsSemanticException
	: ^(op=filterOperator lhs=filterExpression[fm] rhs=filterExpression[fm]
		{
			if (lhs != null) op.setLHS(lhs);
			if (rhs != null) op.setRHS(rhs);
			expr = op;
		}
	)
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
	: seq=SEMICOLON 
		{
			op = new SequentialFilterOper();
			setLocInfo(op, $seq);
			repository.add(op);
		}
	;

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filter [FilterModule fm] returns [Filter filter]
// throws CpsSemanticException
	: ^(frst=FILTER name=IDENTIFIER ft=filterType 
		{
			try {
				if (!fm.isUniqueMemberName($name.text) || currentFilterNames.contains($name.text)) {
					throw new CpsSemanticException(String.format("Filter name \"\%s\" is not unqiue in filter module: \%s", 
						$name.text, fm.getFullyQualifiedName()), input, name);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
			currentFilterNames.add($name.text);
			filter = new FilterImpl($name.text);
			setLocInfo(filter, $frst);
			filter.setOwner(fm); // done so that the correct FQN is produced in repository.add(..)
			if (ft != null) filter.setType(ft);
			repository.add(filter);
		}
		(^(PARAMS
			(asgn=canonAssign[fm,null]
				{
					try {
						try {
							if (asgn != null && asgn.getProperty() != null) filter.addArgument(asgn);
						}
						catch (IllegalArgumentException iae)
						{
							throw new CpsSemanticException(iae.getMessage(), input, name);
						}
					}
					catch (RecognitionException re) {
						reportError(re);
						//recover(input,re);
					}
				}
			)*
		))?
		expr=filterElementExpression[fm,ft]
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
				if ((ft == null) && (filterTypeFactory != null))
				{
					// lazy custom filter type creation
					ft = filterTypeFactory.createCustomFilterType(ftName);
				}
				if (ft == null)
				{
					throw new CpsSemanticException(String.format("Undefined filter type: \%s", ftName), input, errTok);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
	;
	
filterElementExpression [FilterModule fm, FilterType ft] returns [FilterElementExpression expr]
// throws CpsSemanticException
	: ^(op=filterElementOperator lhs=filterElementExpression[fm,ft] rhs=filterElementExpression[fm,ft]
		{
			if (lhs != null) op.setLHS(lhs);
			if (rhs != null) op.setRHS(rhs);
			expr = op;
		}
	)
	| elm=filterElement[fm,ft] {expr = elm;}
	;
	
filterElementOperator returns [BinaryFilterElementOperator op]
	: cor=COR
		{
			op = new CORFilterElmOper();
			setLocInfo(op, $cor);
			repository.add(op);
		}
	;	

/**
 * Filter module passed to resolve FM elements, parent of various elements are not set yet
 */
filterElement [FilterModule fm, FilterType ft] returns [FilterElement fe]
// throws CpsSemanticException
	: ^(strt=FILTER_ELEMENT 
		{
			fe = new FilterElementImpl();
			setLocInfo(fe, $strt);
			repository.add(fe);
		}
		expr=matchingExpression[fm] 
		{
			if (expr != null) fe.setMatchingExpression(expr);
		}
		(asgn=canonAssign[fm,ft]
			{
				if (asgn != null && asgn.getProperty() != null) fe.addAssignment(asgn);
			}
		)*
	)
	;
	
canonAssign [FilterModule fm, FilterType ft] returns [CanonAssignment asgn]
	: ^(strt=EQUALS ^(OPERAND lhs=assignLhs[fm,ft]) ^(OPERAND rhs=assignRhs[fm,ft])
		{
			asgn = new CanonAssignmentImpl();
			setLocInfo(asgn, strt);
			repository.add(asgn);
			if (lhs != null) asgn.setProperty(lhs);
			if (rhs != null) asgn.setValue(rhs);
		}
	)
	;
	
assignLhs [FilterModule fm, FilterType ft] returns [CanonProperty prop]
	: p1=IDENTIFIER p2=IDENTIFIER?
	{
		try {
			try {
				if ($p2 == null)
				{
					prop = createProperty(null, $p1.text, ft);
				}
				else {
					prop = createProperty($p1.text, $p2.text, ft);
				}
			}
			catch (IllegalArgumentException e)
			{
				throw new CpsSemanticException(e.toString(), input, $p1);
			}
			if (prop != null)
			{
				setLocInfo(prop, $p1);
				repository.add(prop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			//recover(input,re);
		} 
	}
	;
	
assignRhs [FilterModule fm, FilterType ft] returns [CpsVariable val]
	: qn=cpsVariableFqn[fm] { val = qn;	}
	| prm=singleFmParam 
		{
			try {
				FMParameter fmp = fm.getParameter($prm.text);
				if (fmp == null) {
					throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
						fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
				}
				val = new ParameterizedCpsVariable(fmp);
				setLocInfo(val, $prm.start);
				repository.add(val);
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			} 
		} 
	| l=LITERAL
		{
			String lvalue = $l.text;
			if (lvalue.length() >= 2)
			{
				lvalue = unescapeLiteral(lvalue.substring(1, lvalue.length()-1));
			}
			else {
				lvalue = "";
			}
			val = new CpsLiteralImpl(lvalue);
			setLocInfo(val, $l);
			repository.add(val);
		}
	;

// $<Condition Expression

matchingExpression [FilterModule fm] returns [MatchingExpression ex]
	: ^(EXPRESSION orexp=orExp[fm] {ex = orexp;})
	;

orExp [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=OR lhs=orExp[fm] rhs=orExp[fm] 
			{
				ex = new OrMEOper();
				setLocInfo(ex, $frst);
				repository.add(ex);
				if (lhs != null) ((BinaryMEOperator) ex).setLHS(lhs);
				if (rhs != null) ((BinaryMEOperator) ex).setRHS(rhs);
			}
		)
	| andex=andExpr[fm] {ex = andex;}
	;

andExpr [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=AND lhs=orExp[fm] rhs=orExp[fm] 
			{
				ex = new AndMEOper();
				setLocInfo(ex, $frst);
				repository.add(ex);
				if (lhs != null) ((BinaryMEOperator) ex).setLHS(lhs);
				if (rhs != null) ((BinaryMEOperator) ex).setRHS(rhs);
			}
		)
	| unex=unaryExpr[fm] {ex = unex;}
	;
	
unaryExpr [FilterModule fm] returns [MatchingExpression ex]
	: ^(frst=NOT oper=orExp[fm]
			{
				ex = new NotMEOper();
				setLocInfo(ex, $frst);
				if (oper != null) ((UnaryMEOperator) ex).setOperand(oper);
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
				//recover(input,re);
			} 
		}
	| cmp=compareStatement[fm] { ex = cmp; }
	;
	
compareStatement [FilterModule fm] returns [MECompareStatement cmp]
	: ^(strt=CMPSTMT oper=cmpOperator ^(OPERAND lhs=assignLhs[fm,null]) ^(OPERAND rhs=cmpRhs[fm])
	{
		cmp = oper;
		if (lhs != null) cmp.setLHS(lhs);
		if (rhs != null) cmp.setRHS(rhs);
	}
	)
	;

cmpOperator returns [MECompareStatement cmp]
	: ^(OPERATOR 	{ Tree ftok = (Tree) input.LT(1); }
	( CMP_INSTANCE	{ cmp = new InstanceMatching(); } 
	| CMP_SIGN 		{ cmp = new SignatureMatching(); }
	| CMP_COMPAT  	{ cmp = new CompatibilityMatching(); }
	| CMP_ANNOT		{ cmp = new AnnotationMatching(); }
	)
	{
		try {
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
		catch (RecognitionException re) {
			reportError(re);
			//recover(input,re);
		}
	}
	)
	; 
	
cpsVariableFqn [FilterModule fm] returns [CpsVariable entity]
@init{
	Tree errTok = (Tree) input.LT(1); 
}
	: qn=fqnAsList
		{
			try {
				if (qn.size() == 0)
				{
					throw new CpsSemanticException(String.format("Invalid data: \%s", errTok.getText()), input, errTok);
				}
				
				if (qn.size() == 1)
				{
					if (PropertyNames.INNER.equals(qn.get(0)))
					{
						entity = createProperty(null, qn.get(0), null);
					}
					else if (PropertyNames.TARGET.equals(qn.get(0)))
					{
						entity = createProperty(null, qn.get(0), null);
					}
					else if (PropertyNames.SELECTOR.equals(qn.get(0)))
					{
						entity = createProperty(null, qn.get(0), null);
					}
					else {
						FilterModuleVariable fmVar = fm.getVariable(qn.get(0));
						if (fmVar instanceof Condition)
						{
							entity = (Condition) fmVar;
						}
						else if (fmVar instanceof Internal)
						{
							entity = (Internal) fmVar;
						}
						else if (fmVar instanceof External)
						{
							entity = (External) fmVar;
						}
						else {
							// fixme: error
						}
					}
				}
				else if (qn.size() == 2)
				{
					if (PropertyPrefix.fromString(qn.get(0)) == PropertyPrefix.MESSAGE)
					{
						entity = createProperty(qn.get(0), qn.get(1), null);
					}
					else if (PropertyPrefix.fromString(qn.get(0)) == PropertyPrefix.FILTER)
					{
						entity = createProperty(qn.get(0), qn.get(1), null);
					}
				}
				
				if (entity == null)
				{
					// qualified name, resolve to a type reference
					StringBuilder sb = new StringBuilder();
					for (String s : qn)
					{
						if (sb.length() > 0)
						{
							sb.append('.');
						}
						sb.append(s);
					}
					TypeReference tr = references.getTypeReference(sb.toString());
					entity = new CpsTypeProgramElementImpl(tr);
					references.addReferenceUser(tr, entity, true);
				}
				if (entity != null && !(entity instanceof FilterModuleVariable))
				{
					setLocInfo(entity, errTok);
					repository.add(entity);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
	;

cmpRhs [FilterModule fm] returns [CpsVariableCollection res]
@init{
	Tree errTok = (Tree) input.LT(1); 
}
	: prm=fmParamList
		{
			try {
				FMParameter fmp = fm.getParameter($prm.text);
				if (fmp == null) {
				throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
					fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
				}
				res = new ParameterizedCpsVariableCollection(fmp);
				setLocInfo(fmp, $prm.start);
				repository.add(fmp);
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
	| ^(lst=LIST { 
		res = new CpsVariableCollectionImpl();
		setLocInfo(res, $lst);
		repository.add(res); 
	} meCmpRhsSingle[fm, res]+)
	| { 
		res = new CpsVariableCollectionImpl();
		setLocInfo(res, errTok);
		repository.add(res); 
	} meCmpRhsSingle[fm, res]
	;
	
meCmpRhsSingle [FilterModule fm, CpsVariableCollection res]
	: fqnv=cpsVariableFqn[fm] { if (fqnv != null) res.add(fqnv); } 
	| prm=singleFmParam
		{
			try {
				FMParameter fmp = fm.getParameter($prm.text);
				if (fmp == null) {
				throw new CpsSemanticException(String.format("\"\%s\" does not contain a parameter with the name \"\%s\"", 
					fm.getFullyQualifiedName(), $prm.text), input, $prm.start);
				}
				res.add(new ParameterizedCpsVariable(fmp));
				setLocInfo(fmp, $prm.start);
				repository.add(fmp);
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		} 
	| l=LITERAL 
		{
			String lvalue = $l.text;
			if (lvalue.length() >= 2)
			{
				lvalue = unescapeLiteral(lvalue.substring(1, lvalue.length()-1));
			}
			else {
				lvalue = "";
			}
			CpsLiteral lit = new CpsLiteralImpl(lvalue);
			res.add(lit); 
			setLocInfo(lit, $l);
			repository.add(lit);
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
			SISpecification si = new SISpecificationImpl();
			setLocInfo(si, $strt);
			c.setSISpecification(si);
			repository.add(si);
			
			// default selector
			Selector selfSel = new LegacySelector("self", c.getFullyQualifiedName(), false);
			repository.add(selfSel);
			si.addSelector(selfSel);
		}
		conditionalSi[si]* selectorSi[si]* filtermoduleSi[si]* annotationSi[si]* constraint[si]*
		)
	;

conditionalSi [SISpecification si]
// throws CpsSemanticException
@init {
	JoinPointContextArgument jpca = JoinPointContextArgument.PARTIAL;
}
	: ^(strt=CONDITION name=IDENTIFIER expr=fqnAsList (jpca1=joinPointContext {jpca = jpca1;})?
		{
			try {
				SICondition cond = new SIConditionImpl($name.text);
				setLocInfo(cond, $strt);
				
				if (expr.size() < 2)
				{
					StringBuilder fqnAsString = new StringBuilder();
					for (String s : expr)
					{
						if (fqnAsString.length() > 0)
						{
							fqnAsString.append('.');
						}
						fqnAsString.append(s);
					} 
					throw new CpsSemanticException(String.format("Invalid method reference: \"\%s\"",
						fqnAsString.toString()), input, $strt);
				}
				
				StringBuilder sb = new StringBuilder();
				for (String s : expr.subList(0, expr.size()-1))
				{
					if (sb.length() > 0)
					{
						sb.append('.');
					}
					sb.append(s);
				}
				MethodReference mref = references.getMethodReference(expr.get(expr.size()-1), sb.toString(), jpca);
				cond.setMethodReference(mref);
				if (!si.addCondition(cond))
				{
					throw new CpsSemanticException(String.format("Condition name \"\%s\" is not unqiue within superimposition for: \%s",
						cond.getName(), si.getFullyQualifiedName()), input, $strt);
				}
				references.addReferenceUser(mref, cond, true);
				repository.add(cond);
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
		)
	;
	
selectorSi [SISpecification si]
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
				//recover(input,re);
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
				//recover(input,re);
			}
		}
		)
	;
	
filtermoduleSi [SISpecification si]
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
				//recover(input,re);
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
					//recover(input,re);
				}
			}
		))? 
		(bnd=fmBinding[si]
		{
			if (bnd != null)
			{
				bnd.setSelector(sel);
				if (cond != null) bnd.setCondition(cond);
				si.addFilterModuleBinding(bnd);
				repository.add(bnd);
			}
		}
		)+)
	;
	
fmBinding [SISpecification si] returns [FilterModuleBinding fmb = new FilterModuleBindingImpl()]
// throws CpsSemanticException
@init {
	if (si.getOwner() == null || !(si.getOwner() instanceof CpsConcern)) 
	{
		throw new CpsSemanticException("Superimposition is not owned by a CpsConcern", input);
	}
}
	: ^(strt=BINDING fmr=concernFmRef[si] 
		{
			setLocInfo(fmb, $strt);
			if (fmr != null) 
			{
				fmb.setFilterModuleReference(fmr);
				if (!fmr.isSelfReference())
				{
					references.addReferenceUser(fmr, fmb, true);
				}
			}
		}
		(^(PARAMS
			{
				List<FMParameterValue> args = new ArrayList<FMParameterValue>();
			} 
				(prm=param[si]
				{
					if (prm != null) args.add(prm);
				}
				)+
			{
				fmb.setParameterValues(args);
			}
			)
		)?)
	;	
	
param [SISpecification si] returns [FMParameterValue fmp]
	: ^(strt=LIST 
		{
			CompositeFMParamValue cfmpv = new CompositeFMParamValue();
			setLocInfo(cfmpv, $strt);
			repository.add(cfmpv);
			fmp = cfmpv;
		}
		(val=paramValue[si]
			{
				if (val != null) cfmpv.addValue(val);
			}
		)+
	)
	| sv=paramValue[si] {fmp = sv;}
	;
	
paramValue [SISpecification si] returns [FMParameterValue fmp]
	: ^(strt=PARAM 
		(sp=fqn 	
			{
				Selector sel = si.getSelector(sp);
				if (sel != null)
				{
					fmp = new SelectorFMParamValue(sel);
				}
				else {
					TypeReference tr = references.getTypeReference(sp);
					fmp = new SimpleFMParamValue(new CpsTypeProgramElementImpl(tr));
					references.addReferenceUser(tr, fmp, false);
				}
			}
		|lt=LITERAL	
			{
				String lvalue = $lt.text;
				if (lvalue.length() >= 2)
				{
					lvalue = unescapeLiteral(lvalue.substring(1, lvalue.length()-1));
				}
				else {
					lvalue = "";
				}
				fmp = new SimpleFMParamValue(new CpsLiteralImpl(lvalue));
			}
		)
		{
			setLocInfo(fmp, $strt);
			repository.add(fmp);
		}
	)
	;

annotationSi [SISpecification si]
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
				//recover(input,re);
			}
			si.addAnnotationBinding(ab);
			repository.add(ab);
		}
		(at=fqn
		{
			TypeReference tr = references.getTypeReference(at);
			ab.addAnnotation(references.getTypeReference(at));
			references.addReferenceUser(tr, ab, true);	
		}
		)+)
	;
	

concernFmRef [SISpecification si] returns [FilterModuleReference res]
// throws CpsSemanticException	
	: {Tree errTok = (Tree) input.LT(1);}
		fr=fqn
		{
			try {
				if (fr.indexOf(".") == -1)
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
					res = references.getFilterModuleReference(fr);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
	;				

constraint [SISpecification si]
// throws CpsSemanticException
	: ^(CONSTRAINT opr=IDENTIFIER 
		{
			List<ConstraintValue> args = new ArrayList<ConstraintValue>();
			
			if (si.getOwner() == null || !(si.getOwner() instanceof CpsConcern)) 
			{
				throw new CpsSemanticException("Superimposition is not owned by a CpsConcern", input);
			}
			CpsConcern concern = (CpsConcern) si.getOwner();
			Tree tok = (Tree) input.LT(1);
		}
		(arg=fqn
			{
				FilterModule fm = concern.getFilterModule(arg);
				SICondition sic = si.getCondition(arg);
				if (fm != null)
				{
					FilterModuleConstraintValue fmcv = new FilterModuleConstraintValueImpl(fm);
					setLocInfo(fmcv, tok);
					repository.add(fmcv);
					args.add(fmcv);
				}
				else if (sic != null)
				{
					ConditionConstraintValue ccv = new ConditionConstraintValueImpl(sic);
					setLocInfo(sic, tok);
					repository.add(ccv);
					args.add(ccv);
				}
				else if (arg.indexOf('.') > 0)
				{
					FilterModuleReference fmr = references.getFilterModuleReference(arg);
					FilterModuleConstraintValue fmcv = new FilterModuleConstraintValueImpl(fmr);
					references.addReferenceUser(fmr, fmcv, false);
					setLocInfo(fmcv, tok);
					repository.add(fmcv);
					args.add(fmcv);
				}
				else {
					throw new CpsSemanticException(String.format("\"\%s\" does not contain a filter module with the name \"\%s\"", 
						concern.getFullyQualifiedName(), arg), input, tok);
				}				
			}
		)+
		{
			try {
				try {
					Constraint fmc = constraintFactory.createConstraint($opr.text, args);
					setLocInfo(fmc, opr);
					si.addFilterModuleConstraint(fmc);
					repository.add(fmc);
				}
				catch (IllegalArgumentException e) {
					throw new CpsSemanticException(e.getMessage(), input, opr);
				}
				catch (InstantiationException e) {
					throw new CpsSemanticException(e.getMessage(), input, opr);
				}
				catch (NullPointerException e) {
					throw new CpsSemanticException(e.getMessage(), input, opr);
				}
			}
			catch (RecognitionException re) {
				reportError(re);
				//recover(input,re);
			}
		}
		)
	;				

// $>
	
// $<Implementation

implementation [CpsConcern c]
	: ^(astr=IMPLEMENTATION asm=fqn)
	| ^(strt=IMPLEMENTATION lang=IDENTIFIER cls=fqn fn=LITERAL code=CODE_BLOCK
		{
			if (embeddedSourceManager != null)
			{
				String lvalue = $fn.text;
				EmbeddedSource src = new EmbeddedSource($lang.text, unescapeLiteral(lvalue.substring(1, lvalue.length()-1)), $code.text);
				SourceInformation srcInfo = new SourceInformation(fileInformation);
				src.setSourceInformation(srcInfo);
				if (strt != null)
				{
					srcInfo.setLine(strt.getLine());
					srcInfo.setLinePos(strt.getCharPositionInLine());
				}
				embeddedSourceManager.addSource(src);
			}
		}
		)
	;
		
// $>
		
