package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;  

public abstract class InlineInstruction implements IVisitable {
	private int label;

	public InlineInstruction()
	{

	}

	public InlineInstruction(int label)
	{
		this.label = label;
	}

	/**
     * @return the label
	 * @property 
     */
	public int get_Label()
	{
		return label;
	}
    
    /**
     * sets the label
     * @property 
     */
    public void set_Label( int value )
    {
        this.label = value;
    }

	public void Accept(IVisitor visitor)
	{
		visitor.VisitInlineInstruction(this);
	}
    
    public String toString(){
        if ( label >= 0 ){
            return "Label " + label + ":\n";
        }
        else{
            return "";
        }
    }
}
