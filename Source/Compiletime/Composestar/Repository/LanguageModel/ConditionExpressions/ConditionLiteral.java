package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;

/**
 * Summary description for ConditionLiteral.
 */
public class ConditionLiteral extends ConditionExpression implements IVisitable 
{
	public ConditionLiteral()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}


	private String _name;

	/** @property */
	public String get_Name()
	{
		return _name;
	}

	/** @property */
	public void set_Name(String value)
	{
		_name = value;
	}

	public void Accept(IVisitor visitor)
	{
		visitor.VisitConditionLiteral(this);
	}
    
    public String toString(){
        return _name;
    }
}
