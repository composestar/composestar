package Composestar.Core.CHKREP;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

public class ExistSelector extends BaseChecker
{
	@Override
	public boolean performCheck()
	{
		Iterator<FilterModuleBinding> fmbi = ds.getAllInstancesOf(FilterModuleBinding.class);
		boolean nonFatal = true;

		while (fmbi.hasNext())
		{
			FilterModuleBinding fmb = fmbi.next();
			String selector = fmb.getSelector().getName();
			boolean exist = false;

			SuperImposition si = (SuperImposition) fmb.getParent();
			Iterator<SelectorDefinition> sels = si.getSelectorIterator();
			while (sels.hasNext())
			{
				SelectorDefinition sd = sels.next();
				if (sd.getName().equals(selector))
				{
					exist = true;
				}
			}

			if (!exist)
			{
				logger.error("Selector " + selector + " is used but not declared", fmb);
				nonFatal = false;
			}
		}
		return nonFatal;
	}

	@Override
	public void check(DataStore newDs) throws ModuleException
	{
		ds = newDs;
		// REXREF does checks it as well, so this would be the second test and
		// is therefore quite useless
		boolean nonFatal = true; // performCheck();

		if (!nonFatal)
		{
			throw new ModuleException("One or more Selectors in filtermodules are not declared", "CHKREP");
		}
	}

}
