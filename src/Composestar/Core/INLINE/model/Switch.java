/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import java.util.Vector;

/**
 * Switch instruction select the next stored action to execute.
 * 
 * @author Arjan
 */
public class Switch extends Instruction
{
	/**
	 * The switchexpression
	 */
	private ContextExpression expression;

	/**
	 * The cases.
	 */
	private Vector cases;

	/**
	 * The constructor
	 * 
	 * @param expression The switchexpression
	 */
	public Switch(ContextExpression expression)
	{
		this.expression = expression;
		this.cases = new Vector();
	}

	/**
	 * @return the expression
	 */
	public ContextExpression getExpression()
	{
		return expression;
	}

	/**
	 * Adds a case to this switch
	 * 
	 * @param caseInstruction The case to add.
	 */
	public void addCase(Case caseInstruction)
	{
		this.cases.add(caseInstruction);
	}

	/**
	 * @return An array containing all cases
	 */
	public Case[] getCases()
	{
		return (Case[]) cases.toArray(new Case[cases.size()]);
	}

	/**
	 * Indicates whether this switch has cases.
	 * 
	 * @return
	 */
	public boolean hasCases()
	{
		return !cases.isEmpty();
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitSwitch(this);
	}

}
