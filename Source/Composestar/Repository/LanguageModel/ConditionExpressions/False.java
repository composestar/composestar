package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;

/**
 * Summary description for False.
 */
public class False extends ConditionExpression implements IVisitable 
{
	public False()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}

	public void Accept(IVisitor visitor)
	{
		visitor.VisitFalse(this);
	}
    
    public String toString(){
        return "false";
    }
}
