package Composestar.Java.TYM.TypeHarvester;

/**
 * Exception used by the JarLoader, thrown if there is an error while loading
 * classes from a jar file.
 */
public class JarLoaderException extends Exception
{
	private static final long serialVersionUID = 363940188119203677L;

	/**
	 * Default constructor.
	 */
	public JarLoaderException()
	{

	}

	/**
	 * Constructor.
	 * 
	 * @param message - an error message
	 */
	public JarLoaderException(String message)
	{
		super(message);
	}
}
