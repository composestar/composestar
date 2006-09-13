package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.ConditionExpressions.*;

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
	 * @property 
	 */
	public ConditionExpression get_ConditionExpression()
	{
		return conditionExpression;
	}

	/**
     * @return the falseBlock
	 * @property 
	 */
		public Block get_FalseBlock()
	{
		return falseBlock;
	}

	/**
     * @param falseBlock the falseBlock to set
	 * @property 
	 */
		public void set_FalseBlock(Block falseBlock)
	{
			this.falseBlock = falseBlock;
	}

	/**
     * @return the trueBlock
	 * @property 
     */
		public Block get_TrueBlock()
	{
			return trueBlock;
		}

	/**
     * @param trueBlock the trueBlock to set
	 * @property 
     */
	public void set_TrueBlock(Block trueBlock)
	{
		this.trueBlock = trueBlock;
		}
}
