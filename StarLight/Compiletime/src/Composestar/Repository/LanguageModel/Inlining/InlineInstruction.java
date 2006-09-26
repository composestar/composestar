package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;  

public abstract class InlineInstruction implements IVisitable {
	private Label label;

	public InlineInstruction()
	{

	}

	public InlineInstruction(Label label)
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
    
    /**
     * sets the label
     * @property 
     */
    public void set_Label( Label value )
    {
        this.label = value;
    }

	public void Accept(IVisitor visitor)
	{
		visitor.VisitInlineInstruction(this);
	}
    
    public String toString(){
        if ( label != null ){
            return "Label" + label.get_Id() + ":\n";
        }
        else{
            return "";
        }
    }
}
