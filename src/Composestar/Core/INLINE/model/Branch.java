/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;

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
	private ConditionExpression conditionExpression;

	/**
	 * The condition that is checked to do the branching, or <code>null</code>
	 * if the conditionexpression is used.
	 */
	private Condition condition;

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
	 * @param conditionExpression The conditionexpression to check for the
	 *            branching.
	 */
	public Branch(ConditionExpression conditionExpression)
	{
		this.conditionExpression = conditionExpression;
	}

	/**
	 * The constructor
	 * 
	 * @param condition The condition to check for the branching.
	 */
	public Branch(Condition condition)
	{
		this.condition = condition;
	}

	/**
	 * @return the conditionExpression
	 */
	public ConditionExpression getConditionExpression()
	{
		return conditionExpression;
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition()
	{
		return condition;
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
