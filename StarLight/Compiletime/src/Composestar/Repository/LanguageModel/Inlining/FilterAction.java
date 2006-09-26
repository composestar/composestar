package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class FilterAction extends InlineInstruction implements IVisitable
{
	private String type;
	private String selector;
	private String target;

	public FilterAction(String type, String selector, String target)
	{
		this.type = type;
		this.selector = selector;
		this.target = target;
	}

	/**
     * @return the message
	 * @property 
	 */
	public String get_Selector()
	{
		return selector;
	}

	/**
	 * @return the target
	 * @property 
	 */
	public String get_Target()
	{
		return target;
	}

	/**
     * @return the type
	 * @property 
     */
	public String get_Type()
	{
		return type;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);

		if (type.equalsIgnoreCase("ContinueAction"))
			visitor.VisitContinueAction(this);
		else if (type.equalsIgnoreCase("SubstitutionAction"))
			visitor.VisitSubstitutionAction(this);
		else if (type.equalsIgnoreCase("ErrorAction"))
			visitor.VisitErrorAction(this);
		else if (type.equalsIgnoreCase("DispatchAction"))
			visitor.VisitDispatchAction(this);
		else if (type.equalsIgnoreCase("BeforeAction"))
			visitor.VisitBeforeAction(this);
		else if (type.equalsIgnoreCase("AfterAction"))
			visitor.VisitAfterAction(this);
		else if (type.equalsIgnoreCase("SkipAction"))
			visitor.VisitSkipAction(this);
		else
			visitor.VisitFilterAction(this); 
	}
    
    public String toString(){
        String s = super.toString();
        s += "FILTERACTION: " + type + ", " + selector + ", " + target + "\n";
        return s;
    }
}
