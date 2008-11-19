package Composestar.Java.FLIRT.Actions;

/**
 *Default exception thrown by the error filter
 * 
 * @author Michiel Hendriks
 */
public final class ErrorFilterException extends RuntimeException
{
	private static final long serialVersionUID = -8676277511091283118L;

	/**
	 * 
	 */
	public ErrorFilterException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ErrorFilterException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ErrorFilterException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ErrorFilterException(Throwable cause)
	{
		super(cause);
	}
}
