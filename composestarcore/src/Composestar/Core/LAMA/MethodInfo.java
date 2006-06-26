package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public abstract class MethodInfo extends ProgramElement implements SerializableRepositoryEntity{
	
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
       Parameters.add( param );
       //param.setParent(this);
    }
    
	public MethodInfo getClone(String n, Type actualParent) throws Exception{
		throw new Exception("MethodInfo.getClone(String n) should only be called on subtypes");
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
	 * Get a list with information about the calls to other methods from within this method.
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
	 * Indicates if this instance had information about the calls to other methods.
	 * @return
	 */
	public boolean hasCallsToOtherMethodsInformation()
	{
		return CallsToOtherMethods.size() > 0;	
	}
		
	/**
	 * Get the resource usage information.
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
	 * @return
	 */
	public boolean hasResourceUsage()
	{
		return ResourceUsage.size() > 0;	
	}
	
	/**
	 * Get a list of strings describing the behaviour of the ReifiedMessage
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
	 * Indicates if the message has a specified ReifiedMessage Behavior collection.
	 * @return boolean
	 */
	public boolean hasReifiedMessageBehavior()
	{
		return ReifiedMessageBehavior.size() > 0;
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
     * @param parent
     */
    public void setParent(Type parent) 
    {
     	Parent = parent;     
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
	
	/**
     * @param type
     */
    public void setReturnType(String type) 
    {
       ReturnTypeString = type;     
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
		CallsToOtherMethods = (HashSet)in.readObject() ;
		ReifiedMessageBehavior = (HashSet)in.readObject();
		ResourceUsage = (HashSet)in.readObject();
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
		out.writeObject(CallsToOtherMethods);
		out.writeObject(ReifiedMessageBehavior);
		out.writeObject(ResourceUsage);
	}
}
