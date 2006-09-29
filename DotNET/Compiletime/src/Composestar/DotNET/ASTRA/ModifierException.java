package Composestar.DotNET.ASTRA;

/**
 * Modifier exceptions are thrown if there are serious errors during the 
 * modification of an assembly.
 * 
 * TODO: why not extend ModuleException?
 */
public class ModifierException extends Exception
{
	private static final long serialVersionUID = 8558389968530240489L;

	public ModifierException()
	{
	}

	public ModifierException(String message)
	{
		super(message);
	}
}
