/*
 * Created on 13-okt-2006
 *
 */
package Composestar.DotNET.LAMA;

import Composestar.Core.LAMA.CallToOtherMethod;

import composestar.dotNET.tym.entities.CallElement;

public class DotNETCallToOtherMethod extends CallToOtherMethod
{
	/**
	 * The corresponding callelement in the database
	 */
	private CallElement callElement;

	/**
	 * @return the callElement
	 */
	public CallElement getCallElement()
	{
		return callElement;
	}

	/**
	 * @param callElement the callElement to set
	 */
	public void setCallElement(CallElement callElement)
	{
		this.callElement = callElement;
	}
	
	
}
