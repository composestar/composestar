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
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

/**
 * @author DoornenbalD Checks if a Selector which is declared in Selectors is
 *         used in the FilterModuleBinding. Since every selector also has a
 *         standard selector self, this one is standard filterd out
 */
public class NotUsedSelector implements BaseChecker
{
	private DataStore ds;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck() Chnaged the check
	 *      to check in all four bindings instead of one, code might be more
	 *      compact, whether it is doubtfull whether we would gain much when we
	 *      compress the four search loops into one more reusable loop
	 */
	public boolean performCheck()
	{
		Iterator selectors = ds.getAllInstancesOf(SelectorDefinition.class);
		while (selectors.hasNext())
		{
			// Gets defintion to check
			SelectorDefinition selDef = (SelectorDefinition) selectors.next();
			boolean isUsed = false;

			// search where this definition is used in the filtermodulebindings
			SuperImposition si = (SuperImposition) selDef.getParent();
			Iterator fmbi = si.getFilterModuleBindingIterator();
			while (fmbi.hasNext())
			{
				FilterModuleBinding fmb = (FilterModuleBinding) fmbi.next();
				SelectorReference sf = fmb.getSelector();
				if (sf.getName().equals(selDef.getName()))
				{
					isUsed = true;
				}
			}

			if (!isUsed)
			{
				// a little (style) error in the repository?
				Iterator annotbinding = si.getAnnotationBindings().iterator();
				while (annotbinding.hasNext())
				{
					AnnotationBinding ab = (AnnotationBinding) annotbinding.next();
					SelectorReference sf = ab.getSelector();
					if (sf.getName().equals(selDef.getName()))
					{
						isUsed = true;
					}
				}
			}

			if (!isUsed)
			{
				Iterator mb = si.getMethodBindingIterator();
				while (mb.hasNext())
				{
					MethodBinding m = (MethodBinding) mb.next();
					SelectorReference sf = m.getSelector();
					if (sf.getName().equals(selDef.getName()))
					{
						isUsed = true;
					}
				}
			}

			if (!isUsed)
			{
				Iterator cb = si.getMethodBindingIterator();
				while (cb.hasNext())
				{
					ConditionBinding c = (ConditionBinding) cb.next();
					SelectorReference sf = c.getSelector();
					if (sf.getName().equals(selDef.getName()))
					{
						isUsed = true;
					}
				}
			}

			/*
			 * Filters out the standard selector self
			 */
			if (!isUsed && !selDef.getName().equals("self"))
			{
				Debug.out(Debug.MODE_WARNING, "CHKREP", "Selector " + selDef.getName() + " is declared but never used",
						selDef.getDescriptionFileName(), selDef.getDescriptionLineNumber());
			}
		}
		return false;
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
