package Composestar.Repository.LanguageModel.ConditionExpressions;

/**
 * Summary description for Not.
 */
public class Not extends ConditionExpression
{
	public Not()
	{
		//
		// TODO: Add Constructor Logic here
		//
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
}
