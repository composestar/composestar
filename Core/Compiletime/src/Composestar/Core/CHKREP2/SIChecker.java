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

import java.util.HashSet;
import java.util.Set;

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SuperImposition.SICondition;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2Impl.FMParams.SelectorFMParamValue;

/**
 * Check the superimposition blocks for used selectors, filter modules and
 * annotations. Warnings will be issued for unused selectors and conditions.
 * Errors will be created for unknown selectors, conditions, filtermodules and
 * annotations.
 * 
 * @author Michiel Hendriks
 */
public class SIChecker extends AbstractChecker
{
	/**
	 * All declared selectors.
	 */
	protected Set<Selector> allSelectors;

	/**
	 * Used selectors
	 */
	protected Set<Selector> usedSelectors;

	/**
	 * All declared conditions
	 */
	protected Set<SICondition> allConditions;

	/**
	 * Used conditions
	 */
	protected Set<SICondition> usedConditions;

	public SIChecker()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CHKREP2.AbstractChecker#performCheck(Composestar.Core
	 * .RepositoryImplementation.DataStore)
	 */
	@Override
	public void performCheck(Repository repository)
	{
		allSelectors = new HashSet<Selector>();
		usedSelectors = new HashSet<Selector>();
		allConditions = new HashSet<SICondition>();
		usedConditions = new HashSet<SICondition>();
		for (RepositoryEntity re : repository)
		{
			if (re instanceof Selector)
			{
				allSelectors.add((Selector) re);
			}
			else if (re instanceof SICondition)
			{
				allConditions.add((SICondition) re);
			}
			else if (re instanceof FilterModuleBinding)
			{
				FilterModuleBinding fmb = (FilterModuleBinding) re;
				if (fmb.getCondition() != null)
				{
					usedConditions.add(fmb.getCondition());
				}
				usedSelectors.add(fmb.getSelector());
			}
			else if (re instanceof AnnotationBinding)
			{
				usedSelectors.add(((AnnotationBinding) re).getSelector());
			}
			else if (re instanceof SelectorFMParamValue)
			{
				usedSelectors.add(((SelectorFMParamValue) re).getSelector());
			}
		}
		// calculate and report usage
		allSelectors.removeAll(usedSelectors);
		for (Selector unused : allSelectors)
		{
			if ("self".equals(unused.getName()))
			{ // implied selector, don't report
				continue;
			}
			results.addWarning(String.format("Selector \"%s\" is not used", unused.getName()), unused);
		}
		allConditions.removeAll(usedConditions);
		for (SICondition unused : allConditions)
		{
			results.addWarning(String.format("Superimposition condition \"%s\" is not used", unused.getName()), unused);
		}
	}
}
