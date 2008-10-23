/*
 * Created on 10-apr-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.LAMA.MethodInfo;

/**
 * Represents a message in the FIRE models
 * 
 * @author Arjan de Roo
 */
@Deprecated
public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The target of the message.
	 */
	private Target target;

	/**
	 * The selector of the message.
	 */
	private String selector;

	/**
	 * The star target. Only used in matching parts and substitution parts.
	 * 
	 * @deprecated Use SELF_TARGET or INNER_TARGET
	 */
	@Deprecated
	public static final Target STAR_TARGET = getStarTarget();

	/**
	 * The star selector. Only used in matching parts and substitution parts.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static final String STAR_SELECTOR = "*";

	/**
	 * The inner target
	 */
	public static final Target INNER_TARGET = getInnerTarget();

	/**
	 * The self target
	 */
	public static final Target SELF_TARGET = getSelfTarget();

	/**
	 * Placeholder for an undistinguishable target
	 */
	public static final Target UNDISTINGUISHABLE_TARGET = getUndistinguishableTarget();

	/**
	 * Placeholder for an undistinguishable selector
	 */
	public static final String UNDISTINGUISHABLE_SELECTOR = "+";

	/**
	 * Placeholder for an undistinguishable message
	 */
	public static final Message UNDISTINGUISHABLE_MESSAGE = new Message(UNDISTINGUISHABLE_TARGET,
			UNDISTINGUISHABLE_SELECTOR);

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	private static Target getStarTarget()
	{
		Target target = new Target();
		target.setName("*");
		return target;
	}

	/**
	 * @return a the Inner target
	 */
	private static Target getInnerTarget()
	{
		Target target = new Target();
		target.setName(Target.INNER);
		return target;
	}

	/**
	 * @return the Self target
	 */
	private static Target getSelfTarget()
	{
		Target target = new Target();
		target.setName(Target.SELF);
		return target;
	}

	/**
	 * @return an undistinguishable target
	 */
	private static Target getUndistinguishableTarget()
	{
		Target target = new Target();
		target.setName("+");
		return target;
	}

	/**
	 * The constructs a message with the given target and selector.
	 * 
	 * @param target
	 * @param selector
	 */
	public Message(Target target, String selector)
	{
		this.target = target;
		this.selector = selector;
	}

	/**
	 * The constructs a message with the given target and MethodInfo as
	 * selector.
	 * 
	 * @param target
	 * @param selector
	 */
	public Message(Target target, MethodInfo selector)
	{
		this.target = target;
		this.selector = selector.getName();
	}

	/**
	 * @return Returns the selector.
	 */
	public String getSelector()
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
		return checkEquals(selector, Message.UNDISTINGUISHABLE_SELECTOR)
				|| checkEquals(target, Message.UNDISTINGUISHABLE_TARGET);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Message))
		{
			return false;
		}
		Message m = (Message) obj;

		return checkEquals(m.selector, selector) && checkEquals(m.target, target);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return selector.hashCode() + target.hashCode();
	}

	/**
	 * Checks the equality between two selectors.
	 * 
	 * @param selector1 The first selector to check.
	 * @param selector2 The second selector to check.
	 * @return <code>true</code> if both selectors are equal.
	 */
	public static boolean checkEquals(String selector1, String selector2)
	{
		return selector1.equals(selector2);
	}

	/**
	 * Checks the equality between to targets. This is done by checking the
	 * equality of the names.
	 * 
	 * @param target1 The first target to check.
	 * @param target2 The second target to check.
	 * @return <code>true</code> if both targets are equal.
	 */
	public static boolean checkEquals(Target target1, Target target2)
	{
		return target1.getName().equals(target2.getName());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return target.getName() + "." + selector;
	}
}
