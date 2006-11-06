/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;

public class Branch extends Instruction
{
	private ConditionExpression conditionExpression;

	private Block trueBlock;

	private Block falseBlock;

	public Branch(ConditionExpression conditionExpression)
	{
		this.conditionExpression = conditionExpression;
	}

	/**
	 * @return the conditionExpression
	 */
	public ConditionExpression getConditionExpression()
	{
		return conditionExpression;
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
