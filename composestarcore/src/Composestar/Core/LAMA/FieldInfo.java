package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class FieldInfo extends ProgramElement implements SerializableRepositoryEntity{

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
	      FieldType = map.getType( FieldTypeString );
	    }
	    return FieldType;
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
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
	    return name();
	}
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
	    return "Field";
	}
	
	/* (non-Javadoc)
	 * @see Composestar.core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
	    return getUnitAttributes().contains(attribute);
	}
	
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		Name = in.readUTF();
		Parent = (Type)in.readObject();
		FieldTypeString = in.readUTF();
	}
	
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeObject(Parent);
		out.writeUTF(FieldTypeString);
	}
}
