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
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Checks on unused Externals.
 * 
 * @author DoornenbalD
 */
public class NotUsedExternals extends BaseChecker
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck()
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
			Iterator<External> externals = filterModule.getExternalIterator();
			while (externals.hasNext())
			{
				/*
				 * For external we will search whether it is used anywhere the
				 * check sequence is: 1. input filter 2. output filter 3.
				 * conditions
				 */
				boolean isExternalUsed = false;
				External external = externals.next();
				String externalID = external.getName();

				Iterator<Filter> inputFilters = filterModule.getInputFilterIterator();
				while (inputFilters.hasNext() && !isExternalUsed)
				{
					Filter inputFilter = inputFilters.next();
					Iterator<FilterElement> fei = inputFilter.getFilterElementIterator();
					while (fei.hasNext() && !isExternalUsed)
					{
						FilterElement fe = fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						Iterator<SubstitutionPart> spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext() && !isExternalUsed)
						{
							SubstitutionPart sp = spi.next();
							if (!(sp == null))
							{
								if (externalID.equals(sp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}

						Iterator<MatchingPart> matchpi = mp.getMatchingPartsIterator();
						while (matchpi.hasNext() && !isExternalUsed)
						{
							MatchingPart matchp = matchpi.next();
							if (!(matchp == null))
							{
								if (externalID.equals(matchp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}
					}
				}

				Iterator<Filter> outputFilters = filterModule.getOutputFilterIterator();
				while (outputFilters.hasNext() && !isExternalUsed)
				{
					Filter outputFilter = outputFilters.next();
					Iterator<FilterElement> fei = outputFilter.getFilterElementIterator();
					while (fei.hasNext() && !isExternalUsed)
					{
						FilterElement fe = fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						// Check whether the internal show up as matching target
						// or substitution target (maybe the if statement is too
						// long)
						Iterator<SubstitutionPart> spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext() && !isExternalUsed)
						{
							SubstitutionPart sp = spi.next();
							if (!(sp == null))
							{
								if (externalID.equals(sp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}

						Iterator<MatchingPart> matchpi = mp.getMatchingPartsIterator();
						while (matchpi.hasNext() && !isExternalUsed)
						{
							MatchingPart matchp = matchpi.next();
							if (!(matchp == null))
							{
								if (externalID.equals(matchp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}
					}
				}

				/*
				 * The search into the conditions.
				 */
				Iterator<Condition> conditions = filterModule.getConditionIterator();
				while (conditions.hasNext() && !isExternalUsed)
				{
					Condition condition = conditions.next();
					Reference ref = condition.getShortref();
					if (ref != null && ref.getName().equals(externalID))
					{
						isExternalUsed = true;
					}
				}

				/*
				 * The end check whether in one of the Iterations the internal
				 * is found to be used and the Debug warning
				 */
				if (!isExternalUsed)
				{
					logger.warn("External " + externalID + " is declared but never used", external);
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
