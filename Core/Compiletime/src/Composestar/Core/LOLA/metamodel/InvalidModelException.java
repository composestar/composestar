/*
 * Created on Oct 25, 2004
 *
 * @author havingaw
 *
 * Used when inconsistencies are detected within a language model itself.
 */

package Composestar.Core.LOLA.metamodel;

public class InvalidModelException extends ModelException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7967746102961876076L;

	String model = "";

	public InvalidModelException(String msg)
	{
		super(msg);
	}

	public InvalidModelException(String model, String msg)
	{
		this(msg);
		this.model = model;
	}

	public String getModel()
	{
		return this.model;
	}
}
