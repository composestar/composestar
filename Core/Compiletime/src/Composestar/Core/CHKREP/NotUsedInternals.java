/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CHKREP;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Test whether all internals are used. Generates non-fatal warnings.
 * 
 * @author DoornenbalD
 */
public class NotUsedInternals extends BaseChecker
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck(java.util.Vector)
	 */
	@Override
	public boolean performCheck()
	{
		Iterator<FilterModule> filterModules = ds.getAllInstancesOf(FilterModule.class);
		while (filterModules.hasNext())
		{
			FilterModule filterModule = filterModules.next();
			/*
			 * If the filtermodule has any internals, o further, otherwise do
			 * nothing
			 */
			Iterator<Internal> internals = filterModule.getInternalIterator();
			while (internals.hasNext())
			{
				/*
				 * For internal we will search whether it is used anywhere the
				 * check sequence is: 1. input filter 2. output filter 3.
				 * conditions
				 */
				boolean isInternalUsed = false;
				Internal internal = internals.next();
				String internalID = internal.getName();

				Iterator<Filter> inputFilters = filterModule.getInputFilterIterator();
				while (inputFilters.hasNext() && !isInternalUsed)
				{
					Filter inputFilter = inputFilters.next();
					Iterator<FilterElement> fei = inputFilter.getFilterElementIterator();
					while (fei.hasNext() && !isInternalUsed)
					{
						FilterElement fe = fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						Iterator<SubstitutionPart> spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext() && !isInternalUsed)
						{
							SubstitutionPart sp = spi.next();
							if (!(sp == null))
							{
								if (internalID.equals(sp.getTarget().getName()))
								{
									isInternalUsed = true;
								}
							}
						}

						Iterator<MatchingPart> matchpi = mp.getMatchingPartsIterator();
						while (matchpi.hasNext() && !isInternalUsed)
						{
							MatchingPart matchp = matchpi.next();
							if (!(matchp == null))
							{
								if (internalID.equals(matchp.getTarget().getName()))
								{
									isInternalUsed = true;
								}
							}
						}
					}
				}

				/*
				 * In the Outputfilters both matching and substitution target
				 * must be checked.
				 */
				Iterator<Filter> outputFilters = filterModule.getOutputFilterIterator();
				while (outputFilters.hasNext() && !isInternalUsed)
				{
					Filter outputFilter = outputFilters.next();
					Iterator<FilterElement> fei = outputFilter.getFilterElementIterator();
					while (fei.hasNext() && !isInternalUsed)
					{
						FilterElement fe = fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						// Check whether the internal show up as matching target
						// or substitution target (maybe the if statement is too
						// long)

						Iterator<SubstitutionPart> spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext() && !isInternalUsed)
						{
							SubstitutionPart sp = spi.next();
							if (!(sp == null))
							{
								if (internalID.equals(sp.getTarget().getName()))
								{
									isInternalUsed = true;
								}
							}
						}

						Iterator<MatchingPart> matchpi = mp.getMatchingPartsIterator();
						while (matchpi.hasNext() && !isInternalUsed)
						{
							MatchingPart matchp = matchpi.next();
							if (!(matchp == null))
							{
								if (internalID.equals(matchp.getTarget().getName()))
								{
									isInternalUsed = true;
								}
							}
						}
					}
				}

				/*
				 * The search into the conditions.
				 */
				Iterator<Condition> conditions = filterModule.getConditionIterator();
				while (conditions.hasNext() && !isInternalUsed)
				{
					Condition condition = conditions.next();
					Reference ref = condition.getShortref();
					if (ref != null && ref.getName().equals(internalID))
					{
						isInternalUsed = true;
					}
				}

				/*
				 * The end check whether in one of the Iterations the internal
				 * is found to be used and the Debug warning
				 */
				if (!isInternalUsed)
				{
					logger.warn("Internal " + internalID + " is declared but never used", internal);
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#check(Composestar.Core.RepositoryImplementation.DataStore)
	 */
	@Override
	public void check(DataStore newDs) throws ModuleException
	{
		ds = newDs;
		performCheck();
	}

}
