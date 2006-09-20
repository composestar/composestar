package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class Jump extends InlineInstruction implements IVisitable
{
	private Label target;

	public Jump(Label target)
	{
		this.target = target;
	}

	/**
     * @return the target
	 * @property 
	 */
	public Label get_Target()
	{
		return target;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);
		visitor.VisitJumpInstruction(this);
	}
}
