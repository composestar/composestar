//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\SANE\\FilterModSIinfo.java

//Source file: D:\\Lodewijk\\software\\ComposeStar\\composestar\\src\\Composestar\\core\\SANE\\FilterModSIinfo.java

package Composestar.Core.SANE;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import java.util.Vector;
import Composestar.Utils.*;
import java.util.Iterator;

/**
 * This represents one single possible set of filtermodules as they may be
 * superimposed.
 */
public class FilterModSIinfo extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity implements
		AbstractSIinfo
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4721833069236805491L;

	public Vector superimposed;

	/**
	 * @roseuid 404C4B660070
	 */
	public FilterModSIinfo()
	{
		superimposed = new Vector();
	}

	/**
	 * @param concern
	 * @roseuid 4059833E031E
	 */
	public void bind(CpsConcern concern)
	{

	}

	/**
	 * get a superimposed filter module through its name
	 * 
	 * @param name
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule
	 * @roseuid 405986F50195
	 */
	public FilterModule getByName(String name)
	{
		FilterModule fm = null;
		CPSIterator iter = this.getIter();
		for (; iter.hasNext();)
		{
			fm = ((FilterModuleReference) iter.next()).getRef();
			if (fm.getName().equals(name)) break;
		}
		if (iter.hasNext())
		{
			return fm;
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 405987550003
	 */
	public Vector getAll()
	{
		return superimposed;
	}

	/**
	 * @return Composestar.Utils.*;
	 * @roseuid 405987550035
	 */
	public CPSIterator getIter()
	{
		return new CPSIterator(superimposed);
	}

	/**
	 * @param fms
	 * @roseuid 405A70AE0275
	 */
	public void addFMs(Iterator fms)
	{
		for (; fms.hasNext();)
		{
			this.getAll().addElement(fms.next());
		}
	}
}
