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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameterValue;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SelectorFilterModuleParameterValue;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Check the super imposition blocks for used selectors and filter modules
 * 
 * @author Michiel Hendriks
 */
public class SIChecker extends AbstractChecker
{
	protected Map<String, SelectorDefinition> selectors;

	protected Map<String, Condition> conditions;

	protected Set<String> usedSelectors;

	protected Set<String> usedConditions;

	public SIChecker()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP2.AbstractChecker#performCheck(Composestar.Core.RepositoryImplementation.DataStore)
	 */
	@Override
	public void performCheck(DataStore repository)
	{
		Iterator<SuperImposition> sii = repository.getAllInstancesOf(SuperImposition.class);
		while (sii.hasNext())
		{
			checkSuperImposition(sii.next(), repository);
		}
	}

	protected void checkSuperImposition(SuperImposition si, DataStore repository)
	{
		logger.info(String.format("Checking %s", si.getQualifiedName()));
		selectors = new HashMap<String, SelectorDefinition>();
		conditions = new HashMap<String, Condition>();
		usedSelectors = new HashSet<String>();
		usedConditions = new HashSet<String>();

		// collect all selectors and conditions
		Iterator<SelectorDefinition> sdi = si.getSelectorIterator();
		while (sdi.hasNext())
		{
			SelectorDefinition sd = sdi.next();
			selectors.put(sd.getName(), sd);
		}
		Iterator<Entry<String, Condition>> ci = si.getFilterModuleConditions();
		while (ci.hasNext())
		{
			Entry<String, Condition> cond = ci.next();
			conditions.put(cond.getKey(), cond.getValue());
		}

		Iterator<FilterModuleBinding> fmbi = si.getFilterModuleBindingIterator();
		while (fmbi.hasNext())
		{
			checkFilterModuleBinding(fmbi.next(), repository);
		}
		for (AnnotationBinding ab : (List<AnnotationBinding>) si.getAnnotationBindings())
		{
			checkAnnotationBinding(ab, repository);
		}

		// calculate and report usage
		usedSelectors.add("self");
		selectors.keySet().removeAll(usedSelectors);
		for (SelectorDefinition unused : selectors.values())
		{
			results.addWarning(String.format("Selector \"%s\" is not used", unused.getName()), unused);
		}
		conditions.keySet().removeAll(usedConditions);
		for (Condition unused : conditions.values())
		{
			results.addWarning(String.format("Superimposition condition \"%s\" is not used", unused.getName()), unused);
		}
	}

	protected void checkFilterModuleBinding(FilterModuleBinding fmb, DataStore repository)
	{
		if (fmb.getFilterModuleCondition() != null)
		{
			usedConditions.add(fmb.getFilterModuleCondition().getName());
		}
		Iterator<FilterModuleReference> fms = fmb.getFilterModuleIterator();
		while (fms.hasNext())
		{
			FilterModuleReference fm = fms.next();
			String fmFqn = fm.getQualifiedName();
			Object o = repository.getObjectByID(fmFqn);
			if (o == null || !(o instanceof FilterModuleAST))
			{
				results.addError(String.format("Filter module \"%s\" does not exist", fmFqn), fmb);
			}
			for (FilterModuleParameter fmp : (Collection<FilterModuleParameter>) fm.getArgs())
			{
				for (FilterModuleParameterValue fmpv : (Collection<FilterModuleParameterValue>) fmp.getValue())
				{
					if (fmpv instanceof SelectorFilterModuleParameterValue)
					{
						usedSelectors.add(((SelectorFilterModuleParameterValue) fmpv).getSelector().getName());
					}
				}
			}
		}
		String sel = fmb.getSelector().getName();
		if (!selectors.containsKey(sel))
		{
			results.addError(String.format("Selector with the name \"%s\" does not exist.", sel), fmb);
		}
		else
		{
			usedSelectors.add(sel);
		}
	}

	protected void checkAnnotationBinding(AnnotationBinding ab, DataStore repository)
	{
		String sel = ab.getSelector().getName();
		if (!selectors.containsKey(sel))
		{
			results.addError(String.format("Selector with the name \"%s\" does not exist.", sel), ab);
		}
		else
		{
			usedSelectors.add(sel);
		}
	}
}
