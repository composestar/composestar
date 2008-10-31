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
		this.matchingExpression = expression;
	}

	/**
	 * The constructor
	 * 
	 * @param condition The condition to check for the branching.
	 */
	public Branch(MethodReference condition)
	{
		this.conditionMethod = condition;
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
	 * @param falseBlock the falseBlock to set
	 */
	public void setFalseBlock(Block falseBlock)
	{
		this.falseBlock = falseBlock;
	}

	/**
	 * @return the trueBlock
	 */
	public Block getTrueBlock()
	{
		return trueBlock;
	}

	/**
	 * @param trueBlock the trueBlock to set
	 */
	public void setTrueBlock(Block trueBlock)
	{
		this.trueBlock = trueBlock;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitBranch(this);
	}

}
