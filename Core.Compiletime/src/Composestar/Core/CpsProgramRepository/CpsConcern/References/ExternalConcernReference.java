/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

/**
 * @author pascal TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExternalConcernReference extends ConcernReference
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7204562444465232564L;

	public String inittarget = "";

	public String initselector = "";

	public void setInitTarget(String inIinittarget)
	{
		inittarget = inIinittarget;
	}

	public String getInitTarget()
	{
		return inittarget;
	}

	public void setInitSelector(String inInitselector)
	{
		initselector = inInitselector;
	}

	public String getInitSelector()
	{
		return initselector;
	}
}