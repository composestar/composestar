package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class MethodInfo extends ProgramElement implements SerializableRepositoryEntity{
	
	public String Name;
	public String ReturnTypeString;
    
	public Type ReturnType;
	public ArrayList Parameters;
	public Type Parent;

	public MethodInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
		Parameters = new ArrayList();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF020E
	 */
	public String name() 
	{
		return Name;     
    }
	
	/**
	 * @param name
	 * @roseuid 402A028601CF
	 */
	public void setName(String name) 
	{
		Name = name;     
	}
	
	/**
	 * @return java.util.List
	 * @roseuid 401B84CF0211
	 */
	public List getParameters() 
	{
		return Parameters;     
	}
	
	/**
	 * @param types Check if the methods has these types
	 * @return true if there is a signature match. False otherwise
	 * @roseuid 402C9CE401C5
	 */
	public boolean hasParameters(String[] types) 
	{
		return true;
	}
	
	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 4050689303BD
	 */
	public Type parent() 
	{
		return Parent;     
	}
	
	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 401B84CF020F
	 */
	public Type returnType() 
	{
        if( ReturnType == null ) {
            TypeMap map = TypeMap.instance();
			ReturnType = map.getType( ReturnTypeString );
        }
        return ReturnType;
	} 
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
    {
      return Name;
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
    {
      return "Method";
    }
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasAttribute(java.lang.String)
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
		ReturnTypeString = in.readUTF();
		Parameters = (ArrayList)in.readObject();   
		Parent = (Type)in.readObject();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeUTF(ReturnTypeString);
		out.writeObject(Parameters);
		out.writeObject(Parent);
	}
}
