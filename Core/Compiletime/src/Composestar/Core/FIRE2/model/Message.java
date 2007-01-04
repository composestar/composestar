/*
 * Created on 10-apr-2006
 *
 */
package Composestar.Core.FIRE2.model;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;

/**
 * @author Arjan de Roo
 */
public class Message
{
	/**
	 * The target of the message, or "*" to indicate an unknown target.
	 */
	private Target target;

	/**
	 * The selector of the message, or "*" to indicate an unknown selector.
	 */
	private MessageSelector selector;

	public final static Target STAR_TARGET = getStarTarget();

	public final static Target INNER_TARGET = getInnerTarget();

	public final static Target SELF_TARGET = getSelfTarget();

	public final static Target UNDISTINGUISHABLE_TARGET = getUndistinguishableTarget();

	public final static MessageSelector STAR_SELECTOR = getStarSelector();

	public final static MessageSelector UNDISTINGUISHABLE_SELECTOR = getUndistinguishableSelector();

	/**
	 * The STAR-message
	 */
	public final static Message STAR_MESSAGE = new Message(STAR_TARGET, STAR_SELECTOR);

	private static Target getStarTarget()
	{
		Target target = new Target();
		target.setName("*");
		return target;
	}

	private static Target getInnerTarget()
	{
		Target target = new Target();
		target.setName("inner");
		return target;
	}

	private static Target getSelfTarget()
	{
		Target target = new Target();
		target.setName("self");
		return target;
	}

	private static Target getUndistinguishableTarget()
	{
		Target target = new Target();
		target.setName("+");
		return target;
	}

	private static MessageSelector getStarSelector()
	{
		// TODO: (michiel) validate that using an empty MessageSelectorAST is
		// legal
		MessageSelector selector = new MessageSelector(new MessageSelectorAST());
		selector.setName("*");
		return selector;
	}

	private static MessageSelector getUndistinguishableSelector()
	{
		// TODO: (michiel) validate that using an empty MessageSelectorAST is
		// legal
		MessageSelector selector = new MessageSelector(new MessageSelectorAST());
		selector.setName("+");
		return selector;
	}

	/**
	 * The constructs a message with the given target and selector.
	 * 
	 * @param target
	 * @param selector
	 */
	public Message(Target target, MessageSelector selector)
	{
		this.target = target;
		this.selector = selector;
	}

	/**
	 * @return Returns the selector.
	 */
	public MessageSelector getSelector()
	{
		return selector;
	}

	/**
	 * @return Returns the target.
	 */
	public Target getTarget()
	{
		return target;
	}

	/**
	 * Indicates whether this message is a generalization of a set of message,
	 * i.e. whether the target and/or the selector is the star.
	 * 
	 * @return <code>true</code> when either the selector or target or both is
	 *         the star. else <code>false</code>
	 */
	public boolean isGeneralization()
	{
		return checkEquals(selector, Message.STAR_SELECTOR) || checkEquals(target, Message.STAR_TARGET);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Message))
		{
			return false;
		}
		Message m = (Message) obj;

		return checkEquals(m.selector, selector) && checkEquals(m.target, target);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return selector.getName().hashCode() + target.getName().hashCode();
	}

	public static boolean checkEquals(MessageSelector selector1, MessageSelector selector2)
	{
		return selector1.getName().equals(selector2.getName());
	}

	public static boolean checkEquals(Target target1, Target target2)
	{
		return target1.getName().equals(target2.getName());
	}
}
