/*
 * Created on 13-okt-2006
 *
 */
package Composestar.DotNET2.LAMA;

import Composestar.Core.LAMA.CallToOtherMethod;

import composestar.dotNET2.tym.entities.CallElement;

public class DotNETCallToOtherMethod extends CallToOtherMethod
{
	private static final long serialVersionUID = -3694886424068852601L;

	/**
	 * The corresponding callelement in the database
	 */
	private transient CallElement callElement;

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
