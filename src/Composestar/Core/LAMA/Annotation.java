package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Annotation implements Serializable
{
	private static final long serialVersionUID = 2225517155784402517L;

	public Type m_type;
	public String m_value;
	public ProgramElement m_target;
    
	public boolean m_isSuperImposed;	// Set to true if this annotation was superimposed by Compose*
    									// This info can be used by the weaver (?) to write the new annos
										// back into the assembly.
	
    public Annotation()
    {
    	m_isSuperImposed = false;
    }
    
    public Annotation(boolean isSuperImposed)
    {
    	m_isSuperImposed = isSuperImposed;
    }
    
	public void register(Type annotationType, ProgramElement target)
    {
    	m_type = annotationType;
		m_target = target;
    	m_type.addAnnotationInstance(this);
		target.addAnnotation(this);
    }
	
	public void deregister()
    {
    	m_type.removeAnnotationInstance(this);
    	m_target.removeAnnotation(this);
    }
    
	public Type getType()
	{
		return m_type;
	}
	
	public void setValue(String theValue)
	{
		m_value = theValue;
	}
	
	public String getValue()
	{
		return m_value;
	}
	
	public boolean isSuperImposed()
	{
		return this.m_isSuperImposed;
	}
	
	public void setIsSuperImposed(boolean isSI)
	{
		m_isSuperImposed = isSI;
	}

	public ProgramElement getTarget()
	{
		return m_target;
	}
	
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		m_type = (Type)in.readObject();
		m_target = (ProgramElement)in.readObject();
		m_value = in.readUTF();
		if (m_value.length() == 0)
			m_value = null;
		m_isSuperImposed = in.readBoolean();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(m_type);
		out.writeObject(m_target);
		out.writeUTF(m_value == null ? "" : m_value);
		out.writeBoolean(m_isSuperImposed);
	}
}
