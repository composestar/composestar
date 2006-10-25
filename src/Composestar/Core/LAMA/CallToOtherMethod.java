/*
 * Created on 26-apr-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * @author Michiel van Oudheusden
 *
 */
public class CallToOtherMethod implements SerializableRepositoryEntity {

	/**
	 * The fully qualified name
	 */
	public String OperationName;
	
	/**
	 * The name of the method
	 */
	public String methodName;
    public MethodInfo calledMethod;
	public String ClassName;       
	public Type Parent;
	
	public CallToOtherMethod()
	{
		
	}
	
	public Type parent() 
	{
		return Parent;
	}
	
	
	/**
     * @return the calledMethod
     */
    public MethodInfo getCalledMethod(){
        return calledMethod;
    }

    /**
     * @param calledMethod the calledMethod to set
     */
    public void setCalledMethod(MethodInfo calledMethod){
        this.calledMethod = calledMethod;
    }

    /**
     * @return the className
     */
    public String getClassName(){
        return ClassName;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className){
        ClassName = className;
    }

    /**
     * @return the operationName
     */
    public String getOperationName(){
        return OperationName;
    }

    /**
     * @param operationName the operationName to set
     */
    public void setOperationName(String operationName){
        OperationName = operationName;
    }

    /**
     * @return the parent
     */
    public Type getParent(){
        return Parent;
    }
    
    public void setParent(Type parent) 
    {
        Parent = parent;
    }
    
    

    /**
	 * @return the methodName
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	/**
	 * Custom deserialization of this object
     * @param in
     */
	private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
	{
		OperationName = in.readUTF();
		Parent = (Type)in.readObject();
		ClassName = in.readUTF();
	}
	
	/**
	 * Custom serialization of this object
     * @param out
     */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(OperationName);
		out.writeObject(Parent);
		out.writeUTF(ClassName);
	}
	
}
