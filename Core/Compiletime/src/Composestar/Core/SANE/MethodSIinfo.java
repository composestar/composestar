//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\SANE\\MethodSIinfo.java

//Source file: D:\\Lodewijk\\software\\ComposeStar\\composestar\\src\\Composestar\\core\\SANE\\MethodSIinfo.java

package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method;

/**
 * @deprecated condition super imposition is not implements
 */
public class MethodSIinfo extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity implements
		AbstractSIinfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4184915080051882714L;

	public Vector superimposed;

	/**
	 * @roseuid 404C4B6600DE
	 */
	public MethodSIinfo()
	{
		superimposed = new Vector();
	}

	/**
	 * @param concern
	 * @roseuid 4059833902DB
	 */
	public void bind(CpsConcern concern)
	{

	}

	/**
	 * get a superimposed method through its name
	 * 
	 * @param name
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
	 * @roseuid 40598713012A
	 */
	public Method getByName(String name)
	{
		return null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 4059875501A7
	 */
	public Vector getAll()
	{
		return null;
	}

	/**
	 * @return Composestar.Utils.*;
	 * @roseuid 4059875501D9
	 */
	public Iterator getIter()
	{
		return null;
	}

	/**
	 * @param methodIter
	 * @roseuid 405A70E30195
	 */
	public void addMethods(Iterator methodIter)
	{

	}
}
