package Composestar.Core.INCRE;

import java.lang.reflect.Field;

public class FieldNode extends Node
{
	public FieldNode(String objectref)
	{
		super(objectref);
	}

	public Object visit(Object obj)
	{
		try 
		{
			// try to get public field
			Field publicfield = obj.getClass().getField(objectref);
			Object publicfieldvalue = publicfield.get(obj);
			return publicfieldvalue;
		}
		catch(Exception excep){}
	
		try {
			// try to get private field
			Field privatefield = obj.getClass().getDeclaredField(objectref);
			privatefield.setAccessible(true);
			Object privatefieldvalue = privatefield.get(obj);
			return privatefieldvalue;
		}
		catch(Exception ex){return null;}
	}
	
	public String getUniqueID(Object obj){
		return obj.hashCode()+"."+this.objectref;
	}
}
