package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * This represents one single possible set of filtermodules as they may be
 * superimposed.
 */
public class FilterModSIinfo extends ContextRepositoryEntity implements AbstractSIinfo
{
	private static final long serialVersionUID = -4721833069236805491L;

	public Vector superimposed;

	/**
	 * 
	 */
	public FilterModSIinfo()
	{
		superimposed = new Vector();
	}

	/**
	 * @param concern
	 */
	public void bind(CpsConcern concern)
	{}

	/**
	 * get a superimposed filter module through its name
	 * 
	 * @param name
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule
	 * @roseuid 405986F50195
	 */
	public FilterModuleSuperImposition getByName(String name)
	{
		FilterModule fm = null;
		Iterator iter = this.getIter();
		while (iter.hasNext())
		{
			FilterModuleSuperImposition fmsi = (FilterModuleSuperImposition) iter.next();
			fm = fmsi.getFilterModule().getRef();
			if (fm.getName().equals(name))
			{
				return fmsi;
			}
		}

		// Not found
		return null;
	}

	/**
	 * @return java.util.Vector
	 */
	public Vector getAll()
	{
		return superimposed;
	}

	/**
	 * @return Composestar.Utils.*;
	 */
	public Iterator getIter()
	{
		return superimposed.iterator();
	}

	/**
	 * @param fms
	 * @roseuid 405A70AE0275
	 */
	public void addFMs(Iterator fms, Condition condition)
	{
		while (fms.hasNext())
		{
			FilterModuleReference fm = (FilterModuleReference) fms.next();
			FilterModuleSuperImposition fmsi = new FilterModuleSuperImposition(fm, condition);
			superimposed.addElement(fmsi);
		}
	}
}
