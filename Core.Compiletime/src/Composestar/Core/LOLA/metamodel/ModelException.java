/*
 * Created on Oct 25, 2004
 *
 * @author wilke
 *
 * Generic language model exception; try to use one of the more specific model exceptions if possible. 
 */

package Composestar.Core.LOLA.metamodel;

public class ModelException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8333402706465401577L;

	public ModelException(String msg)
	{
		super(msg);
	}
}
