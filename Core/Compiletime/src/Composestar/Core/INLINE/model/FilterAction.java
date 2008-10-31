/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;

/**
 * Instruction indicating a FilterAction.
 * 
 * @author Arjan
 */
// TODO: filter arguments
public class FilterAction extends Instruction
{
	/**
	 * The type of the filteraction
	 */
	private String type;

	/**
	 * The current message in the filterset
	 */
	private CpsMessage message;

	/**
	 * Indicates whether the action is on call (true) or on return (false)
	 */
	private boolean onCall;

	/**
	 * Indicates whether the action returns the flow (only when the action is on
	 * call).
	 */
	private boolean returning;

	/**
	 * If true perform resource operation bookkeeping for this action.
	 * BookKeeping in the FilterAction is subject to the bookkeeping of the
	 * parent FilterCode.
	 */
	private boolean bookkeeping;

	/**
	 * A string containing the resource operations that lead to this filter
	 * action as harvested from the execution model.
	 */
	private String resourceOperations = "";

	/**
	 * Should a JointPointContext be created for this filter action?
	 */
	private boolean createJPC = true;

	/**
	 * The constructor
	 * 
	 * @param type The type of the filteraction.
	 * @param message The current message.
	 * @param substitutedMessage The substituted message (substitutionpart
	 *            applied on the current message).
	 * @param onCall Indicates whether the action is on call (true) or on return
	 *            (false)
	 * @param returning Indicates whether the action returns the flow (only when
	 *            the action is on call).
	 */
	public FilterAction(String type, CpsMessage message, boolean onCall, boolean returning)
	{
		this.type = type;
		this.message = message;
		this.onCall = onCall;
		this.returning = returning;
	}

	/**
	 * @return the message
	 */
	public CpsMessage getMessage()
	{
		return message;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @return the onCall
	 */
	public boolean isOnCall()
	{
		return onCall;
	}

	/**
	 * @return the returning
	 */
	public boolean isReturning()
	{
		return returning;
	}

	/**
	 * @param value
	 * @see #bookkeeping
	 */
	public void setBookKeeping(boolean value)
	{
		bookkeeping = value;
	}

	/**
	 * @return
	 * @see #bookkeeping
	 */
	public boolean getBookKeeping()
	{
		return bookkeeping;
	}

	/**
	 * @param value
	 * @see #resourceOperations
	 */
	public void setResourceOperations(String value)
	{
		if (value == null)
		{
			value = "";
		}
		resourceOperations = value;
	}

	/**
	 * @see #resourceOperations
	 * @return
	 */
	public String getResourceOperations()
	{
		return resourceOperations;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitFilterAction(this);
	}

	/**
	 * @return the createJPC
	 */
	public boolean getCreateJPC()
	{
		return createJPC;
	}

	/**
	 * @param createJPC the createJPC to set
	 */
	public void setCreateJPC(boolean createJPC)
	{
		this.createJPC = createJPC;
	}

}
