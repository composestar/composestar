package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class FieldInfo extends ProgramElement
{

	public String Name;

	public Type Parent;

	public String FieldTypeString;

	private Type FieldType;

	public FieldInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	public String name()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public Type fieldType()
	{
		if (this.FieldType == null)
		{
			TypeMap map = TypeMap.instance();
			FieldType = map.getType(FieldTypeString);
		}
		return FieldType;
	}
	
	public String getFieldTypeString()
	{
		return FieldTypeString;
	}

	public void setFieldType(String fieldtype)
	{
		this.FieldTypeString = fieldtype;
	}

	public Type parent()
	{
		return Parent;
	}

	public void setParent(Type parent)
	{
		Parent = parent;
	}

	/** Stuff for LOLA * */

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
		return name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
		return "Field";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}
	
	public abstract boolean isPublic();
	
	public abstract boolean isPrivate();
	
	public abstract boolean isProtected();

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		Name = in.readUTF();
		Parent = (Type) in.readObject();
		FieldTypeString = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeObject(Parent);
		out.writeUTF(FieldTypeString);
	}
}
