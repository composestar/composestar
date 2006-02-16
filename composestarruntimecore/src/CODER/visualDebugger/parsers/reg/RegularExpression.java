package Composestar.RuntimeCore.CODER.VisualDebugger.Parsers.Reg;

/**
 * Summary description for RegularExpression.
 */
public abstract class RegularExpression
{
	protected RegularExpression next = null;
	public void setNext(RegularExpression next)
	{
		this.next = next;
	}

	public boolean hasNext()
	{
		return next != null;
	}
}
