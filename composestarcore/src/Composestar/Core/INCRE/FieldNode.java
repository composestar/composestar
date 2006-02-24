package Composestar.Core.INCRE;

import java.lang.reflect.Field;

public class FieldNode extends Node
{
	public FieldNode(String ref)
	{
		super(ref);
	}
 
	/**
	 * @return the referenced field of the input object (obj)
	 * @param Object obj
	 */
	public Object visit(Object obj)
	{
		try 
		{
			// try to get public field
			Field publicfield = obj.getClass().getField(reference);
			Object publicfieldvalue = publicfield.get(obj);
			return publicfieldvalue;
		}
		catch(Exception excep){}
	
		try { 
			// try to get private field
			Field privatefield = obj.getClass().getDeclaredField(reference);
			privatefield.setAccessible(true);
			Object privatefieldvalue = privatefield.get(obj);
			return privatefieldvalue;
		}
		catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * @return an unique id for a referenced field
	 */
	public String getUniqueID(Object obj){
		return obj.hashCode()+"."+this.reference;
	}
}
