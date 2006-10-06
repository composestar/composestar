package Composestar.Repository.LanguageModel.ConditionExpressions;

import Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.*;     

/**
 * Summary description for And.
 */
public class And extends ConditionExpression implements IVisitable 
{
	
	public And()
	{
		//
		// TODO: Add Constructor Logic here
		//
	}

	private int _branchId;

	/** @property */
	public int get_BranchId()
	{
		return _branchId;
	}

	/** @property */
	public void set_BranchId(int value)
	{
		_branchId = value;
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
		visitor.VisitAndLeft(this);
		((IVisitable)_right).Accept(visitor);
		visitor.VisitAndRight(this);
	}
    
    
    public String toString(){
        return "(" + _left.toString() + " && " + _right.toString() + ")";
    }
}
