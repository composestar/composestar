package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class FilterAction extends InlineInstruction implements IVisitable
{
	private String type;
	private String selector;
	private String target;

    public final static String DISPATCH_ACTION = "DispatchAction";
    public final static String BEFORE_ACTION = "BeforeAction";
    public final static String AFTER_ACTION = "AfterAction";
    public final static String SKIP_ACTION = "SkipAction";
    public final static String ERROR_ACTION = "ErrorAction";
    public final static String SUBSTITUTION_ACTION = "SubstitutionAction";
    public final static String CUSTOM_ACTION = "CustomAction";
    public final static String CONTINUE_ACTION = "ContinueAction";

	public final static String INNER_TARGET = "inner";
	public final static String SELF_TARGET = "self";
    
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

		if (type.equalsIgnoreCase(CONTINUE_ACTION))
			visitor.VisitContinueAction(this);
		else if (type.equalsIgnoreCase(SUBSTITUTION_ACTION))
			visitor.VisitSubstitutionAction(this);
		else if (type.equalsIgnoreCase(ERROR_ACTION))
			visitor.VisitErrorAction(this);
		else if (type.equalsIgnoreCase(DISPATCH_ACTION))
			visitor.VisitDispatchAction(this);
		else if (type.equalsIgnoreCase(BEFORE_ACTION))
			visitor.VisitBeforeAction(this);
		else if (type.equalsIgnoreCase(AFTER_ACTION))
			visitor.VisitAfterAction(this);
		else if (type.equalsIgnoreCase(SKIP_ACTION))
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
