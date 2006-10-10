package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class FilterAction extends InlineInstruction implements IVisitable
{
	private String type;
	private String fullName;
	private String selector;
	private String target;
	private String substitutionSelector;
	private String substitutionTarget;

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
    
	public FilterAction(String type, String fullName, String selector, String target, 
		String substitutionSelector, String substitutionTarget)
	{
		this.type = type;
		this.fullName = fullName;
		this.selector = selector;
		this.target = target;
		this.substitutionSelector = substitutionSelector;
		this.substitutionTarget = substitutionTarget;
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
     * @return the substitutionselector
	 * @property 
	 */
	public String get_SubstitutionSelector()
	{
		return substitutionSelector;
	}

	/**
	 * @return the substitutionTarget
	 * @property 
	 */
	public String get_SubstitutionTarget()
	{
		return substitutionTarget;
	}

	/**
     * @return the type
	 * @property 
     */
	public String get_Type()
	{
		return type;
	}
	
	

	/**
	 * @return the fullName
	 * @property
	 */
	public String get_FullName()
	{
		return fullName;
	}

	
	
	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);

		visitor.VisitFilterAction(this);
	}
    
    public String toString(){
        String s = super.toString();
        s += "FILTERACTION: " + type + ", " + target + '.' + selector + ", " + substitutionTarget + 
        '.' + substitutionSelector + "\n";
        return s;
    }
}
