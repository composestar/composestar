//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\SANE\\ConditionSIinfo.java

//Source file: D:\\Lodewijk\\software\\ComposeStar\\composestar\\src\\Composestar\\core\\SANE\\ConditionSIinfo.java

package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Utils.CPSIterator;

public class ConditionSIinfo extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity implements
		AbstractSIinfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5486410390740649543L;

	public Vector superimposed;

	/**
	 * @roseuid 404C4B66011A
	 */
	public ConditionSIinfo()
	{
		superimposed = new Vector();
	}

	/**
	 * @param concern
	 * @roseuid 405983410278
	 */
	public void bind(CpsConcern concern)
	{

	}

	/**
	 * get a superimposed condition through its name
	 * 
	 * @param name
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
	 * @roseuid 405986AE00B7
	 */
	public Condition getByName(String name)
	{
		return null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 4059875401CE
	 */
	public Vector getAll()
	{
		return null;
	}

	/**
	 * @return Composestar.Utils.*;
	 * @roseuid 405987540200
	 */
	public CPSIterator getIter()
	{
		return new CPSIterator(superimposed);
	}

	/**
	 * @param condIter
	 * @roseuid 405A710C0018
	 */
	public void addConds(Iterator condIter)
	{

	}
}
