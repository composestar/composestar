package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Annotation implements Serializable
{
	private static final long serialVersionUID = 2225517155784402517L;

	private String typeName;

	private Type type;

	private String value;

	public ProgramElement target;

	public boolean superImposed; // Set to true if this annotation was

	// superimposed by Compose*

	// This info can be used by the weaver (?) to write the new annos
	// back into the assembly.

	public Annotation()
	{
		superImposed = false;
	}

	public Annotation(boolean inIsSuperImposed)
	{
		superImposed = inIsSuperImposed;
	}

	public void register(Type annotationType, ProgramElement inTarget)
	{
		type = annotationType;
		target = inTarget;
		type.addAnnotationInstance(this);
		inTarget.addAnnotation(this);
	}

	public void deregister()
	{
		type.removeAnnotationInstance(this);
		target.removeAnnotation(this);
	}

	public void setTypeName(String name)
	{
		typeName = name;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setType(Type t)
	{
		type = t;
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
		return this.superImposed;
	}

	public void setIsSuperImposed(boolean isSI)
	{
		superImposed = isSI;
	}

	public ProgramElement getTarget()
	{
		return target;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// m_type = (Type)in.readObject();
		typeName = in.readUTF();
		if (typeName.length() == 0)
		{
			typeName = null;
		}
		target = (ProgramElement) in.readObject();
		value = in.readUTF();
		if (value.length() == 0)
		{
			value = null;
		}
		superImposed = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		// out.writeObject(m_type);
		out.writeUTF(typeName == null ? "" : typeName);
		out.writeObject(target);
		out.writeUTF(value == null ? "" : value);
		out.writeBoolean(superImposed);
	}
}
