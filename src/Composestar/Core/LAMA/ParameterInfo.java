package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ParameterInfo extends ProgramElement
{

	public String ParameterTypeString;
	private Type ParameterType;
	public String Name;
	
	public ParameterInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF021C
	 */
	public String name() 
	{
		return Name;     
	}
    
	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 401B84CF021D
	 */
	public Type parameterType() 
	{
		if( ParameterType == null ) 
		{
			TypeMap map = TypeMap.instance();
			ParameterType = map.getType( ParameterTypeString );
		}
		return ParameterType;     
	}
	
	/**
	 * @param paramType
	 * @roseuid 402A0736033D
	 */
	public void setParameterType(String paramType) 
	{
		ParameterTypeString = paramType;     
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
		return "Parameter";
	}
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}
	
	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		Name = in.readUTF();
		ParameterTypeString = in.readUTF();
	}
	 
	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeUTF(ParameterTypeString);
	}
}
