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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.ConditionBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.MethodBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Checks if a Selector which is declared in selectors is used in the
 * FilterModuleBinding. Since every selector also has a standard selector self,
 * this one is filtered out by default.
 * 
 * @author DoornenbalD
 */
public class NotUsedSelector extends BaseChecker
{
	/*
	 * @see Composestar.Core.CHKREP.BaseChecker#performCheck() Chnaged the check
	 *      to check in all four bindings instead of one, code might be more
	 *      compact, whether it is doubtfull whether we would gain much when we
	 *      compress the four search loops into one more reusable loop
	 */
	@Override
	public boolean performCheck()
	{
		Iterator<SelectorDefinition> selectors = ds.getAllInstancesOf(SelectorDefinition.class);
		while (selectors.hasNext())
		{
			// Gets defintion to check
			SelectorDefinition selDef = selectors.next();
			boolean isUsed = false;

			// search where this definition is used in the filtermodulebindings
			SuperImposition si = (SuperImposition) selDef.getParent();
			Iterator<FilterModuleBinding> fmbi = si.getFilterModuleBindingIterator();
			while (fmbi.hasNext())
			{
				FilterModuleBinding fmb = fmbi.next();
				SelectorReference sf = fmb.getSelector();
				if (sf.getName().equals(selDef.getName()))
				{
					isUsed = true;
				}
			}

			if (!isUsed)
			{
				// a little (style) error in the repository?
				for (Object o : si.getAnnotationBindings())
				{
					AnnotationBinding ab = (AnnotationBinding) o;
					SelectorReference sf = ab.getSelector();
					if (sf.getName().equals(selDef.getName()))
					{
						isUsed = true;
					}
				}
			}

			if (!isUsed)
			{
				Iterator<MethodBinding> mb = si.getMethodBindingIterator();
				while (mb.hasNext())
				{
					MethodBinding m = mb.next();
					SelectorReference sf = m.getSelector();
					if (sf.getName().equals(selDef.getName()))
					{
						isUsed = true;
					}
				}
			}

			if (!isUsed)
			{
				Iterator<ConditionBinding> cb = si.getMethodBindingIterator();
				while (cb.hasNext())
				{
					ConditionBinding c = cb.next();
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
				logger.warn("Selector " + selDef.getName() + " is declared but never used", selDef);
			}
		}
		return false;
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
