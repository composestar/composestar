/*
 * Created on Oct 25, 2004
 *
 * @author wilke
 *
 * Used when the value of language units clashes with the language model;
 * e.g. the model specifies that a unit has a unique name,
 *      but the program registers two units with this name;
 * or:  a supposedly unique relation has multiple results (or v.v.)
 */

package Composestar.Core.LOLA.metamodel;

public class ModelClashException extends ModelException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 393413282329393644L;

	String model = "";

	public ModelClashException(String msg)
	{
		super(msg);
	}

	public ModelClashException(String model, String msg)
	{
		this(msg);
		this.model = model;
	}

	public String getModel()
	{
		return model;
	}
}
