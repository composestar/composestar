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
 * $Id$
 */

package Composestar.Core.CHKREP2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Performs checks on all filter modules. Warnings will be issued for unused
 * internals, externals and conditions. When unknown internals, externals or
 * conditions are used in the filter elements an error will be created.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleChecker extends AbstractChecker
{
	/**
	 * Contains a list of all defined externals in the current filter module
	 */
	protected Map<String, External> externals;

	/**
	 * Contains a list of all defined internals in the current filter module
	 */
	protected Map<String, Internal> internals;

	/**
	 * A list of all defined condition methods in the current filter module
	 */
	protected Map<String, Condition> conditions;

	/**
	 * A list of names of all externals used in the filter elements
	 */
	protected Set<String> usedExternals;

	/**
	 * A list of all names of the used internals in the filter elements of the
	 * current filter module.
	 */
	protected Set<String> usedInternals;

	/**
	 * A list of the conditions that are used in the filter elements of the
	 * current filter module.
	 */
	protected Set<String> usedConditions;

	public FilterModuleChecker()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP2.AbstractChecker#performCheck(Composestar.Core.RepositoryImplementation.DataStore)
	 */
	@Override
	public void performCheck(DataStore repository)
	{
		Iterator<FilterModule> fmi = repository.getAllInstancesOf(FilterModule.class);
		while (fmi.hasNext())
		{
			checkFilterModule(fmi.next());
		}
	}

	/**
	 * Performs internal/external/condition usage checks on the given filter
	 * module.
	 * 
	 * @param fm the filter module to inspect
	 */
	protected void checkFilterModule(FilterModule fm)
	{
		logger.info("Checking filter module " + fm.getQualifiedName());

		externals = new HashMap<String, External>();
		internals = new HashMap<String, Internal>();
		conditions = new HashMap<String, Condition>();
		usedExternals = new HashSet<String>();
		usedInternals = new HashSet<String>();
		usedConditions = new HashSet<String>();

		// first extract all FM elements: internals, externals, conditions
		Iterator<External> exti = fm.getExternalIterator();
		while (exti.hasNext())
		{
			External ext = exti.next();
			externals.put(ext.getName(), ext);
		}
		Iterator<Internal> inti = fm.getInternalIterator();
		while (inti.hasNext())
		{
			Internal inten = inti.next();
			internals.put(inten.getName(), inten);
		}
		Iterator<Condition> condi = fm.getConditionIterator();
		while (condi.hasNext())
		{
			Condition cond = condi.next();
			conditions.put(cond.getName(), cond);

			// directly check for internal/external usage
			Reference ref = cond.getShortref();
			if (ref != null)
			{
				checkIntExtRef(ref.getName());
			}
		}

		// iterate through all filter elements
		Iterator<Filter> filters = fm.getInputFilterIterator();
		while (filters.hasNext())
		{
			Filter filter = filters.next();
			Iterator<FilterElement> fei = filter.getFilterElementIterator();
			while (fei.hasNext())
			{
				checkFilterElement(fei.next());
			}
		}
		filters = fm.getOutputFilterIterator();
		while (filters.hasNext())
		{
			Filter filter = filters.next();
			Iterator<FilterElement> fei = filter.getFilterElementIterator();
			while (fei.hasNext())
			{
				checkFilterElement(fei.next());
			}
		}

		// calculate and report usage
		externals.keySet().removeAll(usedExternals);
		for (External unused : externals.values())
		{
			results.addWarning(String.format("External \"%s\" is not used", unused.getQualifiedName()), unused);
		}
		internals.keySet().removeAll(usedInternals);
		for (Internal unused : internals.values())
		{
			results.addWarning(String.format("Internal \"%s\" is not used", unused.getQualifiedName()), unused);
		}
		conditions.keySet().removeAll(usedConditions);
		for (Condition unused : conditions.values())
		{
			results.addWarning(String.format("Condition \"%s\" is not used", unused.getQualifiedName()), unused);
		}

	}

	/**
	 * Check if the name is an internal or external and set the usage flag
	 * 
	 * @param ident
	 */
	protected void checkIntExtRef(String ident)
	{
		if (externals.containsKey(ident))
		{
			usedExternals.add(ident);
		}
		else if (internals.containsKey(ident))
		{
			usedInternals.add(ident);
		}
	}

	/**
	 * Performs checks on the given filter element. This method is called from
	 * {@link #checkFilterModule(FilterModule)}
	 * 
	 * @param fe the filter element to validate.
	 */
	protected void checkFilterElement(FilterElement fe)
	{
		// check for conditions
		ConditionExpression expr = fe.getConditionPart();
		if (expr != null && !(expr instanceof CondLiteral))
		{
			checkConditionExpression(expr);
		}
		// check for internals/externals
		MatchingPattern mp = fe.getMatchingPattern();
		Iterator<SubstitutionPart> spi = mp.getSubstitutionPartsIterator();
		while (spi.hasNext())
		{
			SubstitutionPart sp = spi.next();
			String name = sp.getTarget().getName();
			if (Target.INNER.endsWith(name) || Target.SELF.endsWith(name) || "*".endsWith(name))
			{
				continue;
			}
			checkIntExtRef(name);
		}

		Iterator<MatchingPart> matchpi = mp.getMatchingPartsIterator();
		while (matchpi.hasNext())
		{
			MatchingPart matchp = matchpi.next();
			String name = matchp.getTarget().getName();
			if (Target.INNER.endsWith(name) || Target.SELF.endsWith(name) || "*".endsWith(name))
			{
				continue;
			}
			checkIntExtRef(name);
		}
	}

	/**
	 * Inspect the current condition expression for usage of registered
	 * conditions.
	 * 
	 * @param expr the expression instance to inspect
	 */
	protected void checkConditionExpression(ConditionExpression expr)
	{
		if (expr instanceof ConditionVariable)
		{
			ConditionVariable cl = (ConditionVariable) expr;
			String conditionName = cl.getCondition().getName();
			if (conditions.containsKey(conditionName))
			{
				usedConditions.add(conditionName);
			}
			else
			{
				results.addError(String.format("There is no condition with the name \"%s\"", conditionName), expr);
			}
		}
		else if (expr instanceof UnaryOperator)
		{
			checkConditionExpression(((UnaryOperator) expr).getOperand());
		}
		else if (expr instanceof BinaryOperator)
		{
			checkConditionExpression(((BinaryOperator) expr).getLeft());
			checkConditionExpression(((BinaryOperator) expr).getRight());
		}
	}
}
