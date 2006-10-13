package Composestar.RuntimeCore.CODER.BreakPoint.Parsers;

/**
 * Summary description for BreakPointParseException.
 */
public class BreakPointParseException extends Exception
{
	public BreakPointParseException()
	{
		super();
	}

	public BreakPointParseException(String message)
	{
		super(message);
	}

	public BreakPointParseException(Exception message)
	{
		this(message.getMessage());
	}
}