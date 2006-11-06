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

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

/**
 * @author DoornenbalD Checks on not used Externals.
 */
public class NotUsedExternals implements BaseChecker
{
	private DataStore ds;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck()
	 */
	public boolean performCheck()
	{
		Iterator filterModules = ds.getAllInstancesOf(FilterModule.class);
		while (filterModules.hasNext())
		{
			FilterModule filterModule = (FilterModule) filterModules.next();
			/*
			 * If the filtermodule has any internals, o further, otherwise do
			 * nothing
			 */
			Iterator externals = filterModule.getExternalIterator();
			while (externals.hasNext())
			{
				/*
				 * For external we will search whether it is used anywhere the
				 * check sequence is: 1. input filter 2. output filter 3.
				 * conditions
				 */
				boolean isExternalUsed = false;
				External external = (External) externals.next();
				String externalID = external.getName();

				/*
				 * With the Inputfilters you only have to check the substitution
				 * part because the matching target can be only inner or *
				 */
				Iterator inputFilters = filterModule.getInputFilterIterator();
				while (inputFilters.hasNext())
				{
					Filter inputFilter = (Filter) inputFilters.next();
					Iterator fei = inputFilter.getFilterElementIterator();
					while (fei.hasNext())
					{
						FilterElement fe = (FilterElement) fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						Iterator spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext())
						{
							SubstitutionPart sp = (SubstitutionPart) spi.next();
							if (!(sp == null))
							{
								if (externalID.equals(sp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}
					}
				}

				/*
				 * In the Outputfilters both matching and substitution target
				 * must be checked.
				 */
				Iterator outputFilters = filterModule.getInputFilterIterator();
				while (outputFilters.hasNext())
				{
					Filter outputFilter = (Filter) outputFilters.next();
					Iterator fei = outputFilter.getFilterElementIterator();
					while (fei.hasNext())
					{
						FilterElement fe = (FilterElement) fei.next();
						MatchingPattern mp = fe.getMatchingPattern();
						// Check whether the internal show up as matching target
						// or substitution target (maybe the if statement is too
						// long)
						Iterator spi = mp.getSubstitutionPartsIterator();
						while (spi.hasNext())
						{
							SubstitutionPart sp = (SubstitutionPart) spi.next();
							if (!(sp == null))
							{
								if (externalID.equals(sp.getTarget().getName()))
								{
									isExternalUsed = true;
								}
							}
						}

						Iterator matchpi = mp.getMatchingPartsIterator();
						while (matchpi.hasNext())
						{
							MatchingPart matchp = (MatchingPart) matchpi.next();
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
				Iterator conditions = filterModule.getConditionIterator();
				while (conditions.hasNext())
				{
					Condition condition = (Condition) conditions.next();
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
					Debug.out(Debug.MODE_WARNING, "CHKREP", "External " + externalID + " is declared but never used",
							external.getDescriptionFileName(), external.getDescriptionLineNumber());
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
	public void check(DataStore newDs) throws ModuleException
	{
		ds = newDs;
		performCheck();
	}

}
