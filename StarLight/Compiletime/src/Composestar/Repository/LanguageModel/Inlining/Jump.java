package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class Jump extends InlineInstruction implements IVisitable
{
	private int target;

	public Jump(int target)
	{
		this.target = target;
	}

	/**
     * @return the target
	 * @property 
	 */
	public int get_Target()
	{
		return target;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);
		visitor.VisitJumpInstruction(this);
	}
    
    public String toString(){
        return "jump " + target + "\n";
    }
}
