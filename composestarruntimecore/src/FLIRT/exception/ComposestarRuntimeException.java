package Composestar.RuntimeCore.FLIRT.Exception;

/**
 *  General Exception for the composeStar runtime.
 *
 *@author    Carlos Noguera
 */
public class ComposestarRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -638265734486320669L;


	/**
	 *  Default constructor
	 */
	public ComposestarRuntimeException() {
	}


	/**
	 *  Constructs a new ComposeStarException carring a String as a message
	 *
	 *@param  message  General message to send
	 */
	public ComposestarRuntimeException(String message) {
		super(message);
	}
}
