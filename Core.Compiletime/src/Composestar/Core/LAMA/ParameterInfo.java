package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ParameterInfo extends ProgramElement
{
	public String ParameterTypeString;

	private Type ParameterType;

	public String Name;

	public MethodInfo Parent;

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
	 * @param name
	 * @roseuid 402A072800EE
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * @return Composestar.Core.LAMA.Type
	 * @roseuid 401B84CF021D
	 */
	public Type parameterType()
	{
		if (ParameterType == null)
		{
			TypeMap map = TypeMap.instance();
			ParameterType = map.getType(ParameterTypeString);
		}
		return ParameterType;
	}

	/**
	 * @return the parameterTypeString
	 */
	public String getParameterTypeString()
	{
		return ParameterTypeString;
	}

	/**
	 * @param paramType
	 * @roseuid 402A0736033D
	 */
	public void setParameterType(String paramType)
	{
		ParameterTypeString = paramType;
	}

	/**
	 * @return Returns the parent.
	 */
	public MethodInfo getParent()
	{
		return Parent;
	}

	/**
	 * @param parent The parent to set.
	 */
	public void setParent(MethodInfo parent)
	{
		Parent = parent;
	}

	public String toString()
	{
		return ParameterTypeString + " " + Name;
	}

	// Stuff for LOLA

	public String getUnitName()
	{
		return name();
	}

	public String getUnitType()
	{
		return "Parameter";
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		Name = in.readUTF();
		ParameterTypeString = in.readUTF();
		Parent = (MethodInfo) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeUTF(ParameterTypeString);
		out.writeObject(Parent);
	}
}