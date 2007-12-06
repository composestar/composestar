package Composestar.Core.INCRE;

import java.lang.reflect.Field;

public class FieldNode extends Node
{
	private static final long serialVersionUID = 3220062124848983208L;

	public FieldNode(String ref)
	{
		super(ref);
	}

	/**
	 * @return the referenced field of the input object (obj)
	 * @param Object obj
	 */
	@Override
	public Object visit(Object obj)
	{
		try
		{
			// try to get public field
			Field publicfield = obj.getClass().getField(reference);
			return publicfield.get(obj);
		}
		catch (Exception excep)
		{
			// excep.printStackTrace();
		}

		try
		{
			// try to get private field
			Field privatefield = obj.getClass().getDeclaredField(reference);
			privatefield.setAccessible(true);
			return privatefield.get(obj);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * @return an unique id for a referenced field
	 */
	@Override
	public String getUniqueID(Object obj)
	{
		return obj.hashCode() + "." + reference;
	}
}
