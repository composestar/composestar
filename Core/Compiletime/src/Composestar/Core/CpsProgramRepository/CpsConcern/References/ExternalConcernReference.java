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

	public String inittarget = "";

	public String initselector = "";

	public void setInitTarget(String inittarget)
	{
		this.inittarget = inittarget;
	}

	public String getInitTarget()
	{
		return this.inittarget;
	}

	public void setInitSelector(String initselector)
	{
		this.initselector = initselector;
	}

	public String getInitSelector()
	{
		return this.initselector;
	}
}
