package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.ConditionExpressions.*;
import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class Branch extends InlineInstruction implements IVisitable
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

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);

		visitor.VisitBranch(this);
		if (trueBlock != null)
			trueBlock.Accept(visitor);
		visitor.VisitBranchFalse(this);
		if (falseBlock != null)
			falseBlock.Accept(visitor);
		visitor.VisitBranchEnd(this);  
 		
	}
    
    public String toString(){
        String s = super.toString();
        s += "BRANCHING (" + conditionExpression.toString() + ")\nTRUE BRANCH:\n";
        s += trueBlock.toString();
        s += "END TRUE BRANCH\nFALSE BRANCH:";
        s += falseBlock.toString();
        s += "END FALSE BRANCH";
        
        s += "\n\n";
        
        return s;
    }
}
