/*
 * Created on Mar 17, 2005
 *
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.LAMA.ProgramElement;

/**
 * @author whavinga
 */
public class ProgramElementReference extends Reference
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3188324375967823386L;

	private ProgramElement reference;

	public ProgramElementReference()
	{
		super();
	}

	public ProgramElement getRef()
	{
		return this.reference;
	}

	public void setRef(ProgramElement referencedProgElem)
	{
		this.reference = referencedProgElem;
	}
}
