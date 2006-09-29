package Composestar.DotNET.ASTRA;

/**
 * Exception used by the Assembler classes, thrown if there is an error while 
 * assembling or dissassembling.
 */
public class AssemblerException extends Exception
{    
	private static final long serialVersionUID = 363940188119203677L;

	public AssemblerException()
	{
	}

	public AssemblerException(String message)
	{
		super(message);     
	}
}
