package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;

/**
 * Summary description for Or.
 */
public class Or extends ConditionExpression implements IVisitable 
{
	public Or()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}

	private ConditionExpression _left;

	/** @property */
	public ConditionExpression get_Left()
	{
		return _left;
	}

	/** @property */
	public void set_Left(ConditionExpression value)
	{
		_left = value;
	}



	private ConditionExpression _right;

	/** @property */
	public ConditionExpression get_Right()
	{
		return _right;
	}

	/** @property */
	public void set_Right(ConditionExpression value)
	{
		_right = value;
	}

	public void Accept(IVisitor visitor)
	{		
		((IVisitable)_left).Accept(visitor);
		((IVisitable)_right).Accept(visitor);
		visitor.VisitOr(this);
	}
}
