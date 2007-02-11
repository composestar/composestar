package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Composestar.Utils.StringUtils;

public abstract class MethodInfo extends ProgramElement
{

	public String Name;

	public String ReturnTypeString;

	public Type ReturnType;

	public ArrayList Parameters;

	public Type Parent;

	private HashSet CallsToOtherMethods;

	private HashSet ReifiedMessageBehavior;

	private HashSet ResourceUsage;

	public MethodInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
		Parameters = new ArrayList();
		CallsToOtherMethods = new HashSet();
		ReifiedMessageBehavior = new HashSet();
		ResourceUsage = new HashSet();
	}

	/**
	 * @param param
	 */
	public void addParameter(ParameterInfo param)
	{
		Parameters.add(param);
		// param.setParent(this);
	}

	/**
	 * This method should make a clone of the MethodInfo with the name and
	 * parentType changed to the given name and actualParent. The parameters and
	 * return type should stay the same.
	 * 
	 * @param name
	 * @param actualParent
	 */
	public abstract MethodInfo getClone(String name, Type actualParent);

	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF020E
	 * @deprecated Use {@link #getName()} instead
	 */
	public String name()
	{
		return getName();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401B84CF020E
	 */
	public String getName()
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
	 * Get a list with information about the calls to other methods from within
	 * this method.
	 * 
	 * @return java.util.List
	 */
	public HashSet getCallsToOtherMethods()
	{
		return CallsToOtherMethods;
	}

	public void setCallsToOtherMethods(HashSet value)
	{
		CallsToOtherMethods = value;
	}

	/**
	 * Indicates if this instance had information about the calls to other
	 * methods.
	 * 
	 * @return
	 */
	public boolean hasCallsToOtherMethodsInformation()
	{
		return !CallsToOtherMethods.isEmpty();
	}

	/**
	 * Get the resource usage information.
	 * 
	 * @return
	 */
	public HashSet getResourceUsage()
	{
		return ResourceUsage;
	}

	public void setResourceUsage(HashSet value)
	{
		ResourceUsage = value;
	}

	/**
	 * Indicates if this instance had information about the resource usage.
	 * 
	 * @return
	 */
	public boolean hasResourceUsage()
	{
		return !ResourceUsage.isEmpty();
	}

	/**
	 * Get a list of strings describing the behaviour of the ReifiedMessage
	 * 
	 * @return java.util.List
	 */
	public HashSet getReifiedMessageBehavior()
	{
		return ReifiedMessageBehavior;
	}

	public void setReifiedMessageBehavior(HashSet value)
	{
		ReifiedMessageBehavior = value;
	}

	/**
	 * Indicates if the message has a specified ReifiedMessage Behavior
	 * collection.
	 * 
	 * @return boolean
	 */
	public boolean hasReifiedMessageBehavior()
	{
		return !ReifiedMessageBehavior.isEmpty();
	}

	/**
	 * @param types Check if the methods has these types
	 * @return true if there is a signature match. False otherwise
	 * @roseuid 402C9CE401C5
	 */
	public boolean hasParameters(String[] types)
	{
		if (Parameters.size() != types.length)
		{
			return false;
		}

		for (int i = 0; i < types.length; i++)
		{
			ParameterInfo parameter = (ParameterInfo) Parameters.get(i);
			if (!parameter.getParameterTypeString().equals(types[i]))
			{
				return false;
			}
		}

		return true;
	}

	public Type parent()
	{
		return Parent;
	}

	public void setParent(Type parent)
	{
		Parent = parent;
	}

	public String returnTypeName()
	{
		return ReturnTypeString;
	}

	public Type returnType()
	{
		if (ReturnType == null)
		{
			TypeMap map = TypeMap.instance();
			ReturnType = map.getType(ReturnTypeString);

			if (ReturnType == null)
			{
				return null;
				// throw new RuntimeException("Unable to find type specification for
				// '" + ReturnTypeString + "'");
			}
		}

		return ReturnType;
	}

	public void setReturnType(String type)
	{
		ReturnTypeString = type;
	}

	public String getReturnTypeString()
	{
		return ReturnTypeString;
	}

	public String toString()
	{
		String atts = StringUtils.join(getUnitAttributes(), " ");
		String params = StringUtils.join(Parameters, ", ");
		return atts + " " + ReturnTypeString + " " + Name + "(" + params + ")";
	}

	// Stuff for LOLA

	public String getUnitName()
	{
		return getName();
	}

	public String getUnitType()
	{
		return "Method";
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}

	// custom (de)serialization

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		Name = in.readUTF();
		ReturnTypeString = in.readUTF();
		Parameters = (ArrayList) in.readObject();
		Parent = (Type) in.readObject();
		CallsToOtherMethods = (HashSet) in.readObject();
		ReifiedMessageBehavior = (HashSet) in.readObject();
		ResourceUsage = (HashSet) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(Name);
		out.writeUTF(ReturnTypeString);
		out.writeObject(Parameters);
		out.writeObject(Parent);
		out.writeObject(CallsToOtherMethods);
		out.writeObject(ReifiedMessageBehavior);
		out.writeObject(ResourceUsage);
	}

	public boolean checkEquals(MethodInfo method)
	{
		if (!method.Name.equals(this.Name))
		{
			return false;
		}

		if (!method.ReturnTypeString.equals(this.ReturnTypeString))
		{
			return false;
		}

		if (this.Parameters.size() != method.Parameters.size())
		{
			return false;
		}

		for (int i = 0; i < this.Parameters.size(); i++)
		{
			ParameterInfo thisPar = (ParameterInfo) this.Parameters.get(i);
			ParameterInfo objPar = (ParameterInfo) method.Parameters.get(i);
			if (!thisPar.ParameterTypeString.equals(objPar.ParameterTypeString))
			{
				return false;
			}
		}

		return true;
	}
}
