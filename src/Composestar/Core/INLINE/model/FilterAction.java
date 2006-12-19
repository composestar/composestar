/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.FIRE2.model.Message;

/**
 * Instruction indicating a FilterAction.
 * 
 * @author Arjan
 */
public class FilterAction extends Instruction
{
	/**
	 * The type of the filteraction
	 */
	private String type;

	/**
	 * The current message in the filterset
	 */
	private Message message;

	/**
	 * The substituted message (substitutionpart applied on the current
	 * message).
	 */
	private Message substitutedMessage;

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
	public FilterAction(String type, Message message, Message substitutedMessage, boolean onCall, boolean returning)
	{
		this.type = type;
		this.message = message;
		this.substitutedMessage = substitutedMessage;
		this.onCall = onCall;
		this.returning = returning;
	}

	/**
	 * @return the message
	 */
	public Message getMessage()
	{
		return message;
	}

	/**
	 * @return the substitutedMessage
	 */
	public Message getSubstitutedMessage()
	{
		return substitutedMessage;
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
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitFilterAction(this);
	}

}
