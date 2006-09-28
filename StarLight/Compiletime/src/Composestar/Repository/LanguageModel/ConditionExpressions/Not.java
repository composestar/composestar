package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;

/**
 * Not condition.
 */
public class Not extends ConditionExpression implements IVisitable 
{
	public Not()
	{
	}

	private ConditionExpression _operand;

	/** @property */
	public ConditionExpression get_Operand()
	{
		return _operand;
	}

	/** @property */
	public void set_Operand(ConditionExpression value)
	{
		_operand = value;
	}

	public void Accept(IVisitor visitor)
	{
		if (_operand != null)
		{
			((IVisitable)_operand).Accept(visitor);
		}
		visitor.VisitNot(this);
	}
    
    public String toString(){
        return "!" + _operand.toString();
    }
}
