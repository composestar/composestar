package Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Reg;

/**
 * Summary description for RegularExpression.
 */
public abstract class RegularExpressionOperator extends RegularExpression
{
	protected RegularExpression target = null;

	public void setNext(RegularExpression next)
	{
		if(this.target != null)
		{
			this.next = next;
		}
		else
		{
			this.target = next;
		}
	}

	public boolean hasNext()
	{
		return target != null;
	}
}
