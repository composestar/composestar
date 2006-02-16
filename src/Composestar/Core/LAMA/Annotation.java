package Composestar.Core.LAMA;

import java.io.Serializable;

public abstract class Annotation implements Serializable{

	public Type type;
	public String value;
	public ProgramElement target;
    
	public boolean isSuperImposed; // Set to true if this annotation was superimposed by Compose*
    								// This info can be used by the weaver (?) to write the new annots
									// back into the assembly.
	
    public Annotation()
    {
    	this.isSuperImposed = false;
    }
    
    public Annotation(boolean isSuperImposed)
    {
    	this.isSuperImposed = isSuperImposed;
    }
    
	public void register(Type annotationType, ProgramElement target)
    {
    	this.type = annotationType;
		this.target = target;
    	type.addAnnotationInstance(this);
		target.addAnnotation(this);
    }
	
	public void deregister()
    {
    	type.removeAnnotationInstance(this);
    	target.removeAnnotation(this);
    }
    
	public Type getType()
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

	public ProgramElement getTarget()
	{
		return target;
	}
}
