/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * Indicates an expression on the filtercontext.
 * 
 * @author Arjan
 */
public class ContextExpression
{
	/**
	 * The type of the contextexpression.
	 */
	private int type;

	/**
	 * Indicates a boolean contextexpression that indicates whether there are
	 * more stored after actions.
	 */
	public final static int HAS_MORE_ACTIONS = 22;

	/**
	 * Indicates an integer contextexpression that indicates the stored action
	 * to execute.
	 */
	public final static int RETRIEVE_ACTION = 23;

	/**
	 * The constructor
	 * 
	 * @param type The type of contextexpression
	 */
	public ContextExpression(int type)
	{
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}

}
