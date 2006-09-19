package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;  

public abstract class Instruction implements IVisitable {
	private Label label;

	public Instruction()
	{

	}

	public Instruction(Label label)
	{
		this.label = label;
	}

	/**
     * @return the label
	 * @property 
     */
	public Label get_Label()
	{
		return label;
	}

	public void Accept(IVisitor visitor)
	{

	}
}
