package Composestar.Repository.LanguageModel.ConditionExpressions;

/**
 * Summary description for Or.
 */
public class Or extends ConditionExpression
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
}
