/* This file is part of Composestar project [http://composestar.sf.net].
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
import Composestar.Core.CpsProgramRepository.CpsConcern.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

/**
 * @author DoornenbalD ExistFiltermodule checks whether the filtermodules used
 *         at the filtermodulebinding do exist. In the future it might be
 *         necessary to extend this to the filtermodules stated in the externals
 *         (maybe internals). Since these are not yet implemented.
 */
public class ExistFilterModule implements BaseChecker
{

	private DataStore ds;

	private boolean checkFilterMethodBinding()
	{
		boolean nonFatal = true;
		Iterator fmbs = ds.getAllInstancesOf(FilterModuleBinding.class);
		while (fmbs.hasNext())
		{
			FilterModuleBinding fmb = (FilterModuleBinding) fmbs.next();
			Iterator fms = fmb.getFilterModuleIterator();
			while (fms.hasNext())
			{
				FilterModuleReference fm = (FilterModuleReference) fms.next();
				boolean filterModuleReferenceExists = false;
				String filterModuleName = fm.getName();
				Iterator concerns = ds.getAllInstancesOf(CpsConcern.class);
				while (concerns.hasNext())
				{
					CpsConcern concern = (CpsConcern) concerns.next();
					Iterator fmi = concern.getFilterModuleIterator();
					if (fmi != null)
					{
						while (fmi.hasNext())
						{
							FilterModule fm2 = (FilterModule) fmi.next();
							if (fm2.getName().equals(filterModuleName))
							{
								if (((CpsConcern) fm2.getParent()).getName().equals(fm.getConcern()))
								{
									filterModuleReferenceExists = true;
								}
							}
						}
					}
				}
				if (!filterModuleReferenceExists)
				{
					Debug.out(Debug.MODE_ERROR, "CHKREP", "FilterModule " + filterModuleName + " is not declared", fmb
							.getDescriptionFileName(), fmb.getDescriptionLineNumber());
					nonFatal = true;
				}
			}
		}
		return nonFatal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck()
	 */
	public boolean performCheck()
	{
		boolean nonFatal;
		nonFatal = checkFilterMethodBinding();
		return nonFatal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#check(Composestar.Core.RepositoryImplementation.DataStore)
	 */
	public void check(DataStore newDs) throws ModuleException
	{
		ds = newDs;
		boolean nonFatal = performCheck();

		if (!nonFatal)
		{
			throw new ModuleException("One or more FilterModules in filtermodules are not declared", "CHKREP");
		}
	}

}
