package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * General Exception class for Composestar Runtime.
 * 
 * J# does not support nested exceptions on type throwable.
 * 
 * To keep Composestar.RuntimeCore buildable by both J# and Java,
 * we introduce nested exceptions as implemented by Java 1.4.
 * 
 * This means DotNET applications should use getCause() and not
 * property InnerException.
 */
public class ComposestarRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -638265734486320669L;

	/**
	 * The cause of this exception, including null for an unknown or non-chained
	 * cause. This may only be set once; so the field is set to
	 * this until initialized.
	 *
	 * @serial the cause, or null if unknown, or this if not yet set
	 */
	private Throwable cause = this;

	public ComposestarRuntimeException() 
	{
		this((String)null);
	}

	public ComposestarRuntimeException(String message)
	{
		super(message);
	}

	public ComposestarRuntimeException(String message, Throwable cause)
	{
		this(message);
		this.cause = cause;
	}

	public ComposestarRuntimeException(Throwable cause)
	{
		this(cause == null ? null : cause.toString(), cause);
	}

	/**
	 * Returns the cause of this exception, or null if the cause is not known
	 * or non-existant. This cause is initialized by the new constructors,
	 * or by calling initCause.
	 *
	 * @return the cause of this Throwable
	 */
	public Throwable getCause()
	{
		return cause == this ? null : cause;
	}

	/**
	 * Initialize the cause of this Throwable.  This may only be called once
	 * during the object lifetime, including implicitly by chaining
	 * constructors.
	 *
	 * @param cause the cause of this Throwable, may be null
	 * @return this
	 * @throws IllegalArgumentException if cause is this (a Throwable can't be
	 *         its own cause!)
	 * @throws IllegalStateException if the cause has already been set
	 */
	public Throwable initCause(Throwable cause)
	{
		if (cause == this)
			throw new IllegalArgumentException();
		if (this.cause != this)
			throw new IllegalStateException();
		this.cause = cause;
		return this;
	}
}
