package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class Label extends InlineInstruction implements IVisitable
{
	private int id;

	public Label()
	{
		this.id = -1;
	}

	public Label(int id)
	{
		this.id = id;
	}

	/**
     * @return the id
	 * @property 
     */
	public int get_Id()
	{
		return id;
	}

	/**
     * @param id the id to set
	 * @property 
     */
	public void set_Id(int id)
	{
		this.id = id;
	}

	public void Accept(IVisitor visitor)
	{

	}
}
