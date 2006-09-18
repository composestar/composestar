package Composestar.DotNET.TYM.SignatureTransformer;

/**
 * Exception used by the Assembler classes, thrown if there is an error while 
 * assembling or dissassembling.
 */
public class AssemblerException extends Exception
{    
	private static final long serialVersionUID = 363940188119203677L;

	/**
	 * @roseuid 406AB0000121
	 */
	public AssemblerException()
	{
	}

	/**
	 * @param message
	 * @roseuid 406AB000010C
	 */
	public AssemblerException(String message)
	{
		super(message);     
	}
}
