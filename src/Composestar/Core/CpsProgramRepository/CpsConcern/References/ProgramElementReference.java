/*
 * Created on Mar 17, 2005
 *
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.LAMA.ProgramElement;;

/**
 * @author whavinga
 *
 *
 */
public class ProgramElementReference extends Reference
{
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
