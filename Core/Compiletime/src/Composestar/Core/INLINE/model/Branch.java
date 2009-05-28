/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.References.MethodReference;

/**
 * An instruction indicating a conditional branch.
 * 
 * @author Arjan
 */
public class Branch extends Instruction
{
	/**
	 * The conditionexpression that is checked to do the branching, or
	 * <code>null</code> if the condition is used.
	 */
	private MatchingExpression matchingExpression;

	/**
	 * The condition that is checked to do the branching, or <code>null</code>
	 * if the conditionexpression is used.
	 */
	private MethodReference conditionMethod;

	/**
	 * The instructionblock that should be executed when the conditionexpression
	 * is true.
	 */
	private Block trueBlock;

	/**
	 * The instructionblock that should be executed when the conditionexpression
	 * is false.
	 */
	private Block falseBlock;

	/**
	 * The constructor
	 * 
	 * @param expression The expression to check for the branching.
	 */
	public Branch(MatchingExpression expression)
	{
		matchingExpression = expression;
	}

	/**
	 * The constructor
	 * 
	 * @param condition The condition to check for the branching.
	 */
	public Branch(MethodReference condition)
	{
		conditionMethod = condition;
	}

	/**
	 * @return the conditionExpression
	 */
	public MatchingExpression getMatchingExpression()
	{
		return matchingExpression;
	}

	/**
	 * @return the condition
	 */
	public MethodReference getConditionMethod()
	{
		return conditionMethod;
	}

	/**
	 * @return the falseBlock
	 */
	public Block getFalseBlock()
	{
		return falseBlock;
	}

	/**
	 * @param value the falseBlock to set
	 */
	public void setFalseBlock(Block value)
	{
		falseBlock = value;
	}

	/**
	 * @return the trueBlock
	 */
	public Block getTrueBlock()
	{
		return trueBlock;
	}

	/**
	 * @param value the trueBlock to set
	 */
	public void setTrueBlock(Block value)
	{
		trueBlock = value;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitBranch(this);
	}

}
