package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
 **/

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Utils.Debug;
import Composestar.Core.RepositoryImplementation.*;

public class RepositoryPtrList extends TreeBuilder
{
	protected int filterNumber = 0;

	public LinkedList list = null;

	public RepositoryPtrList(LinkedList lst)
	{
		list = lst;
	}

	private FilterComponent parseConditionExpression(ConditionExpression ce)
	{
		if (ce instanceof True)
		{
			return new MatchTrue();
		}
		if (ce instanceof False)
		{
			return new MatchFalse();
		}
		if (ce instanceof ConditionVariable)
		{
			ConditionVariable cl = (ConditionVariable) ce;

			String conditionString = cl.getCondition().getName();

			if (conditionString == null || conditionString.length() == 0)
			{
				// Debug.out(Debug.MODE_WARNING,"FIRE", "Condition string is
				// empty, assuming true");
				// Bailing out with true condition.
				return new MatchTrue();
			}

			if (conditionString.toLowerCase().equals("true")) return new MatchTrue();
			if (conditionString.toLowerCase().equals("false")) return new MatchFalse();

			return new Match((SymbolTable.getInstance()).addSymbol(conditionString, 0), filterNumber);
		}
		else if (ce instanceof And)
		{
			And and = (And) ce;

			Tand tand = new Tand();
			tand.addChild1(parseConditionExpression(and.getLeft()));
			tand.addChild2(parseConditionExpression(and.getRight()));
			return tand;
		}
		else if (ce instanceof Or)
		{
			Or or = (Or) ce;

			Tor tor = new Tor();
			tor.addChild1(parseConditionExpression(or.getLeft()));
			tor.addChild2(parseConditionExpression(or.getRight()));
			return tor;
		}
		else if (ce instanceof Not)
		{
			Not not = (Not) ce;

			Tnot tnot = new Tnot();
			tnot.addChild1(parseConditionExpression(not.getOperand()));
			return tnot;

		}
		else
		{
			Debug.out(Debug.MODE_WARNING, "FIRE",
					"ConditionExpression is not: ConditionLiteral, And, Or, Not. What is it?");
		}

		Debug.out(Debug.MODE_WARNING, "FIRE", "Returning true");
		return new MatchTrue();
	}

	private FilterComponent parseMatching(MatchingPattern mp)
	{
		// TODO WM: needs fixing

		Vector matchParts = mp.getMatchingParts();
		Vector substParts = mp.getSubstitutionParts();

		String matchTarget;
		String matchSelector;
		String substTarget = null;
		String substSelector = null;

		matchTarget = ((MatchingPart) matchParts.firstElement()).getTarget().getName();
		matchSelector = ((MatchingPart) matchParts.firstElement()).getSelector().getName();

		if (!substParts.isEmpty())
		{
			substTarget = ((SubstitutionPart) substParts.firstElement()).getTarget().getName();
			substSelector = ((SubstitutionPart) substParts.firstElement()).getSelector().getName();
		}

		// Signature or name matching?
		SymbolTable st = SymbolTable.getInstance();
		if (!matchParts.isEmpty()
				&& ((MatchingPart) matchParts.firstElement()).getMatchType() instanceof SignatureMatchingType)
		{
			MatchSignature match = new MatchSignature((st.addSymbol(matchTarget, 1)), filterNumber);

			// TODO: Check this
			// FIX BY TOM, INSERT Substitutes after a successful signature match

			Tand joinSubst = new Tand();
			if (!substParts.isEmpty())
			{
				joinSubst.addChild1(new Substitute(st.addSymbol(substTarget, 1), filterNumber));
				joinSubst.addChild2(new Substitute(st.addSymbol(substSelector, 2), filterNumber));

			}
			else
			{
				joinSubst.addChild1(new Substitute(st.addSymbol(matchTarget, 1), filterNumber));
				joinSubst.addChild2(new Substitute(st.addSymbol(matchSelector, 2), filterNumber));
			}
			Tand join = new Tand();
			join.addChild1(match);
			join.addChild2(joinSubst);
			return join;

		}
		else
		// Name matching
		{
			// Creating the FilterComponent.
			Tand joinMatch = new Tand();
			joinMatch.addChild1(new Match(st.addSymbol(matchTarget, 1), filterNumber));
			joinMatch.addChild2(new Match(st.addSymbol(matchSelector, 2), filterNumber));

			if (!substParts.isEmpty())
			{
				Tand joinSubst = new Tand();
				joinSubst.addChild1(new Substitute(st.addSymbol(substTarget, 1), filterNumber));
				joinSubst.addChild2(new Substitute(st.addSymbol(substSelector, 2), filterNumber));

				Tand combine = new Tand();
				combine.addChild1(joinMatch);
				combine.addChild2(joinSubst);

				return combine;
			}
			return joinMatch;
		}
	}

	private FilterComponent parseFilterElement(FilterElement fe)
	{
		FilterComponent fc1 = parseConditionExpression(fe.getConditionPart());

		FilterComponent fc2 = parseMatching(fe.getMatchingPattern());

		if (fe.getEnableOperatorType() instanceof DisableOperator)
		{
			TandNot tandnot = new TandNot();
			tandnot.addChild1(fc1);
			tandnot.addChild2(fc2);
			return tandnot;
		}
		else if (fe.getEnableOperatorType() instanceof EnableOperator)
		{
			Tand tand = new Tand();
			tand.addChild1(fc1);
			tand.addChild2(fc2);
			return tand;
		}

		Debug.out(Debug.MODE_CRUCIAL, "FIRE", "Oops.. Unknown Enable/Disable operator. Bailing out.");

		return null;
	}

	private FilterComponent parseMultipleFilterElements(Iterator iterator)
	{
		// TODO: There is a bug here but I don't have any idea what to fill in
		// :( Pascal@19-11-2004
		// if(iterator.hasNext())
		// {
		FilterComponent fc1 = parseFilterElement((FilterElement) iterator.next());
		if (iterator.hasNext())
		{
			FilterComponent fc2 = parseMultipleFilterElements(iterator);
			Tor tor = new Tor();

			tor.addChild1(fc1);
			tor.addChild2(fc2);
			return tor;
		}
		else
		{
			return fc1;
		}
		/*
		 * } else { return null; }
		 */
	}

	// TODO: SUPRE and FIRE both have the class Filter :-(.
	private FilterComponent parseFilter(
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter repositoryFilter)
	{
		// Get the filter elements
		Iterator iterator = repositoryFilter.getFilterElementIterator();
		FilterComponent fc = parseMultipleFilterElements(iterator);

		Tand tand = new Tand();

		if (fc != null) tand.addChild1(fc);

		// TODO deprecated
		Filter f = FilterFactory.getFilter(repositoryFilter.getFilterType().getType());
		f.setParameters(repositoryFilter.getParameters());
		tand.addChild2(new Action(f, filterNumber));

		return tand;
	}

	private FilterComponent parseMultipleFilters(Iterator iterator)
	{
		if (!iterator.hasNext())
		{
			// System.out.println("FIRE got an empty list of Filters :-(");
			// return new Action (FilterFactory.getFilter("Dispatch"),
			// filterNumber+1);
			return null;
		}

		filterNumber++;

		Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) iterator
				.next();

		if (f.getFilterType().getType().equals(FilterType.META))
		{
			if (iterator.hasNext()) return parseMultipleFilters(iterator);
			else return null;
		}

		FilterComponent fc1 = parseFilter(f);
		if (iterator.hasNext())
		{
			FilterComponent fc2 = parseMultipleFilters(iterator);

			// null was returned if only Meta's were found
			if (fc2 == null) return fc1;

			Tand tand = new Tand();

			tand.addChild1(fc1);
			tand.addChild2(fc2);
			return tand;
		}
		return fc1;
	}

	private FilterComponent parseFilterModule(FilterModule fm)
	{
		Iterator i = fm.getInputFilterIterator();
		FilterComponent fc = parseMultipleFilters(i);

		return fc;
	}

	private FilterComponent parseMultipleFilterModules(Iterator iterator, FilterReasoningEngine fireInfo)
	{
		if (!iterator.hasNext())
		{
			Debug.out(Debug.MODE_CRUCIAL, "FIRE", "FIRE got an empty list of FilterModules :-(");
			return new Action(FilterFactory.getFilter("EOF"), filterNumber + 1);
		}

		String str = (String) iterator.next();
		FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(str);
		FilterComponent fc1 = parseFilterModule(fm);

		if (fc1 == null)
		{
			// fc1 has just metafilters
			// skip this, go next
			return parseMultipleFilterModules(iterator, fireInfo);
		}

		// Add internals, externals etc.
		addFIREInfo(fireInfo, fm);

		if (iterator.hasNext())
		{
			FilterComponent fc2 = parseMultipleFilterModules(iterator, fireInfo); // Recursion
																					// rules
			if (fc2 == null)
			{
				// fc2 has just meta filters
				return fc1;
			}
			Tand tand = new Tand();

			tand.addChild1(fc1);
			tand.addChild2(fc2);
			return tand;
		}
		return fc1;
	}

	public FilterComponent getTree(FilterReasoningEngine fireInfo)
	{
		// We start to parse, make sure we empty the symboltable.
		(SymbolTable.getInstance()).empty();
		filterNumber = 0;

		Iterator iterator = list.iterator();
		FilterComponent fc = parseMultipleFilterModules(iterator, fireInfo);

		Tand rootNode = new Tand();
		rootNode.addChild1(fc);
		rootNode.addChild2(new Action(FilterFactory.getFilter("EOF"), filterNumber + 1));

		// System.out.println (rootNode.toTreeString()); no sneaky printing to
		// the out stream!

		SymbolTable st = SymbolTable.getInstance();
		// Add the so called 'syntactic sugar' symbols.
		st.addSymbol("*", 1);
		st.addSymbol("*", 2);

		// No more symbols.
		st.done();

		return rootNode;
	}

	protected void addFIREInfo(FilterReasoningEngine fireInfo, FilterModule fm)
	{
		Iterator itr = fm.getInternalIterator();
		mapConcernToSymbol(fireInfo, itr);

		itr = fm.getExternalIterator();
		mapConcernToSymbol(fireInfo, itr);
	}

	protected void mapConcernToSymbol(FilterReasoningEngine fireInfo, Iterator itr)
	{
		SymbolTable st = SymbolTable.getInstance();

		while (itr.hasNext())
		{
			TypedDeclaration declaration = (TypedDeclaration) itr.next();

			Symbol symbol = st.getSymbol(declaration.getName(), SymbolTable.TARGET);
			if (st != null)
			{
				fireInfo.addConcernSymbol(declaration.getType().getQualifiedName(), symbol);
			}
		}
	}
}
