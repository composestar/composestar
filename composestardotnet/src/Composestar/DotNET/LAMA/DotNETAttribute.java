package Composestar.DotNET.LAMA;

import Composestar.Core.LAMA.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DotNETAttribute extends Annotation implements Serializable
{
    
	private static final long serialVersionUID = -4093341461570994120L;
	
	//moved to supertype Annotation
	/*private DotNETType type;
    
    private DotNETUnit target;*/

    //moved to supertype Annotation
	/*private boolean isSuperImposed; // Set to true if this annotation was superimposed by Compose*
                                    // This info can be used by the weaver (?) to write the new annots
    								// back into the assembly.*/
    
    //moved to supertype Annotation
    /*public DotNETAttribute(boolean isSuperImposed)
    {
    	this.isSuperImposed = isSuperImposed;
    }*/
    
    //moved to supertype Annotation
    /*public void register(DotNETType annotationType, DotNETUnit target)
    {
    	this.type = annotationType;
		this.target = target;
    	type.addAttributeInstance(this);
		target.addAttribute(this);
    }*/
    
    //moved to supertype Annotation
    /*public void deregister()
    {
    	type.removeAttributeInstance(this);
    	target.removeAttribute(this);
    }
    
	public DotNETType getType()
	{
		return type;
	}
	
	public void setValue(String theValue)
	{
		value = theValue;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public boolean isSuperImposed()
	{
		return this.isSuperImposed;
	}
	
	public void setIsSuperImposed(boolean isSI)
	{
		this.isSuperImposed = isSI;
	}

	public DotNETUnit getTarget()
	{
		return target;
	}*/

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		super.value = in.readUTF();
		if(value.equals(""))
			value = null;
		isSuperImposed = in.readBoolean();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		if(value!=null)
			out.writeUTF(value);
		else
			out.writeUTF("");
		out.writeBoolean(isSuperImposed);
	}
}
