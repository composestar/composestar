package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;

/**
 * Summary description for True.
 */
public class True extends ConditionExpression implements IVisitable 
{
	public True()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}

	public void Accept(IVisitor visitor)
	{
		visitor.VisitTrue(this);  
	}
    
    public String toString(){
        return "true";
    }

}
